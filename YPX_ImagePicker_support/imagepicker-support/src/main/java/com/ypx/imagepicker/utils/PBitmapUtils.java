package com.ypx.imagepicker.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.UriPathInfo;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

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

    public static Bitmap getBitmapFromUri(Context c, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = c.getContentResolver()
                    .openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                return image;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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
            dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
            bitmap.compress(compressFormat, 90, b);// 把数据写入文件
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

    public static Uri saveBitmapToDCIM(Context context,
                                       Bitmap bitmap,
                                       String fileName,
                                       Bitmap.CompressFormat compressFormat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/" + compressFormat.toString());
        contentValues.put(MediaStore.Files.FileColumns.WIDTH, bitmap.getWidth());
        contentValues.put(MediaStore.Files.FileColumns.HEIGHT, bitmap.getHeight());
        String suffix = "." + compressFormat.toString().toLowerCase();
        String path = getDCIMDirectory().getAbsolutePath() + File.separator + fileName + suffix;
        try {
            contentValues.put(MediaStore.Images.Media.DATA, path);
        } catch (Exception ignored) {

        }
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
     * androidQ方式保存一张bitmap到DCIM根目录下
     *
     * @param context        当前context
     * @param sourceFilePath 当前要生成的bitmap
     * @param fileName       图片名称
     * @param mimeType       图片格式
     * @return 此图片的Uri
     */
    public static UriPathInfo copyFileToDCIM(Context context, String sourceFilePath,
                                             String fileName, MimeType mimeType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType.toString());
        if (Build.VERSION.SDK_INT >= 29) {
            contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        boolean isImage = MimeType.isImage(mimeType.toString());
        if (isImage) {
            int[] size = getImageWidthHeight(sourceFilePath);
            contentValues.put(MediaStore.Files.FileColumns.WIDTH, size[0]);
            contentValues.put(MediaStore.Files.FileColumns.HEIGHT, size[1]);
        } else {
            long duration = PBitmapUtils.getLocalVideoDuration(sourceFilePath);
            contentValues.put("duration", duration);
        }
        String suffix = "." + mimeType.getSuffix();
        String path = getDCIMDirectory().getAbsolutePath() + File.separator + fileName + suffix;
        try {
            contentValues.put(MediaStore.Images.Media.DATA, path);
        } catch (Exception ignored) {

        }
        //执行insert操作，向系统文件夹中添加文件
        //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
        Uri uri = context.getContentResolver().insert(isImage ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI :
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        copyFile(context, sourceFilePath, uri);
        return new UriPathInfo(uri, path);
    }

    private static boolean copyFile(Context context, String sourceFilePath, final Uri insertUri) {
        if (insertUri == null) {
            return false;
        }
        ContentResolver resolver = context.getContentResolver();
        InputStream is = null;//输入流
        OutputStream os = null;//输出流
        try {
            os = resolver.openOutputStream(insertUri);
            if (os == null) {
                return false;
            }
            File sourceFile = new File(sourceFilePath);
            if (sourceFile.exists()) { // 文件存在时
                is = new FileInputStream(sourceFile); // 读入原文件
                //输入流读取文件，输出流写入指定目录
                return copyFileWithStream(os, is);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean copyFileWithStream(OutputStream os, InputStream is) {
        if (os == null || is == null) {
            return false;
        }
        int read = 0;
        while (true) {
            try {
                byte[] buffer = new byte[1444];
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                    os.flush();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
    public static long getLocalVideoDuration(String videoPath) {
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
    public static void refreshGalleryAddPic(Context context, Uri uri) {
        if (context == null) {
            return;
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getMimeTypeFromUri(Activity context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.getType(uri);
    }

    public static String getMimeTypeFromPath(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(new File(path).getName());
    }


    public static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            return null;
        }
    }

    public static Uri getVideoContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            return null;
        }
    }

    public static Uri getContentUri(String mimeType, long id) {
        if (id <= 0) {
            return null;
        }
        Uri contentUri;
        if (MimeType.isImage(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (MimeType.isVideo(mimeType)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        return ContentUris.withAppendedId(contentUri, id);
    }

}
