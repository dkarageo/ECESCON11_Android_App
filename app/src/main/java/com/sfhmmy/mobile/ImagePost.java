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

import org.threeten.bp.ZonedDateTime;


public class ImagePost {

    private String        mUploader;
    private ZonedDateTime mUploadedDate;
    private String        mDescription;
    private String        mImageUrl;

    public String getUploader() { return mUploader; }
    public ZonedDateTime getUploadedDate() { return mUploadedDate; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }

    public void setUploader(String uploader) { mUploader = uploader; }
    public void setUploadedDate(ZonedDateTime uploadedDate) { mUploadedDate = uploadedDate; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}
