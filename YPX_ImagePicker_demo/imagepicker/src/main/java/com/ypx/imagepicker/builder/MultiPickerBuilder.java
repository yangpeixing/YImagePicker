package com.ypx.imagepicker.builder;

import android.app.Activity;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.activity.multi.MultiImagePreviewActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;

import java.util.ArrayList;

/**
 * 作者：yangpeixing on 2018/9/19 16:56
 * 功能：图片选择器执行类
 * 产权：南京婚尚信息技术
 */
public class MultiPickerBuilder {
    private MultiSelectConfig multiSelectConfig;
    private IMultiPickerBindPresenter presenter;

    public MultiPickerBuilder(IMultiPickerBindPresenter presenter) {
        this.presenter = presenter;
        this.multiSelectConfig = new MultiSelectConfig();
    }

    public void pick(Activity context, final OnImagePickCompleteListener listener) {
        multiSelectConfig.setSelectMode(multiSelectConfig.getMaxCount() > 1 ?
                ImageSelectMode.MODE_MULTI : ImageSelectMode.MODE_SINGLE);
        MultiPickerData.instance.clear();
        if (multiSelectConfig.getMaxCount() <= 0) {
            presenter.tip(context, context.getResources().getString(R.string.str_setcount));
            return;
        }
        dealIntent(context, listener);
    }

    public void crop(Activity context, OnImagePickCompleteListener listener) {
        multiSelectConfig.setSelectMode(ImageSelectMode.MODE_CROP);
        showVideo(false);
        setMaxCount(1);
        MultiPickerData.instance.clear();
        dealIntent(context, listener);
    }


    public void takePhoto(Activity context, OnImagePickCompleteListener listener) {
        multiSelectConfig.setSelectMode(ImageSelectMode.MODE_TAKEPHOTO);
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
        MultiImagePickerActivity.intent(activity, multiSelectConfig, presenter, listener);
    }


    public MultiPickerBuilder setMaxCount(int selectLimit) {
        multiSelectConfig.setMaxCount(selectLimit);
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


    public MultiPickerBuilder showCamera(boolean showCamera) {
        multiSelectConfig.setShowCamera(showCamera);
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

//    public MultiPickerBuilder showOriginalCheckBox(boolean showOriginalCheckBox) {
//        multiSelectConfig.setShowOriginalCheckBox(showOriginalCheckBox);
//        return this;
//    }

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

//    public MultiPickerBuilder setCanEditPic(boolean canEditPic) {
//        multiSelectConfig.setCanEditPic(canEditPic);
//        return this;
//    }

}
