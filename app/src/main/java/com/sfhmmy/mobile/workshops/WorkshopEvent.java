/*
 * WorkshopEvent.java
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

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;


public class WorkshopEvent implements Serializable {

    @SerializedName("id")
    private long mId;
    @SerializedName("place")
    private String mLocation;
    @SerializedName("start_date_time")
    private ZonedDateTime mBeginDate;
    @SerializedName("finish_date_time")
    private ZonedDateTime mEndDate;

    public long getId() { return mId; }
    public String getLocation() { return mLocation; }
    public ZonedDateTime getBeginDate() { return mBeginDate; }
    public ZonedDateTime getEndDate() { return mEndDate; }

    public void setId(long id) { mId = id; }
    public void setLocation(String location) { mLocation = location; }
    public void setBeginDate(ZonedDateTime dateTime) { mBeginDate = dateTime; }
    public void setEndDate(ZonedDateTime endDate) { mEndDate = endDate; }
}
