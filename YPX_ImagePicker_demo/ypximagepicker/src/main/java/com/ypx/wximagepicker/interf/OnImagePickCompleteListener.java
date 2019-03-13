package com.ypx.wximagepicker.interf;

import com.ypx.wximagepicker.bean.SimpleImageItem;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：yangpeixing on 2018/4/3 10:49
 * 功能：图片选择完成回调
 * 产权：南京婚尚信息技术
 */
public interface OnImagePickCompleteListener extends Serializable {
    void onImagePickComplete(List<SimpleImageItem> items);
}
