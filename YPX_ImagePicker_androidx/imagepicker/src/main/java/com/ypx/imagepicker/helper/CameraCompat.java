package com.ypx.imagepicker.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.UriPathInfo;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.utils.PDateUtil;
import com.ypx.imagepicker.utils.PPermissionUtils;
import com.ypx.imagepicker.utils.PSingleMediaScanner;
import com.ypx.imagepicker.utils.PickerFileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CameraCompat {


    /**
     * 兼容安卓10拍照.因为安卓Q禁止直接写入文件到系统DCIM文件下，所以拍照入参必须是私有目录路径
     * 如果想让拍摄的照片写入外部存储中，则需要copy一份文件到DCIM目录中并刷新媒体库
     *
     * @param activity     调用拍照的页面
     * @param imageName    图片名称
     * @param isCopyInDCIM 是否copy到DCIM中
     * @param listener     拍照回调
     */
    public static void takePhoto(final Activity activity,
                                 final String imageName,
                                 final boolean isCopyInDCIM,
                                 final OnImagePickCompleteListener listener) {
        final String path = PBitmapUtils.getPickerFileDirectory(activity).getAbsolutePath() +
                File.separator + imageName + ".jpg";
        if (!PPermissionUtils.hasCameraPermissions(activity) || listener == null) {
            return;
        }
        final Uri imageUri = PickerFileProvider.getUriForFile(activity, new File(path));
        PLauncher.init(activity).startActivityForResult(getTakePhotoIntent(activity, imageUri), new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK || path == null || path.trim().length() == 0) {
                    PickerErrorExecutor.executeError(listener, PickerError.TAKE_PHOTO_FAILED.getCode());
                    return;
                }
                UriPathInfo uriPathInfo;
                if (isCopyInDCIM) {
                    uriPathInfo = PBitmapUtils.copyFileToDCIM(activity, path, imageName, MimeType.JPEG);
                    PSingleMediaScanner.refresh(activity, uriPathInfo.absolutePath, null);
                } else {
                    uriPathInfo = new UriPathInfo(imageUri, path);
                }

                ImageItem item = new ImageItem();
                item.path = uriPathInfo.absolutePath;
                item.mimeType = MimeType.JPEG.toString();
                item.setUriPath(uriPathInfo.uri.toString());
                item.time = System.currentTimeMillis();
                int[] size = PBitmapUtils.getImageWidthHeight(path);
                item.width = size[0];
                item.height = size[1];
                item.mimeType = MimeType.JPEG.toString();
                ArrayList<ImageItem> list = new ArrayList<>();
                list.add(item);
                listener.onImagePickComplete(list);
            }
        });
    }

    /**
     * 兼容安卓10拍摄视频.因为安卓Q禁止直接写入文件到系统DCIM文件下，所以拍照入参必须是私有目录路径
     * 如果想让拍摄的照片写入外部存储中，则需要copy一份文件到DCIM目录中并刷新媒体库
     *
     * @param activity     activity
     * @param videoName    视频保存路径
     * @param maxDuration  视频最大时长
     * @param isCopyInDCIM 是否copy到DCIM中
     * @param listener     视频回调
     */
    public static void takeVideo(final Activity activity,
                                 final String videoName,
                                 long maxDuration,
                                 final boolean isCopyInDCIM,
                                 final OnImagePickCompleteListener listener) {
        if (!PPermissionUtils.hasCameraPermissions(activity) || listener == null) {
            return;
        }
        final String path = PBitmapUtils.getPickerFileDirectory(activity).getAbsolutePath() +
                File.separator + videoName + ".mp4";
        final Uri videoUri = PickerFileProvider.getUriForFile(activity, new File(path));
        PLauncher.init(activity).startActivityForResult(getTakeVideoIntent(activity, videoUri, maxDuration), new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode != Activity.RESULT_OK || path == null || path.trim().length() == 0) {
                    PickerErrorExecutor.executeError(listener, PickerError.TAKE_PHOTO_FAILED.getCode());
                    return;
                }
                UriPathInfo uriPathInfo;
                if (isCopyInDCIM) {
                    uriPathInfo = PBitmapUtils.copyFileToDCIM(activity, path, videoName, MimeType.MP4);
                    PSingleMediaScanner.refresh(activity, uriPathInfo.absolutePath, null);
                } else {
                    uriPathInfo = new UriPathInfo(videoUri, path);
                }

                ImageItem item = new ImageItem();
                item.path = uriPathInfo.absolutePath;
                item.setUriPath(uriPathInfo.uri.toString());
                item.time = System.currentTimeMillis();
                item.mimeType = MimeType.MP4.toString();
                item.setVideo(true);
                item.duration = PBitmapUtils.getLocalVideoDuration(path);
                item.setDurationFormat(PDateUtil.getVideoDuration(item.duration));
                ArrayList<ImageItem> list = new ArrayList<>();
                list.add(item);
                listener.onImagePickComplete(list);
            }
        });
    }

    private static Intent getTakePhotoIntent(Activity activity, Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            if (Build.VERSION.SDK_INT < 21) {
                List<ResolveInfo> resInfoList = activity.getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, imageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return intent;
    }

    private static Intent getTakeVideoIntent(Activity activity, Uri imageUri, long maxDuration) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (Build.VERSION.SDK_INT < 21) {
                List<ResolveInfo> resInfoList = activity.getPackageManager()
                        .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, imageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (maxDuration > 1) {
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, maxDuration / 1000L);
            }
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        return intent;
    }

}
