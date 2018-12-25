package com.sfhmmy.mobile.users;

public class User {

    private long mUid;
    private String mEmail;
    private String mName;
    private String mToken;

    public User() {}

    public User(long uid, String email, String name) {
        setUid(uid);
        setEmail(email);
        setName(name);
    }

    public long getUid() { return mUid; }
    public String getEmail() { return mEmail; }
    public String getName() { return mName; }
    public String getToken() { return mToken; }

    public void setUid(long uid) { mUid = uid; }
    public void setEmail(String email) { mEmail = email; }
    public void setName(String name) { mName = name; }
    public void setToken(String token) {mToken = token; }
}
