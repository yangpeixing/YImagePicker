package com.ypx.imagepicker.views.redbook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.annotation.NonNull;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.views.base.PickerItemView;

/**
 * Time: 2019/11/13 16:17
 * Author:ypx
 * Description:小红书样式item
 */
public class RedBookItemView extends PickerItemView {
    private ImageView imageView;
    private View mVMask, mVSelect;
    private TextView mTvIndex;
    private TextView mTvDuration;
    private BaseSelectConfig selectConfig;

    public RedBookItemView(Context context) {
        super(context);
    }

    public RedBookItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RedBookItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @return item布局id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.picker_item;
    }

    /**
     * @param view 初始化view
     */
    @Override
    protected void initView(View view) {
        mTvIndex = view.findViewById(R.id.mTvIndex);
        mVMask = view.findViewById(R.id.v_mask);
        mVSelect = view.findViewById(R.id.v_select);
        imageView = view.findViewById(R.id.iv_image);
        mTvDuration = view.findViewById(R.id.mTvDuration);
    }

    @Override
    public View getCameraView(BaseSelectConfig selectConfig, IPickerPresenter presenter) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.picker_item_camera, null);
        TextView mTvvCamera = view.findViewById(R.id.tv_camera);
        mTvvCamera.setText(selectConfig.isOnlyShowVideo() ?
                getContext().getString(R.string.picker_str_item_take_video) :
                getContext().getString(R.string.picker_str_item_take_photo));
        return view;
    }

    @Override
    public View getCheckBoxView() {
        return mVSelect;
    }

    @Override
    public void initItem(ImageItem imageItem, IPickerPresenter presenter, BaseSelectConfig selectConfig) {
        this.selectConfig = selectConfig;
        presenter.displayImage(imageView, imageItem, imageView.getWidth(), true);
    }

    @Override
    public void disableItem(ImageItem imageItem, int disableCode) {
        //默认开启校验是否超过最大数时item状态为不可选中,这里关闭它
        if (disableCode == PickerItemDisableCode.DISABLE_OVER_MAX_COUNT) {
            return;
        }
        mVMask.setVisibility(View.VISIBLE);
        mVMask.setBackgroundColor(Color.parseColor("#80FFFFFF"));
        mTvIndex.setVisibility(View.GONE);
        mVSelect.setVisibility(View.GONE);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void enableItem(ImageItem imageItem, boolean isChecked, int indexOfSelectedList) {
        mTvIndex.setVisibility(View.VISIBLE);
        mVSelect.setVisibility(View.VISIBLE);
        if (imageItem.isVideo()) {
            mTvDuration.setVisibility(View.VISIBLE);
            mTvDuration.setText(imageItem.getDurationFormat());
            if (selectConfig.isVideoSinglePick() && selectConfig.isSinglePickAutoComplete()) {
                mTvIndex.setVisibility(GONE);
                mVSelect.setVisibility(GONE);
            }
        } else {
            mTvDuration.setVisibility(View.GONE);
        }

        if (indexOfSelectedList >= 0) {
            mTvIndex.setText(String.format("%d", indexOfSelectedList + 1));
            mTvIndex.setBackground(PCornerUtils.cornerDrawableAndStroke(getThemeColor(), dp(12), dp(1), Color.WHITE));
        } else {
            mTvIndex.setBackground(getResources().getDrawable(R.mipmap.picker_icon_unselect));
            mTvIndex.setText("");
        }

        if (imageItem.isPress()) {
            mVMask.setVisibility(View.VISIBLE);
            int halfColor = Color.argb(100, Color.red(getThemeColor()), Color.green(getThemeColor()),
                    Color.blue(getThemeColor()));
            Drawable maskDrawable = PCornerUtils.cornerDrawableAndStroke(halfColor, 0, dp(2), getThemeColor());
            mVMask.setBackground(maskDrawable);
        } else {
            mVMask.setVisibility(View.GONE);
        }
    }
}
