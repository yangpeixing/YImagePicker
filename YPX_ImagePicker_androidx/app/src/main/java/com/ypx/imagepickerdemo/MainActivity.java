package com.ypx.imagepickerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepickerdemo.style.RedBookPresenter;
import com.ypx.imagepickerdemo.style.custom.CustomImgPickerPresenter;
import com.ypx.imagepickerdemo.style.WeChatPresenter;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements MainActivityView.MainViewCallBack {

    private MainActivityView mainActivityView;
    private WeChatPresenter weChatPresenter;
    private RedBookPresenter redBookPresenter;
    private CustomImgPickerPresenter customImgPickerPresenter;

    public static boolean isAutoJumpAlohaActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weChatPresenter = new WeChatPresenter();
        redBookPresenter = new RedBookPresenter();
        customImgPickerPresenter = new CustomImgPickerPresenter();
        mainActivityView = MainActivityView.create(this, this);
    }

    @Override
    public void weChatPick(int count) {
        boolean isCustom = mainActivityView.isCustom();
        boolean isShowOriginal = mainActivityView.isShowOriginal();
        Set<MimeType> mimeTypes = mainActivityView.getMimeTypes();
        int selectMode = mainActivityView.getSelectMode();
        boolean isCanPreviewVideo = mainActivityView.isCanPreviewVideo();
        boolean isShowCamera = mainActivityView.isShowCamera();
        boolean isPreviewEnable = mainActivityView.isPreviewEnable();
        boolean isVideoSinglePick = mainActivityView.isVideoSinglePick();
        boolean isSinglePickWithAutoComplete = mainActivityView.isSinglePickWithAutoComplete();
        boolean isSinglePickImageOrVideoType = mainActivityView.isSinglePickImageOrVideoType();
        ArrayList<ImageItem> resultList = mainActivityView.getPicList();
        boolean isCheckLastImageList = mainActivityView.isCheckLastImageList();
        boolean isCheckShieldList = mainActivityView.isCheckShieldList();

        IPickerPresenter presenter = isCustom ? customImgPickerPresenter : weChatPresenter;
        ImagePicker.withMulti(presenter)//指定presenter
                .setMaxCount(count)//设置选择的最大数
                .setColumnCount(4)//设置列数
                .setOriginal(isShowOriginal)
                .mimeTypes(mimeTypes)//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSelectMode(selectMode)
                .setDefaultOriginal(false)
                .setPreviewVideo(isCanPreviewVideo)
                .showCamera(isShowCamera)//显示拍照
                .showCameraOnlyInAllMediaSet(true)
                .setPreview(isPreviewEnable)//是否开启预览
                .setVideoSinglePick(isVideoSinglePick)//设置视频单选
                .setSinglePickWithAutoComplete(isSinglePickWithAutoComplete)
                .setSinglePickImageOrVideoType(isSinglePickImageOrVideoType)//设置图片和视频单一类型选择
                .setMaxVideoDuration(120000L)//设置视频可选取的最大时长
                .setMinVideoDuration(5000L)
                .setSingleCropCutNeedTop(true)
                //设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
                .setLastImageList(isCheckLastImageList ? resultList : null)
                //设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .setShieldList(isCheckShieldList ? resultList : null)
                .pick(this, new OnImagePickCompleteListener2() {
                    @Override
                    public void onPickFailed(PickerError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片选择回调，主线程
                        mainActivityView.notifyImageItemsCallBack(items);
                    }
                });

    }

    @Override
    public void redBookPick(int count) {
        boolean isShowCamera = mainActivityView.isShowCamera();
        Set<MimeType> mimeTypes = mainActivityView.getMimeTypes();
        ArrayList<ImageItem> resultList = mainActivityView.getPicList();
        boolean isVideoSinglePick = mainActivityView.isVideoSinglePick();
        boolean isSinglePickWithAutoComplete = mainActivityView.isSinglePickWithAutoComplete();

        ImagePicker.withCrop(redBookPresenter)//设置presenter
                .setMaxCount(count)//设置选择数量
                .showCamera(isShowCamera)//设置显示拍照
                .setColumnCount(4)//设置列数
                .mimeTypes(mimeTypes)//设置需要加载的文件类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉的文件类型
                .assignGapState(false)
                .setFirstImageItem(resultList.size() > 0 ? resultList.get(0) : null)//设置上一次选中的图片
                .setVideoSinglePick(isVideoSinglePick)//设置视频单选
                .setSinglePickWithAutoComplete(isSinglePickWithAutoComplete)
                .setMaxVideoDuration(120000L)//设置可选区的最大视频时长
                .setMinVideoDuration(5000L)
                .pick(this, new OnImagePickCompleteListener2() {
                    @Override
                    public void onPickFailed(PickerError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片剪裁回调，主线 程
                        //注意：剪裁回调里的ImageItem中getCropUrl()才是剪裁过后的图片地址
                        mainActivityView.notifyImageItemsCallBack(items);
                    }
                });
    }

    @Override
    public void pickAndCrop() {
        boolean isCustom = mainActivityView.isCustom();
        Set<MimeType> mimeTypes = mainActivityView.getMimeTypes();
        boolean isShowCamera = mainActivityView.isShowCamera();
        int minMarginProgress = mainActivityView.getMinMarginProgress();
        boolean isGap = mainActivityView.isGap();
        int cropGapBackgroundColor = mainActivityView.getCropGapBackgroundColor();
        int cropRatioX = mainActivityView.getCropRatioX();
        int cropRatioY = mainActivityView.getCropRatioY();
        boolean isNeedCircle = mainActivityView.isNeedCircle();

        IPickerPresenter presenter = isCustom ? customImgPickerPresenter : weChatPresenter;
        MultiPickerBuilder builder = ImagePicker.withMulti(presenter)//指定presenter
                .setColumnCount(4)//设置列数
                .mimeTypes(mimeTypes)//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .setSingleCropCutNeedTop(true)
                .showCamera(isShowCamera)//显示拍照
                .cropSaveInDCIM(false)
                .cropRectMinMargin(minMarginProgress)
                .cropStyle(isGap ? CropConfig.STYLE_GAP : CropConfig.STYLE_FILL)
                .cropGapBackgroundColor(cropGapBackgroundColor)
                .setCropRatio(cropRatioX, cropRatioY);
        if (isNeedCircle) {
            builder.cropAsCircle();
        }
        builder.crop(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片选择回调，主线程
                mainActivityView.notifyImageItemsCallBack(items);
            }
        });
    }

    @Override
    public void autoCrop() {
        ArrayList<ImageItem> resultList = mainActivityView.getPicList();
        int minMarginProgress = mainActivityView.getMinMarginProgress();
        boolean isGap = mainActivityView.isGap();
        int cropGapBackgroundColor = mainActivityView.getCropGapBackgroundColor();
        int cropRatioX = mainActivityView.getCropRatioX();
        int cropRatioY = mainActivityView.getCropRatioY();
        boolean isNeedCircle = mainActivityView.isNeedCircle();

        if (resultList.size() == 0) {
            Toast.makeText(this, "请至少选择一张图片", Toast.LENGTH_SHORT).show();
            return;
        }

        //配置剪裁属性
        CropConfig cropConfig = new CropConfig();
        cropConfig.setCropRatio(cropRatioX, cropRatioY);//设置剪裁比例
        cropConfig.setCropRectMargin(minMarginProgress);//设置剪裁框间距，单位px
        cropConfig.setCircle(isNeedCircle);//是否圆形剪裁
        cropConfig.setCropStyle(isGap ? CropConfig.STYLE_GAP : CropConfig.STYLE_FILL);
        cropConfig.setCropGapBackgroundColor(cropGapBackgroundColor);
        //用于恢复上一次剪裁状态
        //cropConfig.setCropRestoreInfo();
        ImagePicker.crop(this, new WeChatPresenter(), cropConfig, resultList.get(0).path, new OnImagePickCompleteListener2() {
            @Override
            public void onPickFailed(PickerError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //剪裁回调，主线程
                mainActivityView.notifyImageItemsCallBack(items);
            }
        });
    }

    @Override
    public void takePhoto() {
        String imageName = System.currentTimeMillis() + "";
        boolean isCopyInDCIM = true;
        ImagePicker.takePhoto(this, imageName, isCopyInDCIM, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                mainActivityView.notifyImageItemsCallBack(items);
            }
        });
    }

    @Override
    public void takePhotoAndCrop() {
        int minMarginProgress = mainActivityView.getMinMarginProgress();
        boolean isGap = mainActivityView.isGap();
        int cropGapBackgroundColor = mainActivityView.getCropGapBackgroundColor();
        int cropRatioX = mainActivityView.getCropRatioX();
        int cropRatioY = mainActivityView.getCropRatioY();
        boolean isNeedCircle = mainActivityView.isNeedCircle();
        boolean isCustom = mainActivityView.isCustom();

        //配置剪裁属性
        CropConfig cropConfig = new CropConfig();
        cropConfig.setCropRatio(cropRatioX, cropRatioY);//设置剪裁比例
        cropConfig.setCropRectMargin(minMarginProgress);//设置剪裁框间距，单位px
        cropConfig.setCircle(isNeedCircle);//是否圆形剪裁
        cropConfig.setCropStyle(isGap ? CropConfig.STYLE_GAP : CropConfig.STYLE_FILL);
        cropConfig.setCropGapBackgroundColor(cropGapBackgroundColor);

        IPickerPresenter presenter = isCustom ? customImgPickerPresenter : weChatPresenter;
        ImagePicker.takePhotoAndCrop(this, presenter, cropConfig, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //剪裁回调，主线程
                mainActivityView.notifyImageItemsCallBack(items);
            }
        });
    }

    @Override
    public void preview(int pos) {
        final ArrayList<ImageItem> resultList = mainActivityView.getPicList();
        IPickerPresenter presenter = mainActivityView.isCustom() ? customImgPickerPresenter
                : weChatPresenter;

        //这一段是为了解决预览加载的是原图而不是剪裁的图片，做的兼融处理，实际调用请删除这一段
        ArrayList<String> list = new ArrayList<>();
        for (ImageItem imageItem : resultList) {
            if (imageItem.getCropUrl() != null && imageItem.getCropUrl().length() > 0) {
                list.add(imageItem.getCropUrl());
            } else {
                list.add(imageItem.path);
            }
        }

        ImagePicker.preview(this, presenter, list, pos, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片编辑回调，主线程
                resultList.clear();
                mainActivityView.notifyImageItemsCallBack(items);
            }
        });
    }
}
