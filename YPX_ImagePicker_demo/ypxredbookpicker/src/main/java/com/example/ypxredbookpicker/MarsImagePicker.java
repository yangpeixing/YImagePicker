package com.example.ypxredbookpicker;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.example.ypxredbookpicker.data.OnImagePickCompleteListener;
import com.example.ypxredbookpicker.utils.FileUtil;

import java.io.File;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class MarsImagePicker {

    public static OnImagePickCompleteListener listener;
    static String cropPicSaveFilePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "Crop" + File.separator;

    public static PickerBuilder create(ImageLoaderProvider loaderProvider) {
        return new PickerBuilder(loaderProvider);
    }

    public static void clearCropFiles() {
        FileUtil.deleteFile(new File(cropPicSaveFilePath));
    }


}
