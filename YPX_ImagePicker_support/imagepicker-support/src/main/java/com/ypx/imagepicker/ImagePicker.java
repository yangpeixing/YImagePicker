package com.ypx.imagepicker;

import android.os.Environment;

import com.ypx.imagepicker.builder.CropPickerBuilder;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

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
    public static long MAX_VIDEO_DURATION = 120000;
    //选择返回的key
    public static final String INTENT_KEY_PICKER_RESULT = "pickerResult";
    //选择返回code
    public static final int REQ_PICKER_RESULT_CODE = 1433;

    /**
     * 图片剪裁的保存路径
     */
    public static String cropPicSaveFilePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "Crop" + File.separator;

    /**
     * 小红书样式剪裁activity形式
     *
     * @param bindPresenter 数据交互类
     */
    public static CropPickerBuilder withCrop(ICropPickerBindPresenter bindPresenter) {
        return new CropPickerBuilder(bindPresenter);
    }

    /**
     * 微信样式多选
     *
     * @param iMultiPickerBindPresenter 选择器UI提供者
     * @return 微信样式多选
     */
    public static MultiPickerBuilder withMulti(IMultiPickerBindPresenter iMultiPickerBindPresenter) {
        return new MultiPickerBuilder(iMultiPickerBindPresenter);
    }

    /**
     * 清除缓存数据
     */
    public static void clearAllCache() {
        MultiPickerData.instance.clear();
    }
}
