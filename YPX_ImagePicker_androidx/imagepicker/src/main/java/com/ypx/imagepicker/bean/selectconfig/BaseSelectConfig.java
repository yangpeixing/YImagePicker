package com.ypx.imagepicker.bean.selectconfig;

import android.content.Context;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.utils.PDateUtil;

import java.io.Serializable;
import java.util.ArrayList;
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
    private long maxVideoDuration = 1200000000L;
    private int columnCount = 4;
    private boolean isShowCamera;
    private boolean isShowCameraInAllMedia;
    private boolean isVideoSinglePick = true;
    private boolean isShowVideo = true;
    private boolean isShowImage = true;
    private boolean isLoadGif = false;

    private boolean isSinglePickAutoComplete = false;

    /**
     * 图片和视频只能选择一个
     */
    private boolean isSinglePickImageOrVideoType = false;
    private Set<MimeType> mimeTypes = MimeType.ofAll();
    private ArrayList<ImageItem> shieldImageList = new ArrayList<>();

    public boolean isShowCameraInAllMedia() {
        return isShowCameraInAllMedia;
    }

    public void setShowCameraInAllMedia(boolean showCameraInAllMedia) {
        isShowCameraInAllMedia = showCameraInAllMedia;
    }

    public ArrayList<ImageItem> getShieldImageList() {
        return shieldImageList;
    }

    public void setShieldImageList(ArrayList<ImageItem> shieldImageList) {
        this.shieldImageList = shieldImageList;
    }

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

    public String getMaxVideoDurationFormat(Context context) {
        return PDateUtil.formatTime(context, maxVideoDuration);
    }

    public String getMinVideoDurationFormat(Context context) {
        return PDateUtil.formatTime(context, minVideoDuration);
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

    public boolean isOnlyShowImage() {
        return isShowImage && !isShowVideo;
    }

    public boolean isOnlyShowVideo() {
        return isShowVideo && !isShowImage;
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

    public boolean isSinglePickAutoComplete() {
        return isSinglePickAutoComplete;
    }

    public void setSinglePickAutoComplete(boolean singlePickAutoComplete) {
        isSinglePickAutoComplete = singlePickAutoComplete;
    }

    public boolean isVideoSinglePickAndAutoComplete() {
        return isVideoSinglePick() && isSinglePickAutoComplete();
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
}
