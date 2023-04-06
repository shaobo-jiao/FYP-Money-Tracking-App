package com.example.finalyearproject.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalyearproject.ExportActivity;
import com.example.finalyearproject.ImportActivity;
import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.Receipt;
import com.example.finalyearproject.dao.ReceiptDao;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.databinding.FragmentMoreBinding;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class MoreFragment extends Fragment {
    private com.example.finalyearproject.databinding.FragmentMoreBinding binding;

    public MoreFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);

        initBtns();

        return binding.getRoot();
    }

    /* register onClickListener for the buttons */
    private void initBtns() {
        binding.tvClearBtn.setOnClickListener(v -> {
            showClearConfirmDialog();
        });
        binding.tvExportBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ExportActivity.class);
            startActivity(intent);
        });
        binding.tvImportBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ImportActivity.class);
            startActivity(intent);
        });
        binding.tvAboutBtn.setOnClickListener(v -> {
            showAboutDialog();
        });
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Smart Money Tracker v1.0")
                .setMessage("This app was developed by Shaobo during his Final Year Project")
                .setNegativeButton("DISMISS", null);
        builder.create().show();
    }

    private void showClearConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure you want to delete all data in this app?")
                .setMessage("App data includes all records and receipts. Once confirmed, data will be permanently deleted and cannot be recovered")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("CONFIRM", (dialog, which) -> {
                    RecordDao recordDao = MainApplication.getInstance().getMainDatabase().recordDao();
                    recordDao.deleteAllRecords();
                    ReceiptDao receiptDao = MainApplication.getInstance().getMainDatabase().receiptDao();
                    receiptDao.deleteAllReceipt();
                    // delete all receipts images
                    File receiptImgDir = new File(Receipt.getReceiptImgFolder(getContext()));
                    for (File file : receiptImgDir.listFiles()) {
                        file.delete();
                    }
                    // delete temp cropped img from DocumentScan
                    File cropImgDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    for (File file : cropImgDir.listFiles()) {
                        file.delete();
                    }
                });
        builder.create().show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}