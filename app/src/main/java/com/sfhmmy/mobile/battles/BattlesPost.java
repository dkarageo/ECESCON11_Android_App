/*
 * BattlesPost.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.battles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;


public class BattlesPost  implements Serializable, Parcelable {

    public static final Parcelable.Creator<BattlesPost> CREATOR = new Parcelable.Creator<BattlesPost>() {
        @Override
        public BattlesPost createFromParcel(Parcel in) {
            return new BattlesPost(in);
        }

        @Override
        public BattlesPost[] newArray(int size) {
            return new BattlesPost[size];
        }
    };

    @SerializedName("id")
    private long mId;
    @SerializedName("created_at")
    private ZonedDateTime mUploadedDate;
    private String mDescription;
    @SerializedName("image_url")
    private String mImageUrl;


    public BattlesPost() {}

    public BattlesPost(Parcel in) {
        mId           = in.readInt();
        mDescription  = in.readString();
        mImageUrl     = in.readString();
        mUploadedDate = (ZonedDateTime) in.readSerializable();
    }

    public long getId() { return mId; }
    public ZonedDateTime getUploadedDate() { return mUploadedDate; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }

    public void setId(long id) { mId = id; }
    public void setUploadedDate(ZonedDateTime uploadedDate) { mUploadedDate = uploadedDate; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mDescription);
        dest.writeString(mImageUrl);
        dest.writeSerializable(mUploadedDate);
    }
}
