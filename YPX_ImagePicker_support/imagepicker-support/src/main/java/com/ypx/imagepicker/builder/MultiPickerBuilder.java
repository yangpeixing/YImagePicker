package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.os.Bundle;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.activity.multi.MultiImagePickerFragment;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;

/**
 * Description: 多选选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2018/9/19 16:56
 */
public class MultiPickerBuilder {
    private MultiSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;

    public MultiPickerBuilder(IMultiPickerBindPresenter presenter) {
        this.presenter = presenter;
        this.selectConfig = new MultiSelectConfig();
    }

    /**
     * @param config 选择配置
     */
    public MultiPickerBuilder withMultiSelectConfig(MultiSelectConfig config) {
        this.selectConfig = config;
        return this;
    }

    /**
     * 直接开启相册选择
     *
     * @param context  页面调用者
     * @param listener 选择器选择回调
     */
    public void pick(Activity context, final OnImagePickCompleteListener listener) {
        MultiPickerData.instance.clear();
        if (selectConfig.getMaxCount() == 1) {
            selectConfig.setSelectMode(SelectMode.MODE_SINGLE);
        }
        checkVideoAndImage();
        MultiImagePickerActivity.intent(context, selectConfig, presenter, listener);
    }

    /**
     * 调用剪裁
     *
     * @param context  页面调用者
     * @param listener 选择器剪裁回调，只支持一张图片
     */
    public void crop(Activity context, OnImagePickCompleteListener listener) {
        setMaxCount(1);
        filterMimeType(MimeType.ofVideo());
        setSinglePickImageOrVideoType(false);
        setVideoSinglePick(false);
        setShieldList(null);
        setLastImageList(null);
        setPreview(false);
        MultiPickerData.instance.clear();
        selectConfig.setSelectMode(SelectMode.MODE_CROP);
        if (selectConfig.isCircle()) {
            selectConfig.setCropRatio(1, 1);
        }
        MultiImagePickerActivity.intent(context, selectConfig, presenter, listener);
    }

    /**
     * @param selectLimit 设置最大数量限制
     */
    public MultiPickerBuilder setMaxCount(int selectLimit) {
        selectConfig.setMaxCount(selectLimit);
        return this;
    }

    /**
     * @param selectMode 设置选择模式
     *                   {@link SelectMode}
     */
    public MultiPickerBuilder setSelectMode(int selectMode) {
        selectConfig.setSelectMode(selectMode);
        return this;
    }

    /**
     * @param duration 设置视频可选择的最大时长
     */
    public MultiPickerBuilder setMaxVideoDuration(long duration) {
        ImagePicker.MAX_VIDEO_DURATION = duration;
        return this;
    }

    /**
     * 设置剪裁最小间距，默认充满
     *
     * @param margin 间距
     */
    public MultiPickerBuilder cropRectMinMargin(int margin) {
        selectConfig.setCropRectMargin(margin);
        return this;
    }

    public MultiPickerBuilder cropStyle(int style) {
        selectConfig.setCropStyle(style);
        return this;
    }

    public MultiPickerBuilder cropGapBackgroundColor(int color) {
        selectConfig.setCropGapBackgroundColor(color);
        return this;
    }

    /**
     * 设置剪裁图片保存路径
     *
     * @param path 路径+图片名字
     */
    public MultiPickerBuilder cropSaveFilePath(String path) {
        selectConfig.setCropSaveFilePath(path);
        return this;
    }

    /**
     * 设置文件加载类型
     *
     * @param mimeTypes 文件类型数组
     */
    public MultiPickerBuilder mimeType(MimeType... mimeTypes) {
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
    public MultiPickerBuilder filterMimeType(Set<MimeType> mimeTypes) {
        selectConfig.getMimeTypes().removeAll(mimeTypes);
        return this;
    }

    /**
     * 设置需要过滤掉的文件加载类型
     *
     * @param mimeTypes 需要过滤的文件类型数组
     */
    public MultiPickerBuilder filterMimeType(MimeType... mimeTypes) {
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
    public MultiPickerBuilder mimeType(Set<MimeType> mimeTypes) {
        if (mimeTypes == null || mimeTypes.size() == 0) {
            return this;
        }
        selectConfig.setMimeTypes(mimeTypes);
        return this;
    }

    /**
     * @param showImage 加载图片
     * @deprecated replaced by <code>mimeType(MimeType.ofImage())</code>
     */
    public MultiPickerBuilder showImage(boolean showImage) {
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
    public MultiPickerBuilder showVideo(boolean showVideo) {
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
    public MultiPickerBuilder showGif(boolean showGif) {
        if (!showGif) {
            Set<MimeType> mimeTypes = selectConfig.getMimeTypes();
            mimeTypes.remove(MimeType.GIF);
            selectConfig.setMimeTypes(mimeTypes);
        }
        selectConfig.setLoadGif(showGif);
        return this;
    }

    /**
     * @param columnCount 设置列数
     */
    public MultiPickerBuilder setColumnCount(int columnCount) {
        selectConfig.setColumnCount(columnCount);
        return this;
    }

    /**
     * 设置圆形剪裁
     */
    public MultiPickerBuilder cropAsCircle() {
        selectConfig.setCircle(true);
        return this;
    }

    /**
     * @param isPreview 是否开启预览
     */
    public MultiPickerBuilder setPreview(boolean isPreview) {
        selectConfig.setPreview(isPreview);
        return this;
    }


    /**
     * @param showCamera 显示拍照item
     */
    public MultiPickerBuilder showCamera(boolean showCamera) {
        selectConfig.setShowCamera(showCamera);
        return this;
    }

    /**
     * @param isSinglePickImageOrVideoType 是否只能选择视频或图片
     */
    public MultiPickerBuilder setSinglePickImageOrVideoType(boolean isSinglePickImageOrVideoType) {
        selectConfig.setSinglePickImageOrVideoType(isSinglePickImageOrVideoType);
        return this;
    }


    /**
     * @param isVideoSinglePick 视频是否单选
     */
    public MultiPickerBuilder setVideoSinglePick(boolean isVideoSinglePick) {
        selectConfig.setVideoSinglePick(isVideoSinglePick);
        return this;
    }


    /**
     * @param imageList 设置屏蔽项，默认打开选择器不可选择屏蔽列表的媒体文件
     * @param <T>       String or ImageItem
     */
    public <T> MultiPickerBuilder setShieldList(ArrayList<T> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return this;
        }
        selectConfig.setShieldImageList(transitArray(imageList));
        return this;
    }

    /**
     * @param imageList 设置上一次选择的媒体文件，默认还原上一次选择，可取消
     * @param <T>       String or ImageItem
     */
    public <T> MultiPickerBuilder setLastImageList(ArrayList<T> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return this;
        }
        selectConfig.setLastImageList(transitArray(imageList));
        return this;
    }

    /**
     * 设置单张图片剪裁比例
     *
     * @param x 剪裁比例x
     * @param y 剪裁比例y
     */
    public MultiPickerBuilder setCropRatio(int x, int y) {
        selectConfig.setCropRatio(x, y);
        return this;
    }

    /**
     * 数据类型转化
     */
    private <T> ArrayList<ImageItem> transitArray(ArrayList<T> imageList) {
        ArrayList<ImageItem> items = new ArrayList<>();
        for (T t : imageList) {
            if (t instanceof String) {
                ImageItem imageItem = new ImageItem();
                imageItem.path = (String) t;
                items.add(imageItem);
            } else if (t instanceof ImageItem) {
                items.add((ImageItem) t);
            } else {
                throw new RuntimeException("ImageList item must be instanceof String or ImageItem");
            }
        }
        return items;
    }

    private Bundle getFragmentArguments() {
        checkVideoAndImage();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SELECT_CONFIG, selectConfig);
        bundle.putSerializable(INTENT_KEY_PRESENTER, presenter);
        return bundle;
    }

    /**
     * fragment模式调用
     *
     * @param completeListener 选择回调
     * @return MultiImagePickerFragment
     */
    public MultiImagePickerFragment pickWithFragment(OnImagePickCompleteListener completeListener) {
        MultiImagePickerFragment mFragment = new MultiImagePickerFragment();
        mFragment.setArguments(getFragmentArguments());
        mFragment.setOnImagePickCompleteListener(completeListener);
        return mFragment;
    }

    private void checkVideoAndImage() {
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
