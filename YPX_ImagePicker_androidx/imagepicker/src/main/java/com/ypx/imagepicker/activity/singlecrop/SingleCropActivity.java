package com.ypx.imagepicker.activity.singlecrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.PickerActivityManager;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.PickerActivityCallBack;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.views.base.SingleCropControllerView;
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
    private CropConfig cropConfig;
    private IPickerPresenter presenter;

    /**
     * 跳转单图剪裁
     *
     * @param context    跳转的activity
     * @param presenter  IMultiPickerBindPresenter
     * @param cropConfig 剪裁配置
     * @param path       需要剪裁的图片的原始路径
     * @param listener   剪裁回调
     */
    public static void intentCrop(Activity context,
                                  IPickerPresenter presenter,
                                  CropConfig cropConfig,
                                  String path,
                                  final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(context, SingleCropActivity.class);
        intent.putExtra(INTENT_KEY_PRESENTER, presenter);
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, cropConfig);
        intent.putExtra(INTENT_KEY_CURRENT_IMAGE, path);
        PLauncher.init(context).startActivityForResult(intent, PickerActivityCallBack.create(listener));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        presenter = (IPickerPresenter) getIntent().getSerializableExtra(INTENT_KEY_PRESENTER);
        cropConfig = (CropConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        if (presenter == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }

        if (cropConfig == null) {
            PickerErrorExecutor.executeError(this, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return;
        }
        String url = getIntent().getStringExtra(INTENT_KEY_CURRENT_IMAGE);
        if (url == null || url.trim().length() == 0) {
            PickerErrorExecutor.executeError(this, PickerError.CROP_URL_NOT_FOUND.getCode());
            return;
        }

        PickerActivityManager.addActivity(this);
        setContentView(R.layout.picker_activity_crop);
        cropView = findViewById(R.id.cropView);
        cropView.setBackgroundColor(presenter.getUiConfig(this).getCropViewBackgroundColor());
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
        ImageItem imageItem = new ImageItem();
        imageItem.path = url;
        presenter.displayImage(cropView, imageItem, 0, false);

        setControllerView();
    }

    private void setControllerView() {
        FrameLayout mCropPanel = findViewById(R.id.mCropPanel);
        SingleCropControllerView cropControllerView = presenter.getUiConfig(this)
                .getPickerUiProvider()
                .getSingleCropControllerView(this);

        mCropPanel.addView(cropControllerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        cropControllerView.setStatusBar();
        cropControllerView.setCropViewParams(cropView, (ViewGroup.MarginLayoutParams) cropView.getLayoutParams());
        cropControllerView.getCompleteView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropComplete();
            }
        });
    }

    private void cropComplete() {
        if (cropView.isEditing()) {
            return;
        }
        String cropUrl = generateCropFile("crop_" + System.currentTimeMillis());
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
        if (presenter.interceptPickerCompleteClick(SingleCropActivity.this, list, cropConfig)) {
            return;
        }
        notifyOnImagePickComplete(list);
    }

    public String generateCropFile(String fileName) {
        String cropUrl;
        Bitmap bitmap;
        if (cropConfig.isGap()) {
            bitmap = cropView.generateCropBitmapFromView(cropConfig.getCropGapBackgroundColor());
        } else {
            bitmap = cropView.generateCropBitmap();
        }

        Bitmap.CompressFormat format = cropConfig.isNeedPng() ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
        if (cropConfig.isSaveInDCIM()) {
            cropUrl = PBitmapUtils.saveBitmapToDICM(this, bitmap, fileName, format).toString();
        } else {
            cropUrl = PBitmapUtils.saveBitmapToFile(this, bitmap, fileName, format);
        }

        return cropUrl;
    }

    private void notifyOnImagePickComplete(ArrayList<ImageItem> list) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, list);
        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PickerActivityManager.removeActivity(this);
    }
}
