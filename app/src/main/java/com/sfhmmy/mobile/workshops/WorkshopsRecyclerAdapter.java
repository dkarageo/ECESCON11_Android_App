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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.R;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class WorkshopsRecyclerAdapter
        extends RecyclerView.Adapter<WorkshopsRecyclerAdapter.WorkshopVH> {

    private List<Workshop> mWorkshops;


    class WorkshopVH extends RecyclerView.ViewHolder {

        TextView  mName;
        ImageView mImage;
        TextView  mLocation;
        TextView  mDateTime;

        WorkshopVH(View v) {
            super(v);
            mName     = v.findViewById(R.id.workshops_item_name);
            mImage    = v.findViewById(R.id.workshops_item_image);
            mLocation = v.findViewById(R.id.workshops_item_location);
            mDateTime = v.findViewById(R.id.workshops_item_date);
        }
    }


    WorkshopsRecyclerAdapter(List<Workshop> workshops) {
        mWorkshops = workshops;
    }

    @NonNull
    @Override
    public WorkshopVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.workshops_item_layout, viewGroup, false
        );
        return new WorkshopVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkshopVH holder, int position) {
        Workshop curWorkshop = mWorkshops.get(position);

        holder.mName.setText(curWorkshop.getName());
        holder.mLocation.setText(curWorkshop.getPlace());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm  dd-MM-yyyy");
        holder.mDateTime.setText(curWorkshop.getDateTime().format(formatter));

        Glide.with(holder.mImage.getContext())
                .load(curWorkshop.getImageUrl())
                .fitCenter()
                .into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    void updateWorkshopsList(List<Workshop> workshops) {
        mWorkshops = workshops;
        notifyDataSetChanged();
    }
}
