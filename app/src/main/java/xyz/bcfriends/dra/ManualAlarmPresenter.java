package xyz.bcfriends.dra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import xyz.bcfriends.dra.util.Alarm;
import xyz.bcfriends.dra.util.DeviceBootReceiver;

import java.util.Calendar;

public class ManualAlarmPresenter implements Alarm.Presenter {
    private final Alarm alarm;
    private final PendingIntent pendingIntent;
    private final Class<? extends BroadcastReceiver> receiver;

    public ManualAlarmPresenter(Context mContext, SharedPreferences prefs, @NonNull PendingIntent pendingIntent) {
        this(mContext, prefs, pendingIntent, DeviceBootReceiver.class);
    }

    public ManualAlarmPresenter(Context mContext, SharedPreferences prefs, @NonNull PendingIntent pendingIntent, @NonNull Class<? extends BroadcastReceiver> receiver) {
        this.receiver = receiver;
        this.pendingIntent = pendingIntent;
        this.alarm = new ManualAlarmImpl(mContext, prefs);
    }

    @Override
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
