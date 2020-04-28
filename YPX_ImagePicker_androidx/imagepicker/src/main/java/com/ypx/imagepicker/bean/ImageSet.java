package com.ypx.imagepicker.bean;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Description: 文件夹信息
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImageSet implements Serializable {
    public static final String ID_ALL_MEDIA = "-1";
    public static final String ID_ALL_VIDEO = "-2";
    public String id;
    public String name;
    public String coverPath;
    public int count;
    public ImageItem cover;
    public ArrayList<ImageItem> imageItems;
    public boolean isSelected = false;

    @Override
    public boolean equals(Object o) {
        ImageSet other = (ImageSet) o;
        if (this == o) {
            return true;
        }
        if (this.id != null && other != null && other.id != null) {
            return this.id.equals(other.id);
        }
        return super.equals(o);
    }

    public ImageSet copy() {
        ImageSet imageSet = new ImageSet();
        imageSet.name = this.name;
        imageSet.coverPath = this.coverPath;
        imageSet.cover = this.cover;
        imageSet.isSelected = this.isSelected;
        imageSet.imageItems = new ArrayList<>();
        if (this.imageItems != null) {
            imageSet.imageItems.addAll(this.imageItems);
        }
        return imageSet;
    }

    public ImageSet copy(boolean isFilterVideo) {
        ImageSet imageSet = new ImageSet();
        imageSet.name = this.name;
        imageSet.coverPath = this.coverPath;
        imageSet.cover = this.cover;
        imageSet.isSelected = this.isSelected;
        imageSet.imageItems = new ArrayList<>();
        if (imageItems != null && imageItems.size() > 0) {
            for (ImageItem item : this.imageItems) {
                if (isFilterVideo && item.isVideo()) {
                    continue;
                }
                ImageItem newItem = item.copy();
                imageSet.imageItems.add(newItem);
            }
        }
        return imageSet;
    }

    public static ImageSet allImageSet(String name) {
        ImageSet imageSet = new ImageSet();
        imageSet.id = ImageSet.ID_ALL_MEDIA;
        imageSet.name = name;
        return imageSet;
    }

    public boolean isAllMedia() {
        return id == null || id.equals(ID_ALL_MEDIA);
    }

    public boolean isAllVideo() {
        return id != null && id.equals(ID_ALL_VIDEO);
    }

}
