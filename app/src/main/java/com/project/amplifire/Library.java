package com.project.amplifire;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.project.amplifire.Adapters.FragmentAdapter;
import com.project.amplifire.Fragments.songListFragment;


public class Library extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = findViewById(R.id.library_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }
    @Override
    protected void onStart(){
        super.onStart();
    }
    public void setupViewPager(ViewPager viewPager) {

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new songListFragment(),"Song adapter");
        viewPager.setAdapter(adapter);
    }
    @Override
    protected void onPause(){
        super.onPause();
   }
    @Override
    protected void onResume(){
        super.onResume();
    }

}