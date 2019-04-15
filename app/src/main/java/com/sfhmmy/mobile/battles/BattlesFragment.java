/*
 * BattlesFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.battles;

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

import com.sfhmmy.mobile.PaginationRecyclerViewScrollListener;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;
import com.sfhmmy.mobile.UserAwareFragment;
import com.sfhmmy.mobile.remoteserver.PhotoshopBattlesService;
import com.sfhmmy.mobile.users.User;

import java.util.ArrayList;
import java.util.List;


public class BattlesFragment extends UserAwareFragment {

    private static final String IMAGES_LIST_TAG     = "images_list";
    private static final String SCROLL_POSITION_TAG = "scroll_position";

    private TopLevelFragmentEventsListener mTopListener;

    // Layout views handlers.
    private RecyclerView       mRecyclerView;
    private SwipeRefreshLayout mRefreshWrapper;

    private ArrayList<BattlesPost> mBattlesPosts;  // Posts to be displayed on photo wall.
    private BattlesRecyclerViewAdapter mAdapter;

    private PhotoshopBattlesService mBattlesService;


    public BattlesFragment() {}

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
            mBattlesPosts = savedInstanceState.getParcelableArrayList(IMAGES_LIST_TAG);
        } else {
            mBattlesPosts = new ArrayList<>();
        }

        if (mAdapter == null) {
            // Setup adapter.
            mAdapter = new BattlesRecyclerViewAdapter(mBattlesPosts);
            mAdapter.setLoginRequestListener(new BattlesRecyclerViewAdapter.LoginRequestListener() {
                @Override
                public void onLoginRequest() {
                    displayLoginDialog();
                }
            });
            // Initially, display the loading indicator at the end of recycler view. It should be
            // hidden when all content has been loaded from service.
            mAdapter.setMoreContentAfterLoadedContentExists(true);

            // Setup PhotoshopBattlesService.
            mBattlesService = new PhotoshopBattlesService();
            mBattlesService.setPhotoshopBattlesServiceListener(new PhotoshopBattlesService.PhotoshopBattlesServiceListener() {
                @Override
                public void onMoreContentReady(List<BattlesPost> morePosts) {
                    for (BattlesPost p : morePosts) mAdapter.addBattlesPost(p);
                    mBattlesPosts.addAll(morePosts);

                    // Every time new content is loaded, check whether it's the last one or not.
                    // If it's the last one, then stop displaying loading icons.
                    if (mBattlesService.hasAllContentBeenLoaded()) {
                        mAdapter.setMoreContentAfterLoadedContentExists(false);

                    }
                }

                @Override
                public void onContentRefreshed(List<BattlesPost> newPosts) {
                    mAdapter.clearBattlesPosts();
                    for (BattlesPost p : newPosts) mAdapter.addBattlesPost(p);
                    mBattlesPosts.clear();
                    mBattlesPosts.addAll(newPosts);

                    // Catch the case where content is loaded in a single page.
                    if (mBattlesService.hasAllContentBeenLoaded()) {
                        mAdapter.setMoreContentAfterLoadedContentExists(false);
                    }

                    mRecyclerView.smoothScrollToPosition(0);

                    mRefreshWrapper.setRefreshing(false);
                }
            });
            // Initialize service after all views have been created.
            mBattlesService.initService();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_battles, container, false);

        mRecyclerView   = root.findViewById(R.id.battles_recyclerview);
        mRefreshWrapper = root.findViewById(R.id.battles_refresh_wrapper);

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

                if (!mBattlesService.isRefreshing()) mBattlesService.refreshContent();
            }
        });

        // Use a linear layout manager.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(
                new PaginationRecyclerViewScrollListener(
                        layoutManager, mBattlesService.getPageSize()) {

                    @Override
                    protected void loadMoreItems() {
                        mBattlesService.prepareMoreContent();
                    }

                    @Override
                    public boolean isLoading() {
                        return mBattlesService.isLoading();
                    }

                    @Override
                    public boolean isLastPage() {
                        return mBattlesService.hasAllContentBeenLoaded();
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
            mTopListener.updateTitle(getString(R.string.battles_title));
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

        // Currently, there is no user specific content, so no need to display warning dialog.
//        if (user == null) mAdapter.displayUnloggedUserItem(true);
//        else mAdapter.displayUnloggedUserItem(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(IMAGES_LIST_TAG, mBattlesPosts);

        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (manager != null) {
            int currentPosition = manager.findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION_TAG, currentPosition);
        }
    }
}
