package com.ypx.imagepicker.bean.selectconfig;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.SelectMode;

import java.util.ArrayList;

/**
 * Description: 多选配置项
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiSelectConfig extends CropConfig {
    private boolean isShowOriginalCheckBox;
    private boolean isDefaultOriginal;
    private boolean isCanEditPic;
    private boolean isCanPreviewVideo = true;
    private boolean isPreview = true;

    private int selectMode = SelectMode.MODE_MULTI;
    private ArrayList<ImageItem> lastImageList = new ArrayList<>();

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
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

    public boolean isShowOriginalCheckBox() {
        return isShowOriginalCheckBox;
    }

    public void setShowOriginalCheckBox(boolean showOriginalCheckBox) {
        isShowOriginalCheckBox = showOriginalCheckBox;
    }

    public boolean isDefaultOriginal() {
        return isDefaultOriginal;
    }

    public void setDefaultOriginal(boolean defaultOriginal) {
        isDefaultOriginal = defaultOriginal;
    }

    public boolean isCanEditPic() {
        return isCanEditPic;
    }

    public void setCanEditPic(boolean canEditPic) {
        isCanEditPic = canEditPic;
    }

    /**
     * 是否是之前选中过的
     */
    public boolean isLastItem(ImageItem imageItem) {
        if (lastImageList == null || lastImageList.size() == 0) {
            return false;
        }
        return lastImageList.contains(imageItem);
    }

    public boolean isCanPreviewVideo() {
        return isCanPreviewVideo;
    }

    public void setCanPreviewVideo(boolean canPreviewVideo) {
        isCanPreviewVideo = canPreviewVideo;
    }
}
