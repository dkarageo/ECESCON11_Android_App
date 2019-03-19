/*
 * UserAwareFragment.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import androidx.fragment.app.Fragment;

import com.sfhmmy.mobile.users.LoginDialogFragment;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.users.UserManager;


public class UserAwareFragment extends Fragment {

    private boolean mShouldNotifyAboutUserEvent;

    @Override
    public void onResume() {
        super.onResume();

        // When fragment is visible, handle all user events immediately.
        mShouldNotifyAboutUserEvent = true;
        // When fragment becomes visible, make sure to handle any event took place in the meantime.
        onCreateUserSpecificContent(
                UserManager.getUserManager().getCurrentUser()
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        mShouldNotifyAboutUserEvent = false;
    }

    protected void onCreateUserSpecificContent(User user) {}

    public void notifyOnUserChange(User user) {
        if (mShouldNotifyAboutUserEvent) onCreateUserSpecificContent(user);
    }

    protected void displayLoginDialog() {
        LoginDialogFragment loginDialog = LoginDialogFragment.newInstance();
        loginDialog.show(requireFragmentManager(), "loginDialog");
    }
}
