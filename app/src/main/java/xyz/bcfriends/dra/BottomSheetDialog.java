package xyz.bcfriends.dra;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.navigation.Navigation;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.firebase.firestore.DocumentSnapshot;
import xyz.bcfriends.dra.util.DBHelper;
import xyz.bcfriends.dra.util.DepressStatus;

public class BottomSheetDialog extends BottomSheetDialogFragment implements DBHelper.Executor {

    private final EventDay eventDay;

    public BottomSheetDialog(EventDay ed) {
        this.eventDay = ed;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LocalDate localDate = LocalDateTime
                .ofInstant(eventDay.getCalendar().toInstant(), eventDay.getCalendar().getTimeZone().toZoneId())
                .toLocalDate();
        String selectedDay = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        View view = inflater.inflate(R.layout.status_view, container, false);
        View calendarLayout = inflater.inflate(R.layout.calendar, container, false);

        Calendar calendar = Calendar.getInstance();
        CalendarView calendarView = calendarLayout.findViewById(R.id.calendarView);
        List<EventDay> events = new ArrayList<>();
        Button insertBtn = view.findViewById(R.id.bsInsertBtn);
//        DataBaseHelper dbh = DataBaseHelper.getInstance(requireActivity());

        try {
            FirestoreHelper helper = new FirestoreHelper(this);
            helper.readData(selectedDay, new DBHelper.QueryCallback() {
                @Override
                public void onCallback(@Nullable DocumentSnapshot data) {
                    if (data != null && data.exists()) {
                        RadioGroup rg = view.findViewById(R.id.feelingRadioGroup);
                        String depressStatus = Objects.requireNonNull(data.get("depressStatus")).toString();

                        switch (depressStatus) {
                            case DepressStatus.BAD:
                                rg.check(R.id.feelingRadioBad);
                                break;
                            case DepressStatus.SAD:
                                rg.check(R.id.feelingRadioSad);
                                break;
                            case DepressStatus.NORMAL:
                                rg.check(R.id.feelingRadioNormal);
                                break;
                            case DepressStatus.GOOD:
                                rg.check(R.id.feelingRadioGood);
                                break;
                            case DepressStatus.NICE:
                                rg.check(R.id.feelingRadioNice);
                                break;
                            default:

                        }
                        String memo = (String) data.get("memo");
                        EditText ed = view.findViewById(R.id.memo);
                        ed.setText(memo);
                    }
                }
            });
        } catch (UnsupportedOperationException ignored) {

        }

        insertBtn.setOnClickListener(v -> {
            RadioGroup rg = view.findViewById(R.id.feelingRadioGroup);
            EditText ed = view.findViewById(R.id.memo);

            Drawable da;
            int drawable;

            int radioButtonId = rg.getCheckedRadioButtonId();
            if (radioButtonId == -1) {
                return;
            }

            View radioButton = rg.findViewById(radioButtonId);
            int idx = rg.indexOfChild(radioButton);

            try {
                FirestoreHelper helper = new FirestoreHelper(this);
                Map<String, Object> data = new HashMap<>();
                data.put("depressStatus", idx + 1);
                data.put("memo", ed.getText().toString());

                drawable = DepressStatus.getDepressDrawObj(idx + 1);

                da = getResources().getDrawable(drawable, null);
                da.setTint(Color.BLUE);

                events.add(new EventDay((Calendar) calendar.clone(), da));
                calendarView.setEvents(events);

                Log.d("a", String.valueOf(getChildFragmentManager()));

                helper.writeData(selectedDay, data);
            } catch (UnsupportedOperationException ignored) {

            } finally {
                dismiss();
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.homeFragment);
            }
        });

        return view;
    }

    @Override
    public void showResult(String message) {

    }
}
