package com.ssimo.remind;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Fade;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

interface OnKeyboardVisibilityListener {
    void onVisibilityChanged(boolean visible);
}

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnKeyboardVisibilityListener{

    private ArrayList<String> memo_texts = new ArrayList<>();
    private ArrayList<String> memo_images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the main activity to lunch on startup
        setContentView(R.layout.activity_main);

        Toolbar toolbarTop = findViewById(R.id.toolbar);
        toolbarTop.setTitle("Tasks");
        toolbarTop.setTitleMarginStart((toolbarTop.getTitleMarginEnd()+toolbarTop.getTitleMarginStart()) /2);

        Toolbar toolbarBot = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(toolbarBot);




        //Floating Action Button
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onShown(FloatingActionButton fab) {
                        super.onShown(fab);
                    }

                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        super.onHidden(fab);
                        ChangeFragment();
                        //ChangeBar();
                        fab.show();
                    }
                });
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbarBot, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //left hidden menu
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        populateRecycleView();


        //check for keyboard visibility
        setKeyboardVisibilityListener(this);


    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        Toast.makeText(MainActivity.this, visible ? "Keyboard is active" : "Keyboard is Inactive", Toast.LENGTH_SHORT).show();

        ActionBar ab = getActionBar();
        FloatingActionButton fab = findViewById(R.id.fab);
        if(visible){
            if(ab!=null)
                ab.hide();
            if(fab!=null)
                fab.hide();
        }else{
            if(ab!=null)
                ab.show();
            if(fab!=null)
                fab.show();
        }
    }


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
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }



    public void ChangeFragment(){
        android.support.v4.app.FragmentManager man = getSupportFragmentManager();
        FragmentTransaction transaction = man.beginTransaction();
        Calendar c = new Calendar();
        transaction.replace(R.id.fragment, c);
        transaction.commitNow();

    }



    public void ChangeBar(){
        BottomAppBar bottom_app_bar = findViewById(R.id.bottom_app_bar);
        // Hide navigation drawer icon
        bottom_app_bar.setNavigationIcon(null);
        // Move FAB from the center of BottomAppBar to the end of it
        bottom_app_bar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
        // Replace the action menu
        bottom_app_bar.replaceMenu(R.menu.main);
        // Change FAB icon
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_reply_white_24dp));
        fab.show();
    }

    public void ChangeBotBar(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onShown(FloatingActionButton fab) {
                super.onShown(fab);
            }

            @Override
            public void onHidden(FloatingActionButton fab) {
                super.onHidden(fab);
                //ChangeFragment();
                ChangeBar();
                fab.show();
            }
        });
    }

    //perform transition animations and should set the values for the note
    private void performTransition(int position)
    {

        if (isDestroyed())
        {
            return;
        }
        Fragment previousFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        Fragment nextFragment = new Calendar();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // 1. Exit for Previous Fragment
        Fade exitFade = new Fade();
        if (previousFragment != null) {
            previousFragment.setExitTransition(exitFade);
        }

        // 3. Enter Transition for New Fragment
        Fade enterFade = new Fade();
        enterFade.setStartDelay(exitFade.getDuration());
        nextFragment.setEnterTransition(enterFade);

        fragmentTransaction.replace(R.id.fragment, nextFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }












    //Recycle view
    public static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<String> mDatasetTexts;
        private ArrayList<String> mDatasetImages;
        private Context mContext;

        MyAdapter(Context mContext, ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetImages) {
            this.mDatasetTexts = mDatasetTexts;
            this.mDatasetImages = mDatasetImages;
            this.mContext = mContext;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView textView;
            ImageView imgView;
            RelativeLayout parentLayout;
            MyViewHolder(View v) {
                super(v);
                imgView = v.findViewById(R.id.image_list);
                textView = v.findViewById(R.id.list_text);
                parentLayout = v.findViewById(R.id.parent_layout);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<String> mDatasetTexts, ArrayList<String> mDatasetImages) {
            this.mDatasetTexts = mDatasetTexts;
            this.mDatasetImages = mDatasetImages;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // create a new view
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout_notes, parent, false);

            return new MyViewHolder(view);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            // I'm using holder.getAdapterPosition() instead of position,
            // need to check if it works with new notes and notes re-arrangement

            Log.d("mydebug","onBindViewHolder: called");

            //set the text
            holder.textView.setText(mDatasetTexts.get(holder.getAdapterPosition()));
            holder.textView.setTransitionName(String.valueOf(holder.getAdapterPosition()));

            //set the image (load from url)
            Glide.with(mContext)
                    .asBitmap()
                    .load(mDatasetImages.get(position))
                    .into(holder.imgView);

            //set the click listener for the item
            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //change bottom nav bar icons
                    MainActivity mainActivity = (MainActivity) mContext;
                    mainActivity.ChangeBotBar();
                    mainActivity.performTransition(holder.getAdapterPosition());

                    //change fragment
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    Calendar c = new Calendar();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, c).addToBackStack(null).commit();
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDatasetTexts.size();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Bot menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_bar_right, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Left hidden menu

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

            Toast.makeText(this, "Share!", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    void populateRecycleView(){

        //Recycle view
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //some sample text and images
        for(int i = 0; i < 30; i++){
            memo_texts.add("memo text value is : " + String.valueOf(i));
            memo_images.add("https://picsum.photos/200/200/?image="+i);
        }

        // specify an adapter (see also next example)
        MyAdapter mAdapter = new MyAdapter(this, memo_texts, memo_images);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    void refreshRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        MyAdapter mAdapter = new MyAdapter(this, memo_texts, memo_images);
        recyclerView.swapAdapter(mAdapter,true);
        //recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }



}
