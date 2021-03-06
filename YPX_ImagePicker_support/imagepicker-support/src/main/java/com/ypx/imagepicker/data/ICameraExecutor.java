package com.ypx.imagepicker.data;

import android.support.annotation.Nullable;

import com.ypx.imagepicker.bean.ImageItem;

public interface ICameraExecutor {

    void takePhoto();

    void takeVideo();

    void onTakePhotoResult(@Nullable ImageItem imageItem);
}
