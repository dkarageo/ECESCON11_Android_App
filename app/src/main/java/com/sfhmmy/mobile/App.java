package com.sfhmmy.mobile;

import android.content.Context;
import android.content.res.Resources;
import android.app.Application;

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
    }

    public static Resources getAppResources() { return resources; }
    public static Context getAppContext() { return context; }
}
