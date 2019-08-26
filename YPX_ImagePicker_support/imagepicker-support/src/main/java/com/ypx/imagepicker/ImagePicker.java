package com.ypx.imagepicker;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.builder.CropPickerBuilder;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.builder.MultiPickerBuilder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;


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
    public static final String INTENT_KEY_PICKERRESULT = "pickerResult";
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
     * 小红书样式剪裁fragment形式
     *
     * @param bindPresenter 数据交互类
     */
    public static CropPickerBuilder withCropFragment(ICropPickerBindPresenter bindPresenter) {
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
     * 微信样式多选
     *
     * @param iMultiPickerBindPresenter 选择器UI提供者
     * @return 微信样式多选
     */
    public static MultiPickerBuilder withMultiFragment(IMultiPickerBindPresenter iMultiPickerBindPresenter) {
        return new MultiPickerBuilder(iMultiPickerBindPresenter);
    }

    /**
     * 注册媒体监听器，用于捕获系统媒体文件发生变化
     *
     * @param application 应用application，可放入自定义Application中
     * @deprecated
     */
    public static void registerMediaObserver(Application application) {

    }

    /**
     * 预加载选择器媒体文件
     *
     * @param activity    预加载的activity
     * @param isLoadImage 是否预加载图片
     * @param isLoadVideo 是否预加载视频
     * @param isLoadGif   是否预加载GIF图
     * @deprecated
     */
    public static void preload(FragmentActivity activity, boolean isLoadImage, boolean isLoadVideo, boolean isLoadGif) {

    }

    /**
     * 清除缓存数据
     */
    public static void clearAllCache() {
        MultiPickerData.instance.clear();
    }
}
