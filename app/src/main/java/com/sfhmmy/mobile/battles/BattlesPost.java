package com.sfhmmy.mobile.battles;


import org.threeten.bp.ZonedDateTime;

public class BattlesPost  {
    private ZonedDateTime mUploadedDate;
    private String        mDescription;
    private String        mImageUrl;

    public ZonedDateTime getUploadedDate() { return mUploadedDate; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }

    public void setUploadedDate(ZonedDateTime uploadedDate) { mUploadedDate = uploadedDate; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}
