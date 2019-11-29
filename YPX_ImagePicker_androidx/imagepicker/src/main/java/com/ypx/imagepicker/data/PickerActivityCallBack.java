package com.ypx.imagepicker.data;

import android.content.Intent;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.helper.launcher.PLauncher;

import java.util.ArrayList;

/**
 * Time: 2019/11/6 17:35
 * Author:ypx
 * Description:选择器activityResult处理类
 */
public class PickerActivityCallBack implements PLauncher.Callback {
    private OnImagePickCompleteListener listener;

    public static PickerActivityCallBack create(OnImagePickCompleteListener listener) {
        return new PickerActivityCallBack(listener);
    }

    private PickerActivityCallBack(OnImagePickCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void onActivityResult(int resultCode, Intent data) {
        if (listener != null
                && resultCode == ImagePicker.REQ_PICKER_RESULT_CODE
                && data.hasExtra(ImagePicker.INTENT_KEY_PICKER_RESULT)) {
            ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
            listener.onImagePickComplete(list);
        } else if (listener instanceof OnImagePickCompleteListener2) {
            if (resultCode == 0) {
                resultCode = PickerError.CANCEL.getCode();
            }
            ((OnImagePickCompleteListener2) listener).onPickFailed(PickerError.valueOf(resultCode));
        }
    }
}
