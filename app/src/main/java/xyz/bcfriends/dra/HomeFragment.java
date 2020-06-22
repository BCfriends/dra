package xyz.bcfriends.dra;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import xyz.bcfriends.dra.util.DBHelper;
import xyz.bcfriends.dra.util.DepressStatus;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


public class HomeFragment extends Fragment implements DBHelper.Executor {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar, container, false);

        Calendar calendar = Calendar.getInstance();
        CalendarView calendarView = v.findViewById(R.id.calendarView);

        List<EventDay> events = new ArrayList<>();

        try {
            FirestoreHelper helper = new FirestoreHelper(this);
            LocalDate ld = LocalDate.now();

            ld.with(TemporalAdjusters.firstDayOfMonth());

            calendarView.setDate(calendar);

            helper.readDataAll(
                    helper.getDatabase().collection("users")
                            .document(Objects.requireNonNull(helper.getUser().getUid()))
                            .collection("Records"),

                    new DBHelper.QueryCallback() {
                        @Override
                        public void onCallback(@Nullable QuerySnapshot data) {
                            Drawable da;
                            int drawable;

                            String depressStatus;

                            if (data != null) {
                                for (QueryDocumentSnapshot document : data) {
                                    LocalDate dbLD = LocalDate.parse(document.getId());
                                    calendar.set(dbLD.getYear(), dbLD.getMonthValue() - 1, dbLD.getDayOfMonth());

                                    depressStatus = String.valueOf(document.getData().get("depressStatus"));

                                    drawable = DepressStatus.getDepressDrawObj(depressStatus);

                                    da = getResources().getDrawable(drawable, null);
                                    da.setTint(Color.BLUE);

                                    events.add(new EventDay((Calendar) calendar.clone(), da));
                                }
                            }
                            Log.d("Date Debug", String.valueOf(events.size()));

                            calendarView.setEvents(events);
                            v.findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    }
            );

        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            v.findViewById(R.id.progressBar).setVisibility(View.GONE);
        } finally {
            calendarView.setOnDayClickListener(eventDay -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(eventDay);

                bottomSheetDialog.show(requireActivity().getSupportFragmentManager(), "bottomSheet");
            });
        }

        return v;
    }

    @Override
    public void showResult(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
