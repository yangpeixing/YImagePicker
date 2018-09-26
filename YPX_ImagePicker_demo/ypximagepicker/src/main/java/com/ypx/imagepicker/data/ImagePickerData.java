package com.ypx.imagepicker.data;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.interf.OnImagePickCompleteListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yangpeixing on 2018/9/20 15:43
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class ImagePickerData {
    public static OnImagePickCompleteListener onImagePickCompleteListener;
    public static List<ImageItem> selectImgs = new ArrayList<>();
    public static ImageSet currentImageSet = new ImageSet();

    public static ImageSet getCurrentImageSet() {
        if (currentImageSet == null) {
            currentImageSet = new ImageSet();
        }
        return currentImageSet;
    }

    public static void setCurrentImageSet(ImageSet currentImageSet) {
        ImagePickerData.currentImageSet = currentImageSet;
    }

    public static void addImageItem(ImageItem imageItem) {
        if (selectImgs == null) {
            selectImgs = new ArrayList<>();
        }
        selectImgs.add(imageItem);
    }

    public static void removeImageItem(ImageItem imageItem) {
        if (selectImgs != null && selectImgs.contains(imageItem)) {
            selectImgs.remove(imageItem);
        }
    }

    public static List<ImageItem> getSelectImgs() {
        if (selectImgs != null) {
            return selectImgs;
        }
        return new ArrayList<>();
    }

    public static boolean hasItem(ImageItem imageItem) {
        return selectImgs != null && selectImgs.contains(imageItem);
    }

    public static boolean isOverLimit(int limit) {
        return selectImgs != null && selectImgs.size() > limit;
    }
}
