package com.ssimo.remind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NoteEditor extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    View v;
    TextInputEditText title, description;
    int myId = -1;

    //Variables
    int grade = 0;
    int className = 0;
    int status = 0;
    String date = "";

    //public setter
    public void setGrade(int grade) {
        this.grade = grade;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setClassName(int className) {
        this.className = className;
    }
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_note_desc, container, false);
        Bundle bundle = this.getArguments();

        MainActivity.getFab().setOnClickListener(this);
        title = v.findViewById(R.id.note_title);
        description = v.findViewById(R.id.note_description);

        MainActivity.getActBar().setOnMenuItemClickListener(this);

        int noteid = -1;
        if (bundle != null) {
            noteid = bundle.getInt("ID", -1);
        }
        if (noteid == -1) {
            //New note
            Log.d("TEST", "Creating a new note");
        } else {
            Log.d("TEST", "Editing a note");
            myId = noteid;

            DBHelper dbh = MainActivity.getDBHelper();
            Cursor cursor = dbh.getOneNote(noteid);
            cursor.moveToFirst();

            //get the last note (should only be 1 tho)
            String db_title = "";
            String db_description = "";
            while (!cursor.isAfterLast()) {
                db_title = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOTE_TITLE));
                db_description = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NOTE_DESCRIPTION));
                cursor.moveToNext();
            }
            cursor.close();

            //set title and description if necessary
            if(!db_title.equals("")){
                title.setText(db_title);
            }
            if(!db_description.equals("")){
                description.setText(db_description);
            }
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            //save button clicked
            DBHelper dbh = MainActivity.getDBHelper();
            boolean hasTitle = false;
            String noteTitle, noteDesc;
            if (title != null && Objects.requireNonNull(title.getText()).toString().compareTo("") != 0)
                try {
                    noteTitle = Objects.requireNonNull(title.getText()).toString();
                    hasTitle = true;
                }catch (Exception e) {
                    noteTitle = "";
                }
            else
                noteTitle = "";

            if (description != null && Objects.requireNonNull(description.getText()).toString().compareTo("") != 0)
                try {
                    noteDesc = Objects.requireNonNull(description.getText()).toString();
                }catch (Exception e) {
                    noteDesc = "";
                }
            else
                noteDesc = "";

            //if the note has at least the title the save it
            if(hasTitle){
                //int grade = Integer.valueOf(((EditText)findViewById(R.id.et_grade)).getText().toString());

                //get today value
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ITALY);
                String strDate = dateFormat.format(today);

                //TODO: use settings defined default values as default instead of developer defined values

                date = MainActivity.getNotePrefs("date", strDate);

                grade = MainActivity.getNotePrefs("priority", 1);

                className = MainActivity.getNotePrefs("class", 0);

                status = 0;

                long code = dbh.insertNewNote(myId, noteTitle, noteDesc, date, grade, className, status);
                if (code != -1)
                    //Toast.makeText(v.getContext(), "Inserimento effettuato", Toast.LENGTH_LONG).show();
                    Log.d("TEST","Inserimento effettuato");
            }


            //go back to main activity and refresh recycle view
            Intent i = new Intent(v.getContext(), MainActivity.class);
            //non back stack of activity
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //start new activity
            startActivity(i);
        }
        //else if(v.getId() == R.id.)
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.app_bar_calendar:
                //showTimePickerDialog(getView());
                showDatePickerDialog(getView());
                break;
            case R.id.app_bar_priority:
                onCreatePriorityDialog().show();
                break;
            case R.id.app_bar_class:
                onCreateClassDialog().show();
                break;

        }
        return true;
    }


    public Dialog onCreatePriorityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set note priority")
                .setItems(R.array.priority_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        MainActivity.setNotePrefs("priority", which); //0-> low, 1-> normal, 2-> high
                        Toast.makeText(getContext(), getResources().obtainTypedArray(R.array.priority_array).getText(which), Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }

    public Dialog onCreateClassDialog() {
        //TODO: obtain classes from db and change R.array.class_array
        //get classes from db
        DBHelper dbhInstance = MainActivity.getDBHelper();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set note class")
                .setItems(R.array.class_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        MainActivity.setNotePrefs("class", classesID[which]); //0-> home, 1-> personal, 2-> university, 3-> job
                        Toast.makeText(getContext(), classes[which], Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(MainActivity.getFragManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(MainActivity.getFragManager(), "datePicker");
    }
}