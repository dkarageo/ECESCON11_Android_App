package com.sfhmmy.mobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.sfhmmy.mobile.battles.BattlesFragment;
import com.sfhmmy.mobile.promo.InfoFragment;
import com.sfhmmy.mobile.users.PassportFragment;
import com.sfhmmy.mobile.workshops.WorkshopsFragment;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    // A map for all active fragments navigable from bottom navigation bar.
    private Map<String, Fragment> activeFragments = new HashMap<>();
    // An indicator set to true when a fragment change is caused by a press to back button (and
    // e.g. not from a click to navigation bar buttons).
    private boolean switchCausedByBackButton = false;

    // Listener for clicks on bottom navigation bar items.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (!switchCausedByBackButton) {
                Fragment selection;

                switch (item.getItemId()) {
                    case R.id.bot_nav_home:
                        if (activeFragments.containsKey("home"))
                            selection = activeFragments.get("home");
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
                        if (activeFragments.containsKey("passport")) {
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
                t.addToBackStack(Integer.toString(item.getItemId()));
                t.commit();
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            // Initially set activity's fragment to home.
            Fragment homeFrag = new HomeFragment();
            activeFragments.put("home", homeFrag);
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.add(R.id.main_fragment_container, homeFrag);
            t.commit();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {

        FragmentManager fm = getSupportFragmentManager();
        switchCausedByBackButton = true;

        int backStackCount;
        if ((backStackCount = fm.getBackStackEntryCount()) > 0) {
            // Update active item at bottom navigation bar when pressing back button.
            int menuItemId;

            // On first transaction, the previous fragment is always the home screen.
            // All fragment transactions are expected to contain in their 'name' the id of menu
            // item their containing fragment wants to be active (while the fragment is active).
            if (backStackCount > 1) {
                FragmentManager.BackStackEntry t = fm.getBackStackEntryAt(backStackCount - 2);
                menuItemId = Integer.parseInt(t.getName());
            } else {
                menuItemId = R.id.bot_nav_home;
            }

            BottomNavigationView navBar = findViewById(R.id.navigation);
            if (navBar == null) throw new RuntimeException("Failed to acquire bottom navigation bar");
            navBar.setSelectedItemId(menuItemId);

            fm.popBackStack();
        } else {
            super.onBackPressed();
        }

        switchCausedByBackButton = false;
    }
}
