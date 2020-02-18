package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.os.Bundle;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.selectconfig.CropSelectConfig;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.activity.crop.MultiImageCropActivity;
import com.ypx.imagepicker.activity.crop.MultiImageCropFragment;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Description: 小红书剪裁选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class CropPickerBuilder {
    private CropSelectConfig selectConfig;
    private IPickerPresenter presenter;

    public CropPickerBuilder(IPickerPresenter presenter) {
        this.presenter = presenter;
        this.selectConfig = new CropSelectConfig();
    }

    /**
     * @param columnCount 设置列数
     */
    public CropPickerBuilder setColumnCount(int columnCount) {
        selectConfig.setColumnCount(columnCount);
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
     * 设置需要加载的文件类型
     *
     * @param mimeTypes 需要加载的文件类型集合
     */
    public CropPickerBuilder mimeTypes(Set<MimeType> mimeTypes) {
        if (mimeTypes == null || mimeTypes.size() == 0) {
            return this;
        }
        selectConfig.setMimeTypes(mimeTypes);
        return this;
    }

    /**
     * 设置文件加载类型
     *
     * @param mimeTypes 文件类型数组
     */
    public CropPickerBuilder mimeTypes(MimeType... mimeTypes) {
        if (mimeTypes == null || mimeTypes.length == 0) {
            return this;
        }
        Set<MimeType> mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
        return mimeTypes(mimeTypeSet);
    }

    /**
     * 设置需要过滤掉的文件类型
     *
     * @param mimeTypes 需要过滤的文件类型数组
     */
    public CropPickerBuilder filterMimeTypes(MimeType... mimeTypes) {
        if (mimeTypes == null || mimeTypes.length == 0) {
            return this;
        }
        Set<MimeType> mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
        return filterMimeTypes(mimeTypeSet);
    }

    /**
     * 设置需要过滤掉的文件类型
     *
     * @param mimeTypes 文件类型集合
     */
    public CropPickerBuilder filterMimeTypes(Set<MimeType> mimeTypes) {
        selectConfig.getMimeTypes().removeAll(mimeTypes);
        return this;
    }

    /**
     * @param isAutoComplete 设置单选模式下是否点击item就自动回调
     */
    public CropPickerBuilder setSinglePickWithAutoComplete(boolean isAutoComplete) {
        selectConfig.setSinglePickAutoComplete(isAutoComplete);
        return this;
    }

    //--------------- 以下是小红书剪裁特有属性 -------------------------------------

    /**
     * 在没有指定setFirstImageItem时，使用这个方法传入当前的第一张图片的宽高信息,
     * 会生成一个新的FirstImageItem，其剪裁模式根据图片宽高决定，如果已经指定了FirstImageItem，则该方法无效
     *
     * @param width  第一张图片的宽
     * @param height 第一张图片的高
     */
    public CropPickerBuilder setFirstImageItemSize(int width, int height) {
        if (width == 0 || height == 0 || selectConfig.hasFirstImageItem()) {
            return this;
        }
        ImageItem firstImageItem = new ImageItem();
        firstImageItem.setVideo(false);
        firstImageItem.width = width;
        firstImageItem.height = height;
        if (Math.abs(width - height) < 5) {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FULL);
        } else {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FIT);
        }
        return setFirstImageItem(firstImageItem);
    }

    /**
     * 强制指定留白模式，即一打开只有留白模式
     *
     * @param isAssignGap 指定留白
     */
    public CropPickerBuilder assignGapState(boolean isAssignGap) {
        selectConfig.setAssignGapState(isAssignGap);
        if (isAssignGap) {
            setFirstImageItemSize(1, 1);
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
            if (firstImageItem.isVideo() || selectConfig.hasFirstImageItem()) {
                return this;
            }
            if ((firstImageItem.width > 0 && firstImageItem.height > 0)) {
                selectConfig.setFirstImageItem(firstImageItem);
            }
        }
        return this;
    }
    //--------------- 以上是小红书剪裁特有属性 -------------------------------------


    /**
     * @param selectConfig 选择配置项
     */
    public CropPickerBuilder withSelectConfig(CropSelectConfig selectConfig) {
        this.selectConfig = selectConfig;
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
        if (selectConfig.getMimeTypes() == null || selectConfig.getMimeTypes().size() == 0) {
            PickerErrorExecutor.executeError(listener, PickerError.MIMETYPES_EMPTY.getCode());
            presenter.tip(activity, activity.getString(R.string.picker_str_tip_mimeTypes_empty));
            return;
        }
        MultiImageCropActivity.intent(activity, presenter, selectConfig, listener);
    }


    /**
     * fragment构建
     *
     * @param imageListener 图片视频选择回调
     */
    public MultiImageCropFragment pickWithFragment(OnImagePickCompleteListener imageListener) {
        checkVideoAndImage();
        MultiImageCropFragment mFragment = new MultiImageCropFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MultiImageCropActivity.INTENT_KEY_DATA_PRESENTER, presenter);
        bundle.putSerializable(MultiImageCropActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        mFragment.setArguments(bundle);
        mFragment.setOnImagePickCompleteListener(imageListener);
        return mFragment;
    }

    /**
     * 检测文件加载类型中是否全是图片或视频
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
