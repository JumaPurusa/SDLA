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
import android.os.FileUriExposedException;
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

import com.example.jay.sdla.Adapters.VideoAdapter;
import com.example.jay.sdla.Dialogs.ExceptionDialog;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemClickListener;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemLongClickListener;
import com.example.jay.sdla.Models.VideoFile;
import com.example.jay.sdla.R;
import com.example.jay.sdla.Utils.FileEncryptorAndDecryptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */

public class VideoContents extends Fragment {

    public static final int MY_PERMISSION_REQUEST = 1;
    public static final int DIALOG_KEY = 0;

    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;

    private List<VideoFile> listOfVideos;
    //List<VideoFile> selectedItems;
    public static ActionMode mActionMode;

    File dir;

    ProgressDialog mProgressDialog;
    ProgressBar progressBar;

    private List<File> fileList1;

    FileEncryptorAndDecryptor encryptorAndDecryptor;

    public VideoContents() {

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

    public void getStuff() {

        View view = getView();

        videoRecyclerView = view.findViewById(R.id.recycler_document);
        videoRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        videoRecyclerView.setLayoutManager(linearLayoutManager);
        //videoRecyclerView.addItemDecoration( new MyItemDecoration(getContext()) );
        videoAdapter = new VideoAdapter(getContext());

        dir = new File(String.valueOf(Environment.getExternalStorageDirectory().getAbsoluteFile()));

        updateVideosList1();

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        if(mActionMode != null){
                            VideoContents.mActionMode.finish();
                        }

                        updateVideosList2();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    public void updateVideosList1(){

        LoadingVideosTask1 loading1 = new LoadingVideosTask1();
        loading1.execute(dir);
    }

    public void updateVideosList2(){

        LoadingVideosTask2 loading2 = new LoadingVideosTask2();
        loading2.execute(dir);
    }

    public void updateVideosList3(){

        LoadingVideosTask3 loading3 = new LoadingVideosTask3();
        onCreateDialog(DIALOG_KEY);
        loading3.execute(dir);
    }

    private void implementListViewClickListeners() {

        videoAdapter.setOnRecyclerViewItemLongClickListener(
                new OnRecyclerViewItemLongClickListener() {
                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        onListItemSelect(position);
                        return true;
                    }
                }
        );

        videoAdapter.setOnRecyclerViewItemClickListener(
                new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(mActionMode != null){
                            onListItemSelect(position);
                        }else{
                            try{

                                Intent implicitIntent = new Intent();
                                implicitIntent.setAction(Intent.ACTION_VIEW);
                                VideoFile obj = listOfVideos.get(position);
                                File file = new File(obj.getUrl());
                                Uri videoUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()+".com.example.jay.sdla.provider", file);
                                implicitIntent.setDataAndType(videoUri, "video/*");
                                implicitIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(implicitIntent);
                            }catch(ActivityNotFoundException e){
                                Toast.makeText(getContext(), "No activity Found", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
        );

    }

    private void onListItemSelect(int position) {

        videoAdapter.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = videoAdapter.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && mActionMode == null) {
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        } else if (!hasCheckedItems && mActionMode != null) {
            // there no selected items, finish the actionMode
            mActionMode.finish();
            setNullToActionMode();
        }

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(videoAdapter
                    .getSelectedCount()) + " selected");
        }

    }

    public List<VideoFile> getListOfAudioFile(List<File> fileList){
        listOfVideos = new ArrayList<>();

        for(int i=0; i<fileList.size(); i++){

            if(fileList.get(i).getName().endsWith(".mp4") || fileList.get(i).getName().endsWith(".avi")
                    || fileList.get(i).getName().endsWith(".flv") || fileList.get(i).getName().endsWith(".wmv")
                    || fileList.get(i).getName().endsWith(".mov")){

                listOfVideos.add(new VideoFile(fileList.get(i).getAbsolutePath(),
                        fileList.get(i).getName(), formatBytes(fileList.get(i).length())));
            }

        }

        return listOfVideos;
    }

    public List<File> getFileList(File dir) throws Exception{

        File listFiles[] = dir.listFiles();

        if (listFiles != null && listFiles.length > 0) {
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    getFileList(listFiles[i]);
                } else {
                    boolean booleanAudio = false;
                    if (listFiles[i].getName().endsWith(".mp4") || listFiles[i].getName().endsWith(".avi")
                            || listFiles[i].getName().endsWith(".flv") || listFiles[i].getName().endsWith(".wmv")
                            || listFiles[i].getName().endsWith(".mov")) {

                        for (int j = 0; j < fileList1.size(); j++) {

                            if (fileList1.get(j).getName().equals(listFiles[i].getName())) {
                                booleanAudio = true;
                            } else {

                            }
                        }

                        if (booleanAudio) {
                            booleanAudio = false;
                        } else {
                            fileList1.add(listFiles[i]);
                        }
                    }
                }
            }
        }

        return fileList1;
    }

    public String formatBytes(long bytes) {
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

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        VideoAdapter videoAdapter = new VideoAdapter(getContext());
        SparseBooleanArray selected = videoAdapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                listOfVideos.remove(selected.keyAt(i));
                videoAdapter.notifyDataSetChanged();//notify adapter

            }
        }
        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

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
                getStuff();
                implementListViewClickListeners();
            }

        }else{

            getStuff();
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
                            getStuff();
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
            final SparseBooleanArray selected = videoAdapter.getSelectedIds();
            if(item.getItemId() == R.id.action_encrypt){

                VideoFile videoFile = null;
                final List<VideoFile> selectedVideos = new ArrayList<>();
                AlertDialog.Builder encryptBuilder = new AlertDialog.Builder(getContext());

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        if(selected.valueAt(i)){
                            videoFile = listOfVideos.get(selected.keyAt(i));
                            selectedVideos.add(videoFile);
                        }
                    }
                }

                if(selectedVideos.size() == 1){
                    encryptBuilder.setMessage("Encrypt Video " + "\"" + selectedVideos.get(0).getName() + "\"");
                }else{
                    encryptBuilder.setMessage("Encrypt " + selectedVideos.size() + " Video Files");
                }

                encryptBuilder.setPositiveButton(R.string.button_encrypt,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences preferences = getActivity().getSharedPreferences("keySettings", Context.MODE_PRIVATE);
                                String encryptionKey =
                                        preferences.getString("password", "");
                                File[] files = new File[selectedVideos.size()];
                                for (int i = 0; i < selectedVideos.size(); i++) {
                                    File f = new File(selectedVideos.get(i).getUrl());
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
                                    updateVideosList3();

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



            } else if(item.getItemId() == R.id.action_delete){

                VideoFile videoFile = null;
                final List<VideoFile> selectedVideos = new ArrayList<>();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.confirm_text);

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        if(selected.valueAt(i)){
                            videoFile = listOfVideos.get(selected.keyAt(i));
                            selectedVideos.add(videoFile);
                        }
                    }
                }


                if(selectedVideos.size() == 1){
                    builder.setMessage("Delete Video " + "\"" + selectedVideos.get(0).getName() + "\"");
                }else{
                    builder.setMessage("Delete " + selectedVideos.size() + " Video Files");
                }

                builder.setPositiveButton(R.string.button_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < selectedVideos.size(); i++) {
                                    File f = new File(selectedVideos.get(i).getUrl());
                                    f.delete();
                                }
                                mode.finish();
                                updateVideosList3();
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
            videoAdapter.removeSelection(); // remove selection
            setNullToActionMode();
            videoAdapter.notifyDataSetChanged();

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


    public class LoadingVideosTask1 extends AsyncTask<File, Integer, List<VideoFile>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<VideoFile> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();

            try {
                fileList1 = new ArrayList<>();
                fileList = getFileList(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return getListOfAudioFile(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<VideoFile> list) {
            videoAdapter.addVideoList(list);
            videoRecyclerView.setAdapter(videoAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    public class LoadingVideosTask2 extends AsyncTask<File, Integer, List<VideoFile>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<VideoFile> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();

            try {
                fileList1 = new ArrayList<>();
                fileList = getFileList(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return getListOfAudioFile(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(List<VideoFile> list) {
            videoAdapter.addVideoList(list);
            videoRecyclerView.setAdapter(videoAdapter);
        }
    }

    public class LoadingVideosTask3 extends AsyncTask<File, Integer, List<VideoFile>> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected List<VideoFile> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();

            try {
                fileList1 = new ArrayList<>();
                fileList = getFileList(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return getListOfAudioFile(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<VideoFile> list) {
            videoAdapter.addVideoList(list);
            videoRecyclerView.setAdapter(videoAdapter);
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

}
