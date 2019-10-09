package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropFragment;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.PFileUtil;

import java.util.ArrayList;

/**
 * Description: 选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class CropPickerBuilder {
    private CropSelectConfig selectConfig;
    private ICropPickerBindPresenter imageLoaderProvider;

    public CropPickerBuilder(ICropPickerBindPresenter imageLoaderProvider) {
        this.imageLoaderProvider = imageLoaderProvider;
        this.selectConfig = new CropSelectConfig();
    }

    public CropPickerBuilder setFirstImageItem(ImageItem firstImageItem) {
        if (firstImageItem != null && firstImageItem.width > 0 && firstImageItem.height > 0) {
            selectConfig.setFirstImageItem(firstImageItem);
        }
        return this;
    }


    public CropPickerBuilder withSelectConfig(CropSelectConfig selectConfig) {
        this.selectConfig = selectConfig;
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
        if (firstImageUrl == null || firstImageUrl.length() == 0 || selectConfig.hasFirstImageItem()) {
            return this;
        }
        ImageItem firstImageItem = new ImageItem();
        firstImageItem.setCropUrl(firstImageUrl);
        int[] imageSize = PFileUtil.getImageWidthHeight(firstImageUrl);
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
        selectConfig.setMaxCount(maxCount);
        return this;
    }

    public CropPickerBuilder setVideoSinglePick(boolean isSinglePick) {
        selectConfig.setVideoSinglePick(isSinglePick);
        return this;
    }

    /**
     * @deprecated
     */
    public CropPickerBuilder showBottomView(boolean isShowBottomView) {
        return this;
    }

    /**
     * @deprecated
     */
    public CropPickerBuilder showDraftDialog(boolean isShowDraft) {
        return this;
    }

    public CropPickerBuilder showCamera(boolean isShowCamera) {
        selectConfig.setShowCamera(isShowCamera);
        return this;
    }

    public CropPickerBuilder showGif(boolean showGif) {
        selectConfig.setLoadGif(showGif);
        return this;
    }

    public CropPickerBuilder showVideo(boolean isShowVideo) {
        selectConfig.setShowVideo(isShowVideo);
        return this;
    }

    public CropPickerBuilder showImage(boolean isShowImage) {
        selectConfig.setShowImage(isShowImage);
        return this;
    }

    /**
     * @deprecated
     */
    public CropPickerBuilder startDirect(boolean isShowDraft) {
        return this;
    }

    public CropPickerBuilder setCropPicSaveFilePath(String cropPicSaveFilePath) {
        selectConfig.setCropSaveFilePath(cropPicSaveFilePath);
        return this;
    }

    public void pick(Activity activity, final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, ImagePickAndCropActivity.class);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_DATA_PRESENTER, imageLoaderProvider);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
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
        bundle.putSerializable(ImagePickAndCropActivity.INTENT_KEY_DATA_PRESENTER, imageLoaderProvider);
        bundle.putSerializable(ImagePickAndCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        return bundle;
    }

    public ImagePickAndCropFragment pickWithFragment(OnImagePickCompleteListener imageListener) {
        ImagePickAndCropFragment mFragment = new ImagePickAndCropFragment();
        mFragment.setArguments(getFragmentArguments());
        mFragment.setOnImagePickCompleteListener(imageListener);
        return mFragment;
    }
}
