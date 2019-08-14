package com.ypx.imagepicker.activity.multi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.oginotihiro.cropview.CropView;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.ActivityLauncher;
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
    private PickerUiConfig uiConfig;

    public static void intentCrop(Activity context,
                                  IMultiPickerBindPresenter presenter,
                                  PickerSelectConfig config,
                                  String path,
                                  final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(context, SingleCropActivity.class);
        intent.putExtra(INTENT_KEY_UI_CONFIG, presenter);
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, config);
        intent.putExtra(INTENT_KEY_CURRENT_IMAGE, path);
        ActivityLauncher.init(context).startActivityForResult(intent, new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKERRESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKERRESULT);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_crop);
        if (getIntent() != null && getIntent().hasExtra(INTENT_KEY_UI_CONFIG)) {
            IMultiPickerBindPresenter presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_UI_CONFIG);
            PickerSelectConfig selectConfig = (PickerSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
            uiConfig = presenter.getUiConfig(this);
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
        if (uiConfig.isImmersionBar()) {
            StatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true,
                    StatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));

            top_bar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        }
        iv_back.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
        top_bar.setBackgroundColor(uiConfig.getTitleBarBackgroundColor());
        iv_back.setColorFilter(uiConfig.getBackIconColor());
        tv_title.setTextColor(uiConfig.getTitleColor());
        ((LinearLayout) findViewById(R.id.mTitleRoot)).setGravity(uiConfig.getTitleBarGravity());
        if (uiConfig.getOkBtnSelectBackground() == null) {
            tv_rightBtn.setPadding(0, 0, 0, 0);
        }
        tv_rightBtn.setBackground(uiConfig.getOkBtnSelectBackground());
        tv_rightBtn.setTextColor(uiConfig.getOkBtnSelectTextColor());

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
