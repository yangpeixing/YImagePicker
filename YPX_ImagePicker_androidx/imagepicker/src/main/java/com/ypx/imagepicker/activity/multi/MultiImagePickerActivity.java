package com.ypx.imagepicker.activity.multi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PStatusBarUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 多选页
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiImagePickerActivity extends FragmentActivity {
    public static final String INTENT_KEY_SELECT_CONFIG = "MultiSelectConfig";
    public static final String INTENT_KEY_UI_CONFIG = "IMultiPickerBindPresenter";
    public static final String INTENT_KEY_CURRENT_INDEX = "currentIndex";
    public static final String INTENT_KEY_CURRENT_IMAGE = "currentImage";
    public static final int REQ_CAMERA = 1431;

    private MultiImagePickerFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_fragment_wrapper);
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_SELECT_CONFIG)
                || !getIntent().hasExtra(INTENT_KEY_UI_CONFIG)) {
            finish();
            return;
        }
        MultiSelectConfig selectConfig = (MultiSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        IMultiPickerBindPresenter presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_UI_CONFIG);
        if (selectConfig == null || presenter == null) {
            finish();
            return;
        }
        View mStatusBar = findViewById(R.id.mStatusBar);
        PickerUiConfig uiConfig = presenter.getUiConfig(this);
        if (uiConfig != null && uiConfig.isImmersionBar()) {
            mStatusBar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mStatusBar.getLayoutParams();
            params.height = PStatusBarUtil.getStatusBarHeight(this);
            mStatusBar.setBackgroundColor(uiConfig.getTitleBarBackgroundColor());
            PStatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true,
                    PStatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));
        } else {
            mStatusBar.setVisibility(View.GONE);
        }

        fragment = ImagePicker.withMulti(presenter)
                .withMultiSelectConfig(selectConfig)
                .pickWithFragment(new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, (Serializable) items);
                        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
                        finish();
                    }
                });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onTakePhotoResult(requestCode, resultCode);
        }
    }
}
