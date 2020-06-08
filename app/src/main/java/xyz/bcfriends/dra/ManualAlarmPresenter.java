package xyz.bcfriends.dra;

import androidx.annotation.NonNull;
import xyz.bcfriends.dra.util.Alarm;

import java.util.Calendar;

public class ManualAlarmPresenter implements Alarm.Presenter {
    @Override
    public void scheduleAlarm(@NonNull Calendar alarmTime) {

    }

    @Override
    public void cancelAlarm() {

    }

    @Override
    public Calendar getScheduleTime() {
        return null;
    }
}
