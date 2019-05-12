package com.ssimo.remind;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Objects;

public class NoteEditor extends Fragment implements View.OnClickListener {

    View v;
    TextInputEditText title, description;

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
            //TODO: need to retrieve info from DB
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            //save button clicked
            DBHelper dbh = MainActivity.getDBHelper();
            String noteTitle = null, noteDesc = null;
            if (title != null)
                try {
                    noteTitle = Objects.requireNonNull(title.getText()).toString();
                }catch (Exception e) {
                    Log.e("TEST ERROR", "Null pointer exception on title");
                }
            else
                Log.e("TEST ERROR", "null title");
            if (description != null)
                try {
                    noteDesc = Objects.requireNonNull(description.getText()).toString();
                }catch (Exception e) {
                    Log.e("TEST ERROR", "Null pointer exception on description");
                }
            else
                Log.e("TEST ERROR", "null description");

            //TODO: to get other required info from shared pref
            //int grade = Integer.valueOf(((EditText)findViewById(R.id.et_grade)).getText().toString());
            int grade = 0;
            int className = 0;
            int status = 0;

            if(noteTitle!=null && noteDesc !=null) {
                long code = dbh.insertNewNote(noteTitle, noteDesc, "test", grade, className, status);
                if (code != -1)
                    Toast.makeText(v.getContext(), "Inserimento effettuato", Toast.LENGTH_LONG).show();
            }

            //TODO: check for recycle view refresh
            //go back to main activity (and hopefully refresh recycle view automatically)
            Intent i = new Intent(v.getContext(), MainActivity.class);
            startActivity(i);
            //TODO: need to clear back stack before launching intent
        }
    }
}