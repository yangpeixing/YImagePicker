package com.ypx.imagepickerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;

import java.util.ArrayList;

/**
 * Time: 2019/11/6 18:24
 * Author:ypx
 * Description:
 */
public class SecondActivity extends Activity {
    ArrayList<ImageItem> imageItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        imageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
    }

    public void click(View view) {
        ImagePicker.closePickerWithCallback(imageItems);
        finish();
    }
}
