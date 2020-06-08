package xyz.bcfriends.dra;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import xyz.bcfriends.dra.util.DBHelper;

public class AnalyticsFragment extends Fragment implements DBHelper.Executor {

    BarChart mpBarChart;

    public AnalyticsFragment() {
        // Required empty public constructor
    }

    public static AnalyticsFragment newInstance(String param1, String param2) {
        return new AnalyticsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.graph_stat, container, false);

//        setContentView(R.layout.graph_stat);
        mpBarChart = v.findViewById(R.id.barchart);


        int[][] in = new int[12][5];

        FirestoreHelper helper = new FirestoreHelper(this);
        helper.readDataAll(
                helper.getDatabase().collection("users")
                        .document(Objects.requireNonNull(helper.getUser().getUid()))
                        .collection("Records"),
                new DBHelper.QueryCallback() {
                    @Override
                    public void onCallback(@Nullable QuerySnapshot data) {
                        if (data != null) {
                            for (QueryDocumentSnapshot document : data) {
                                LocalDate mLD = LocalDate.parse(document.getId());
                                in[mLD.getMonthValue() - 1][Integer.parseInt(document.getData().get("depressStatus").toString()) - 1]++;
                                Log.d("month", String.valueOf(mLD.getMonthValue() + 1));
                            }

                            ArrayList<ArrayList<BarEntry>> barEntries = some(in);

                            BarDataSet barDataSet1 = new BarDataSet(barEntries.get(0),"우울함");
                            barDataSet1.setColor(Color.RED);

                            BarDataSet barDataSet2 = new BarDataSet(barEntries.get(1),"슬픔");
                            barDataSet2.setColor(Color.BLUE);

                            BarDataSet barDataSet3 = new BarDataSet(barEntries.get(2),"보통");
                            barDataSet3.setColor(Color.MAGENTA);

                            BarDataSet barDataSet4 = new BarDataSet(barEntries.get(3),"좋음");
                            barDataSet4.setColor(Color.GRAY);

                            BarDataSet barDataSet5 = new BarDataSet(barEntries.get(4),"아주 좋음");
                            barDataSet5.setColor(Color.GREEN);

                            BarData barData = new BarData(barDataSet1, barDataSet2, barDataSet3, barDataSet4, barDataSet5);
                            mpBarChart.setData(barData);

                            String[] days = new String[] {"1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월"};
                            XAxis xAxis = mpBarChart.getXAxis();
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
                            xAxis.setCenterAxisLabels(true);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setGranularity(1);
                            xAxis.setGranularityEnabled(true);

                            mpBarChart.setDragEnabled(true);
                            mpBarChart.setVisibleXRangeMaximum(6);

                            float barSpace = 0.1f;
                            float groupSpace = 0.05f;
                            barData.setBarWidth(0.09f);

                            mpBarChart.getXAxis().setAxisMinimum(0);
                            mpBarChart.getXAxis().setAxisMaximum(0 + mpBarChart.getBarData().getGroupWidth(groupSpace, barSpace) * 12);

                            mpBarChart.groupBars(0,groupSpace,barSpace);

                            mpBarChart.invalidate();

                        }
                    }
                }
        );
        return v;
    }

    private ArrayList<ArrayList<BarEntry>> some(int[][] in) {
        ArrayList<ArrayList<BarEntry>> result = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            result.add(new ArrayList<>());
        }

        for (int i = 0; i < 12; i++){
            for (int j = 0; j < 5; j++) {
                result.get(j).add(new BarEntry(i, in[i][j]));
            }
        }

        return result;
    }

    @Override
    public void showResult(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
