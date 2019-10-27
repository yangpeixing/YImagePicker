package com.ypx.imagepicker.data;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;

import java.util.ArrayList;

/**
 * Time: 2019/10/27 21:26
 * Author:ypx
 * Description: OnPickerCompleteListener 子类，实现了String 类型回调
 */
public abstract class OnStringCompleteListener extends OnPickerCompleteListener<String> {

    public abstract void onPickComplete(String path);

    @Override
    public String onTransit(ArrayList<ImageItem> items) {
        if (items.size() > 0 && items.get(0) != null) {
            return  items.get(0).path;
        }
        return null;
    }

    @Override
    public void onPickFailed(PickerError error) {

    }

    @Override
    public void onImagePickComplete(ArrayList<ImageItem> items) {
        onPickComplete(onTransit(items));
    }
}
