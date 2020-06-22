package xyz.bcfriends.dra;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;
import java.util.Objects;

public class UserPromptActivity extends AppCompatActivity implements BottomSheetDialog.BottomSheetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prompt);

//        Objects.requireNonNull(getSupportActionBar()).hide();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(new EventDay(Calendar.getInstance()));

        bottomSheetDialog.show(getSupportFragmentManager(), "bottomSheet");
    }

    @Override
    public void onDismissed() {
        finish();
    }
}
