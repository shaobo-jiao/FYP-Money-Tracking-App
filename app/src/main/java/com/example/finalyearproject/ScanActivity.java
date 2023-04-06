package com.example.finalyearproject;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.finalyearproject.beans.Category;
import com.example.finalyearproject.beans.Receipt;
import com.example.finalyearproject.beans.Record;
import com.example.finalyearproject.dao.ReceiptDao;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.utils.TextReceipt;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.websitebeaver.documentscanner.DocumentScanner;
import com.websitebeaver.documentscanner.constants.ResponseType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class ScanActivity extends AppCompatActivity {
    private static final String TAG = "ScanActivity";
    private final Receipt receipt = new Receipt(); // receipt to be created from scanned image and inserted into DB
    private DocumentScanner documentScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check if image from camera or gallery;
        boolean fromGallery = getIntent().getBooleanExtra("fromGallery", false);
        if (fromGallery) {
            processGalleryImage();
        } else {
            initDocumentScanner();
            documentScanner.startScan();
        }
    }

    /* pick an image from gallery, process text, extract receipt info and, store receipt and record, return */
    private void processGalleryImage() {
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // get uri of selected image, load into bitmap; save in app's folder, start textRecognizer;
                        Uri uri = result.getData().getData();
                        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            saveCroppedImageAndThumbnail(bitmap);
                            initTextRecognizer(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    /* save receipt img and thumbnail for selected image, update receipt */
    private String saveCroppedImageAndThumbnail(Bitmap bitmap) throws IOException {
        String dir = Receipt.getReceiptImgFolder(this); // dir to store cropped receipt images
        String filename = System.currentTimeMillis() + ".jpeg"; // use millis as unique name for new receipt
        Path dst = Paths.get(dir, filename);
        Path dstThumbnail = Paths.get(dir, "thumbnail_" + filename);
        Log.d(TAG, "receipt img dst: " + dst.toAbsolutePath().toString());
        Log.d(TAG, "receipt thumbnail dst: " + dstThumbnail.toAbsolutePath().toString());
        // save ori-quality img and low-quality thumbnail
        try (FileOutputStream fosH = new FileOutputStream(dst.toFile());
             FileOutputStream fosL = new FileOutputStream(dstThumbnail.toFile())) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosH);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, fosL);
        }
        Log.d(TAG, "Receipt Img and Thumbnail saved");
        // update receipt;
        receipt.setImagePath(dst.toAbsolutePath().toString());
        receipt.setThumbnailPath(dstThumbnail.toAbsolutePath().toString());
        return dst.toAbsolutePath().toString();
    }

    /* init and start text recognition on given bitmap */
    private void initTextRecognizer(Bitmap bitmap) {
        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Log.d(TAG, "initTextRecognizer - bitmap: " + bitmap.getHeight() + ", " + bitmap.getWidth());
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    extractReceiptInfo(visionText);
                    storeReceiptAndRecordAndFinish();
                })
                .addOnFailureListener(e -> Log.d(TAG, "TextRecognition Fail"));
    }

    /* use visionText to extract receipt info */
    private void extractReceiptInfo(Text visionText) {
        TextReceipt textReceipt = new TextReceipt(visionText);
        Log.d(TAG, "receipt getBlockText: \n" + textReceipt.getBlockText());
        Log.d(TAG, "receipt getText: \n" + textReceipt.getText());
        String name = textReceipt.extractTitle();
        Log.d(TAG, "extractTitle Finish: " + name);
        String date = textReceipt.extractDate(); // in form of dd/MM/yyyy
        Log.d(TAG, "extractDate Finish: " + date);
        double amount = textReceipt.extractTotalAmount();
        Log.d(TAG, "extractTotalAmount Finish: " + amount);
        String[] splits = date.split("/");
        int dayOfMonth = Integer.parseInt(splits[0]);
        int month = Integer.parseInt(splits[1]) - 1; // 0-11
        int year = Integer.parseInt(splits[2]);
        receipt.setName(name);
        receipt.setAmount(amount);
        receipt.setDesc("");
        receipt.setYear(year);
        receipt.setMonth(month);
        receipt.setDayOfMonth(dayOfMonth);
        Log.d(TAG, "receipt to insert: " + receipt);
    }

    /* store receipt and corresponding record into DB; then finish and return receiptId and recordId */
    private void storeReceiptAndRecordAndFinish() {
        // insert updated receipt into DB and return newly inserted receiptId;
        ReceiptDao receiptDao = MainApplication.getInstance().getMainDatabase().receiptDao();
        int receiptId = (int) receiptDao.insertReceipt(receipt);
        Log.d(TAG, "newReceiptId: " + receiptId);
        // create and insert corresponding record into DB, default category as Supermarket;
        Record record = new Record();
        record.setRecordType(Record.RECORD_EXPENSE);
        record.setCategoryName("Supermarket");
        record.setCategoryImageId(Category.getCategoryNameImgMap().get("Supermarket"));
        record.setAmount(receipt.getAmount());
        record.setYear(receipt.getYear());
        record.setMonth(receipt.getMonth());
        record.setDayOfMonth(receipt.getDayOfMonth());
        record.setDesc(receipt.getName());
        Log.d(TAG, "record to insert: " + record);
        RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
        int recordId = (int) recordDao.insertRecord(record);
        Log.d(TAG, "newRecordId: " + recordId);
        // return newly inserted receipt's id
        Intent intent = new Intent();
        intent.putExtra("newReceiptId", receiptId);
        intent.putExtra("newRecordId", recordId);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initDocumentScanner() {
        documentScanner = new DocumentScanner(
                this,
                (croppedImageResults) -> {
                    try {
                        // load cropped image to bitmap, save it and thumbnail in App's folder, initTextRecognizer;
                        Uri uri = Uri.parse(croppedImageResults.get(0));
                        Log.d(TAG, "croppedImageResult's uriPath: " + uri.getPath());
                        try (FileInputStream fis = new FileInputStream(uri.getPath())) {
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            saveCroppedImageAndThumbnail(bitmap);
                            initTextRecognizer(bitmap);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                },
                (errorMessage) -> {
                    // an error happened
                    Log.v("documentscannerlogs", errorMessage);
                    finish();
                    return null;
                },
                () -> {
                    // user canceled document scan
                    Log.v("documentscannerlogs", "User canceled document scan");
                    finish();
                    return null;
                },
                ResponseType.IMAGE_FILE_PATH,
                true,
                1,
                null
        );
    }


}