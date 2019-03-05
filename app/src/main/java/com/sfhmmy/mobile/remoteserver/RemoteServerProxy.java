package com.sfhmmy.mobile.remoteserver;

import android.os.Bundle;

import com.sfhmmy.mobile.users.User;


public class RemoteServerProxy {

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_ERROR = 1;
    public static final int RESPONSE_WARNING = 2;

    String baseUrl = "https://sfhmmy.gr/";
    String clientKey = "";

    public String acquireOAuth2Token(String email, String password) {
        // TODO: Implemente OAuth2 ROPC flow.

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        if (email.equals("user@gmail.com") && password.equals("user")) return "user123token";
        else if(email.equals("secretary@gmail.com") && password.equals("secretary")) return "secretary123token";
        else return null;
    }

    public String refreshOAuth2Token(String refreshToken) {
        // TODO: Implement OAuth2 Refresh flow.

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        return "abcdef123";
    }

    public boolean isOAuth2TokenValid(String token) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        return false;
    }

    public User getUserProfile(String accessToken) {
        // TODO: Implement profile request.

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        User u = null;

        if (accessToken.equals("secretary123token")) {
            u = new User(5, "secretary@gmail.com", "Fat");
            u.setSurname("Elephant");
            u.setToken(accessToken);
            u.setRole(User.USER_ROLE_SECRETARY);
            u.setOrganization("ECESCON11 Organizing Committee");

        } else if (accessToken.equals("user123token")) {
            u = new User(1001, "user@gmail.com", "Kostas");
            u.setSurname("Dimitriou");
            u.setToken(accessToken);
            u.setRole(User.USER_ROLE_VISITOR);
            u.setOrganization("Aristotle University Of Thessaloniki");
        }

        return u;
    }

    public ResponseContainer checkInUser(String accessToken, String codeValue) {
        ResponseContainer rc = new ResponseContainer();

        try {
            Thread.sleep(2000);
        } catch(InterruptedException ex) {}

        if (codeValue.equals("ecescon11://user")) {
            rc.setObject(getUserProfile("user123token"));
            rc.setMessage("User has already checked in today");
            rc.setCode(RESPONSE_WARNING);
        } else if (codeValue.equals("ecescon11://secretary")) {
            rc.setObject(getUserProfile("secretary123token"));
            rc.setMessage("Success");
            rc.setCode(RESPONSE_SUCCESS);
        } else {
            rc.setObject(null);
            rc.setMessage("Invalid code.");
            rc.setCode(RESPONSE_ERROR);
        }

        return rc;
    }


    public class ResponseContainer {
        private Object mObject;
        private int    mCode;
        private String mMessage;

        public Object getObject() { return mObject; }
        public int getCode() { return mCode; }
        public String getMessage() { return mMessage; }

        public void setObject(Object object) { mObject = object; }
        public void setCode(int code) { mCode = code; }
        public void setMessage(String message) { mMessage = message; }
    }
}
