package com.example.jay.sdla.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.jay.sdla.Adapters.EncryptedDocumentsAdapter;
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

public class EncryptedDocuments extends Fragment {

    public static final int MY_PERMISSION_REQUEST = 1;
    public static final int DIALOG_KEY = 0;

    private RecyclerView recyclerView;
    private EncryptedDocumentsAdapter encryptedDocumentsAdapter;
    private List<DocumentFile> listOfEncryptedDocuments;

    List<File> fileList1;

    File dir;

    public static ActionMode mActionMode;

    ProgressDialog mProgressDialog;

    public EncryptedDocuments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_encrypted_documents, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void updateDocumentList(){
        LoadingEncryptedDocumentsTask2 loading2 = new LoadingEncryptedDocumentsTask2();
        onCreateDialog(DIALOG_KEY);
        loading2.execute(dir);
    }

    public void updateDocumentList1(){
        LoadingEncryptedDocumentsTask loading1 = new LoadingEncryptedDocumentsTask();
        loading1.execute(dir);
    }

    public void populateListView(){

        View view = getView();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        encryptedDocumentsAdapter = new EncryptedDocumentsAdapter(getContext());

        dir = new File(String.valueOf(Environment.getExternalStorageDirectory().getAbsoluteFile()));

        LoadingEncryptedDocumentsTask loading = new LoadingEncryptedDocumentsTask();
        loading.execute(dir);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if(mActionMode != null){
                            EncryptedDocuments.mActionMode.finish();
                        }

                        updateDocumentList1();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    private void implementListViewClickListeners(){

        encryptedDocumentsAdapter.setOnRecyclerViewItemClickListener(
                new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if(mActionMode != null){
                            onListItemSelect(position);
                        }else{

                            DocumentFile EncryptedDocumentFile = listOfEncryptedDocuments.get(position);
                            File file = new File(EncryptedDocumentFile.getUrl());
                            alertIfEncryptedDialog(file);
                        }
                    }
                }
        );

        encryptedDocumentsAdapter.setOnRecylerViewItemLongClickListener(
                new OnRecyclerViewItemLongClickListener() {
                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        onListItemSelect(position);
                        return true;
                    }
                }
        );
    }

    private void onListItemSelect(int position) {

        encryptedDocumentsAdapter.toggleSelection(position);

        boolean hasCheckedItems = encryptedDocumentsAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            setNullToActionMode();
        }

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(encryptedDocumentsAdapter
                    .getSelectedCount()) + " selected");
        }
    }

    // set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null) {
            mActionMode = null;
        }
    }


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
    }

    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;
            mode.getMenuInflater().inflate(R.menu.contextual_action_encrypted, menu); // inflate the menu over actionbar
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.findItem(R.id.action_decrypt).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

            SparseBooleanArray selected = encryptedDocumentsAdapter.getSelectedIds();
            if (item.getItemId() == R.id.action_decrypt) {


                DocumentFile encryptedDocumentFile = null;
                final List<DocumentFile> encryptedSelectedDocs = new ArrayList<>();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        encryptedDocumentFile = listOfEncryptedDocuments.get(selected.keyAt(i));
                        encryptedSelectedDocs.add(encryptedDocumentFile);
                    }
                }

                if(encryptedSelectedDocs.size() == 1){
                    builder.setMessage("Decrypt Document " + "\"" + encryptedSelectedDocs.get(0).getName() + "\"");
                }else{
                    builder.setMessage("Decrypt " + encryptedSelectedDocs.size() + " encrypted Documents");
                }

                builder.setPositiveButton(R.string.button_decrypted,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                /*
                                for(int i=0; i<encryptedSelectedDocs.size(); i++){
                                    File f = new File(encryptedSelectedDocs.get(i).getUrl());
                                    f.delete();
                                }
                                */
                                decryptionDialog(encryptedSelectedDocs);
                                //mode.finish();
                                //updateDocumentList();
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

                builder.create().show();
            } else if (item.getItemId() == R.id.action_delete) {

                //EnterSecurityKeyDialog deleteDialog = new EnterSecurityKeyDialog(documents, adapter);
                //deleteDialog.show(getFragmentManager(), "delete dialog");
                //updateDocumentList();

                DocumentFile encryptedDocumentFile = null;
                final List<DocumentFile> encryptedSelectedDocs = new ArrayList<>();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.confirm_text);

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        encryptedDocumentFile = listOfEncryptedDocuments.get(selected.keyAt(i));
                        encryptedSelectedDocs.add(encryptedDocumentFile);
                    }
                }

                if(encryptedSelectedDocs.size() == 1){
                    builder.setMessage("Delete Document " + "\"" + encryptedSelectedDocs.get(0).getName() + "\"");
                }else{
                    builder.setMessage("Delete " + encryptedSelectedDocs.size() + " encrypted Documents");
                }

                builder.setPositiveButton(R.string.button_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i=0; i<encryptedSelectedDocs.size(); i++){
                                    File f = new File(encryptedSelectedDocs.get(i).getUrl());
                                    f.delete();
                                }

                                mode.finish();
                                updateDocumentList();
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

                //mode.finish();
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            encryptedDocumentsAdapter.removeSelection();
            setNullToActionMode();
            encryptedDocumentsAdapter.notifyDataSetChanged();
        }
    };

    public List<File> getListItems(File dir) throws Exception {

        File listFile[] = dir.listFiles();

        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getListItems(listFile[i]);
                } else {
                    boolean booleanDocument = false;
                    if (listFile[i].getName().endsWith(".pdf.enc") || listFile[i].getName().endsWith(".docx.enc")
                            || listFile[i].getName().endsWith(".pptx.enc") || listFile[i].getName().endsWith(".txt.enc")
                            || listFile[i].getName().endsWith((".xls.enc")) || listFile[i].getName().endsWith(".html.enc")) {

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

    public List<DocumentFile> getListOfDocuments(List<File> files){

        listOfEncryptedDocuments = new ArrayList<>();

        for(int i=0; i<files.size(); i++){

            if(files.get(i).getName().endsWith(".enc")){
                DocumentFile documentFile = new DocumentFile(files.get(i).getAbsolutePath(),
                        files.get(i).getName(), formatBytes(files.get(i).length()), R.drawable.generic_file_icon);

                listOfEncryptedDocuments.add(documentFile);
            }
        }

        return listOfEncryptedDocuments;
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

    public class LoadingEncryptedDocumentsTask extends AsyncTask<File, Void, List<DocumentFile>> {

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
        protected void onPostExecute(List<DocumentFile> list) {
            encryptedDocumentsAdapter.addListOfEncryptedDoc(list);
            recyclerView.setAdapter(encryptedDocumentsAdapter);
        }
    }

    public class LoadingEncryptedDocumentsTask2 extends AsyncTask<File, Integer, List<DocumentFile>>{

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
        protected void onPostExecute(List<DocumentFile> list) {
            encryptedDocumentsAdapter.addListOfEncryptedDoc(list);
            recyclerView.setAdapter(encryptedDocumentsAdapter);
            mProgressDialog.dismiss();
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

    public void alertIfEncryptedDialog(File file){

        final List<DocumentFile> listTobeDec = new ArrayList<>();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());

        alertBuilder.setMessage("file " + "\"" + file.getName() + "\"" + " is encrypted. It cannot be used");

        final DocumentFile encrypted = new DocumentFile(file.getAbsolutePath(), file.getName(), formatBytes(file.length()), R.drawable.file);
        listTobeDec.add(encrypted);
        alertBuilder.setPositiveButton(R.string.button_decrypted,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        decryptionDialog(listTobeDec);
                    }
                });

        alertBuilder.setNegativeButton(R.string.button_cancel2,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertBuilder.create().show();
    }

    public void decryptionDialog(final List<DocumentFile> encryptedSelectedDocs){

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.decryption_dialog, null);
        builder.setView(view);
        final EditText decryptionKeyText = view.findViewById(R.id.decryption_key);

        builder.setPositiveButton(R.string.button_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String decryptionKey = decryptionKeyText.getText().toString();
                        //FileEncryptorAndDecryptor decryptor = new FileEncryptorAndDecryptor();
                        passDecryptionKey(decryptionKey, encryptedSelectedDocs);

                        /*
                        for(int i=0; i<encryptedSelectedDocs.size(); i++){
                                File f = new File(encryptedSelectedDocs.get(i).getUrl());
                                decryptor.decrypt(f, decKey);
                        }
                        */


                    }
                });

        builder.setNegativeButton(R.string.button_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.create().show();
    }

    public void passDecryptionKey(String decryptionKey, List<DocumentFile> encryptedSelectedDocs){


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("keySettings", Context.MODE_PRIVATE);
        String securityKey = sharedPreferences.getString("password", "key");

        Log.i("TAG", "securityKey : " + securityKey);

        Log.i("TAG", "decryptionKey : " + decryptionKey);

        String decValue = sharedPreferences.getString("password", decryptionKey);

        FileEncryptorAndDecryptor decryptor = new FileEncryptorAndDecryptor();

        if(decryptionKey.isEmpty()){
            Toast.makeText(getContext(),
                    "please provide the key", Toast.LENGTH_LONG).show();
        }else if(!decryptionKey.equals(securityKey)){
            Toast.makeText(getContext(),
                    "Wrong Key", Toast.LENGTH_LONG).show();
        }else{

            for(int i=0; i<encryptedSelectedDocs.size(); i++){
                File f = new File(encryptedSelectedDocs.get(i).getUrl());
                try {
                    decryptor.decrypt(f, decryptionKey);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Wrong Key", Toast.LENGTH_LONG).show();
                }
            }

            if(mActionMode != null){
                EncryptedDocuments.mActionMode.finish();
            }
            updateDocumentList();
        }

    }


}
