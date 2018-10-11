package com.ypx.imagepicker.config;

import java.io.Serializable;

/**
 * 作者：yangpeixing on 2018/9/26 16:27
 * 功能：图片选择器配置器
 * 产权：南京婚尚信息技术
 */
public class ImgPickerSelectConfig implements Serializable {
    private int selectLimit = 9;
    private int columnCount = 3;
    private boolean isShowCamera = true;
    private boolean isShowOriginalCheckBox;
    private boolean isCanEditPic;
    private int selectMode;

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public boolean isShowOriginalCheckBox() {
        return isShowOriginalCheckBox;
    }

    public void setShowOriginalCheckBox(boolean showOriginalCheckBox) {
        isShowOriginalCheckBox = showOriginalCheckBox;
    }

    public boolean isCanEditPic() {
        return isCanEditPic;
    }

    public void setCanEditPic(boolean canEditPic) {
        isCanEditPic = canEditPic;
    }
}
