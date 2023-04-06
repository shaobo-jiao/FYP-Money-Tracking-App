package com.example.finalyearproject.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.MainApplication;
import com.example.finalyearproject.R;
import com.example.finalyearproject.ReceiptDetailsActivity;
import com.example.finalyearproject.beans.Receipt;
import com.example.finalyearproject.dao.ReceiptDao;
import com.example.finalyearproject.dao.RecordDao;
import com.example.finalyearproject.utils.ImageUtils;
import com.example.finalyearproject.utils.SizeUtils;

import java.util.List;
import java.util.Locale;

public class MonthlyReceiptsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MonthlyReceiptsAdapter";
    private Context context;
    private List<Receipt> receiptList;

    public MonthlyReceiptsAdapter(Context context, List<Receipt> receiptList) {
        this.context = context;
        this.receiptList = receiptList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.receipt, parent, false);
        return new ReceiptHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ReceiptHolder holder = (ReceiptHolder) viewHolder;
        Receipt receipt = receiptList.get(position);
        long t1, t2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        // load only dimen first to calculate inSampleSize
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(receipt.getThumbnailPath(), options);
        options.inSampleSize = SizeUtils.calculateInSampleSize(options, 100, 100);
        Log.d(TAG, "src img size: " + options.outWidth + ", " + options.outHeight);
        Log.d(TAG, "inSampleSize: " + options.inSampleSize);
        // load thumbnail with inSampleSize to speed up loading
        t1 = System.currentTimeMillis();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(receipt.getThumbnailPath(), options);
        t2 = System.currentTimeMillis();
        Log.d(TAG, "time decode thumbnail with inSampleSize: " + (t2 - t1));
        Log.d(TAG, "decoded thumbnail size: " + bitmap.getWidth() + ", " + bitmap.getHeight());
        // set views' contents
        holder.ivImage.setImageBitmap(bitmap);
        holder.tvName.setText(receipt.getName());
        holder.tvDesc.setText(receipt.getDesc());
        holder.tvAmount.setText(String.format(Locale.US, "%.2f", receipt.getAmount()));
        holder.tvDate.setText(String.format(Locale.US, "%04d-%02d-%02d",
                receipt.getYear(), receipt.getMonth() + 1, receipt.getDayOfMonth()));
        //on receipt select: start ReceiptDetailActivity and pass selected receiptId
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReceiptDetailsActivity.class);
            intent.putExtra("receiptId", receipt.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return receiptList.size();
    }

    public static class ReceiptHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;
        private final TextView tvDesc;
        private final TextView tvAmount;
        private final TextView tvDate;

        public ReceiptHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);

        }
    }

}
