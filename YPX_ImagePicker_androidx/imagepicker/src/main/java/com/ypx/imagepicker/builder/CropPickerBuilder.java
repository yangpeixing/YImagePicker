package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.os.Bundle;

import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropFragment;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.PFileUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Description: 选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class CropPickerBuilder extends PBaseBuilder {
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
     * @param columnCount 设置列数
     */
    public CropPickerBuilder setColumnCount(int columnCount) {
        selectConfig.setColumnCount(columnCount);
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
        this.selectConfig.setMaxVideoDuration(duration);
        return this;
    }

    /**
     * @param duration 设置视频可选择的最小时长
     */
    public CropPickerBuilder setMinVideoDuration(long duration) {
        this.selectConfig.setMinVideoDuration(duration);
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
     * @param showImage 加载图片
     * @deprecated replaced by <code>mimeType(MimeType.ofImage())</code>
     */
    public CropPickerBuilder showImage(boolean showImage) {
        if (!showImage) {
            Set<MimeType> mimeTypes = selectConfig.getMimeTypes();
            mimeTypes.removeAll(MimeType.ofImage());
            selectConfig.setMimeTypes(mimeTypes);
        }
        selectConfig.setShowImage(showImage);
        return this;
    }

    /**
     * @param showVideo 加载视频
     * @deprecated replaced by <code>mimeType(MimeType.ofVideo())</code>
     */
    public CropPickerBuilder showVideo(boolean showVideo) {
        if (!showVideo) {
            Set<MimeType> mimeTypes = selectConfig.getMimeTypes();
            mimeTypes.removeAll(MimeType.ofVideo());
            selectConfig.setMimeTypes(mimeTypes);
        }
        selectConfig.setShowVideo(showVideo);
        return this;
    }

    /**
     * @param showGif 加载GIF
     * @deprecated replaced by <code>filterMimeType(MimeType.GIF)</code>
     */
    public CropPickerBuilder showGif(boolean showGif) {
        if (!showGif) {
            Set<MimeType> mimeTypes = selectConfig.getMimeTypes();
            mimeTypes.remove(MimeType.GIF);
            selectConfig.setMimeTypes(mimeTypes);
        }
        selectConfig.setLoadGif(showGif);
        return this;
    }


    /**
     * 设置文件加载类型
     *
     * @param mimeTypes 文件类型数组
     */
    public CropPickerBuilder mimeType(MimeType... mimeTypes) {
        if (mimeTypes == null || mimeTypes.length == 0) {
            return this;
        }
        Set<MimeType> mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
        return mimeType(mimeTypeSet);
    }

    /**
     * 设置文件加载类型
     *
     * @param mimeTypes 文件类型集合
     */
    public CropPickerBuilder filterMimeType(Set<MimeType> mimeTypes) {
        selectConfig.getMimeTypes().removeAll(mimeTypes);
        return this;
    }

    /**
     * 设置需要过滤掉的文件加载类型
     *
     * @param mimeTypes 需要过滤的文件类型数组
     */
    public CropPickerBuilder filterMimeType(MimeType... mimeTypes) {
        if (mimeTypes == null || mimeTypes.length == 0) {
            return this;
        }
        Set<MimeType> mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
        return filterMimeType(mimeTypeSet);
    }

    /**
     * 设置需要过滤掉的文件加载类型
     *
     * @param mimeTypes 需要过滤的文件类型集合
     */
    public CropPickerBuilder mimeType(Set<MimeType> mimeTypes) {
        if (mimeTypes == null || mimeTypes.size() == 0) {
            return this;
        }
        selectConfig.setMimeTypes(mimeTypes);
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
        checkVideoAndImage();
        ImagePickAndCropActivity.intent(activity, presenter, selectConfig, listener);
    }

    /**
     * @return 获取选择器的presenter和selectConfig
     */
    private Bundle getFragmentArguments() {
        checkVideoAndImage();
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

    /**
     * 检测是否加载视频和图片
     */
    private void checkVideoAndImage() {
        selectConfig.setSinglePickImageOrVideoType(true);
        if (selectConfig == null) {
            return;
        }
        selectConfig.setShowVideo(false);
        selectConfig.setShowImage(false);
        for (MimeType mimeType : selectConfig.getMimeTypes()) {
            if (MimeType.ofVideo().contains(mimeType)) {
                selectConfig.setShowVideo(true);
            }
            if (MimeType.ofImage().contains(mimeType)) {
                selectConfig.setShowImage(true);
            }
        }
    }
}
