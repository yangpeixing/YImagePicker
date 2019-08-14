package com.ypx.imagepicker.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 多选配置项
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class PickerSelectConfig implements Serializable {
    private int maxCount = -1;
    private int columnCount = 4;
    private boolean isShowCamera = true;
    private boolean isShowVideo = true;
    private boolean isShowImage = true;
    private boolean isLoadGif = true;
    private boolean isShowOriginalCheckBox;
    private boolean isCanEditPic;
    private boolean isPreview = true;
    /**
     * 图片和视频只能选择一个
     */
    private boolean isSinglePickImageOrVideoType = true;
    /**
     * 视频是否可以多选
     */
    private boolean isVideoSinglePick = true;
    private int selectMode;
    private ArrayList<ImageItem> lastImageList = new ArrayList<>();
    private ArrayList<ImageItem> shieldImageList = new ArrayList<>();

    private int cropRatioX = 1;
    private int cropRatioY = 1;

    public boolean isSinglePickImageOrVideoType() {
        return isSinglePickImageOrVideoType;
    }

    public void setSinglePickImageOrVideoType(boolean isSinglePickImageOrVideoType) {
        this.isSinglePickImageOrVideoType = isSinglePickImageOrVideoType;
    }

    public boolean isVideoSinglePick() {
        return isVideoSinglePick;
    }

    public void setVideoSinglePick(boolean isVideoSinglePick) {
        this.isVideoSinglePick = isVideoSinglePick;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    public boolean isLoadGif() {
        return isLoadGif;
    }

    public void setLoadGif(boolean loadGif) {
        isLoadGif = loadGif;
    }

    public int getCropRatioX() {
        return cropRatioX;
    }

    public boolean isShowImage() {
        return isShowImage;
    }

    public void setShowImage(boolean showImage) {
        isShowImage = showImage;
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

    public boolean isShowVideo() {
        return isShowVideo;
    }

    public void setShowVideo(boolean showVideo) {
        isShowVideo = showVideo;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
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
