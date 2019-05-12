package com.ssimo.remind;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class Notes extends Fragment {

    View v;
    private RecyclerView myRecycleView;
    private ArrayList<String> memo_texts = new ArrayList<>();
    private ArrayList<String> memo_days_left = new ArrayList<>();
    //private ArrayList<String> memo_images = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notes, container, false);
        myRecycleView = v.findViewById(R.id.my_recycler_view);

        //TODO: read info from local DB

        DBHelper dbhInstance = MainActivity.getDBHelper();
        Cursor cursor = dbhInstance.getNotes();
        cursor.moveToFirst();
        int instances = 0;
        while (!cursor.isAfterLast()) {
            instances++;
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOTE_TITLE));
            String description = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOTE_DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE));
            int className = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CLASS));
            int priority = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_PRIORITY));
            int status = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_STATUS));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TEST", "There are " + instances + " instances");


        for(int i = 0; i <= 10; i++){
            //memo_texts.add("memo text value is : " + i);
            memo_days_left.add(String.valueOf((int)(Math.random() * 10) + 1)); //some random numbers
            //memo_images.add("https://picsum.photos/200/200/?image="+i);
        }
        //some notes
        memo_texts.add("Clean room");
        memo_texts.add("Throw garbage");
        memo_texts.add("Talk to mr. James");
        memo_texts.add("Study for math exam");
        memo_texts.add("Do english homework");
        memo_texts.add("Complete android project");
        memo_texts.add("Go swimming");
        memo_texts.add("Call mom");
        memo_texts.add("New marvel movie!");
        memo_texts.add("Shopping list");
        memo_texts.add("Move to NJ");

        MyAdapter mAdapter = new MyAdapter(getContext(), memo_texts, memo_days_left);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycleView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
