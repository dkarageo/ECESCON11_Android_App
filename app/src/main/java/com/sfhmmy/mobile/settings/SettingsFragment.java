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
import android.os.AsyncTask;
import android.os.Bundle;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.MainActivity;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;
import com.sfhmmy.mobile.utils.DrawableUtils;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference mUsersListUpdate;

    private RemoteServerProxy.UsersListFetcherListener mUsersListFetcherListener
            = new RemoteServerProxy.UsersListFetcherListener() {
                @Override
                public void onProgressOnUserFetching(int fetchedCount, int totalCount) {
                    if (mUsersListUpdate == null) return;

                    if (fetchedCount < totalCount) {
                        mUsersListUpdate.setSummary(String.format(
                                getString(R.string.preferences_users_list_updating),
                                (float) fetchedCount / (float) totalCount * 100
                        ));
                    } else {
                        mUsersListUpdate.setSummary(getString(R.string.preferences_users_list_loading));
                        new UsersListStatusFetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }
    };

    private RemoteServerProxy mProxy = new RemoteServerProxy();


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

        mUsersListUpdate = findPreference("preferences_users_list_update");
        User curUser = UserManager.getUserManager().getCurrentUser();
        if (curUser != null && curUser.hasRoleOf(User.Role.SECRETARY) && mUsersListUpdate != null) {
            mUsersListUpdate.setVisible(true);
            mUsersListUpdate.setSummary(getString(R.string.preferences_users_list_loading));
            mUsersListUpdate.setIcon(DrawableUtils.applyTintToDrawable(
                    getResources().getDrawable(R.drawable.generic_user_icon),
                    R.color.colorPrimary
            ));

            new UsersListStatusFetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            mUsersListUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    mProxy.asyncUpdateUsersList(curUser.getToken());
                    return false;
                }
            });

        } else  if (mUsersListUpdate != null){
            mUsersListUpdate.setVisible(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mProxy.registerUsersListFetcherListener(mUsersListFetcherListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mProxy.unregisterUsersListFetcherListener(mUsersListFetcherListener);
    }


    private class UsersListStatusFetcher extends AsyncTask<Void, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Void... voids) {

            RemoteServerProxy proxy = new RemoteServerProxy();
            int usersCount = proxy.getCachedUsersListCount();
            ZonedDateTime lastUpdate = proxy.getCachedUsersListLastUpdate();

            return new Object[] { usersCount, lastUpdate };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            super.onPostExecute(args);

            int usersCount = (Integer) args[0];
            ZonedDateTime lastUpdate = (ZonedDateTime) args[1];

            if (mUsersListUpdate != null) {

                String lastUpdateText = "";
                if (lastUpdate != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
                    lastUpdateText = String.format(
                            "  -  %s %s",
                            getString(R.string.preferences_users_list_users_last_update_prefix),
                            lastUpdate.format(formatter)
                    );
                }

                mUsersListUpdate.setSummary(String.format(Locale.getDefault(), "%s %d%s",
                        getString(R.string.preferences_users_list_users_count_prefix),
                        usersCount,
                        lastUpdateText
                ));
            }
        }
    }
}
