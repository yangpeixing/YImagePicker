package com.ypx.imagepicker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Time: 2019/7/17 14:16
 * Author:ypx
 * Description:
 */
public class FileUtil {
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
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
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
}
