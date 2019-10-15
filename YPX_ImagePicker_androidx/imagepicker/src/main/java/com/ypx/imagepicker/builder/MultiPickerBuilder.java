package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.activity.multi.MultiImagePickerFragment;
import com.ypx.imagepicker.activity.multi.MultiImagePreviewActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

import java.util.ArrayList;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_UI_CONFIG;

/**
 * Description: 多选选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2018/9/19 16:56
 */
public class MultiPickerBuilder {
    private MultiSelectConfig multiSelectConfig;
    private IMultiPickerBindPresenter presenter;

    public MultiPickerBuilder(IMultiPickerBindPresenter presenter) {
        this.presenter = presenter;
        this.multiSelectConfig = new MultiSelectConfig();
    }

    /**
     * @param config 选择配置
     */
    public MultiPickerBuilder withMultiSelectConfig(MultiSelectConfig config) {
        this.multiSelectConfig = config;
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
        if (multiSelectConfig.getMaxCount() <= 0) {
            presenter.tip(context, context.getResources().getString(R.string.str_setcount));
            return;
        }
        dealIntent(context, listener);
    }

    /**
     * 调用剪裁
     *
     * @param context  页面调用者
     * @param listener 选择器剪裁回调，只支持一张图片
     */
    public void crop(Activity context, OnImagePickCompleteListener listener) {
        setMaxCount(1);
        showVideo(false);
        setSinglePickImageOrVideoType(false);
        setVideoSinglePick(false);
        setShieldList(null);
        setLastImageList(null);
        setPreview(false);
        MultiPickerData.instance.clear();
        multiSelectConfig.setSelectMode(SelectMode.MODE_CROP);
        dealIntent(context, listener);
    }

    /**
     * @param context  页面调用者
     * @param listener 拍照回调
     * @deprecated
     */
    public void takePhoto(Activity context, OnImagePickCompleteListener listener) {
        multiSelectConfig.setSelectMode(SelectMode.MODE_TAKEPHOTO);
        MultiPickerData.instance.clear();
        dealIntent(context, listener);
    }


    /**
     * 图片预览
     *
     * @param context   上下文
     * @param imageList 预览的图片数组
     * @param pos       默认位置
     * @param listener  编辑回调
     * @param <T>       String or ImageItem
     */
    public <T> void preview(Activity context, ArrayList<T> imageList, int pos, OnImagePickCompleteListener listener) {
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        MultiPickerData.instance.clear();
        MultiImagePreviewActivity.preview(context,
                multiSelectConfig,
                presenter,
                context instanceof MultiImagePickerActivity,
                transitArray(imageList),
                pos,
                listener);
    }

    /**
     * 处理跳转数据
     */
    private void dealIntent(Activity activity, final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, MultiImagePickerActivity.class);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG, multiSelectConfig);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_UI_CONFIG, presenter);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKER_RESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }

    /**
     * @param selectLimit 设置最大数量限制
     */
    public MultiPickerBuilder setMaxCount(int selectLimit) {
        multiSelectConfig.setMaxCount(selectLimit);
        return this;
    }

    /**
     * @param selectMode 设置选择模式
     *                   {@link SelectMode}
     */
    public MultiPickerBuilder setSelectMode(int selectMode) {
        multiSelectConfig.setSelectMode(selectMode);
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
        multiSelectConfig.setCropRectMargin(margin);
        return this;
    }

    /**
     * 设置剪裁图片保存路径
     *
     * @param path 路径+图片名字
     */
    public MultiPickerBuilder cropSaveFilePath(String path) {
        multiSelectConfig.setCropSaveFilePath(path);
        return this;
    }

    /**
     * @param showVideo 加载视频
     */
    public MultiPickerBuilder showVideo(boolean showVideo) {
        multiSelectConfig.setShowVideo(showVideo);
        return this;
    }

    /**
     * @param showGif 加载GIF
     */
    public MultiPickerBuilder showGif(boolean showGif) {
        multiSelectConfig.setLoadGif(showGif);
        return this;
    }

    /**
     * @param columnCount 设置列数
     */
    public MultiPickerBuilder setColumnCount(int columnCount) {
        multiSelectConfig.setColumnCount(columnCount);
        return this;
    }

    /**
     *  设置圆形剪裁
     */
    public MultiPickerBuilder cropAsCircle() {
        multiSelectConfig.setCircle(true);
        return this;
    }

    /**
     * @param isPreview 是否开启预览
     */
    public MultiPickerBuilder setPreview(boolean isPreview) {
        multiSelectConfig.setPreview(isPreview);
        return this;
    }


    /**
     * @param showCamera 显示拍照item
     */
    public MultiPickerBuilder showCamera(boolean showCamera) {
        multiSelectConfig.setShowCamera(showCamera);
        return this;
    }

    /**
     * @param isSinglePickImageOrVideoType 是否只能选择视频或图片
     */
    public MultiPickerBuilder setSinglePickImageOrVideoType(boolean isSinglePickImageOrVideoType) {
        multiSelectConfig.setSinglePickImageOrVideoType(isSinglePickImageOrVideoType);
        return this;
    }


    /**
     * @param isVideoSinglePick 视频是否单选
     */
    public MultiPickerBuilder setVideoSinglePick(boolean isVideoSinglePick) {
        multiSelectConfig.setVideoSinglePick(isVideoSinglePick);
        return this;
    }

    /**
     * @param showImage 加载图片
     */
    public MultiPickerBuilder showImage(boolean showImage) {
        multiSelectConfig.setShowImage(showImage);
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
        multiSelectConfig.setShieldImageList(transitArray(imageList));
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
        multiSelectConfig.setLastImageList(transitArray(imageList));
        return this;
    }

    /**
     * 设置单张图片剪裁比例
     *
     * @param x 剪裁比例x
     * @param y 剪裁比例y
     */
    public MultiPickerBuilder setCropRatio(int x, int y) {
        multiSelectConfig.setCropRatio(x, y);
        return this;
    }

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
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_SELECT_CONFIG, multiSelectConfig);
        bundle.putSerializable(INTENT_KEY_UI_CONFIG, presenter);
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
}
