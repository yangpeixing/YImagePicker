package com.ypx.imagepicker.data;


import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 图片选择器回调
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface OnImagePickCompleteListener2 extends OnImagePickCompleteListener {
    void onPickFailed(PickerError error);
}
