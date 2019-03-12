/*
 * UsersListRecyclerAdapter.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.checkins;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.users.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class UsersListRecyclerAdapter
        extends RecyclerView.Adapter<UsersListRecyclerAdapter.UserViewHolder>
        implements Filterable {

    // A list containing all users to be displayed (after filtering).
    private List<User> mUsersToDisplay;

    private List<User>   mOriginalUsers;  // A list containing all users available to this adapter.
    private UsersFilter  mFilter;
    private CharSequence mActiveFilter;

    private OnCheckInRequestListener mCheckInRequestListener;


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public CardView mItem;
        public TextView mNameSurname;
        public TextView mEmail;
        public Button   mCheckInButton;
        public View     mContentArea;
        public TextView mLastCheckIn;

        public UserViewHolder(View v) {
            super(v);
            mItem          = v.findViewById(R.id.checkin_users_list_item);
            mNameSurname   = v.findViewById(R.id.checkin_users_list_item_name_surname);
            mEmail         = v.findViewById(R.id.checkin_users_list_item_email);
            mCheckInButton = v.findViewById(R.id.checkin_users_list_item_checkin_button);
            mContentArea   = v.findViewById(R.id.checkin_users_list_item_content);
            mLastCheckIn   = v.findViewById(R.id.checkin_users_list_item_last_checkin_date_text);
        }
    }


    public UsersListRecyclerAdapter(List<User> users) {
        mOriginalUsers  = users;
        mUsersToDisplay = users;
        mFilter         = new UsersFilter();
        mActiveFilter   = null;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.checkin_users_list_item_layout, viewGroup, false
        );

        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User curUser = mUsersToDisplay.get(position);

        // Update contents of given user.
        holder.mNameSurname.setText(String.format("%s %s", curUser.getName(), curUser.getSurname()));
        holder.mEmail.setText(curUser.getEmail());

        Date lastCheckInDate = curUser.getLastCheckInDate();
        holder.mLastCheckIn.setText(
                lastCheckInDate == null ?
                        App.getAppResources().getString(R.string.checkin_users_list_item_no_checkin) :
                        new SimpleDateFormat("hh:mm dd-MM-yyyy")
                                .format(curUser.getLastCheckInDate())
        );

        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View content = v.findViewById(R.id.checkin_users_list_item_content);
                content.setVisibility(
                        content.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        holder.mCheckInButton.setOnClickListener(new CheckInButtonListener(curUser));
    }

    @Override
    public int getItemCount() {
        return mUsersToDisplay.size();
    }

    @Override
    public Filter getFilter() { return mFilter; }

    public void updateDataset(List<User> users) {
        mOriginalUsers = users;
        mFilter.filter(mActiveFilter);
    }

    void setOnCheckInRequesListener(OnCheckInRequestListener l) {
        mCheckInRequestListener = l;
    }

    void notifyOnCheckInRequest(User user) {
        mCheckInRequestListener.onCheckInRequested(user);
    }


    interface OnCheckInRequestListener {
        void onCheckInRequested(User user);
    }


    private class UsersFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // Save current filter, to replay it when updating underlying dataset.
            mActiveFilter = constraint;

            FilterResults results = new FilterResults();

            if (constraint == null || constraint.toString().equals("")) {
                results.values = mOriginalUsers;
                results.count  = mOriginalUsers.size();
            } else {

                List<User> filteredUsers = new ArrayList<>();
                String stringConstraint = constraint.toString();

                // Search given filter in full name and email of the users.
                for (User u : mOriginalUsers) {
                    String fullName = String.format("%s %s", u.getName(), u.getSurname());
                    String email    = u.getEmail();

                    if (fullName.toLowerCase().contains(stringConstraint.toLowerCase()) ||
                            email.toLowerCase().contains(stringConstraint.toLowerCase())) {
                        filteredUsers.add(u);
                    }
                }

                results.values = filteredUsers;
                results.count  = filteredUsers.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            mUsersToDisplay = (List<User>) results.values;
            notifyDataSetChanged();
        }
    }


    private class CheckInButtonListener implements View.OnClickListener {
        User mAttachedUser;

        public CheckInButtonListener(User user) {
            mAttachedUser = user;
        }

        public void onClick(View v) {
            notifyOnCheckInRequest(mAttachedUser);
        }
    }
}
