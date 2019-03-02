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

import com.sfhmmy.mobile.R;


/**
 * Activity that provides access to QR Check-In capabilities and a user list for manual
 * Check-Ins.
 */
public class CheckInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

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
                    return new CameraScannerFragment();
                case 1:
                    return new UsersListFragment();
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
