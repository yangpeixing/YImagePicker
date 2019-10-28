package com.ypx.imagepicker.data;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;

import java.util.ArrayList;

/**
 * Time: 2019/10/27 22:02
 * Author:ypx
 * Description: 类型回调类，调用者可自己定制回调的返回类型
 */
public abstract class OnPickerCompleteListener<T> implements OnImagePickCompleteListener2 {

    /**
     * 默认回调出来的是 ArrayList<ImageItem> 类型，调用者自己实现类型间转化
     *
     * @param items 选择器回调
     * @return 用户自己类型
     */
    public abstract T onTransit(ArrayList<ImageItem> items);

    /**
     * 选择器完成回调
     *
     * @param t 回调类型
     */
    public abstract void onPickComplete(T t);

    @Override
    public void onPickFailed(PickerError error) {

    }

    @Override
    public void onImagePickComplete(ArrayList<ImageItem> items) {
        onPickComplete(onTransit(items));
    }
}
