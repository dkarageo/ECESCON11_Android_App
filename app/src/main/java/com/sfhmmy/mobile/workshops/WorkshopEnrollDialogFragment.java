/*
 * WorkshopEnrollDialogFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.workshops;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sfhmmy.mobile.R;


public class WorkshopEnrollDialogFragment extends DialogFragment {

    private static final String WORKSHOP_PARCEL_KEY = "workshop_parcel";

    private AppCompatActivity mAttachedActivity = null;

    private TextView mQuestion;
    private EditText mAnswer;

    private Workshop mWorkshop;

    private EnrollRequestListener mEnrollRequestListener;


    public static WorkshopEnrollDialogFragment newInstance(Workshop workshop) {
        WorkshopEnrollDialogFragment fragment = new WorkshopEnrollDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(WORKSHOP_PARCEL_KEY, workshop);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkshopEnrollDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mAttachedActivity = (AppCompatActivity) context;
        } else {
            throw new RuntimeException(
                    "Workshop Enroll dialog fragment should be attach to an AppCompatActivity");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mWorkshop = getArguments().getParcelable(WORKSHOP_PARCEL_KEY);
        }
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(mAttachedActivity);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.workshop_enroll_dialog_layout, null);

        mQuestion = root.findViewById(R.id.workshop_enroll_dialog_question);
        mAnswer   = root.findViewById(R.id.workshop_enroll_dialog_answer);

        mQuestion.setText(mWorkshop.getJoinQuestion());

        builder.setView(root)
               .setPositiveButton(R.string.workshop_enroll_dialog_enroll_button_text, null)
               .setNegativeButton(R.string.workshop_enroll_dialog_cancel_button_text, null);
        AlertDialog dialog = builder.create();

        // Don't implement DialogInterface.OnClickDialog for positive button, because it always
        // closes dialog popup window. Instead, implement directly the OnClickListener of the
        // button and verify the answer.
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                final AlertDialog alertDialog = (AlertDialog) dialogInterface;
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (mAnswer.getText().toString().length() < 1) {
                            mAnswer.setError(
                                    getString(R.string.workshop_enroll_dialog_answer_needed)
                            );
                        } else {
                            notifyOnEnrollRequest();
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public void setEnrollRequestListener(EnrollRequestListener l) {
        mEnrollRequestListener = l;
    }

    public void notifyOnEnrollRequest() {
        if (mEnrollRequestListener != null) {
            mEnrollRequestListener.onEnrollRequest(mWorkshop, mAnswer.getText().toString());
        }
    }


    public interface EnrollRequestListener {
        void onEnrollRequest(Workshop workshop, String answer);
    }
}
