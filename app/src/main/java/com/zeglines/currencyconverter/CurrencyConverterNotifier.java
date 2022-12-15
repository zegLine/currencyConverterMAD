package com.zeglines.currencyconverter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class CurrencyConverterNotifier {

    private static final int NOTIFICATION_ID = 123;

    private static String CHANNEL_ID = "currency_converter_channel";
    private static String CHANNEL_DESCRIPTION = "Show Updated Currencies";

    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public CurrencyConverterNotifier(Context context){
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel("currency_converter_channel");
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_DESCRIPTION,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Currency converter...")
                .setAutoCancel(false);


        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        notificationBuilder.setContentIntent(resultPendingIntent);

    }

    public void showOrUpdateNotification() {
        notificationBuilder.setContentText("Currencies have been updated!");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
