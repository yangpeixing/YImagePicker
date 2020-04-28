package com.ypx.imagepicker.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.LongSparseArray;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.ypx.imagepicker.data.MediaStoreConstants.BUCKET_ORDER_BY;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_BUCKET_ID;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_COUNT;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_URI;
import static com.ypx.imagepicker.data.MediaStoreConstants.MEDIA_TYPE;
import static com.ypx.imagepicker.data.MediaStoreConstants.MEDIA_TYPE_IMAGE;
import static com.ypx.imagepicker.data.MediaStoreConstants.MEDIA_TYPE_VIDEO;
import static com.ypx.imagepicker.data.MediaStoreConstants.MIME_TYPE;
import static com.ypx.imagepicker.data.MediaStoreConstants.QUERY_URI;
import static com.ypx.imagepicker.data.MediaStoreConstants.SIZE;
import static com.ypx.imagepicker.data.MediaStoreConstants._ID;


public class MediaSetsLoader extends CursorLoader {
    private boolean isLoadVideo;
    private boolean isLoadImage;
    private static final String[] COLUMNS = {
            _ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            COLUMN_URI,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            _ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            MIME_TYPE};

    private MediaSetsLoader(Context context, String selection, String[] selectionArgs, boolean isLoadVideo, boolean isLoadImage) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
        this.isLoadVideo = isLoadVideo;
        this.isLoadImage = isLoadImage;
    }

    public static CursorLoader create(Context context, Set<MimeType> mimeTypeSet, boolean isLoadVideo, boolean isLoadImage) {
        int index = 0;
        String mimeSelection = "";
        ArrayList<String> arrayList = MimeType.getMimeTypeList(mimeTypeSet);
        String[] selectionArgs = new String[arrayList.size()];
        for (String mimeType : arrayList) {
            selectionArgs[index] = mimeType;
            mimeSelection = String.format("%s =? OR %s", MIME_TYPE, mimeSelection);
            index++;
        }

        if (mimeSelection.endsWith(" OR ")) {
            mimeSelection = mimeSelection.substring(0, mimeSelection.length() - 4);
        }
        String selection = "(" + MEDIA_TYPE + "=" + MEDIA_TYPE_VIDEO + " OR " + MEDIA_TYPE + "=" + MEDIA_TYPE_IMAGE + ")" +
                " AND " +
                SIZE + ">0" +
                " AND (" +
                mimeSelection + ")";
        return new MediaSetsLoader(context, selection, selectionArgs, isLoadVideo, isLoadImage);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        Uri allAlbumCoverUri = null;
        LongSparseArray<Long> countMap = new LongSparseArray<>();
        if (albums != null) {
            while (albums.moveToNext()) {
                long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));
                Long count = countMap.get(bucketId);
                count = count == null ? 1L : (count + 1);
                countMap.put(bucketId, count);
            }
        }
        MatrixCursor newAlbums = new MatrixCursor(COLUMNS);
        if (albums != null) {
            if (albums.moveToFirst()) {
                allAlbumCoverUri = getUri(albums);
                Set<Long> done = new HashSet<>();
                do {
                    long bucketId = albums.getLong(albums.getColumnIndex(COLUMN_BUCKET_ID));
                    if (done.contains(bucketId)) {
                        continue;
                    }
                    long fileId = albums.getLong(albums.getColumnIndex(_ID));
                    String bucketDisplayName = albums.getString(albums.getColumnIndex(COLUMN_BUCKET_DISPLAY_NAME));
                    Uri uri = getUri(albums);
                    long count = countMap.get(bucketId);
                    newAlbums.addRow(new String[]{
                            Long.toString(fileId),
                            Long.toString(bucketId),
                            bucketDisplayName,
                            uri.toString(),
                            String.valueOf(count)});
                    done.add(bucketId);
                    totalCount += count;
                } while (albums.moveToNext());
            }
        }

        String name = "";
        if (isLoadImage && isLoadVideo) {
            name = getContext().getString(R.string.picker_str_folder_item_all);
        } else if (isLoadImage) {
            name = getContext().getString(R.string.picker_str_folder_item_image);
        } else if (isLoadVideo) {
            name = getContext().getString(R.string.picker_str_folder_item_video);
        }

        allAlbum.addRow(new String[]{ImageSet.ID_ALL_MEDIA, ImageSet.ID_ALL_MEDIA, name,
                allAlbumCoverUri == null ? null : allAlbumCoverUri.toString(),
                String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, newAlbums});
    }

    private static Uri getUri(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
        String mimeType = cursor.getString(cursor.getColumnIndex(MIME_TYPE));
        Uri contentUri;
        if (MimeType.isImage(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (MimeType.isVideo(mimeType)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = QUERY_URI;
        }
        return ContentUris.withAppendedId(contentUri, id);
    }

    @Override
    public void onContentChanged() {
    }
}