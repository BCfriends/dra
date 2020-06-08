package xyz.bcfriends.dra.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import java.util.Calendar;

public class AlarmPresenter implements Alarm.Presenter {
    private final AlarmImpl alarm;
    private final PendingIntent pendingIntent;
    private final Class<? extends BroadcastReceiver> receiver;

    public AlarmPresenter(Context mContext, SharedPreferences prefs, @NonNull PendingIntent pendingIntent) {
        this(mContext, prefs, pendingIntent, DeviceBootReceiver.class);
    }

    public AlarmPresenter(Context mContext, SharedPreferences prefs, @NonNull PendingIntent pendingIntent, @NonNull Class<? extends BroadcastReceiver> receiver) {
        this.receiver = receiver;
        this.pendingIntent = pendingIntent;
        this.alarm = new AlarmImpl(mContext, prefs);
    }

    public void scheduleAlarm() {
        cancelAlarm();
        alarm.setSchedule(pendingIntent);
        alarm.setBootReceiver(receiver);
    }

    @Override
    public void scheduleAlarm(@NonNull Calendar alarmTime) {
        alarm.saveScheduleTime(alarmTime);
        scheduleAlarm();
    }

    @Override
    public void cancelAlarm() {
        alarm.unsetSchedule(pendingIntent);
    }

    @Override
    public Calendar getScheduleTime() {
        return alarm.loadScheduleTime();
    }
}
