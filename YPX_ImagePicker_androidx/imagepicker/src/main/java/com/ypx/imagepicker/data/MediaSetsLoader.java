package com.ypx.imagepicker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.util.Set;

import static com.ypx.imagepicker.data.MediaSetsConstants.BUCKET_ORDER_BY;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_BUCKET_ID;
import static com.ypx.imagepicker.data.MediaSetsConstants.COLUMN_COUNT;
import static com.ypx.imagepicker.data.MediaSetsConstants.DATA;
import static com.ypx.imagepicker.data.MediaSetsConstants.MEDIA_TYPE;
import static com.ypx.imagepicker.data.MediaSetsConstants.MEDIA_TYPE_IMAGE;
import static com.ypx.imagepicker.data.MediaSetsConstants.MEDIA_TYPE_VIDEO;
import static com.ypx.imagepicker.data.MediaSetsConstants.MIME_TYPE;
import static com.ypx.imagepicker.data.MediaSetsConstants.QUERY_URI;
import static com.ypx.imagepicker.data.MediaSetsConstants.SIZE;


public class MediaSetsLoader extends CursorLoader {
    private boolean isLoadVideo;
    private boolean isLoadImage;
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            DATA,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            COLUMN_BUCKET_ID,
            COLUMN_BUCKET_DISPLAY_NAME,
            DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    private MediaSetsLoader(Context context, String selection, String[] selectionArgs, boolean isLoadVideo, boolean isLoadImage) {
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

        String selection = "(" + MEDIA_TYPE + "=" + MEDIA_TYPE_VIDEO + " OR " +
                MEDIA_TYPE + "=" + MEDIA_TYPE_IMAGE + ")" +
                " AND " +
                SIZE + ">0" +
                " AND (" +
                mimeSelection + ")" +
                ") GROUP BY (bucket_id";

        return new MediaSetsLoader(context, selection, selectionArgs, isLoadVideo, isLoadImage);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(DATA));
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
        allAlbum.addRow(new String[]{ImageSet.ID_ALL_MEDIA, ImageSet.ID_ALL_MEDIA, name, allAlbumCoverPath,
                String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }

    @Override
    public void onContentChanged() {
    }
}