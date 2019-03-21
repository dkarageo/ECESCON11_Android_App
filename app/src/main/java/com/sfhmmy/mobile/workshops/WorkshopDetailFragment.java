/*
 * WorkshopDetailFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.workshops;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sfhmmy.mobile.R;

import org.threeten.bp.format.DateTimeFormatter;


public class WorkshopDetailFragment extends Fragment {

    private static final String WORKSHOP_PARCEL_KEY = "workshop_parcel";

    private ImageView mEnrollStatusIcon;
    private TextView  mEnrollStatus;
    private ImageView mWorkshopImage;
    private TextView  mLocation;
    private TextView  mDate;
    private TextView  mDescription;
    private Button    mEnrollButton;

    private Workshop  mWorkshop;  // Workshop to display.

    private WorkshopEnrollListener mEnrollListener;


    public static WorkshopDetailFragment newInstance(Workshop workshop) {
        WorkshopDetailFragment fragment = new WorkshopDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(WORKSHOP_PARCEL_KEY, workshop);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkshopDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mWorkshop = savedInstanceState.getParcelable(WORKSHOP_PARCEL_KEY);
        } else if (getArguments() != null) {
            mWorkshop = getArguments().getParcelable(WORKSHOP_PARCEL_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_workshop_detail, container, false);

        mEnrollStatusIcon = root.findViewById(R.id.workshop_detail_enroll_status_icon);
        mEnrollStatus     = root.findViewById(R.id.workshop_detail_enroll_status);
        mWorkshopImage    = root.findViewById(R.id.workshop_detail_image);
        mLocation         = root.findViewById(R.id.workshop_detail_location);
        mDate             = root.findViewById(R.id.workshop_detail_date);
        mDescription      = root.findViewById(R.id.workshop_detail_description);
        mEnrollButton     = root.findViewById(R.id.workshop_detail_enroll_button);

        mEnrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When enroll button is clicked, a popup dialog shows up in order to complete
                // enroll form.
                WorkshopEnrollDialogFragment dialog =
                        WorkshopEnrollDialogFragment.newInstance(mWorkshop);
                dialog.setEnrollRequestListener(
                        new WorkshopEnrollDialogFragment.EnrollRequestListener() {
                            @Override
                            public void onEnrollRequest(Workshop workshop, String answer) {
                                // When an enroll request takes place on popup dialog, just inform
                                // the registered listener.
                                notifyOnEnrollRequest(workshop, answer);
                            }
                });

                if (getFragmentManager() != null) {
                    dialog.show(getFragmentManager(), "enrollDialog");
                }
            }
        });

        displayWorkshop(mWorkshop);

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(WORKSHOP_PARCEL_KEY, mWorkshop);
    }

    public void setWorkshopEnrollListener(WorkshopEnrollListener l) {
        mEnrollListener = l;
    }

    public void notifyOnEnrollRequest(Workshop workshop, String answer) {
        if (mEnrollListener != null) mEnrollListener.onWorkshopEnrollRequest(workshop, answer);
    }

    public void updateWorkshop(Workshop workshop) {
        mWorkshop = workshop;
        displayWorkshop(mWorkshop);
    }

    private void displayWorkshop(Workshop workshop) {

        if (workshop.getPlace() != null) mLocation.setText(workshop.getPlace());
        if (workshop.getDescription() != null) mDescription.setText(workshop.getDescription());

        if (workshop.getDateTime() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm  dd-MM-yyyy");
            mDate.setText(workshop.getDateTime().format((formatter)));
        }

        if (workshop.getEnrollStatus() != null) setEnrollStatus(workshop.getEnrollStatus());
        else setEnrollStatus(Workshop.EnrollStatus.UNAVAILABLE);

        if (workshop.getImageUrl() != null) {
            Glide.with(mWorkshopImage.getContext()).load(workshop.getImageUrl()).into(mWorkshopImage);
        }
    }

    private void setEnrollStatus(Workshop.EnrollStatus status) {

        int iconRes;
        int textRes;
        int colorRes;

        boolean displayEnrollButton;

        switch (status) {
            case AVAILABLE:
                iconRes  = R.drawable.question_mark;
                textRes  = R.string.workshop_detail_enroll_status_available_text;
                colorRes = R.color.statusNeutralColor;
                displayEnrollButton = true;
                break;

            case UNAVAILABLE:
                iconRes  = R.drawable.cross_mark;
                textRes  = R.string.workshop_detail_enroll_status_unavailable_text;
                colorRes = R.color.statusNegativeColor;
                displayEnrollButton = false;
                break;

            case ACCEPTED:
                iconRes  = R.drawable.check_mark;
                textRes  = R.string.workshop_detail_enroll_status_accepted_text;
                colorRes = R.color.statusPositiveColor;
                displayEnrollButton = false;
                break;

            case REJECTED:
                iconRes  = R.drawable.cross_mark;
                textRes  = R.string.workshop_detail_enroll_status_rejected_text;
                colorRes = R.color.statusNegativeColor;
                displayEnrollButton = false;
                break;

            case PENDING:
                iconRes  = R.drawable.question_mark;
                textRes  = R.string.workshop_detail_enroll_status_pending_text;
                colorRes = R.color.statusUnknownColor;
                displayEnrollButton = false;
                break;

            default:
                throw new RuntimeException("Invalid workshop enroll status");
        }

        // Set icon with tint, supporting pre-lollipop devices too.
        Drawable icon = DrawableCompat.wrap(getResources().getDrawable(iconRes)).mutate();
        DrawableCompat.setTint(icon, getResources().getColor(colorRes));
        mEnrollStatusIcon.setImageDrawable(icon);

        // Set colored text.
        mEnrollStatus.setText(getText(textRes));
        mEnrollStatus.setTextColor(getResources().getColor(colorRes));

        if (displayEnrollButton) mEnrollButton.setVisibility(View.VISIBLE);
        else mEnrollButton.setVisibility(View.GONE);
    }


    public interface WorkshopEnrollListener {
        void onWorkshopEnrollRequest(Workshop workshop, String answer);
    }
}
