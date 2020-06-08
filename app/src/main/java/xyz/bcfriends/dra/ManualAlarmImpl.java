package xyz.bcfriends.dra;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import androidx.annotation.NonNull;
import xyz.bcfriends.dra.util.Alarm;

import java.util.Calendar;

public class ManualAlarmImpl implements Alarm {
    @Override
    public void setSchedule(PendingIntent pendingIntent) {

    }

    @Override
    public void unsetSchedule(PendingIntent pendingIntent) {

    }

    @Override
    public void setBootReceiver(@NonNull Class<? extends BroadcastReceiver> receiver) {

    }

    @Override
    public Calendar loadScheduleTime() {
        return null;
    }

    @Override
    public void saveScheduleTime(Calendar scheduleTime) {

    }
}
