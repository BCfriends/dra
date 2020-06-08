package xyz.bcfriends.dra.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import xyz.bcfriends.dra.DailyAlarmPresenter;
import xyz.bcfriends.dra.DailyAlarmReceiver;

import java.util.Objects;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
//            SharedPreferences prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            Intent alarmIntent = new Intent(context, DailyAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            DailyAlarmPresenter presenter = new DailyAlarmPresenter(context, prefs, pendingIntent);
            presenter.scheduleAlarm();
        }
    }
}
