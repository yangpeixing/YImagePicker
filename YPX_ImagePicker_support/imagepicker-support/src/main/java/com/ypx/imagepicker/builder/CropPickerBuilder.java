package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropFragment;
import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
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
    private ICropPickerBindPresenter presenter;

    public CropPickerBuilder(ICropPickerBindPresenter presenter) {
        this.presenter = presenter;
        this.selectConfig = new CropSelectConfig();
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
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FULL);
        } else {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FIT);
        }
        return this;
    }


    /**
     * @param firstImageItem 设置之前选择的第一个item,用于指定默认剪裁模式,如果当前item是图片，
     *                       则强制所有图片剪裁模式为当前图片比例，如果当前item是视频，
     *                       则强制只能选择视频
     */
    public CropPickerBuilder setFirstImageItem(ImageItem firstImageItem) {
        if (firstImageItem != null) {
            if (firstImageItem.isVideo() || (firstImageItem.width > 0 && firstImageItem.height > 0)) {
                selectConfig.setFirstImageItem(firstImageItem);
            }
        }
        return this;
    }


    /**
     * @param selectConfig 选择配置项
     */
    public CropPickerBuilder withSelectConfig(CropSelectConfig selectConfig) {
        this.selectConfig = selectConfig;
        return this;
    }

    /**
     * @param duration 设置视频可选择的最大时长
     */
    public CropPickerBuilder setMaxVideoDuration(long duration) {
        ImagePicker.MAX_VIDEO_DURATION = duration;
        return this;
    }

    /**
     * @param maxCount 选中数量限制
     */
    public CropPickerBuilder setMaxCount(int maxCount) {
        selectConfig.setMaxCount(maxCount);
        return this;
    }

    /**
     * @param isSinglePick 是否单选视频，如果设置为true，则点击item会走presenter的clickVideo方法，
     *                     设置为false,则触发视频多选和预览模式
     */
    public CropPickerBuilder setVideoSinglePick(boolean isSinglePick) {
        selectConfig.setVideoSinglePick(isSinglePick);
        return this;
    }

    /**
     * @param isShowCamera 是否显示拍照item
     */
    public CropPickerBuilder showCamera(boolean isShowCamera) {
        selectConfig.setShowCamera(isShowCamera);
        return this;
    }

    /**
     * @param showGif 是否显示GIF
     */
    public CropPickerBuilder showGif(boolean showGif) {
        selectConfig.setLoadGif(showGif);
        return this;
    }

    /**
     * @param isShowVideo 是否加载视频
     */
    public CropPickerBuilder showVideo(boolean isShowVideo) {
        selectConfig.setShowVideo(isShowVideo);
        return this;
    }

    /**
     * @param isShowImage 是否加载图片
     */
    public CropPickerBuilder showImage(boolean isShowImage) {
        selectConfig.setShowImage(isShowImage);
        return this;
    }

    /**
     * @param cropPicSaveFilePath 剪裁图片的默认保存路径
     */
    public CropPickerBuilder setCropPicSaveFilePath(String cropPicSaveFilePath) {
        selectConfig.setCropSaveFilePath(cropPicSaveFilePath);
        return this;
    }

    /**
     * 页面直接调用剪裁器
     *
     * @param activity 调用者
     * @param listener 图片视频选择回调
     */
    public void pick(Activity activity, final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, ImagePickAndCropActivity.class);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_DATA_PRESENTER, presenter);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (data != null && data.hasExtra(ImagePicker.INTENT_KEY_PICKER_RESULT)
                        && resultCode == ImagePicker.REQ_PICKER_RESULT_CODE && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }

    private Bundle getFragmentArguments() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePickAndCropActivity.INTENT_KEY_DATA_PRESENTER, presenter);
        bundle.putSerializable(ImagePickAndCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        return bundle;
    }

    /**
     * fragment构建
     *
     * @param imageListener 图片视频选择回调
     */
    public ImagePickAndCropFragment pickWithFragment(OnImagePickCompleteListener imageListener) {
        ImagePickAndCropFragment mFragment = new ImagePickAndCropFragment();
        mFragment.setArguments(getFragmentArguments());
        mFragment.setOnImagePickCompleteListener(imageListener);
        return mFragment;
    }
}
