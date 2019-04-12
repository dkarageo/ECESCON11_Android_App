package com.sfhmmy.mobile.workshops;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sfhmmy.mobile.R;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.List;


public class WorkshopEventsListAdapter extends ArrayAdapter<WorkshopEvent> {

    public WorkshopEventsListAdapter(Context context, List<WorkshopEvent> workshopEvents) {
        super(context, R.layout.workshop_events_list_item, workshopEvents);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView;

        if (convertView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.workshop_events_list_item, parent, false
            );
        } else itemView = convertView;

        TextView location = itemView.findViewById(R.id.workshop_detail_events_location);
        TextView date = itemView.findViewById(R.id.workshop_detail_events_date);
        TextView time = itemView.findViewById(R.id.workshop_detail_events_time);

        WorkshopEvent curItem = getItem(position);
        location.setText(curItem.getLocation());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        date.setText(curItem.getBeginDate().format(dateFormatter));

        time.setText(String.format(
                "%s - %s",
                curItem.getBeginDate().format(timeFormatter),
                curItem.getEndDate().format(timeFormatter)
        ));

        return itemView;
    }
}
