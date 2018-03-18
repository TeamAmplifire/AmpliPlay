package com.project.amplifire;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewPager);
        setupViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        TabLayout tabLayout = findViewById(R.id.library_tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
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
   }
    @Override
    protected void onResume(){
        super.onResume();
    }
}