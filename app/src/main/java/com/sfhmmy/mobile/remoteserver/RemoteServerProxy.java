package com.sfhmmy.mobile.remoteserver;

import com.sfhmmy.mobile.users.User;


public class RemoteServerProxy {

    String baseUrl = "https://sfhmmy.gr/";
    String clientKey = "";

    public String acquireOAuth2Token(String email, String password) {
        // TODO: Implemente OAuth2 ROPC flow.

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        if (email.equals("user@gmail.com") && password.equals("user")) return "abcdef123";
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

        return new User(1001, "user@gmail.com", "Kostas Dimitriou");
    }
}
