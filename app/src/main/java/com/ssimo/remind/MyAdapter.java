package com.ssimo.remind;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

//Recycle view
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDatasetTexts;
    private ArrayList<String> mDatasetImages;
    private Context mContext;

    MyAdapter(Context mContext, ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetImages) {
        this.mDatasetTexts = mDatasetTexts;
        this.mDatasetImages = mDatasetImages;
        this.mContext = mContext;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView textView;
        ImageView imgView;
        RelativeLayout parentLayout;
        MyViewHolder(View v) {
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
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout_notes, parent, false);

        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // I'm using holder.getAdapterPosition() instead of position,
        // need to check if it works with new notes and notes re-arrangement

        Log.d("mydebug","onBindViewHolder: called");

        //set the text
        holder.textView.setText(mDatasetTexts.get(holder.getAdapterPosition()));
        holder.textView.setTransitionName(String.valueOf(holder.getAdapterPosition()));

        //set the image (load from url)
        Glide.with(mContext)
                .asBitmap()
                .load(mDatasetImages.get(position))
                .into(holder.imgView);

        //set the click listener for the item
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change bottom nav bar icons
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.ChangeBotBar();
                //mainActivity.performTransition(holder.getAdapterPosition());

                //change fragment
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Calendar c = new Calendar();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, c).addToBackStack(null).commit();
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetTexts.size();
    }
}