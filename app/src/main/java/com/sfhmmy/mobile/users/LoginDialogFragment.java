/*
 * LoginDialogFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.users;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.sfhmmy.mobile.R;


public class LoginDialogFragment
        extends DialogFragment
        implements UserManager.UserAuthenticationListener {

    // Keys for storing UI state to Bundle.
    private static final String IS_PROGRESS_VISIBLE_KEY = "is_progress_visible";

    private AppCompatActivity mAttachedActivity = null;

    private AutoCompleteTextView mEmail;               // Handler for email input field.
    private TextInputEditText    mPassword;            // Handler for pass input field.
    private CircularProgressBar  mProgressSpinner;     // Handler for progress spinner.
    private View                 mCredentialsWrapper;  // Handler for credentials container.
    private TextView             mRegisterHint;
    private TextView             mPasswordRestoreHint;

    // Indicates whether progress UI elements are visible, instead of login form ones.
    private boolean mIsProgressVisible;


    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }

    public LoginDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppCompatActivity) {
            mAttachedActivity = (AppCompatActivity) context;
        } else {
            throw new RuntimeException(
                    "Login dialog fragment should be attach to an AppCompatActivity");
        }
    }

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = mAttachedActivity.getLayoutInflater();
        View root = inflater.inflate(R.layout.fragment_login_dialog, null);

        mEmail               = root.findViewById(R.id.login_dialog_email);
        mPassword            = root.findViewById(R.id.login_dialog_password);
        mCredentialsWrapper  = root.findViewById(R.id.login_dialog_credentials_wrapper);
        mProgressSpinner     = root.findViewById(R.id.login_dialog_progress_spinner);
        mRegisterHint        = root.findViewById(R.id.login_dialog_register_hint);
        mPasswordRestoreHint = root.findViewById(R.id.login_dialog_password_restore_hint);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    // Hide keyboard before starting login process.
                    closeVirtualKeyboard();
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mAttachedActivity);
        builder.setView(root)
               .setPositiveButton(R.string.login_dialog_login_button_text, null)
               .setNegativeButton(R.string.login_dialog_cancel_button_text, null)
               .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        // Overwrite behavior of DialogInterface.OnClickDialog for positive button that
        // dismisses the dialog. Instead, positive button initiates a login process.
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final AlertDialog alertDialog = (AlertDialog) dialogInterface;
                Button positive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeVirtualKeyboard();
                        attemptLogin();
                    }
                });

                // Set color of buttons text.
                positive.setTextColor(getResources().getColor(R.color.colorPrimary));
                Button negative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negative.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        mRegisterHint.setMovementMethod(LinkMovementMethod.getInstance());
        mPasswordRestoreHint.setMovementMethod(LinkMovementMethod.getInstance());

        if (savedInstanceState != null) {
            // Restore any saved state of the dialog.
            showProgress(savedInstanceState.getBoolean(IS_PROGRESS_VISIBLE_KEY));
        } else {
            showProgress(false);
        }

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (UserManager.getUserManager().getCurrentUser() != null) {
            // If for any reason a logged in user exists, just dismiss the login dialog.
            dismiss();
        } else {
            UserManager.getUserManager().registerUserAuthenticationListener(this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_PROGRESS_VISIBLE_KEY, mIsProgressVisible);
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getUserManager().unregisterUserAuthenticationListener(this);
    }

    @Override
    public void onSessionCreated(User user) {
        // When a user session has been created, login dialog is no longer needed.
        dismiss();
    }

    @Override
    public void onSessionRestorationFailure(String error) {
        showProgress(false);
        showActionButtons(true);
    }

    @Override
    public void onSessionDestroyed() {}

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);

        // Store values at the time of the login attempt.
        String email    = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner and ask for login.
            showProgress(true);
            closeVirtualKeyboard();
            showActionButtons(false);
            UserManager.getUserManager().asyncLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
//        return password.length() > 4;
        return true;
    }

    private void showProgress(boolean show) {
        if (show) {
            mProgressSpinner.setVisibility(View.VISIBLE);
            mCredentialsWrapper.setVisibility(View.GONE);
            mIsProgressVisible = true;
        } else {
            mProgressSpinner.setVisibility(View.GONE);
            mCredentialsWrapper.setVisibility(View.VISIBLE);
            mIsProgressVisible = false;
        }
    }

    private void showActionButtons(boolean show) {
        AlertDialog dialog = (AlertDialog) getDialog();
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (show) {
            positive.setVisibility(View.VISIBLE);
            negative.setVisibility(View.VISIBLE);
        } else {
            positive.setVisibility(View.GONE);
            negative.setVisibility(View.GONE);
        }
    }

    private void closeVirtualKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                mAttachedActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                mCredentialsWrapper.getWindowToken(), 0
        );
    }
}
