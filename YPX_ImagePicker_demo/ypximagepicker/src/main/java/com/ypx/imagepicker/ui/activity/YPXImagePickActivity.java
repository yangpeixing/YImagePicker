package com.ypx.imagepicker.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.interf.OnImagePickCompleteListener;

import java.util.ArrayList;

/**
 * 作者：yangpeixing on 2018/6/21 14:02
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class YPXImagePickActivity extends Activity {
    OnImagePickCompleteListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagepick);
        if (getIntent() != null) {
            listener = (OnImagePickCompleteListener) getIntent().getSerializableExtra("OnImagePickCompleteListener");
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    ArrayList<ImageItem> list = new ArrayList<>();
                    for (int i = 0; i < 9; i++) {
                        ImageItem imageItem = new ImageItem("path" + i, "name" + i, i);
                        list.add(imageItem);
                    }
                    listener.onImagePickComplete(list);
                    finish();
                }
            }
        });

    }


}
