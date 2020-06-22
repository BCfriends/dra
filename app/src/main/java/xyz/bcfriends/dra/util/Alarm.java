package xyz.bcfriends.dra.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import androidx.annotation.NonNull;

import java.util.Calendar;

public interface Alarm {
    int DEFAULT_CHANNEL = 0;
    int MANUAL_CHANNEL = 1;

    void setSchedule(PendingIntent pendingIntent);
    void unsetSchedule(PendingIntent pendingIntent);
    void setBootReceiver(@NonNull Class<? extends BroadcastReceiver> receiver);
    Calendar loadScheduleTime();
    void saveScheduleTime(Calendar scheduleTime);

    interface Presenter {
        void scheduleAlarm();
        void scheduleAlarm(@NonNull Calendar alarmTime);
        void cancelAlarm();
        Calendar getScheduleTime();
    }
}
