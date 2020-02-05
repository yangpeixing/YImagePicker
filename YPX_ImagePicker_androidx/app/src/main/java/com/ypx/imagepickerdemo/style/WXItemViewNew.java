package com.ypx.imagepickerdemo.style;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.views.wx.WXItemView;
import com.ypx.imagepickerdemo.R;

public class WXItemViewNew extends WXItemView {
    public WXItemViewNew(Context context) {
        super(context);
    }

    @Override
    public void initItem(ImageItem imageItem, IPickerPresenter presenter, BaseSelectConfig selectConfig) {
        super.initItem(imageItem, presenter, selectConfig);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                findViewById(R.id.mCheckBoxPanel).getLayoutParams();
        params.height = dp(50);
        params.width = dp(50);

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams)
                findViewById(R.id.mCheckBox).getLayoutParams();
        params1.gravity = Gravity.END;
        params1.topMargin = dp(5);
        params1.rightMargin = dp(10);
    }
}
