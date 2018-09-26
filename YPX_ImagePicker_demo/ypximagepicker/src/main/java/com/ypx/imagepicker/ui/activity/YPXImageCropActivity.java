package com.ypx.imagepicker.ui.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oginotihiro.cropview.CropView;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.YPXImagePicker;
import com.ypx.imagepicker.YPXImagePickerUiBuilder;
import com.ypx.imagepicker.ui.view.DefaultTitleBar;
import com.ypx.imagepicker.utils.CornerUtils;


/**
 * 截取头像
 */
public class YPXImageCropActivity extends FragmentActivity {
    private CropView cropView;
    private YPXImagePickerUiBuilder uiBuilder;
    private String imagePath;
    private Bitmap bmp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_crop);
        uiBuilder = YPXImagePicker.getInstance().getUiBuilder();
        if (uiBuilder == null) {
            uiBuilder = new YPXImagePickerUiBuilder(this);
        }
        cropView = (CropView) findViewById(R.id.iv_pic);
        initTitleBar();
        imagePath = "file://" + getIntent().getStringExtra("key_pic_path");
        if (TextUtils.isEmpty(imagePath)) {
            throw new RuntimeException("AndroidImagePicker:you have to give me an image path from sdcard");
        } else {
            Uri source = Uri.parse(imagePath);
            cropView.of(source).asSquare().initialize(this);
        }

    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        RelativeLayout top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        top_bar.removeAllViews();
        DefaultTitleBar titleBar = new DefaultTitleBar(this);
        top_bar.addView(titleBar);
        TextView tv_complete = titleBar.getCompleteTextView();
        ImageView iv_back = titleBar.getLeftIconImageView();
        TextView tv_title = titleBar.getTitleTextView();
        iv_back.setColorFilter(uiBuilder.getThemeColor());
        tv_complete.setBackground(CornerUtils.halfAlphaSelector(titleBar.dp(5), uiBuilder.getThemeColor()));
        tv_complete.setText("完成");
        tv_title.setText("剪裁图片");
        tv_complete.setOnClickListener(new View.OnClickListener() {
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
                                finish();
                                YPXImagePicker.getInstance().notifyImageCropComplete(imagePath, bmp, 0);
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
