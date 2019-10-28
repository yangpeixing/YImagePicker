package com.ypx.imagepicker.data;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;

import java.util.ArrayList;

/**
 * Time: 2019/10/27 21:26
 * Author:ypx
 * Description:OnPickerCompleteListener子类，实现了ArrayList<String> 回调
 */
public abstract class OnStringListCompleteListener extends OnPickerCompleteListener<ArrayList<String>> {

    public abstract void onPickComplete(ArrayList<String> list);

    @Override
    public void onPickFailed(PickerError error) {

    }

    @Override
    public void onImagePickComplete(ArrayList<ImageItem> items) {
        onPickComplete(onTransit(items));
    }

    @Override
    public ArrayList<String> onTransit(ArrayList<ImageItem> items) {
        ArrayList<String> list = new ArrayList<>();
        for (ImageItem item : items) {
            list.add(item.path);
        }
        return list;
    }
}
