package xyz.bcfriends.dra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import xyz.bcfriends.dra.util.DBHelper;

public class ExportExcelFragment extends Fragment implements DBHelper.Executor {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_export_excel, container, false);
        Workbook wb = new XSSFWorkbook();

        String safeName = WorkbookUtil.createSafeSheetName("통계");
        CreationHelper createHelper = wb.getCreationHelper();
        FirestoreHelper firestoreHelper = new FirestoreHelper(this);

        Sheet statSheet = wb.createSheet(safeName);

        CheckBox exportMemo = v.findViewById(R.id.export_memo);
        boolean noteFlag = exportMemo.isChecked();

        firestoreHelper.readDataAll(
                firestoreHelper.getDatabase().collection("users")
                        .document(Objects.requireNonNull(firestoreHelper.getUser().getUid()))
                        .collection("Records"),
                new DBHelper.QueryCallback() {
                    @Override
                    public void onCallback(@Nullable QuerySnapshot data) {
                        if (data != null) {
                            for (QueryDocumentSnapshot document : data) {
                                Row row;
                                if (statSheet.getLastRowNum() == -1) {
                                    row = statSheet.createRow(0);

                                    Cell cell = row.createCell(1);
                                    cell.setCellValue("일자");
                                    cell = row.createCell(2);
                                    cell.setCellValue("정도");
                                    if (noteFlag) {
                                        cell = row.createCell(3);
                                        cell.setCellValue("메모");
                                    }
                                } else {
                                    row = statSheet.createRow(statSheet.getLastRowNum() + 1);
                                }

                                // Date
                                Cell cell = row.createCell(1);
                                cell.setCellValue(LocalDate.parse(document.getId()));
                                CellStyle cellStyle = wb.createCellStyle();
                                cellStyle.setDataFormat(
                                        createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
                                cell.setCellStyle(cellStyle);

                                // DepressStatus
                                cell = row.createCell(2);
                                cell.setCellValue(Objects.requireNonNull(document.getData().get("depressStatus")).toString());

                                // Note
                                if (noteFlag) {
                                    cell = row.createCell(3);
                                    cell.setCellValue(Objects.requireNonNull(document.getData().get("note")).toString());
                                }
                            }
                        }
                    }
                });

        File xls = new File(requireActivity().getApplicationContext().getFilesDir() + "/" + safeName + ".xlsx");

        try {
//            xls = File.createTempFile("통계", ".xlsx", requireActivity().getExternalCacheDir());
            OutputStream fileOut = new FileOutputStream(xls);
            wb.write(fileOut);

            Uri uri = Uri.parse("mailto:darponge@gmail.com");
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra(Intent.EXTRA_SUBJECT, "excel file email test");
            it.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + xls));
            startActivityForResult(it, 200);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(requireContext().getApplicationContext(), xls.getAbsolutePath() + "에 저장되었습니다.", Toast.LENGTH_SHORT).show();

        return v;
    }

    @Override
    public void showResult(String message) {

    }
}
