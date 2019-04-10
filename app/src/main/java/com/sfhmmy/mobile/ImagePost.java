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

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;


public class ImagePost implements Serializable {

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
    @SerializedName("uploader_username")
//    private String mUploaderUsername;
//    @SerializedName("picture_mime")
//    private String mPictureMime;
//    @SerializedName("picture")
//    private String mPictureFilename;

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
}
