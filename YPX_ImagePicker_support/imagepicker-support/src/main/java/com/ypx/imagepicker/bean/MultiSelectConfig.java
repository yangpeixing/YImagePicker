package com.ypx.imagepicker.bean;

import java.util.ArrayList;

/**
 * Description: 多选配置项
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiSelectConfig extends BaseSelectConfig {
    private int columnCount = 4;
    private boolean isShowOriginalCheckBox;
    private boolean isCanEditPic;
    private boolean isPreview = true;
    /**
     * 图片和视频只能选择一个
     */
    private boolean isSinglePickImageOrVideoType = true;
    private int selectMode = SelectMode.MODE_MULTI;
    private ArrayList<ImageItem> lastImageList = new ArrayList<>();
    private ArrayList<ImageItem> shieldImageList = new ArrayList<>();

    private int cropRatioX = 1;
    private int cropRatioY = 1;
    private int cropRectMargin = 0;
    private String cropSaveFilePath = "";

    public boolean isSinglePickImageOrVideoType() {
        return isSinglePickImageOrVideoType;
    }

    public void setSinglePickImageOrVideoType(boolean isSinglePickImageOrVideoType) {
        this.isSinglePickImageOrVideoType = isSinglePickImageOrVideoType;
    }

    public int getCropRectMargin() {
        return cropRectMargin;
    }

    public void setCropRectMargin(int cropRectMargin) {
        this.cropRectMargin = cropRectMargin;
    }

    public String getCropSaveFilePath() {
        return cropSaveFilePath;
    }

    public void setCropSaveFilePath(String cropSaveFilePath) {
        this.cropSaveFilePath = cropSaveFilePath;
    }
    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public int getCropRatioX() {
        return cropRatioX;
    }

    public void setCropRatio(int x, int y) {
        this.cropRatioX = x;
        this.cropRatioY = y;
    }

    public int getCropRatioY() {
        return cropRatioY;
    }


    public ArrayList<ImageItem> getShieldImageList() {
        return shieldImageList;
    }

    public void setShieldImageList(ArrayList<ImageItem> shieldImageList) {
        this.shieldImageList = shieldImageList;
    }

    public ArrayList<ImageItem> getLastImageList() {
        return lastImageList;
    }

    public void setLastImageList(ArrayList<ImageItem> lastImageList) {
        this.lastImageList = lastImageList;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
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

    /**
     * 是否屏蔽某个URL
     */
    public boolean isShieldItem(ImageItem imageItem) {
        if (shieldImageList == null || shieldImageList.size() == 0) {
            return false;
        }
        for (ImageItem item : shieldImageList) {
            if (item.equals(imageItem)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否是之前选中过的
     */
    public boolean isLastItem(ImageItem imageItem) {
        if (lastImageList == null || lastImageList.size() == 0) {
            return false;
        }
        for (ImageItem item : lastImageList) {
            if (item.equals(imageItem)) {
                return true;
            }
        }
        return false;
    }
}
