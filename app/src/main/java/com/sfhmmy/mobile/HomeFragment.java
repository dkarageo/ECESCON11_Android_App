/*
 * HomeFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private TopLevelFragmentEventsListener mTopListener;

    private RecyclerView mRecyclerView;

    private List<ImagePost> mImagePosts;  // Posts to be displayed on photo wall.
    private HomeRecyclerViewAdapter mAdapter;


    public HomeFragment() {
        mImagePosts = new ArrayList<>();
    }

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = root.findViewById(R.id.home_recyclerview);
        if (mRecyclerView == null) {
            throw new RuntimeException("Unable to fetch home screen recycler view.");
        }
        // Use a linear layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HomeRecyclerViewAdapter(mImagePosts);
        mRecyclerView.setAdapter(mAdapter);

        // Ask to fetch latests posts.
        new PhotoWallPostsFetcher().execute(this);

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

    private void updateImagePosts(List<ImagePost> imagePosts) {
        mImagePosts.clear();
        mImagePosts.addAll(imagePosts);
        mAdapter.updateImagePosts(mImagePosts);
    }


    private static class PhotoWallPostsFetcher extends AsyncTask<HomeFragment, Void, Object[]> {

        @Override
        protected Object[] doInBackground(HomeFragment... homeFragments) {
            RemoteServerProxy.ResponseContainer rc = new RemoteServerProxy().getPhotoWallPosts(
                    com.sfhmmy.mobile.users.UserManager.getUserManager().getCurrentUser().getToken()
            );
            return new Object[] { rc, homeFragments[0] };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            RemoteServerProxy.ResponseContainer<List<ImagePost>> rc =
                    (RemoteServerProxy.ResponseContainer<List<ImagePost>>) args[0];
            HomeFragment fragToUpdate = (HomeFragment) args[1];
            List<ImagePost> imagePosts = rc.getObject();
            if (imagePosts != null) fragToUpdate.updateImagePosts(imagePosts);
        }
    }
}
