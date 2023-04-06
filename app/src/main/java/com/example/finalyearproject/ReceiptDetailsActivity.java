package com.example.finalyearproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.finalyearproject.beans.Receipt;
import com.example.finalyearproject.dao.ReceiptDao;
import com.example.finalyearproject.databinding.ActivityReceiptDetailsBinding;
import com.example.finalyearproject.dialogs.MyDatePickerDialog;
import com.example.finalyearproject.utils.DateUtils;

import java.util.Locale;

public class ReceiptDetailsActivity extends AppCompatActivity {

    private com.example.finalyearproject.databinding.ActivityReceiptDetailsBinding binding;
    private Receipt receipt;
    private ReceiptDao receiptDao;
    private boolean isDeleted = false; // check how user leaves this activity: delete? return?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiptDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initReceiptData();
        initOnClickListeners();
        initEtDesc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // update receipt when user leaves this activity by return; if delete then no update
        if (!isDeleted) {
            receipt.setName(binding.etName.getText().toString());
            receipt.setDesc(binding.etDesc.getText().toString());
            receipt.setAmount(Double.parseDouble(binding.etAmount.getText().toString()));
            receiptDao.updateReceipt(receipt);
        }
    }

    /* load receipt using received id, setup receipt data */
    private void initReceiptData() {
        // load receipt using received id;
        int newReceiptId = getIntent().getIntExtra("receiptId", -1);
        receiptDao = MainApplication.getInstance().getMainDatabase().receiptDao();
        receipt = receiptDao.getReceiptById(newReceiptId);
        // setup displayed data;
        binding.etName.setText(receipt.getName());
        binding.etAmount.setText(String.format(Locale.US, "%.2f", receipt.getAmount()));
        binding.tvDate.setText(String.format(Locale.US, "%04d-%02d-%02d",
                receipt.getYear(), receipt.getMonth() + 1, receipt.getDayOfMonth()));
        binding.etDesc.setText(receipt.getDesc());
        Bitmap bitmap = BitmapFactory.decodeFile(receipt.getImagePath());
        binding.ivImage.setImageBitmap(bitmap);
    }

    /* onClickListener for ivBack, ivDelete, tvDate, btnExtract */
    private void initOnClickListeners() {
        binding.ivBack.setOnClickListener(v -> finish());
        binding.ivDelete.setOnClickListener(v -> {
            // show alert dialog to confirm deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure to delete this receipt?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("CONFIRM", (dialog, which) -> {
                        receiptDao.deleteReceipt(receipt);
                        isDeleted = true;
                        finish();
                    });
            builder.create().show();
        });
        binding.tvDate.setOnClickListener(v -> {
            // show date picker dialog
            MyDatePickerDialog dialog = new MyDatePickerDialog(this,
                    receipt.getYear(), receipt.getMonth(), receipt.getDayOfMonth());
            dialog.setOnDateSetListener((year, month, dayOfMonth) -> {
                // update shown date, update receipt's data;
                binding.tvDate.setText(DateUtils.getDateStrYYYYMMDD(year, month + 1, dayOfMonth));
                receipt.setYear(year);
                receipt.setMonth(month);
                receipt.setDayOfMonth(dayOfMonth);
            });
            dialog.show();
        });
        binding.btnExtract.setOnClickListener(v -> {
            // pass image to ReceiptExtractTextActivity
            Intent intent = new Intent(this, ReceiptExtractTextActivity.class);
            intent.putExtra("imagePath", receipt.getImagePath());
            startActivity(intent);
        });
    }

    /* set etDesc to clear focus (remove blinking cursor) and hide soft keyboard after pressed "Done" button */
    private void initEtDesc() {
        EditText etDesc = binding.etDesc;
        etDesc.setRawInputType(InputType.TYPE_CLASS_TEXT);
        etDesc.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etDesc.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etDesc.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                etDesc.clearFocus();
                return true;
            }
            return false;
        });
    }

}