package xyz.bcfriends.dra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.google.firebase.firestore.DocumentSnapshot;
import xyz.bcfriends.dra.util.DBHelper;

public class DailyAlarmReceiver extends BroadcastReceiver implements DBHelper.Executor {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            final FirestoreHelper helper = new FirestoreHelper(this);
            helper.readData(DBHelper.DEFAULT, new DBHelper.QueryCallback() {
                @Override
                public void onCallback(@Nullable DocumentSnapshot data) {
                    if (!(data != null && data.exists())) {
                        execute(context, intent);
                    }
                }
            });
        } catch (UnsupportedOperationException e) {
            execute(context, intent);
        }
    }

    private void execute(Context context, Intent intent) {
        final String CHANNEL_ID = "alarm_receiver";

        final NotificationImpl noti = new NotificationImpl(context);
        noti.createNotificationChannel(CHANNEL_ID, "Alarm", "정기 알람 채널");
        Intent notificationIntent = new Intent(context, UserPromptActivity.class);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
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

    @Override
    public void showResult(String message) {

    }
}
