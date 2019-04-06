/*
 * WorkshopsFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.workshops;

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

import com.sfhmmy.mobile.NavigableKey;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.UserAwareFragment;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;

import java.util.ArrayList;
import java.util.List;


public class WorkshopsFragment extends UserAwareFragment {

    private static final String WORKSHOPS_LIST_KEY = "workshops_list";

    private TopLevelFragmentEventsListener mTopListener;

    private RecyclerView             mRecyclerView;
    private WorkshopsRecyclerAdapter mWorkshopsRecyclerAdapter;
    private ArrayList<Workshop>      mWorkshops;


    public WorkshopsFragment() {
        mWorkshops = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TopLevelFragmentEventsListener) {
            mTopListener = (TopLevelFragmentEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TopLevelFragmentEventsListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_workshops, container, false);

        mRecyclerView = root.findViewById(R.id.workshops_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mWorkshopsRecyclerAdapter = new WorkshopsRecyclerAdapter(mWorkshops);
        mWorkshopsRecyclerAdapter.setWorkshopDetailRequestListener(
                new WorkshopsRecyclerAdapter.WorkshopDetailRequestListener() {
            @Override
            public void onWorkshopDetailRequest(final Workshop workshop) {

                if (mTopListener != null) {
                    mTopListener.navigateToNavigableKey(new NavigableKey() {
                        Workshop bindedWorkshop = workshop;

                        @Override
                        public String getKey() {
                            return String.format("workshop_detail_%d", workshop.getId());
                        }

                        @Override
                        public Fragment createFragment() {
                            WorkshopDetailFragment detailFrag
                                    = WorkshopDetailFragment.newInstance(bindedWorkshop);
                            detailFrag.setWorkshopEnrollListener(
                                    new LocalWorkshopEnrollListener(detailFrag)
                            );
                            return detailFrag;
                        }
                    }, false);
                }

                // No navigation elements should be element on detail page.
                mTopListener.hideNavigationBar();
            }
        });
        mWorkshopsRecyclerAdapter.setLoginRequestListener(
                new WorkshopsRecyclerAdapter.LoginRequestListener() {
            @Override
            public void onLoginRequest() {
                displayLoginDialog();
            }
        });
        mRecyclerView.setAdapter(mWorkshopsRecyclerAdapter);

        if (savedInstanceState == null) {
            // The first time fragment is created, fetch the latest copy of workshops.
            fetchWorkshops();
        } else {
            ArrayList<Workshop> restored
                    = savedInstanceState.getParcelableArrayList(WORKSHOPS_LIST_KEY);
            updateWorkshopsList(restored);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mTopListener != null) {
            mTopListener.updateTitle(getString(R.string.workshops_title));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Make sure that nav bar is visible again, once returned from detail fragment.
        mTopListener.showNavigationBar();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mTopListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(WORKSHOPS_LIST_KEY, mWorkshops);
    }

    @Override
    protected void onCreateUserSpecificContent(User user) {
        super.onCreateUserSpecificContent(user);

        if (user == null) mWorkshopsRecyclerAdapter.displayUnloggedUserItem(true);
        else mWorkshopsRecyclerAdapter.displayUnloggedUserItem(false);

        // Every time the user changes, fetch workshops again, so their state keeps consistent with
        // the new user.
        fetchWorkshops();
    }

    private void updateWorkshopsList(List<Workshop> workshops) {
        mWorkshops.clear();
        mWorkshops.addAll(workshops);
        mWorkshopsRecyclerAdapter.updateWorkshopsList(mWorkshops);
    }

    private void updateWorkshop(Workshop updated) {
        for (int i = 0; i < mWorkshops.size(); ++i) {
            Workshop w = mWorkshops.get(i);
            if (w.getId() == updated.getId()) {
                mWorkshops.set(i, updated);
                break;
            }
        }
        mWorkshopsRecyclerAdapter.updateWorkshopsList(mWorkshops);
    }

    private void fetchWorkshops() {
        new WorkshopsFetcher().execute(this);
    }

    private static class WorkshopsFetcher extends AsyncTask<WorkshopsFragment, Void, Object[]> {

        @Override
        protected Object[] doInBackground(WorkshopsFragment... workshopsFragments) {
            User user = UserManager.getUserManager().getCurrentUser();
            RemoteServerProxy.ResponseContainer<List<Workshop>> rc =
                    new RemoteServerProxy().getWorkshopsList(user != null ? user.getToken() : null);

            return new Object[] { rc, workshopsFragments[0] };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            RemoteServerProxy.ResponseContainer<List<Workshop>> rc =
                    (RemoteServerProxy.ResponseContainer<List<Workshop>>) args[0];
            List<Workshop> workshops = rc.getObject();
            WorkshopsFragment target = (WorkshopsFragment) args[1];
            if (target != null && workshops != null) target.updateWorkshopsList(workshops);
        }
    }


    private static class WorkshopEnrollTask extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... args) {
            Workshop workshop             = (Workshop) args[0];
            String answer                 = (String) args[1];
            WorkshopDetailFragment detail = (WorkshopDetailFragment) args[2];
            WorkshopsFragment listFrag    = (WorkshopsFragment) args[3];

            User user = UserManager.getUserManager().getCurrentUser();
            RemoteServerProxy.ResponseContainer<Workshop> rc = new RemoteServerProxy()
                    .enrollToWorkshop(user != null ? user.getToken() : null, workshop, answer);

            return new Object[] { rc, detail, listFrag };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            super.onPostExecute(args);

            RemoteServerProxy.ResponseContainer<Workshop> rc
                    = (RemoteServerProxy.ResponseContainer<Workshop>) args[0];
            Workshop updatedWorkshop      = rc.getObject();
            WorkshopDetailFragment detail = (WorkshopDetailFragment) args[1];
            WorkshopsFragment listFrag    = (WorkshopsFragment) args[2];

            detail.updateWorkshop(updatedWorkshop);
            listFrag.updateWorkshop(updatedWorkshop);
        }
    }


    private class LocalWorkshopEnrollListener
            implements WorkshopDetailFragment.WorkshopEnrollListener {

        private WorkshopDetailFragment mTarget;

        LocalWorkshopEnrollListener(WorkshopDetailFragment target) {
            mTarget = target;
        }

        @Override
        public void onWorkshopEnrollRequest(Workshop workshop, String answer) {
            new WorkshopEnrollTask().execute(workshop, answer, mTarget, WorkshopsFragment.this);
        }
    }
}
