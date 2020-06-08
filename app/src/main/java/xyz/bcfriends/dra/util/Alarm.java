package xyz.bcfriends.dra.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import androidx.annotation.NonNull;

import java.util.Calendar;

public interface Alarm {
    public static int DEFAULT_CHANNEL = 0;
    public static int TEST_CHANNEL = 1;
    public static int WEEKLY_CHANNEL = 2;
    public static int MONTHLY_CHANNEL = 3;

    void setSchedule(PendingIntent pendingIntent);
    void unsetSchedule(PendingIntent pendingIntent);
    void setBootReceiver(@NonNull Class<? extends BroadcastReceiver> receiver);
    Calendar loadScheduleTime();
    void saveScheduleTime(Calendar scheduleTime);

    interface Presenter {
        void scheduleAlarm(@NonNull Calendar alarmTime);
        void cancelAlarm();
        Calendar getScheduleTime();
    }
}
