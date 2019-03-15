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

import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.UserManager;

import java.util.ArrayList;
import java.util.List;


public class WorkshopsFragment extends Fragment {

    private TopLevelFragmentEventsListener mTopListener;

    private RecyclerView             mRecyclerView;
    private WorkshopsRecyclerAdapter mWorkshopsRecyclerAdapter;
    private List<Workshop>           mWorkshops;


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
        mRecyclerView.setAdapter(mWorkshopsRecyclerAdapter);

        // Each time view is created fetch the latest copy of workshops.
        new WorkshopsFetcher().execute(this);

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
    public void onDetach() {
        super.onDetach();
        mTopListener = null;
    }

    private void updateWorkshopsList(List<Workshop> workshops) {
        mWorkshops.clear();
        mWorkshops.addAll(workshops);
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
}
