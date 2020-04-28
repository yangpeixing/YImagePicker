package com.ypx.imagepicker.views.wx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.views.base.PickerItemView;
import com.ypx.imagepicker.widget.ShowTypeImageView;

/**
 * Time: 2019/8/8 15:45
 * Author:ypx
 * Description: 微信样式item
 */
public class WXItemView extends PickerItemView {
    private ShowTypeImageView mImageView;
    private View mVMasker;
    private CheckBox mCheckBox;
    private FrameLayout mCheckBoxPanel;
    private TextView mVideoTime;
    private LinearLayout mVideoLayout;
    private BaseSelectConfig selectConfig;

    public WXItemView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_image_grid_item;
    }

    @Override
    protected void initView(View view) {
        mImageView = view.findViewById(R.id.mImageView);
        mVMasker = view.findViewById(R.id.v_masker);
        mCheckBox = view.findViewById(R.id.mCheckBox);
        mCheckBoxPanel = view.findViewById(R.id.mCheckBoxPanel);
        mVideoTime = view.findViewById(R.id.mVideoTime);
        mVideoLayout = view.findViewById(R.id.mVideoLayout);

        mCheckBox.setClickable(false);
        Drawable unSelectDrawable = getResources().getDrawable(R.mipmap.picker_wechat_unselect);
        unSelectDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        setCheckBoxDrawable(unSelectDrawable, getResources().getDrawable(R.mipmap.picker_wechat_select));
    }

    @SuppressLint("InflateParams")
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
        return mCheckBoxPanel;
    }

    @Override
    public void initItem(ImageItem imageItem, IPickerPresenter presenter, BaseSelectConfig selectConfig) {
        this.selectConfig = selectConfig;
        presenter.displayImage(mImageView, imageItem, mImageView.getWidth(), true);
    }

    @Override
    public void disableItem(ImageItem imageItem, int disableCode) {
        //默认开启校验是否超过最大数,当超过最大选择数量时，
        if (disableCode == PickerItemDisableCode.DISABLE_OVER_MAX_COUNT) {
            return;
        }
        mCheckBox.setVisibility(View.GONE);
        mVMasker.setVisibility(View.VISIBLE);
        mVMasker.setBackgroundColor(Color.parseColor("#80FFFFFF"));
    }

    @Override
    public void enableItem(ImageItem imageItem, boolean isChecked, int indexOfSelectedList) {
        if (imageItem.isVideo()) {
            mVideoLayout.setVisibility(View.VISIBLE);
            mVideoTime.setText(imageItem.getDurationFormat());
            mImageView.setType(ShowTypeImageView.TYPE_NONE);
        } else {
            mVideoLayout.setVisibility(View.GONE);
            mImageView.setTypeFromImage(imageItem);
        }


        mCheckBox.setVisibility(View.VISIBLE);
        mCheckBoxPanel.setVisibility(View.VISIBLE);

        boolean isVideoSinglePickAndAutoComplete = imageItem.isVideo() && selectConfig.isVideoSinglePickAndAutoComplete();
        if (isVideoSinglePickAndAutoComplete || (selectConfig.isSinglePickAutoComplete() && selectConfig.getMaxCount() <= 1)) {
            mCheckBox.setVisibility(View.GONE);
            mCheckBoxPanel.setVisibility(View.GONE);
        }

        mCheckBox.setChecked(isChecked);
        mVMasker.setVisibility(isChecked ? VISIBLE : GONE);
        mVMasker.setBackgroundColor(isChecked ? Color.parseColor("#80000000") : Color.TRANSPARENT);
    }


    public void setCheckBoxDrawable(int unCheckDrawableID, int checkedDrawableID) {
        PCornerUtils.setCheckBoxDrawable(mCheckBox, checkedDrawableID, unCheckDrawableID);
    }

    public void setCheckBoxDrawable(Drawable unCheckDrawable, Drawable checkedDrawable) {
        PCornerUtils.setCheckBoxDrawable(mCheckBox, checkedDrawable, unCheckDrawable);
    }
}
