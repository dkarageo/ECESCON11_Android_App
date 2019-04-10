/*
 * Institution.java
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


public class Institution implements Serializable {

    @SerializedName("id")
    long mId;
    @SerializedName("name")
    String mName;
    @SerializedName("schools_count")
    int mSchoolsCount;
    @SerializedName("faculties_count")
    int mFacultiesCount;
    @SerializedName("category")
    String mCategory;


    public long getId() { return mId; }
    public String getName() { return mName; }
    public long getSchoolsCount() { return mSchoolsCount; }
    public long getFacultiesCount() { return mFacultiesCount; }
    public String getCategory() { return mCategory; }

    public void setId(long id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setSchoolsCount(int schoolsCount) { mSchoolsCount = schoolsCount; }
    public void setFacultiesCount(int facultiesCount) { mFacultiesCount = facultiesCount; }
    public void setCategory(String category) { mCategory = category; }
}
