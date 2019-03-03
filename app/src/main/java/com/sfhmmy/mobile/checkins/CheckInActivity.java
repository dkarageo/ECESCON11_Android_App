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

import android.os.Bundle;
import android.widget.Toast;

import com.sfhmmy.mobile.R;


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
                Toast.makeText(CheckInActivity.this, value, Toast.LENGTH_SHORT).show();
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
}
