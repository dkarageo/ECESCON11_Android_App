/*
 * CheckinDate.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.users;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import org.threeten.bp.ZonedDateTime;


public class CheckinDate implements Serializable, Parcelable {

    public static final Parcelable.Creator<CheckinDate> CREATOR = new Parcelable.Creator<CheckinDate>() {
        @Override
        public CheckinDate createFromParcel(Parcel source) {
            return new CheckinDate(source);
        }

        @Override
        public CheckinDate[] newArray(int size) {
            return new CheckinDate[size];
        }
    };

    @SerializedName("id")
    private long mId;
    @SerializedName("created_at")
    private ZonedDateTime mDate;
    @SerializedName("day")
    private String mDayTag;  // TODO: Convert to enum. For now "first", "second", "third".


    public CheckinDate() {}

    public CheckinDate(Parcel source) {
        mId     = source.readLong();
        mDate   = (ZonedDateTime) source.readSerializable();
        mDayTag = source.readString();
    }

    public long getId() { return mId; }
    public ZonedDateTime getDate() { return mDate; }
    public String getDayTag() { return mDayTag; }

    public void setId(long id) { mId = id; }
    public void setDate(ZonedDateTime date) { mDate = date; }
    public void setDayTag(String dayTag) { mDayTag = dayTag; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeSerializable(mDate);
        dest.writeString(mDayTag);
    }
}
