package com.ypx.imagepicker.data;


import com.ypx.imagepicker.bean.ImageSet;

import java.util.List;

/**
 * Description: 数据加载完成回调
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface OnImagesLoadedListener {
    void onImagesLoaded(List<ImageSet> imageSetList);
}
