package com.ypx.imagepicker.activity.singlecrop;

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
import com.ypx.imagepicker.bean.CropConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PConstantsUtil;
import com.ypx.imagepicker.utils.PFileUtil;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

import java.io.File;
import java.util.ArrayList;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_CURRENT_IMAGE;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;


/**
 * Description: 图片剪裁页面
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public class SingleCropActivity extends FragmentActivity {
    private final String PNG = ".png";
    private final String JPEG = ".jpg";
    private CropImageView cropView;
    private PickerUiConfig uiConfig;
    private CropConfig cropConfig;
    private IMultiPickerBindPresenter presenter;

    /**
     * 跳转单图剪裁
     *
     * @param context    跳转的activity
     * @param presenter  IMultiPickerBindPresenter
     * @param cropConfig 剪裁配置
     * @param path       需要剪裁的图片的原始路径
     * @param listener   剪裁回调
     */
    public static void intentCrop(Activity context, IMultiPickerBindPresenter presenter, CropConfig cropConfig,
                                  String path, final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(context, SingleCropActivity.class);
        intent.putExtra(INTENT_KEY_PRESENTER, presenter);
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, cropConfig);
        intent.putExtra(INTENT_KEY_CURRENT_IMAGE, path);
        PLauncher.init(context).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKER_RESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
                    listener.onImagePickComplete(list);
                } else if (listener instanceof OnImagePickCompleteListener2) {
                    ((OnImagePickCompleteListener2) listener).onPickFailed(PickerError.valueOf(resultCode));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_PRESENTER);
        cropConfig = (CropConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        if (presenter == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }

        if (cropConfig == null) {
            PickerErrorExecutor.executeError(this, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return;
        }
        uiConfig = presenter.getUiConfig(this);
        if (uiConfig == null) {
            uiConfig = new PickerUiConfig();
        }
        String url = getIntent().getStringExtra(INTENT_KEY_CURRENT_IMAGE);
        if (url == null || url.trim().length() == 0 || !new File(url).exists()) {
            PickerErrorExecutor.executeError(this, PickerError.CROP_URL_NOT_FOUND.getCode());
            return;
        }
        String imagePath = "file://" + url;
        setContentView(R.layout.picker_activity_crop);
        setTitleBar();
        cropView = findViewById(R.id.cropView);
        cropView.setMaxScale(7.0f);
        cropView.setRotateEnable(false);
        cropView.enable();
        cropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        cropView.setBounceEnable(!cropConfig.isGap());
        cropView.setCropMargin(cropConfig.getCropRectMargin());
        if (!cropConfig.isCircle()) {
            cropView.setCropRatio(cropConfig.getCropRatioX(), cropConfig.getCropRatioY());
        } else {
            cropView.setCropRatio(1, 1);
        }
        cropView.setCircle(cropConfig.isCircle());
        presenter.displayPerViewImage(cropView, imagePath);
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
        tv_title.setText(PConstantsUtil.getString(this, presenter).picker_str_crop_title);
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
                String cropUrl = generateCropFile(cropConfig.getCropSaveFilePath(), "crop_" + System.currentTimeMillis());
                if (cropUrl.startsWith("Exception:")) {
                    PickerError.CROP_EXCEPTION.setMessage(cropUrl);
                    PickerErrorExecutor.executeError(SingleCropActivity.this, PickerError.CROP_EXCEPTION.getCode());
                    return;
                }
                ImageItem item = new ImageItem();
                item.path = cropUrl;
                if (cropUrl.endsWith(PNG)) {
                    item.mimeType = MimeType.JPEG.toString();
                } else {
                    item.mimeType = MimeType.PNG.toString();
                }

                item.width = cropView.getCropWidth();
                item.height = cropView.getCropHeight();
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

    public String generateCropFile(String filePath, String fileName) {
        File f = new File(filePath, fileName + (cropConfig.isNeedPng() ? PNG : JPEG));
        String cropUrl;
        Bitmap bitmap;
        if (cropConfig.isGap()) {
            bitmap = cropView.generateCropBitmapFromView(cropConfig.getCropGapBackgroundColor());
        } else {
            bitmap = cropView.generateCropBitmap();
        }
        if (cropConfig.isNeedPng()) {
            cropUrl = PFileUtil.saveBitmapToLocalWithPNG(bitmap, f.getAbsolutePath());
        } else {
            cropUrl = PFileUtil.saveBitmapToLocalWithJPEG(bitmap, f.getAbsolutePath());
        }
        return cropUrl;
    }

    private void notifyOnImagePickComplete(ArrayList<ImageItem> list) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, list);
        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        finish();
    }
}
