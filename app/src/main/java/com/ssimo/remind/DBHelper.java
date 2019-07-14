package com.ssimo.remind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TABLE_NOTES = "notes";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_NOTE_TITLE = "title";
    static final String COLUMN_NOTE_DESCRIPTION = "description";
    static final String COLUMN_CLASS = "class";
    static final String COLUMN_PRIORITY = "priority";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_STATUS = "status";

    private static final String TABLE_CLASSES = "classes";
    static final String COLUMN_CLASSES_ID = "_id";
    static final String COLUMN_CLASSES_NAME = "name";

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

    private static final String CLASSES_CREATE = "create table "
            + TABLE_CLASSES + "( " + COLUMN_CLASSES_ID + " integer primary key autoincrement, "
            + COLUMN_CLASSES_NAME + " text not null);";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(CLASSES_CREATE); //creates classes
        //adds default classes
        insertDefaultClasses(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    void insertDefaultClasses(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CLASSES_NAME, "Home");
        db.insert(TABLE_CLASSES,null, cv);
        cv = new ContentValues();
        cv.put(COLUMN_CLASSES_NAME, "Personal");
        db.insert(TABLE_CLASSES,null, cv);
        cv = new ContentValues();
        cv.put(COLUMN_CLASSES_NAME, "University");
        db.insert(TABLE_CLASSES,null, cv);
        cv = new ContentValues();
        cv.put(COLUMN_CLASSES_NAME, "Job");
        db.insert(TABLE_CLASSES,null, cv);
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

    Cursor getClasses() {
        return getWritableDatabase().query(TABLE_CLASSES, null, null, null, null, null, null);
    }

    private String[] classesNames(){
        Cursor cursor = getClasses();
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
        return classes;
    }

    Cursor getNotes(int sorting, boolean[] priority, boolean[] classes, int[] classesIDs) {


        //sorting -> 0 : order by priority
        //sorting -> 1 : order by date
        //sorting -> 2 : do nothing
        String selection = "";
        for (int i = 0; i < classes.length; i++) {
            if(classes[i]){
                if(i>0)
                    selection += " AND ";
                selection += COLUMN_CLASS + "=" + classesIDs[i];
            }
        }
        selection += " AND " + COLUMN_CLASS + "=0";
        if(sorting == 0){
            return getWritableDatabase().query(TABLE_NOTES, null, selection, null, null, null, COLUMN_PRIORITY + " DESC");
        }else if(sorting == 1){
            return getWritableDatabase().query(TABLE_NOTES, null, selection, null, null, null, COLUMN_DATE);
        }else {
            return getWritableDatabase().query(TABLE_NOTES, null, selection, null, null, null, null);
        }
    }

    //TODO: test
    Cursor getNotes(int[] priority, int sorting, int[] classes) {
        if(priority.length>0){

            String selectionColumn = COLUMN_PRIORITY + "=?";
            String[] selectionArgs = new String[classes.length + priority.length];

            selectionArgs[0] = String.valueOf(priority);

            for (int i = 1; i < classes.length; i++) {
                selectionColumn +=  " AND " + COLUMN_PRIORITY + "=?";
                selectionArgs[i] = String.valueOf(classes[i]);
            }

            for (int i = 0; i < classes.length; i++) {
                selectionColumn +=  " AND " + COLUMN_CLASS + "=?";
                selectionArgs[i + priority.length] = String.valueOf(classes[i]);
            }
            return getWritableDatabase().query(TABLE_NOTES, null,  selectionColumn, selectionArgs, null, null, null);
        }
        return null; //attention could return null in case of error!
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