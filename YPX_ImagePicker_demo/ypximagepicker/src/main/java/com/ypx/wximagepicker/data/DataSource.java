package com.ypx.wximagepicker.data;

/**
 * <b>DataSource of imagePicker</b><br/>
 * data can be from network source or android local database<br/>
 * Created by Eason.Lai on 2015/11/1 10:42 <br/>
 * contact：easonline7@gmail.com <br/>
 */
public interface DataSource {
    void provideMediaItems(OnImagesLoadedListener loadedListener);
}
