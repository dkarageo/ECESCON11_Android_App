/*
 * WorkshopsRecyclerAdapter.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.workshops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.R;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class WorkshopsRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Workshop> mWorkshops;

    private WorkshopDetailRequestListener mDetailRequestListener;

    private boolean mDisplayUnloggedUserItem;

    private LoginRequestListener mLoginRequestListener;


    class WorkshopVH extends RecyclerView.ViewHolder {

        TextView  mName;
        ImageView mImage;
        TextView  mLocation;
        TextView  mDateTime;
        CardView  mContainerCard;

        WorkshopVH(View v) {
            super(v);
            mName          = v.findViewById(R.id.workshops_item_name);
            mImage         = v.findViewById(R.id.workshops_item_image);
            mLocation      = v.findViewById(R.id.workshops_item_location);
            mDateTime      = v.findViewById(R.id.workshops_item_date);
            mContainerCard = v.findViewById(R.id.workshops_item_container);
        }
    }

    private class UnloggedUserVH extends RecyclerView.ViewHolder {

        Button mLoginButton;

        UnloggedUserVH(View v) {
            super(v);
            mLoginButton = v.findViewById(R.id.workshops_unlogged_user_notification_login_button);
        }
    }


    WorkshopsRecyclerAdapter(List<Workshop> workshops) {
        mWorkshops = new ArrayList<>();
        updateWorkshopsList(workshops);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;

        switch(viewType) {
            case 0:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.workshops_item_layout, viewGroup, false
                );
                vh = new WorkshopVH(v);
                break;

            case 1:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.workshops_unlogged_user_notification_item_layout, viewGroup, false
                );
                vh = new UnloggedUserVH(v);
                break;

            default:
                throw new RuntimeException("Invalid view type.");
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()) {
            case 0:
                bindWorkshopVH((WorkshopVH) holder, position);
                break;

            case 1:
                bindUnloggedUserVH((UnloggedUserVH) holder, position);
                break;

            default:
                throw new RuntimeException("Invalid view type.");
        }
    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mDisplayUnloggedUserItem && position == 0) return 1;
        else return 0;
    }

    void displayUnloggedUserItem(boolean display) {
        // As long as notification should be visible, keep a null item to image posts list.
        if (display && !mDisplayUnloggedUserItem) mWorkshops.add(0, new Workshop());
        else if (!display && mDisplayUnloggedUserItem) mWorkshops.remove(0);

        mDisplayUnloggedUserItem = display;

        notifyDataSetChanged();
    }

    public void setLoginRequestListener(LoginRequestListener l) {
        mLoginRequestListener = l;
    }

    private void notifyOnLoginRequest() {
        mLoginRequestListener.onLoginRequest();
    }

    public void setWorkshopDetailRequestListener(WorkshopDetailRequestListener l) {
        mDetailRequestListener = l;
    }

    public void notifyOnDetailRequest(Workshop workshop) {
        if (mDetailRequestListener != null) {
            mDetailRequestListener.onWorkshopDetailRequest(workshop);
        }
    }

    void updateWorkshopsList(List<Workshop> workshops) {
        mWorkshops.clear();
        mWorkshops.addAll(workshops);
        if (mDisplayUnloggedUserItem) mWorkshops.add(0, new Workshop());
        notifyDataSetChanged();
    }

    private void bindWorkshopVH(WorkshopVH holder, int position) {
        Workshop curWorkshop = mWorkshops.get(position);

        holder.mName.setText(curWorkshop.getName());
        holder.mLocation.setText(curWorkshop.getPlace());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm  dd-MM-yyyy");
        holder.mDateTime.setText(curWorkshop.getDateTime().format(formatter));

        holder.mContainerCard.setOnClickListener(new WorkshopCardClickListener(curWorkshop));

        Glide.with(holder.mImage.getContext())
                .load(curWorkshop.getImageUrl())
                .fitCenter()
                .into(holder.mImage);
    }

    private void bindUnloggedUserVH(UnloggedUserVH holder, int position) {
        holder.mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyOnLoginRequest();
            }
        });
    }


    public interface WorkshopDetailRequestListener {
        void onWorkshopDetailRequest(Workshop workshop);
    }

    public interface LoginRequestListener {
        void onLoginRequest();
    }

    private class WorkshopCardClickListener implements View.OnClickListener {

        Workshop mTarget;

        WorkshopCardClickListener(Workshop workshop) {
            mTarget = workshop;
        }

        @Override
        public void onClick(View view) {
            notifyOnDetailRequest(mTarget);
        }
    }
}
