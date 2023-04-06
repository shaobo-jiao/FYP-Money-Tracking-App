package com.example.finalyearproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.ActivityExportBinding;
import com.example.finalyearproject.dialogs.MyDatePickerDialog;
import com.example.finalyearproject.utils.DateUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ExportActivity extends AppCompatActivity {
    private static final String TAG = "ExportActivity";
    private com.example.finalyearproject.databinding.ActivityExportBinding binding;
    int startYear, startMonth, startDayOfMonth;
    int endYear, endMonth, endDayOfMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDates();
        initBtns();
    }

    /* init start and end dates as first and last day of current month */
    private void initDates() {
        Calendar calendar = Calendar.getInstance();
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH);
        startDayOfMonth = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        endYear = calendar.get(Calendar.YEAR);
        endMonth = calendar.get(Calendar.MONTH);
        endDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String startDate = DateUtils.getDateStrYYYYMMDD(startYear, startMonth, startDayOfMonth);
        String endDate = DateUtils.getDateStrYYYYMMDD(endYear, endMonth, endDayOfMonth);
        binding.tvStartDate.setText(startDate);
        binding.tvEndDate.setText(endDate);
    }

    /* register onClickListeners for buttons */
    private void initBtns() {
        binding.ivBack.setOnClickListener(v -> finish());
        binding.tvStartDate.setOnClickListener(v -> {
            // launch date picker dialog
            MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, startYear, startMonth, startDayOfMonth);
            datePickerDialog.setOnDateSetListener((year, month, dayOfMonth) -> {
                startYear = year;
                startMonth = month;
                startDayOfMonth = dayOfMonth;
                binding.tvStartDate.setText(DateUtils.getDateStrYYYYMMDD(year, month, dayOfMonth));
            });
            datePickerDialog.show();
        });
        binding.tvEndDate.setOnClickListener(v -> {
            // launch date picker dialog
            MyDatePickerDialog datePickerDialog = new MyDatePickerDialog(this, endYear, endMonth, endDayOfMonth);
            datePickerDialog.setOnDateSetListener((year, month, dayOfMonth) -> {
                endYear = year;
                endMonth = month;
                endDayOfMonth = dayOfMonth;
                binding.tvEndDate.setText(DateUtils.getDateStrYYYYMMDD(year, month, dayOfMonth));
            });
            datePickerDialog.show();
        });
        binding.btnExport.setOnClickListener(v -> {
            // 1. check startDate <= endDate
            if (startYear > endYear || startMonth > endMonth || startDayOfMonth > endDayOfMonth) {
                Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show();
                return;
            }
            // 2. load List<Record> within chosen range;
            List<Record> records = loadRecordsWithinRange();
            if (records.isEmpty()) {
                showDialogOnEmptyRecord(records);
                return;
            }
            try {
                // 3. create Excel, save in cache dir
                File excel = createAndSaveExcel(records);
                // 4. share the file;
                shareExcel(excel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /* use FileProvider to create uri for excel with granted permission for other apps. Lunch Intent.ACTION_SEND */
    private void shareExcel(File excel) {
        Uri uri = FileProvider.getUriForFile(this, "com.example.finalyearproject.provider", excel);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share"));
    }

    /* confirm with user when no record in chosen date range */
    private void showDialogOnEmptyRecord(List<Record> records) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Record")
                .setMessage("There is no record within the range. Do you still want to export? ")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        // 3. create Excel, save in cache dir
                        File excel = createAndSaveExcel(records);
                        // 4. share the file;
                        shareExcel(excel);
                    } catch (IOException e) {
                        Log.d(TAG, "Error when creating Excel");
                        throw new RuntimeException(e);
                    }
                });
        builder.create().show();
    }

    private List<Record> loadRecordsWithinRange() {
        RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
        return recordDao.getRecordsByDateRange(startYear, startMonth, startDayOfMonth, endYear, endMonth, endDayOfMonth);
    }

    /* create excel using loaded records, temporarily save at App's cacheDir */
    private File createAndSaveExcel(List<Record> records) throws IOException {
        File excel = createExcelFile();
        try (FileOutputStream fos = new FileOutputStream(excel);
             HSSFWorkbook workbook = new HSSFWorkbook()) {
            writeExcelContents(workbook, records);
            workbook.write(fos);
        }
        return excel;
    }

    /* prepare empty excel file in cache dir, return path to the excel file */
    private File createExcelFile() throws IOException {
        // generate filename as the date range;
        String fileName = String.format(Locale.US, "%s to %s",
                DateUtils.getDateStrYYYYMMDD(startYear, startMonth, startDayOfMonth),
                DateUtils.getDateStrYYYYMMDD(endYear, endMonth, endDayOfMonth));
        Path excel = Paths.get(getCacheDir().getPath(), fileName + ".xls");
        Log.d(TAG, "TempExcel: " + excel.toAbsolutePath());
        // create the file in cache dir
        if (!Files.exists(excel)) {
            Files.createFile(excel);
        }
        return excel.toFile();
    }

    /* fill contents of a HSSWorkbook using records */
    private void writeExcelContents(HSSFWorkbook workbook, List<Record> records) {
        HSSFSheet sheet = workbook.createSheet();
        // first row as header row;
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("Date (Please Format as Date)");
        row.createCell(1).setCellValue("Record Type (0 for Expense, 1 for Income)");
        row.createCell(2).setCellValue("Category");
        row.createCell(3).setCellValue("Description");
        row.createCell(4).setCellValue("Amount");
        // use records to generate cells
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            row = sheet.createRow(i + 1);
            // set record info in each row;
            Calendar calendar = Calendar.getInstance();
            calendar.set(record.getYear(), record.getMonth(), record.getDayOfMonth());
            row.createCell(0).setCellValue(calendar.getTime());
            row.createCell(1).setCellValue(record.getRecordType());
            row.createCell(2).setCellValue(record.getCategoryName());
            row.createCell(3).setCellValue(record.getDesc());
            row.createCell(4).setCellValue(record.getAmount());
        }
    }
}