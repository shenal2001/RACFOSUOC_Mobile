package com.example.rota1;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.example.rota1.R;


public class NotificationReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_TITLE = "notification-title";
    public static final String NOTIFICATION_TEXT = "notification-text";
    public static final String NOTIFICATION_ID = "notification-id";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(NOTIFICATION_TITLE);
        String text = intent.getStringExtra(NOTIFICATION_TEXT);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "BookingReminder")
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Replace with your app icon
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}
