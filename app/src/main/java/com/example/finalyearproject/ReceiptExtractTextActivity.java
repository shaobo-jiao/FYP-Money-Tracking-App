package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.example.finalyearproject.databinding.ActivityReceiptDetailsBinding;
import com.example.finalyearproject.databinding.ActivityReceiptExtractTextBinding;
import com.example.finalyearproject.utils.TextReceipt;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ReceiptExtractTextActivity extends AppCompatActivity {
    private static final String TAG = "ReceiptExtractTextActivity";
    private com.example.finalyearproject.databinding.ActivityReceiptExtractTextBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptExtractTextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initOnClickListener();
        String imagePath = getIntent().getStringExtra("imagePath");
        initTextRecognizer(imagePath);
    }

    private void initOnClickListener() {
        binding.ivBack.setOnClickListener(v -> finish());
    }

    private void initTextRecognizer(String imagePath) {
        TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    TextReceipt textReceipt = new TextReceipt(visionText);
                    binding.tvExtractedText.setText(textReceipt.getText());
                })
                .addOnFailureListener(e -> Log.d(TAG, "TextRecognition Fail"));
    }


}