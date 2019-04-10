/*
 * Faculty.java
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


public class Faculty implements Serializable {

    @SerializedName("id")
    long mId;
    @SerializedName("name")
    String mName;
    @SerializedName("schools_count")
    long mSchoolsCount;


    public long getId() { return mId; }
    public String getName() { return mName; }
    public long getSchoolsCount() { return mSchoolsCount; }

    public void setId(long id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setSchoolsCount(long schoolsCount) { mSchoolsCount = schoolsCount; }
}
