package com.ypx.imagepicker.bean;

import java.io.Serializable;
import java.util.Set;

/**
 * Time: 2019/9/30 11:05
 * Author:ypx
 * Description: 配置类基类
 */
public class BaseSelectConfig implements Serializable {
    private int maxCount;
    private boolean isShowCamera;
    private boolean isVideoSinglePick = true;
    private boolean isShowVideo = true;
    private boolean isShowImage = true;
    private boolean isLoadGif = false;
    private Set<MimeType> mimeTypes = MimeType.ofAll();

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
