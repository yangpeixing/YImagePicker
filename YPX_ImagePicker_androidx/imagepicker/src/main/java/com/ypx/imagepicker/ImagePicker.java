package com.ypx.imagepicker;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.utils.PDateUtil;
import com.ypx.imagepicker.utils.PPermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

/**
 * Description: 图片加载启动类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
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
     * 直接调用拍照，默认图片存储路径为DCIM目录下Camera文件夹下
     *
     * @param activity 调用activity
     * @param listener 拍照回调
     */
    public static void takePhoto(Activity activity, OnImagePickCompleteListener listener) {
        String fileName = "IMG_" + System.currentTimeMillis();
        String path = PBitmapUtils.getDCIMDirectory().getAbsolutePath() +
                File.separator + fileName + ".jpg";
        takePhoto(activity, path, listener);
    }

    /**
     * 直接调用摄像头拍视频，默认视频存储路径为DCIM目录下Camera文件夹下
     *
     * @param activity 调用activity
     * @param listener 视频回调
     */
    public static void takeVideo(Activity activity, OnImagePickCompleteListener listener) {
        String fileName = "VIDEO_" + System.currentTimeMillis();
        String path = PBitmapUtils.getDCIMDirectory().getAbsolutePath() +
                File.separator + fileName + ".mp4";
        takeVideo(activity, path, listener);
    }

    /**
     * 直接调用拍照
     *
     * @param activity 调用activity
     * @param savePath 照片保存路径+文件名
     * @param listener 拍照回调
     */
    public static void takePhoto(final Activity activity, final String savePath, final OnImagePickCompleteListener listener) {
        if (!PPermissionUtils.hasCameraPermissions(activity) || listener == null) {
            return;
        }
        PLauncher.init(activity).startActivityForResult(PBitmapUtils.getTakePhotoIntent(activity, savePath), new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK || savePath == null || savePath.trim().length() == 0) {
                    PickerErrorExecutor.executeError(listener, PickerError.TAKE_PHOTO_FAILED.getCode());
                    return;
                }
                PBitmapUtils.refreshGalleryAddPic(activity, savePath);
                ImageItem item = new ImageItem();
                item.path = savePath;
                item.mimeType = MimeType.JPEG.toString();
                item.time = System.currentTimeMillis();
                item.width = PBitmapUtils.getImageWidthHeight(savePath)[0];
                item.height = PBitmapUtils.getImageWidthHeight(savePath)[1];
                item.mimeType = MimeType.JPEG.toString();
                ArrayList<ImageItem> list = new ArrayList<>();
                list.add(item);
                listener.onImagePickComplete(list);
            }
        });
    }

    /**
     * 直接调用摄像头拍视频
     *
     * @param activity activity
     * @param savePath 视频保存路径
     * @param listener 视频回调
     */
    public static void takeVideo(final Activity activity, final String savePath, final OnImagePickCompleteListener listener) {
        if (!PPermissionUtils.hasCameraPermissions(activity) || listener == null) {
            return;
        }
        PLauncher.init(activity).startActivityForResult(PBitmapUtils.getTakeVideoIntent(activity, savePath), new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK || savePath == null || savePath.trim().length() == 0) {
                    PickerErrorExecutor.executeError(listener, PickerError.TAKE_PHOTO_FAILED.getCode());
                    return;
                }
                PBitmapUtils.refreshGalleryAddPic(activity, savePath);
                ImageItem item = new ImageItem();
                item.path = savePath;
                item.time = System.currentTimeMillis();
                item.mimeType = MimeType.MP4.toString();
                item.setVideo(true);
                item.duration = PBitmapUtils.getLocalVideoDuration(savePath);
                item.setDurationFormat(PDateUtil.getVideoDuration(item.duration));
                ArrayList<ImageItem> list = new ArrayList<>();
                list.add(item);
                listener.onImagePickComplete(list);
            }
        });
    }


    /**
     * 直接调用拍照并剪裁
     *
     * @param activity   调用activity
     * @param presenter  选择器样式类，主要负责返回UIConfig
     * @param cropConfig 剪裁配置
     * @param listener   剪裁回调
     */
    public static void takePhotoAndCrop(final Activity activity, final IPickerPresenter presenter,
                                        final CropConfig cropConfig, @NonNull final OnImagePickCompleteListener listener) {
        if (presenter == null) {
            PickerErrorExecutor.executeError(activity, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        if (cropConfig == null) {
            PickerErrorExecutor.executeError(activity, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return;
        }
        takePhoto(activity, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                if (items != null && items.size() > 0) {
                    SingleCropActivity.intentCrop(activity, presenter, cropConfig, items.get(0).path, listener);
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
     * @param cropImagePath 需要剪裁的图片路径
     * @param listener      剪裁回调
     */
    public static void crop(final Activity activity, final IPickerPresenter presenter,
                            final CropConfig cropConfig, String cropImagePath, final OnImagePickCompleteListener listener) {
        if (presenter == null || cropConfig == null || listener == null) {
            PickerErrorExecutor.executeError(activity, PickerError.PRESENTER_NOT_FOUND.getCode());
            return;
        }
        SingleCropActivity.intentCrop(activity, presenter, cropConfig, cropImagePath, listener);
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
        MultiImagePreviewActivity.intent(context, null, transitArray(imageList),
                selectConfig, presenter, pos, new MultiImagePreviewActivity.PreviewResult() {
                    @Override
                    public void onResult(ArrayList<ImageItem> imageItems, boolean isCancel) {
                        if (listener != null) {
                            listener.onImagePickComplete(imageItems);
                        }
                    }
                });
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
}
