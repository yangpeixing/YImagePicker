package com.ypx.imagepicker.data;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Time: 2019/10/29 20:38
 * Author:ypx
 * Description:
 */
class MediaStoreConstants {
    static final String MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE;
    static final String MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE;
    static final String DISPLAY_NAME = MediaStore.Files.FileColumns.DISPLAY_NAME;
    static final int MEDIA_TYPE_VIDEO = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    static final int MEDIA_TYPE_IMAGE = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
    static final String WIDTH = MediaStore.Files.FileColumns.WIDTH;
    static final String HEIGHT = MediaStore.Files.FileColumns.HEIGHT;
    static final String DATE_MODIFIED = MediaStore.Files.FileColumns.DATE_MODIFIED;
    static final String DURATION = MediaStore.MediaColumns.DURATION;
    static final String SIZE = MediaStore.MediaColumns.SIZE;
    static final String _ID = MediaStore.Files.FileColumns._ID;
    static final String COLUMN_BUCKET_ID = "bucket_id";
    static final String COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name";
    static final String COLUMN_URI = "uri";
    static final String COLUMN_COUNT = "count";
    static final String BUCKET_ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC";
    /**
     * @deprecated android 10 已废弃此常亮
     */
    static final String DATA = MediaStore.MediaColumns.DATA;
    static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    /**
     * @return 是否是Android10之前版本
     */
    static boolean isBeforeAndroidQ() {
        return android.os.Build.VERSION.SDK_INT < 29;
    }
}
