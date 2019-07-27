package com.example.jay.sdla.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
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
import android.support.v7.widget.GridLayoutManager;
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

import com.example.jay.sdla.Adapters.ImagesAdapter;
import com.example.jay.sdla.Dialogs.ExceptionDialog;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemClickListener;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemLongClickListener;
import com.example.jay.sdla.Models.ImageFile;
import com.example.jay.sdla.R;
import com.example.jay.sdla.Utils.FileEncryptorAndDecryptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class ImagesContent extends Fragment {

    public static final int MY_PERMISSION_REQUEST = 1;
    public static final int DIALOG_KEY = 0;

    private RecyclerView imagesRecyclerView;
    private ImagesAdapter imagesAdapter;
    List<ImageFile> listOfImages;
    public static ActionMode mActionMode;

    FileEncryptorAndDecryptor encryptorAndDecryptor;

    private List<File> fileList1;

    ProgressDialog mProgressDialog;

    ProgressBar progressBar;

    File dir;

    public ImagesContent() {
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

    public void getStuff() {
        View view = getView();
        imagesRecyclerView = view.findViewById(R.id.recycler_document);
        imagesRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        imagesRecyclerView.setLayoutManager(gridLayoutManager);

        imagesAdapter = new ImagesAdapter(getContext());

        dir = new File(String.valueOf(Environment.getExternalStorageDirectory().getAbsoluteFile()));

        //imagesRecyclerView.setAdapter(imagesAdapter);
        updateImageLoading();

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateImageSwipe();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

    }

    public void updateImageLoading(){

        ImagesLoading loading = new ImagesLoading();
        loading.execute(dir);
    }

    public void updateImageSwipe(){
        LoadingImagesSwipe loadingImagesSwipe = new LoadingImagesSwipe();
        loadingImagesSwipe.execute(dir);
    }

    public void updateImageList(){
        LoadingImages loadingImages = new LoadingImages();
        onCreateDialog(DIALOG_KEY);
        loadingImages.execute(dir);
    }

    private void implementListViewClickListeners(){

        imagesAdapter.setOnRecyclerViewItemLongClickListener(
                new OnRecyclerViewItemLongClickListener() {
                    @Override
                    public boolean onLongItemClick(View view, int position) {
                        onListItemSelect(position);
                        return true;
                    }
                }
        );

        imagesAdapter.setOnRecyclerViewItemClickListener(
                new OnRecyclerViewItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if(mActionMode != null){
                            onListItemSelect(position);
                        }else{

                           /*
                            try{

                                ImageFile imageFile = listOfImages.get(position);
                                File file = new File(imageFile.getUrl());
                                Uri uri = Uri.parse("file://" + file.getPath());
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(
                                        imageFile.getUrl()));
                                intent.setDataAndType(uri, "image/*");
                                startActivity(intent);

                            }catch(ActivityNotFoundException e){
                                Toast.makeText(getContext(), "No activity Found", Toast.LENGTH_SHORT).show();
                            }
                            */



                            ImageFile imageFile = listOfImages.get(position);
                            final File file = new File(imageFile.getUrl());
                            MediaScannerConnection.scanFile(getContext(), new String[] { file.toString() },
                                    null, new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.wtf("onScanCompleted", "yes");


                                            Intent intent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            Uri imageUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName()+".com.example.jay.sdla.provider", file);
                                            intent.setDataAndType(imageUri, "image/*");
                                            intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION); //must for reading data from directory
                                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            startActivity(intent);
                                        }
                                    });

                        }
                    }
                }
        );
    }

    private void onListItemSelect(int position){

        imagesAdapter.toggleSelection(position);

        boolean hasCheckedItems = imagesAdapter.getSelectedCount() > 0;

        if(hasCheckedItems && mActionMode == null){
            mActionMode = ((AppCompatActivity)getActivity()).startSupportActionMode(actionModeCallback);
        }else if(!hasCheckedItems && mActionMode != null){
            mActionMode.finish();
            setNullToActionMode();
        }

        if (mActionMode != null) {
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(imagesAdapter
                    .getSelectedCount()) + " selected");
        }
    }

    /*
    public void getImages(){
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] proj = {  MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        Cursor imagesCursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);

        if(imagesCursor != null){
            if (imagesCursor.moveToFirst()) {
                do {
                    String url = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    String size = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.SIZE));


                    File file = new File(url);
                    listOfImages.add(new ImageFile(url, file.getName(), formatBytes(Long.parseLong(size))));

                } while (imagesCursor.moveToNext());
            }
        }
        imagesCursor.close();

        imagesAdapter.addImagesList(listOfImages);
        imagesRecyclerView.setAdapter(imagesAdapter);
    }
    */

    public List<File> getListItems(File dir) throws Exception {

        //File downloadsFolder = Environment.getExternalStorageDirectory().getAbsoluteFile();
        File listFile[] = dir.listFiles();
        //DocumentFile document = null;

        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    getListItems(listFile[i]);
                } else {
                    boolean booleanImage = false;
                    if (listFile[i].getName().endsWith(".png") || listFile[i].getName().endsWith(".jpg")){

                        for (int j = 0; j < fileList1.size(); j++) {

                            if (fileList1.get(j).getName().equals(listFile[i].getName())) {
                                booleanImage = true;
                            } else {

                            }
                        }

                        if (booleanImage) {
                            booleanImage = false;
                        } else {
                            fileList1.add(listFile[i]);
                        }
                    }
                }
            }
        }

        return fileList1;
    }

    public List<ImageFile> getListOfImageFiles(List<File> list){

        listOfImages = new ArrayList<>();
        ImageFile imageFile = null;

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getName().endsWith(".png")) {
                imageFile = new ImageFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()));
                listOfImages.add(imageFile);
            }


            if (list.get(i).getName().endsWith(".jpg")) {
                imageFile = new ImageFile(list.get(i).getAbsolutePath(),
                        list.get(i).getName(), formatBytes(list.get(i).length()));
                listOfImages.add(imageFile);
            }
        }

        return listOfImages;
    }


    public static String formatBytes(long bytes) {
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


    // set action mode null after use
    public void setNullToActionMode(){
        if(mActionMode != null){
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

            final SparseBooleanArray selected = imagesAdapter.getSelectedIds();
            if(item.getItemId() == R.id.action_encrypt){

                ImageFile imageFile = null;
                final List<ImageFile> selectedImages = new ArrayList<>();
                AlertDialog.Builder encryptBuilder = new AlertDialog.Builder(getContext());

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        if(selected.valueAt(i)){
                            imageFile = listOfImages.get(selected.keyAt(i));
                            selectedImages.add(imageFile);
                        }
                    }
                }

                if(selectedImages.size() == 1){
                    encryptBuilder.setMessage("Encrypt Image File " + "\"" + selectedImages.get(0).getName() + "\"");
                }else{
                    encryptBuilder.setMessage("Encrypt " + selectedImages.size() + " Image Files");
                }

                encryptBuilder.setPositiveButton(R.string.button_encrypt,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences preferences = getActivity().getSharedPreferences("keySettings", Context.MODE_PRIVATE);
                                String encryptionKey =
                                        preferences.getString("password", "");
                                File[] files = new File[selectedImages.size()];
                                for (int i = 0; i < selectedImages.size(); i++) {
                                    File f = new File(selectedImages.get(i).getUrl());
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
                                    updateImageList();

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


            }else if(item.getItemId() == R.id.action_delete){

                ImageFile imageFile = null;
                final List<ImageFile> selectedImages = new ArrayList<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.confirm_text);

                for(int i=0; i<selected.size(); i++){

                    if(selected.valueAt(i)){
                        imageFile = listOfImages.get(selected.keyAt(i));
                        selectedImages.add(imageFile);
                    }
                }

                if(selectedImages.size() == 1){
                    builder.setMessage("Delete Image File " + "\"" + selectedImages.get(0).getName() + "\"");
                }else{
                    builder.setMessage("Delete " + selectedImages.size() + " Image Files");
                }

                builder.setPositiveButton(R.string.button_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < selectedImages.size(); i++) {
                                    File f = new File(selectedImages.get(i).getUrl());
                                    f.delete();
                                }
                                mode.finish();
                                updateImageList();
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
            imagesAdapter.removeSelection();
            setNullToActionMode();
            imagesAdapter.notifyDataSetChanged();
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

    public class LoadingImages extends AsyncTask<File, Integer, List<ImageFile>> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }

        @Override
        protected List<ImageFile> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();
            try {
                fileList1 = new ArrayList<>();
                fileList = getListItems(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getListOfImageFiles(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<ImageFile> imageFiles) {
            imagesAdapter.addImagesList(imageFiles);
            imagesRecyclerView.setAdapter(imagesAdapter);
            mProgressDialog.dismiss();
        }
    }

    public class ImagesLoading extends AsyncTask<File, Integer, List<ImageFile>>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ImageFile> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();
            try {
                fileList1 = new ArrayList<>();
                fileList = getListItems(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getListOfImageFiles(fileList);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<ImageFile> imageFiles) {
            imagesAdapter.addImagesList(imageFiles);
            imagesRecyclerView.setAdapter(imagesAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    public class LoadingImagesSwipe extends AsyncTask<File, Void, List<ImageFile>>{

        @Override
        protected List<ImageFile> doInBackground(File... files) {
            List<File> fileList = new ArrayList<>();
            try {
                fileList1 = new ArrayList<>();
                fileList = getListItems(files[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return getListOfImageFiles(fileList);
        }

        @Override
        protected void onPostExecute(List<ImageFile> imageFiles) {
            imagesAdapter.addImagesList(imageFiles);
            imagesRecyclerView.setAdapter(imagesAdapter);
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
