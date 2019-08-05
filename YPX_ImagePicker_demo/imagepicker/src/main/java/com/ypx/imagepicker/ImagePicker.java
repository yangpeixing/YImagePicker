package com.ypx.imagepicker;

import android.app.Application;
import android.os.Environment;

import androidx.fragment.app.FragmentActivity;

import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.builder.CropPickerBuilder;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.MediaDataSource;
import com.ypx.imagepicker.data.impl.MediaObserver;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.builder.MultiPickerBuilder;

import java.io.File;
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

    public static void registerMediaObserver(Application application) {
        MediaObserver.instance.register(application);
    }

    public static void preload(FragmentActivity activity, boolean isLoadImage, boolean isLoadVideo, boolean isLoadGif) {
        MediaDataSource dataSource = new MediaDataSource(activity);
        dataSource.setLoadVideo(isLoadVideo);
        dataSource.setLoadImage(isLoadImage);
        dataSource.setLoadGif(isLoadGif);
        dataSource.provideMediaItems(new OnImagesLoadedListener() {
            @Override
            public void onImagesLoaded(List<ImageSet> imageSetList) {

            }
        });
    }
}
