package xyz.bcfriends.dra;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_btns);

        BtnOnClickListener btnClickListener = new BtnOnClickListener();

        Button btn = findViewById(R.id.btn_push_listener);
        btn.setOnClickListener(btnClickListener);
    }


    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_push_listener:
                    btnPushListener(v);
                    break;
                default:
                    break;
            }
        }

        void btnPushListener(View v) {
            final String CHANNEL_ID = "test";

            final NotificationImpl noti = new NotificationImpl(TestActivity.this);
            noti.createNotificationChannel(CHANNEL_ID, "테스트 알림 채널", "테스트용 알림 채널");
            final Intent intent = new Intent(TestActivity.this, UserPromptActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            PendingIntent pendingIntent = PendingIntent.getActivity(TestActivity.this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(TestActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Dra")
                    .setContentText("오늘의 기분은 어떠신가요?")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            noti.notify("test", builder.build());
        }
    }
}
