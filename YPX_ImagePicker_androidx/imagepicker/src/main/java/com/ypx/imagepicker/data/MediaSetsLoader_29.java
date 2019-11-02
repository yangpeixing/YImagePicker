package com.ypx.imagepicker.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.LongSparseArray;

import androidx.loader.content.CursorLoader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.util.HashSet;
import java.util.Set;

import static com.ypx.imagepicker.data.MediaSetsConstants.BUCKET_ORDER_BY;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_BUCKET_ID;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_COUNT;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_URI;
import static com.ypx.imagepicker.data.MediaSetsConstants.MEDIA_TYPE;
import static com.ypx.imagepicker.data.MediaSetsConstants.MEDIA_TYPE_IMAGE;
import static com.ypx.imagepicker.data.MediaSetsConstants.MEDIA_TYPE_VIDEO;
import static com.ypx.imagepicker.data.MediaSetsConstants.MIME_TYPE;
import static com.ypx.imagepicker.data.MediaSetsConstants.QUERY_URI;
import static com.ypx.imagepicker.data.MediaSetsConstants.SIZE;
import static com.ypx.imagepicker.data.MediaSetsConstants._ID;


public class MediaSetsLoader_29 extends CursorLoader {
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

    private MediaSetsLoader_29(Context context, String selection, String[] selectionArgs, boolean isLoadVideo, boolean isLoadImage) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
        this.isLoadVideo = isLoadVideo;
        this.isLoadImage = isLoadImage;
    }

    public static CursorLoader create(Context context, Set<MimeType> mimeTypeSet, boolean isLoadVideo, boolean isLoadImage) {
        String[] selectionArgs = new String[mimeTypeSet.size()];

        int index = 0;
        String mimeSelection = "";
        for (MimeType type : mimeTypeSet) {
            selectionArgs[index] = String.valueOf(type);
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
        return new MediaSetsLoader_29(context, selection, selectionArgs, isLoadVideo, isLoadImage);
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
            name = getContext().getResources().getString(R.string.picker_str_all);
        } else if (isLoadImage) {
            name = getContext().getResources().getString(R.string.picker_str_all_image);
        } else if (isLoadVideo) {
            name = getContext().getResources().getString(R.string.picker_str_all_video);
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