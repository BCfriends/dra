package xyz.bcfriends.dra;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import xyz.bcfriends.dra.util.Alarm;

import java.util.Calendar;
import java.util.Objects;

public class ManualAlarmImpl implements Alarm {
    private final Context mContext;
    private final SharedPreferences prefs;
    private final AlarmManager alarmManager;

    ManualAlarmImpl(Context mContext, SharedPreferences prefs) {
        this.mContext = mContext;
        this.prefs = prefs;
        this.alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        try {
            Objects.requireNonNull(this.alarmManager);
        } catch (NullPointerException e) {
            Log.e("ManualAlarmImpl", "ManualAlarmManager is null");
        }
    }

    @Override
    public void setSchedule(PendingIntent pendingIntent) {
        Calendar cal = loadScheduleTime();

        unsetSchedule(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
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

    @Override
    public Calendar loadScheduleTime() {
        if (!(prefs.contains("year") && prefs.contains("month") && prefs.contains("dayOfMonth"))) {
            return Calendar.getInstance();
        }

        Calendar cal = Calendar.getInstance();

        int year = prefs.getInt("year", -1);
        int month = prefs.getInt("month", -1);
        int dayOfMonth = prefs.getInt("dayOfMonth", -1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.HOUR_OF_DAY, 6); //temp
        cal.set(Calendar.MINUTE, 0); //temp

        return cal;
    }

    @Override
    public void saveScheduleTime(Calendar scheduleTime) {
        int year = scheduleTime.get(Calendar.YEAR);
        int month = scheduleTime.get(Calendar.MONTH);
        int dayOfMonth = scheduleTime.get(Calendar.DAY_OF_MONTH);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("dayOfMonth", dayOfMonth);
        editor.apply();
    }
}
