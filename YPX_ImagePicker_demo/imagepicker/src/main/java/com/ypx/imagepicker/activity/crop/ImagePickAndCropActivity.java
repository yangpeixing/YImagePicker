package com.ypx.imagepicker.activity.crop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.FileUtil;
import com.ypx.imagepicker.utils.StatusBarUtil;
import com.ypx.imagepicker.utils.TakePhotoUtil;

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
    public static final String INTENT_KEY_IMAGELOADER = "ICropPickerBindPresenter";
    public static final String INTENT_KEY_MAXSELECTEDCOUNT = "maxSelectedCount";
    public static final String INTENT_KEY_FIRSTIMAGEITEM = "firstImageItem";
    public static final String INTENT_KEY_SHOWBOTTOMVIEW = "isShowBottomView";
    public static final String INTENT_KEY_CROPPICSAVEFILEPATH = "cropPicSaveFilePath";
    public static final String INTENT_KEY_SHOWDRAFTDIALOG = "isShowDraftDialog";
    public static final String INTENT_KEY_SHOWCAMERA = "isShowCamera";
    public static final String INTENT_KEY_SHOWVIDEO = "isShowVideo";
    private ImagePickAndCropFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (StatusBarUtil.hasNotchOPPO(this)) {
            setStatusBar(Color.TRANSPARENT, true, true);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_fragment_wrapper);
        initView();
    }

    /**
     * 启动参数转发
     */
    private void initView() {
        Bundle bundle = new Bundle();
        // 接口回调
        if (getIntent().hasExtra(INTENT_KEY_IMAGELOADER)) {
            bundle.putSerializable(INTENT_KEY_IMAGELOADER, getIntent().getSerializableExtra(INTENT_KEY_IMAGELOADER));
        }

        // 最大选中数
        if (getIntent().hasExtra(INTENT_KEY_MAXSELECTEDCOUNT)) {
            bundle.putInt(INTENT_KEY_MAXSELECTEDCOUNT, getIntent().getIntExtra(INTENT_KEY_MAXSELECTEDCOUNT, 9));
        }

        // 第一个选中的图片信息
        if (getIntent().hasExtra(INTENT_KEY_FIRSTIMAGEITEM)) {
            bundle.putSerializable(INTENT_KEY_FIRSTIMAGEITEM, getIntent().getSerializableExtra(INTENT_KEY_FIRSTIMAGEITEM));
        }

        // 是否显示底部自定义View
        if (getIntent().hasExtra(INTENT_KEY_SHOWBOTTOMVIEW)) {
            bundle.putBoolean(INTENT_KEY_SHOWBOTTOMVIEW, getIntent().getBooleanExtra(INTENT_KEY_SHOWBOTTOMVIEW, false));
        }

        // 设置剪裁图片后的文件保存路径
        if (getIntent().hasExtra(INTENT_KEY_CROPPICSAVEFILEPATH)) {
            bundle.putString(INTENT_KEY_CROPPICSAVEFILEPATH, getIntent().getStringExtra(INTENT_KEY_CROPPICSAVEFILEPATH));
        }

        // 是否显示草稿箱对话框
        if (getIntent().hasExtra(INTENT_KEY_SHOWDRAFTDIALOG)) {
            bundle.putBoolean(INTENT_KEY_SHOWDRAFTDIALOG, getIntent().getBooleanExtra(INTENT_KEY_SHOWDRAFTDIALOG, false));
        }

        // 是否显示拍照按钮
        if (getIntent().hasExtra(INTENT_KEY_SHOWCAMERA)) {
            bundle.putBoolean(INTENT_KEY_SHOWCAMERA, getIntent().getBooleanExtra(INTENT_KEY_SHOWCAMERA, false));
        }

        // 是否显示视频
        if (getIntent().hasExtra(INTENT_KEY_SHOWVIDEO)) {
            bundle.putBoolean(INTENT_KEY_SHOWVIDEO, getIntent().getBooleanExtra(INTENT_KEY_SHOWVIDEO, false));
        }
        mFragment = new ImagePickAndCropFragment();
        mFragment.setArguments(bundle);
        mFragment.setImageListener(new OnImagePickCompleteListener() {
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

    public void back(View view) {
        if (null != mFragment && mFragment.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    protected void setStatusBar(int bgColor, boolean isFullScreen, boolean isDarkStatusBarIcon) {
        //5.0以下不处理
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        int option = 0;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //只有在6.0以上才改变状态栏颜色，否则在5.0机器上，电量条图标是白色的，标题栏也是白色的，就看不见电量条了了
        //在5.0上显示默认灰色背景色
        if (Build.VERSION.SDK_INT >= 23) {
            // 设置状态栏底色颜色
            getWindow().setStatusBarColor(bgColor);
            //浅色状态栏，则让状态栏图标变黑，深色状态栏，则让状态栏图标变白
            if (isDarkStatusBarIcon) {
                if (isFullScreen) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
            } else {
                if (isFullScreen) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
                } else {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
                }
            }
        } else {
            getWindow().setStatusBarColor(Color.parseColor("#B0B0B0"));
            if (isFullScreen) {
                option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            } else {
                option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(option);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CAMERA) {
            if (mFragment != null) {
                mFragment.refreshPhoto();
            }
        }
    }
}
