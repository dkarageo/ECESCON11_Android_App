/*
 * CheckInActivity.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.checkins;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;
import com.sfhmmy.mobile.users.UserProfileFragment;

import org.threeten.bp.ZonedDateTime;

import java.util.List;
import java.util.concurrent.Executors;


/**
 * Activity that provides access to QR Check-In capabilities and a user list for manual
 * Check-Ins.
 */
public class CheckInActivity extends AppCompatActivity {

    // Fragments of the PagerAdapter.
    CameraScannerFragment mScannerFragment;
    UsersListFragment     mUsersFragment;
    ViewPager             mViewPager;

    private boolean mIsProfileViewVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        // Create and setup a new camera scanner fragment.
        if (mScannerFragment == null) mScannerFragment = new CameraScannerFragment();
        mScannerFragment.registerOnCodeFoundEventListener(
                new CameraScannerFragment.OnCodeFoundEventListener() {
            @Override
            public void onCodeFound(String value) {

                ZonedDateTime secondDayBegin = ZonedDateTime.parse(
                        getString(R.string.conference_second_day_starts_at)
                );
                ZonedDateTime thirdDayBegin = ZonedDateTime.parse(
                        getString(R.string.conference_third_day_starts_at)
                );
                ZonedDateTime now = ZonedDateTime.now();

                String dayTag;
                if (now.isBefore(secondDayBegin)) dayTag = "first";
                else if (now.isBefore(thirdDayBegin)) dayTag = "second";
                else dayTag = "third";

                checkIn(value, dayTag);
            }
        });

        // Create and setup a new users list fragment.
        if (mUsersFragment == null) mUsersFragment = new UsersListFragment();
        mUsersFragment.registerOnCheckInRequestListener(
                new UsersListFragment.OnCheckInRequestListener() {
            @Override
            public void onCheckInRequested(User user, String dayTag) {
                checkIn("ecescon11://"+user.getPassportValue(), dayTag);
            }
        });
        mUsersFragment.setOnViewProfileRequestListener(new UsersListFragment.OnViewProfileRequestListener() {
            @Override
            public void onViewProfileRequested(User user) {
                UserProfileFragment profileFrag = UserProfileFragment.newInstance(user);
                FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.check_in_activity_base_wrapper, profileFrag);
                t.addToBackStack("checkin_user_profile");
                t.commit();
                mIsProfileViewVisible = true;
                mViewPager.setVisibility(View.GONE);
            }
        });

        // Setup view pager with scanner and users list fragments.
        QRScannerPagerAdapter pagerAdapter = null;
        try {
            pagerAdapter = new QRScannerPagerAdapter(getSupportFragmentManager());
        } catch (NullPointerException e) { e.printStackTrace(); }
        mViewPager = findViewById(R.id.qrscanner_viewpager);
        mViewPager.setAdapter(pagerAdapter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_layout);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new UsersListFetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUsersFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setStatusBarColor(getResources().getColor(R.color.white));
        setDarkStatusBarIcons(true);
        updateTitle(getString(R.string.checkin_activity_action_bar_title));
    }

    private void checkIn(String value, String dayTag) {
        // Firstly, display the dialog with its loading spinner.
        CheckInStatusDialogFragment statusDialog = new CheckInStatusDialogFragment();
        statusDialog.show(getSupportFragmentManager(), "statusDialog");
        // Set a listener that starts camera scanner again when popup status window closes.
        statusDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // Camera scanner should restart only when check in is initiated by it.
                if (mViewPager.getCurrentItem() == 0) mScannerFragment.startScanner();
            }
        });

        // Then, handle check-in process on a new thread.
        new CheckInProcess().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, value, dayTag, statusDialog);
    }


    public class QRScannerPagerAdapter extends FragmentPagerAdapter {
        QRScannerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mScannerFragment;
                case 1:
                    return mUsersFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public long getItemId(int position) {
            switch (position) {
                case 0:
                    return mScannerFragment.hashCode();
                case 1:
                    return mUsersFragment.hashCode();
                default:
                    return POSITION_NONE;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    /**
     * Async Task for checking in a user via the code received from camera scanner.
     *
     * Parameters:
     * -args : A multi-argument structure. The first argument should be a String with the code
     *          received from scanner. The second argument should be the
     *          CheckInStatusDialogFragment which will be updated with the result of check-in
     *          process.
     */
    private static class CheckInProcess
            extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... args) {
            String code = (String) args[0];
            String dayTag = (String) args[1];
            CheckInStatusDialogFragment dialog = (CheckInStatusDialogFragment) args[2];

            return new Object[] {
                    new RemoteServerProxy().checkInUser(
                            UserManager.getUserManager().getCurrentUser().getToken(), code, dayTag),
                    dialog
            };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            RemoteServerProxy.ResponseContainer rc = (RemoteServerProxy.ResponseContainer) args[0];
            CheckInStatusDialogFragment dialog = (CheckInStatusDialogFragment) args[1];

            int    dialogStatus;
            String errorMessage;

            switch (rc.getCode()) {
                case RemoteServerProxy.RESPONSE_SUCCESS:
                    dialogStatus = CheckInStatusDialogFragment.STATUS_SUCCESS;
                    errorMessage = null;
                    break;
                case RemoteServerProxy.RESPONSE_ERROR:
                    dialogStatus = CheckInStatusDialogFragment.STATUS_ERROR;
                    errorMessage = rc.getMessage();
                    break;
                case RemoteServerProxy.RESPONSE_WARNING:
                    dialogStatus = CheckInStatusDialogFragment.STATUS_WARNING;
                    errorMessage = rc.getMessage();
                    break;
                default:
                    throw new RuntimeException("Unknown code contained in check-in response");
            }

            dialog.setCheckInStatusData((User) rc.getObject(), dialogStatus, errorMessage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mIsProfileViewVisible) {
            mViewPager.setVisibility(View.VISIBLE);
            mIsProfileViewVisible = false;
        }
    }

    /**
     * AsyncTask that fetches the complete user list (includes every registered user).
     */
    private static class UsersListFetcher
            extends AsyncTask<UsersListFragment, Void, Object[]> {
        @Override
        protected Object[] doInBackground(UsersListFragment... args) {
            return new Object[] {
                    new RemoteServerProxy().getUsersList(
                            UserManager.getUserManager().getCurrentUser().getToken()),
                    args[0]
            };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            RemoteServerProxy.ResponseContainer<List<User>> rc =
                    (RemoteServerProxy.ResponseContainer<List<User>>) args[0];
            UsersListFragment ulFrag = (UsersListFragment) args[1];

            // Let any null object (in case of error) to be handled by UsersListFragment.
            ulFrag.updateUsersList(rc.getObject());

            // In case of an erroneous situation, display appropriate message.
            switch(rc.getCode()) {
                case RemoteServerProxy.RESPONSE_SUCCESS:
                    ulFrag.displayError(null, false);
                    break;
                case RemoteServerProxy.RESPONSE_ERROR:
                    ulFrag.displayError(rc.getMessage(), false);
                    break;
                case RemoteServerProxy.RESPONSE_WARNING:
                    ulFrag.displayError(rc.getMessage(), true);
                    break;
                default:
                    throw new RuntimeException("Unknown code contained in check-in response");
            }
        }
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

    private void updateTitle(String newTitle) {

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            View view = actionBar.getCustomView();
            TextView title = view.findViewById(R.id.action_bar_text);
            if (title != null) title.setText(newTitle);
        }
    }
}
