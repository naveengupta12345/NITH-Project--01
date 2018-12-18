package com.nith.major.nithlogger;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.nith.major.nithlogger.events.ComplaintFragment;
import com.nith.major.nithlogger.events.EventFragment;
import com.nith.major.nithlogger.user.ProfileFragment;


public class MainActivity extends AppCompatActivity implements  ProfileFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        textView = (TextView) findViewById(R.id.appname);
        textView.setText("Profile");
        loadFragment(new ProfileFragment());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_event:
                    textView.setText("Upcoming Events");
                    fragment = new EventFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_complain:
                    textView.setText("Register Complaint");
                    fragment = new ComplaintFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_profile:
                    textView.setText("Profile");
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }
}