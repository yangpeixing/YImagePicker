package com.ypx.imagepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Time: 2019/7/17 14:16
 * Author:ypx
 * Description:文件工具类
 */
public class PFileUtil {
    public static int[] getImageWidthHeight(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            return new int[]{options.outWidth, options.outHeight};
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0};
        }
    }

    /**
     * 保存一张图片到本地
     */
    public static String saveBitmapToLocalWithPNG(Bitmap bmp, String localPath) {
        if (bmp == null || localPath == null || localPath.length() == 0) {
            return "";
        }
        FileOutputStream b = null;
        createFile(localPath);
        try {
            b = new FileOutputStream(localPath);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                }
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    /**
     * 保存一张图片到本地
     */
    public static String saveBitmapToLocalWithJPEG(Bitmap bmp, String localPath) {
        if (bmp == null || localPath == null || localPath.length() == 0) {
            return "";
        }
        FileOutputStream b = null;
        createFile(localPath);
        try {
            b = new FileOutputStream(localPath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                }
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    /**
     * 保存一张图片到本地
     */
    public static String saveBitmapToLocalWithJPEG(View view, String localPath) {
        Bitmap bmp = getViewBitmap(view);
        if (bmp == null || localPath == null || localPath.length() == 0) {
            return "";
        }
        FileOutputStream b = null;
        createFile(localPath);
        try {
            b = new FileOutputStream(localPath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                }
                if (b != null) {
                    b.close();
                }
                view.destroyDrawingCache();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    public static Bitmap getViewBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }

    public static boolean exists(String fileName) {
        return new File(fileName).exists();
    }

    /**
     * 生成文件，如果父文件夹不存在，则先生成父文件夹
     *
     * @param fileName :要生成的文件全路径
     * @return File对象，如果有文件名不存在则返回null
     */
    public static File createFile(String fileName) {

        if (fileName == null || fileName.length() <= 0) {
            return null;
        }
        File file = new File(fileName);
        // 获取父文件夹
        File folderFile = file.getParentFile();
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    /**
     * 获取系统相册文件路径
     */
    public static String getDCIMOutputPath(String fileNameStart, String fileNameEnd) {
        String outputDir;
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (null != dcim && dcim.exists()) {
            outputDir = dcim.getAbsolutePath() + File.separator + "Camera";
        } else {
            outputDir = (Environment.getDataDirectory().getPath() + File.separator + "Camera");
        }
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }
        return outputDir + File.separator + fileNameStart + System.currentTimeMillis() + fileNameEnd;
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
            Uri imageUri;
            if (android.os.Build.VERSION.SDK_INT < 24) {
                imageUri = Uri.fromFile(new File(savePath));
            } else {
                imageUri = PickerFileProvider.getUriForFile(activity, activity
                        .getApplication().getPackageName() + ".picker.fileprovider", new File(savePath));
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        return intent;
    }

}
