package com.ypx.imagepicker.views;

import android.content.Context;

import com.ypx.imagepicker.views.base.SingleCropControllerView;
import com.ypx.imagepicker.views.wx.WXBottomBar;
import com.ypx.imagepicker.views.wx.WXFolderItemView;
import com.ypx.imagepicker.views.wx.WXItemView;
import com.ypx.imagepicker.views.wx.WXPreviewControllerView;
import com.ypx.imagepicker.views.wx.WXSingleCropControllerView;
import com.ypx.imagepicker.views.wx.WXTitleBar;
import com.ypx.imagepicker.views.base.PickerControllerView;
import com.ypx.imagepicker.views.base.PickerFolderItemView;
import com.ypx.imagepicker.views.base.PickerItemView;
import com.ypx.imagepicker.views.base.PreviewControllerView;


/**
 * Time: 2019/10/27 22:22
 * Author:ypx
 * Description: 选择器UI提供类,默认为微信样式
 */
public class PickerUiProvider {

    public PickerControllerView getTitleBar(Context context) {
        return new WXTitleBar(context);
    }

    public PickerControllerView getBottomBar(Context context) {
        return new WXBottomBar(context);
    }

    public PickerItemView getItemView(Context context) {
        return new WXItemView(context);
    }

    public PickerFolderItemView getFolderItemView(Context context) {
        return new WXFolderItemView(context);
    }

    public PreviewControllerView getPreviewControllerView(Context context) {
        return new WXPreviewControllerView(context);
    }

    public SingleCropControllerView getSingleCropControllerView(Context context) {
        return new WXSingleCropControllerView(context);
    }
}
