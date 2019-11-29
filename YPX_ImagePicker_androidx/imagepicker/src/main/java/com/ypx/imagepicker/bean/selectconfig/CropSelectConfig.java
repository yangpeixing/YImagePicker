package com.ypx.imagepicker.bean.selectconfig;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;


/**
 * Time: 2019/9/3 13:46
 * Author:ypx
 * Description:小红书剪裁配置类
 */
public class CropSelectConfig extends BaseSelectConfig {
    private ImageItem firstImageItem;
  //  private String cropSaveFilePath = ImagePicker.cropPicSaveFilePath;

    public CropSelectConfig() {
        setSinglePickImageOrVideoType(true);
    }

    public ImageItem getFirstImageItem() {
        return firstImageItem;
    }


    public void setFirstImageItem(ImageItem firstImageItem) {
        this.firstImageItem = firstImageItem;
    }

//    public String getCropSaveFilePath() {
//        return cropSaveFilePath;
//    }
//
//    public void setCropSaveFilePath(String cropSaveFilePath) {
//        this.cropSaveFilePath = cropSaveFilePath;
//    }

    public boolean hasFirstImageItem() {
        return firstImageItem != null && firstImageItem.width > 0 && firstImageItem.height > 0;
    }
}
