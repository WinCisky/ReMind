package com.ssimo.remind;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Graph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        DBHelper dbh = new DBHelper(this);
        int notes = dbh.NotesAmount();

        TextView tv = (TextView) findViewById(R.id.textView2) ;
        tv.setText("You've inserted a total amount of \n " + notes + " \n notes so far");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String[] dates = new String[7];

        for (int i = 0; i< 7; i++){

            int _day = day - i;
            int _month = month;
            if(_day<0) {
                _day = 31 - _day;
                _month -=1;
                if(_month < 0){
                    _month = 12;
                }
            }

            dates[i] = year + "-";
            if(++_month < 10)
                dates[i] += "0";
            dates[i] += _month + "-";


            if(_day < 10)
                dates[i] += "0";
            dates[i] += _day + " " + "00:00:00";
        }

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(1, dbh.NotesCompleted(dates[6])),
                new DataPoint(2, dbh.NotesCompleted(dates[5])),
                new DataPoint(3, dbh.NotesCompleted(dates[4])),
                new DataPoint(4, dbh.NotesCompleted(dates[3])),
                new DataPoint(5, dbh.NotesCompleted(dates[2])),
                new DataPoint(6, dbh.NotesCompleted(dates[1])),
                new DataPoint(7, dbh.NotesCompleted(dates[0]))
        });
        graph.addSeries(series);
    }
}
