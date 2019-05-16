package com.ssimo.remind;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

public class NoteEditor extends Fragment implements View.OnClickListener {

    View v;
    TextInputEditText title, description;
    int myId = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_note_desc, container, false);
        Bundle bundle = this.getArguments();

        MainActivity.getFab().setOnClickListener(this);
        title = v.findViewById(R.id.note_title);
        description = v.findViewById(R.id.note_description);

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
                //TODO: to get other required info from shared pref
                //int grade = Integer.valueOf(((EditText)findViewById(R.id.et_grade)).getText().toString());
                int grade = 0;
                int className = 0;
                int status = 0;

                long code = dbh.insertNewNote(myId, noteTitle, noteDesc, "test", grade, className, status);
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
    }
}