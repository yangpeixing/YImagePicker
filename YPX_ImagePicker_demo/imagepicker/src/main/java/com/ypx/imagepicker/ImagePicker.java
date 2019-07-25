package com.ypx.imagepicker;

import android.os.Environment;

import com.ypx.imagepicker.builder.CropPickerBuilder;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.builder.MultiPickerBuilder;

import java.io.File;

/**
 * Description: 图片加载启动类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class ImagePicker {
    /**
     * 可以选取的视频最大时长
     */
    public static final int MAX_VIDEO_DURATION = 120000;
    //选择返回的key
    public static final String INTENT_KEY_PICKERRESULT = "pickerResult";
    //选择返回code
    public static final int REQ_PICKER_RESULT_CODE = 1433;

    public static String cropPicSaveFilePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "Crop" + File.separator;

    public static CropPickerBuilder withCrop(ICropPickerBindPresenter loaderProvider) {
        return new CropPickerBuilder(loaderProvider);
    }

    public static CropPickerBuilder withCropFragment(ICropPickerBindPresenter loaderProvider) {
        return new CropPickerBuilder(loaderProvider);
    }

    public static MultiPickerBuilder withMulti(IMultiPickerBindPresenter iMultiPickerUIProvider) {
        return new MultiPickerBuilder(iMultiPickerUIProvider);
    }
}
