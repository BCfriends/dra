package xyz.bcfriends.dra;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

        Button exportButton = v.findViewById(R.id.export_button);
        exportButton.setOnClickListener(v1 -> {
            Workbook wb = new XSSFWorkbook();

            String safeStatName = WorkbookUtil.createSafeSheetName("통계");
            String safeGraphName = WorkbookUtil.createSafeSheetName("그래프");
            CreationHelper createHelper = wb.getCreationHelper();

            Sheet statSheet = wb.createSheet(safeStatName);
            Sheet graphSheet = wb.createSheet(safeGraphName);

            CheckBox exportMemo = v.findViewById(R.id.export_memo);
            boolean noteFlag = exportMemo.isChecked();

            Row row = statSheet.createRow(0);

            Cell cell = row.createCell(1);
            cell.setCellValue("일자");

            cell = row.createCell(2);
            cell.setCellValue("정도");

            if (noteFlag) {
                cell = row.createCell(3);
                cell.setCellValue("메모");
            }


            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "true");
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            0);
                }
            }

            try {
                FirestoreHelper firestoreHelper = new FirestoreHelper(this);

                firestoreHelper.readDataAll(
                        firestoreHelper.getDatabase().collection("users")
                                .document(Objects.requireNonNull(firestoreHelper.getUser().getUid()))
                                .collection("Records"),
                        new DBHelper.QueryCallback() {
                            @Override
                            public void onCallback(@Nullable QuerySnapshot data) {
                                boolean isNoteExist = true;
                                if (data != null) {
                                    for (QueryDocumentSnapshot document : data) {
                                        Row row = statSheet.createRow(statSheet.getLastRowNum() + 1);

                                        Cell cell = row.createCell(0);
                                        cell.setCellValue(statSheet.getLastRowNum());

                                        // Date
                                        cell = row.createCell(1);
                                        cell.setCellValue(LocalDate.parse(document.getId()));
                                        CellStyle cellStyle = wb.createCellStyle();
                                        cellStyle.setDataFormat(
                                                createHelper.createDataFormat().getFormat("yyyy-mm-dd"));
                                        cell.setCellStyle(cellStyle);

                                        // DepressStatus
                                        cell = row.createCell(2);
                                        cell.setCellValue(Integer.parseInt(Objects.requireNonNull(document.getData().get("depressStatus")).toString()));

                                        // Note
                                        if (noteFlag) {
                                            if (document.getData().get("note") != null) {
                                                cell = row.createCell(3);
                                                cell.setCellValue(document.getData().get("note").toString());
                                            }
                                        }
                                    }

                                } else {
                                    isNoteExist = false;
                                }

                                for (int i = 0; i < statSheet.getLastRowNum(); i++) {
                                    statSheet.setColumnWidth(1, (statSheet.getColumnWidth(i)) + 1024);
                                }

                                statSheet.setAutoFilter(CellRangeAddress.valueOf("B:C"));

                                File file = new File(Environment.getExternalStorageDirectory(), safeStatName + ".xlsx");

                                if (isNoteExist) {
                                    try {
                                        OutputStream fileO = new FileOutputStream(file);
                                        wb.write(fileO);

                                        EditText exportEmail = v.findViewById(R.id.export_email);
                                        String emailRegEx = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
                                        boolean emailValid = exportEmail.getText().toString().toLowerCase().matches(emailRegEx);

                                        if (emailValid) {
                                            Uri uri = Uri.parse("mailto:" + exportEmail.getText());
                                            Uri path = Uri.fromFile(file);
                                            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                                            it.putExtra(Intent.EXTRA_SUBJECT, "excel file email test");
                                            it.putExtra(Intent.EXTRA_STREAM, path);
                                            startActivityForResult(it, 200);
                                        } else {
                                            Toast.makeText(requireContext().getApplicationContext(), "이메일을 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Toast.makeText(requireContext().getApplicationContext(), file.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (UnsupportedOperationException ignored) {

            }

/*
            try {
//            xls = File.createTempFile("통계", ".xlsx", requireActivity().getExternalCacheDir());
                OutputStream fileOut = new FileOutputStream(xls);
                // Write excel to file
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
*/
        });
        return v;
    }

    @Override
    public void showResult(String message) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
