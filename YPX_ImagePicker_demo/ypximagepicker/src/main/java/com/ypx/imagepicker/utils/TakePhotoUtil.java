package com.ypx.imagepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

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
    /**
     * 调用系统相机拍照
     */
    public static void takePhoto(Activity activity,int REQ) {
        File photoFile;
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                // Create the File where the photo should go
                photoFile = createImageSaveFile();
                if (photoFile != null) {
                    mCurrentPhotoPath=photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
            }
            activity.startActivityForResult(takePictureIntent, REQ);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
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
        String localPath=f.getAbsolutePath();
        if (bitmap == null ||  localPath.length() == 0) {
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
        if(fileName != null && fileName.length() > 0) {
            File file = new File(fileName);
            File folderFile = file.getParentFile();
            if(!folderFile.exists()) {
                folderFile.mkdirs();
            }

            try {
                if(!file.exists()) {
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
}
