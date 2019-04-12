package com.ypx.imagepicker;

import android.os.Environment;

import com.ypx.imagepicker.utils.FileUtil;

import java.io.File;

/**
 * Description: 图片加载启动类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class CropImagePicker {

    //选择返回的key
    public static final String INTENT_KEY_PICKERRESULT = "pickerResult";
    //选择返回code
    public static final int REQ_PICKER_RESULT_CODE = 1433;

    static String cropPicSaveFilePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "Crop" + File.separator;

    public static PickerBuilder create(IDataBindingProvider loaderProvider) {
        return new PickerBuilder(loaderProvider);
    }

    public static void clearCropFiles() {
        FileUtil.deleteFile(new File(cropPicSaveFilePath));
    }
}
