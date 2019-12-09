package com.ypx.imagepicker.views.wx;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.views.base.SingleCropControllerView;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

public class WXSingleCropControllerView extends SingleCropControllerView {
    private TextView mCompleteBtn;

    public WXSingleCropControllerView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_wx_crop;
    }

    @Override
    protected void initView(View view) {
        RelativeLayout mTitleBar = view.findViewById(R.id.mTitleBar);
        ImageView mIvBack = view.findViewById(R.id.iv_back);
        mCompleteBtn = view.findViewById(R.id.tv_rightBtn);
        mTitleBar.setBackgroundColor(Color.WHITE);
        mCompleteBtn.setBackground(PCornerUtils.cornerDrawable(getResources().getColor(R.color.wx), dp(2)));
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setStatusBar() {
        PStatusBarUtil.setStatusBar((Activity) getContext(), Color.WHITE, false, true);
    }

    @Override
    public View getCompleteView() {
        return mCompleteBtn;
    }

    @Override
    public void setCropViewParams(CropImageView cropImageView, ViewGroup.MarginLayoutParams params) {
        params.topMargin = dp(50);
        cropImageView.setLayoutParams(params);
    }

}
