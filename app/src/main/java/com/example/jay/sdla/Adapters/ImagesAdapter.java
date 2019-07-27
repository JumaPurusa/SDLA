package com.example.jay.sdla.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemClickListener;
import com.example.jay.sdla.Listeners.OnRecyclerViewItemLongClickListener;
import com.example.jay.sdla.Models.ImageFile;
import com.example.jay.sdla.R;

import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>{

    private List<ImageFile> listOfImages;
    private Context context;
    private OnRecyclerViewItemClickListener listener;
    private OnRecyclerViewItemLongClickListener longListener;
    private SparseBooleanArray mSelectedItemIds;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    public ImagesAdapter(Context context){
        this.context = context;
        //this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //this.listOfImages = list;
        mSelectedItemIds = new SparseBooleanArray();
    }

    public void addImagesList(List<ImageFile> list){
        this.listOfImages = list;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image_item, parent, false);
        ImageViewHolder holder = new ImageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, final int position) {

        Glide.with(context)
                .load(listOfImages.get(position).getUrl().toString())
                .into(holder.imageView);

        holder.cardView
                .setBackgroundColor(
                        mSelectedItemIds.get(position) ?  Color.parseColor("#D6FF5722")
                                : Color.TRANSPARENT
                );

        holder.imageView.
                setBackgroundColor(
                        mSelectedItemIds.get(position) ? Color.parseColor("#FF4096FF")
                                : Color.TRANSPARENT
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

    }

    @Override
    public int getItemCount() {
        return listOfImages.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }

    public void toggleSelection(int position){
        selectView(position, !mSelectedItemIds.get(position));
    }

    //Remove selected selections
    public void removeSelection(){
        mSelectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value){
        if(value){
            mSelectedItemIds.put(position, value);
        }else{
            mSelectedItemIds.delete(position);
        }

        notifyDataSetChanged();
    }

    public int getSelectedCount(){
        return mSelectedItemIds.size();
    }

    public SparseBooleanArray getSelectedIds(){
        return mSelectedItemIds;
    }

}
