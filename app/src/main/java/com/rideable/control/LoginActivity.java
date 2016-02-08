package com.rideable.control;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toolbar;

import com.rideable.R;
import com.rideable.model.User;
import com.rideable.resources.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class LoginActivity extends FragmentActivity {

    private static final int NUM_PAGES = 2;
    private ViewPager mPager;
    private Toolbar actionBar;
    private SlidingTabLayout mSlidingTabLayout;

    private String[] tabs = {"Login", "Sign Up"};
    private ScreenSlidePagerAdapter mPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPager = (ViewPager)findViewById(R.id.pager);
        actionBar = (Toolbar)findViewById(R.id.my_toolbar);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mPager);



    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new LoginFragment();
                case 1:
                    return new SignUpFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return tabs[0].toUpperCase(l);
                case 1:
                    return tabs[1].toUpperCase(l);
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
