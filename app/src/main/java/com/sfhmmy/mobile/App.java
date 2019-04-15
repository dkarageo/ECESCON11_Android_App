/*
 * App.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.2
 */


package com.sfhmmy.mobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.app.Application;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Locale;

/**
 * Helper class for accessing application's resource from outside of Context classes.
 */
public class App extends Application {
    private static Resources resources;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
        context = getApplicationContext();

        // Initialize timezone information of AndroidThreeTen library.
        AndroidThreeTen.init(this);

        restoreLocale();
    }

    public static Resources getAppResources() { return resources; }
    public static Context getAppContext() { return context; }

    public static void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = App.getAppResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        // Check that new locale is different from the current one.
        if (!conf.locale.getDisplayLanguage().equals(myLocale.getDisplayLanguage())) {
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        }
    }

    private void restoreLocale() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        String locale = sp.getString("preferences_locale_selector", "");

        if (!TextUtils.isEmpty(locale)) setLocale(locale);
    }
}
