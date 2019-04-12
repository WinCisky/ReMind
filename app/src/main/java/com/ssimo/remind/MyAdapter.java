package com.ssimo.remind;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDatasetTexts = new ArrayList<String>();
    private ArrayList<String> mDatasetImages = new ArrayList<String>();
    private Context mContext;

    public MyAdapter(Context mContext, ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetImages) {
        this.mDatasetTexts = mDatasetTexts;
        this.mDatasetImages = mDatasetImages;
        this.mContext = mContext;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public ImageView imgView;
        public RelativeLayout parentLayout;
        public MyViewHolder(View v) {
            super(v);
            imgView = v.findViewById(R.id.image_list);
            textView = v.findViewById(R.id.list_text);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetImages) {
        this.mDatasetTexts = mDatasetTexts;
        this.mDatasetImages = mDatasetImages;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout_notes, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        Log.d("mydebug","onBindViewHolder: called");

        //set the text
        holder.textView.setText(mDatasetTexts.get(position));

        //set the image (load from url)
        Glide.with(mContext)
                .asBitmap()
                .load(mDatasetImages.get(position))
                .into(holder.imgView);

        //set the click listener for the item
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetTexts.size();
    }
}