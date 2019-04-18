/*
 * MainActivity.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.2
 */

package com.sfhmmy.mobile;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sfhmmy.mobile.battles.BattlesFragment;
import com.sfhmmy.mobile.checkins.CheckInActivity;
import com.sfhmmy.mobile.startups.StartupManager;
import com.sfhmmy.mobile.users.LoginDialogFragment;
import com.sfhmmy.mobile.users.PassportFragment;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;
import com.sfhmmy.mobile.utils.DrawableUtils;
import com.sfhmmy.mobile.workshops.WorkshopsFragment;

import java.util.LinkedList;


public class MainActivity extends AppCompatActivity
        implements TopLevelFragmentEventsListener,
                   UserManager.UserAuthenticationListener {

    private static final String HOME_FRAGMENT_TAG = "home";
    private static final String WORKSHOPS_FRAGMENT_TAG = "workshops";
    private static final String PASSPORT_FRAGMENT_TAG = "passport";
    private static final String BATTLES_FRAGMENT_TAG = "battles";
    private static final String MAIN_MENU_FRAGMENT_TAG = "main_menu";

    private static final String STARTUP_COMPLETED_STORE_KEY = "startup_completed";

    private BottomNavigationView mNavBar;
    private FrameLayout          mFragmentContainer;
    private View                 mStartupLoadingBg;
    private ImageView            mStartupLoadingIcon;

    // Custom backstack for the current activity.
    private LinkedList<BackstackItem> mBackStack = new LinkedList<>();
    // Indicates whether a click to bottom navigation bar was not initiated by the user, but from
    // an attempt to fix currently highlighted item.
    private boolean mIsFixingBottomNavigationSelection = false;

    private StartupManager.StartupProcessListener mStartupListener;
    private boolean mStartupCompleted;

    // Listener for clicks on bottom navigation bar items.
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (!mIsFixingBottomNavigationSelection) {
                NavigableKey navKey;

                switch (item.getItemId()) {
                    case R.id.bot_nav_home:
                        navKey = new NavigableKey() {
                            @Override
                            public String getKey() {
                                return HOME_FRAGMENT_TAG;
                            }

                            @Override
                            public Fragment createFragment() {
                                return new HomeFragment();
                            }
                        };
                        break;

                    case R.id.bot_nav_workshops:
                        navKey = new NavigableKey() {
                            @Override
                            public String getKey() {
                                return WORKSHOPS_FRAGMENT_TAG;
                            }

                            @Override
                            public Fragment createFragment() {
                                return new WorkshopsFragment();
                            }
                        };
                        break;

                    case R.id.bot_nav_passport:
                        navKey = new NavigableKey() {
                            @Override
                            public String getKey() {
                                return PASSPORT_FRAGMENT_TAG;
                            }

                            @Override
                            public Fragment createFragment() {
                                return new PassportFragment();
                            }
                        };
                        break;

                    case R.id.bot_nav_battles:
                        navKey = new NavigableKey() {
                            @Override
                            public String getKey() {
                                return BATTLES_FRAGMENT_TAG;
                            }

                            @Override
                            public Fragment createFragment() {
                                return new BattlesFragment();
                            }
                        };
                        break;

                    case R.id.bot_nav_main_menu:
                        navKey = new NavigableKey() {
                            @Override
                            public String getKey() {
                                return MAIN_MENU_FRAGMENT_TAG;
                            }

                            @Override
                            public Fragment createFragment() {
                                return new MainMenuFragment();
                            }
                        };
                        break;

                    default:
                        return false;
                }

                navigateToNavigableKey(navKey, false);
            }

            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavBar             = findViewById(R.id.main_activity_navigation);
        mFragmentContainer  = findViewById(R.id.main_activity_fragment_container);
        mStartupLoadingBg   = findViewById(R.id.main_activity_startup_loading_bg);
        mStartupLoadingIcon = findViewById(R.id.main_activity_startup_loading_icon);

        mNavBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            // Initially set activity's fragment to home.
            navigateToNavigableKey(new NavigableKey() {
                @Override
                public String getKey() {
                    return HOME_FRAGMENT_TAG;
                }

                @Override
                public Fragment createFragment() {
                    return new HomeFragment();
                }
            }, false);

            // Initialization of application starts here.
            executeStartupProcess();
        } else {
            mBackStack = (LinkedList<BackstackItem>) getLastCustomNonConfigurationInstance();
            mStartupCompleted = savedInstanceState.getBoolean(STARTUP_COMPLETED_STORE_KEY);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_layout);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity/Firebase", "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();
                        Log.d("MainActivity/Firebase", "Instance token: "+token);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserManager.getUserManager().registerUserAuthenticationListener(this);

        if (mStartupCompleted) {
            setStatusBarColor(getResources().getColor(R.color.white));
            setDarkStatusBarIcons(true);
        } else {
            setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            setDarkStatusBarIcons(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        UserManager.getUserManager().unregisterUserAuthenticationListener(this);

        // Just a best effort cleanup of the listener.
        if (mStartupCompleted) {
            StartupManager.getStartupManager().unregisterStartupProcessListener(mStartupListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STARTUP_COMPLETED_STORE_KEY, mStartupCompleted);
    }

    @Override
    public void onBackPressed() {
        // Delegate to custom navigator only back requests
        if (mBackStack.size() > 1) goBack();
        else super.onBackPressed();
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
            case R.id.mainactivity_actionbar_scanner:
                startActivity(new Intent(this, CheckInActivity.class));
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        super.onRetainCustomNonConfigurationInstance();
        return mBackStack;
    }

    // ---- TopLevelFragmentEventsListener methods ----
    @Override
    public void updateTitle(String newTitle) {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            View view = actionBar.getCustomView();
            TextView title = view.findViewById(R.id.action_bar_text);
            if (title != null) title.setText(newTitle);
        }
    }

    @Override
    public void hideNavigationBar() {
        mNavBar.setVisibility(View.GONE);
    }

    @Override
    public void showNavigationBar() {
        mNavBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void navigateToNavigableKey(NavigableKey target, boolean skipCurrentOnBack) {

        BackstackItem targetItem = null;

        // If there is a request for skipping current (last) key in backstack, then remove it.
        if (skipCurrentOnBack) mBackStack.removeLast();

        // Firstly, search backstack for the existence of requested destination.
        for (BackstackItem item : mBackStack) {
            if (item.getNavigableKey().getKey().equals(target.getKey())) {
                targetItem = item;

                // If a matching item found, remove it from its previous position, so to place
                // it a the end.
                mBackStack.remove(item);
                break;
            }
        }

        // If destination could not be found in backstack, create one.
        if (targetItem == null) {
            targetItem = new BackstackItem(target, target.createFragment());
        }

        // Always add the item to the end of backstack.
        mBackStack.add(targetItem);

        FragmentManager manager = getSupportFragmentManager();
        Fragment targetFrag = manager.findFragmentByTag(targetItem.getNavigableKey().getKey());
        if (targetFrag == null) targetFrag = targetItem.getFragment();

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_activity_fragment_container, targetFrag);
        t.commit();

        // Fix selected item on bottom navigation bar.
        selectBottomNavigationItem(targetItem);
    }

    public void goBack() {
        if (mBackStack.size() > 1) {
            // Remove current item from backstack.
            mBackStack.removeLast();

            // Navigate to the previous one.
            navigateToNavigableKey(mBackStack.peekLast().getNavigableKey(), false);
        }
    }

    // ---- UserManager.UserAuthenticationListener methods ----
    @Override
    public void onSessionCreated(User user) { updateUserSpecificContent(user); }

    @Override
    public void onSessionDestroyed() { updateUserSpecificContent(null); }

    @Override
    public void onSessionRestorationFailure(String error) { updateUserSpecificContent(null); }

    private void updateUserSpecificContent(User user) {
        // Notify all child User Aware Fragments about the user change.
        for (BackstackItem item : mBackStack) {

            Fragment f = item.getFragment();

            if (f instanceof UserAwareFragment) {
                ((UserAwareFragment) f).notifyOnUserChange(user);
            }
        }

        // Refresh the options menu, to properly display or hide user specific options.
        invalidateOptionsMenu();
    }

    private void executeStartupProcess() {

        mStartupCompleted = false;

        setVisibilityOfStartupLoadingScreen(true);

        mStartupListener = new StartupManager.StartupProcessListener() {
            @Override
            public void onStartupCompleted() {
                setVisibilityOfStartupLoadingScreen(false);
                mStartupCompleted = true;
                setStatusBarColor(getResources().getColor(R.color.white));
                setDarkStatusBarIcons(true);

                // On first run of the application, open login dialog.
                StartupManager suManager = StartupManager.getStartupManager();
                if (suManager.isFirstRun() && UserManager.getUserManager().getCurrentUser() == null)  {
                    LoginDialogFragment loginDialog = LoginDialogFragment.newInstance();
                    loginDialog.show(getSupportFragmentManager(), "loginDialog");
                    suManager.terminateFirstRun();
                }
            }
        };

        StartupManager manager = StartupManager.getStartupManager();
        manager.registerStartupProcessListener(mStartupListener);
        manager.startup();
    }

    private void setVisibilityOfStartupLoadingScreen(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (!show) actionBar.show();
            else actionBar.hide();
        }

        if (show) {
            mStartupLoadingIcon.setVisibility(View.VISIBLE);
            mStartupLoadingBg.setVisibility(View.VISIBLE);
            mNavBar.setVisibility(View.GONE);
            mFragmentContainer.setVisibility(View.GONE);

            mStartupLoadingIcon.setImageDrawable(
                    DrawableUtils.applyTintToDrawable(
                            getResources().getDrawable(R.drawable.ecescon11_logo),
                            R.color.white)
            );

            Animation pulseAnim = AnimationUtils.loadAnimation(
                    this, R.anim.main_activity_startup_icon_pulse
            );
            mStartupLoadingIcon.startAnimation(pulseAnim);
        } else {
            mStartupLoadingIcon.clearAnimation();

            mStartupLoadingIcon.setVisibility(View.GONE);
            mStartupLoadingBg.setVisibility(View.GONE);
            mNavBar.setVisibility(View.VISIBLE);
            mFragmentContainer.setVisibility(View.VISIBLE);
        }
    }

    private void selectBottomNavigationItem(BackstackItem item) {
        mIsFixingBottomNavigationSelection = true;

        String tag = item.getNavigableKey().getKey();

        // Initially, enable the ability of bottom navigation bar to display last clicked item.
        mNavBar.getMenu().setGroupCheckable(0, true, true);

        if (tag.equals(HOME_FRAGMENT_TAG)) mNavBar.setSelectedItemId(R.id.bot_nav_home);
        else if (tag.equals(WORKSHOPS_FRAGMENT_TAG)) mNavBar.setSelectedItemId(R.id.bot_nav_workshops);
        else if (tag.equals(PASSPORT_FRAGMENT_TAG)) mNavBar.setSelectedItemId(R.id.bot_nav_passport);
        else if (tag.equals(BATTLES_FRAGMENT_TAG)) mNavBar.setSelectedItemId(R.id.bot_nav_battles);
        else if (tag.equals(MAIN_MENU_FRAGMENT_TAG)) mNavBar.setSelectedItemId(R.id.bot_nav_main_menu);
        else {
            // If navigation leads to a target not reachable directly by bottom nav bar, then
            // unselect all items.
            mNavBar.getMenu().setGroupCheckable(0, false, true);
        }

        mIsFixingBottomNavigationSelection = false;
    }

    @TargetApi(23)
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(color);
        }
    }

    @TargetApi(23)
    private void setDarkStatusBarIcons(boolean darkIcons) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();

            if (darkIcons) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(0);
            }
        }
    }

    private class BackstackItem {
        private NavigableKey mNavigableKey;
        private Fragment mFragment;

        public BackstackItem(NavigableKey navigableKey, Fragment fragment) {
            mNavigableKey = navigableKey;
            mFragment = fragment;
        }

        public NavigableKey getNavigableKey() { return mNavigableKey; }
        public Fragment getFragment() { return mFragment; }

        public void setFragment(Fragment fragment) { mFragment = fragment; }
        public void setNavigableKey(NavigableKey navigableKey) { mNavigableKey = navigableKey; }
    }
}
