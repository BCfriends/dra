package xyz.bcfriends.dra;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import xyz.bcfriends.dra.util.Alarm;
import xyz.bcfriends.dra.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PreferencesFragment extends PreferenceFragmentCompat implements DBHelper.Executor {
    private static final String TAG = "PreferencesFragment";
    SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
//        prefs = requireActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        super.onPreferenceTreeClick(preference);
        final Intent alarmIntent = new Intent(requireActivity(), DailyAlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), Alarm.DEFAULT_CHANNEL, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Alarm.Presenter presenter = new AlarmPresenter(requireActivity(), prefs, pendingIntent);

        switch (preference.getKey()) {
            case "google_login":
                Intent intent = new Intent(requireActivity(), GoogleSignInActivity.class);
                startActivity(intent);
                break;
            case "alarm_daily":
                if (prefs.getBoolean("alarm_daily", false)) {
                    presenter.scheduleAlarm();
                }
                else {
                    presenter.cancelAlarm();
                }
                break;
            case "alarm_time":
                Calendar cal = presenter.getScheduleTime();
                int hourOfDay, minute;

                hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(requireActivity(), android.R.style.Theme_Material_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        presenter.scheduleAlarm(cal);
                    }
                }, hourOfDay, minute, false);

                dialog.show();
                break;
            case "alarm_manual_prefs":
                Navigation.findNavController(requireView()).navigate(R.id.action_PreferencesFragment_to_alarmPreferencesFragment);
                break;
            case "app_info":
                new AlertDialog.Builder(requireActivity())
                        .setTitle("앱 정보")
                        .setMessage("Dra v" + BuildConfig.VERSION_NAME)
                        .setPositiveButton("OK", null)
                        .create()
                        .show();
                break;
            case "db_test":
                try {
                    FirestoreHelper helper = new FirestoreHelper(this);
                    Map<String, Object> data = new HashMap<>();
                    data.put("depressStatus", 5);
                    helper.writeData(DBHelper.DEFAULT, data);
                    Toast.makeText(requireActivity(), "작업을 실행했습니다.", Toast.LENGTH_SHORT).show();
                } catch (UnsupportedOperationException ignored) {

                }
                break;
            case "get_firebase_id":
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();

                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("InstanceID")
                                .setMessage(token)
                                .setPositiveButton("OK", null)
                                .create()
                                .show();
                    }
                });
                break;
            case "debug":
                intent = new Intent(requireActivity(), TestActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }

        return true;
    }

    public void showAlarmMessage(Calendar scheduleTime) {
        String nextNotifyDate = new SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분", Locale.getDefault()).format(scheduleTime.getTime());
        Toast.makeText(requireActivity(), nextNotifyDate + "으로 알림이 설정되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showResult(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
