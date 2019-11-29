package com.ypx.imagepicker.data;

import com.ypx.imagepicker.bean.ImageItem;

import java.util.List;

public interface IReloadExecutor {

    /**
     * 根据当前选择列表，重新刷新选择器选择状态
     *
     * @param selectedList 当前选中列表
     */
    void reloadPickerWithList(List<ImageItem> selectedList);
}
