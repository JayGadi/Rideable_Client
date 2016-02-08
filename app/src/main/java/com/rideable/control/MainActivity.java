package com.rideable.control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.rideable.R;
import com.rideable.model.User;
import com.rideable.resources.FragmentDrawer;
import com.rideable.resources.Types;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private FragmentDrawer drawerFragment;
    private Toolbar myToolbar;

    private User aUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aUser = Types.aUser;

        myToolbar = (Toolbar) findViewById(R.id.options_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout), myToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);

    }

    private void displayView(int position) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new ProfileFragment();
                title = "Profile";
                break;
            case 1:
                title = "Post A Ride";
                fragment = new PostFragment();
                break;
            case 2:
                title = "Find A Ride";
                fragment = new FindFragment();
                break;
            case 3:
                title = "Chat";
                fragment = new ChatFragment();

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

           // getSupportActionBar().setTitle(title);
        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}
