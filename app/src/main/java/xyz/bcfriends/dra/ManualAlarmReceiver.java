package xyz.bcfriends.dra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import xyz.bcfriends.dra.util.DBHelper;

public class ManualAlarmReceiver extends BroadcastReceiver implements DBHelper.Executor {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String CHANNEL_ID = "manual_alarm_receiver";

        final NotificationImpl noti = new NotificationImpl(context);
        noti.createNotificationChannel(CHANNEL_ID, "Alarm", "면담 일정 알림 채널");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Dra")
                .setContentText("오늘 면담 일정이 있습니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        noti.notify("alarm_manual", builder.build());
    }

    @Override
    public void showResult(String message) {

    }
}
