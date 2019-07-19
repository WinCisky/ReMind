package com.ssimo.remind;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

//Recycle view
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<String> mDatasetTexts;
    private ArrayList<String> mDatasetDaysLeft;
    //private ArrayList<String> mDatasetImages;
    private ArrayList<Integer> mDatasetId;
    private Context mContext;

    MyAdapter(Context mContext, ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetDaysLeft, ArrayList<Integer> mDatasetId) {
        this.mDatasetTexts = mDatasetTexts;
        this.mDatasetDaysLeft = mDatasetDaysLeft;
        //this.mDatasetImages = mDatasetImages;
        this.mContext = mContext;
        this.mDatasetId = mDatasetId;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        int noteID;
        TextView noteTitle, noteDaysLeft;
        RelativeLayout parentLayout;
        MyViewHolder(View v) {
            super(v);
            noteDaysLeft = v.findViewById(R.id.remaining_days);
            noteTitle = v.findViewById(R.id.list_text);
            parentLayout = v.findViewById(R.id.parent_layout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetDaysLeft, ArrayList<Integer> mDatasetId) {
        this.mDatasetTexts = mDatasetTexts;
        this.mDatasetDaysLeft = mDatasetDaysLeft;
        //this.mDatasetImages = mDatasetImages;
        this.mDatasetId = mDatasetId;
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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // I'm using holder.getAdapterPosition() instead of position,
        // need to check if it works with new notes and notes re-arrangement

        Log.d("TEST","onBindViewHolder: called");

        //set the text
        holder.noteTitle.setText(mDatasetTexts.get(holder.getAdapterPosition()));

        holder.noteID = mDatasetId.get(holder.getAdapterPosition());

        //set the image (load from url)
        //Glide.with(mContext)
        //        .asBitmap()
        //        .load(mDatasetImages.get(position))
        //        .into(holder.imgView);

        //int value = (int)(Math.random()*10) + 1;
        int value = Integer.parseInt(mDatasetDaysLeft.get((holder.getAdapterPosition())));
        String text_part_one = "<br/><small><small><small><small>day";
        String text_part_two =  " left</small></small></small></small>";
        if(value != 1)
            text_part_one += "s";
        holder.noteDaysLeft.setText(Html.fromHtml(value + text_part_one + text_part_two));

        //set the click listener for the item
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change bottom nav bar icons
                MainActivity mainActivity = (MainActivity) mContext;
                mainActivity.ChangeBotBar(1);

                //change fragment
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                NoteEditor noteEditor = new NoteEditor();
                Bundle bundle = new Bundle();
                bundle.putInt("ID", mDatasetId.get(holder.getAdapterPosition()));
                noteEditor.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, noteEditor).addToBackStack(null).commit();
            }
        });

        //Create the dialog
        final Dialog myDialog = new Dialog(mContext);
        myDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.quick_note_edit);
        TextView quick_title = myDialog.findViewById(R.id.reminder_title);
        quick_title.setText(holder.noteTitle.getText());

        Button tomorrow = myDialog.findViewById(R.id.reminder_tomorrow);
        tomorrow.setOnClickListener(OnDialogClick(myDialog.findViewById(R.id.reminder_tomorrow), myDialog, holder.noteID));
        Button delete = myDialog.findViewById(R.id.reminder_delete);
        delete.setOnClickListener(OnDialogClick(myDialog.findViewById(R.id.reminder_delete), myDialog, holder.noteID));
        Button completed = myDialog.findViewById(R.id.reminder_completed);
        completed.setOnClickListener(OnDialogClick(myDialog.findViewById(R.id.reminder_completed), myDialog, holder.noteID));

        //on long click quick edit the note
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //show the dialog
                Objects.requireNonNull(myDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();
                return false;
            }
        });

    }

    private void ReloadActivityWithoutAnimations(Context context){
        //go back to main activity and refresh recycle view
        Intent i = new Intent(context, MainActivity.class);
        //non back stack of activity
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //start new activity
        context.startActivity(i);
        Activity activity = getActivity(context);
        if (activity != null) {
            activity.overridePendingTransition(0,0);
        }else{
            Log.d("CONTEXT", context.getClass().toString());
        }
    }

    private Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private View.OnClickListener OnDialogClick(View v, final Dialog dialog, final int id) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    //TODO: add logic
                    case R.id.reminder_tomorrow:
                        Toast.makeText(v.getContext(), "tomorrow", Toast.LENGTH_SHORT).show();
                        Log.d("ID:", ((String.valueOf(v.getId()))));
                        AlarmReceiver.PostponeAction(id, new DBHelper(v.getContext()));
                        ReloadActivityWithoutAnimations(v.getContext());

                        break;
                    case R.id.reminder_delete:
                        Toast.makeText(v.getContext(), "delete", Toast.LENGTH_SHORT).show();
                        AlarmReceiver.EndAction(id, new DBHelper(v.getContext()));
                        ReloadActivityWithoutAnimations(v.getContext());
                        break;
                    case R.id.reminder_completed:
                        Toast.makeText(v.getContext(), "completed", Toast.LENGTH_SHORT).show();//TODO: if ongiong, or start if not started
                        AlarmReceiver.StartAction(id, new DBHelper(v.getContext()));
                        ReloadActivityWithoutAnimations(v.getContext());
                        break;
                }
                dialog.dismiss(); //closes dialog
            }
        };
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDatasetTexts.size();
    }
}