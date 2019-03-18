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

import org.threeten.bp.ZonedDateTime;


public class Workshop {
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
    private ZonedDateTime mDateTime;
    private EnrollStatus  mEnrollStatus;

    public int getId() { return mId; }
    public String getName() { return mName; }
    public String getDescription() { return mDescription; }
    public String getImageUrl() { return mImageUrl; }
    public String getJoinQuestion() { return mJoinQuestion; }
    public String getPlace() { return mPlace; }
    public ZonedDateTime getDateTime() { return mDateTime; }
    public EnrollStatus getEnrollStatus() { return mEnrollStatus; }

    public void setId(int id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setDescription(String description) { mDescription = description; }
    public void setImageUrl(String imageUrl) { mImageUrl = imageUrl; }
    public void setJoinQuestion(String joinQuestion) { mJoinQuestion = joinQuestion; }
    public void setPlace(String place) { mPlace = place; }
    public void setDateTime(ZonedDateTime dateTime) { mDateTime = dateTime; }
    public void setEnrollStatus(EnrollStatus enrollStatus) { mEnrollStatus = enrollStatus; }
}
