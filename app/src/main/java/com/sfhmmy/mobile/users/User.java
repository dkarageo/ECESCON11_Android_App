package com.sfhmmy.mobile.users;

import android.graphics.Bitmap;

import org.threeten.bp.ZonedDateTime;

import java.util.Date;

public class User {

    public enum Role { VISITOR, SECRETARY, ADMINISTRATOR }
    public static String USER_ROLE_SECRETARY = "secretary";

    private long   mUid;
    private String mEmail;
    private String mName;
    private String mSurname;
    private String mOrganization;
    private String mToken;
    private Role   mRole;
    private Bitmap mProfilePicture;
    private String mPassportValue;
    private ZonedDateTime mLastCheckInDate;

    public User() {}

    public User(long uid, String email, String name) {
        setUid(uid);
        setEmail(email);
        setName(name);
    }

    public long getUid() { return mUid; }
    public String getEmail() { return mEmail; }
    public String getName() { return mName; }
    public String getSurname() { return mSurname; }
    public String getToken() { return mToken; }
    public Role getRole() { return mRole; }
    public Bitmap getProfilePicture() { return mProfilePicture; }
    public String getOrganization() { return mOrganization; }
    public String getPassportValue() { return mPassportValue; }
    public ZonedDateTime getLastCheckInDate() { return mLastCheckInDate; }


    public void setUid(long uid) { mUid = uid; }
    public void setEmail(String email) { mEmail = email; }
    public void setName(String name) { mName = name; }
    public void setSurname(String surname) { mSurname = surname; }
    public void setToken(String token) { mToken = token; }
    public void setRole(Role role) { mRole = role; }
    public void setProfilePicture(Bitmap profilePicture) { mProfilePicture = profilePicture; }
    public void setOrganization(String organization) { mOrganization = organization; }
    public void setPassportValue(String passportValue) { mPassportValue = passportValue; }
    public void setLastCheckInDate(ZonedDateTime lastCheckInDate) { mLastCheckInDate = lastCheckInDate; }

    public boolean hasRoleOf(Role role) {
        if (mRole == role) return true;
        else if (role == Role.SECRETARY && mRole == Role.ADMINISTRATOR) return true;
        else return false;
    }
}
