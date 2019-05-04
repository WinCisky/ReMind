package com.ssimo.remind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoteEditor extends Fragment {

    View v;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_note_desc, container, false);
        Bundle bundle = this.getArguments();

        int noteid = -1;
        if (bundle != null) {
            noteid = bundle.getInt("ID", -1);
        }
        if(noteid == -1){
            //New note
            Log.d("TEST", "Creating a new note");
        }else{
            Log.d("TEST", "Editing a note");
            //TODO: need to retrieve info from DB
        }
        return v;
    }

}