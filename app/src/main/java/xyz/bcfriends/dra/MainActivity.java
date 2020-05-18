package xyz.bcfriends.dra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_btns);

        BtnOnClickListener BonClickListener = new BtnOnClickListener();

        Button btn = findViewById(R.id.btn_push_listener);
        btn.setOnClickListener(BonClickListener);
    }


    class BtnOnClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_push_listener:
                    btnPushListener(v);
                    break;
            }
        }

        void btnPushListener(View v) {
            final String CHANNEL_ID = "main";

            final NotificationImpl noti = new NotificationImpl(MainActivity.this);
            noti.createNotificationChannel(CHANNEL_ID, "알림 채널", "테스트용 알림 채널");
            Button btn = findViewById(R.id.push_btn);
            final Intent intent = new Intent(MainActivity.this, UserPromptActivity.class);


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
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
