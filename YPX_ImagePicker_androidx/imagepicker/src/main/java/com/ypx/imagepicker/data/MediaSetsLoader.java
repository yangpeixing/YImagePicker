/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ypx.imagepicker.data;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.util.Set;


public class MediaSetsLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    private static final String BUCKET_ORDER_BY =   MediaStore.MediaColumns.DATE_MODIFIED + " DESC";

    private boolean isLoadVideo;
    private boolean isLoadImage;

    private MediaSetsLoader(Context context, String selection, String[] selectionArgs, boolean isLoadVideo, boolean isLoadImage) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
        this.isLoadVideo = isLoadVideo;
        this.isLoadImage = isLoadImage;
    }

    public static CursorLoader newInstance(Context context, Set<MimeType> mimeTypeSet, boolean isLoadVideo, boolean isLoadImage) {
        String[] selectionArgs = new String[mimeTypeSet.size()];

        int index = 0;
        String mimeSelection = "";
        for (MimeType type : mimeTypeSet) {
            selectionArgs[index] = String.valueOf(type);
            mimeSelection = String.format("%s =? OR %s", MediaStore.Files.FileColumns.MIME_TYPE, mimeSelection);
            index++;
        }

        if (mimeSelection.endsWith(" OR ")) {
            mimeSelection = mimeSelection.substring(0, mimeSelection.length() - 4);
        }

        String selection = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + ")" +
                " AND " +
                MediaStore.MediaColumns.SIZE + ">0" +
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
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        String name = "";
        if (isLoadImage && isLoadVideo) {
            name = getContext().getResources().getString(R.string.str_image_video);
        } else if (isLoadImage) {
            name = getContext().getResources().getString(R.string.str_image);
        } else if (isLoadVideo) {
            name = getContext().getResources().getString(R.string.str_allvideo);
        }
        allAlbum.addRow(new String[]{ImageSet.ID_ALL_MEDIA, ImageSet.ID_ALL_MEDIA, name, allAlbumCoverPath,
                String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }

    @Override
    public void onContentChanged() {
    }
}