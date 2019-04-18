/*
 * AlarmReceiver.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sfhmmy.mobile.notifications.LocalNotificationsManager;
import com.sfhmmy.mobile.notifications.Notification;

import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        LocalNotificationsManager manager = LocalNotificationsManager.getNotificationsManager();
        List<Notification> notifications
                = manager.getNotificationsInRangeFromNow(1000 * 30);

        for (Notification n : notifications) {
            LocalNotificationsManager.sendNotificationNow(context, n.getBody(), n.getTitle());
            manager.markNotificationAsHandled(n);
        }

        Log.d("LocalNotifications", "Received alarm.");
    }
}
