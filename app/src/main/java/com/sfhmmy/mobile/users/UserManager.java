/*
 * UserManager.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.2
 */

package com.sfhmmy.mobile.users;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

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
    private static final String USER_PREFERENCES_FILE_NAME = "com.sfhmmy.mobile.users";
    private static final String USER_PREFERENCES_TOKEN_KEY = "com.sfhmmy.mobile.users.token";
    private static final String USER_PREFERENCES_NAME_KEY = "com.sfhmmy.mobile.users.name";
    private static final String USER_PREFERENCES_SURNAME_KEY = "com.sfhmmy.mobile.users.surname";
    private static final String USER_PREFERENCES_EMAIL_KEY = "com.sfhmmy.mobile.users.email";
    private static final String USER_PREFERENCES_UID_KEY = "com.sfhmmy.mobile.users.id";
    private static final String USER_PREFERENCES_ROLE_KEY = "com.sfhmmy.mobile.users.role";
    private static final String USER_PREFERENCES_PROFILE_PIC_URL_KEY
            = "com.sfhmmy.mobile.users.profile_pic";
    private static final String USER_PREFERENCES_ORGANIZATION_KEY
            = "com.sfhmmy.mobile.users.organization";
    private static final String USER_PREFERENCES_PASSPORT_VALUE_KEY
            = "com.sfhmmy.mobile.users.passport_value";
    private static final String USER_PREFERENCES_LAST_CHECKIN_DATE_KEY
            = "com.sfhmmy.mobile.users.last_checkin_date";
    private static final String USER_PREFERENCES_EDUCATION_LEVEL_KEY
            = "com.sfhmmy.mobile.users.education_level";
    private static final String USER_PREFERENCES_DEPARTMENT_KEY
            = "com.sfhmmy.mobile.users.department";
    private static final String USER_PREFERENCES_DEPARTMENT_SPECIALIZATION_KEY
            = "com.sfhmmy.mobile.users.department_specialization";
    private static final String USER_PREFERENCES_YEARS_OF_EXPERIENCE_KEY
            = "com.sfhmmy.mobile.users.years_of_experience";
    private static final String USER_PREFERENCES_GENDER_KEY = "com.sfhmmy.mobile.users.gender";
    private static final String USER_PREFERENCES_PREFERED_LANGUAGE_KEY
            = "com.sfhmmy.mobile.prefered_language";
    private static final String USER_PREFERENCES_REGISTRATION_DATE_KEY
            = "com.sfhmmy.mobile.registration_date";

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
     * Destroys any currently saved user session and notifies all registered
     * UserAuthenticationListener objects via a call to onSessionDestroyed() method.
     */
    public void logout() {
        mCurrentUser = null;
        saveUserObject(null);
        notifyOnSessionDestroyed();
    }

    /**
     * Checks whether current session is still valid on the server side.
     *
     * @return True when session is valid, else false.
     */
    public boolean isCurrentSessionAlive() {
        return false;
    }

    public void registerUserAuthenticationListener(UserAuthenticationListener l) {
        authenticationListeners.add(l);
    }

    public void unregisterUserAuthenticationListener(UserAuthenticationListener l) {
        authenticationListeners.remove(l);
    }

    public boolean isCurrentUserSecretary() {
        return mCurrentUser != null && mCurrentUser.hasRoleOf(User.Role.SECRETARY);
    }

    public interface UserAuthenticationListener {
        void onSessionCreated(User user);
        void onSessionRestorationFailure(String error);
        void onSessionDestroyed();
    }

    /**
     * Saves given User object to a SharedPreferences file.
     *
     * Currently saved user (if any) is replaced by given one. If given user is null, then any
     * previously saved user is removed from the storage.
     */
    private void saveUserObject(User u) {
        SharedPreferences.Editor spe = App.getAppContext().getSharedPreferences(
                USER_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE).edit();

        if (u != null) {
            spe.putLong(USER_PREFERENCES_UID_KEY, u.getUid());
            spe.putString(USER_PREFERENCES_EMAIL_KEY, u.getEmail());
            spe.putString(USER_PREFERENCES_NAME_KEY, u.getName());
            spe.putString(USER_PREFERENCES_SURNAME_KEY, u.getSurname());
            spe.putString(USER_PREFERENCES_TOKEN_KEY, u.getToken());
            spe.putString(USER_PREFERENCES_ROLE_KEY, u.getRole().name());
            spe.putString(USER_PREFERENCES_PROFILE_PIC_URL_KEY, u.getProfilePictureURL());
            spe.putString(USER_PREFERENCES_ORGANIZATION_KEY, u.getOrganization());
            spe.putString(USER_PREFERENCES_PASSPORT_VALUE_KEY, u.getPassportValue());
            spe.putString(USER_PREFERENCES_LAST_CHECKIN_DATE_KEY,
                          u.getLastCheckInDate() != null ?
                                  u.getLastCheckInDate().format(DateTimeFormatter.ISO_ZONED_DATE_TIME) :
                                  null
            );
            spe.putString(USER_PREFERENCES_EDUCATION_LEVEL_KEY, u.getEducationLevel());
            spe.putString(USER_PREFERENCES_DEPARTMENT_KEY, u.getDepartment());
            spe.putString(USER_PREFERENCES_DEPARTMENT_SPECIALIZATION_KEY,
                          u.getDepartmentSpecialization());
            spe.putInt(USER_PREFERENCES_YEARS_OF_EXPERIENCE_KEY, u.getYearsOfExperience());

            if (u.getGender() != null) spe.putString(USER_PREFERENCES_GENDER_KEY, u.getGender().name());
            else spe.remove(USER_PREFERENCES_GENDER_KEY);

            spe.putString(USER_PREFERENCES_PREFERED_LANGUAGE_KEY, u.getPreferedLanguage());
            spe.putString(USER_PREFERENCES_REGISTRATION_DATE_KEY,
                          u.getRegistrationDate() != null ?
                                  u.getRegistrationDate().format(DateTimeFormatter.ISO_ZONED_DATE_TIME) :
                                  null
            );

        } else {
            spe.remove(USER_PREFERENCES_UID_KEY);
            spe.remove(USER_PREFERENCES_EMAIL_KEY);
            spe.remove(USER_PREFERENCES_NAME_KEY);
            spe.remove(USER_PREFERENCES_SURNAME_KEY);
            spe.remove(USER_PREFERENCES_TOKEN_KEY);
            spe.remove(USER_PREFERENCES_ROLE_KEY);
            spe.remove(USER_PREFERENCES_PROFILE_PIC_URL_KEY);
            spe.remove(USER_PREFERENCES_ORGANIZATION_KEY);
            spe.remove(USER_PREFERENCES_PASSPORT_VALUE_KEY);
            spe.remove(USER_PREFERENCES_LAST_CHECKIN_DATE_KEY);
            spe.remove(USER_PREFERENCES_EDUCATION_LEVEL_KEY);
            spe.remove(USER_PREFERENCES_DEPARTMENT_KEY);
            spe.remove(USER_PREFERENCES_DEPARTMENT_SPECIALIZATION_KEY);
            spe.remove(USER_PREFERENCES_YEARS_OF_EXPERIENCE_KEY);
            spe.remove(USER_PREFERENCES_GENDER_KEY);
            spe.remove(USER_PREFERENCES_PREFERED_LANGUAGE_KEY);
            spe.remove(USER_PREFERENCES_REGISTRATION_DATE_KEY);
        }

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

        if (sp.contains(USER_PREFERENCES_UID_KEY)) {
            u = new User();
            u.setUid(sp.getLong(USER_PREFERENCES_UID_KEY, -1));
            u.setEmail(sp.getString(USER_PREFERENCES_EMAIL_KEY, null));
            u.setName(sp.getString(USER_PREFERENCES_NAME_KEY, null));
            u.setSurname(sp.getString(USER_PREFERENCES_SURNAME_KEY, null));
            u.setToken(sp.getString(USER_PREFERENCES_TOKEN_KEY, null));
            u.setPassportValue(sp.getString(USER_PREFERENCES_PASSPORT_VALUE_KEY, null));
            u.setProfilePictureURL(sp.getString(USER_PREFERENCES_PROFILE_PIC_URL_KEY, null));
            u.setOrganization(sp.getString(USER_PREFERENCES_ORGANIZATION_KEY, null));
            u.setEducationLevel(sp.getString(USER_PREFERENCES_EDUCATION_LEVEL_KEY, null));
            u.setDepartment(sp.getString(USER_PREFERENCES_DEPARTMENT_KEY, null));
            u.setDepartmentSpecialization(
                    sp.getString(USER_PREFERENCES_DEPARTMENT_SPECIALIZATION_KEY, null));
            u.setYearsOfExperience(sp.getInt(USER_PREFERENCES_YEARS_OF_EXPERIENCE_KEY, -1));
            u.setPreferedLanguage(sp.getString(USER_PREFERENCES_PREFERED_LANGUAGE_KEY, null));

            String roleString = sp.getString(USER_PREFERENCES_ROLE_KEY, null);
            try {
                u.setRole(roleString != null ? User.Role.valueOf(roleString) : null);
            } catch (IllegalArgumentException ex) {
                // In case for a reason preferences file got corrupted, don't allow the app to
                // crash by just not setting role attribute.
                u.setRole(null);
            }

            String genderString = sp.getString(USER_PREFERENCES_GENDER_KEY, null);
            try {
                u.setGender(genderString != null ? User.Gender.valueOf(genderString) : null);
            } catch (IllegalArgumentException ex) {
                // In case for a reason preferences file got corrupted, don't allow the app to
                // crash by just not setting role attribute.
                u.setGender(null);
            }

            String dateString = sp.getString(USER_PREFERENCES_LAST_CHECKIN_DATE_KEY, null);
            if (dateString != null) {
                ZonedDateTime dateTime = ZonedDateTime.parse(
                        dateString, DateTimeFormatter.ISO_ZONED_DATE_TIME
                );
                u.setLastCheckInDate(dateTime);
            }

            String registrationDateString = sp.getString(USER_PREFERENCES_REGISTRATION_DATE_KEY, null);
            if (registrationDateString != null) {
                ZonedDateTime dateTime = ZonedDateTime.parse(
                        registrationDateString, DateTimeFormatter.ISO_ZONED_DATE_TIME
                );
                u.setRegistrationDate(dateTime);
            }
        }

        return u;
    }

    private void notifyOnSessionCreated(User user) {
        for (UserAuthenticationListener listener : authenticationListeners) {
            listener.onSessionCreated(user);
        }
    }

    private void notifyOnSessionRestorationFailure() {
        for (UserAuthenticationListener listener : authenticationListeners) {
            listener.onSessionRestorationFailure(
                    App.getAppResources().getString(R.string.error_login_failed));
        }
    }

    private void notifyOnSessionDestroyed() {
        for (UserAuthenticationListener listener : authenticationListeners) {
            listener.onSessionDestroyed();
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
            if (u != null) notifyOnSessionCreated(getCurrentUser());
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
            if (u != null) notifyOnSessionCreated(getCurrentUser());
            else notifyOnSessionRestorationFailure();
        }
    }
}
