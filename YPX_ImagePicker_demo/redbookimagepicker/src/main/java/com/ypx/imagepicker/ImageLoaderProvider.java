package com.ypx.imagepicker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Description: 图片加载提供类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface ImageLoaderProvider extends Serializable {

    void displayListImage(ImageView imageView, String url);

    void displayCropImage(ImageView imageView, String url);

    View getBottomView(Context context);

    void showDraftDialog(Context context);

}
