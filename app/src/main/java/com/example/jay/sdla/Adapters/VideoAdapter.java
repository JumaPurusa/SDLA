package com.example.jay.sdla.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemClickListener;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemLongClickListener;
import com.example.jay.sdla.Models.VideoFile;
import com.example.jay.sdla.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private View view;
    private Context context;
    private List<VideoFile> itemModels;
    private SparseBooleanArray mSelectedItemIds;

    private OnRecyclerViewItemClickListener listener;
    private OnRecyclerViewItemLongClickListener longListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener longListener) {
        this.longListener = longListener;
    }



    public VideoAdapter(Context context){
        this.context = context;
        this.mSelectedItemIds = new SparseBooleanArray();
    }

    public void addVideoList(List<VideoFile> list){
        this.itemModels = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.video_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        /*
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(itemModels.get(position).getVideoUrl().toString(),
                  MediaStore.Images.Thumbnails.MINI_KIND);
        Matrix matrix = new Matrix();
        Bitmap bmThumbnail = Bitmap.createBitmap(thumb, 0, 0,
                thumb.getWidth(), thumb.getHeight(), matrix, true);
                */

        Glide.with(context)
                .load(itemModels.get(position).getUrl().toString())
                .into(holder.videoImage);


        //holder.videoImage.setImageBitmap(bmThumbnail);
        holder.videoName.setText(itemModels.get(position).getName());
        holder.videoSize.setText((itemModels.get(position).getSize()));
        //holder.videoDuration.setText((itemModels.get(position).getVideoDuration() + "s"));


        holder.cardView
                .setBackgroundColor(
                        mSelectedItemIds.get(position) ? Color.LTGRAY
                                : Color.TRANSPARENT
                );


        holder.itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(longListener != null)
                            longListener.onLongItemClick(v, position);

                        return true;
                    }
                }
        );

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null)
                            listener.onItemClick(v, position);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView videoImage;
        public TextView videoName;
        public TextView videoSize;
        //public TextView videoDuration;
        public RelativeLayout cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            videoImage = itemView.findViewById(R.id.video_image);
            videoName= itemView.findViewById(R.id.video_name);
            videoSize = itemView.findViewById(R.id.video_size);
            //videoDuration = itemView.findViewById(R.id.video_duration);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }


    /***
     * Methods required for do selections, remove selections, etc.
     */

    //Toggle selection methods
    public void toggleSelection(int position){
        selectView(position, !mSelectedItemIds.get(position));
    }

    //Remove selected selections
    public void removeSelection(){
        mSelectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    //Put or delete selected position into SparseBooleanArray
    public void selectView(int position, boolean value){
        if(value){
            mSelectedItemIds.put(position, value);
        }else{
            mSelectedItemIds.delete(position);
        }

        notifyDataSetChanged();
    }

    // Get total selected count
    public int getSelectedCount(){
        return mSelectedItemIds.size();
    }

    // return all Selected ids
    public SparseBooleanArray getSelectedIds(){
        return mSelectedItemIds;
    }

}

