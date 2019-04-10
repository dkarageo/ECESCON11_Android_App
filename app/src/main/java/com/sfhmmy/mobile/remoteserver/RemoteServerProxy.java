/*
 * RemoteServerProxy.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.remoteserver;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sfhmmy.mobile.cache.CacheProvider;
import com.sfhmmy.mobile.remoteserver.deserializers.AccessTokenDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.ListDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.UserDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.ZonedDateTimeDeserializer;
import com.sfhmmy.mobile.users.AccessToken;
import com.sfhmmy.mobile.users.EducationRank;
import com.sfhmmy.mobile.users.Faculty;
import com.sfhmmy.mobile.users.Institution;
import com.sfhmmy.mobile.users.School;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.workshops.Workshop;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class RemoteServerProxy {

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_ERROR = 1;
    public static final int RESPONSE_WARNING = 2;

    private static final String BASE_URL = "https://sfhmmy.gr/api/";

    private static final String INSTITUTIONS_LIST_CACHE_KEY
            = "remoteserver.RemoteServerProxy.INSTITUTIONS_LIST_CACHE_KEY";
    private static final String FACULTIES_LIST_CACHE_KEY
            = "remoteserver.RemoteServerProxy.FACULTIES_LIST_CACHE_KEY";
    private static final String SCHOOLS_LIST_CACHE_KEY
            = "remoteserver.RemoteServerProxy.SCHOOLS_LIST_CACHE_KEY";
    private static final String EDUCATION_RANKS_LIST_CACHE_KEY
            = "remoteserver.RemoteServerProxy.EDUCATION_RANKS_LIST_CACHE_KEY";


    // - Static networking objects -

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(AccessToken.class, new AccessTokenDeserializer())
            .registerTypeAdapter(User.class, new UserDeserializer())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
            .registerTypeAdapter(List.class, new ListDeserializer())
            .setLenient()
            .create();

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = retrofitBuilder.build();

    private static HttpLoggingInterceptor loggingInterceptor
            = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    private static <S> S createService(Class<S> serviceClass) {
        boolean shouldBuild = false;

        // Add logging interceptor. // TODO: Comment out to disable logging.
        if (!httpClientBuilder.interceptors().contains(loggingInterceptor)) {
            httpClientBuilder.addInterceptor(loggingInterceptor);
            retrofitBuilder.client(httpClientBuilder.build());
            shouldBuild = true;
        }

        if (shouldBuild) retrofit = retrofitBuilder.build();

        return retrofit.create(serviceClass);
    }

    public String acquireOAuth2Token(String email, String password) {

        String token = null;

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            EcesconAPI api = createService(EcesconAPI.class);
            Call<AccessToken> call = api.login(email, password);

            try {
                AccessToken accessToken = call.execute().body();
                if (accessToken != null) token = accessToken.getAccessToken();

            } catch (IOException ex) {}
        }

        return token;
    }

    public boolean isOAuth2TokenValid(String token) {
        // TODO: Implement token validation.
        return true;
    }

    public User getUserProfile(String accessToken) {

        User user = null;

        Log.d("getUserProfile", "Entering getUserProfile");

        if (!TextUtils.isEmpty(accessToken)) {
            EcesconAPI api = createService(EcesconAPI.class);

            Call<User> call = api.getMyProfile("Bearer " + accessToken);
            try {
                user = call.execute().body();
                if (user == null) Log.d("getUserProfile", "Received user is null");

            } catch (Exception ex){}
        }

        return user;
    }

    public School getSchool(long id) {

        List<School> schoolsList = getSchoolsList();
        School school = null;

        if (schoolsList != null) {
            for (School s : schoolsList) {
                if (s.getId() == id) {
                    school = s;
                    break;
                }
            }
        }

        return school;
    }

    public List<School> getSchoolsList() {

        ArrayList<School> list;

        // Firstly, try to restore cache.
        CacheProvider cache = CacheProvider.getCacheProvider();
        list = (ArrayList<School>) cache.retrieveObject(SCHOOLS_LIST_CACHE_KEY);

        // When no cached object could be restored, get it from remote api.
        if (list == null) {
            list = new ArrayList<>();

            EcesconAPI api = createService(EcesconAPI.class);
            List<School> page = null;
            int curPage = 1;

            do {
                Call<List<School>> call = api.getSchoolsList(curPage);

                try {
                    page = call.execute().body();
                } catch(Exception ex) {
                    list = null;
                    break;
                }

                if (page != null) list.addAll(page);
                ++curPage;

            } while (page != null && page.size() > 0);

            if (list != null) cache.storeObject(SCHOOLS_LIST_CACHE_KEY, list);
        }

        return list;
    }

    public Faculty getFaculty(long id) {

        List<Faculty> facultiesList = getFacultiesList();
        Faculty faculty = null;

        if (facultiesList != null) {
            for (Faculty f : facultiesList) {
                if (f.getId() == id) {
                    faculty = f;
                    break;
                }
            }
        }

        return faculty;
    }

    public List<Faculty> getFacultiesList() {

        ArrayList<Faculty> list;

        // Firstly, try to restore cache.
        CacheProvider cache = CacheProvider.getCacheProvider();
        list = (ArrayList<Faculty>) cache.retrieveObject(FACULTIES_LIST_CACHE_KEY);

        // When no cached object could be restored, get it from remote api.
        if (list == null) {
            list = new ArrayList<>();

            EcesconAPI api = createService(EcesconAPI.class);
            List<Faculty> page = null;
            int curPage = 1;

            do {
                Call<List<Faculty>> call = api.getFacultiesList(curPage);

                try {
                    page = call.execute().body();
                } catch(Exception ex) {
                    list = null;
                    break;
                }

                if (page != null) list.addAll(page);
                ++curPage;

            } while (page != null && page.size() > 0);

            if (list != null) cache.storeObject(FACULTIES_LIST_CACHE_KEY, list);
        }

        return list;
    }

    public Institution getInstitution(long id) {

        List<Institution> institutionsList = getInstitutionsList();
        Institution institution = null;

        if (institutionsList != null) {
            for (Institution i : institutionsList) {
                if (i.getId() == id) {
                    institution = i;
                    break;
                }
            }
        }

        return institution;
    }

    public List<Institution> getInstitutionsList() {
        ArrayList<Institution> list;

        // Firstly, try to restore cache.
        CacheProvider cache = CacheProvider.getCacheProvider();
        list = (ArrayList<Institution>) cache.retrieveObject(INSTITUTIONS_LIST_CACHE_KEY);

        // When no cached object could be restored, get it from remote api.
        if (list == null) {
            list = new ArrayList<>();

            EcesconAPI api = createService(EcesconAPI.class);
            List<Institution> page = null;
            int curPage = 1;

            do {
                Call<List<Institution>> call = api.getInstitutionsList(curPage);

                try {
                    page = call.execute().body();
                } catch(Exception ex) {
                    list = null;
                    break;
                }

                if (page != null) list.addAll(page);
                ++curPage;

            } while (page != null && page.size() > 0);

            if (list != null) cache.storeObject(INSTITUTIONS_LIST_CACHE_KEY, list);
        }

        return list;
    }

    public EducationRank getEducationRank(long id) {

        List<EducationRank> ranksList = getEducationRanksList();
        EducationRank rank = null;

        if (ranksList != null) {
            for (EducationRank r : ranksList) {
                if (r.getId() == id) {
                    rank = r;
                    break;
                }
            }
        }

        return rank;
    }

    public List<EducationRank> getEducationRanksList() {
        ArrayList<EducationRank> list;

        // Firstly, try to restore cache.
        CacheProvider cache = CacheProvider.getCacheProvider();
        list = (ArrayList<EducationRank>) cache.retrieveObject(EDUCATION_RANKS_LIST_CACHE_KEY);

        // When no cached object could be restored, get it from remote api.
        if (list == null) {
            list = new ArrayList<>();

            EcesconAPI api = createService(EcesconAPI.class);
            List<EducationRank> page = null;
            int curPage = 1;

            do {
                Call<List<EducationRank>> call = api.getEducationRanksList(curPage);

                try {
                    page = call.execute().body();
                } catch(Exception ex) {
                    list = null;
                    break;
                }

                if (page != null) list.addAll(page);
                ++curPage;

            } while (page != null && page.size() > 0);

            if (list != null) cache.storeObject(EDUCATION_RANKS_LIST_CACHE_KEY, list);
        }

        return list;
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

    public ResponseContainer<List<Workshop>> getWorkshopsList(String accessToken) {
        ResponseContainer<List<Workshop>> rc = new ResponseContainer<>();

        List<Workshop> workshops = new ArrayList<>();

        Workshop work1 = new Workshop();
        work1.setId(0);
        work1.setName("R4A Workshop");
        work1.setImageUrl("https://r4a.issel.ee.auth.gr/images/middleware.png");
        work1.setJoinQuestion("Do you love robotics?");
        work1.setPlace("Inside a NAO");
        work1.setBeginDate(ZonedDateTime.parse(
                "2019-04-21T14:30:00.000+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));
        work1.setEndDate(ZonedDateTime.parse(
                "2019-04-21T17:30:00.000+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));
        work1.setDescription("Learn everything about robots!");

        Workshop work2 = new Workshop();
        work2.setId(1);
        work2.setName("Mhtsos Workshop");
        work2.setImageUrl("https://sfhmmy.gr/img/pages/conference/organizing_committee/IT/Dimitrios_Karageorgiou.jpg");
        work2.setJoinQuestion("Is there any reason to join that workshop?");
        work2.setPlace("Στο τσαντίρι του");
        work2.setBeginDate(ZonedDateTime.parse(
                "2019-04-21T18:00:00.000+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));
        work2.setEndDate(ZonedDateTime.parse(
                "2019-04-21T20:00:00.000+02:00[Europe/Athens]",
                DateTimeFormatter.ISO_ZONED_DATE_TIME
        ));
        work2.setDescription("Ένα εργαστήριο γεμάτο εκπλήξεις! ;)");

        if (accessToken != null) {
            work1.setEnrollStatus(Workshop.EnrollStatus.AVAILABLE);
            work2.setEnrollStatus(Workshop.EnrollStatus.AVAILABLE);
        } else {
            work1.setEnrollStatus(Workshop.EnrollStatus.UNAVAILABLE);
            work2.setEnrollStatus(Workshop.EnrollStatus.UNAVAILABLE);
        }

        workshops.add(work1);
        workshops.add(work2);

        rc.setCode(RESPONSE_SUCCESS);
        rc.setMessage("Success.");
        rc.setObject(workshops);

        return rc;
    }

    public ResponseContainer<Workshop> enrollToWorkshop(String accessToken, Workshop workshop,
                                                        String answer) {

        ResponseContainer<Workshop> rc = new ResponseContainer<>();

        workshop.setEnrollStatus(Workshop.EnrollStatus.PENDING);

        rc.setObject(workshop);
        rc.setMessage("Success.");
        rc.setCode(RESPONSE_SUCCESS);

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


    private interface EcesconAPI {
        @POST("login")
        Call<AccessToken> login(@Query("email") String email, @Query("password") String password);

        @GET("me")
        Call<User> getMyProfile(@Header("Authorization") String accessToken);

        @GET("schools")
        Call<List<School>> getSchoolsList(@Query("page") int page);

        @GET("faculties")
        Call<List<Faculty>> getFacultiesList(@Query("page") int page);

        @GET("institutions")
        Call<List<Institution>> getInstitutionsList(@Query("page") int page);

        @GET("education_ranks")
        Call<List<EducationRank>> getEducationRanksList(@Query("page") int page);
    }
}
