package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropFragment;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.ActivityLauncher;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.FileUtil;

import java.util.ArrayList;

/**
 * Description: 选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class CropPickerBuilder {
    private int maxCount = 9;
    private ImageItem firstImageItem = null;
    private boolean isShowBottomView = false;
    private boolean isShowDraft = false;
    private boolean isShowCamera = false;
    private boolean isShowVideo = false;
    private boolean isStartDirect;
    private ICropPickerBindPresenter imageLoaderProvider;
    private OnImagePickCompleteListener imageListener;

    public CropPickerBuilder(ICropPickerBindPresenter imageLoaderProvider) {
        this.imageLoaderProvider = imageLoaderProvider;
    }

    public CropPickerBuilder setFirstImageItem(ImageItem firstImageItem) {
        if (firstImageItem != null && firstImageItem.width > 0 && firstImageItem.height > 0) {
            this.firstImageItem = firstImageItem;
        }
        return this;
    }

    public CropPickerBuilder setImageListener(OnImagePickCompleteListener imageListener) {
        this.imageListener = imageListener;
        return this;
    }

    public CropPickerBuilder setMaxVideoDuration(long duration) {
        ImagePicker.MAX_VIDEO_DURATION = duration;
        return this;
    }


    /**
     * 在没有指定setFirstImageItem时，使用这个方法传入当前的第一张剪裁图片url,
     * 会生成一个新的FirstImageItem，其剪裁模式根据图片宽高决定，如果已经指定了FirstImageItem，则该方法无效
     *
     * @param firstImageUrl 第一张建材后的图片
     */
    public CropPickerBuilder setFirstImageUrl(String firstImageUrl) {
        if (firstImageUrl == null || firstImageUrl.length() == 0 || firstImageItem != null) {
            return this;
        }
        this.firstImageItem = new ImageItem();
        firstImageItem.setCropUrl(firstImageUrl);
        int[] imageSize = FileUtil.getImageWidthHeight(firstImageUrl);
        firstImageItem.width = imageSize[0];
        firstImageItem.height = imageSize[1];

        if (firstImageItem.getWidthHeightType() == 0) {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FILL);
        } else {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FIT);
        }
        return this;
    }

    public CropPickerBuilder setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public CropPickerBuilder showBottomView(boolean isShowBottomView) {
        this.isShowBottomView = isShowBottomView;
        return this;
    }

    public CropPickerBuilder showDraftDialog(boolean isShowDraft) {
        this.isShowDraft = isShowDraft;
        return this;
    }

    public CropPickerBuilder showCamera(boolean isShowCamera) {
        this.isShowCamera = isShowCamera;
        return this;
    }

    public CropPickerBuilder showVideo(boolean isShowVideo) {
        this.isShowVideo = isShowVideo;
        return this;
    }

    public CropPickerBuilder startDirect(boolean isShowDraft) {
        this.isShowDraft = isShowDraft;
        return this;
    }

    public CropPickerBuilder setCropPicSaveFilePath(String cropPicSaveFilePath) {
        ImagePicker.cropPicSaveFilePath = cropPicSaveFilePath;
        return this;
    }

    private Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, ImagePickAndCropActivity.class);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_IMAGELOADER, imageLoaderProvider);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_MAXSELECTEDCOUNT, maxCount);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_FIRSTIMAGEITEM, firstImageItem);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_CROPPICSAVEFILEPATH, ImagePicker.cropPicSaveFilePath);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SHOWBOTTOMVIEW, isShowBottomView);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SHOWDRAFTDIALOG, isShowDraft);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SHOWCAMERA, isShowCamera);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SHOWVIDEO, isShowVideo);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_STARTDIRECT, isStartDirect);
        return intent;

    }

    public void pick(Activity activity, final OnImagePickCompleteListener listener) {
        ActivityLauncher.init(activity).startActivityForResult(getIntent(activity), new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (data != null && data.hasExtra(ImagePicker.INTENT_KEY_PICKERRESULT)
                        && resultCode == ImagePicker.REQ_PICKER_RESULT_CODE && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKERRESULT);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }


    private Bundle getFragmentArguments() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePickAndCropActivity.INTENT_KEY_IMAGELOADER, imageLoaderProvider);
        bundle.putInt(ImagePickAndCropActivity.INTENT_KEY_MAXSELECTEDCOUNT, maxCount);
        bundle.putSerializable(ImagePickAndCropActivity.INTENT_KEY_FIRSTIMAGEITEM, firstImageItem);
        bundle.putString(ImagePickAndCropActivity.INTENT_KEY_CROPPICSAVEFILEPATH, ImagePicker.cropPicSaveFilePath);
        bundle.putBoolean(ImagePickAndCropActivity.INTENT_KEY_SHOWBOTTOMVIEW, isShowBottomView);
        bundle.putBoolean(ImagePickAndCropActivity.INTENT_KEY_SHOWDRAFTDIALOG, isShowDraft);
        bundle.putBoolean(ImagePickAndCropActivity.INTENT_KEY_SHOWCAMERA, isShowCamera);
        bundle.putBoolean(ImagePickAndCropActivity.INTENT_KEY_SHOWVIDEO, isShowVideo);
        bundle.putBoolean(ImagePickAndCropActivity.INTENT_KEY_STARTDIRECT, isStartDirect);
        return bundle;
    }

    public ImagePickAndCropFragment pickWithFragment() {
        ImagePickAndCropFragment mFragment = new ImagePickAndCropFragment();
        mFragment.setArguments(getFragmentArguments());
        mFragment.setImageListener(imageListener);
        return mFragment;
    }
}
