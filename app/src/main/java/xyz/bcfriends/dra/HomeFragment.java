package xyz.bcfriends.dra;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import xyz.bcfriends.dra.util.DBHelper;


public class HomeFragment extends Fragment {

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

        FirestoreHelper firestoreHelper = new FirestoreHelper();
        FirebaseFirestore FDB = FirebaseFirestore.getInstance();
        FirebaseAuth userId = FirebaseAuth.getInstance();

        try {
            LocalDate ld = LocalDate.now();

            ld.with(TemporalAdjusters.firstDayOfMonth());

            String date;
            String datetime;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            calendarView.setDate(calendar);

            firestoreHelper.readDataAll(
                    FDB.collection("users")
                            .document(Objects.requireNonNull(userId.getUid()))
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

                                    switch (depressStatus) {
                                        case DepressStatus.BAD:
                                            drawable = R.drawable.bs_checkbox_feeling_bad;
                                            break;
                                        case DepressStatus.SAD:
                                            drawable = R.drawable.bs_checkbox_feeling_sad;
                                            break;
                                        case DepressStatus.NORMAL:
                                            drawable = R.drawable.bs_checkbox_feeling_normal;
                                            break;
                                        case DepressStatus.GOOD:
                                            drawable = R.drawable.bs_checkbox_feeling_good;
                                            break;
                                        case DepressStatus.NICE:
                                            drawable = R.drawable.bs_checkbox_feeling_nice;
                                            break;
                                        default:
                                            throw new IllegalStateException("Unexpected value: " + depressStatus);
                                    }

                                    da = getResources().getDrawable(drawable, null);
                                    da.setTint(Color.BLUE);

                                    events.add(new EventDay((Calendar) calendar.clone(), da));
                                }
                            }
                            Log.d("Date Debug", String.valueOf(events.size()));

                            calendarView.setEvents(events);
                            requireActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    }
            );


            calendarView.setOnDayClickListener(eventDay -> {
                BottomSheetDialog bottomSheetDialog = BottomSheetDialog.getInstance(eventDay);

                bottomSheetDialog.show(requireActivity().getSupportFragmentManager(), "bottomSheet");
            });

        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        return v;
    }
}
