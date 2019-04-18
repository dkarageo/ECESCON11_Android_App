/*
 * Notification.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.notifications;

import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;


public class Notification implements Serializable {

    private long mId;
    private String mTitle;
    private String mBody;
    private ZonedDateTime mTime;


    public long getId() { return mId; }
    public String getTitle() { return mTitle; }
    public String getBody() { return mBody; }
    public ZonedDateTime getTime() { return mTime; }

    public void setId(long id) { mId = id; }
    public void setTitle(String title) { mTitle = title; }
    public void setBody(String body) { mBody = body; }
    public void setTime(ZonedDateTime time) { mTime = time; }
}
