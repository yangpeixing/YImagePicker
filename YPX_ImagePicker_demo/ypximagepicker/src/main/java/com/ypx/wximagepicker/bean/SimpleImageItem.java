package com.ypx.wximagepicker.bean;

import java.io.Serializable;

/**
 * Created by Eason.Lai on 2015/11/1 10:42
 * contact：easonline7@gmail.com
 */
public class SimpleImageItem implements Serializable {
    private static final long serialVersionUID = 1L;
    public String path;
    public String name;
    public int width;
    public int height;
    public long time;
    public String timeFormat;

    public SimpleImageItem(){

    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public SimpleImageItem(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    public SimpleImageItem(String path, String name, int width, int height, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object o) {
        try {
            SimpleImageItem other = (SimpleImageItem) o;
            return this.path.equalsIgnoreCase(other.path) && this.time == other.time;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

}
