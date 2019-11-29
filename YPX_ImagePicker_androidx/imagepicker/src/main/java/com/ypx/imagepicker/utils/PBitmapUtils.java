package com.ypx.imagepicker.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;

import com.ypx.imagepicker.ImagePicker;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Time: 2019/7/17 14:16
 * Author:ypx
 * Description:文件工具类
 */
public class PBitmapUtils {
    /**
     * 根据相对路径获取图片宽高
     *
     * @param c   上下文
     * @param uri 图片uri地址
     * @return 宽高信息
     */

    public static int[] getImageWidthHeight(Context c, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = c.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                return new int[]{image.getWidth(), image.getHeight()};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new int[]{0, 0};
    }

    /**
     * 根据绝对路径得到图片的宽高，亲测比楼上速度快几十倍
     *
     * @param path 绝对路径！绝对路径！绝对路径！
     * @return 宽高
     */
    public static int[] getImageWidthHeight(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            return new int[]{options.outWidth, options.outHeight};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{0, 0};
    }


    /**
     * @param context 上下文
     * @return 获取app私有目录
     */
    public static File getPickerFileDirectory(Context context) {
        File file = new File(context.getExternalFilesDir(null), ImagePicker.DEFAULT_FILE_NAME);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file;
            }
        }
        return file;
    }

    /**
     * 获取系统相册文件路径
     */
    public static File getDCIMDirectory() {
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (!dcim.exists()) {
            if (dcim.mkdir()) {
                return dcim;
            }
        }
        return dcim;
    }


    /**
     * androidQ中默认项目私有文件只能存储在Android/data/包名/files/下
     *
     * @param context        上下文
     * @param bitmap         要保存的bitmap
     * @param fileName       图片名称
     * @param compressFormat 图片格式
     * @return 该图片的绝对路径，不是Uri相对路径
     */
    public static String saveBitmapToFile(Context context,
                                          Bitmap bitmap,
                                          String fileName,
                                          Bitmap.CompressFormat compressFormat) {

        File file = getPickerFileDirectory(context);
        file = new File(file, fileName + "." + compressFormat.toString().toLowerCase());
        try {
            FileOutputStream b = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            b.flush();
            b.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            if (file.exists()) {
                file.delete();
            }
            return "Exception:" + e.getMessage();
        }
    }

    /**
     * androidQ方式保存一张bitmap到DCIM根目录下
     *
     * @param context        当前context
     * @param bitmap         当前要生成的bitmap
     * @param fileName       图片名称
     * @param compressFormat 图片格式
     * @return 此图片的Uri
     */
    public static Uri saveBitmapToDICM(Context context,
                                       Bitmap bitmap,
                                       String fileName,
                                       Bitmap.CompressFormat compressFormat) {

        //设置保存参数到ContentValues中
        ContentValues contentValues = new ContentValues();
        //兼容Android Q和以下版本
        if (Build.VERSION.SDK_INT >= 29) {
            //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
            //RELATIVE_PATH是相对路径不是绝对路径
            //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM +
                    File.separator + ImagePicker.DEFAULT_FILE_NAME);
        } else {
            File outputFolder = getDCIMDirectory();
            String suffix = "." + compressFormat.toString().toLowerCase();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.DATA, outputFolder.getAbsolutePath()
                    + File.separator + fileName + suffix);
        }
        //设置文件类型
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/" + compressFormat.toString());
        contentValues.put(MediaStore.Files.FileColumns.WIDTH, bitmap.getWidth());
        contentValues.put(MediaStore.Files.FileColumns.HEIGHT, bitmap.getHeight());
        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            //若生成了uri，则表示该文件添加成功
            //使用流将内容写入该uri中即可
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(compressFormat, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    return uri;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return uri;
            }
        }

        return uri;
    }


    /**
     * @return view的截图，在InVisible时也可以获取到bitmap
     */
    public static Bitmap getViewBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        return view.getDrawingCache(true);
    }


    /**
     * 获取视频封面
     */
    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    /**
     * 获取视频时长
     */
    public static int getLocalVideoDuration(String videoPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }

    /**
     * 刷新相册
     */
    public static void refreshGalleryAddPic(Context context, String path) {
        if (context == null) {
            return;
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static Intent getTakePhotoIntent(Activity activity, String savePath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Uri imageUri = PickerFileProvider.getUriForFile(activity, new File(savePath));
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

    public static Intent getTakeVideoIntent(Activity activity, String savePath) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Uri imageUri = PickerFileProvider.getUriForFile(activity, new File(savePath));
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

}
