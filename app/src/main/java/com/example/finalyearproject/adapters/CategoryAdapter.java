package com.example.finalyearproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.R;
import com.example.finalyearproject.beans.Category;
import com.example.finalyearproject.beans.Record;

import java.util.List;

/* Adapter to set categories (icon + name) for recycle view in 2 fragments in NewRecordActivity
* */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Category> categoryList;
    private OnItemClickedListener onItemClickedListener;
    private int selectedPosition = -1;
    private final int recordType;

    public CategoryAdapter(Context context, List<Category> categoryList, int recordType) {
        this.context = context;
        this.categoryList = categoryList;
        this.recordType = recordType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Category category = categoryList.get(position);
        CategoryViewHolder holder = (CategoryViewHolder) viewHolder;
        holder.ivCategoryIcon.setImageResource(category.getImageId());
        holder.tvCategoryName.setText(category.getName());
        holder.itemView.setOnClickListener(v -> {
            // item onClick Listener; implemented in parent activity/fragment
            onItemClickedListener.OnItemClicked(v, position);
        });
        // for selected position: set background tint and image tint;
        // for unselected position: set bg_tint to grey and image tint to black (temp)
        if (position != selectedPosition) {
            holder.ivCategoryIcon.setImageTintList(context.getColorStateList(R.color.grey_text));
            holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.grey_icon_bg));
        }
        else {
            holder.ivCategoryIcon.setImageTintList(context.getColorStateList(R.color.white));
            if (recordType == Record.RECORD_EXPENSE)
                holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.red));
            else
                holder.ivCategoryIcon.setBackgroundTintList(context.getColorStateList(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public Category getCategoryAt(int position) {
        return categoryList.get(position);
    }

    public int getPositionOfCategory(String categoryName) {
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            if (category.getName().equals(categoryName))
                return i;
        }
        return -1;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCategoryIcon;
        public TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }

    public interface OnItemClickedListener {
        void OnItemClicked(View view, int position);
    }

}
