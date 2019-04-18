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

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.NavigableKey;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.UserAwareFragment;
import com.sfhmmy.mobile.TopLevelFragmentEventsListener;
import com.sfhmmy.mobile.notifications.LocalNotificationsManager;
import com.sfhmmy.mobile.notifications.Notification;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class WorkshopsFragment extends UserAwareFragment {

    private static final String WORKSHOPS_LIST_KEY = "workshops_list";

    private TopLevelFragmentEventsListener mTopListener;

    private RecyclerView             mRecyclerView;
    private SwipeRefreshLayout       mRefresher;

    private WorkshopsRecyclerAdapter mWorkshopsRecyclerAdapter;
    private ArrayList<Workshop>      mWorkshops;

    private boolean mIsRefreshing;
    private final Object  mRefreshLock = new Object();


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
        mRefresher    = root.findViewById(R.id.workshops_refresh_wrapper);

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

        mRefresher.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        mRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchWorkshops(true);
            }
        });

        if (savedInstanceState == null) {
            // The first time fragment is created, fetch the latest copy of workshops.
            fetchWorkshops(false);
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
        fetchWorkshops(false);
    }

    private void updateWorkshopsList(List<Workshop> workshops) {
        mWorkshops.clear();
        mWorkshops.addAll(workshops);
        mWorkshopsRecyclerAdapter.updateWorkshopsList(mWorkshops);

        new WorkshopsNotificationSetter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void fetchWorkshops(boolean forceRefresh) {
        synchronized (mRefreshLock) {
            if (!mIsRefreshing) {
                mIsRefreshing = true;
                new WorkshopsFetcher().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR, this, forceRefresh
                );
            } else {
                mRefresher.setRefreshing(false);
            }
        }
    }

    private static class WorkshopsFetcher extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... args) {

            WorkshopsFragment fragment = (WorkshopsFragment) args[0];
            boolean forceRefresh = (Boolean) args[1];

            User user = UserManager.getUserManager().getCurrentUser();
            RemoteServerProxy.ResponseContainer<List<Workshop>> rc =
                    new RemoteServerProxy().getWorkshopsList(
                            user != null ? user.getToken() : null, forceRefresh
                    );

            return new Object[] { rc, fragment };
        }

        @Override
        protected void onPostExecute(Object[] args) {
            RemoteServerProxy.ResponseContainer<List<Workshop>> rc =
                    (RemoteServerProxy.ResponseContainer<List<Workshop>>) args[0];
            List<Workshop> workshops = rc.getObject();
            WorkshopsFragment target = (WorkshopsFragment) args[1];
            if (target != null && workshops != null) target.updateWorkshopsList(workshops);

            if (target != null) {
                synchronized (target.mRefreshLock) {
                    target.mIsRefreshing = false;
                    target.mRefresher.setRefreshing(false);
                }
            }
        }
    }


    private static class WorkshopEnrollTask extends AsyncTask<Object, Void, Object[]> {

        @Override
        protected Object[] doInBackground(Object... args) {
            Workshop workshop             = (Workshop) args[0];
            String answer                 = (String) args[1];
            WorkshopEvent event           = (WorkshopEvent) args[2];
            WorkshopDetailFragment detail = (WorkshopDetailFragment) args[3];
            WorkshopsFragment listFrag    = (WorkshopsFragment) args[4];

            User user = UserManager.getUserManager().getCurrentUser();
            RemoteServerProxy.ResponseContainer<Workshop> rc = new RemoteServerProxy()
                    .enrollToWorkshop(user != null ? user.getToken() : null, workshop, event, answer);

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
        public void onWorkshopEnrollRequest(Workshop workshop, WorkshopEvent event, String answer) {
            new WorkshopEnrollTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    workshop, answer, event, mTarget, WorkshopsFragment.this
            );
        }
    }

    private class WorkshopsNotificationSetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            LocalNotificationsManager manager = LocalNotificationsManager.getNotificationsManager();

            for (Workshop w : mWorkshops) {
                if (w.getWorkshopEvents() == null || w.getWorkshopEvents().size() == 0) continue;

                if (w.getEnrollStatus() == Workshop.EnrollStatus.ACCEPTED) {

                    int eventCount = 0;

                    for (WorkshopEvent ev : w.getWorkshopEvents()) {

                        Notification n = new Notification();
                        n.setId(10000*w.getId() + eventCount);
                        n.setTitle(App.getAppContext().getString(R.string.workshops_notification_title));
                        n.setBody(String.format(App.getAppContext().getString(R.string.workshops_notification_body),
                                                w.getName()));

                        // Set notification for each
                        n.setTime(ev.getBeginDate().minusHours(1));

                        ++eventCount;

                        manager.sendNotification(n);
                    }
                }
            }

            return null;
        }
    }
}
