package com.ypx.imagepicker.activity.crop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.PStatusBarUtil;

import java.util.ArrayList;

/**
 * Description: 图片选择和剪裁页面
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public class ImagePickAndCropActivity extends FragmentActivity {
    public static final String INTENT_KEY_DATA_PRESENTER = "ICropPickerBindPresenter";
    public static final String INTENT_KEY_SELECT_CONFIG = "selectConfig";
    private ImagePickAndCropFragment mFragment;
    private ICropPickerBindPresenter presenter;
    private CropSelectConfig selectConfig;

    public static void intent(Activity activity,
                              ICropPickerBindPresenter presenter,
                              CropSelectConfig selectConfig,
                              final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, ImagePickAndCropActivity.class);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_DATA_PRESENTER, presenter);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (data != null && data.hasExtra(ImagePicker.INTENT_KEY_PICKER_RESULT)
                        && resultCode == ImagePicker.REQ_PICKER_RESULT_CODE && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
                    listener.onImagePickComplete(list);
                } else if (listener instanceof OnImagePickCompleteListener2) {
                    ((OnImagePickCompleteListener2) listener).onPickFailed(PickerError.valueOf(resultCode));
                }
            }
        });
    }

    /**
     * 校验传递数据是否合法
     */
    private boolean isIntentDataValid() {
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_DATA_PRESENTER)) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return false;
        }
        //获取相关配置信息
        presenter = (ICropPickerBindPresenter) getIntent().
                getSerializableExtra(INTENT_KEY_DATA_PRESENTER);
        selectConfig = (CropSelectConfig) getIntent().
                getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        if (presenter == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return false;
        }
        if (selectConfig == null) {
            PickerErrorExecutor.executeError(this, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (isIntentDataValid()) {
            initView();
        }
    }

    /**
     * 启动参数转发
     */
    private void initView() {

        CropUiConfig uiConfig = presenter.getUiConfig(this);
        if (uiConfig == null) {
            uiConfig = new CropUiConfig();
        }

        //刘海屏幕需要适配状态栏颜色
        if (uiConfig.isShowStatusBar() || PStatusBarUtil.hasNotchInScreen(this)) {
            PStatusBarUtil.setStatusBar(this, uiConfig.getTitleBarBackgroundColor(),
                    false, PStatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.picker_activity_fragment_wrapper);
        //fragment构建
        mFragment = ImagePicker.withCrop(presenter)
                .withSelectConfig(selectConfig)
                .pickWithFragment(new OnImagePickCompleteListener2() {
                    @Override
                    public void onPickFailed(PickerError error) {
                        PickerErrorExecutor.executeError(ImagePickAndCropActivity.this, error.getCode());
                    }

                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, items);
                        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
                        finish();
                    }
                });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, mFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (null != mFragment && mFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
