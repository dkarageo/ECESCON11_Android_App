package com.sfhmmy.mobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.TextView;

import com.sfhmmy.mobile.battles.BattlesFragment;
import com.sfhmmy.mobile.promo.InfoFragment;
import com.sfhmmy.mobile.users.PassportFragment;
import com.sfhmmy.mobile.workshops.WorkshopsFragment;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    // A map for all active fragments navigable from bottom navigation bar.
    Map<String, Fragment> activeFragments = new HashMap<>();

    // Listener for clicks on bottom navigation bar items.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        /**
         * Implementation of bottom navigation bar logic.
         */
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selection;

            switch (item.getItemId()) {
                case R.id.bot_nav_home:
                    if (activeFragments.containsKey("home")) selection = activeFragments.get("home");
                    else {
                        selection = new HomeFragment();
                        activeFragments.put("home", selection);
                    }
                    break;

                case R.id.bot_nav_workshops:
                    if (activeFragments.containsKey("workshops")) {
                        selection = activeFragments.get("workshops");
                    } else {
                        selection = new WorkshopsFragment();
                        activeFragments.put("workshops", selection);
                    }
                    break;

                case R.id.bot_nav_passport:
                    if(activeFragments.containsKey("passport")) {
                        selection = activeFragments.get("passport");
                    } else {
                        selection = new PassportFragment();
                        activeFragments.put("passport", selection);
                    }
                    break;

                case R.id.bot_nav_battles:
                    if (activeFragments.containsKey("battles")) {
                        selection = activeFragments.get("battles");
                    } else {
                        selection = new BattlesFragment();
                        activeFragments.put("battles", selection);
                    }
                    break;

                case R.id.bot_nav_info:
                    if (activeFragments.containsKey("info")) {
                        selection = activeFragments.get("info");
                    } else {
                        selection = new InfoFragment();
                        activeFragments.put("info", selection);
                    }
                    break;

                default:
                    return false;
            }

            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.main_fragment_container, selection);
            t.addToBackStack(null);
            t.commit();

            // TODO: Update bottom navigation bar when pressing back button.

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment homeFrag = new HomeFragment();
        activeFragments.put("home", homeFrag);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.add(R.id.main_fragment_container, homeFrag);
        t.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
