/*
 * CacheProvider.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.cache;

import com.sfhmmy.mobile.App;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class CacheProvider {

    private static final String CACHE_FOLDER = "/CustomCacheProvider";

    private static CacheProvider mCacheProvider;


    public static CacheProvider getCacheProvider() {
        if (mCacheProvider == null) mCacheProvider = new CacheProvider();
        return mCacheProvider;
    }

    private CacheProvider() {}

    public void storeObject(String key, Serializable object) {
        File cacheDir = getCacheFolder();

        if (cacheDir != null) {
            File cacheFile = new File(cacheDir, key);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
                oos.writeObject(object);
                oos.close();

            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {}
        }
    }

    public Object retrieveObject(String key) {
        File cacheDir = getCacheFolder();
        File cacheFile;
        Object cachedObject = null;

        if (cacheDir != null && (cacheFile = new File(cacheDir.getAbsolutePath(), key)).exists()) {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile));
                cachedObject = ois.readObject();
                ois.close();

            } catch (IOException ex) {
            } catch (ClassNotFoundException ex) {}
        }

        return cachedObject;
    }

    private File getCacheFolder() {
        File cacheDir = new File(App.getAppContext().getCacheDir().getAbsolutePath(), CACHE_FOLDER);

        boolean success = true;
        if (!cacheDir.exists()) success = cacheDir.mkdir();

        return success ? cacheDir : null;
    }
}
