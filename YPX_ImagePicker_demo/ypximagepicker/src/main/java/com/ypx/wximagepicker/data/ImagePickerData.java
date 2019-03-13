package com.ypx.wximagepicker.data;

import com.ypx.wximagepicker.bean.SimpleImageItem;
import com.ypx.wximagepicker.bean.ImageSet;
import com.ypx.wximagepicker.interf.OnImagePickCompleteListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yangpeixing on 2018/9/20 15:43
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class ImagePickerData {
    public static OnImagePickCompleteListener onImagePickCompleteListener;
    public static List<SimpleImageItem> selectImgs = new ArrayList<>();
    public static ImageSet currentImageSet = new ImageSet();

    public static ImageSet getCurrentImageSet() {
        if (currentImageSet == null) {
            currentImageSet = new ImageSet();
        }
        return currentImageSet;
    }

    public static void setCurrentImageSet(ImageSet mcurrentImageSet) {
        currentImageSet = mcurrentImageSet;
    }

    public static void addImageItem(SimpleImageItem simpleImageItem) {
        if (selectImgs == null) {
            selectImgs = new ArrayList<>();
        }
        selectImgs.add(simpleImageItem);
    }

    public static void removeImageItem(SimpleImageItem simpleImageItem) {
        if (selectImgs != null && selectImgs.contains(simpleImageItem)) {
            selectImgs.remove(simpleImageItem);
        }
    }

    public static List<SimpleImageItem> getSelectImgs() {
        if (selectImgs != null) {
            return selectImgs;
        }
        return new ArrayList<>();
    }

    public static boolean hasItem(SimpleImageItem simpleImageItem) {
        return selectImgs != null && selectImgs.contains(simpleImageItem);
    }

    public static boolean isOverLimit(int limit) {
        return selectImgs != null && selectImgs.size() > limit;
    }
}
