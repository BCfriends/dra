package xyz.bcfriends.dra;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.NonNull;
import xyz.bcfriends.dra.util.Alarm;

import java.util.Calendar;
import java.util.Objects;

public class DailyAlarmImpl implements Alarm {
    private final Context mContext;
    private final SharedPreferences prefs;
    private final AlarmManager alarmManager;

    DailyAlarmImpl(Context mContext, SharedPreferences prefs) {
        this.mContext = mContext;
        this.prefs = prefs;
        this.alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        try {
            Objects.requireNonNull(this.alarmManager);
        } catch (NullPointerException e) {
            Log.e("AlarmImpl", "AlarmManager is null");
        }
    }

    @Override
    public void setSchedule(PendingIntent pendingIntent) {
        Calendar alarmTime = loadScheduleTime();

        unsetSchedule(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        }
//        else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        }

    }

    @Override
    public void unsetSchedule(PendingIntent pendingIntent) {
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void setBootReceiver(@NonNull Class<? extends BroadcastReceiver> receiver) {
        PackageManager pm = mContext.getPackageManager();
        ComponentName component = new ComponentName(mContext, receiver);

        pm.setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public Calendar loadScheduleTime() {
        if (!(prefs.contains("hourOfDay") && prefs.contains("minute"))) {
            return Calendar.getInstance();
        }

        Calendar cal = Calendar.getInstance();
        Calendar origCal = (Calendar) cal.clone();

        int hourOfDay = prefs.getInt("hourOfDay", -1);
        int minute = prefs.getInt("minute", -1);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);

        if (cal.before(origCal)) {
            cal.add(Calendar.DATE, 1);
        }

        return cal;
    }

    public void saveScheduleTime(Calendar scheduleTime) {
        int hourOfDay = scheduleTime.get(Calendar.HOUR_OF_DAY);
        int minute = scheduleTime.get(Calendar.MINUTE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("hourOfDay", hourOfDay);
        editor.putInt("minute", minute);
        editor.apply();
    }


}
