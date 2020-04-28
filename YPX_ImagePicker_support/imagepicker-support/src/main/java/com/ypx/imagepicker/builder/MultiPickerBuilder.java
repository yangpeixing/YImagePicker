package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.os.Bundle;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.activity.multi.MultiImagePickerFragment;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.bean.selectconfig.MultiSelectConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.presenter.IPickerPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;

/**
 * Description: 多选选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2018/9/19 16:56
 */
public class MultiPickerBuilder {
    private MultiSelectConfig selectConfig;
    private IPickerPresenter presenter;

    public MultiPickerBuilder(IPickerPresenter presenter) {
        this.presenter = presenter;
        this.selectConfig = new MultiSelectConfig();
    }

    /**
     * @param isAutoComplete 设置单选模式下是否点击item就自动回调
     */
    public MultiPickerBuilder setSinglePickWithAutoComplete(boolean isAutoComplete) {
        selectConfig.setSinglePickAutoComplete(isAutoComplete);
        return this;
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
        this.selectConfig.setMaxVideoDuration(duration);
        return this;
    }

    /**
     * @param duration 设置视频可选择的最小时长
     */
    public MultiPickerBuilder setMinVideoDuration(long duration) {
        this.selectConfig.setMinVideoDuration(duration);
        return this;
    }

    /**
     * 设置文件加载类型
     *
     * @param mimeTypes 文件类型数组
     */
    public MultiPickerBuilder mimeTypes(MimeType... mimeTypes) {
        if (mimeTypes == null || mimeTypes.length == 0) {
            return this;
        }
        Set<MimeType> mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
        return mimeTypes(mimeTypeSet);
    }

    /**
     * 设置文件加载类型
     *
     * @param mimeTypes 文件类型集合
     */
    public MultiPickerBuilder filterMimeTypes(Set<MimeType> mimeTypes) {
        if (mimeTypes != null && selectConfig != null && selectConfig.getMimeTypes() != null) {
            selectConfig.getMimeTypes().removeAll(mimeTypes);
        }
        return this;
    }

    /**
     * 设置需要过滤掉的文件加载类型
     *
     * @param mimeTypes 需要过滤的文件类型数组
     */
    public MultiPickerBuilder filterMimeTypes(MimeType... mimeTypes) {
        if (mimeTypes == null || mimeTypes.length == 0) {
            return this;
        }
        Set<MimeType> mimeTypeSet = new HashSet<>(Arrays.asList(mimeTypes));
        return filterMimeTypes(mimeTypeSet);
    }

    /**
     * 设置需要加载的文件类型
     *
     * @param mimeTypes 需要过滤的文件类型集合
     */
    public MultiPickerBuilder mimeTypes(Set<MimeType> mimeTypes) {
        if (mimeTypes == null || mimeTypes.size() == 0) {
            return this;
        }
        selectConfig.setMimeTypes(mimeTypes);
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
     * @param showCamera 显示拍照item
     */
    public MultiPickerBuilder showCamera(boolean showCamera) {
        selectConfig.setShowCamera(showCamera);
        return this;
    }

    /**
     * 只在全部媒体相册里展示拍照
     */
    public MultiPickerBuilder showCameraOnlyInAllMediaSet(boolean showCamera) {
        selectConfig.setShowCameraInAllMedia(showCamera);
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


    //—————————————————————— 以下为微信选择器特有的属性 ——————————————————————

    /**
     * @param isPreview 视频是否支持预览
     */
    public MultiPickerBuilder setPreviewVideo(boolean isPreview) {
        selectConfig.setCanPreviewVideo(isPreview);
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
     * @param isOriginal 设置是否支持原图选项
     */
    public MultiPickerBuilder setOriginal(boolean isOriginal) {
        selectConfig.setShowOriginalCheckBox(isOriginal);
        return this;
    }

    /**
     * @param isOriginal 设置原图选项默认值，true则代表默认打开原图，false代表不打开
     */
    public MultiPickerBuilder setDefaultOriginal(boolean isOriginal) {
        selectConfig.setDefaultOriginal(isOriginal);
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


    //—————————————————————— 以下为单图剪裁的属性 ——————————————————————

    /**
     * 设置剪裁最小间距，默认充满
     *
     * @param margin 间距
     */
    public MultiPickerBuilder cropRectMinMargin(int margin) {
        selectConfig.setCropRectMargin(margin);
        return this;
    }

    /**
     * 设置剪裁模式，
     * <p>
     * MultiSelectConfig.STYLE_FILL:充满模式
     * MultiSelectConfig.STYLE_GAP：留白模式
     *
     * @param style MultiSelectConfig.STYLE_FILL or  MultiSelectConfig.STYLE_GAP
     */
    public MultiPickerBuilder cropStyle(int style) {
        selectConfig.setCropStyle(style);
        return this;
    }

    /**
     * 设置留白剪裁模式下背景色，如果设置成透明色，则默认生成png图片
     *
     * @param color 背景色
     */
    public MultiPickerBuilder cropGapBackgroundColor(int color) {
        selectConfig.setCropGapBackgroundColor(color);
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
     * 开启圆形剪裁
     */
    public MultiPickerBuilder cropAsCircle() {
        selectConfig.setCircle(true);
        return this;
    }

    /**
     * 剪裁完成的图片是否保存在DCIM目录下
     *
     * @param isSaveInDCIM true：存储在系统目录DCIM下 false：存储在 data/包名/files/imagePicker/ 目录下
     */
    public MultiPickerBuilder cropSaveInDCIM(boolean isSaveInDCIM) {
        selectConfig.saveInDCIM(isSaveInDCIM);
        return this;
    }

    /**
     * 单图剪裁页面，剪裁框是否在最上层
     *
     * @param singleCropCutNeedTop 剪裁框是否在activity最顶层（会盖住所有的view）
     */
    public MultiPickerBuilder setSingleCropCutNeedTop(boolean singleCropCutNeedTop) {
        selectConfig.setSingleCropCutNeedTop(singleCropCutNeedTop);
        return this;
    }

    //—————————————————————— 以上为单图剪裁的属性 ——————————————————————

    /**
     * @param config 选择配置
     */
    public MultiPickerBuilder withMultiSelectConfig(MultiSelectConfig config) {
        this.selectConfig = config;
        return this;
    }

    /**
     * fragment模式调用
     *
     * @param completeListener 选择回调
     * @return MultiImagePickerFragment
     */
    public MultiImagePickerFragment pickWithFragment(OnImagePickCompleteListener completeListener) {
        checkVideoAndImage();
        MultiImagePickerFragment mFragment = new MultiImagePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SELECT_CONFIG, selectConfig);
        bundle.putSerializable(INTENT_KEY_PRESENTER, presenter);
        mFragment.setArguments(bundle);
        mFragment.setOnImagePickCompleteListener(completeListener);
        return mFragment;
    }

    /**
     * 直接开启相册选择
     *
     * @param context  页面调用者
     * @param listener 选择器选择回调
     */
    public void pick(Activity context, final OnImagePickCompleteListener listener) {
        checkVideoAndImage();
        if (selectConfig.getMimeTypes() == null || selectConfig.getMimeTypes().size() == 0) {
            PickerErrorExecutor.executeError(listener, PickerError.MIMETYPES_EMPTY.getCode());
            presenter.tip(context, context.getString(R.string.picker_str_tip_mimeTypes_empty));
            return;
        }
        MultiImagePickerActivity.intent(context, selectConfig, presenter, listener);
    }

    /**
     * 调用单图剪裁
     *
     * @param context  页面调用者
     * @param listener 选择器剪裁回调，只支持一张图片
     */
    public void crop(Activity context, OnImagePickCompleteListener listener) {
        setMaxCount(1);
        filterMimeTypes(MimeType.ofVideo());
        setSinglePickImageOrVideoType(false);
        setSinglePickWithAutoComplete(true);
        setVideoSinglePick(false);
        setShieldList(null);
        setLastImageList(null);
        setPreview(false);
        selectConfig.setSelectMode(SelectMode.MODE_CROP);
        if (selectConfig.isCircle()) {
            selectConfig.setCropRatio(1, 1);
        }
        if (selectConfig.getMimeTypes() == null || selectConfig.getMimeTypes().size() == 0) {
            PickerErrorExecutor.executeError(listener, PickerError.MIMETYPES_EMPTY.getCode());
            presenter.tip(context, context.getString(R.string.picker_str_tip_mimeTypes_empty));
            return;
        }
        MultiImagePickerActivity.intent(context, selectConfig, presenter, listener);
    }

    /**
     * 检测文件加载类型中是否全是图片或视频
     */
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
}
