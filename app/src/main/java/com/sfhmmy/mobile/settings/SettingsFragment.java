/*
 * SettingsFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */


package com.sfhmmy.mobile.settings;

import android.content.Intent;
import android.os.Bundle;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.MainActivity;
import com.sfhmmy.mobile.R;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ListPreference localeSelector = findPreference("preferences_locale_selector");
        localeSelector.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String locale = (String) newValue;
                App.setLocale(locale);
                Intent refresh = new Intent(getActivity(), MainActivity.class);
                startActivity(refresh);
                requireActivity().finish();
                return true;
            }
        });
    }
}
