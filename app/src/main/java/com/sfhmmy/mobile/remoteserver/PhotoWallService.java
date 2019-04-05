/*
 * PhotoWallService.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.remoteserver;

import android.os.AsyncTask;

import com.sfhmmy.mobile.ImagePost;
import com.sfhmmy.mobile.cache.CacheProvider;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;


public class PhotoWallService {

    private static final int PAGE_SIZE = 2;
    private static final String CACHE_KEY = "remoteserver.PhotoWallService.cacheFile";

    private PhotoWallServiceListener mListener;
    private boolean mIsLoading;
    private boolean mIsRefreshing;
    private final Object mLoadingLock = new Object();
    private int mCurPage = 0;
    private int mLastPage = -1;
    private boolean mIsLoadingFromCache;
    private final Object mCacheLoadingLock = new Object();


    public void initService() {

        // Firstly, restore cached content. This content is expected to be loaded fast.
        loadFromCache();

        // In the meantime, initiate a request to the actual web service.
        loadFromServer(true);
    }

    public void prepareMoreContent() {
        loadFromServer(false);
    }

    public void refreshContent() {
        loadFromServer(true);
    }

    public boolean hasAllContentBeenLoaded() {
        return mLastPage > 0 && mCurPage >= mLastPage;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setPhotoWallServiceListener(PhotoWallServiceListener listener) {
        mListener = listener;
    }

    public int getPageSize() { return PAGE_SIZE; }

    private void notifyOnMoreContentReady(List<ImagePost> morePosts) {
        if (mListener != null) mListener.onMoreContentReady(morePosts);
    }

    private void notifyOnContentRefreshed(List<ImagePost> newPosts) {
        if (mListener != null) mListener.onContentRefreshed(newPosts);
    }

    private void loadFromServer(boolean fromBeginning) {
        synchronized (mLoadingLock) {
            if ((!mIsLoading && !hasAllContentBeenLoaded()) || (fromBeginning && !mIsRefreshing)) {
                mIsLoading = true;
                mIsRefreshing = fromBeginning;
                if (fromBeginning) mCurPage = 0;
                new PhotoWallServerFetcher().execute(mCurPage);
            }
        }
    }

    private void loadFromCache() {
        synchronized (mCacheLoadingLock) {
            mIsLoadingFromCache = true;
            new PhotoWallCacheFetcher().execute();
        }
    }


    public interface PhotoWallServiceListener {
        void onMoreContentReady(List<ImagePost> morePosts);
        void onContentRefreshed(List<ImagePost> newPosts);
    }


    private class PhotoWallCacheFetcher extends AsyncTask<Void, Void, List<ImagePost>> {
        @Override
        protected List<ImagePost> doInBackground(Void... voids) {

            ArrayList<ImagePost> imagePosts =
                    (ArrayList<ImagePost>) CacheProvider.getCacheProvider().retrieveObject(CACHE_KEY);

            return imagePosts;
        }

        @Override
        protected void onPostExecute(List<ImagePost> imagePosts) {
            super.onPostExecute(imagePosts);

            synchronized (mCacheLoadingLock) {
                if (imagePosts != null) notifyOnContentRefreshed(imagePosts);
                mIsLoadingFromCache = false;
                mCacheLoadingLock.notifyAll();
            }
        }
    }


    private class PhotoWallServerFetcher extends AsyncTask<Integer, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Integer... args) {

            int curPage = args[0];  // Current page when async task created.

            // TODO: Fetch posts list from remote API of page curPage + 1.
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {}

            ArrayList<ImagePost> imagePosts = new ArrayList<>();

            ImagePost post1 = new ImagePost();
            post1.setImageUrl("https://sfhmmy.gr/img/pages/conference/organizing_committee/Teams/IT.jpg");
            post1.setDescription("The best IT team ever.");
            post1.setUploader("Ecescon 11 Organizing Committee");
            post1.setUploadedDate(ZonedDateTime.parse(
                    "2019-03-14T01:25:38.492+02:00[Europe/Athens]",
                    DateTimeFormatter.ISO_ZONED_DATE_TIME
            ));

            ImagePost post2 = new ImagePost();
            post2.setImageUrl("https://sfhmmy.gr/img/pages/conference/organizing_committee/Teams/IT.jpg");
            post2.setDescription("Once more, the best IT team ever.");
            post2.setUploader("Ecescon 11 Organizing Committee");
            post2.setUploadedDate(ZonedDateTime.parse(
                    "2019-02-10T12:25:38.492+02:00[Europe/Athens]",
                    DateTimeFormatter.ISO_ZONED_DATE_TIME
            ));

            imagePosts.add(post1);
            imagePosts.add(post2);

            // TODO: Fetch pages count from remote API.
            int lastPage = 5;

            // Always let a simultaneous loading from cache to complete first, so new content
            // overrides cached one.
            synchronized (mCacheLoadingLock) {
                while (mIsLoadingFromCache) {
                    try {
                        mCacheLoadingLock.wait();
                    } catch (InterruptedException ex) {}
                }
            }

            // Store only first page to cache. It's enough.
            if (curPage == 0) {
                CacheProvider.getCacheProvider().storeObject(CACHE_KEY, imagePosts);
            }

            return new Object[] { imagePosts, curPage, lastPage };
        }

        @Override
        protected void onPostExecute(Object[] argsPass) {
            super.onPostExecute(argsPass);

            List<ImagePost> imagePosts = (List<ImagePost>) argsPass[0];
            int curPage = (Integer) argsPass[1];
            int lastPage = (Integer) argsPass[2];

            synchronized (mLoadingLock) {
                // When during fetching of subsequent pages a refresh request was invoked,
                // drop received content and allow only tasks fetching first page to notify for
                // content update.
                if (!(mIsRefreshing && curPage > 0)) {
                    mCurPage = curPage + 1;
                    mLastPage = lastPage;
                    mIsRefreshing = false;
                    mIsLoading = false;

                    // When current page is 0, it means that content is loaded from the beginning.
                    if (curPage == 0) notifyOnContentRefreshed(imagePosts);
                    else notifyOnMoreContentReady(imagePosts);
                }
            }
        }
    }
}
