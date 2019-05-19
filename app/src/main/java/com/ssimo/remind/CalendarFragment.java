package com.ssimo.remind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.EventDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    View v;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_calendar, container, false);

        //TODO: check befor adding another calendar fragment to the stack
        //TODO: add items to calendar
        //TODO: add calendar functionality (as tap on day to look at notes or create a new note, ...)
        //CalendarView cal = v.findViewById(R.id.calendar);
        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ITALY);
        String strDate = dateFormat.format(date);
        //Toast.makeText(v.getContext(), strDate, Toast.LENGTH_SHORT).show(); //show date (works fine)

        //calendar.set(2019,4,13);
        calendar.add(Calendar.DATE, 1); //tomorrow
        events.add(new EventDay(calendar, R.drawable.ic_note_description_black_24dp));

        com.applandeo.materialcalendarview.CalendarView calendarView = v.findViewById(R.id.calendar_view);
        calendarView.showCurrentMonthPage();
        calendarView.setEvents(events);

        return v;
    }

}
