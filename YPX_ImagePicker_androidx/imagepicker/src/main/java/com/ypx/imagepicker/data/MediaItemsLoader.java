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
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.loader.content.CursorLoader;

import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.util.Set;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MediaItemsLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Files.FileColumns.DATE_MODIFIED};

    private static final String ORDER_BY = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

    private MediaItemsLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    public static CursorLoader newInstance(Context context, ImageSet album, Set<MimeType> mimeTypeSet) {
        String[] selectionsArgs;
        String albumSelections = "";
        String mimeSelections = "";
        int index = 0;
        if (album.isAllMedia() || album.isAllVideo()) {
            selectionsArgs = new String[mimeTypeSet.size()];
        } else {
            selectionsArgs = new String[mimeTypeSet.size() + 1];
            selectionsArgs[0] = album.id;
            index = 1;
            albumSelections = " bucket_id=? AND ";
        }

        for (MimeType type : mimeTypeSet) {
            selectionsArgs[index] = String.valueOf(type);
            mimeSelections = String.format("%s =? OR %s", MediaStore.Files.FileColumns.MIME_TYPE, mimeSelections);
            index++;
        }

        if (mimeSelections.endsWith(" OR ")) {
            mimeSelections = mimeSelections.substring(0, mimeSelections.length() - 4);
        }

        String selections = albumSelections + "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0" + " AND (" + mimeSelections + ")";

        return new MediaItemsLoader(context, selections, selectionsArgs);
    }

    @Override
    public void onContentChanged() {
    }
}
