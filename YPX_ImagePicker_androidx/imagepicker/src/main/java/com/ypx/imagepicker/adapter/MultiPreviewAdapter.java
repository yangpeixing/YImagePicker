package com.ypx.imagepicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.activity.preview.MultiImagePreviewActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.helper.recyclerviewitemhelper.ItemTouchHelperAdapter;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.widget.ShowTypeImageView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Time: 2019/7/23 10:43
 * Author:ypx
 * Description: 多选预览adapter
 */
public class MultiPreviewAdapter extends RecyclerView.Adapter<MultiPreviewAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private ArrayList<ImageItem> previewList;
    private Context context;
    private IPickerPresenter presenter;
    private ImageItem previewImageItem;

    public void setPreviewImageItem(ImageItem previewImageItem) {
        this.previewImageItem = previewImageItem;
        notifyDataSetChanged();
    }

    public MultiPreviewAdapter(ArrayList<ImageItem> previewList, IPickerPresenter presenter) {
        this.previewList = previewList;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ShowTypeImageView imageView = new ShowTypeImageView(context);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(dp(60), dp(60));
        params.leftMargin = dp(8);
        params.rightMargin = dp(8);
        params.topMargin = dp(15);
        params.bottomMargin = dp(15);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ImageItem imageItem = previewList.get(position);
        boolean isSelect = previewImageItem != null && previewImageItem.equals(imageItem);
        holder.imageView.setSelect(isSelect, ImagePicker.getThemeColor());
        holder.imageView.setTypeFromImage(imageItem);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MultiImagePreviewActivity) {
                    ((MultiImagePreviewActivity) context).onPreviewItemClick(imageItem);
                }
            }
        });
        presenter.displayImage(holder.imageView, imageItem, 0, true);
    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    public int dp(float dp) {
        if (context == null) {
            return 0;
        }
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        try {
            if (null == previewList
                    || fromPosition >= previewList.size()
                    || toPosition >= previewList.size()) {
                return true;
            }
            Collections.swap(previewList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ShowTypeImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ShowTypeImageView) itemView;
        }
    }
}
