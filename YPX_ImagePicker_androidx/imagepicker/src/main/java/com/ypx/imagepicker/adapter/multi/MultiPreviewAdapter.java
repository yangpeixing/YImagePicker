package com.ypx.imagepicker.adapter.multi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.activity.multi.MultiImagePreviewActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.widget.ShowTypeImageView;

import java.util.ArrayList;

/**
 * Time: 2019/7/23 10:43
 * Author:ypx
 * Description: 多选预览adapter
 */
public class MultiPreviewAdapter extends RecyclerView.Adapter<MultiPreviewAdapter.ViewHolder> {
    private ArrayList<ImageItem> previewList;
    private Context context;
    private IMultiPickerBindPresenter presenter;

    public MultiPreviewAdapter(ArrayList<ImageItem> previewList, IMultiPickerBindPresenter presenter) {
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
        holder.imageView.setTypeFromImage(imageItem);
        holder.imageView.setSelect(imageItem.isSelect(), presenter.getUiConfig(context).getThemeColor());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MultiImagePreviewActivity) {
                    ((MultiImagePreviewActivity) context).onPreviewItemClick(imageItem);
                }
            }
        });
        presenter.displayListImage(holder.imageView, imageItem, 0);
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ShowTypeImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ShowTypeImageView) itemView;
        }
    }
}
