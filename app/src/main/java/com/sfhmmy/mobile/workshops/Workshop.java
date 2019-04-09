/*
 * Workshop.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.workshops;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.ZonedDateTime;


public class Workshop implements Parcelable {

    public static final Parcelable.Creator<Workshop> CREATOR = new Parcelable.Creator<Workshop>() {
        @Override
        public Workshop createFromParcel(Parcel in) {
            return new Workshop(in);
        }

        @Override
        public Workshop[] newArray(int size) {
            return new Workshop[size];
        }
    };

    public enum EnrollStatus {
        AVAILABLE,
        UNAVAILABLE,
        ACCEPTED,
        REJECTED,
        PENDING
    }

    private int           mId;
    private String        mName;
    private String        mDescription;
    private String        mImageUrl;
    private String        mJoinQuestion;
    private String        mPlace;
    private ZonedDateTime mBeginDate;
    private ZonedDateTime mEndDate;
    private EnrollStatus  mEnrollStatus;


    public Workshop() {}

    public Workshop(Parcel in) {
        mId           = in.readInt();
        mName         = in.readString();
        mDescription  = in.readString();
        mImageUrl     = in.readString();
        mJoinQuestion = in.readString();
        mPlace        = in.readString();
        mBeginDate    = (ZonedDateTime) in.readSerializable();
        mEndDate      = (ZonedDateTime) in.readSerializable();
        mEnrollStatus = (EnrollStatus) in.readSerializable();
    }

    public int getId() { return mId; }
    public String getName() { return mName; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }
    public String getJoinQuestion() { return mJoinQuestion; }
    public String getPlace() { return mPlace; }
    public ZonedDateTime getBeginDate() { return mBeginDate; }
    public ZonedDateTime getEndDate() { return mEndDate; }
    public EnrollStatus getEnrollStatus() { return mEnrollStatus; }

    public void setId(int id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
    public void setJoinQuestion(String joinQuestion) { mJoinQuestion = joinQuestion; }
    public void setPlace(String place) { mPlace = place; }
    public void setBeginDate(ZonedDateTime dateTime) { mBeginDate = dateTime; }
    public void setEndDate(ZonedDateTime endDate) { mEndDate = endDate; }
    public void setEnrollStatus(EnrollStatus enrollStatus) { mEnrollStatus = enrollStatus; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mImageUrl);
        dest.writeString(mJoinQuestion);
        dest.writeString(mPlace);
        dest.writeSerializable(mBeginDate);
        dest.writeSerializable(mEndDate);
        dest.writeSerializable(mEnrollStatus);
    }
}
