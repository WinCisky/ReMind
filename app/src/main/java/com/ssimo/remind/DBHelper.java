package com.ssimo.remind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NOTES = "notes";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_NOTE_TITLE = "title";
    static final String COLUMN_NOTE_DESCRIPTION = "description";
    static final String COLUMN_CLASS = "class";
    static final String COLUMN_PRIORITY = "priority";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_STATUS = "status";

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES + "( " + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_STATUS + " integer not null, "
            + COLUMN_NOTE_TITLE + " text not null, "
            + COLUMN_NOTE_DESCRIPTION + " text not null, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_CLASS + " integer not null, "
            + COLUMN_PRIORITY + " integer not null);";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    long insertNewNote(int id, String title, String description, String date, int priority, int className, int status) {
        //before inserting I need to check if there's already a note with this id
        if(!existNote(id)){
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_STATUS, status);
            cv.put(COLUMN_NOTE_TITLE, title);
            cv.put(COLUMN_NOTE_DESCRIPTION, description);
            cv.put(COLUMN_DATE, date);
            cv.put(COLUMN_PRIORITY, priority);
            cv.put(COLUMN_CLASS, className);

            return getWritableDatabase().insert(TABLE_NOTES, null, cv);
        }else{
            //update the note
            return UpdateNote(id, title, description, date, priority, className, status);
        }

    }

    Cursor getNotes() {
        return getWritableDatabase().query(TABLE_NOTES, null, null, null, null, null, null);
    }

    Cursor getOneNote(int _id) {
        return getWritableDatabase().query(TABLE_NOTES, null, COLUMN_ID + "=?", new String[] { String.valueOf(_id) }, null, null, null);
    }

    private boolean existNote(int _id) {
        Cursor c = getWritableDatabase().query(TABLE_NOTES, new String[] {COLUMN_ID},COLUMN_ID + "=?", new String[] { String.valueOf(_id) },null,null,null);
        if (c.getCount() <= 0)
            return false;
        c.close();
        return true;
    }

    private int UpdateNote(int _id, String title, String description, String date, int priority, int className, int status){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        cv.put(COLUMN_NOTE_TITLE, title);
        cv.put(COLUMN_NOTE_DESCRIPTION, description);
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_PRIORITY, priority);
        cv.put(COLUMN_CLASS, className);
        return getWritableDatabase().update(TABLE_NOTES, cv, COLUMN_ID + "=?", new String[]{String.valueOf(_id)});
    }

    /*
    public long insertNewStudent(String firstName, String lastName, String className, int grade) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOTE_TITLE, firstName);
        cv.put(COLUMN_NOTE_DESCRIPTION, lastName);
        cv.put(COLUMN_CLASS, className);
        cv.put(COLUMN_PRIORITY, grade);

        long code = getWritableDatabase().insert(TABLE_NOTES, null, cv);
        return code;
    }

    public Cursor getGrades() {
        return getWritableDatabase().query(TABLE_NOTES, null, null, null, null, null, null);
    }

    public void deleteStudent(int id) {
        getWritableDatabase().delete(TABLE_NOTES, COLUMN_ID + "=?", new String[] { String.valueOf(id) });
    }

    public Cursor get30() {
        return getWritableDatabase().query(TABLE_NOTES, null, COLUMN_PRIORITY + "=?", new String[] { "30" }, null, null, null);
    }
    */
}