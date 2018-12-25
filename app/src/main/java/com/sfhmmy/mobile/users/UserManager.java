package com.sfhmmy.mobile.users;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.MainActivity;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * User Manager is the central point for managing users.
 *
 * User Manager is a singleton whose instance can be acquired through getUserManager()
 * static method.
 */
public class UserManager {

    // Name of the SharedPreferences file used for storing user data.
    private static String USER_PREFERENCES_FILE_NAME = "com.sfhmmy.mobile.users";
    private static String USER_PREFERENCES_TOKEN_ID = "com.sfhmmy.mobile.users.token";
    private static String USER_PREFERENCES_NAME_ID = "com.sfhmmy.mobile.users.name";
    private static String USER_PREFERENCES_EMAIL_ID = "com.sfhmmy.mobile.users.email";
    private static String USER_PREFERENCES_UID_ID = "com.sfhmmy.mobile.users.id";

    // Singleton reference of UserManager.
    private static UserManager mUserManager = null;

    // List of all currently registered UserAuthenticationListener objects.
    private List<UserAuthenticationListener> authenticationListeners;

    // Current active user of the application.
    private User mCurrentUser = null;

    private UserManager() {
        authenticationListeners = new ArrayList<>();
    }

    /**
     * @return Returns a reference of the application's UserManager.
     */
    public static UserManager getUserManager() {
        if (mUserManager == null) mUserManager = new UserManager();
        return mUserManager;
    }

    /**
     * @return Returns currently logged-in user, or null if there is no logged-in user.
     */
    public User getCurrentUser() { return mCurrentUser; }

    /**
     * Initiates an asynchronous login attempt with the provided credentials.
     *
     * After a successful login, all registered UserAuthenticationListener objects get notified
     * via a call to onSessionCreated(). On the other side, when login fails they get notified
     * via a call to onSessionRestorationFailure().
     *
     * @param email Email in plain text format to be used for logging in.
     * @param password Password in plain text format to be used for logging in.
     */
    public void asyncLogin(String email, String password) {
        // TODO: asyncLogin implement
        new LoginTask().execute(email, password);
    }

    /**
     * Initiates an asynchronous operation for restoring a possible previously saved session.
     *
     * After successful restoration, all registered UserAuthenticationListener objects get notified
     * via a call to onSessionCreated(). On the other side, when restoration fails they get notified
     * via a call to onSessionRestorationFailure().
     */
    public void asyncRestoreLastSession() {
        // TODO: asyncRestoreLastSession implement
        new SessionRestoreTask().execute();
    }

    /**
     * Destroys any currently saved user session.
     */
    public void logout() {
        // TODO: logout implement
    }

    /**
     * Checks whether current session is still valid on the server side.
     *
     * @return True when session is valid, else false.
     */
    public boolean isCurrentSessionAlive() {
        return false;
    }

    /**
     *
     * @param l
     * @return
     */
    public boolean registerUserAuthenticationListener(UserAuthenticationListener l) {
        return authenticationListeners.add(l);
    }

    /**
     *
     * @param l
     * @return
     */
    public boolean unregisterUserAuthenticationListener(UserAuthenticationListener l) {
        return authenticationListeners.remove(l);
    }

    /**
     *
     */
    public interface UserAuthenticationListener {
        void onSessionCreated();
        void onSessionRestorationFailure(String error);
    }

    /**
     * Saves given User object to a SharedPreferences file.
     *
     * Currently saved user (if any) is replaced by given one.
     */
    private void saveUserObject(User u) {
        if (u == null) throw new NullPointerException();

        SharedPreferences.Editor spe = App.getAppContext().getSharedPreferences(
                USER_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();

        if (u.getName() != null) spe.putString(USER_PREFERENCES_NAME_ID, u.getName());
        if (u.getEmail() != null) spe.putString(USER_PREFERENCES_EMAIL_ID, u.getEmail());
        if (u.getToken() != null) spe.putString(USER_PREFERENCES_TOKEN_ID, u.getToken());
        if (u.getUid() > 0) spe.putLong(USER_PREFERENCES_UID_ID, u.getUid());

        spe.apply();
    }

    /**
     * Restores saved User from SharedPreferences file.
     *
     * @return If a saved user exists returns a User object containing his info. Else,
     * returns null.
     */
    private User restoreUserObject() {
        SharedPreferences sp = App.getAppContext().getSharedPreferences(
                USER_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

        User u = null;

        if (sp.contains(USER_PREFERENCES_UID_ID)) {
            u = new User();
            u.setUid(sp.getLong(USER_PREFERENCES_UID_ID, -1));
            u.setToken(sp.getString(USER_PREFERENCES_TOKEN_ID, ""));
            u.setName(sp.getString(USER_PREFERENCES_NAME_ID, ""));
            u.setEmail(sp.getString(USER_PREFERENCES_EMAIL_ID, ""));
        }

        return u;
    }

    private void notifyOnSessionCreated() {
        for (UserAuthenticationListener listener : authenticationListeners) {
            listener.onSessionCreated();
        }
    }

    private void notifyOnSessionRestorationFailure() {
        for (UserAuthenticationListener listener : authenticationListeners) {
            listener.onSessionRestorationFailure(
                    App.getAppResources().getString(R.string.error_login_failed));
        }
    }

    /**
     * Async task for performing user login by email and password.
     */
    private class LoginTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... params) {
            String email = params[0];
            String pass  = params[1];

            if (email == null || pass == null) return null;

            RemoteServerProxy proxy = new RemoteServerProxy();
            String token = proxy.acquireOAuth2Token(email, pass);
            if (token == null) return null;

            mCurrentUser = proxy.getUserProfile(token);

            // If a user object is returned, save it. It now represents the logged in user.
            if (mCurrentUser != null) saveUserObject(mCurrentUser);

            return mCurrentUser;
        }

        @Override
        protected void onPostExecute(User u) {
            // When background worker returns no users, notify authentication listeners
            // about the error.
            // TODO: Implement cause specific errors.
            if (u != null) notifyOnSessionCreated();
            else notifyOnSessionRestorationFailure();
        }
    }

    /**
     * Async task for restoring a possible previsouly stored user session.
     */
    private class SessionRestoreTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            User u = restoreUserObject();
            if (u == null) return null;

            boolean isAlive = new RemoteServerProxy().isOAuth2TokenValid(u.getToken());

            if (isAlive) {
                mCurrentUser = u;
                return u;
            } else return null;
        }

        @Override
        protected void onPostExecute(User u) {
            // When background worker returns no users, notify authentication listeners
            // about the error.
            // TODO: Implement cause specific errors.
            if (u != null) notifyOnSessionCreated();
            else notifyOnSessionRestorationFailure();
        }
    }
}
