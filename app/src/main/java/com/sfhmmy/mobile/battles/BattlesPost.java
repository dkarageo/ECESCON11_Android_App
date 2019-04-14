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

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;


public class BattlesPost  implements Serializable {

    @SerializedName("id")
    private long mId;
    @SerializedName("created_at")
    private ZonedDateTime mUploadedDate;
    private String mDescription;
    @SerializedName("image_url")
    private String mImageUrl;

    public long getId() { return mId; }
    public ZonedDateTime getUploadedDate() { return mUploadedDate; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }

    public void setId(long id) { mId = id; }
    public void setUploadedDate(ZonedDateTime uploadedDate) { mUploadedDate = uploadedDate; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}
