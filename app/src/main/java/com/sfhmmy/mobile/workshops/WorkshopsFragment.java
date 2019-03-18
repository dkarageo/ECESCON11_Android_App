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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.UserManager;

import java.util.ArrayList;
import java.util.List;


public class WorkshopsFragment extends Fragment {

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
            public void onWorkshopDetailRequest(Workshop workshop) {
                WorkshopDetailFragment detailFrag = WorkshopDetailFragment.newInstance(workshop);
                detailFrag.setWorkshopEnrollListener(new LocalWorkshopEnrollListener(detailFrag));

                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.replace(R.id.main_fragment_container, detailFrag);
                t.addToBackStack("workshopDetailFragment");
                t.commit();

                // No navigation elements should be element on detail page.
                mTopListener.hideNavigationBar();
            }
        });
        mRecyclerView.setAdapter(mWorkshopsRecyclerAdapter);

        if (savedInstanceState == null) {
            // The first time fragment is created, fetch the latest copy of workshops.
            new WorkshopsFetcher().execute(this);
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

    private static class WorkshopsFetcher extends AsyncTask<WorkshopsFragment, Void, Object[]> {

        @Override
        protected Object[] doInBackground(WorkshopsFragment... workshopsFragments) {
            return new Object[] {
                    new RemoteServerProxy().getWorkshopsList(
                            UserManager.getUserManager().getCurrentUser().getToken()),
                    workshopsFragments[0]
            };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            RemoteServerProxy.ResponseContainer<List<Workshop>> rc =
                    (RemoteServerProxy.ResponseContainer<List<Workshop>>) args[0];
            List<Workshop> workshops = rc.getObject();
            WorkshopsFragment target = (WorkshopsFragment) args[1];
            target.updateWorkshopsList(workshops);
        }
    }


    private static class WorkshopEnrollTask extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... args) {
            Workshop workshop             = (Workshop) args[0];
            String answer                 = (String) args[1];
            WorkshopDetailFragment detail = (WorkshopDetailFragment) args[2];
            WorkshopsFragment listFrag    = (WorkshopsFragment) args[3];

            RemoteServerProxy.ResponseContainer<Workshop> rc = new RemoteServerProxy()
                    .enrollToWorkshop(
                        UserManager.getUserManager().getCurrentUser().getToken(),
                        workshop, answer
                    );

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
