package com.ypx.wximagepicker.ui.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oginotihiro.cropview.CropView;
import com.ypx.wximagepicker.R;
import com.ypx.wximagepicker.YPXImagePicker;
import com.ypx.wximagepicker.bean.SimpleImageItem;
import com.ypx.wximagepicker.bean.UiConfig;
import com.ypx.wximagepicker.config.IImgPickerUIConfig;
import com.ypx.wximagepicker.utils.StatusBarUtils;
import com.ypx.wximagepicker.utils.TakePhotoUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 截取头像
 */
public class YPXImageCropActivity extends FragmentActivity {
    private CropView cropView;
    private String imagePath;
    private Bitmap bmp = null;

    private IImgPickerUIConfig iImgPickerUIConfig;
    private UiConfig uiConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_crop);
        cropView = (CropView) findViewById(R.id.iv_pic);
        dealIntentData();
        setTitleBar();

        if (TextUtils.isEmpty(imagePath)) {
            throw new RuntimeException("AndroidImagePicker:you have to give me an image path from sdcard");
        } else {
            Uri source = Uri.parse(imagePath);
            cropView.of(source).asSquare().initialize(this);
        }

    }

    /**
     * 接收传参
     */
    private void dealIntentData() {
        iImgPickerUIConfig = (IImgPickerUIConfig) getIntent().getSerializableExtra("IImgPickerUIConfig");
        uiConfig = iImgPickerUIConfig.getUiConfig(this);
        imagePath = "file://" + getIntent().getStringExtra("imagePath");
    }

    private void setTitleBar() {
        RelativeLayout top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        ImageView iv_back = (ImageView) findViewById(R.id.iv_back);
        if (uiConfig.isImmersionBar() && uiConfig.getTopBarBackgroundColor() != 0) {
            StatusBarUtils.setWindowStatusBarColor(this, uiConfig.getTopBarBackgroundColor());
        }
        if (uiConfig.getBackIconID() != 0) {
            iv_back.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
        }

        if (uiConfig.getTopBarBackgroundColor() != 0) {
            top_bar.setBackgroundColor(uiConfig.getTopBarBackgroundColor());
        }

        if (uiConfig.getLeftBackIconColor() != 0) {
            iv_back.setColorFilter(uiConfig.getLeftBackIconColor());
        }

        if (uiConfig.getRightBtnBackground() != 0) {
            tv_rightBtn.setBackground(getResources().getDrawable(uiConfig.getRightBtnBackground()));
        }

        if (uiConfig.getTitleColor() != 0) {
            tv_title.setTextColor(uiConfig.getTitleColor());
        }

        if (uiConfig.getRightBtnTextColor() != 0) {
            tv_rightBtn.setTextColor(uiConfig.getRightBtnTextColor());
        }

        tv_title.setGravity(Gravity.CENTER | uiConfig.getTopBarTitleGravity());
       // tv_rightBtn.setTextColor(uiConfig.getThemeColor());
        tv_rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        bmp = cropView.getOutput();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bmp == null || imagePath == null) {
                                    Toast.makeText(YPXImageCropActivity.this, "剪裁图片失败!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SimpleImageItem item = new SimpleImageItem(TakePhotoUtil.saveBitmapToPic(bmp), "", -1);
                                List<SimpleImageItem> list = new ArrayList<>();
                                list.add(item);
                                YPXImagePicker.notifyOnImagePickComplete(list);
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                    }
                }.start();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
