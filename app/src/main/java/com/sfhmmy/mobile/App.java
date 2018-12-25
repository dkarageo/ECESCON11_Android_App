package com.sfhmmy.mobile;

import android.content.res.Resources;
import android.app.Application;

/**
 * Helper class for accessing application's resource from outside of Context classes.
 */
public class App extends Application {
    private static Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
        resources = getResources();
    }

    public static Resources getAppResources() { return resources; }
}
