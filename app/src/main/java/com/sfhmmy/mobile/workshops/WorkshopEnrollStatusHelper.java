/*
 * WorkshopEnrollStatusHelper.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.workshops;

import com.google.gson.annotations.SerializedName;


public class WorkshopEnrollStatusHelper {

    @SerializedName("id")
    private long mWorkshopId;
    private Workshop.EnrollStatus mEnrollStatus;

    public long getWorkshopId() { return mWorkshopId; }
    public Workshop.EnrollStatus getEnrollStatus() { return mEnrollStatus; }

    public void setWorkshopId(long id) { mWorkshopId = id; }
    public void setEnrollStatus(Workshop.EnrollStatus status) { mEnrollStatus = status; }
}
