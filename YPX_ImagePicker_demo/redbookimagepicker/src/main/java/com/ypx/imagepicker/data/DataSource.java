package com.ypx.imagepicker.data;

/**
 * Description: 数据加载接口
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public interface DataSource {
    void provideMediaItems(OnImagesLoadedListener loadedListener);
}
