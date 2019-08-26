package com.ypx.imagepicker.activity.multi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PFileUtil;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.widget.CropImageView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_CURRENT_IMAGE;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_UI_CONFIG;


/**
 * 截取头像
 */
public class SingleCropActivity extends FragmentActivity {
    private CropImageView cropView;
    private PickerUiConfig uiConfig;
    private PickerSelectConfig selectConfig;

    public static void intentCrop(Activity context,
                                  IMultiPickerBindPresenter presenter,
                                  PickerSelectConfig config,
                                  String path,
                                  final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(context, SingleCropActivity.class);
        intent.putExtra(INTENT_KEY_UI_CONFIG, presenter);
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, config);
        intent.putExtra(INTENT_KEY_CURRENT_IMAGE, path);
        PLauncher.init(context).startActivityForResult(intent, new PLauncher.Callback() {
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
            selectConfig = (PickerSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
            uiConfig = presenter.getUiConfig(this);
            String imagePath = "file://" + getIntent().getStringExtra(INTENT_KEY_CURRENT_IMAGE);
            cropView = findViewById(R.id.cropView);
            setTitleBar();
            cropView.enable(); // 启用图片缩放功能
            cropView.setMaxScale(7.0f);
            cropView.setRotateEnable(false);
            cropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cropView.setCropMargin(selectConfig.getCropRectMargin());
            presenter.displayPerViewImage(cropView, imagePath);
            cropView.setCropRatio(selectConfig.getCropRatioX(), selectConfig.getCropRatioY());
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
            PStatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true,
                    PStatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));

            top_bar.setPadding(0, PStatusBarUtil.getStatusBarHeight(this), 0, 0);
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
                if (cropView.isEditing()) {
                    return;
                }
                Bitmap bitmap = cropView.generateCropBitmap();
                File f = new File(selectConfig.getCropSaveFilePath(), "crop_" + System.currentTimeMillis() + ".jpg");
                String cropUrl = PFileUtil.saveBitmapToLocalWithJPEG(bitmap, f.getAbsolutePath());
                ImageItem item = new ImageItem();
                item.path = cropUrl;
                ArrayList<ImageItem> list = new ArrayList<>();
                list.add(item);
                notifyOnImagePickComplete(list);
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
