package com.ssimo.remind;

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


    Toolbar toolbarTop, toolbarBot;
    DrawerLayout drawer;
    NavigationView navigationView;
    ActionBar actionBar;
    ImageButton back;
    CoordinatorLayout.LayoutParams layoutParams;
    CoordinatorLayout.Behavior behavior;
    Drawable defaultNavigationIcon;

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
    }

    //gives the db helper instance
    public static DBHelper getDBHelper() {
        return dbh;
    }
    //gives the db helper instance
    public static FloatingActionButton getFab() {
        return fab;
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
            //TODO: I need to update the recycleview if there has been changes
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

    //TODO: bottom app bar isn't showing if it was hidden
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
                return true;
            default:
                Log.d("TEST", String.valueOf(item.getItemId()));
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
