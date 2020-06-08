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
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import xyz.bcfriends.dra.util.AlarmPresenter;
import xyz.bcfriends.dra.util.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PreferencesFragment extends PreferenceFragmentCompat {
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
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmPresenter presenter = new AlarmPresenter(requireActivity(), prefs, pendingIntent);

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
            case "app_info":
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("앱 정보").setMessage("Dra v" + BuildConfig.VERSION_NAME);
                builder.setPositiveButton("OK", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case "db_test":
                FirestoreHelper helper = new FirestoreHelper();
                if (!helper.IsUserExist()) {
                    Toast.makeText(requireActivity(), "먼저 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Map<String, Object> data = new HashMap<>();
                data.put("depressStatus", 5);
                helper.writeData(DBHelper.DEFAULT, data);
                Toast.makeText(requireActivity(), "작업을 실행했습니다.", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                        builder.setTitle("InstanceID").setMessage(token);
                        builder.setPositiveButton("OK", null);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
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
}
