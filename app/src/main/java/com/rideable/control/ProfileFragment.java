package com.rideable.control;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;


import com.rideable.R;
import com.rideable.model.User;
import com.rideable.resources.SlidingTabLayout;
import com.rideable.resources.Types;

import java.util.Locale;


public class ProfileFragment extends Fragment {

    private TextView name;
    private TextView userEmail;
    private RatingBar rating;

    private String fName;
    private String lName;
    private String email;

    private User aUser;

    public ProfileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView =  (ViewGroup)inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        name = (TextView)rootView.findViewById(R.id.name);
        userEmail = (TextView)rootView.findViewById(R.id.email);
        rating = (RatingBar)rootView.findViewById(R.id.rating_bar);

        rating.setRating(5);

        setHasOptionsMenu(true);
        return rootView;
    }

     @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

            aUser = Types.aUser;
            name.setText(aUser.getFirstName() + " " + aUser.getLastName());
            userEmail.setText(aUser.getUserEmail());


    }

   /* @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
           // super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }*/





}
