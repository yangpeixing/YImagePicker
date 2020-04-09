package com.ypx.imagepicker.helper;

import android.widget.ImageView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.IPickerPresenter;

public class DetailImageLoadHelper {

    public static void displayDetailImage(boolean isCrop, final ImageView imageView,
                                          final IPickerPresenter presenter, final ImageItem imageItem) {
        if (presenter != null) {
            //剪裁不压缩，大图预览尺寸超过2K的图片需要压缩，不能使用ARGB-8888加载，滑动会卡顿，并且浪费内存，
            // 其实最好的做法是分段加载，但是cropImageView在支持剪裁的基础上不能支持分段加载
            if (isCrop || ImagePicker.isPreviewWithHighQuality()) {
                presenter.displayImage(imageView, imageItem, imageView.getWidth(), false);
            } else {
                presenter.displayImage(imageView, imageItem, imageView.getWidth(), imageItem.isOver2KImage());
            }

        }
    }
}
