package com.ypx.imagepicker.activity.multi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.utils.PViewSizeUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 多选页
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public class MultiImagePickerActivity extends FragmentActivity {
    public static final String INTENT_KEY_SELECT_CONFIG = "MultiSelectConfig";
    public static final String INTENT_KEY_PRESENTER = "IMultiPickerBindPresenter";
    public static final String INTENT_KEY_CURRENT_INDEX = "currentIndex";
    public static final String INTENT_KEY_CURRENT_IMAGE = "currentImage";

    private MultiImagePickerFragment fragment;
    private MultiSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;

    /**
     * 跳转微信选择器页面
     *
     * @param activity     跳转的activity
     * @param selectConfig 配置项
     * @param presenter    IMultiPickerBindPresenter
     * @param listener     选择回调
     */
    public static void intent(Activity activity, MultiSelectConfig selectConfig, IMultiPickerBindPresenter presenter,
                              final OnImagePickCompleteListener listener) {
        if (PViewSizeUtils.onDoubleClick()) {
            return;
        }
        Intent intent = new Intent(activity, MultiImagePickerActivity.class);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_PRESENTER, presenter);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKER_RESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
                    listener.onImagePickComplete(list);
                } else if (listener instanceof OnImagePickCompleteListener2) {
                    if (resultCode == 0) {
                        resultCode = PickerError.CANCEL.getCode();
                    }
                    ((OnImagePickCompleteListener2) listener).onPickFailed(PickerError.valueOf(resultCode));
                }
            }
        });
    }

    /**
     * 校验传递数据是否合法
     */
    private boolean isIntentDataFailed() {
        selectConfig = (MultiSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_PRESENTER);
        if (presenter == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return true;
        }
        if (selectConfig == null) {
            PickerErrorExecutor.executeError(this, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isIntentDataFailed()) {
            return;
        }
        setContentView(R.layout.picker_activity_fragment_wrapper);
        setStatusBar();
        setFragment();
    }

    /**
     * 设置是否沉浸式状态栏
     */
    private void setStatusBar() {
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
    }

    /**
     * 填充选择器fragment
     */
    private void setFragment() {
        fragment = ImagePicker.withMulti(presenter)
                .withMultiSelectConfig(selectConfig)
                .pickWithFragment(new OnImagePickCompleteListener2() {
                    @Override
                    public void onPickFailed(PickerError error) {
                        PickerErrorExecutor.executeError(MultiImagePickerActivity.this, error.getCode());
                    }

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
}
