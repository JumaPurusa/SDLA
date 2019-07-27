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
import com.example.jay.sdla.Models.DocumentFile;
import com.example.jay.sdla.R;

import java.util.List;

public class EncryptedDocumentsAdapter extends  RecyclerView.Adapter<EncryptedDocumentsAdapter.MyViewHolder>{

    private Context context;
    private List<DocumentFile> listOfEncryptedDoc;
    SparseBooleanArray mSelectedItemIds;

    private OnRecyclerViewItemClickListener listener;
    private OnRecyclerViewItemLongClickListener longClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener){
        this.listener = listener;
    }

    public void setOnRecylerViewItemLongClickListener(OnRecyclerViewItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    public EncryptedDocumentsAdapter(Context context){
        this.context = context;
        mSelectedItemIds = new SparseBooleanArray();
    }

    public void addListOfEncryptedDoc(List<DocumentFile> list){
        this.listOfEncryptedDoc = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.encrypted_document_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {

        Glide.with(context)
                .load(listOfEncryptedDoc.get(position).getIcon())
                .into(holder.icon);

        holder.fileName.setText(listOfEncryptedDoc.get(position).getName());

        holder.relativeLayout.
                setBackgroundColor(
                        mSelectedItemIds.get(position) ? Color.LTGRAY
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
                        if(longClickListener != null)
                            longClickListener.onLongItemClick(v, position);
                        return true;
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return listOfEncryptedDoc.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout relativeLayout;
        private ImageView icon;
        private TextView fileName;

        public MyViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relative_layout);
            icon = itemView.findViewById(R.id.list_icon);
            fileName = itemView.findViewById(R.id.list_item);
        }
    }

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
