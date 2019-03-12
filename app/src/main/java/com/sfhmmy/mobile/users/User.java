package com.sfhmmy.mobile.users;

import android.graphics.Bitmap;

import java.util.Date;

public class User {

    public static String USER_ROLE_VISITOR = "visitor";
    public static String USER_ROLE_SECRETARY = "secretary";

    private long   mUid;
    private String mEmail;
    private String mName;
    private String mSurname;
    private String mOrganization;
    private String mToken;
    private String mRole;
    private Bitmap mProfilePicture;
    private String mPassportValue;
    private Date   mLastCheckInDate;

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
    public String getRole() { return mRole; }
    public Bitmap getProfilePicture() { return mProfilePicture; }
    public String getOrganization() { return mOrganization; }
    public String getPassportValue() { return mPassportValue; }
    public Date getLastCheckInDate() { return mLastCheckInDate; }


    public void setUid(long uid) { mUid = uid; }
    public void setEmail(String email) { mEmail = email; }
    public void setName(String name) { mName = name; }
    public void setSurname(String surname) { mSurname = surname; }
    public void setToken(String token) { mToken = token; }
    public void setRole(String role) { mRole = role; }
    public void setProfilePicture(Bitmap profilePicture) { mProfilePicture = profilePicture; }
    public void setOrganization(String organization) { mOrganization = organization; }
    public void setPassportValue(String passportValue) { mPassportValue = passportValue; }
    public void setLastCheckInDate(Date lastCheckInDate) { mLastCheckInDate = lastCheckInDate; }
}
