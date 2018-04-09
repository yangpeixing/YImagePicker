package com.ypx.imagepicker.bean;

import java.io.Serializable;

/**
 * Created by Eason.Lai on 2015/11/1 10:42
 * contact：easonline7@gmail.com
 */
public class ImageItem implements Serializable {
    public String path;
    public String name;
    public int width;
    public int height;
    public long time;

    public ImageItem(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public ImageItem(String path, String name, int width, int height, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageItem other = (ImageItem) o;
            return this.path.equalsIgnoreCase(other.path) && this.time == other.time;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
