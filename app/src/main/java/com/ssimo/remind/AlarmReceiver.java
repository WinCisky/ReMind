package com.ssimo.remind;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {


    public static void StartAction(int id, DBHelper dbh){
        Cursor c = dbh.getOneNote(id);
        if(c.getCount() == 0)
            return;
        c.moveToFirst();
        String db_title = c.getString(c.getColumnIndex(DBHelper.COLUMN_NOTE_TITLE));
        String db_description = c.getString(c.getColumnIndex(DBHelper.COLUMN_NOTE_DESCRIPTION));
        String db_date = c.getString(c.getColumnIndex(DBHelper.COLUMN_DATE));
        int db_priority = c.getInt(c.getColumnIndex(DBHelper.COLUMN_PRIORITY));
        int db_classname = c.getInt(c.getColumnIndex(DBHelper.COLUMN_CLASS));
        dbh.UpdateNote(id, db_title, db_description, db_date, db_priority, db_classname, 1);
    }

    static void EndAction(int id, DBHelper dbh){
        Cursor c = dbh.getOneNote(id);
        if(c.getCount() == 0)
            return;
        c.moveToFirst();
        String db_title = c.getString(c.getColumnIndex(DBHelper.COLUMN_NOTE_TITLE));
        String db_description = c.getString(c.getColumnIndex(DBHelper.COLUMN_NOTE_DESCRIPTION));
        String db_date = c.getString(c.getColumnIndex(DBHelper.COLUMN_DATE));
        int db_priority = c.getInt(c.getColumnIndex(DBHelper.COLUMN_PRIORITY));
        int db_classname = c.getInt(c.getColumnIndex(DBHelper.COLUMN_CLASS));
        dbh.UpdateNote(id, db_title, db_description, db_date, db_priority, db_classname, 2);
    }

    static void PostponeAction(int id, DBHelper dbh){
        Cursor c = dbh.getOneNote(id);
        if(c.getCount() == 0)
            return;
        c.moveToFirst();
        String db_title = c.getString(c.getColumnIndex(DBHelper.COLUMN_NOTE_TITLE));
        String db_description = c.getString(c.getColumnIndex(DBHelper.COLUMN_NOTE_DESCRIPTION));
        String db_date = c.getString(c.getColumnIndex(DBHelper.COLUMN_DATE));
        int db_priority = c.getInt(c.getColumnIndex(DBHelper.COLUMN_PRIORITY));
        int db_classname = c.getInt(c.getColumnIndex(DBHelper.COLUMN_CLASS));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ITALY);
        Date designedDate = Calendar.getInstance().getTime();
        try {
            designedDate = df.parse(db_date);
        } catch (ParseException e) {
            Log.e("TEST ERROR", "parse was wrong");
        }
        String dateString = df.format(new Date(designedDate.getTime() + 86400000));
        dbh.UpdateNote(id, db_title, db_description, dateString, db_priority, db_classname, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("methodName");
        if(action != null && action.equals("start")){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getIntExtra("id", 0);
            mNotificationManager.cancel(id);
            DBHelper dbh = new DBHelper(context);
            StartAction(id, dbh);
        }else if(action!= null && action.equals("end")){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getIntExtra("id", 0);
            mNotificationManager.cancel(id);
            DBHelper dbh = new DBHelper(context);
            EndAction(id, dbh);
        }else if(action!= null && action.equals("postpone")){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getIntExtra("id", 0);
            mNotificationManager.cancel(id);
            DBHelper dbh = new DBHelper(context);
            PostponeAction(id, dbh);
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("title", intent.getStringExtra("title"));
            alarmIntent.putExtra("id", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 86400000, pendingIntent);
        }else{
            Intent startIntent = new Intent(context, AlarmReceiver.class);
            startIntent.putExtra("methodName", "start");
            startIntent.putExtra("id", intent.getIntExtra("id", 1));
            PendingIntent startPendingIntent =
                    PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent endIntent = new Intent(context, AlarmReceiver.class);
            endIntent.putExtra("methodName", "end");
            endIntent.putExtra("id", intent.getIntExtra("id", 1));
            PendingIntent endPendingIntent =
                    PendingIntent.getBroadcast(context, 1, endIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent postponeIntent = new Intent(context, AlarmReceiver.class);
            postponeIntent.putExtra("methodName", "postpone");
            postponeIntent.putExtra("title", intent.getStringExtra("title"));
            postponeIntent.putExtra("id", intent.getIntExtra("id", 1));
            PendingIntent postponePendingIntent =
                    PendingIntent.getBroadcast(context, 2, postponeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar now = GregorianCalendar.getInstance();
            //int dayOfWeek = now.get(Calendar.DATE);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentTitle(intent.getStringExtra("title"))
                            .setContentText("Time's up!")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                            .addAction(R.drawable.ic_date_range_white_24dp, "Start",
                                    startPendingIntent)
                            .addAction(R.drawable.ic_date_range_white_24dp, "End",
                                    endPendingIntent)
                            .addAction(R.drawable.ic_date_range_white_24dp, "Postpone",
                                    postponePendingIntent);

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setAutoCancel(true);
            Notification n = mBuilder.build();
            n.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(intent.getIntExtra("id", 1), n);
        }
    }
}