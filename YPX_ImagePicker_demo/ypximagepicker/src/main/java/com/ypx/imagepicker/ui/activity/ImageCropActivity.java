package com.ypx.imagepicker.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oginotihiro.cropview.CropView;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.YPXImagePicker;


/**
 * 截取头像
 */
public class ImageCropActivity extends FragmentActivity implements View.OnClickListener {
    CropView cropView;
    String imagePath;
    Bitmap bmp = null;
    String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        setContentView(R.layout.ipk_activity_crop);
        TextView btnOk = (TextView) findViewById(R.id.btn_pic_ok);
        TextView btnReChoose = (TextView) findViewById(R.id.btn_pic_rechoose);
        cropView = (CropView) findViewById(R.id.iv_pic);
        btnOk.setOnClickListener(this);
        btnReChoose.setOnClickListener(this);

        imagePath = "file://" + getIntent().getStringExtra("key_pic_path");
        if (TextUtils.isEmpty(imagePath)) {
            throw new RuntimeException("AndroidImagePicker:you have to give me an image path from sdcard");
        } else {
            Uri source = Uri.parse(imagePath);
            cropView.of(source).asSquare().initialize(this);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pic_ok) {
            // loadding.show("照片裁剪中...");
            new Thread() {
                public void run() {
                    bmp = cropView.getOutput();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (bmp == null || path == null) {
                                Toast.makeText(ImageCropActivity.this, "剪裁图片失败!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            finish();
                            YPXImagePicker.getInstance().notifyImageCropComplete(path, bmp, 0);
                        }
                    });
                }
            }.start();
        } else if (v.getId() == R.id.btn_pic_rechoose) {
            finish();
        }
    }
}
