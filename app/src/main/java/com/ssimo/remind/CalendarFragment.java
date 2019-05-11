package com.ssimo.remind;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class CalendarFragment extends Fragment {

    View v;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_calendar, container, false);

        //add items to calendar
        //CalendarView cal = v.findViewById(R.id.calendar);
        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        //calendar.set(2019,4,13);
        calendar.add(Calendar.DATE, 1); //tomorrow
        events.add(new EventDay(calendar, R.drawable.ic_note_description_black_24dp));

        com.applandeo.materialcalendarview.CalendarView calendarView = v.findViewById(R.id.calendar_view);
        calendarView.showCurrentMonthPage();
        calendarView.setEvents(events);

        return v;
    }

}
