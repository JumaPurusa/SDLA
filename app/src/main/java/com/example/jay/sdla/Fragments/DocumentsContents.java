package com.example.jay.sdla.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jay.sdla.Adapters.DocumentAdapter;
import com.example.jay.sdla.Dialogs.ExceptionDialog;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemClickListener;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemLongClickListener;
import com.example.jay.sdla.Models.DocumentFile;
import com.example.jay.sdla.R;
import com.example.jay.sdla.Utils.FileEncryptorAndDecryptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

public class DocumentsContents extends Fragment{

    public static final int MY_PERMISSION_REQUEST = 1;
    public static final int DIALOG_KEY = 0;

    private RecyclerView documentRecyclerView;
    private DocumentAdapter adapter;
    private List<DocumentFile> documents;

    private List<File> fileList1;

    FileEncryptorAndDecryptor encryptorAndDecryptor;

    //boolean boolean_permission;

    File dir;

    public static ActionMode mActionMode;

    ProgressDialog mProgressDialog;
    ProgressBar progressBar;

    SharedPreferences keySettings;

    public DocumentsContents() {
        // Required empty public constructor

        encryptorAndDecryptor = new FileEncryptorAndDecryptor();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_documents_contents, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }

    private void populateListView() {
        View view = getView();
        documentRecyclerView = view.findViewById(R.id.recycler_document);

        documentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        documentRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new DocumentAdapter(getContext());

        dir = new File(String.valueOf(Environment.getExternalStorageDirectory().getAbsoluteFile()));
        updateDocumentList3();

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(mActionMode != null){
                            DocumentsContents.mActionMode.finish();
                        }

                        updateDocumentList2();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    @Override
    public void onStart() {
        super.onStart();
        //updateDocumentList();
    }

    public void updateDocumentList2(){
        LoadingDocumentFiles2 loadingDocumentFiles = new LoadingDocumentFiles2();
        loadingDocumentFiles.execute(dir);
    }

    public void updateDocumentList1(){
        LoadingDocumentFiles loadingDocumentFiles = new LoadingDocumentFiles();
        onCreateDialog(DIALOG_KEY);
        loadingDocumentFiles.execute(dir);
    }

    public void updateDocumentList3(){
        LoadingDocumentFiles3 loadingDocumentFiles3 = new LoadingDocumentFiles3();
        loadingDocumentFiles3.execute(dir);
    }

    private void implementListViewClickListeners() {


        adapter.setOnRecylerViewItemLongClickListener(
                new OnRecyclerViewItemLongClickListener() {
                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        onListItemSelect(position);
                        return true;
                    }
                }
        );

        //DocumentAdapter adapter = new DocumentAdapter(getContext(), list);
        adapter.setOnRecyclerViewItemClickListener(
                new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if (mActionMode != null) {
                            onListItemSelect(position);
                        } else {
                            try {

                                String[] mimeTypes =
                                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                                "text/plain",
                                                "application/pdf",
                                                "application/zip"};

                                DocumentFile documentFile = documents.get(position);
                                File file = new File(documentFile.getUrl());
                                Log.i("File info", "absolute Path: " + file.getAbsolutePath());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                /*
                                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(
                                        documentFile.getUrl()
                                ));
                                */

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                                    if (mimeTypes.length > 0) {

                                        Uri documentUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()+".com.example.jay.sdla.provider", file);
                                        intent.setDataAndType(documentUri, mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                                    }
                                } else {
                                    String mimeTypesStr = "";
                                    for (String mimeType : mimeTypes) {
                                        mimeTypesStr += mimeType + "|";
                                    }

                                    Uri documentUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName(), file);
                                    intent.setDataAndType(documentUri, mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                                }

                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);

                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(getContext(), "No Activity Found ", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                }
        );

    }

    private void onListItemSelect(int position) {

        adapter.toggleSelection(position);

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            setNullToActionMode();
        }

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(adapter
                    .getSelectedCount()) + " selected");
        }
    }

    // set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }

    /*
    public void deleteRows() {
        SparseBooleanArray selected = adapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                list.remove(selected.keyAt(i));
                adapter.notifyDataSetChanged();//notify adapter

            }
        }

        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show(); // Show Toast
        mActionMode.finish(); // Finish action mode after use

    }
    */

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions((Activity) getContext(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                } else {
                    ActivityCompat.requestPermissions((Activity) getContext(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                }
            } else {
                populateListView();
                implementListViewClickListeners();
            }
        }else{
            populateListView();
            implementListViewClickListeners();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                            populateListView();
                            implementListViewClickListeners();
                        }
                    } else {
                        Toast.makeText(getContext(), "No Permission granted", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                    return;
            }
        }
    }// end method onRequestPermissionResult


    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;
            mode.getMenuInflater().inflate(R.menu.contextual_action_mode, menu); // inflate the menu over actionbar
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.findItem(R.id.action_encrypt).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            //menu.findItem(R.id.action_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

            SparseBooleanArray selected = adapter.getSelectedIds();
            if (item.getItemId() == R.id.action_encrypt) {

                DocumentFile documentFile = null;
                final List<DocumentFile> selectedDocuments = new ArrayList<>();
                AlertDialog.Builder encryptBuilder = new AlertDialog.Builder(getContext());

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        if(selected.valueAt(i)){
                            documentFile = documents.get(selected.keyAt(i));
                            selectedDocuments.add(documentFile);
                        }
                    }
                }

                if(selectedDocuments.size() == 1){
                    encryptBuilder.setMessage("Encrypt Document " + "\"" + selectedDocuments.get(0).getName() + "\"");
                }else{
                    encryptBuilder.setMessage("Encrypt " + selectedDocuments.size() + " Documents");
                }

                encryptBuilder.setPositiveButton(R.string.button_encrypt,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences preferences = getActivity().getSharedPreferences("keySettings", Context.MODE_PRIVATE);
                                String encryptionKey =
                                        preferences.getString("password", "");
                                File[] files = new File[selectedDocuments.size()];
                                for (int i = 0; i < selectedDocuments.size(); i++) {
                                    File f = new File(selectedDocuments.get(i).getUrl());
                                    //FileEncryptorAndDecryptor encryption = new FileEncryptorAndDecryptor();
                                    //encryption.encrypt(f, encryptionKey);
                                    files[i] = f;
                                }

                                if(encryptionKey.isEmpty()){
                                    //Toast.makeText(getContext(), "Cannot encrypt, there is no Key, please set the key", Toast.LENGTH_LONG);
                                    new ExceptionDialog("Cannot encrypt", "there is no Key, please set the key")
                                            .show(getFragmentManager(), "Dialog");
                                }else if(encryptionKey.length() < 8){
                                    //Toast.makeText(getContext(), "Cannot encrypt, Key must be at least ")
                                    new ExceptionDialog("Cannot encrypt", "Key must have at least 8 characters")
                                            .show(getFragmentManager(), "Dialog");
                                }else{
                                    for(File file : files){
                                        encrypt(file, encryptionKey);
                                    }

                                    mode.finish();
                                    updateDocumentList1();

                                }
                            }
                        });

                encryptBuilder.setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                encryptBuilder.create().show();

            }else if (item.getItemId() == R.id.action_delete) {

                DocumentFile documentFile = null;
                final List<DocumentFile> selectedDocuments = new ArrayList<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.confirm_text);

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        documentFile = documents.get(selected.keyAt(i));
                        selectedDocuments.add(documentFile);
                    }
                }

                if(selectedDocuments.size() == 1){
                    builder.setMessage("Delete Document " + "\"" + selectedDocuments.get(0).getName() + "\"");
                }else{
                    builder.setMessage("Delete " + selectedDocuments.size() + " Documents");
                }

                builder.setPositiveButton(R.string.button_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < selectedDocuments.size(); i++) {
                                    File f = new File(selectedDocuments.get(i).getUrl());
                                    f.delete();
                                }
                                mode.finish();
                                updateDocumentList1();
                                //DocumentsContents.mActionMode.finish();
                            }
                        }
                );

                builder.setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });


                Dialog dialog = builder.create();
                dialog.show();
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.removeSelection();
            setNullToActionMode();
            adapter.notifyDataSetChanged();
        }
    };

    private void encrypt(File file, String key){

        Log.i("TAG", "Security Key : " + key);
        if(!file.isDirectory() && file.exists()){

            encryptorAndDecryptor.encrypt(file, key);
        }else{
            new ExceptionDialog("Cannot encrypt", "The file does not exit or it's not a file")
                    .show(getFragmentManager(), "Dialog");
        }

    }

    public List<DocumentFile> getListOfDocuments(List<File> list) {

        documents = new ArrayList<>();
        DocumentFile documentFile = null;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getName().endsWith(".pdf")) {
                documentFile = new DocumentFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()),R.drawable.pdf);
                documents.add(documentFile);
            }

            if (list.get(i).getName().endsWith(".docx")) {
                documentFile = new DocumentFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()), R.drawable.new_docx);
                documents.add(documentFile);
            }

            if (list.get(i).getName().endsWith(".pptx")) {
                documentFile = new DocumentFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()), R.drawable.pptx);
                documents.add(documentFile);
            }

            if (list.get(i).getName().endsWith((".txt"))) {
                documentFile = new DocumentFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()), R.drawable.txt);
                documents.add(documentFile);
            }

            if (list.get(i).getName().endsWith(".xls")) {
                documentFile = new DocumentFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()), R.drawable.xlsx);
                documents.add(documentFile);
            }

            if (list.get(i).getName().endsWith(".html")) {
                documentFile = new DocumentFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()), R.drawable.html);
                documents.add(documentFile);
            }

        }

        return documents;
    }

    public List<File> getListItems(File dir) throws Exception {

        //File downloadsFolder = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File listFile[] = dir.listFiles();
        //DocumentFile document = null;

        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getListItems(listFile[i]);
                } else {
                    boolean booleanDocument = false;
                    if (listFile[i].getName().endsWith(".pdf") || listFile[i].getName().endsWith(".docx")
                            || listFile[i].getName().endsWith(".pptx") || listFile[i].getName().endsWith(".txt")
                            || listFile[i].getName().endsWith((".xls")) || listFile[i].getName().endsWith(".html")) {

                        for (int j = 0; j < fileList1.size(); j++) {

                            if (fileList1.get(j).getName().equals(listFile[i].getName())) {
                                booleanDocument = true;
                            } else {

                            }
                        }

                        if (booleanDocument) {
                            booleanDocument = false;
                        } else {
                            fileList1.add(listFile[i]);
                        }
                    }
                }
            }
        }

        return fileList1;
    }

    public  String formatBytes(long bytes) {
        // TODO: add flag to which part is needed (e.g. GB, MB, KB or bytes)
        String retStr = "";
        // One binary gigabyte equals 1,073,741,824 bytes.
        if (bytes > 1073741824) {// Add GB
            long gbs = bytes / 1073741824;
            retStr += (new Long(gbs)).toString() + "GB ";
            bytes = bytes - (gbs * 1073741824);
        }
        // One MB - 1048576 bytes
        if (bytes > 1048576) {// Add GB
            long mbs = bytes / 1048576;
            retStr += (new Long(mbs)).toString() + "MB ";
            bytes = bytes - (mbs * 1048576);
        }
        if (bytes > 1024) {
            long kbs = bytes / 1024;
            retStr += (new Long(kbs)).toString() + "KB";
            bytes = bytes - (kbs * 1024);
        } else
            retStr += (new Long(bytes)).toString() + " bytes";
        return retStr;
    }

    public class LoadingDocumentFiles extends AsyncTask<File, Integer, List<DocumentFile>> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected List<DocumentFile> doInBackground(File... files) {

            List<File> fileList = new ArrayList<>();

            try {
                fileList1 = new ArrayList<>();
                fileList = getListItems(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return getListOfDocuments(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            mProgressDialog.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(List<DocumentFile> documentFiles) {
            adapter.addDocumentList(documentFiles);
            documentRecyclerView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

    public class LoadingDocumentFiles2 extends AsyncTask<File, Integer, List<DocumentFile>>{

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<DocumentFile> doInBackground(File... files) {

            List<File> fileList = new ArrayList<>();

            try {
                fileList1 = new ArrayList<>();
                fileList = getListItems(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return getListOfDocuments(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(List<DocumentFile> documentFiles) {
            adapter.addDocumentList(documentFiles);
            documentRecyclerView.setAdapter(adapter);
        }
    }

    public class LoadingDocumentFiles3 extends AsyncTask<File, Integer, List<DocumentFile>>{

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<DocumentFile> doInBackground(File... files) {

            List<File> fileList = new ArrayList<>();

            try {
                fileList1 = new ArrayList<>();
                fileList = getListItems(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return getListOfDocuments(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<DocumentFile> documentFiles) {
            adapter.addDocumentList(documentFiles);
            documentRecyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    protected Dialog onCreateDialog(int id){

        switch (id){
            case DIALOG_KEY:
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMessage("Please wait for few seconds...");
                mProgressDialog.setCancelable(false);

                return mProgressDialog;
        }

        return null;
    }

}
