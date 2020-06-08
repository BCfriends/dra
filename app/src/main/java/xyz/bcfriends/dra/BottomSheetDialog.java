package xyz.bcfriends.dra;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.bcfriends.dra.util.DBHelper;
import xyz.bcfriends.dra.util.DepressStatusUtil;

public class BottomSheetDialog extends BottomSheetDialogFragment implements DBHelper.Executor {

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
        View calendarLayout = inflater.inflate(R.layout.calendar, container, false);

        Calendar calendar = Calendar.getInstance();
        CalendarView calendarView = calendarLayout.findViewById(R.id.calendarView);
        List<EventDay> events = new ArrayList<>();

        Button insertBtn = view.findViewById(R.id.bsInsertBtn);
//        DataBaseHelper dbh = DataBaseHelper.getInstance(requireActivity());

        insertBtn.setOnClickListener(v -> {
            RadioGroup rg = view.findViewById(R.id.fellingRadioGroup);

            Drawable da;
            int drawable;

            int radioButtonId = rg.getCheckedRadioButtonId();
            if (radioButtonId == -1) {
                return;
            }

            View radioButton = rg.findViewById(radioButtonId);
            int idx = rg.indexOfChild(radioButton);

            LocalDate localDate = LocalDateTime
                    .ofInstant(eventDay.getCalendar().toInstant(), eventDay.getCalendar().getTimeZone().toZoneId())
                    .toLocalDate();

            try {
                FirestoreHelper helper = new FirestoreHelper(this);
                Map<String, Object> data = new HashMap<>();
                data.put("depressStatus", idx + 1);

                drawable = DepressStatusUtil.getDepressDrawObj(idx + 1);

                da = getResources().getDrawable(drawable, null);
                da.setTint(Color.BLUE);

                events.add(new EventDay((Calendar) calendar.clone(), da));
                calendarView.setEvents(events);

                Log.d("a", String.valueOf(getChildFragmentManager()));

                helper.writeData(localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), data);
            } catch (UnsupportedOperationException ignored) {

            } finally {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void showResult(String message) {

    }
}
