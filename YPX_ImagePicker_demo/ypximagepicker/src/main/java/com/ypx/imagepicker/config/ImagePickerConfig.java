package com.ypx.imagepicker.config;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.data.ImagePickerData;
import com.ypx.imagepicker.interf.ImageSelectMode;
import com.ypx.imagepicker.interf.ImgLoader;
import com.ypx.imagepicker.interf.OnImagePickCompleteListener;
import com.ypx.imagepicker.ui.activity.YPXImageGridActivity2;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：yangpeixing on 2018/9/19 16:56
 * 功能：图片选择器配置器
 * 产权：南京婚尚信息技术
 */
public class ImagePickerConfig implements Serializable {
    private int selectLimit = 9;
    private int columnCount = 3;
    private boolean isShowCamera = true;
    private boolean isShowOriginalCheckBox;
    private boolean isCanEditPic;

    private boolean isImmersionBar;
    private int themeColor = Color.parseColor("#FF2B82");
    private int cameraIconId = R.mipmap.ypx_ic_camera;
    private int selectMode;
    private int selectIcon = R.mipmap.ypx_pic_selected;
    private int unSelectIcon = R.mipmap.ypx_pic_unselected;
    private ImgLoader imgLoader;

    public ImagePickerConfig(ImgLoader imgLoader) {
        this.imgLoader = imgLoader;
    }

    public ImgLoader getImgLoader() {
        return imgLoader;
    }


    public void pick(Context context, OnImagePickCompleteListener listener) {
        selectMode = selectLimit > 1 ? ImageSelectMode.MODE_MULTI : ImageSelectMode.MODE_SINGLE;
        ImagePickerData.onImagePickCompleteListener = listener;
        Intent intent = new Intent(context, YPXImageGridActivity2.class);
        intent.putExtra("ImagePickerConfig", this);
        context.startActivity(intent);
    }

    public void crop(OnImagePickCompleteListener listener) {
        selectMode = ImageSelectMode.MODE_CROP;
    }


    public void takePhoto(OnImagePickCompleteListener listener) {
        selectMode = ImageSelectMode.MODE_TAKEPHOTO;
    }

    public void preview(List<String> urls) {

    }

    public ImagePickerConfig selectIcon(int selectIcon) {
        this.selectIcon = selectIcon;
        return this;
    }

    public ImagePickerConfig unSelectIcon(int unSelectIcon) {
        this.unSelectIcon = unSelectIcon;
        return this;
    }

    public int getSelectIcon() {
        return selectIcon;
    }

    public int getUnSelectIcon() {
        return unSelectIcon;
    }

    public int getSelectMode() {
        return selectMode;
    }

    public ImagePickerConfig cameraIconId(int cameraIconId) {
        this.cameraIconId = cameraIconId;
        return this;
    }

    public int getCameraIconId() {
        return cameraIconId;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public boolean isShowOriginalCheckBox() {
        return isShowOriginalCheckBox;
    }

    public boolean isCanEditPic() {
        return isCanEditPic;
    }

    public boolean isImmersionBar() {
        return isImmersionBar;
    }

    public int getThemeColor() {
        return themeColor;
    }

    public ImagePickerConfig selectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
        return this;
    }

    public ImagePickerConfig columnCount(int columnCount) {
        this.columnCount = columnCount;
        return this;
    }


    public ImagePickerConfig showCamera(boolean showCamera) {
        isShowCamera = showCamera;
        return this;
    }

    public ImagePickerConfig showOriginalCheckBox(boolean showOriginalCheckBox) {
        isShowOriginalCheckBox = showOriginalCheckBox;
        return this;
    }


    public ImagePickerConfig canEditPic(boolean canEditPic) {
        isCanEditPic = canEditPic;
        return this;
    }


    public ImagePickerConfig immersionBar(boolean immersionBar) {
        isImmersionBar = immersionBar;
        return this;
    }

    public ImagePickerConfig themeColor(int themeColor) {
        this.themeColor = themeColor;
        return this;
    }
}
