package com.ypx.imagepicker.data;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.util.ArrayList;
import java.util.Set;

import static com.ypx.imagepicker.data.MediaStoreConstants.DATA;
import static com.ypx.imagepicker.data.MediaStoreConstants.DATE_MODIFIED;
import static com.ypx.imagepicker.data.MediaStoreConstants.DISPLAY_NAME;
import static com.ypx.imagepicker.data.MediaStoreConstants.DURATION;
import static com.ypx.imagepicker.data.MediaStoreConstants.HEIGHT;
import static com.ypx.imagepicker.data.MediaStoreConstants.MEDIA_TYPE;
import static com.ypx.imagepicker.data.MediaStoreConstants.MEDIA_TYPE_IMAGE;
import static com.ypx.imagepicker.data.MediaStoreConstants.MEDIA_TYPE_VIDEO;
import static com.ypx.imagepicker.data.MediaStoreConstants.MIME_TYPE;
import static com.ypx.imagepicker.data.MediaStoreConstants.SIZE;
import static com.ypx.imagepicker.data.MediaStoreConstants.WIDTH;
import static com.ypx.imagepicker.data.MediaStoreConstants._ID;


public class MediaItemsLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            _ID,
            DATA,
            DISPLAY_NAME,
            WIDTH,
            HEIGHT,
            MIME_TYPE,
            SIZE,
            DURATION,
            DATE_MODIFIED};

    private static final String ORDER_BY = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";

    private MediaItemsLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    static CursorLoader newInstance(Context context, ImageSet album, Set<MimeType> mimeTypeSet) {
        String[] selectionsArgs;
        String albumSelections = "";
        String mimeSelections = "";
        int index = 0;
        ArrayList<String> arrayList = MimeType.getMimeTypeList(mimeTypeSet);
        if (album.isAllMedia() || album.isAllVideo()) {
            selectionsArgs = new String[arrayList.size()];
        } else {
            selectionsArgs = new String[arrayList.size() + 1];
            selectionsArgs[0] = album.id;
            index = 1;
            albumSelections = " bucket_id=? AND ";
        }

        for (String mimeType : arrayList) {
            selectionsArgs[index] = mimeType;
            mimeSelections = String.format("%s =? OR %s", MediaStore.Files.FileColumns.MIME_TYPE, mimeSelections);
            index++;
        }

        if (mimeSelections.endsWith(" OR ")) {
            mimeSelections = mimeSelections.substring(0, mimeSelections.length() - 4);
        }

        String selections = albumSelections + "(" + MEDIA_TYPE + "=" + MEDIA_TYPE_IMAGE + " OR " +
                MEDIA_TYPE + "=" + MEDIA_TYPE_VIDEO + ")"
                + " AND " + SIZE + ">0" + " AND (" + mimeSelections + ")";

        return new MediaItemsLoader(context, selections, selectionsArgs);
    }

    @Override
    public void onContentChanged() {
    }
}
