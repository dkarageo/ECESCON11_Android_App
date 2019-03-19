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


public class StartupManager {

    private static final String STARTUP_PROPERTIES_FILENAME
            = "com.sfhmmy.mobily.startups.StartupManager.StartupPropertiesFile";
    private static final String IS_FIRST_RUN_KEY = "is_first_run";

    private static StartupManager mActiveStartupManager;

    private StartupProperties mStartupProperties;


    public static StartupManager getStartupManager() {
        if (mActiveStartupManager == null) mActiveStartupManager = new StartupManager();
        return mActiveStartupManager;
    }

    public boolean isFirstRun() {
        return mStartupProperties.isFirstRun();
    }

    public void terminateFirstRun() {
        mStartupProperties.setFirstRun(false);
        saveStartupProperties();
    }

    private StartupManager() {
        restoreStartupProperties();
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
}
