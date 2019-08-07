package com.ypx.imagepicker.activity.multi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.oginotihiro.cropview.CropView;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.MultiUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.StatusBarUtil;
import com.ypx.imagepicker.utils.TakePhotoUtil;

import java.io.Serializable;
import java.util.ArrayList;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_CURRENT_IMAGE;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_UI_CONFIG;


/**
 * 截取头像
 */
public class SingleCropActivity extends FragmentActivity {
    private CropView cropView;
    private String imagePath;
    private Bitmap bmp = null;
    private MultiUiConfig multiUiConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_crop);
        if (getIntent() != null && getIntent().hasExtra(INTENT_KEY_UI_CONFIG)) {
            IMultiPickerBindPresenter presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_UI_CONFIG);
            MultiSelectConfig selectConfig = (MultiSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
            multiUiConfig = presenter.getUiConfig(this);
            imagePath = "file://" + getIntent().getStringExtra(INTENT_KEY_CURRENT_IMAGE);
            cropView = findViewById(R.id.iv_pic);
            setTitleBar();
            Uri source = Uri.parse(imagePath);
            cropView.of(source).withAspect(selectConfig.getCropRatioX(),
                    selectConfig.getCropRatioY()).initialize(this);
        } else {
            finish();
        }
    }

    private void setTitleBar() {
        ViewGroup top_bar = findViewById(R.id.top_bar);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_rightBtn = findViewById(R.id.tv_rightBtn);
        ImageView iv_back = findViewById(R.id.iv_back);
        if (multiUiConfig.isImmersionBar() && multiUiConfig.getTopBarBackgroundColor() != 0) {
            StatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true,
                    StatusBarUtil.isDarkColor(multiUiConfig.getTopBarBackgroundColor()));

            top_bar.setPadding(0,StatusBarUtil.getStatusBarHeight(this),0,0);
        }
        if (multiUiConfig.getBackIconID() != 0) {
            iv_back.setImageDrawable(getResources().getDrawable(multiUiConfig.getBackIconID()));
        }

        if (multiUiConfig.getTopBarBackgroundColor() != 0) {
            top_bar.setBackgroundColor(multiUiConfig.getTopBarBackgroundColor());
        }

        if (multiUiConfig.getBackIconColor() != 0) {
            iv_back.setColorFilter(multiUiConfig.getBackIconColor());
        }

        if (multiUiConfig.getTitleColor() != 0) {
            tv_title.setTextColor(multiUiConfig.getTitleColor());
        }

        ((LinearLayout)findViewById(R.id.mTitleRoot)).setGravity(multiUiConfig.getTopBarTitleGravity());
        tv_rightBtn.setBackground(multiUiConfig.getOkBtnSelectBackground());
        if (multiUiConfig.getOkBtnSelectTextColor() != 0) {
            tv_rightBtn.setTextColor(multiUiConfig.getOkBtnSelectTextColor());
        }
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
                                    Toast.makeText(SingleCropActivity.this, "剪裁图片失败!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ImageItem item = new ImageItem();
                                item.path = TakePhotoUtil.saveBitmapToPic(bmp);
                                ArrayList<ImageItem> list = new ArrayList<>();
                                list.add(item);
                                notifyOnImagePickComplete(list);
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

    private void notifyOnImagePickComplete(ArrayList<ImageItem> list) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKERRESULT, (Serializable) list);
        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        finish();
    }

}
