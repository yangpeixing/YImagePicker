package com.ypx.imagepicker.bean;

import com.ypx.imagepicker.utils.PDateUtil;

import java.io.Serializable;
import java.util.Set;

/**
 * Time: 2019/9/30 11:05
 * Author:ypx
 * Description: 配置类基类
 */
public class BaseSelectConfig implements Serializable {
    private int maxCount;
    private int minCount;
    private long minVideoDuration = 0;
    private long maxVideoDuration = 120000L;
    private int columnCount = 4;
    private boolean isShowCamera;
    private boolean isVideoSinglePick = true;
    private boolean isShowVideo = true;
    private boolean isShowImage = true;
    private boolean isLoadGif = false;
    /**
     * 图片和视频只能选择一个
     */
    private boolean isSinglePickImageOrVideoType = false;
    private Set<MimeType> mimeTypes = MimeType.ofAll();

    public boolean isSinglePickImageOrVideoType() {
        return isSinglePickImageOrVideoType;
    }

    public void setSinglePickImageOrVideoType(boolean singlePickImageOrVideoType) {
        isSinglePickImageOrVideoType = singlePickImageOrVideoType;
    }

    public int getMinCount() {
        return minCount;
    }

    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    public long getMinVideoDuration() {
        return minVideoDuration;
    }

    public void setMinVideoDuration(long minVideoDuration) {
        this.minVideoDuration = minVideoDuration;
    }

    public long getMaxVideoDuration() {
        return maxVideoDuration;
    }

    public String getMaxVideoDurationFormat() {
        return PDateUtil.formatTime(maxVideoDuration);
    }

    public String getMinVideoDurationFormat() {
        return PDateUtil.formatTime(minVideoDuration);
    }

    public void setMaxVideoDuration(long maxVideoDuration) {
        this.maxVideoDuration = maxVideoDuration;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public boolean isVideoSinglePick() {
        return isVideoSinglePick;
    }

    public void setVideoSinglePick(boolean videoSinglePick) {
        isVideoSinglePick = videoSinglePick;
    }

    public boolean isShowVideo() {
        return isShowVideo;
    }

    public void setShowVideo(boolean showVideo) {
        isShowVideo = showVideo;
    }

    public boolean isShowImage() {
        return isShowImage;
    }

    public void setShowImage(boolean showImage) {
        isShowImage = showImage;
    }

    public boolean isLoadGif() {
        return isLoadGif;
    }

    public void setLoadGif(boolean loadGif) {
        isLoadGif = loadGif;
    }

    public Set<MimeType> getMimeTypes() {
        return mimeTypes;
    }

    public void setMimeTypes(Set<MimeType> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }
}
