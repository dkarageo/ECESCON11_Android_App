/*
 * CheckInDialogFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.checkins;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.users.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import cdflynn.android.library.checkview.CheckView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class CheckInStatusDialogFragment extends DialogFragment {

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_WARNING = 2;
    public static final int STATUS_LOADING = 3;

    private AppCompatActivity mAttachedActivity = null;

    private DialogInterface.OnDismissListener mDismissListener = null;

    private CircularProgressBar mProgressBar;
    private View      mContentWrapper;
    private View      mErrorWrapper;
    private TextView  mErrorText;
    private CheckView mCheckmark;
    private TextView  mUserName;
    private TextView  mUserSurame;
    private TextView  mEmail;
    private TextView  mOrganization;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mAttachedActivity = (AppCompatActivity) context;
        } else {
            throw new RuntimeException(
                    "CheckInStatus dialog fragment should be attach to an AppCompatActivity");
        }
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(mAttachedActivity);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_checkin_status, null);
        mProgressBar    = root.findViewById(R.id.checkin_status_loading_spinner);
        mContentWrapper = root.findViewById(R.id.checkin_status_content);
        mErrorWrapper   = root.findViewById(R.id.checkin_status_warning_area);
        mErrorText      = root.findViewById(R.id.checkin_status_warning_text);
        mCheckmark      = root.findViewById(R.id.checkin_status_checkmark);
        mUserName       = root.findViewById(R.id.checkin_status_user_name);
        mUserSurame     = root.findViewById(R.id.checkin_status_user_surname);
        mEmail          = root.findViewById(R.id.checkin_status_user_email);
        mOrganization   = root.findViewById(R.id.checkin_status_user_org);

        setCheckInStatusData(null, STATUS_LOADING, null);

        builder.setView(root)
               .setPositiveButton(
                       R.string.checkin_status_done_button_text,
                       new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
        return builder.create();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mDismissListener != null) mDismissListener.onDismiss(dialog);
    }

    public void setCheckInStatusData(User user, int status, String errorMessage) {
        switch(status) {
            case STATUS_LOADING:
                displayProgressBar(true);
                displayUserData(null, false);
                displayError(null, false);
                break;

            case STATUS_SUCCESS:
                displayProgressBar(false);
                displayUserData(user, true);
                displayError(null, false);
                break;

            case STATUS_ERROR:
                displayProgressBar(false);
                displayUserData(user, false);
                displayError(errorMessage, false);
                break;

            case STATUS_WARNING:
                displayProgressBar(false);
                displayUserData(user, false);
                displayError(errorMessage, true);
                break;

            default:
                throw new RuntimeException("Invalid status code passed to CheckInStatusDialog");
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener l) {
        mDismissListener = l;
    }

    private void displayError(String message, boolean isWarning) {
        if (message != null) {
            mErrorWrapper.setVisibility(View.VISIBLE);
            mErrorText.setText(message);

            if (isWarning) {
                mErrorWrapper.setBackgroundColor(
                        getResources().getColor(R.color.warningBackgroundColor));
                mErrorText.setTextColor(
                        getResources().getColor(R.color.warningTextOnWarningBackgroundColor));
            } else {
                mErrorWrapper.setBackgroundColor(
                        getResources().getColor(R.color.errorBackgroundColor));
                mErrorText.setTextColor(
                        getResources().getColor(R.color.errorTextOnErrorBackgroundColor));
            }
        } else {
            mErrorWrapper.setVisibility(View.GONE);
        }
    }

    private void displayProgressBar(boolean display) {
        mProgressBar.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    private void displayUserData(User user, boolean displayCheckmark) {
        if (user != null) {
            mContentWrapper.setVisibility(View.VISIBLE);
            mUserName.setText(user.getName());
            mUserSurame.setText(user.getSurname());
            mEmail.setText(user.getEmail());
            mOrganization.setText(user.getOrganization());

            mCheckmark.setVisibility(displayCheckmark ? View.VISIBLE : View.GONE);
            mCheckmark.check();
        } else {
            mContentWrapper.setVisibility(View.GONE);
        }
    }
}
