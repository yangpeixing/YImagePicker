package com.ypx.imagepicker.activity.crop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.PickerActivityManager;
import com.ypx.imagepicker.data.PickerActivityCallBack;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.bean.selectconfig.CropSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PViewSizeUtils;

import java.util.ArrayList;

/**
 * Description: 图片选择和剪裁页面
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x
 */
public class MultiImageCropActivity extends FragmentActivity {
    public static final String INTENT_KEY_DATA_PRESENTER = "ICropPickerBindPresenter";
    public static final String INTENT_KEY_SELECT_CONFIG = "selectConfig";
    private MultiImageCropFragment mFragment;
    private IPickerPresenter presenter;
    private CropSelectConfig selectConfig;

    /**
     * 跳转小红书剪裁页面
     *
     * @param activity     跳转activity
     * @param presenter    ICropPickerBindPresenter
     * @param selectConfig 选择器配置
     * @param listener     选择回调
     */
    public static void intent(@NonNull Activity activity, @NonNull IPickerPresenter presenter,
                              @NonNull CropSelectConfig selectConfig, final @NonNull OnImagePickCompleteListener listener) {
        if (!PViewSizeUtils.onDoubleClick()) {
            Intent intent = new Intent(activity, MultiImageCropActivity.class);
            intent.putExtra(MultiImageCropActivity.INTENT_KEY_DATA_PRESENTER, presenter);
            intent.putExtra(MultiImageCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
            PLauncher.init(activity).startActivityForResult(intent, PickerActivityCallBack.create(listener));
        }
    }

    /**
     * 校验传递数据是否合法
     */
    private boolean isIntentDataFailed() {
        presenter = (IPickerPresenter) getIntent().getSerializableExtra(INTENT_KEY_DATA_PRESENTER);
        selectConfig = (CropSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
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
        PickerActivityManager.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.picker_activity_fragment_wrapper);
        setFragment();
    }

    /**
     * 填充fragment
     */
    private void setFragment() {
        mFragment = ImagePicker.withCrop(presenter)
                .withSelectConfig(selectConfig)
                .pickWithFragment(new OnImagePickCompleteListener2() {
                    @Override
                    public void onPickFailed(PickerError error) {
                        PickerErrorExecutor.executeError(MultiImageCropActivity.this, error.getCode());
                        PickerActivityManager.clear();
                    }

                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        ImagePicker.closePickerWithCallback(items);
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
