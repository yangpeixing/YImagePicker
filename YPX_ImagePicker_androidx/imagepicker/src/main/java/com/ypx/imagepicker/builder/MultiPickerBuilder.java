package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity;
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

    public MultiPickerBuilder withMultiSelectConfig(MultiSelectConfig config) {
        this.multiSelectConfig = config;
        return this;
    }

    public void pick(Activity context, final OnImagePickCompleteListener listener) {
        MultiPickerData.instance.clear();
        if (multiSelectConfig.getMaxCount() <= 0) {
            presenter.tip(context, context.getResources().getString(R.string.str_setcount));
            return;
        }
        dealIntent(context, listener);
    }

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


    public void takePhoto(Activity context, OnImagePickCompleteListener listener) {
        multiSelectConfig.setSelectMode(SelectMode.MODE_TAKEPHOTO);
        MultiPickerData.instance.clear();
        dealIntent(context, listener);
    }


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

    private void dealIntent(Activity activity, final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, MultiImagePickerActivity.class);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG, multiSelectConfig);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_UI_CONFIG, presenter);
        PLauncher.init(activity).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKERRESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKERRESULT);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }


    public MultiPickerBuilder setMaxCount(int selectLimit) {
        multiSelectConfig.setMaxCount(selectLimit);
        return this;
    }

    public MultiPickerBuilder setSelectMode(int selectMode) {
        multiSelectConfig.setSelectMode(selectMode);
        return this;
    }

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

    public MultiPickerBuilder showVideo(boolean showVideo) {
        multiSelectConfig.setShowVideo(showVideo);
        return this;
    }

    public MultiPickerBuilder showGif(boolean showGif) {
        multiSelectConfig.setLoadGif(showGif);
        return this;
    }

    public MultiPickerBuilder setColumnCount(int columnCount) {
        multiSelectConfig.setColumnCount(columnCount);
        return this;
    }

    public MultiPickerBuilder setPreview(boolean isPreview) {
        multiSelectConfig.setPreview(isPreview);
        return this;
    }


    public MultiPickerBuilder showCamera(boolean showCamera) {
        multiSelectConfig.setShowCamera(showCamera);
        return this;
    }

    public MultiPickerBuilder setSinglePickImageOrVideoType(boolean isSinglePickImageOrVideoType) {
        multiSelectConfig.setSinglePickImageOrVideoType(isSinglePickImageOrVideoType);
        return this;
    }


    public MultiPickerBuilder setVideoSinglePick(boolean isVideoSinglePick) {
        multiSelectConfig.setVideoSinglePick(isVideoSinglePick);
        return this;
    }


    public MultiPickerBuilder showImage(boolean showImage) {
        multiSelectConfig.setShowImage(showImage);
        return this;
    }

    public <T> MultiPickerBuilder setShieldList(ArrayList<T> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return this;
        }
        multiSelectConfig.setShieldImageList(transitArray(imageList));
        return this;
    }

    public <T> MultiPickerBuilder setLastImageList(ArrayList<T> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return this;
        }
        multiSelectConfig.setLastImageList(transitArray(imageList));
        return this;
    }

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

    public MultiImagePickerFragment pickWithFragment(OnImagePickCompleteListener completeListener) {
        MultiImagePickerFragment mFragment = new MultiImagePickerFragment();
        mFragment.setArguments(getFragmentArguments());
        mFragment.setOnImagePickCompleteListener(completeListener);
        return mFragment;
    }
}
