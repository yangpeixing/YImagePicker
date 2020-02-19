package com.ypx.imagepicker.views.redbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.views.base.PickerControllerView;

import java.util.ArrayList;

/**
 * Time: 2019/11/11 14:41
 * Author:ypx
 * Description: 微信标题栏
 */
public class RedBookTitleBar extends PickerControllerView {
    private TextView mTvTitle;
    private ImageView mArrowImg;
    private TextView mTvNext;
    private TextView mTvSelectNum;

    public RedBookTitleBar(Context context) {
        super(context);
    }

    public RedBookTitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RedBookTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getViewHeight() {
        return dp(55);
    }

    public void setImageSetArrowIconID(int imageSetArrowIconID) {
        mArrowImg.setImageDrawable(getResources().getDrawable(imageSetArrowIconID));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_redbook_titlebar;
    }

    @Override
    protected void initView(View view) {
        ImageView mBackImg = view.findViewById(R.id.mBackImg);
        mTvTitle = view.findViewById(R.id.mTvSetName);
        mArrowImg = view.findViewById(R.id.mArrowImg);
        mTvNext = view.findViewById(R.id.mTvNext);
        mTvSelectNum = view.findViewById(R.id.mTvSelectNum);
        mTvSelectNum.setVisibility(View.GONE);
        setImageSetArrowIconID(R.mipmap.picker_arrow_down);
        mBackImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        setBackgroundColor(Color.BLACK);
        mBackImg.setColorFilter(Color.WHITE);
        mArrowImg.setColorFilter(Color.WHITE);
        mTvTitle.setTextColor(Color.WHITE);
        mTvNext.setTextColor(Color.WHITE);
        mTvNext.setBackground(PCornerUtils.cornerDrawable(Color.parseColor("#50B0B0B0"), dp(30)));
        mTvNext.setText(getContext().getString(R.string.picker_str_title_right));
    }

    @Override
    public boolean isAddInParent() {
        return true;
    }

    @Override
    public View getCanClickToCompleteView() {
        return mTvNext;
    }

    @Override
    public View getCanClickToIntentPreviewView() {
        return null;
    }

    @Override
    public View getCanClickToToggleFolderListView() {
        return mTvTitle;
    }


    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void onTransitImageSet(boolean isOpen) {
        if (isOpen) {
            mArrowImg.setRotation(180);
        } else {
            mArrowImg.setRotation(0);
        }
    }

    @Override
    public void onImageSetSelected(ImageSet imageSet) {
        mTvTitle.setText(imageSet.name);
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void refreshCompleteViewState(ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
        if (selectedList != null && selectedList.size() == 0) {
            mTvNext.setEnabled(false);
            mTvNext.setBackground(PCornerUtils.cornerDrawable(Color.parseColor("#50B0B0B0"), dp(30)));
            mTvSelectNum.setVisibility(View.GONE);
        } else {
            mTvNext.setEnabled(true);
            mTvNext.setBackground(PCornerUtils.cornerDrawable(getThemeColor(), dp(30)));
        }
    }
}
