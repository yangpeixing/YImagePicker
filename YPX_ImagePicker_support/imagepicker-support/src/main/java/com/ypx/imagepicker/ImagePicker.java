package com.ypx.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import com.ypx.imagepicker.activity.multi.MultiImagePreviewActivity;
import com.ypx.imagepicker.activity.multi.SingleCropActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.builder.CropPickerBuilder;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.utils.PFileUtil;
import com.ypx.imagepicker.utils.PPermissionUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * Description: 图片加载启动类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public class ImagePicker {
    /**
     * 可以选取的视频最大时长
     */
    public static long MAX_VIDEO_DURATION = 120000;
    //选择返回的key
    public static final String INTENT_KEY_PICKER_RESULT = "pickerResult";
    //选择返回code
    public static final int REQ_PICKER_RESULT_CODE = 1433;
    //拍照返回码、拍照权限码
    public static final int REQ_CAMERA = 1431;
    //存储权限码
    public static final int REQ_STORAGE = 1432;

    /**
     * 图片剪裁的保存路径
     */
    public static String cropPicSaveFilePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "Crop" + File.separator;

    /**
     * 小红书样式剪裁activity形式
     *
     * @param bindPresenter 数据交互类
     */
    public static CropPickerBuilder withCrop(ICropPickerBindPresenter bindPresenter) {
        return new CropPickerBuilder(bindPresenter);
    }

    /**
     * 微信样式多选
     *
     * @param iMultiPickerBindPresenter 选择器UI提供者
     * @return 微信样式多选
     */
    public static MultiPickerBuilder withMulti(IMultiPickerBindPresenter iMultiPickerBindPresenter) {
        return new MultiPickerBuilder(iMultiPickerBindPresenter);
    }

    /**
     * 直接调用拍照
     *
     * @param activity 调用activity
     * @param savePath 照片保存路径+文件名
     * @param listener 拍照回调
     */
    public static void takePhoto(final Activity activity, final String savePath, final OnImagePickCompleteListener listener) {
        if (!PPermissionUtils.hasCameraPermissions(activity)) {
            return;
        }
        PLauncher.init(activity).startActivityForResult(PFileUtil.getTakePhotoIntent(activity, savePath), new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                PFileUtil.refreshGalleryAddPic(activity, savePath);
                if (listener != null) {
                    ImageItem item = new ImageItem(savePath, System.currentTimeMillis());
                    item.width = PFileUtil.getImageWidthHeight(savePath)[0];
                    item.height = PFileUtil.getImageWidthHeight(savePath)[1];
                    ArrayList<ImageItem> list = new ArrayList<>();
                    list.add(item);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }

    /**
     * 直接调用拍照，默认图片存储路径为DCIM目录下Camera文件夹下
     *
     * @param activity 调用activity
     * @param listener 拍照回调
     */
    public static void takePhoto(Activity activity, OnImagePickCompleteListener listener) {
        String fileName = "IMG_" + System.currentTimeMillis();
        String path = PFileUtil.getDCIMOutputPath(fileName, ".jpg");
        takePhoto(activity, path, listener);
    }


    /**
     * 直接调用拍照并剪裁
     *
     * @param activity     调用activity
     * @param presenter    选择器样式类，主要负责返回UIConfig
     * @param selectConfig 剪裁配置
     * @param listener     剪裁回调
     */
    public static void takePhotoAndCrop(final Activity activity, final IMultiPickerBindPresenter presenter,
                                        final MultiSelectConfig selectConfig, final OnImagePickCompleteListener listener) {
        if (presenter == null || selectConfig == null || listener == null) {
            return;
        }
        takePhoto(activity, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                if (items != null && items.size() > 0) {
                    SingleCropActivity.intentCrop(activity, presenter, selectConfig, items.get(0).path, listener);
                }
            }
        });
    }

    /**
     * 图片预览
     *
     * @param context   上下文
     * @param imageList 预览的图片数组
     * @param pos       默认位置
     * @param listener  编辑回调
     * @param <T>       String or ImageItem
     */
    public static <T> void preview(Activity context, final IMultiPickerBindPresenter presenter, ArrayList<T> imageList,
                                   int pos, OnImagePickCompleteListener listener) {
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        MultiPickerData.instance.clear();
        MultiImagePreviewActivity.intent(context, null, presenter, false,
                transitArray(imageList), pos, listener);
    }

    /**
     * @param imageList 需要转化的list
     * @param <T>       ImageItem or String
     * @return 转化后可识别的item列表
     */
    private static <T> ArrayList<ImageItem> transitArray(ArrayList<T> imageList) {
        ArrayList<ImageItem> items = new ArrayList<>();
        for (T t : imageList) {
            if (t instanceof String) {
                ImageItem imageItem = new ImageItem();
                imageItem.path = (String) t;
                items.add(imageItem);
            } else if (t instanceof ImageItem) {
                items.add((ImageItem) t);
            } else {
                throw new RuntimeException("ImageList item must be instanceof String or ImageItem");
            }
        }
        return items;
    }

    /**
     * 清除缓存数据
     */
    public static void clearAllCache() {
        MultiPickerData.instance.clear();
    }
}
