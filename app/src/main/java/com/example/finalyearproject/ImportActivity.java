package com.example.finalyearproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.finalyearproject.beans.Category;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.ActivityImportBinding;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.StringUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImportActivity extends AppCompatActivity {
    private static final String TAG = "ImportActivity";
    private com.example.finalyearproject.databinding.ActivityImportBinding binding;
    Map<String, Integer> categoryNameImgMap = Category.getCategoryNameImgMap();
    ActivityResultLauncher<Intent> importExcelLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLauncher();
        initBtns();
    }

    /* init launcher of selecting and processing excel */
    private void initLauncher() {
        importExcelLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // open selected excel and read into HSSFWorkbook
                        Uri uri = result.getData().getData();
                        try (InputStream inputStream = getContentResolver().openInputStream(uri);
                             HSSFWorkbook workbook = new HSSFWorkbook(inputStream) ) {
                            String errorMsg = validateExcel(workbook);
                            // invalid excel; display error message;
                            if (!errorMsg.equals("")) {
                                binding.tvMessage.setText(errorMsg);
                            }
                            // valid worksheet, create List<Record> and insert into DB
                            else {
                                createAndSaveRecordsFromExcel(workbook);
                                binding.tvMessage.setText(R.string.excel_import_success);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    private void initBtns() {
        binding.ivBack.setOnClickListener(v -> finish());
        binding.tvDownloadTemplate.setOnClickListener(v -> {
            try {
                File templateFile = prepareTemplateFile();
                shareExcel(templateFile);
            } catch (IOException e) {
                Log.d(TAG, "Error preparing TemplateFile");
                throw new RuntimeException(e);
            }
        });
        binding.tvImport.setOnClickListener(v -> {
            // pick excel and process;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.ms-excel");
            importExcelLauncher.launch(intent);
        });
    }

    /* create RecordList from valid excel and save into DB */
    private void createAndSaveRecordsFromExcel(HSSFWorkbook workbook) {
        List<Record> recordList = new ArrayList<>();
        HSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) { // skip header row;
            Row row = sheet.getRow(rowNum);
            if (row == null) continue; // skip empty row;
            // get info from the 5 columns: date, record type, category, description, amount;
            Date date = row.getCell(0).getDateCellValue();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int recordType = (int) row.getCell(1).getNumericCellValue();
            String categoryName = row.getCell(2).getStringCellValue();
            categoryName = categoryName.substring(0,1).toUpperCase() + categoryName.substring(1).toLowerCase(); // capitalize
            String desc = new DataFormatter().formatCellValue(row.getCell(3)); // any desc as str
            double amount = row.getCell(4).getNumericCellValue();
            // create corresponding record;
            Record record = new Record();
            record.setYear(calendar.get(Calendar.YEAR));
            record.setMonth(calendar.get(Calendar.MONTH));
            record.setDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            record.setRecordType(recordType);
            record.setCategoryName(categoryName);
            record.setCategoryImageId(categoryNameImgMap.get(categoryName));
            record.setDesc(desc);
            record.setAmount(amount);
            Log.d(TAG, "record: " + record);
            recordList.add(record);
        }
        // save recordList in DB;
        RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
        recordDao.insertRecordList(recordList);
    }

    /* return error message of excel, return empty str if no error; */
    private String validateExcel(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        Log.d(TAG, "lastRowNum: " + lastRowNum);
        for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) { // skip header row;
            Row row = sheet.getRow(rowNum);
            if (row == null) continue; // skip empty row;
            // check date
            try {
                row.getCell(0).getDateCellValue();
            } catch (Exception e) {
                return "Error in row " + rowNum + ", invalid date";
            }
            // check record type
            try {
                int recordType = (int) row.getCell(1).getNumericCellValue();
                if (recordType != 0 && recordType != 1)
                    return "Error in row " + rowNum + ", invalid record type, should be either 0 or 1";
            } catch (Exception e) {
                return "Error in row " + rowNum + ", invalid record type, should be either 0 or 1";
            }
            // check category name
            try {
                String categoryName = row.getCell(2).getStringCellValue();
                categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1).toLowerCase(); // capitalize
                if (!categoryNameImgMap.containsKey(categoryName))
                    return "Error in row " + rowNum + ", invalid category name, should exist in app's provided categories";
            } catch (Exception e) {
                return "Error in row " + rowNum + ", invalid category name";
            }
            // desc as string;
            DataFormatter formatter = new DataFormatter(); // get empty string for empty desc
            String desc = formatter.formatCellValue(row.getCell(3));
            Log.d(TAG, "desc:" + desc);
            // check amount
            try {
                double amount = row.getCell(4).getNumericCellValue();
                if (amount <= 0)
                    return "Error in row " + rowNum + ", invalid amount, should be greater than 0";
            } catch (Exception e) {
                return "Error in row " + rowNum + ", invalid amount";
            }
        }
        if (lastRowNum == 0)
            return "Empty Record List";
        return "";
    }

    /* return File of stored templateFile, create templateFile for 1st time */
    private File prepareTemplateFile() throws IOException {
        Path templatePath = Paths.get(getExternalFilesDir("").getPath(), "template.xls");
        Log.d(TAG, "templatePath: " + templatePath);
        if (Files.exists(templatePath)) {
            return templatePath.toFile();
        }
        // create the template file and write header line if not exists;
        Files.createFile(templatePath);
        try (FileOutputStream fos = new FileOutputStream(templatePath.toFile());
             HSSFWorkbook workbook = new HSSFWorkbook()) {
            HSSFSheet sheet = workbook.createSheet();
            // first row as header row;
            HSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("Date");
            row.createCell(1).setCellValue("Record Type (0 for Expense, 1 for Income)");
            row.createCell(2).setCellValue("Category (match app's category)");
            row.createCell(3).setCellValue("Description");
            row.createCell(4).setCellValue("Amount");
            workbook.write(fos);
        }
        return templatePath.toFile();
    }

    /* Share the excel file, use FileProvider to create uri for excel with granted permission for other apps */
    private void shareExcel(File excel) {
        Uri uri = FileProvider.getUriForFile(this, "com.example.finalyearproject.provider", excel);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share"));
    }

}