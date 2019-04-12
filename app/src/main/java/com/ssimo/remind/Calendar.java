package com.ssimo.remind;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class Calendar extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //clear view
        if (container != null) {
            container.removeAllViews();
        }
        View v = inflater.inflate(R.layout.fragment_note_desc, container, false);

        //final FloatingActionButton fab = getView().findViewById(R.id.fab);
        EditText et = v.findViewById(R.id.note_description);

        //et.setOnFocusChangeListener(new HideMe());

        // Inflate the layout for this fragment
        return v;
    }

}

class HideMe implements View.OnFocusChangeListener {

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        FloatingActionButton fab = v.getRootView().findViewById(R.id.fab);
        BottomAppBar bab = v.getRootView().findViewById(R.id.bottom_app_bar);
        if(hasFocus){

            fab.hide();
        }else {

            fab.show();
        }
    }
}
