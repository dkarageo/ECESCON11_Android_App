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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;


/**
 * Activity that provides access to QR Check-In capabilities and a user list for manual
 * Check-Ins.
 */
public class CheckInActivity extends AppCompatActivity {

    // Fragments of the PagerAdapter.
    CameraScannerFragment mScannerFragment;
    UsersListFragment     mUsersFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        // Create and setup a new camera scanner fragment.
        mScannerFragment = new CameraScannerFragment();
        mScannerFragment.registerOnCodeFoundEventListener(
                new CameraScannerFragment.OnCodeFoundEventListener() {
            @Override
            public void onCodeFound(String value) {
                // Firstly, display the dialog with its loading spinner.
                CheckInStatusDialogFragment statusDialog = new CheckInStatusDialogFragment();
                statusDialog.show(getSupportFragmentManager(), "statusDialog");
                // Set a listener that starts camera scanner again when popup status window closes.
                statusDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mScannerFragment.startScanner();
                    }
                });

                // Then, handle check-in process on a new thread.
                new CheckInProcess().execute(value, statusDialog);
            }
        });

        // Create and setup a new users list fragment.
        mUsersFragment = new UsersListFragment();

        // Setup view pager with scanner and users list fragments.
        QRScannerPagerAdapter pagerAdapter = null;
        ViewPager viewPager;
        try {
            pagerAdapter = new QRScannerPagerAdapter(getSupportFragmentManager());
        } catch (NullPointerException e) { e.printStackTrace(); }
        viewPager = findViewById(R.id.qrscanner_viewpager);
        viewPager.setAdapter(pagerAdapter);
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
            CheckInStatusDialogFragment dialog = (CheckInStatusDialogFragment) args[1];

            return new Object[] {
                    new RemoteServerProxy().checkInUser(
                            UserManager.getUserManager().getCurrentUser().getToken(), code),
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
}
