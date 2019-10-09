package com.ypx.imagepicker.activity.crop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.PStatusBarUtil;

import java.util.ArrayList;

/**
 * Description: 图片选择和剪裁页面
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImagePickAndCropActivity extends FragmentActivity {
    //拍照返回码、拍照权限码
    public static final int REQ_CAMERA = 1431;
    //存储权限码
    public static final int REQ_STORAGE = 1432;
    public static final String INTENT_KEY_DATA_PRESENTER = "ICropPickerBindPresenter";
    public static final String INTENT_KEY_SELECT_CONFIG = "selectConfig";
    private ImagePickAndCropFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.picker_activity_fragment_wrapper);
        initView();
    }

    /**
     * 启动参数转发
     */
    private void initView() {
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_DATA_PRESENTER)) {
            finish();
            return;
        }

        ICropPickerBindPresenter presenter = (ICropPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_DATA_PRESENTER);
        CropSelectConfig selectConfig = (CropSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        if (presenter == null || selectConfig == null) {
            finish();
            return;
        }

        if (PStatusBarUtil.hasNotchInScreen(this)) {
            CropUiConfig cropUiConfig = presenter.getUiConfig(this);
            if (cropUiConfig == null) {
                cropUiConfig = new CropUiConfig();
            }
            PStatusBarUtil.setStatusBar(this, cropUiConfig.getTitleBarBackgroundColor(),
                    false, true);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        mFragment = ImagePicker.withCrop(presenter).
                withSelectConfig(selectConfig).
                pickWithFragment(new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        Intent intent = new Intent();
                        intent.putExtra(ImagePicker.INTENT_KEY_PICKERRESULT, items);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFragment != null) {
            mFragment.onTakePhotoResult(requestCode, resultCode);
        }
    }
}
