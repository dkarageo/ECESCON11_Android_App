/*
 * LocalNotificationsManager.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.notifications;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.MainActivity;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.cache.CacheProvider;
import com.sfhmmy.mobile.receivers.AlarmReceiver;
import com.sfhmmy.mobile.receivers.BootCompletedReceiver;

import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import androidx.core.app.NotificationCompat;


public class LocalNotificationsManager {

    private static LocalNotificationsManager mNotificationsManager;

    private static final String NOTIFICATIONS_STORAGE_KEY
            = ".notifications.LocalNotificationsManager.NOTIFICATIONS";

    private ArrayList<Notification> mNotifications;


    public static LocalNotificationsManager getNotificationsManager() {
        if (mNotificationsManager == null) mNotificationsManager = new LocalNotificationsManager();
        return mNotificationsManager;
    }

    private LocalNotificationsManager() {
        restoreNotifications();
    }

    public void sendNotification(Notification n) {

        // Don't add notifications with the same id twice.
        for (Notification ln : mNotifications) {
            if (n.getId() == ln.getId()) return;
        }

        PackageManager pm = App.getAppContext().getPackageManager();
        ComponentName receiver = new ComponentName(App.getAppContext(), BootCompletedReceiver.class);
        Intent alarmIntent = new Intent(App.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(App.getAppContext(), 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager) App.getAppContext().getSystemService(App.getAppContext().ALARM_SERVICE);


        ZonedDateTime curTime = ZonedDateTime.now(ZoneId.systemDefault());
        long millisUntilNotification = ChronoUnit.MILLIS.between(curTime, n.getTime());

        Log.d("LocalNotifications",
                String.format("Registered notification in %d millis.", millisUntilNotification));

        Log.d("LocalNotifications", curTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
        Log.d("LocalNotifications", n.getTime().format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        if (manager != null) {
            manager.setExact(AlarmManager.RTC_WAKEUP, millisUntilNotification, pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis()+millisUntilNotification,
                        pendingIntent
                );
            }
            Log.d("LocalNotifications", "registered");
        }

        // Enable Boot Receiver class.
        pm.setComponentEnabledSetting(receiver,
                                      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                      PackageManager.DONT_KILL_APP);

        saveNotification(n);
    }

    public List<Notification> getNotificationsInRangeFromNow(long millisRange) {
        ZonedDateTime curTime = ZonedDateTime.now(ZoneId.systemDefault());

        List<Notification> nearbyNotifications = new ArrayList<>();
        for (Notification n : mNotifications) {
            long d = ChronoUnit.MILLIS.between(curTime, n.getTime());
            if (Math.abs(d) <= millisRange) nearbyNotifications.add(n);
        }

        return nearbyNotifications;
    }

    public void reregisterAllNotifications() {
        for (Notification n : mNotifications) {
            sendNotification(n);
        }
    }

    /**
     * Create and show a simple notification.
     *
     * @param messageBody Body of notification.
     * @param messageTitle Title of notification.
     */
    public static void sendNotificationNow(Context context, String messageBody, String messageTitle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "local_notifications_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ecescon_icon)
                        .setContentTitle(messageTitle)
                        .setColor(context.getResources().getColor(R.color.colorPrimary))
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVibrate(new long[] { 1000, 1000, 1000 })
                        .setLights(Color.YELLOW, 2000, 2000)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) App.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "General ECESCON11 Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 , notificationBuilder.build());
    }

    public synchronized void markNotificationAsHandled(Notification n) {
        mNotifications.remove(n);
        saveNotifications();
    }

    private synchronized void saveNotification(Notification n) {
        mNotifications.add(n);
        saveNotifications();
    }

    private synchronized void saveNotifications() {
        CacheProvider cache = CacheProvider.getCacheProvider();
        cache.storeObject(NOTIFICATIONS_STORAGE_KEY, mNotifications);
    }

    private void restoreNotifications() {
        CacheProvider cache = CacheProvider.getCacheProvider();
        ArrayList<Notification> nots
                = (ArrayList<Notification>) cache.retrieveObject(NOTIFICATIONS_STORAGE_KEY);

        if (nots != null) mNotifications = nots;
        else mNotifications = new ArrayList<>();
    }

}
