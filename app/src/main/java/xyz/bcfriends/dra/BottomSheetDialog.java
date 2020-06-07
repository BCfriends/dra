package xyz.bcfriends.dra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private final EventDay eventDay;

    public static BottomSheetDialog getInstance(EventDay ed) {
        return new BottomSheetDialog(ed);
    }

    private BottomSheetDialog(EventDay ed) {
        this.eventDay = ed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);

        Button insertBtn = view.findViewById(R.id.bsInsertBtn);
        DataBaseHelper dbh = DataBaseHelper.getInstance(requireActivity());
        FirestoreManager FM = new FirestoreManager();

        insertBtn.setOnClickListener(v -> {
            RadioGroup rg = view.findViewById(R.id.fellingRadioGroup);
            int radioButtonId = rg.getCheckedRadioButtonId();
            if (radioButtonId == -1) {
                return;
            }
            View radioButton = rg.findViewById(radioButtonId);
            int idx = rg.indexOfChild(radioButton);

            LocalDate localDate = LocalDateTime
                    .ofInstant(eventDay.getCalendar().toInstant(), eventDay.getCalendar().getTimeZone().toZoneId())
                    .toLocalDate();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(requireActivity(), "먼저 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            FM.FSWriteData(idx + 1);
        });

        return view;
    }
}
