package com.ssimo.remind;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.behavior.HideBottomViewOnScrollBehavior;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Objects;

interface OnKeyboardVisibilityListener {
    void onVisibilityChanged(boolean visible);
}

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener ,
        OnKeyboardVisibilityListener,
        View.OnClickListener,
        FragmentManager.OnBackStackChangedListener{

    //fab
    private static FloatingActionButton fab;
    //db
    private static DBHelper dbh;
    //bottom action bar
    private static Toolbar bab;
    //fragment manager
    private static FragmentManager fm;
    //preference editor
    private static SharedPreferences notePrefs;


    Toolbar toolbarTop, toolbarBot;
    DrawerLayout drawer;
    NavigationView navigationView;
    ImageButton back;
    CoordinatorLayout.LayoutParams layoutParams;
    CoordinatorLayout.Behavior behavior;
    Drawable defaultNavigationIcon;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //DB initialization (needs to be set before invocation ;)
        dbh = new DBHelper(this);

        //set the main activity to lunch on startup
        setContentView(R.layout.activity_main);

        //top bar
        toolbarTop = findViewById(R.id.toolbar);
        //toolbarTop.setTitle("Tasks");
        toolbarTop.setTitleMarginStart((toolbarTop.getTitleMarginEnd()+toolbarTop.getTitleMarginStart()) /2);

        //back button on toolbar top
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        //bot bar
        toolbarBot = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(toolbarBot);
        actionBar = getSupportActionBar();
        bab = toolbarBot;

        //get original bot bar behaviour
        layoutParams = (CoordinatorLayout.LayoutParams) toolbarBot.getLayoutParams();
        behavior = layoutParams.getBehavior();

        //Floating Action Button
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);


        //Left menu
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarBot, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //check for keyboard visibility
        setKeyboardVisibilityListener(this);

        //back stack listener
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        fm = getSupportFragmentManager();

        notePrefs = getSharedPreferences("myPrefs",MODE_PRIVATE);
    }

    //gives the db helper instance
    public static DBHelper getDBHelper() {
        return dbh;
    }
    //gives the fab instance
    public static FloatingActionButton getFab() {
        return fab;
    }
    //gives the bottom app bar instance
    public static Toolbar getActBar() {
        return bab;
    }
    //gives the fragment manager instance
    public static FragmentManager getFragManager() {
        return fm;
    }
    //edits a shared pref value
    public static void setNotePrefs(String key, String value){ //string
        notePrefs.edit().putString(key, value).apply();
    }
    public static void setNotePrefs(String key, int value){ //int
        notePrefs.edit().putInt(key, value).apply();
    }
    //gets a shared pref value
    public static String getNotePrefs(String key, String defaultValue){ //string
        return notePrefs.getString(key, defaultValue);
    }
    public static int getNotePrefs(String key, int defaultValue){ //int
        return notePrefs.getInt(key, defaultValue);
    }

    //Click listener (just used by fab
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab:
                fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onShown(fab);
                    }

                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        super.onHidden(fab);

                        ShowBottomAppBar(true);
                        NewFragment(new NoteEditor());
                        ChangeBar(1);

                        fab.show();
                    }
                });
                break;
            case R.id.back:
                getSupportFragmentManager().popBackStack();
                break;
        }
    }

    //the backstack has changed
    @Override
    public void onBackStackChanged() {
        int backCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backCount == 0){
            //set the bottom bar ast the original one
            ChangeBotBar(0);
            //hide back button
            back.setVisibility(View.GONE);
        }
    }

    //Hide bottom app bar when the user is writing and show when the user is no longer writing
    @Override
    public void onVisibilityChanged(boolean visible) {

        if(visible){
            if(toolbarBot != null && actionBar != null){
                ShowBottomAppBar(false);
            }
            if(fab != null)
                fab.hide();
        }else{
            if(toolbarBot != null && actionBar != null){
                ShowBottomAppBar(true);
            }
            if(fab != null)
                fab.show();
        }
    }

    //Show or hide the bottom app bar
    public void ShowBottomAppBar(boolean show){
        //copy the original behaviour
        CoordinatorLayout.Behavior b = layoutParams.getBehavior();
        //custom behaviour to show/hide the bottom bar
        layoutParams.setBehavior(new CustomHideBottomViewOnScrollBehavior<>());
        if(show){
            ((CustomHideBottomViewOnScrollBehavior) Objects.requireNonNull(layoutParams.getBehavior())).slideUp(findViewById(R.id.bottom_app_bar));
            actionBar.show();
        }else{
            ((CustomHideBottomViewOnScrollBehavior) Objects.requireNonNull(layoutParams.getBehavior())).slideDown(findViewById(R.id.bottom_app_bar));
            actionBar.hide();
        }
        //set behaviour back to normal
        layoutParams.setBehavior(b);
    }


    //Creates a listener for the keyboard input (checks if the screen is occupated by the amount required by the keyboard)
    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }


    //New note, no need to add Bundle info
    public void NewFragment(Fragment f){
        android.support.v4.app.FragmentManager man = getSupportFragmentManager();
        FragmentTransaction transaction = man.beginTransaction();
        //NoteEditor noteEditor = new NoteEditor();
        transaction.replace(R.id.fragment, f);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    //Change the bot bar icons to edit mode
    public void ChangeBar(int type){
        ShowBottomAppBar(true);
        switch (type){
            case 1: //Note editor
                if(defaultNavigationIcon == null)
                    //get default navigation icon
                    defaultNavigationIcon = toolbarBot.getNavigationIcon();
                // Hide navigation drawer icon
                toolbarBot.setNavigationIcon(null);
                // Move FAB from the center of BottomAppBar to the end of it
                ((BottomAppBar)toolbarBot).setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                // Replace the action menu
                ((BottomAppBar)toolbarBot).replaceMenu(R.menu.main);
                // Change FAB icon
                fab.setImageResource(R.drawable.ic_save_white_24dp);
                //show back button (it was gone)
                back.setVisibility(View.VISIBLE);
                break;
            case 2: //CalendarFragment
                break;
            default: //Basic
                fab.setOnClickListener(this);
                toolbarBot.setNavigationIcon(defaultNavigationIcon);
                ((BottomAppBar)toolbarBot).setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                ((BottomAppBar)toolbarBot).replaceMenu(R.menu.bottom_bar_right);
                fab.setImageResource(R.drawable.ic_add_white_24dp);
                back.setVisibility(View.GONE);
                //Apparently the menu bar disappear if I don't "refresh" it (android bug?)
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawer, toolbarBot, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.syncState();
                break;
        }
    }

    //Changes the bot bar to edit mode
    public void ChangeBotBar(final int type){
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onShown(FloatingActionButton fab) {
                super.onShown(fab);
            }

            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                //ShowBottomAppBar(showBotAppBar);
                layoutParams.setBehavior(new HideBottomViewOnScrollBehavior());
                ChangeBar(type);
                fab.show();
            }
        });
    }

    //intercepts the back press and closes the drawer if it's open
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //inflates the bot menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_bar_right, menu);

        return true;
    }

    //listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_priority:
                SharedPreferences sharedPref1 = getPreferences(Context.MODE_PRIVATE);
                final boolean[] values1 = { true, true, true};
                for (int i = 0; i < 3; i++){
                    values1[i] = sharedPref1.getBoolean("selected_priority"+i, true);
                }
                final CharSequence[] items1 = {"High", "Normal", "Low"};

                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Select priorities");
                builder1.setMultiChoiceItems(items1, values1, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if(isChecked)
                            editor.putBoolean("selected_priority" + which, true);
                        else
                            editor.putBoolean("selected_priority" + which, false);
                        editor.commit();
                    }
                });
                AlertDialog alert1 = builder1.create();
                alert1.show();
                return true;
            case R.id.action_class:
                //get classes from db
                DBHelper dbhInstance = getDBHelper();
                Cursor cursor = dbhInstance.getClasses();
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
                cursor.close();
                SharedPreferences sharedPref2 = getPreferences(Context.MODE_PRIVATE);
                final boolean[] values2 = new boolean[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++){
                    values2[i] = sharedPref2.getBoolean("selected_class"+classesID[i], true);
                }
                //final CharSequence[] items2 = classes;

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Select classes to show");
                builder2.setMultiChoiceItems(classes, values2, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        if(isChecked)
                            editor.putBoolean("selected_class" + classesID[which], true);
                        else
                            editor.putBoolean("selected_class" + classesID[which], false);
                        editor.commit();
                    }
                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
                break;
            case R.id.action_sort:
                SharedPreferences sharedPref3 = getPreferences(Context.MODE_PRIVATE);
                int sel_val = sharedPref3.getInt("selected_sorting", 2);
                final CharSequence[] items3 = {"Priority", "Date", "Insertion Order"};

                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setTitle("Select sorting");
                builder3.setSingleChoiceItems(items3, sel_val, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), items3[item],
                                Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("selected_sorting", item);
                        editor.commit();
                    }
                });
                AlertDialog alert3 = builder3.create();
                alert3.show();
                break;
            default:
                Log.d("ERROR", String.valueOf(item.getItemId()));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Left menu (the hidden one)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_calendar:
                //go to calendar fragment
                NewFragment(new CalendarFragment());
                break;
            case  R.id.nav_graphs:
                //go to graphs fragment
                break;
            case R.id.nav_settings:
                //go to settings fragment
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
