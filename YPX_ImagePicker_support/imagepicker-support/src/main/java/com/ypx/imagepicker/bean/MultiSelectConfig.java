package com.ypx.imagepicker.bean;

import java.util.ArrayList;

/**
 * Description: 多选配置项
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiSelectConfig extends CropConfig {
    private boolean isShowOriginalCheckBox;
    private boolean isCanEditPic;
    private boolean isPreview = true;
    private int selectMode = SelectMode.MODE_MULTI;
    private ArrayList<ImageItem> lastImageList = new ArrayList<>();
    private ArrayList<ImageItem> shieldImageList = new ArrayList<>();

    public boolean isPreview() {
        return isPreview;
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
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
