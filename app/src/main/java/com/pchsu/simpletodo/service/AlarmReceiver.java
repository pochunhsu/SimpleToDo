package com.pchsu.simpletodo.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.pchsu.simpletodo.R;
import com.pchsu.simpletodo.ui.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(context, "Task Due", "123", "456");
    }

    public void createNotification(Context context, String msgTitle, String msgText,String msgAlert ){
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_assignment_white_48dp)
                .setContentTitle(msgTitle)
                .setTicker(msgAlert)
                .setContentText(msgText);

        builder.setContentIntent(intent);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());
    }
}
