/*
 * UsersListFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.checkins;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserProfileFragment;

import java.util.ArrayList;
import java.util.List;


public class UsersListFragment extends Fragment {

    private RecyclerView             mRecyclerView;
    private UsersListRecyclerAdapter mAdapter;
    private View                     mErrorWrapper;
    private TextView                 mErrorText;

    private List<User> mUsers;

    private SearchView mSearchBar;

    private List<OnCheckInRequestListener> mCheckInRequestListeners;
    private OnViewProfileRequestListener mViewProfileRequestListener;


    public UsersListFragment() {
        mUsers = new ArrayList<>();
        mCheckInRequestListeners = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_users_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.checkin_users_list);
        if (mRecyclerView == null) {
            throw new RuntimeException("Unable to fetch recycler view for users.");
        }
        // Use a linear layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new UsersListRecyclerAdapter(mUsers);
        mAdapter.setOnCheckInRequesListener(new UsersListRecyclerAdapter.OnCheckInRequestListener() {
            @Override
            public void onCheckInRequested(User user, String dayTag) {
                notifyOnCheckInRequest(user, dayTag);
            }
        });
        mAdapter.setOnViewProfileRequestListener(new UsersListRecyclerAdapter.OnViewProfileRequestListener() {
            @Override
            public void onViewProfile(User user) {
                notifyOnViewProfileRequest(user);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mSearchBar = rootView.findViewById(R.id.checkin_users_search_bar);
        mSearchBar.setOnQueryTextListener(new SearchListener());

        mErrorWrapper = rootView.findViewById(R.id.checkin_users_list_warning_area);
        mErrorText = rootView.findViewById(R.id.checkin_users_list_warning_text);

        return rootView;
    }

    public void updateUsersList(List<User> users) {
        if (users != null) {
            mUsers.clear();
            mUsers.addAll(users);
            mAdapter.updateDataset(mUsers);
        }
    }

    public void displayError(String message, boolean isWarning) {
        if (message != null) {
            mErrorWrapper.setVisibility(View.VISIBLE);
            mErrorText.setText(message);

            if (isWarning) {
                mErrorWrapper.setBackgroundColor(
                        getResources().getColor(R.color.warningBackgroundColor));
                mErrorText.setTextColor(
                        getResources().getColor(R.color.warningTextOnWarningBackgroundColor));
            } else {
                mErrorWrapper.setBackgroundColor(
                        getResources().getColor(R.color.errorBackgroundColor));
                mErrorText.setTextColor(
                        getResources().getColor(R.color.errorTextOnErrorBackgroundColor));
            }
        } else {
            mErrorWrapper.setVisibility(View.GONE);
        }
    }

    public void registerOnCheckInRequestListener(OnCheckInRequestListener l) {
        mCheckInRequestListeners.add(l);
    }

    public void removeOnCheckInRequestListener(OnCheckInRequestListener l) {
        mCheckInRequestListeners.remove(l);
    }

    public void notifyOnCheckInRequest(User user, String dayTag) {
        for (OnCheckInRequestListener l : mCheckInRequestListeners) {
            l.onCheckInRequested(user, dayTag);
        }
    }

    public void setOnViewProfileRequestListener(OnViewProfileRequestListener l) {
        mViewProfileRequestListener = l;
    }

    public void notifyOnViewProfileRequest(User user) {
        if (mViewProfileRequestListener != null) {
            mViewProfileRequestListener.onViewProfileRequested(user);
        }
    }


    class SearchListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            mAdapter.getFilter().filter(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mAdapter.getFilter().filter(newText);
            return false;
        }
    }


    interface OnViewProfileRequestListener {
        void onViewProfileRequested(User user);
    }


    interface OnCheckInRequestListener {
        void onCheckInRequested(User user, String dayTag);
    }
}
