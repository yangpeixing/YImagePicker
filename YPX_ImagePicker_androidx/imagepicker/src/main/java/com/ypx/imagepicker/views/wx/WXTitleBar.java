package com.ypx.imagepicker.views.wx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class WXTitleBar extends PickerControllerView {
    private TextView mTvTitle;
    private ImageView mSetArrowImg;
    private ImageView ivBack;
    private TextView mCompleteBtn;
    private String completeText;

    private Drawable selectDrawable;
    private Drawable unSelectDrawable;
    private int selectColor;
    private int unSelectColor;
    private boolean canToggleFolderList;

    public WXTitleBar(Context context) {
        super(context);
    }

    @Override
    public int getViewHeight() {
        return dp(50);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_default_titlebar;
    }

    @Override
    protected void initView(View view) {
        mTvTitle = view.findViewById(R.id.tv_title);
        mSetArrowImg = view.findViewById(R.id.mSetArrowImg);
        ivBack = view.findViewById(R.id.iv_back);
        mCompleteBtn = view.findViewById(R.id.tv_rightBtn);

        setShowArrow(false);
        setBackgroundColor(getResources().getColor(R.color.white_F5));
        setImageSetArrowIconID(R.mipmap.picker_arrow_down);
        completeText = getContext().getString(R.string.picker_str_title_right);
        selectDrawable = PCornerUtils.cornerDrawable(getThemeColor(), dp(2));
        int halfColor = Color.argb(100, Color.red(getThemeColor()), Color.green(getThemeColor()),
                Color.blue(getThemeColor()));
        unSelectDrawable = PCornerUtils.cornerDrawable(halfColor, dp(2));
        unSelectColor = Color.WHITE;
        selectColor = Color.WHITE;

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public View getCanClickToCompleteView() {
        return mCompleteBtn;
    }

    @Override
    public View getCanClickToIntentPreviewView() {
        return null;
    }

    @Override
    public View getCanClickToToggleFolderListView() {
        return canToggleFolderList ? mTvTitle : null;
    }

    @Override
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    @Override
    public void onTransitImageSet(boolean isOpen) {
        mSetArrowImg.setRotation(isOpen ? 180 : 0);
    }

    @Override
    public void onImageSetSelected(ImageSet imageSet) {
        if (canToggleFolderList) {
            mTvTitle.setText(imageSet.name);
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void refreshCompleteViewState(ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
        if (selectConfig.getMaxCount() <= 1 && selectConfig.isSinglePickAutoComplete()) {
            mCompleteBtn.setVisibility(View.GONE);
        } else {
            mCompleteBtn.setVisibility(View.VISIBLE);
            //设置标题栏右上角完成按钮选中和未选中样式，以及文字颜色
            if (selectedList == null || selectedList.size() == 0) {
                mCompleteBtn.setEnabled(false);
                mCompleteBtn.setText(completeText);
                mCompleteBtn.setTextColor(unSelectColor);
                mCompleteBtn.setBackground(unSelectDrawable);
            } else {
                mCompleteBtn.setEnabled(true);
                mCompleteBtn.setTextColor(selectColor);
                mCompleteBtn.setText(String.format("%s(%d/%d)", completeText, selectedList.size(), selectConfig.getMaxCount()));
                mCompleteBtn.setBackground(selectDrawable);
            }
        }
    }

    //------------ 以下为对外暴露的设置自定义UI的方法 ------------------

    /**
     * 标题栏剧中
     */
    public void centerTitle() {
        ((LinearLayout) mTvTitle.getParent()).setGravity(Gravity.CENTER);
    }

    /**
     * @param backIconID 设置返回按钮图标
     */
    public void setBackIconID(int backIconID) {
        ivBack.setImageDrawable(getResources().getDrawable(backIconID));
    }

    /**
     * @param canToggleFolderList 是否可以切换文件夹
     */
    public void setCanToggleFolderList(boolean canToggleFolderList) {
        this.canToggleFolderList = canToggleFolderList;
    }

    /**
     * 设置选中或未选中时的drawable
     *
     * @param selectDrawable   选中
     * @param unSelectDrawable 未选中
     */
    public void setCompleteBackground(Drawable selectDrawable, Drawable unSelectDrawable) {
        this.selectDrawable = selectDrawable;
        this.unSelectDrawable = unSelectDrawable;
        mCompleteBtn.setBackground(unSelectDrawable);
    }

    /**
     * 设置完成按钮的文本
     *
     * @param completeText 完成按钮的文本
     */
    public void setCompleteText(String completeText) {
        this.completeText = completeText;
        mCompleteBtn.setText(completeText);
    }

    /**
     * 设置完成按钮的文字颜色
     *
     * @param selectColor   选中颜色
     * @param unSelectColor 未选中颜色
     */
    public void setCompleteTextColor(int selectColor, int unSelectColor) {
        this.selectColor = selectColor;
        this.unSelectColor = unSelectColor;
        mCompleteBtn.setTextColor(unSelectColor);
    }

    /**
     * @param imageSetArrowIconID 设置标题栏文件夹切换的箭头图标
     */
    public void setImageSetArrowIconID(int imageSetArrowIconID) {
        mSetArrowImg.setImageDrawable(getResources().getDrawable(imageSetArrowIconID));
    }

    /**
     * @param isShow 是否显示标题栏文件夹切换的箭头图标
     */
    public void setShowArrow(boolean isShow) {
        mSetArrowImg.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setTitleTextColor(int color) {
        mTvTitle.setTextColor(color);
        mSetArrowImg.setColorFilter(color);
    }
}
