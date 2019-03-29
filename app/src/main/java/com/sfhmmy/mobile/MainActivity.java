/*
 * MainActivity.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.sfhmmy.mobile.battles.BattlesFragment;
import com.sfhmmy.mobile.checkins.CheckInActivity;
import com.sfhmmy.mobile.startups.StartupManager;
import com.sfhmmy.mobile.users.LoginDialogFragment;
import com.sfhmmy.mobile.users.PassportFragment;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;
import com.sfhmmy.mobile.workshops.WorkshopsFragment;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements TopLevelFragmentEventsListener,
                   UserManager.UserAuthenticationListener {

    BottomNavigationView navBar;

    // A map for all active fragments navigable from bottom navigation bar.
    private Map<String, Fragment> activeFragments = new HashMap<>();
    // An indicator set to true when a fragment change is caused by a press to back button (and
    // e.g. not from a click to navigation bar buttons).
    private boolean switchCausedByBackButton = false;
//    // An indicator set to true when profile menu is open (i.e. MainMenuFragment
//    // is the current one).
//    private boolean isProfileMenuEnabled = false;
//    // Indicates whether navigation bar was visible when profile menu showed up.
//    private boolean wasNavBarVisibleWhenProfileMenuDisplayed = false;

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

                    case R.id.bot_nav_main_menu:
                        if (activeFragments.containsKey("main_menu")) {
                            selection = activeFragments.get("main_menu");
                        } else {
                            selection = new MainMenuFragment();
                            activeFragments.put("main_menu", selection);
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

        navBar = findViewById(R.id.navigation);
        if (navBar == null) throw new RuntimeException("Failed to acquire bottom navigation bar");
        navBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserManager.getUserManager().registerUserAuthenticationListener(this);

        // On first run of the application, open login dialog.
        boolean isFirstRun = StartupManager.getStartupManager().isFirstRun();
        if (isFirstRun && UserManager.getUserManager() == null)  {
            LoginDialogFragment loginDialog = LoginDialogFragment.newInstance();
            loginDialog.show(getSupportFragmentManager(), "loginDialog");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserManager.getUserManager().unregisterUserAuthenticationListener(this);
    }

    @Override
    public void onBackPressed() {

//        // When profile menu is enabled, the back button should hide the menu and not switch
//        // between main fragments.
//        if (isProfileMenuEnabled) {
//            displayProfileMenu(false);
//            return;
//        }

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

            navBar.setSelectedItemId(menuItemId);

            fm.popBackStack();
        } else {
            super.onBackPressed();
        }

        switchCausedByBackButton = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Enable Scanner button for users with Secretary privileges.
        MenuItem scannerButton = menu.findItem(R.id.mainactivity_actionbar_scanner);
        if (UserManager.getUserManager().isCurrentUserSecretary()) scannerButton.setVisible(true);
        else scannerButton.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
//            case R.id.mainactivity_actionbar_menu_profile:
//                displayProfileMenu(!isProfileMenuEnabled);
//                break;

            case R.id.mainactivity_actionbar_scanner:
                startActivity(new Intent(this, CheckInActivity.class));
                break;

            default:
                return false;
        }

        return true;
    }

    // ---- TopLevelFragmentEventsListener methods ----
    @Override
    public void updateTitle(String newTitle) {
        setTitle(newTitle);
    }

    @Override
    public void hideNavigationBar() {
        navBar.setVisibility(View.GONE);
    }

    @Override
    public void showNavigationBar() {
        navBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateTo(Fragment target, String tag) {
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_fragment_container, target, tag);
        t.addToBackStack(tag);
        t.commit();
    }

    // ---- UserManager.UserAuthenticationListener methods ----
    @Override
    public void onSessionCreated(User user) { updateUserSpecificContent(user); }

    @Override
    public void onSessionDestroyed() { updateUserSpecificContent(null); }

    @Override
    public void onSessionRestorationFailure(String error) { updateUserSpecificContent(null); }

//    /**
//     * Displays/Hides the profile menu of the application.
//     */
//    private void displayProfileMenu(boolean display) {
//        if (display && !isProfileMenuEnabled) {
//            MainMenuFragment menuFrag;
//
//            if (!activeFragments.containsKey("profile_menu")) {
//                menuFrag = new MainMenuFragment();
//                activeFragments.put("profile_menu", menuFrag);
//            } else {
//                menuFrag = (MainMenuFragment) activeFragments.get("profile_menu");
//            }
//
//            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
//            t.replace(R.id.main_fragment_container, menuFrag);
//            t.addToBackStack("profile_menu");
//            t.commit();
//
//            wasNavBarVisibleWhenProfileMenuDisplayed = isNavigationBarVisible();
//            // Bottom nav bar should be hidden during menu display.
//            hideNavigationBar();
//
//            isProfileMenuEnabled = true;
//
//        } else if (!display && isProfileMenuEnabled) {
//            getSupportFragmentManager().popBackStack(
//                    "profile_menu", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//
//            if (wasNavBarVisibleWhenProfileMenuDisplayed) showNavigationBar();
//
//            isProfileMenuEnabled = false;
//        }
//    }

//    private boolean isNavigationBarVisible() {
//        return navBar.getVisibility() == View.VISIBLE;
//    }

    private void updateUserSpecificContent(User user) {
        // Notify all child User Aware Fragments about the user change.
        for (Fragment f : activeFragments.values()) {
            if (f instanceof UserAwareFragment) {
                ((UserAwareFragment) f).notifyOnUserChange(user);
            }
        }

        // Refresh the options menu, to properly display or hide user specific options.
        invalidateOptionsMenu();
    }
}
