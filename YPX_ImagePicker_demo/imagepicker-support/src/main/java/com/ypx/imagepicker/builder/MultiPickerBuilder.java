package com.ypx.imagepicker.builder;

import android.app.Activity;
import android.os.Bundle;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.activity.multi.MultiImagePickerFragment;
import com.ypx.imagepicker.activity.multi.MultiImagePreviewActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
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
    private PickerSelectConfig pickerSelectConfig;
    private IMultiPickerBindPresenter presenter;

    public MultiPickerBuilder(IMultiPickerBindPresenter presenter) {
        this.presenter = presenter;
        this.pickerSelectConfig = new PickerSelectConfig();
    }

    public MultiPickerBuilder setPickerSelectConfig(PickerSelectConfig config) {
        this.pickerSelectConfig = config;
        return this;
    }

    public void pick(Activity context, final OnImagePickCompleteListener listener) {
        pickerSelectConfig.setSelectMode(pickerSelectConfig.getMaxCount() > 1 ?
                ImageSelectMode.MODE_MULTI : ImageSelectMode.MODE_SINGLE);
        MultiPickerData.instance.clear();
        if (pickerSelectConfig.getMaxCount() <= 0) {
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
        pickerSelectConfig.setSelectMode(ImageSelectMode.MODE_CROP);
        dealIntent(context, listener);
    }


    public void takePhoto(Activity context, OnImagePickCompleteListener listener) {
        pickerSelectConfig.setSelectMode(ImageSelectMode.MODE_TAKEPHOTO);
        MultiPickerData.instance.clear();
        dealIntent(context, listener);
    }


    public <T> void preview(Activity context, ArrayList<T> imageList, int pos, OnImagePickCompleteListener listener) {
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        MultiPickerData.instance.clear();
        MultiImagePreviewActivity.preview(context,
                pickerSelectConfig,
                presenter,
                context instanceof MultiImagePickerActivity,
                transitArray(imageList),
                pos,
                listener);
    }


    private void dealIntent(Activity activity, final OnImagePickCompleteListener listener) {
        MultiImagePickerActivity.intent(activity, pickerSelectConfig, presenter, listener);
    }


    public MultiPickerBuilder setMaxCount(int selectLimit) {
        if (selectLimit > 1) {
            pickerSelectConfig.setSelectMode(ImageSelectMode.MODE_MULTI);
        }
        pickerSelectConfig.setMaxCount(selectLimit);
        return this;
    }

    public MultiPickerBuilder setMaxVideoDuration(long duration) {
        ImagePicker.MAX_VIDEO_DURATION = duration;
        return this;
    }

    public MultiPickerBuilder showVideo(boolean showVideo) {
        pickerSelectConfig.setShowVideo(showVideo);
        return this;
    }

    public MultiPickerBuilder showGif(boolean showGif) {
        pickerSelectConfig.setLoadGif(showGif);
        return this;
    }


    public MultiPickerBuilder setColumnCount(int columnCount) {
        pickerSelectConfig.setColumnCount(columnCount);
        return this;
    }

    public MultiPickerBuilder setPreview(boolean isPreview) {
        pickerSelectConfig.setPreview(isPreview);
        return this;
    }


    public MultiPickerBuilder showCamera(boolean showCamera) {
        pickerSelectConfig.setShowCamera(showCamera);
        return this;
    }

    public MultiPickerBuilder setSinglePickImageOrVideoType(boolean isSinglePickImageOrVideoType) {
        pickerSelectConfig.setSinglePickImageOrVideoType(isSinglePickImageOrVideoType);
        return this;
    }


    public MultiPickerBuilder setVideoSinglePick(boolean isVideoSinglePick) {
        pickerSelectConfig.setVideoSinglePick(isVideoSinglePick);
        return this;
    }


    public MultiPickerBuilder showImage(boolean showImage) {
        pickerSelectConfig.setShowImage(showImage);
        return this;
    }

    public <T> MultiPickerBuilder setShieldList(ArrayList<T> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return this;
        }
        pickerSelectConfig.setShieldImageList(transitArray(imageList));
        return this;
    }

    public <T> MultiPickerBuilder setLastImageList(ArrayList<T> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return this;
        }
        pickerSelectConfig.setLastImageList(transitArray(imageList));
        return this;
    }

    public MultiPickerBuilder setCropRatio(int x, int y) {
        pickerSelectConfig.setCropRatio(x, y);
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
        bundle.putSerializable(INTENT_KEY_SELECT_CONFIG, pickerSelectConfig);
        bundle.putSerializable(INTENT_KEY_UI_CONFIG, presenter);
        return bundle;
    }

    public MultiImagePickerFragment pickWithFragment() {
        MultiImagePickerFragment mFragment = new MultiImagePickerFragment();
        mFragment.setArguments(getFragmentArguments());
        return mFragment;
    }
}
