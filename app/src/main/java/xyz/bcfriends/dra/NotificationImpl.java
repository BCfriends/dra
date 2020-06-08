package xyz.bcfriends.dra;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationImpl {
    final Context mContext;
    final NotificationManager notificationManager;

    public NotificationImpl(Context context) {
        this.mContext = context;
        this.notificationManager = mContext.getSystemService(NotificationManager.class);
    }

    public void createNotificationChannel(String channel_id, String channel_name, String channel_description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, importance);
            channel.setDescription(channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void notify(String notify_tag, Notification notification) {
        notificationManager.notify(notify_tag, 1, notification);
    }
}
