package com.ssimo.remind;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class Notes extends Fragment{

    View v;
    private RecyclerView myRecycleView;
    private ArrayList<String> memo_texts = new ArrayList<>();
    private ArrayList<String> memo_days_left = new ArrayList<>();
    //private ArrayList<String> memo_images = new ArrayList<>();
    private ArrayList<Integer> memo_id = new ArrayList<>();

    private int[] GetClasses(DBHelper dbhInstance){
        Cursor cursor = dbhInstance.getClasses();
        cursor.moveToFirst();
        final String[] classes = new String[cursor.getCount()];
        final int[] classesID = new int[cursor.getCount()];
        int instances = 0;
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_CLASSES_ID));
            String className = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CLASSES_NAME));
            classes[instances] = className;
            classesID[instances] = id;
            cursor.moveToNext();
            instances++;
        }
        cursor.close();
        return classesID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_notes, container, false);
        myRecycleView = v.findViewById(R.id.my_recycler_view);


        DBHelper dbhInstance = MainActivity.getDBHelper();

        //TODO: add calendar, priority, status and class functionality
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int sorting = sharedPref.getInt("selected_sorting", -1);
        boolean[] selPriority = new boolean[]{
            sharedPref.getBoolean("selected_priority2",true),
            sharedPref.getBoolean("selected_priority1",true),
            sharedPref.getBoolean("selected_priority0",true)
        };
        int[] classesIDs = GetClasses(dbhInstance);
        boolean[] selClasses = new boolean[classesIDs.length];
        for (int i=0; i< classesIDs.length; i++){
            selClasses[i] = sharedPref.getBoolean("selected_class"+classesIDs[i],true);
        }

        Cursor cursor = dbhInstance.getNotes(sorting, selPriority, selClasses, classesIDs);
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
            memo_texts.add(title);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ITALY);
            Date designedDate = Calendar.getInstance().getTime();
            try {
                designedDate = df.parse(date);
            } catch (ParseException e) {
                Log.e("TEST ERROR", "parse was wrong");
            }
            long startTime = Calendar.getInstance().getTime().getTime();
            long endTime = designedDate.getTime();


            //Log.d("TIME","start: " + df.format(Calendar.getInstance().getTime().getTime()));
            //Log.d("TIME","end: " + df.format(endTime));

            long diffTime = endTime - startTime;
            long diffDays = diffTime / (1000 * 60 * 60 * 24);

            if(diffTime > 0) //if there's more than 1 day left add 1 day
                diffDays++;
            else if(diffTime < 0) //instead of negative numbers
                diffDays = 0;

            memo_days_left.add(String.valueOf(diffDays));
            memo_id.add(id);
            cursor.moveToNext();
        }
        cursor.close();
        //Log.d("TEST", "There are " + instances + " instances");

        MyAdapter mAdapter = new MyAdapter(getContext(), memo_texts, memo_days_left, memo_id);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myRecycleView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
