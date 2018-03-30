package com.project.amplifire;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.project.amplifire.Adapters.FragmentAdapter;
import com.project.amplifire.Fragments.songListFragment;


public class Library extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_interface);
        ViewPager viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = findViewById(R.id.library_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar libraryToolbar = findViewById(R.id.libraryToolbar);
        setSupportActionBar(libraryToolbar);
        String title = "  ampliplay";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#B53471")), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);


        Drawable logo = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_ampliplay);
        logo.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setLogo(logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
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