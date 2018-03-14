package com.project.amplifire;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private ArrayList<Song> mSongArrayList;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewPager);
        Toolbar libraryToolbar = findViewById(R.id.libraryToolbar);
        setSupportActionBar(libraryToolbar);
        String title = "AmpliPlay";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#ecf0f1")), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
       // changeFragment(0);
        setupViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }


    public void setupViewPager(ViewPager viewPager) {

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new songListFragment(),"abc");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onPause(){
        super.onPause();
//
//        if(!mSongArrayList.isEmpty()){
//            mSongArrayList.clear();
//        }
    }
    @Override
    protected void onResume(){
        super.onResume();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText);
//                return false;
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }

}