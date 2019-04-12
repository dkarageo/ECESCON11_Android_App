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

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;


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

    @SerializedName("id")
    private int mId;
    @SerializedName("title")
    private String mName;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("join_question")
    private String mJoinQuestion;
    private EnrollStatus mEnrollStatus;
    @SerializedName("workshop_programs")
    private ArrayList<WorkshopEvent> mEvents;

    public Workshop() {}

    public Workshop(Parcel in) {
        mId           = in.readInt();
        mName         = in.readString();
        mDescription  = in.readString();
        mImageUrl     = in.readString();
        mJoinQuestion = in.readString();
        mEvents       = (ArrayList<WorkshopEvent>) in.readSerializable();
        mEnrollStatus = (EnrollStatus) in.readSerializable();
    }

    public int getId() { return mId; }
    public String getName() { return mName; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }
    public String getJoinQuestion() { return mJoinQuestion; }
    public EnrollStatus getEnrollStatus() { return mEnrollStatus; }
    public List<WorkshopEvent> getWorkshopEvents() { return mEvents; }

    public void setId(int id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
    public void setJoinQuestion(String joinQuestion) { mJoinQuestion = joinQuestion; }
    public void setEnrollStatus(EnrollStatus enrollStatus) { mEnrollStatus = enrollStatus; }

    public void setWorkshopEvents(List<WorkshopEvent> workshopEvents) {
        mEvents = new ArrayList<>(workshopEvents);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mDescription);
        dest.writeString(mImageUrl);
        dest.writeString(mJoinQuestion);
        dest.writeSerializable(mEvents);
        dest.writeSerializable(mEnrollStatus);
    }
}
