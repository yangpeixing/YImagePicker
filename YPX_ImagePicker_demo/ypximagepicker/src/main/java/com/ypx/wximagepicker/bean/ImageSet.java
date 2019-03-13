package com.ypx.wximagepicker.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Eason.Lai on 2015/11/1 10:42
 * contact：easonline7@gmail.com
 */
public class ImageSet implements Serializable {
    public String name;
    public String path;
    public SimpleImageItem cover;
    public List<SimpleImageItem> simpleImageItems;

    @Override
    public boolean equals(Object o) {
        try {
            ImageSet other = (ImageSet) o;
            return this.path.equalsIgnoreCase(other.path) && this.name.equalsIgnoreCase(other.name);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
