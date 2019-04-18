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

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sfhmmy.mobile.ImagePost;
import com.sfhmmy.mobile.battles.BattlesPost;
import com.sfhmmy.mobile.cache.CacheProvider;
import com.sfhmmy.mobile.checkins.CheckinDateForCheckinProcess;
import com.sfhmmy.mobile.remoteserver.deserializers.AccessTokenDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.ContentPageDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.ListDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.UserDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.WorkshopEnrollStatusDeserializer;
import com.sfhmmy.mobile.remoteserver.deserializers.ZonedDateTimeDeserializer;
import com.sfhmmy.mobile.users.AccessToken;
import com.sfhmmy.mobile.users.CheckinDate;
import com.sfhmmy.mobile.users.EducationRank;
import com.sfhmmy.mobile.users.Faculty;
import com.sfhmmy.mobile.users.Institution;
import com.sfhmmy.mobile.users.School;
import com.sfhmmy.mobile.users.User;
import com.sfhmmy.mobile.workshops.Workshop;
import com.sfhmmy.mobile.workshops.WorkshopEnrollStatusHelper;
import com.sfhmmy.mobile.workshops.WorkshopEvent;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

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
import retrofit2.http.Path;
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
    private static final String WORKSHOPS_LIST_CACHE_KEY
            = "remoteserver.RemoteServerProxy.WORKSHOPS_LIST_CACHE_KEY";
    private static final String USERS_LIST_CACHE_KEY
            = "remoteserver.RemoteServerProxy.USERS_LIST_CACHE_KEY";
    private static final String USERS_LIST_CACHE_LAST_UPDATE_KEY
            = "remoteserver.RemoteServerProxy.USERS_LIST_CACHE_LAST_UPDATE_KEY";

    // - Static networking objects -

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(AccessToken.class, new AccessTokenDeserializer())
            .registerTypeAdapter(User.class, new UserDeserializer())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
            .registerTypeAdapter(List.class, new ListDeserializer())
            .registerTypeAdapter(ContentPage.class, new ContentPageDeserializer())
            .registerTypeAdapter(WorkshopEnrollStatusHelper.class, new WorkshopEnrollStatusDeserializer())
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

    private static ArrayList<User> mCachedUsersList;  // Cached users list.
    private static List<UsersListFetcherListener> mUsersListFetcherListeners = new ArrayList<>();
    private static final Object mUserListFetchingLock = new Object();
    private static boolean mIsUserListFetching = false;
    private static ZonedDateTime mUserListLastUpdate;


    private static <S> S createService(Class<S> serviceClass) {
        boolean shouldBuild = false;

        // Add logging interceptor. // TODO: Comment out to disable logging.
        if (!httpClientBuilder.interceptors().contains(loggingInterceptor)) {
//            httpClientBuilder.addInterceptor(loggingInterceptor);
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

        if (user != null) user.setToken(accessToken);

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

    public ContentPage<ImagePost> getPhotoWallPage(int page) {

        ContentPage<ImagePost> pageContainer;

        EcesconAPI api = createService(EcesconAPI.class);
        Call<ContentPage<ImagePost>> call = api.getPhotoWallList(page);
        try {
            pageContainer = call.execute().body();
        } catch (IOException ex) {
            pageContainer = null;
        }

        return pageContainer;
    }

    public ContentPage<BattlesPost> getPhotoshopBattlesPage(int page) {

        ContentPage<BattlesPost> pageContainer;

        EcesconAPI api = createService(EcesconAPI.class);
        Call<ContentPage<BattlesPost>> call = api.getPhotoshopBattlesList(page);
        try {
            pageContainer = call.execute().body();
        } catch (IOException ex) {
            pageContainer = null;
        }

        return pageContainer;
    }

    public ResponseContainer<List<User>> getUsersList(String accessToken) {

        ResponseContainer<List<User>> rc = new ResponseContainer<>();

        ArrayList<User> list = null;

        CacheProvider cache = CacheProvider.getCacheProvider();

        synchronized (mUserListFetchingLock) {

            if (mCachedUsersList != null) {  // Firstly, check if there is a copy in memory.
                list = mCachedUsersList;
            } else {  // Then, try to restore from cache.
                mCachedUsersList = (ArrayList<User>) cache.retrieveObject(USERS_LIST_CACHE_KEY);
                mUserListLastUpdate = (ZonedDateTime) cache.retrieveObject(
                        USERS_LIST_CACHE_LAST_UPDATE_KEY
                );
                list = mCachedUsersList;
            }

            // When no copy is available locally, fetch it from remote api.
            if (list == null) asyncUpdateUsersList(accessToken);

            // Wait until data becomes available or fetcher has stopped.
            while (mCachedUsersList == null && mIsUserListFetching) {
                try {
                    mUserListFetchingLock.wait();
                } catch (InterruptedException ex) {}
            }
        }

        if (list != null) {
            rc.setObject(list);
            rc.setCode(RESPONSE_SUCCESS);
            rc.setMessage("Success.");
        } else {
            rc.setObject(new ArrayList<>());
            rc.setCode(RESPONSE_ERROR);
            rc.setMessage("Failed to retrieve users list.");
        }

        return rc;
    }

    public int getCachedUsersListCount() {

        ArrayList<User> list = null;

        CacheProvider cache = CacheProvider.getCacheProvider();

        synchronized (mUserListFetchingLock) {

            if (mCachedUsersList != null) {  // Firstly, check if there is a copy in memory.
                list = mCachedUsersList;
            } else {  // Then, try to restore from cache.
                mCachedUsersList = (ArrayList<User>) cache.retrieveObject(USERS_LIST_CACHE_KEY);
                list = mCachedUsersList;
            }
        }

        return list != null ? list.size() : 0;
    }

    public ZonedDateTime getCachedUsersListLastUpdate() {
        ZonedDateTime lastUpdate;

        CacheProvider cache = CacheProvider.getCacheProvider();

        synchronized (mUserListFetchingLock) {
            if (mUserListLastUpdate != null) {  // Firstly, check if there is a copy in memory.
                lastUpdate = mUserListLastUpdate;
            } else {  // Then, try to restore from cache.
                mUserListLastUpdate = (ZonedDateTime) cache.retrieveObject(
                        USERS_LIST_CACHE_LAST_UPDATE_KEY
                );
                lastUpdate = mUserListLastUpdate;
            }
        }

        return lastUpdate;
    }

    public ResponseContainer<User> checkInUser(String accessToken, String codeValue, String dayTag) {
        ResponseContainer<User> rc = new ResponseContainer<>();

        final String prefix = "ecescon11://";
        final String prefixOld = "sfhmmy11://";

        User userToCheckin = null;
        String error = null;
        String warning = null;
        String passportId = null;

        // Strip given code from prefix.
        if (codeValue.startsWith(prefix)) {
            passportId = codeValue.substring(prefix.length());
        } else if (codeValue.startsWith(prefixOld)) {
            passportId = codeValue.substring(prefixOld.length());
        } else {
            error = "Invalid code provided.";
        }

        // If no token provided, no need to attempt api call.
        if (TextUtils.isEmpty(accessToken)) {
            error = "Unauthorized to check-in.";
        }

        // If no day specified, no need to attempt api call.
        if (dayTag == null) {
            error = "No day specified.";
        }

        // Find user whose passport id is the given one.
        if (error == null) {
            synchronized (mUserListFetchingLock) {
                List<User> users = getUsersList(accessToken).getObject();
                if (users != null) {
                    for (User u : users) {
                        if (u.getPassportValue().equals(passportId)) {
                            userToCheckin = u;
                            break;
                        }
                    }
                }
            }

            if (userToCheckin == null) {
                error = "User passport not found.";
            }
        }

        // Check if user has already checked-in at given day.
        if (error == null && userToCheckin.getCheckinDates() != null) {
            for (CheckinDate c : userToCheckin.getCheckinDates()) {
                if (c.getDayTag().equals(dayTag)) {
                    warning = String.format("User has already checked-in for %s day.", dayTag);
                    break;
                }
            }
        }

        // If a valid user is found, then attempt to check-in him.
        if (error == null && warning == null) {
            CheckinDateForCheckinProcess checkin = null;

            EcesconAPI api = createService(EcesconAPI.class);
            Call<CheckinDateForCheckinProcess> call = api.checkinUser(
                    "Bearer " + accessToken, userToCheckin.getUid(), dayTag
            );

            try {
                checkin = call.execute().body();
            } catch (IOException ex) {
                userToCheckin = null;
                error = "Server refused to check-in user";
            }

            // Add check-in data to user.
            if (checkin != null) {
                CheckinDate properCheckin = new CheckinDate();
                properCheckin.setDate(checkin.getDate());
                properCheckin.setDayTag(dayTag);
                properCheckin.setId(checkin.getId());
                ArrayList<CheckinDate> checkinsList = userToCheckin.getCheckinDates();
                checkinsList = checkinsList != null ? checkinsList : new ArrayList<>();
                checkinsList.add(properCheckin);
                userToCheckin.setCheckinDates(checkinsList);
            } else {
                // There is currently no way to check why server did not complete check-in.
                // Assume that the user has already checked-in for this day.
                warning = String.format("User has already checked-in for %s day.", dayTag);
            }
        }

        rc.setObject(userToCheckin);
        if (error != null) {
            rc.setMessage(error);
            rc.setCode(RESPONSE_ERROR);
        } else if (warning != null) {
            rc.setMessage(warning);
            rc.setCode(RESPONSE_WARNING);
        } else {
            rc.setMessage("Success.");
            rc.setCode(RESPONSE_SUCCESS);
        }

        return rc;
    }

    public ResponseContainer<List<Workshop>> getWorkshopsList(String accessToken,
                                                              boolean forceRefresh) {

        ResponseContainer<List<Workshop>> rc = new ResponseContainer<>();

        ContentPage<Workshop> workshopsPage = null;

        CacheProvider cache = CacheProvider.getCacheProvider();
        // Firstly, try to restore cache.
        if (!forceRefresh) {
            workshopsPage = (ContentPage<Workshop>) cache.retrieveObject(WORKSHOPS_LIST_CACHE_KEY);
        }

        // If no data loaded from cache, fetch them from remote api.
        if (workshopsPage == null) {
            EcesconAPI api = createService(EcesconAPI.class);
            Call<ContentPage<Workshop>> call = api.getWorkshopsList();
            try {
                workshopsPage = call.execute().body();

                // When access token is ready, fill workshops with enroll status of the user.
                if (workshopsPage != null && !TextUtils.isEmpty(accessToken)) {
                    Call<ContentPage<WorkshopEnrollStatusHelper>> statusesCall =
                            api.getWorkshopsEnrollStatusesList("Bearer " + accessToken);
                    ContentPage<WorkshopEnrollStatusHelper> statusesPage = statusesCall.execute().body();

                    if (statusesPage != null) {
                        List<WorkshopEnrollStatusHelper> statuses = statusesPage.getContentList();
                        for (WorkshopEnrollStatusHelper s : statuses) {
                            for (Workshop w : workshopsPage.getContentList()) {
                                if (w.getId() == s.getWorkshopId()) {
                                    w.setEnrollStatus(s.getEnrollStatus());
                                }
                            }
                        }
                    }
                }

            } catch (IOException ex) {}

            if (workshopsPage != null) cache.storeObject(WORKSHOPS_LIST_CACHE_KEY, workshopsPage);
        }

        if (workshopsPage != null) {
            rc.setObject(workshopsPage.getContentList());
            rc.setCode(RESPONSE_SUCCESS);
            rc.setMessage("Success");
        } else {
            rc.setObject(null);
            rc.setCode(RESPONSE_ERROR);
            rc.setMessage("Failed to acquire workshops.");
        }

        return rc;
    }

    public ResponseContainer<Workshop> enrollToWorkshop(String accessToken, Workshop workshop,
                                                        WorkshopEvent event, String answer) {

        ResponseContainer<Workshop> rc = new ResponseContainer<>();

        workshop.setEnrollStatus(Workshop.EnrollStatus.PENDING);

        rc.setObject(workshop);
        rc.setMessage("Success.");
        rc.setCode(RESPONSE_SUCCESS);

        Log.d("enrollToWorkshop", workshop.getName());
        Log.d("enrollToWorkshop", Long.toString(event.getId()));
        Log.d("enrollToWorkshop", answer);

        return rc;
    }

    public void asyncUpdateUsersList(String accessToken) {
        synchronized (mUserListFetchingLock) {
            if (!mIsUserListFetching) {
                new UsersListFetcher().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, accessToken);
                mIsUserListFetching = true;
            }
        }
    }

    public void registerUsersListFetcherListener(UsersListFetcherListener listener) {
        mUsersListFetcherListeners.add(listener);
    }

    public void unregisterUsersListFetcherListener(UsersListFetcherListener listener) {
        mUsersListFetcherListeners.remove(listener);
    }

    public void notifyUsersListFetcherListener(int fetchedCount, int totalCount) {
        for (UsersListFetcherListener l : mUsersListFetcherListeners) {
            l.onProgressOnUserFetching(fetchedCount, totalCount);
        }
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

    public interface UsersListFetcherListener {
        void onProgressOnUserFetching(int fetchedCount, int totalCount);
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

        @GET("public/pictures")
        Call<ContentPage<ImagePost>> getPhotoWallList(@Query("page") int page);

        @GET("public/photoshop_battles_submissions")
        Call<ContentPage<BattlesPost>>getPhotoshopBattlesList(@Query("page") int page);

        @GET("public/workshops")
        Call<ContentPage<Workshop>> getWorkshopsList();

        @GET("workshops")
        Call<ContentPage<WorkshopEnrollStatusHelper>>
        getWorkshopsEnrollStatusesList(@Header("Authorization") String accessToken);

        @GET("users")
        Call<ContentPage<User>> getUsersPage(@Header("Authorization") String accessToken,
                                             @Query("page") int page);

        @POST("users/{user_id}/checkins")
        Call<CheckinDateForCheckinProcess> checkinUser(@Header("Authorization") String accessToken,
                                                       @Path("user_id") long userId,
                                                       @Query("day") String dayTag);

//        @POST("workshops")
    }


    private class UsersListFetcher extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... args) {

            String accessToken = args[0];
            ArrayList<User> list = null;

            CacheProvider cache = CacheProvider.getCacheProvider();

            if (!TextUtils.isEmpty(accessToken)) {
                list = new ArrayList<>();

                EcesconAPI api = createService(EcesconAPI.class);
                ContentPage<User> page = null;
                int curPage = 1;

                do {
                    Call<ContentPage<User>> call = api.getUsersPage("Bearer " + accessToken, curPage);

                    try {
                        page = call.execute().body();
                    } catch(Exception ex) {
                        list = null;
                        break;
                    }

                    if (page != null) {
                        list.addAll(page.getContentList());
                        publishProgress(page.getCurrentPage(), page.getTotalPages());
                    }
                    ++curPage;

                } while (page != null && page.getContentList().size() > 0);
            }

            synchronized (mUserListFetchingLock) {
                // Update memory and disk cache.
                if (list != null) {
                    cache.storeObject(USERS_LIST_CACHE_KEY, list);
                    mCachedUsersList = list;
                    mUserListLastUpdate = ZonedDateTime.now(ZoneId.systemDefault());
                    cache.storeObject(USERS_LIST_CACHE_LAST_UPDATE_KEY, mUserListLastUpdate);
                }

                mIsUserListFetching = false;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            notifyUsersListFetcherListener(values[0], values[1]);
        }
    }
}
