package com.project.amplifire;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.amplifire.Adapters.FragmentAdapter;
import com.project.amplifire.DataModels.References;
import com.project.amplifire.Fragments.PlaylistGridFragment;
import com.project.amplifire.Fragments.SongListFragment;


public class Library extends AppCompatActivity {

    private MaterialSearchView mSearchView;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_activity);
        References.applicationContext = getApplicationContext();
        ViewPager viewPager = findViewById(R.id.library_interface_view_pager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = findViewById(R.id.library_interface_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar libraryToolbar = findViewById(R.id.library_interface_libraryToolbar);
        setSupportActionBar(libraryToolbar);
        String title = "  AmpliPlay";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);


        Drawable logo = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_ampliplay);
        logo.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setLogo(logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mSearchView = findViewById(R.id.library_interface_search_view);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
    public void setupViewPager(ViewPager viewPager) {

        final FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addFragment(new SongListFragment(),"Song adapter");
                adapter.notifyDataSetChanged();
            }
        }, 50);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.addFragment(new PlaylistGridFragment(), "playlist songs");
                adapter.notifyDataSetChanged();
            }
        }, 200);
    }
    @Override
    protected void onPause(){
        super.onPause();
   }
    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(mSearchView.isSearchOpen())
        {
            mSearchView.closeSearch();
//            mTabLayout.setEnabled(true);
        }
        else {
            super.onBackPressed();
        }
    }
}