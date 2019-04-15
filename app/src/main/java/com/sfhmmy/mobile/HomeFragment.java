/*
 * HomeFragment.java
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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfhmmy.mobile.remoteserver.PhotoWallService;
import com.sfhmmy.mobile.users.User;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends UserAwareFragment {

    private static final String IMAGES_LIST_TAG     = "images_list";
    private static final String SCROLL_POSITION_TAG = "scroll_position";

    private TopLevelFragmentEventsListener mTopListener;

    // Layout views handlers.
    private RecyclerView       mRecyclerView;
    private SwipeRefreshLayout mRefreshWrapper;

    private ArrayList<ImagePost> mImagePosts;  // Posts to be displayed on photo wall.
    private HomeRecyclerViewAdapter mAdapter;

    private PhotoWallService mPhotoWallService;


    public HomeFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TopLevelFragmentEventsListener) {
            mTopListener = (TopLevelFragmentEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implent TopLevelFragmentEventsListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mImagePosts = savedInstanceState.getParcelableArrayList(IMAGES_LIST_TAG);
        } else {
            mImagePosts = new ArrayList<>();
        }

        if (mAdapter == null) {
            // Setup adapter.
            mAdapter = new HomeRecyclerViewAdapter(mImagePosts);
            mAdapter.setLoginRequestListener(new HomeRecyclerViewAdapter.LoginRequestListener() {
                @Override
                public void onLoginRequest() {
                    displayLoginDialog();
                }
            });
            // Initially, display the loading indicator at the end of recycler view. It should be
            // hidden when all content has been loaded from service.
            mAdapter.setMoreContentAfterLoadedContentExists(true);
        }

        if (mPhotoWallService == null) {
            // Setup PhotoWallService.
            mPhotoWallService = new PhotoWallService();
            mPhotoWallService.setPhotoWallServiceListener(new PhotoWallService.PhotoWallServiceListener() {
                @Override
                public void onMoreContentReady(List<ImagePost> morePosts) {
                    for (ImagePost p : morePosts) mAdapter.addImagePost(p);
                    mImagePosts.addAll(morePosts);

                    // Every time new content is loaded, check whether it's the last one or not.
                    // If it's the last one, then stop displaying loading icons.
                    if (mPhotoWallService.hasAllContentBeenLoaded()) {
                        mAdapter.setMoreContentAfterLoadedContentExists(false);
                    }
                }

                @Override
                public void onContentRefreshed(List<ImagePost> newPosts) {
                    mAdapter.clearImagePosts();
                    for (ImagePost p : newPosts) mAdapter.addImagePost(p);
                    mImagePosts.clear();
                    mImagePosts.addAll(newPosts);

                    // Catch the case where content is loaded in a single page.
                    if (mPhotoWallService.hasAllContentBeenLoaded()) {
                        mAdapter.setMoreContentAfterLoadedContentExists(false);
                    }

                    mRecyclerView.smoothScrollToPosition(0);

                    mRefreshWrapper.setRefreshing(false);
                }
            });
            // Initialize service after all views have been created.
            mPhotoWallService.initService();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView   = root.findViewById(R.id.home_recyclerview);
        mRefreshWrapper = root.findViewById(R.id.home_refresh_wrapper);

        // Set color of spinning arrow.
        mRefreshWrapper.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        // Set a listener for handling the user request for content refresh.
        mRefreshWrapper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // When the user asks for content refresh, assume that new content will not be
                // loaded in a single page. If it is, that case will be handled on callback from
                // service object.
                mAdapter.setMoreContentAfterLoadedContentExists(true);

                if (!mPhotoWallService.isRefreshing()) mPhotoWallService.refreshContent();
            }
        });

        // Use a linear layout manager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(
                new PaginationRecyclerViewScrollListener(
                        layoutManager, mPhotoWallService.getPageSize()) {

            @Override
            protected void loadMoreItems() {
                mPhotoWallService.prepareMoreContent();
            }

            @Override
            public boolean isLoading() {
                return mPhotoWallService.isLoading();
            }

            @Override
            public boolean isLastPage() {
                return mPhotoWallService.hasAllContentBeenLoaded();
            }
        });

        if (savedInstanceState != null) {
            int scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_TAG, 0);
            mRecyclerView.scrollToPosition(scrollPosition);
        }

        setRetainInstance(true);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTopListener != null) {
            mTopListener.updateTitle(getString(R.string.home_title));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTopListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.clearOnScrollListeners();
    }

    @Override
    protected void onCreateUserSpecificContent(User user) {
        super.onCreateUserSpecificContent(user);

        if (user == null) mAdapter.displayUnloggedUserItem(true);
        else mAdapter.displayUnloggedUserItem(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(IMAGES_LIST_TAG, mImagePosts);

        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (manager != null) {
            int currentPosition = manager.findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION_TAG, currentPosition);
        }
    }
}
