package com.ypx.imagepicker.activity.crop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
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
    public static final String INTENT_KEY_IMAGELOADER = "ICropPickerBindPresenter";
    public static final String INTENT_KEY_MAXSELECTEDCOUNT = "maxSelectedCount";
    public static final String INTENT_KEY_FIRSTIMAGEITEM = "firstImageItem";
    public static final String INTENT_KEY_SHOWBOTTOMVIEW = "isShowBottomView";
    public static final String INTENT_KEY_CROPPICSAVEFILEPATH = "cropPicSaveFilePath";
    public static final String INTENT_KEY_SHOWDRAFTDIALOG = "isShowDraftDialog";
    public static final String INTENT_KEY_SHOWCAMERA = "isShowCamera";
    public static final String INTENT_KEY_SHOWVIDEO = "isShowVideo";
    public static final String INTENT_KEY_STARTDIRECT = "startDirect";
    private ImagePickAndCropFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (PStatusBarUtil.hasNotchInScreen(this)) {
            PStatusBarUtil.setStatusBar(this, Color.parseColor("#F6F6F6"),
                    false, true);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.picker_activity_fragment_wrapper);
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

        // 是否直接启动
        if (getIntent().hasExtra(INTENT_KEY_STARTDIRECT)) {
            bundle.putBoolean(INTENT_KEY_STARTDIRECT, getIntent().getBooleanExtra(INTENT_KEY_STARTDIRECT, true));
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
