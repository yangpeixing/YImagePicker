package com.ypx.imagepicker.activity.singlecrop;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.PickerActivityManager;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.selectconfig.CropConfigParcelable;
import com.ypx.imagepicker.data.ProgressSceneEnum;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.PickerActivityCallBack;
import com.ypx.imagepicker.helper.DetailImageLoadHelper;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.views.base.SingleCropControllerView;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

import java.util.ArrayList;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;


/**
 * Description: 图片剪裁页面
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x
 */
public class SingleCropActivity extends FragmentActivity {
    public static final String INTENT_KEY_CURRENT_IMAGE_ITEM = "currentImageItem";
    private CropImageView cropView;
    private CropConfigParcelable cropConfig;
    private IPickerPresenter presenter;
    private ImageItem currentImageItem;

    /**
     * 跳转单图剪裁
     *
     * @param activity   跳转的activity
     * @param presenter  IPickerPresenter
     * @param cropConfig 剪裁配置
     * @param path       需要剪裁的图片的原始路径，可以为Uri相对路径
     * @param listener   剪裁回调
     */
    public static void intentCrop(Activity activity,
                                  IPickerPresenter presenter,
                                  CropConfig cropConfig,
                                  String path,
                                  final OnImagePickCompleteListener listener) {
        intentCrop(activity, presenter, cropConfig, ImageItem.withPath(activity, path), listener);
    }

    public static void intentCrop(Activity activity,
                                  IPickerPresenter presenter,
                                  CropConfig cropConfig,
                                  ImageItem item,
                                  final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, SingleCropActivity.class);
        intent.putExtra(INTENT_KEY_PRESENTER, presenter);
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, cropConfig.getCropInfo());
        intent.putExtra(INTENT_KEY_CURRENT_IMAGE_ITEM, (Parcelable) item);
        PLauncher.init(activity).startActivityForResult(intent, PickerActivityCallBack.create(listener));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        presenter = (IPickerPresenter) getIntent().getSerializableExtra(INTENT_KEY_PRESENTER);
        cropConfig = getIntent().getParcelableExtra(INTENT_KEY_SELECT_CONFIG);
        if (presenter == null) {
            PickerErrorExecutor.executeError(this, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        if (cropConfig == null) {
            PickerErrorExecutor.executeError(this, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return;
        }
        currentImageItem = getIntent().getParcelableExtra(INTENT_KEY_CURRENT_IMAGE_ITEM);
        if (currentImageItem == null || currentImageItem.isEmpty()) {
            PickerErrorExecutor.executeError(this, PickerError.CROP_URL_NOT_FOUND.getCode());
            return;
        }

        PickerActivityManager.addActivity(this);
        setContentView(cropConfig.isSingleCropCutNeedTop() ?
                R.layout.picker_activity_crop_cover : R.layout.picker_activity_crop);

        //初始化剪裁view
        cropView = findViewById(R.id.cropView);
        cropView.setMaxScale(7.0f);
        cropView.setRotateEnable(true);
        cropView.enable();
        cropView.setBounceEnable(!cropConfig.isGap());
        cropView.setCropMargin(cropConfig.getCropRectMargin());
        cropView.setCircle(cropConfig.isCircle());
        cropView.setCropRatio(cropConfig.getCropRatioX(), cropConfig.getCropRatioY());

        //恢复上一次剪裁属性
        if (cropConfig.getCropRestoreInfo() != null) {
            cropView.setRestoreInfo(cropConfig.getCropRestoreInfo());
        }

        //加载图片
        DetailImageLoadHelper.displayDetailImage(true, cropView, presenter, currentImageItem);
        setControllerView();
    }

    /**
     * 设置剪裁控制器View
     */
    private void setControllerView() {
        FrameLayout mCropPanel = findViewById(R.id.mCropPanel);
        PickerUiConfig uiConfig = presenter.getUiConfig(this);
        findViewById(R.id.mRoot).setBackgroundColor(uiConfig.getSingleCropBackgroundColor());
        SingleCropControllerView cropControllerView = uiConfig.getPickerUiProvider()
                .getSingleCropControllerView(this);
        mCropPanel.addView(cropControllerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        cropControllerView.setStatusBar();
        cropControllerView.setCropViewParams(cropView, (ViewGroup.MarginLayoutParams) cropView.getLayoutParams());
        cropControllerView.getCompleteView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PViewSizeUtils.onDoubleClick()) {
                    return;
                }
                generateCropFile("crop_" + System.currentTimeMillis());
            }
        });
    }


    /**
     * 剪裁完成
     *
     * @param cropUrl 剪裁生成的绝对路径
     */
    private void cropComplete(String cropUrl) {
        //如果正在编辑中...
        if (cropView.isEditing()) {
            return;
        }
        //剪裁异常
        if (cropUrl == null || cropUrl.length() == 0 || cropUrl.startsWith("Exception:")) {
            presenter.tip(this, getString(R.string.picker_str_tip_singleCrop_error));
            cropView.setCropRatio(cropConfig.getCropRatioX(), cropConfig.getCropRatioY());
            return;
        }
        //回调剪裁数据
        // currentImageItem.path = cropUrl;
        currentImageItem.mimeType = cropConfig.isNeedPng() ? MimeType.PNG.toString() : MimeType.JPEG.toString();
        currentImageItem.width = cropView.getCropWidth();
        currentImageItem.height = cropView.getCropHeight();
        currentImageItem.setCropUrl(cropUrl);
        currentImageItem.setCropRestoreInfo(cropView.getInfo());
        notifyOnImagePickComplete(currentImageItem);
    }


    private DialogInterface dialogInterface;

    /**
     * 生成剪裁文件
     *
     * @param fileName 图片名称
     */
    public void generateCropFile(final String fileName) {
        dialogInterface = presenter.showProgressDialog(this, ProgressSceneEnum.crop);
        if (cropConfig.isGap() && !cropConfig.isCircle()) {
            cropView.setBackgroundColor(cropConfig.getCropGapBackgroundColor());
        }
        currentImageItem.displayName = fileName;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap;
                if (cropConfig.isGap()) {
                    bitmap = cropView.generateCropBitmapFromView(cropConfig.getCropGapBackgroundColor());
                } else {
                    bitmap = cropView.generateCropBitmap();
                }
                final String url = saveBitmapToFile(bitmap, fileName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                        cropComplete(url);
                    }
                });
            }
        }).start();
    }

    /**
     * 保存bitmap到本地磁盘
     *
     * @param bitmap   图片bitmap
     * @param fileName 图片名字
     */
    private String saveBitmapToFile(final Bitmap bitmap, final String fileName) {
        final String cropUrl;
        Bitmap.CompressFormat format = cropConfig.isNeedPng() ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
        if (cropConfig.isSaveInDCIM()) {
            cropUrl = PBitmapUtils.saveBitmapToDCIM(SingleCropActivity.this, bitmap, fileName, format).toString();
        } else {
            cropUrl = PBitmapUtils.saveBitmapToFile(SingleCropActivity.this, bitmap, fileName, format);
        }
        return cropUrl;
    }


    /**
     * 回调当前剪裁图片信息
     *
     * @param imageItem 剪裁图片信息
     */
    private void notifyOnImagePickComplete(ImageItem imageItem) {
        ArrayList<ImageItem> list = new ArrayList<>();
        list.add(imageItem);
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, list);
        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        if (dialogInterface != null) {
            dialogInterface.dismiss();
        }
        PickerActivityManager.removeActivity(this);
    }
}
