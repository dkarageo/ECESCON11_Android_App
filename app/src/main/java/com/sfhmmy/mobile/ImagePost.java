/*
 * ImagePost.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.sfhmmy.mobile.workshops.Workshop;
import com.sfhmmy.mobile.workshops.WorkshopEvent;

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.util.ArrayList;


public class ImagePost
        implements Serializable, Parcelable {

    public static final Parcelable.Creator<ImagePost> CREATOR = new Parcelable.Creator<ImagePost>() {
        @Override
        public ImagePost createFromParcel(Parcel in) {
            return new ImagePost(in);
        }

        @Override
        public ImagePost[] newArray(int size) {
            return new ImagePost[size];
        }
    };

    @SerializedName("id")
    private long mId;
    @SerializedName("uploader_name")
    private String mUploader;
    @SerializedName("created_at")
    private ZonedDateTime mUploadedDate;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("url")
    private String mImageUrl;
//    @SerializedName("uploader_username")
//    private String mUploaderUsername;
//    @SerializedName("picture_mime")
//    private String mPictureMime;
//    @SerializedName("picture")
//    private String mPictureFilename;

    public ImagePost() {}

    public ImagePost(Parcel in) {
        mId           = in.readInt();
        mUploader     = in.readString();
        mDescription  = in.readString();
        mImageUrl     = in.readString();
        mUploadedDate = (ZonedDateTime) in.readSerializable();
    }

    public long getId() { return mId; }
    public String getUploader() { return mUploader; }
    public ZonedDateTime getUploadedDate() { return mUploadedDate; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }

    public void setId(long id) { mId = id; }
    public void setUploader(String uploader) { mUploader = uploader; }
    public void setUploadedDate(ZonedDateTime uploadedDate) { mUploadedDate = uploadedDate; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mUploader);
        dest.writeString(mDescription);
        dest.writeString(mImageUrl);
        dest.writeSerializable(mUploadedDate);
    }
}
