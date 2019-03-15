package com.sfhmmy.mobile.remoteserver;

import android.os.Bundle;

import com.sfhmmy.mobile.ImagePost;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.workshops.Workshop;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


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
            u.setPassportValue("ecescon11://secretary");

        } else if (accessToken.equals("user123token")) {
            u = new User(1001, "user@gmail.com", "Kostas");
            u.setSurname("Dimitriou");
            u.setToken(accessToken);
            u.setRole(User.USER_ROLE_VISITOR);
            u.setOrganization("Aristotle University Of Thessaloniki");
            u.setPassportValue("ecescon11://user");

            Calendar cal = GregorianCalendar.getInstance();
            cal.set(2019, 2, 8);
            u.setLastCheckInDate(cal.getTime());
        }

        return u;
    }

    public ResponseContainer<List<User>> getUsersList(String accessToken) {

        ResponseContainer<List<User>> rc = new ResponseContainer<>();

        if (accessToken.equals("secretary123token")) {
            List<User> users = new ArrayList<>();
            users.add(getUserProfile("secretary123token"));
            users.add(getUserProfile("user123token"));

            rc.setObject(users);
            rc.setCode(RESPONSE_SUCCESS);
            rc.setMessage("Success");

        } else if (accessToken.equals("user123token")) {
            rc.setObject(new ArrayList<User>());
            rc.setCode(RESPONSE_SUCCESS);
            rc.setMessage("Success");
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}

        return rc;
    }

    public ResponseContainer<User> checkInUser(String accessToken, String codeValue) {
        ResponseContainer<User> rc = new ResponseContainer<>();

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

    public ResponseContainer<List<ImagePost>> getPhotoWallPosts(String accessToken) {
        ResponseContainer<List<ImagePost>> rc = new ResponseContainer<>();

        List<ImagePost> imagePosts = new ArrayList<>();

        ImagePost post1 = new ImagePost();
        post1.setImageUrl("https://sfhmmy.gr/img/pages/conference/organizing_committee/Teams/IT.jpg");
        post1.setDescription("The best IT team ever.");
        post1.setUploader("Ecescon 11 Organizing Committee");
        post1.setUploadedDate(ZonedDateTime.parse(
                "2019-03-14T01:25:38.492+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));

        ImagePost post2 = new ImagePost();
        post2.setImageUrl("https://sfhmmy.gr/img/pages/conference/organizing_committee/Teams/IT.jpg");
        post2.setDescription("Once more, the best IT team ever.");
        post2.setUploader("Ecescon 11 Organizing Committee");
        post2.setUploadedDate(ZonedDateTime.parse(
                "2019-02-10T12:25:38.492+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));

        imagePosts.add(post1);
        imagePosts.add(post2);

        rc.setCode(RESPONSE_SUCCESS);
        rc.setMessage("Success.");
        rc.setObject(imagePosts);

        return rc;
    }

    public ResponseContainer<List<Workshop>> getWorkshopsList(String accessToken) {
        ResponseContainer<List<Workshop>> rc = new ResponseContainer<>();

        List<Workshop> workshops = new ArrayList<>();

        Workshop work1 = new Workshop();
        work1.setName("R4A Workshop");
        work1.setImageUrl("https://r4a.issel.ee.auth.gr/images/middleware.png");
        work1.setJoinQuestion("Do you love robotics?");
        work1.setPlace("Inside a NAO");
        work1.setDateTime(ZonedDateTime.parse(
                "2019-04-21T14:30:00.000+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));

        Workshop work2 = new Workshop();
        work2.setName("Mhtsos Workshop");
        work2.setImageUrl("https://sfhmmy.gr/img/pages/conference/organizing_committee/IT/Dimitrios_Karageorgiou.jpg");
        work2.setJoinQuestion("Is there any reason to join that workshop?");
        work2.setPlace("Στο τσαντίρι του");
        work2.setDateTime(ZonedDateTime.parse(
                "2019-04-21T18:00:00.000+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));

        workshops.add(work1);
        workshops.add(work2);

        rc.setCode(RESPONSE_SUCCESS);
        rc.setMessage("Success.");
        rc.setObject(workshops);

        return rc;
    }


    public class ResponseContainer<T> {
        private T      mObject;
        private int    mCode;
        private String mMessage;

        public T getObject() { return mObject; }
        public int getCode() { return mCode; }
        public String getMessage() { return mMessage; }

        public void setObject(T object) { mObject = object; }
        public void setCode(int code) { mCode = code; }
        public void setMessage(String message) { mMessage = message; }
    }
}
