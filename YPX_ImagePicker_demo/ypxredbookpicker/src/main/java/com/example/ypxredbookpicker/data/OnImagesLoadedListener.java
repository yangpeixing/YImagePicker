package com.example.ypxredbookpicker.data;


import com.example.ypxredbookpicker.bean.ImageSet;

import java.util.List;

/**
 * <b>Listener when data ready</b><br/>
 * Created by Eason.Lai on 2015/11/1 10:42 <br/>
 * contact：easonline7@gmail.com <br/>
 */
public interface OnImagesLoadedListener {
    void onImagesLoaded(List<ImageSet> imageSetList);
}
