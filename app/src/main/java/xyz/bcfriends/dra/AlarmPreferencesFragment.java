package xyz.bcfriends.dra;

import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import xyz.bcfriends.dra.util.Alarm;

import java.util.Calendar;

public class AlarmPreferencesFragment extends PreferenceFragmentCompat {
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.alarm_preferences, rootKey);
//        prefs = requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        updatePreferences();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        super.onPreferenceTreeClick(preference);
        final Intent alarmIntent = new Intent(requireActivity(), ManualAlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), Alarm.MANUAL_CHANNEL, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Alarm.Presenter presenter = new ManualAlarmPresenter(requireActivity(), prefs, pendingIntent);

        switch (preference.getKey()) {
            case "alarm_manual":
                if (prefs.getBoolean("alarm_manual", false)) {
                    if (prefs.contains("year") && prefs.contains("month") && prefs.contains("dayOfMonth")) {
                        presenter.scheduleAlarm();
                    }
                }
                else {
                    presenter.cancelAlarm();
                }
                break;
            case "manual_alarm_date":
                Calendar cal = presenter.getScheduleTime();
                int year, month, dayOfMonth;

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(requireActivity(), android.R.style.Theme_Material_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        presenter.scheduleAlarm(cal);
                        updatePreferences();
                    }
                }, year, month, dayOfMonth);

                dialog.show();
                break;
            default:
                return false;
        }

        return true;
    }

    void updatePreferences() {
        if (prefs.contains("year") && prefs.contains("month") && prefs.contains("dayOfMonth")) {
            String s = prefs.getInt("year", -1) + "년 " + Integer.sum(prefs.getInt("month", -1), 1) + "월 " + prefs.getInt("dayOfMonth", -1) + "일 오전 6시에 알림을 받습니다.";

            Preference pref = findPreference("manual_alarm_date");
            if (pref != null) {
                pref.setSummary(s);
            }
        }
    }
}
