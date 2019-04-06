/*
 * StartupManager.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.startups;


import android.content.Context;
import android.content.SharedPreferences;
import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public class StartupManager {

    private static final String STARTUP_PROPERTIES_FILENAME
            = "com.sfhmmy.mobily.startups.StartupManager.StartupPropertiesFile";
    private static final String IS_FIRST_RUN_KEY = "is_first_run";

    private static StartupManager mActiveStartupManager;

    private StartupProperties mStartupProperties;

    private List<StartupProcessListener> mStartupListeners;

    private boolean mUserRestoreProcessCompleted;
    private UserManager.UserAuthenticationListener mUserAuthenticationListener;

    public static StartupManager getStartupManager() {
        if (mActiveStartupManager == null) mActiveStartupManager = new StartupManager();
        return mActiveStartupManager;
    }

    public boolean isFirstRun() {
        return mStartupProperties.isFirstRun();
    }

    /**
     * Fires startup process.
     *
     * At the moment, this method is intended to be called inside MainActivity's onCreate().
     *
     * When startup process is completed, all registered StartupProcessListener objects are
     * notified via a call to their onStartupCompleted() method.
     */
    public void startup() {
        startUserSessionRestorationProcess();
    }

    public void terminateFirstRun() {
        mStartupProperties.setFirstRun(false);
        saveStartupProperties();
    }

    public void registerStartupProcessListener(@NonNull StartupProcessListener l) {
        mStartupListeners.add(l);
    }

    public void unregisterStartupProcessListener(@NonNull StartupProcessListener l) {
        mStartupListeners.remove(l);

//        // When there are no listeners left, cleanup and let Startup Manager die.
//        if (mStartupListeners.size() == 0) {
//            UserManager.getUserManager()
//                       .unregisterUserAuthenticationListener(mUserAuthenticationListener);
//        }
    }

    private void notifyOnStartupCompleted() {

        boolean allCompleted = mUserRestoreProcessCompleted;

        if (allCompleted) {
            for (StartupProcessListener l : mStartupListeners) {
                l.onStartupCompleted();
            }
        }
    }

    private StartupManager() {
        restoreStartupProperties();
        mStartupListeners = new ArrayList<>();
    }

    private void saveStartupProperties() {
        SharedPreferences.Editor spe = App.getAppContext().getSharedPreferences(
                IS_FIRST_RUN_KEY, Context.MODE_PRIVATE
        ).edit();

        spe.putBoolean(STARTUP_PROPERTIES_FILENAME, mStartupProperties.isFirstRun());

        spe.apply();
    }

    private void restoreStartupProperties() {
        SharedPreferences sp = App.getAppContext().getSharedPreferences(
                IS_FIRST_RUN_KEY, Context.MODE_PRIVATE
        );

        mStartupProperties = new StartupProperties();
        mStartupProperties.setFirstRun(sp.getBoolean(IS_FIRST_RUN_KEY, true));
    }

    private void startUserSessionRestorationProcess() {
        UserManager manager = UserManager.getUserManager();

        mUserAuthenticationListener = new UserManager.UserAuthenticationListener() {
            @Override
            public void onSessionCreated(User user) {
                mUserRestoreProcessCompleted = true;
                notifyOnStartupCompleted();
            }

            @Override
            public void onSessionRestorationFailure(String error) {
                mUserRestoreProcessCompleted = true;
                notifyOnStartupCompleted();
            }

            @Override
            public void onSessionDestroyed() {
                // Nothing to be done here.
            }
        };

        manager.registerUserAuthenticationListener(mUserAuthenticationListener);
        mUserRestoreProcessCompleted = false;
        manager.asyncRestoreLastSession();
    }


    public interface StartupProcessListener {
        void onStartupCompleted();
    }
}
