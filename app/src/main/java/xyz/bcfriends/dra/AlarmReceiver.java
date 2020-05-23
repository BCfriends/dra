package xyz.bcfriends.dra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String CHANNEL_ID = "alarm_receiver";

        final NotificationImpl noti = new NotificationImpl(context);
        noti.createNotificationChannel(CHANNEL_ID, "Alarm", "정기 알람 채널");
        Intent notificationIntent = new Intent(context, UserPromptActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Dra")
                .setContentText("오늘의 기분은 어떠신가요?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        noti.notify("alarm", builder.build());
    }
}
