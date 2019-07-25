package com.ypx.imagepicker.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        try {
            ImageSet other = (ImageSet) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
