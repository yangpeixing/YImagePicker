package com.ypx.wximagepicker;

import com.ypx.wximagepicker.bean.SimpleImageItem;
import com.ypx.wximagepicker.data.ImagePickerData;
import com.ypx.wximagepicker.config.IImgPickerUIConfig;

import java.util.List;

/**
 * 作者：yangpeixing on 2018/5/6 15:10
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class YPXImagePicker {


    public static ImagePickerPresenter with(IImgPickerUIConfig iImgPickerUIConfig) {
        return new ImagePickerPresenter(iImgPickerUIConfig);
    }

    public static void notifyOnImagePickComplete(List<SimpleImageItem> items) {
        if (ImagePickerData.onImagePickCompleteListener != null) {
            ImagePickerData.onImagePickCompleteListener.onImagePickComplete(items);
        }
    }


    public static void clear() {
        if (ImagePickerData.selectImgs != null) {
            ImagePickerData.selectImgs.clear();
            ImagePickerData.selectImgs = null;
        }
        ImagePickerData.currentImageSet = null;
        //ImagePickerData.onImagePickCompleteListener = null;
    }


}
