package com.ypx.imagepicker.views.wx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.views.base.PickerFolderItemView;

/**
 * Time: 2019/11/13 15:16
 * Author:ypx
 * Description:自定义文件夹Item样式
 */
public class WXFolderItemView extends PickerFolderItemView {
    private ImageView mCover;
    private TextView mName;
    private TextView mSize;
    private ImageView mIndicator;

    public WXFolderItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_list_item_folder;
    }

    @Override
    protected void initView(View view) {
        mCover = view.findViewById(R.id.cover);
        mName = view.findViewById(R.id.name);
        mSize = view.findViewById(R.id.size);
        mIndicator = view.findViewById(R.id.indicator);
    }

    @Override
    public int getItemHeight() {
        return -1;
    }

    @Override
    public void displayCoverImage(String coverUrl, IPickerPresenter presenter) {
        ImageItem imageItem = new ImageItem();
        imageItem.path = coverUrl;
        presenter.displayImage(mCover, imageItem, mCover.getMeasuredWidth(), true);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void loadItem(ImageSet imageSet) {
        mName.setText(imageSet.name);
        mSize.setText(String.format("%d%s", imageSet.count, getResources().getString(R.string.picker_str_piece)));
        if (imageSet.isSelected) {
            mIndicator.setVisibility(View.VISIBLE);
        } else {
            mIndicator.setVisibility(View.GONE);
        }
    }


    public void setIndicatorDrawable(Drawable drawable) {
        mIndicator.setImageDrawable(drawable);
    }

    public void setIndicatorColor(int color) {
        mIndicator.setColorFilter(color);
    }
}
