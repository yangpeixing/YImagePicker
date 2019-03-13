/*
 * Created by fanchao
 *
 * Date:2014年9月18日下午6:31:32
 *
 * Copyright (c) 2014, Show(R). All rights reserved.
 *
 */
package com.ypx.imagepicker.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Function: 处理文件名，创建文件的类， 如果要得到某个文件目录
 * <p>
 * Date: 2014年9月18日 下午6:31:32
 *
 * @author fanchao
 */
public final class FileUtil {
    private static final String TAG = "FileUtil";

    private FileUtil() {
    }

    /**
     * 删除文件夹
     *
     * @param folder
     */
    public static void deleteFolderRecursively(File folder) {
        if (folder != null && folder.isDirectory() && folder.canWrite()) {
            File[] listFiles = folder.listFiles();

            if (listFiles != null) {
                for (File f : listFiles) {
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        deleteFolderRecursively(f);
                    }
                }
                folder.delete();
            }
        }
    }


    /**
     * 判断文件是否存成
     *
     * @param fileName
     * @return true：文件存成 false:文件不存在
     */
    public static boolean isFileExist(String fileName) {
        boolean flag = false;
        if (fileName == null || fileName.length() == 0) {
            return false;
        }
        File file = new File(fileName);
        flag = isFileExist(file);
        return flag;
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return true：文件存成 false:文件不存在
     */
    public static boolean isFileExist(File file) {
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为URI路径
     *
     * @param filename
     * @return true:是URI路径 false:不是
     */
    public static boolean isURIPath(String filename) {
        if (filename.contains(":")) {
            return true;
        } else {
            return false;
        }
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

    /**
     * 复制一个文件
     *
     * @param src
     * @param tar
     * @throws Exception
     */
    public static void copyFile(File src, File tar) throws Exception {
        try {
            int bytesum = 0;
            int byteread = 0;
            if (src.isFile()) { // 文件存在时
                InputStream inStream = new FileInputStream(src); // 读入原文件
                FileOutputStream fs = new FileOutputStream(tar);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
                src.delete();
            }
        } catch (Exception e) {
            //System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
        // if (src.isFile()) {
        // InputStream is = new FileInputStream(src);
        // OutputStream op = new FileOutputStream(tar);
        // BufferedInputStream bis = new BufferedInputStream(is);
        // BufferedOutputStream bos = new BufferedOutputStream(op);
        // byte[] bt = new byte[4*1024];
        // int len = bis.read(bt);
        // while (len != -1) {
        // bos.write(bt, 0, len);
        // len = bis.read(bt);
        // }
        // bis.close();
        // bos.close();
        // src.delete();
        // }
    }

    /**
     * 获取文件大小
     *
     * @param
     * @return
     */
    public static long getFileSize(File file) {
        long dirSize = 0;

        if (file == null) {
            return 0;
        }
        if (!file.isDirectory()) {
            dirSize += file.length();
            return dirSize;
        }

        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                dirSize += f.length();
            } else if (f.isDirectory()) {
                dirSize += f.length();
                dirSize += getFileSize(f); // 如果遇到目录则通过递归调用继续统计
            }
        }

        return dirSize;
    }

//	/**
//	 * 获取文件夹大小
//	 *
//	 * @return
//	 */
//	public static String getDirSize(Context context) {
//		File file = StorageUtils.getCacheDirectory(context);
//		//ZGSystemUtil.getImageCacheDirectory();
//		double size = 0;
//		long dirSize = getFileSize(file);
//		size = (dirSize + 0.0) / (1024 * 1024);
//		DecimalFormat df = new DecimalFormat("0.00");// 以Mb为单位保留两位小数
//		String filesize = df.format(size);
//		return filesize;
//
//	}
//

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值 ， 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }


    /**
     * 保存一张图片到本地
     *
     * @param view
     * @param localPath
     */
    public static String saveBitmapToLocalWithJPEG(View view, String localPath) {
        Bitmap bmp = getViewBitmap(view);
        if (bmp == null || localPath == null || localPath.length() == 0) {
            return "";
        }
        FileOutputStream b = null;
        FileUtil.createFile(localPath);
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

//	public static Bitmap getViewBitmap(View view) {
//		Bitmap bkg = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//		view.draw(new Canvas(bkg));
//		return bkg;
//	}

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

    //flie：要删除的文件夹的所在位置
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteFile(f);
            }
            file.delete();//如要保留文件夹，只删除文件，请注释这行
        } else if (file.exists()) {
            file.delete();
        }
    }

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
}
