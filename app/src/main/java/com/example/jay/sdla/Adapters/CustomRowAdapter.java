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
import android.widget.TextView;

import com.example.jay.sdla.Models.SongInfo;
import com.example.jay.sdla.R;

import java.util.ArrayList;
import java.util.List;

public class CustomRowAdapter extends RecyclerView.Adapter<CustomRowAdapter.MyViewHolder> {

    private View view;
    private Context context;
    private List<SongInfo> itemModels;
    private SparseBooleanArray mSelectedItemIds;

    private OnLongItemClickListener onLongItemClickListener;
    private OnItemClickListener onItemClickListener;

    public interface OnLongItemClickListener{
        boolean onLongItemClick(View view, int position);
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnLongItemClickListener(OnLongItemClickListener onLongItemClickListener){
        this.onLongItemClickListener = onLongItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public CustomRowAdapter(Context context){
        this.context = context;
        this.mSelectedItemIds = new SparseBooleanArray();
    }

    public void addAudiosList(List<SongInfo> list){
        this.itemModels = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.custom_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.itemImage.setImageResource(R.drawable.audio_icon);
        holder.itemName.setText(itemModels.get(position).getName());
        holder.itemSize.setText((itemModels.get(position).getSize()));

        holder.cardView
                .setBackgroundColor(
                        mSelectedItemIds.get(position) ? Color.LTGRAY
                                : Color.TRANSPARENT
                );

        holder.itemView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onLongItemClickListener.onLongItemClick(v, position);
                        return true;
                    }
                }
        );

        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView itemImage;
        public TextView itemName;
        public TextView itemSize;
        public CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.audio_image);
            itemName = itemView.findViewById(R.id.song_name);
            itemSize = itemView.findViewById(R.id.file_size);
            cardView = itemView.findViewById(R.id.cardView);
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

    public List<SongInfo> getSelectedItem(){
        List<SongInfo> newList = new ArrayList<>();
        for(int i=0; i<mSelectedItemIds.size(); i++){
            newList.add(itemModels.get(mSelectedItemIds.keyAt(i)));
        }

        return newList;
    }
}
