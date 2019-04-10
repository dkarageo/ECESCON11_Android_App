/*
 * EducationRank.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.users;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class EducationRank implements Serializable {

    @SerializedName("id")
    long mId;
    @SerializedName("name")
    String mName;
    @SerializedName("users_count")
    int mUsersCount;

    public long getId() { return mId; }
    public String getName() { return mName; }
    public long getUsersCount() { return mUsersCount; }

    public void setId(long id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setUsersCount(int usersCount) { mUsersCount = usersCount; }
}
