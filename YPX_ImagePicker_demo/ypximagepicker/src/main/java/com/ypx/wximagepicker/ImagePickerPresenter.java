package com.ypx.wximagepicker;

import android.content.Context;
import android.content.Intent;

import com.ypx.wximagepicker.config.IImgPickerUIConfig;
import com.ypx.wximagepicker.config.ImgPickerSelectConfig;
import com.ypx.wximagepicker.data.ImagePickerData;
import com.ypx.wximagepicker.interf.ImageSelectMode;
import com.ypx.wximagepicker.interf.OnImagePickCompleteListener;
import com.ypx.wximagepicker.ui.activity.YPXImageGridActivity;

import java.util.List;

/**
 * 作者：yangpeixing on 2018/9/19 16:56
 * 功能：图片选择器执行类
 * 产权：南京婚尚信息技术
 */
public class ImagePickerPresenter {
    private ImgPickerSelectConfig imgPickerSelectConfig;
    private IImgPickerUIConfig iImgPickerUIConfig;

    ImagePickerPresenter(IImgPickerUIConfig iImgPickerUIConfig) {
        this.iImgPickerUIConfig = iImgPickerUIConfig;
        this.imgPickerSelectConfig = new ImgPickerSelectConfig();
    }

    public IImgPickerUIConfig getiImagePickerConfig() {
        return iImgPickerUIConfig;
    }

    public ImgPickerSelectConfig getImgPickerSelectConfig() {
        return imgPickerSelectConfig;
    }

    public void pick(Context context, OnImagePickCompleteListener listener) {
        imgPickerSelectConfig.setSelectMode(imgPickerSelectConfig.getSelectLimit() > 1 ?
                ImageSelectMode.MODE_MULTI : ImageSelectMode.MODE_SINGLE);
        ImagePickerData.onImagePickCompleteListener = listener;
        Intent intent = new Intent(context, YPXImageGridActivity.class);
        intent.putExtra("ImgPickerSelectConfig", imgPickerSelectConfig);
        intent.putExtra("IImgPickerUIConfig", iImgPickerUIConfig);
        context.startActivity(intent);
    }

    public void crop(Context context,OnImagePickCompleteListener listener) {
        imgPickerSelectConfig.setSelectMode(ImageSelectMode.MODE_CROP);
        ImagePickerData.onImagePickCompleteListener = listener;
        Intent intent = new Intent(context, YPXImageGridActivity.class);
        intent.putExtra("ImgPickerSelectConfig", imgPickerSelectConfig);
        intent.putExtra("IImgPickerUIConfig", iImgPickerUIConfig);
        context.startActivity(intent);
    }


    public void takePhoto(Context context, OnImagePickCompleteListener listener) {
        imgPickerSelectConfig.setSelectMode(ImageSelectMode.MODE_TAKEPHOTO);
        ImagePickerData.onImagePickCompleteListener = listener;
        Intent intent = new Intent(context, YPXImageGridActivity.class);
        intent.putExtra("ImgPickerSelectConfig", imgPickerSelectConfig);
        intent.putExtra("IImgPickerUIConfig", iImgPickerUIConfig);
        context.startActivity(intent);
    }

    public void preview(List<String> urls) {

    }


    public ImagePickerPresenter selectLimit(int selectLimit) {
        imgPickerSelectConfig.setSelectLimit(selectLimit);
        return this;
    }

    public ImagePickerPresenter columnCount(int columnCount) {
        imgPickerSelectConfig.setColumnCount(columnCount);
        return this;
    }


    public ImagePickerPresenter showCamera(boolean showCamera) {
        imgPickerSelectConfig.setShowCamera(showCamera);
        return this;
    }

    public ImagePickerPresenter showOriginalCheckBox(boolean showOriginalCheckBox) {
        imgPickerSelectConfig.setShowOriginalCheckBox(showOriginalCheckBox);
        return this;
    }


    public ImagePickerPresenter canEditPic(boolean canEditPic) {
        imgPickerSelectConfig.setCanEditPic(canEditPic);
        return this;
    }

}
