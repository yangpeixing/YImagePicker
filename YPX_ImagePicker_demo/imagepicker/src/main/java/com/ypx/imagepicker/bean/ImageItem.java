package com.ypx.imagepicker.bean;

import java.io.Serializable;

/**
 * Description: 图片信息
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImageItem implements Serializable {
    private static final long serialVersionUID = 3429291195776736078L;
    private int id;
    public String path;
    public int width;
    public int height;
    public long time;
    public String timeFormat;

    public long duration;
    public String durationFormat;
    public String videoImageUri;
    private boolean isVideo = false;

    private boolean isSelect = false;
    private boolean isPress = false;
    private int selectIndex = -1;

    private int cropMode = ImageCropMode.ImageScale_FILL;
    private String cropUrl;

    public ImageItem() {

    }

    public String getVideoImageUri() {
        if (videoImageUri == null || videoImageUri.length() == 0) {
            return path;
        }
        return videoImageUri;
    }

    public ImageItem(String path, long duration, String videoImageUri) {
        this.path = path;
        this.duration = duration;
        this.videoImageUri = videoImageUri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDurationFormat() {
        return durationFormat;
    }

    public void setDurationFormat(String durationFormat) {
        this.durationFormat = durationFormat;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public int getCropMode() {
        return cropMode;
    }

    public void setCropMode(int cropMode) {
        this.cropMode = cropMode;
    }

    public String getCropUrl() {
        return cropUrl;
    }

    public void setCropUrl(String cropUrl) {
        this.cropUrl = cropUrl;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public boolean isPress() {
        return isPress;
    }

    public void setPress(boolean press) {
        isPress = press;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public ImageItem(String path, long time) {
        this.path = path;
        this.time = time;
    }

    public ImageItem(String path, int width, int height, long time) {
        this.path = path;
        this.time = time;
        this.width = width;
        this.height = height;
    }

    public float getWidthHeightRatio() {
        if (height == 0) {
            return 1;
        }
        return width * 1.00f / (height * 1.00f);
    }

    /**
     * 获取图片宽高类型，误差0.1
     *
     * @return 1：宽图  -1：高图  0：方图
     */
    public int getWidthHeightType() {
        if (getWidthHeightRatio() > 1.02f) {
            return 1;
        }

        if (getWidthHeightRatio() < 0.98f) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (path == null) {
            return false;
        }
        try {
            ImageItem other = (ImageItem) o;
            if (other.path == null) {
                return false;
            }
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
