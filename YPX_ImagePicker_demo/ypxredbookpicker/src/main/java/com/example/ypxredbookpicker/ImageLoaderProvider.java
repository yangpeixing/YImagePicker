package com.example.ypxredbookpicker;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface ImageLoaderProvider extends Serializable {

    void displayListImage(ImageView imageView, String url, int size);

}
