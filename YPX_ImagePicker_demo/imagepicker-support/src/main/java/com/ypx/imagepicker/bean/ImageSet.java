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
    public String name;
    public String path;
    public ImageItem cover;
    public ArrayList<ImageItem> imageItems;
    public boolean isSelected = false;

    @Override
    public boolean equals(Object o) {
        ImageSet other = (ImageSet) o;
        if (this == o) {
            return true;
        }
        if (this.name != null && other != null && other.name != null) {
            return this.name.equals(other.name);
        }
        return super.equals(o);
    }

    public ImageSet copy() {
        ImageSet imageSet = new ImageSet();
        imageSet.name = this.name;
        imageSet.path = this.path;
        imageSet.cover = this.cover;
        imageSet.isSelected = this.isSelected;
        imageSet.imageItems = new ArrayList<>();
        imageSet.imageItems.addAll(this.imageItems);
        return imageSet;
    }

}
