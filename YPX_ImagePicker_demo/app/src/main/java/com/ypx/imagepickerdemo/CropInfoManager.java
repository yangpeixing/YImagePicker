package com.ypx.imagepickerdemo;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.widget.browseimage.Info;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Time: 2019/8/16 9:20
 * Author:ypx
 * Description:剪裁属性保存
 */
public enum CropInfoManager {
    instance;

    private HashMap<ImageItem, Info> map = new HashMap<>();

    public void addInfo(ImageItem imageItem, Info info) {
        if (map == null) {
            map = new HashMap<>();
        }
        for (ImageItem item : map.keySet()) {
            if (item.equals(imageItem)) {
                map.remove(item);
                break;
            }
        }
        map.put(imageItem, info);
    }

    public void removeInfo(ImageItem imageItem) {
        if (map == null) {
            return;
        }

        for (ImageItem item : map.keySet()) {
            if (item.equals(imageItem)) {
                map.remove(item);
                return;
            }
        }
    }

    public Info getInfo(ImageItem imageItem) {
        if (map == null) {
            return null;
        }
        for (ImageItem item : map.keySet()) {
            if (item.equals(imageItem)) {
                return map.get(item);
            }
        }

        return null;
    }

    public void clear() {
        if (map != null) {
            map.clear();
        }
    }

}
