/*
 * CheckinDateForCheckinProcess.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.checkins;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;


/**
 * A class for handling the absolute inconsistency across remote API fields and names. The same
 * resources are returned from different endpoints with different fields and names.
 */
public class CheckinDateForCheckinProcess implements Serializable {

    @SerializedName("id")
    private long mId;
    @SerializedName("check_in_date")
    private ZonedDateTime mDate;
    @SerializedName("user_id")
    private long mUserId;


    public long getId() { return mId; }
    public ZonedDateTime getDate() { return mDate; }
    public long getUserId() { return mUserId; }

    public void setId(long id) { mId = id; }
    public void setDate(ZonedDateTime date) { mDate = date; }
    public void setUserId(long userId) { mUserId = userId; }

}
