package com.ypx.imagepickerdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.helper.CropHelper;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.utils.PFileUtil;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.widget.CropImageView;
import com.ypx.imagepicker.widget.browseimage.Info;

import java.io.File;

/**
 * Time: 2019/8/14 16:10
 * Author:ypx
 * Description:
 */
public class MarsCropActivity extends FragmentActivity implements View.OnClickListener {
    private static final String KEY_IMAGE_ITEM = "key_imageItem";
    private TextView mTvOriginal;
    private TextView mTvRatio11;
    private TextView mTvRatio43;
    private TextView mTvRatio34;
    private LinearLayout mBottomBar;
    private CropImageView mCropImg;
    private ImageItem imageItem;
    private int unSelectColor;
    private int selectColor;
    private boolean isOrig = true;

    public interface ImageCropResult {
        void cropEnd(ImageItem imageItem);
    }

    public static void intent(Activity activity, ImageItem imageItem, final ImageCropResult result) {
        Intent intent = new Intent(activity, MarsCropActivity.class);
        intent.putExtra(KEY_IMAGE_ITEM, imageItem);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (result != null && data != null && data.hasExtra(KEY_IMAGE_ITEM)) {
                    result.cropEnd((ImageItem) data.getSerializableExtra(KEY_IMAGE_ITEM));
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PStatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true, false);
        setContentView(R.layout.activity_crop);
        if (getIntent().hasExtra(KEY_IMAGE_ITEM)) {
            imageItem = (ImageItem) getIntent().getSerializableExtra(KEY_IMAGE_ITEM);
            if (imageItem == null) {
                finish();
            }
        }
        findView();

        mCropImg.enable(); // 启用图片缩放功能
        mCropImg.setMaxScale(7.0f);
        mCropImg.setRotateEnable(false);
        Glide.with(this).load(imageItem.path).into(mCropImg);
        final Info info = CropInfoManager.instance.getInfo(imageItem);
        if (info != null) {
            isOrig = false;
            mCropImg.setRestoreInfo(info);
            if (info.mCropX == 1 && info.mCropY == 1) {
                press(mTvRatio11);
            } else if (info.mCropX <= 0 || info.mCropY <= 0) {
                press(mTvOriginal);
            } else if (info.mCropX == 3 && info.mCropY == 4) {
                press(mTvRatio34);
            } else if (info.mCropX == 4 && info.mCropY == 3) {
                press(mTvRatio43);
            }
        } else {
            isOrig = true;
            mTvOriginal.performClick();
        }
    }

    private void findView() {
        mTvOriginal = findViewById(R.id.mTvOriginal);
        mTvRatio11 = findViewById(R.id.mTvRatio11);
        mTvRatio43 = findViewById(R.id.mTvRatio43);
        mTvRatio34 = findViewById(R.id.mTvRatio34);
        mBottomBar = findViewById(R.id.mBottomBar);
        mCropImg = findViewById(R.id.mCropImg);
        unSelectColor = Color.parseColor("#B0B0B0");
        selectColor = Color.parseColor("#859D7B");

        mTvOriginal.setOnClickListener(this);
        mTvRatio11.setOnClickListener(this);
        mTvRatio34.setOnClickListener(this);
        mTvRatio43.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        isOrig = v == mTvOriginal;
        press((TextView) v);
        if (v == mTvOriginal) {
            mCropImg.setCropRatio(-1, -1);
        } else if (v == mTvRatio11) {
            mCropImg.setCropRatio(1, 1);
        } else if (v == mTvRatio34) {
            mCropImg.setCropRatio(3, 4);
        } else if (v == mTvRatio43) {
            mCropImg.setCropRatio(4, 3);
        }
    }


    private void unPressAll() {
        mTvOriginal.setTextColor(unSelectColor);
        setDrawableTop(mTvOriginal, 0);
        mTvRatio11.setTextColor(unSelectColor);
        setDrawableTop(mTvRatio11, 0);
        mTvRatio34.setTextColor(unSelectColor);
        setDrawableTop(mTvRatio34, 0);
        mTvRatio43.setTextColor(unSelectColor);
        setDrawableTop(mTvRatio43, 0);
    }

    private void press(TextView textView) {
        unPressAll();
        textView.setTextColor(selectColor);
        if (textView == mTvOriginal) {
            setDrawableTop(mTvOriginal, 0);
        } else if (textView == mTvRatio11) {
            setDrawableTop(mTvRatio11, 0);
        } else if (textView == mTvRatio34) {
            setDrawableTop(mTvRatio34, 0);
        } else if (textView == mTvRatio43) {
            setDrawableTop(mTvRatio43, 0);
        }
    }

    private void setDrawableTop(TextView textView, int id) {
        if (id == 0) {
            return;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(id),
                null, null);
    }

    public void back(View view) {
        finish();
    }

    public void ok(View view) {
        if (imageItem == null) {
            return;
        }
        if (!isOrig) {
            Bitmap bitmap = CropHelper.cropViewToBitmap(mCropImg, mCropImg.getCropWidth(), mCropImg.getCropHeight());
            File f = new File(ImagePicker.cropPicSaveFilePath, "crop_" + System.currentTimeMillis() + ".jpg");
            String cropUrl = PFileUtil.saveBitmapToLocalWithJPEG(bitmap, f.getAbsolutePath());
            imageItem.setCropUrl(cropUrl);
        } else {
            imageItem.setCropUrl(imageItem.path);
        }
        CropInfoManager.instance.addInfo(imageItem, mCropImg.getInfo());
        Intent intent = new Intent();
        intent.putExtra(KEY_IMAGE_ITEM, imageItem);
        setResult(RESULT_OK, intent);

        finish();
    }
}
