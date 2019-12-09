package com.example.imagepicker_support.style.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.example.imagepicker_support.R;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.views.base.SingleCropControllerView;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

public class CustomCropControllerView extends SingleCropControllerView {
    private ImageView mCloseImg;
    private ImageView mOkImg;

    public CustomCropControllerView(Context context) {
        super(context);
    }

    /**
     * @return item布局id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.layout_custom_crop;
    }

    /**
     * @param view 初始化view
     */
    @Override
    protected void initView(View view) {
        mCloseImg = view.findViewById(R.id.mCloseImg);
        mOkImg = view.findViewById(R.id.mOkImg);
        mCloseImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setStatusBar() {
        PStatusBarUtil.fullScreenWithCheckNotch((Activity) getContext(), Color.BLACK);
    }

    @Override
    public View getCompleteView() {
        return mOkImg;
    }

    @Override
    public void setCropViewParams(CropImageView cropImageView, MarginLayoutParams params) {
        params.bottomMargin = dp(60);
    }

}
