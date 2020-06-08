package xyz.bcfriends.dra;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class UserPromptActivity extends AppCompatActivity implements UserPromptDialog.BottomSheetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prompt);

//        Objects.requireNonNull(getSupportActionBar()).hide();
        UserPromptDialog dialog = new UserPromptDialog();
        dialog.show(getSupportFragmentManager(), "exampleBottomSheet");
    }

    @Override
    public void onButtonClicked(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismissed() {
        finish();
    }
}
