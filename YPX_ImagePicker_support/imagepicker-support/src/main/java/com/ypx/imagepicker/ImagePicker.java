package com.ypx.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.ypx.imagepicker.activity.PickerActivityManager;
import com.ypx.imagepicker.activity.preview.MultiImagePreviewActivity;
import com.ypx.imagepicker.activity.singlecrop.SingleCropActivity;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.selectconfig.MultiSelectConfig;
import com.ypx.imagepicker.builder.CropPickerBuilder;
import com.ypx.imagepicker.data.MediaItemsDataSource;
import com.ypx.imagepicker.data.MediaSetsDataSource;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.helper.CameraCompat;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.utils.PPermissionUtils;

import java.util.ArrayList;
import java.util.Set;

/**
 * Description: 图片加载启动类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x
 */
public class ImagePicker {
    public static String DEFAULT_FILE_NAME = "imagePicker";
    //选择返回的key
    public static final String INTENT_KEY_PICKER_RESULT = "pickerResult";
    //选择返回code
    public static final int REQ_PICKER_RESULT_CODE = 1433;
    //拍照返回码、拍照权限码
    public static final int REQ_CAMERA = 1431;
    //存储权限码
    public static final int REQ_STORAGE = 1432;

    /**
     * 是否选中原图
     */
    public static boolean isOriginalImage = false;

    private static int themeColor = Color.RED;

    private static boolean previewWithHighQuality = false;

    /**
     * @param previewWithHighQuality 预览是否极致高清，true会导致放大后滑动卡顿，false在加载超过3K图片时，放大后部分像素丢失
     */
    public static void setPreviewWithHighQuality(boolean previewWithHighQuality) {
        ImagePicker.previewWithHighQuality = previewWithHighQuality;
    }

    public static boolean isPreviewWithHighQuality() {
        return previewWithHighQuality;
    }

    /**
     * 小红书样式剪裁activity形式
     *
     * @param presenter 数据交互类
     */
    public static CropPickerBuilder withCrop(IPickerPresenter presenter) {
        return new CropPickerBuilder(presenter);
    }

    /**
     * 微信样式多选
     *
     * @param presenter 选择器UI提供者
     * @return 微信样式多选
     */
    public static MultiPickerBuilder withMulti(IPickerPresenter presenter) {
        return new MultiPickerBuilder(presenter);
    }

    /**
     * 兼容安卓10拍照.因为安卓Q禁止直接写入文件到系统DCIM文件下，所以拍照入参必须是私有目录路径
     * 如果想让拍摄的照片写入外部存储中，则需要copy一份文件到DCIM目录中并刷新媒体库
     *
     * @param activity     调用拍照的页面
     * @param imageName    图片名称
     * @param isCopyInDCIM 是否copy到DCIM中
     * @param listener     拍照回调
     */
    public static void takePhoto(Activity activity,
                                 String imageName,
                                 boolean isCopyInDCIM,
                                 OnImagePickCompleteListener listener) {
        if (imageName == null || imageName.length() == 0) {
            imageName = "Img_" + System.currentTimeMillis();
        }
        CameraCompat.takePhoto(activity, imageName, isCopyInDCIM, listener);
    }

    /**
     * 兼容安卓10拍摄视频.因为安卓Q禁止直接写入文件到系统DCIM文件下，所以拍照入参必须是私有目录路径
     * 如果想让拍摄的照片写入外部存储中，则需要copy一份文件到DCIM目录中并刷新媒体库
     *
     * @param activity     activity
     * @param videoName    视频名称
     * @param maxDuration  视频最大时长
     * @param isCopyInDCIM 是否copy到DCIM中
     * @param listener     视频回调
     */
    public static void takeVideo(Activity activity,
                                 String videoName,
                                 long maxDuration,
                                 boolean isCopyInDCIM,
                                 OnImagePickCompleteListener listener) {
        if (videoName == null || videoName.length() == 0) {
            videoName = "Video_" + System.currentTimeMillis();
        }
        CameraCompat.takeVideo(activity, videoName, maxDuration, isCopyInDCIM, listener);
    }


    /**
     * 直接调用拍照并剪裁
     *
     * @param activity   调用activity
     * @param presenter  选择器样式类，主要负责返回UIConfig
     * @param cropConfig 剪裁配置
     * @param listener   剪裁回调
     */
    public static void takePhotoAndCrop(final Activity activity,
                                        final IPickerPresenter presenter,
                                        final CropConfig cropConfig,
                                        @NonNull final OnImagePickCompleteListener listener) {
        if (presenter == null) {
            PickerErrorExecutor.executeError(activity, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        if (cropConfig == null) {
            PickerErrorExecutor.executeError(activity, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return;
        }
        takePhoto(activity, null, false, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                if (items != null && items.size() > 0) {
                    SingleCropActivity.intentCrop(activity, presenter, cropConfig, items.get(0), listener);
                }
            }
        });
    }

    /**
     * 直接调用拍照并剪裁
     *
     * @param activity      调用activity
     * @param presenter     选择器样式类，主要负责返回UIConfig
     * @param cropConfig    剪裁配置
     * @param cropImagePath 需要剪裁的图片路径,可以是uri路径
     * @param listener      剪裁回调
     */
    public static void crop(final Activity activity, final IPickerPresenter presenter,
                            final CropConfig cropConfig, String cropImagePath,
                            final OnImagePickCompleteListener listener) {
        if (presenter == null || cropConfig == null || listener == null) {
            PickerErrorExecutor.executeError(activity, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        SingleCropActivity.intentCrop(activity, presenter, cropConfig, cropImagePath, listener);
    }

    /**
     * 直接调用拍照并剪裁
     *
     * @param activity   调用activity
     * @param presenter  选择器样式类，主要负责返回UIConfig
     * @param cropConfig 剪裁配置
     * @param imageItem  需要剪裁的图片信息
     * @param listener   剪裁回调
     */
    public static void crop(final Activity activity, final IPickerPresenter presenter,
                            final CropConfig cropConfig, ImageItem imageItem,
                            final OnImagePickCompleteListener listener) {
        if (presenter == null || cropConfig == null || listener == null) {
            PickerErrorExecutor.executeError(activity, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        SingleCropActivity.intentCrop(activity, presenter, cropConfig, imageItem, listener);
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
    public static <T> void preview(Activity context, final IPickerPresenter presenter, ArrayList<T> imageList,
                                   int pos, final OnImagePickCompleteListener listener) {
        if (imageList == null || imageList.size() == 0) {
            return;
        }
        MultiSelectConfig selectConfig = new MultiSelectConfig();
        selectConfig.setMaxCount(imageList.size());
        MultiImagePreviewActivity.intent(context, null, transitArray(context, imageList),
                selectConfig, presenter, pos, new MultiImagePreviewActivity.PreviewResult() {
                    @Override
                    public void onResult(ArrayList<ImageItem> imageItems, boolean isCancel) {
                        if (listener != null) {
                            if (isCancel && listener instanceof OnImagePickCompleteListener2) {
                                ((OnImagePickCompleteListener2) listener).onPickFailed(PickerError.CANCEL);
                            } else {
                                listener.onImagePickComplete(imageItems);
                            }
                        }
                    }
                });
    }

    /**
     * @param imageList 需要转化的list
     * @param <T>       ImageItem or String
     * @return 转化后可识别的item列表
     */
    public static <T> ArrayList<ImageItem> transitArray(Activity activity, ArrayList<T> imageList) {
        ArrayList<ImageItem> items = new ArrayList<>();
        for (T t : imageList) {
            if (t instanceof String) {
                ImageItem imageItem = ImageItem.withPath(activity, (String) t);
                items.add(imageItem);
            } else if (t instanceof ImageItem) {
                items.add((ImageItem) t);
            } else if (t instanceof Uri) {
                Uri uri = (Uri) t;
                ImageItem imageItem = new ImageItem();
                imageItem.path = uri.toString();
                imageItem.mimeType = PBitmapUtils.getMimeTypeFromUri(activity, uri);
                imageItem.setVideo(MimeType.isVideo(imageItem.mimeType));
                imageItem.setUriPath(uri.toString());
                items.add(imageItem);
            } else {
                throw new RuntimeException("ImageList item must be instanceof String or Uri or ImageItem");
            }
        }
        return items;
    }

    /**
     * 提供媒体相册列表
     *
     * @param activity    调用activity
     * @param mimeTypeSet 指定相册文件类型
     * @param provider    相回调
     */
    public static void provideMediaSets(FragmentActivity activity,
                                        Set<MimeType> mimeTypeSet,
                                        MediaSetsDataSource.MediaSetProvider provider) {
        if (PPermissionUtils.hasStoragePermissions(activity)) {
            MediaSetsDataSource.create(activity).setMimeTypeSet(mimeTypeSet).loadMediaSets(provider);
        }
    }

    /**
     * 根据相册提供媒体数据
     *
     * @param activity    调用activity
     * @param set         相册文件
     * @param mimeTypeSet 加载类型
     * @param provider    媒体文件回调
     */
    public static void provideMediaItemsFromSet(FragmentActivity activity,
                                                ImageSet set,
                                                Set<MimeType> mimeTypeSet,
                                                MediaItemsDataSource.MediaItemProvider provider) {
        if (PPermissionUtils.hasStoragePermissions(activity)) {
            MediaItemsDataSource.create(activity, set).setMimeTypeSet(mimeTypeSet).loadMediaItems(provider);
        }
    }

    /**
     * 根据相册提供媒体数据，预加载指定数目
     *
     * @param activity        调用activity
     * @param set             相册文件
     * @param mimeTypeSet     加载类型
     * @param preloadSize     预加载个数
     * @param preloadProvider 预加载回调
     * @param provider        所有文件回调
     */
    public static void provideMediaItemsFromSetWithPreload(FragmentActivity activity,
                                                           ImageSet set,
                                                           Set<MimeType> mimeTypeSet,
                                                           int preloadSize,
                                                           MediaItemsDataSource.MediaItemPreloadProvider preloadProvider,
                                                           MediaItemsDataSource.MediaItemProvider provider) {
        if (PPermissionUtils.hasStoragePermissions(activity)) {
            MediaItemsDataSource dataSource = MediaItemsDataSource.create(activity, set)
                    .setMimeTypeSet(mimeTypeSet)
                    .preloadSize(preloadSize);
            dataSource.setPreloadProvider(preloadProvider);
            dataSource.loadMediaItems(provider);
        }
    }


    /**
     * 提供所有媒体数据
     *
     * @param activity    调用activity
     * @param mimeTypeSet 加载文件类型
     * @param provider    文件列表回调
     */
    public static void provideAllMediaItems(FragmentActivity activity,
                                            Set<MimeType> mimeTypeSet,
                                            MediaItemsDataSource.MediaItemProvider provider) {
        ImageSet set = new ImageSet();
        set.id = ImageSet.ID_ALL_MEDIA;
        provideMediaItemsFromSet(activity, set, mimeTypeSet, provider);
    }

    /**
     * 清除缓存数据
     *
     * @deprecated
     */
    public static void clearAllCache() {

    }

    /**
     * 关闭选择器并回调数据
     *
     * @param list 回调数组
     */
    public static void closePickerWithCallback(ArrayList<ImageItem> list) {
        Activity activity = PickerActivityManager.getLastActivity();
        if (activity == null || list == null || list.size() == 0) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, list);
        activity.setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        activity.finish();
        PickerActivityManager.clear();
    }

    /**
     * 关闭选择器并回调数据
     *
     * @param imageItem 回调数据
     */
    public static void closePickerWithCallback(ImageItem imageItem) {
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        imageItems.add(imageItem);
        closePickerWithCallback(imageItems);
    }

    public static int getThemeColor() {
        return themeColor;
    }

    public static void setThemeColor(int themeColor) {
        ImagePicker.themeColor = themeColor;
    }
}
