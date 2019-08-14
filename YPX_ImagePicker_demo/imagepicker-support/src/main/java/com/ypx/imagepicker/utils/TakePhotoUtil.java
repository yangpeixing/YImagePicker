package com.ypx.imagepicker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;


import com.ypx.imagepicker.helper.PickerFileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 作者：yangpeixing on 2018/10/9 11:30
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class TakePhotoUtil {
    public static String mCurrentPhotoPath;

    /*
     * 判断sdcard是否被挂载
     */
    private static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * 调用系统相机拍照
     */
    public static void takePhoto(Activity activity, int REQ) {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ);
            }
            return;
        }

        mCurrentPhotoPath = "";
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            Uri imageUri;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
            String fileName = "IMG_" + timeStamp;
            mCurrentPhotoPath = getDCIMOutputPath(activity, fileName, ".jpg");
            if (Build.VERSION.SDK_INT < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(new File(mCurrentPhotoPath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                imageUri = PickerFileProvider.getUriForFile(activity,activity
                        .getApplication().getPackageName() + ".picker.fileprovider",new File(mCurrentPhotoPath));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, REQ);
    }

    /**
     * 获取系统相册文件路径
     */
    public static String getDCIMOutputPath(Context me, String fileNameStart, String fileNameEnd) {
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
     * create a file to save photo
     */
    private static File createImageSaveFile() {
        String sdStatus = Environment.getExternalStorageState();
        File tmpFile;
        File cacheDir;
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = Environment.getDataDirectory();
        } else {
            // 已挂载
            cacheDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = "IMG_" + timeStamp;
        tmpFile = new File(cacheDir, fileName + ".jpg");
        return tmpFile;
    }

    public static String saveBitmapToPic(Bitmap bitmap) {
        File f = createImageSaveFile();
        String localPath = f.getAbsolutePath();
        if (bitmap == null || localPath.length() == 0) {
            return "";
        }
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(localPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, b);// 把数据写入文件
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
        return f.getAbsolutePath();
    }

    public static File createFile(String fileName) {
        if (fileName != null && fileName.length() > 0) {
            File file = new File(fileName);
            File folderFile = file.getParentFile();
            if (!folderFile.exists()) {
                folderFile.mkdirs();
            }

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return file;
        } else {
            return null;
        }
    }

    /**
     * 刷新相册
     */
    public static void refreshGalleryAddPic(Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
