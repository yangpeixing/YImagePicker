package com.ypx.imagepicker.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_BUCKET_DISPLAY_NAME;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_BUCKET_ID;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_COUNT;
import static com.ypx.imagepicker.data.MediaStoreConstants.COLUMN_URI;


/**
 * Description: 媒体文件夹数据
 * <p>
 * Author: peixing.yang
 * Date: 2019/4/11
 */
public class MediaSetsDataSource implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 1;
    private WeakReference<FragmentActivity> mContext;
    private LoaderManager mLoaderManager;
    private MediaSetProvider mediaSetProvider;
    private boolean isLoadVideo;
    private boolean isLoadImage;

    private Set<MimeType> mimeTypeSet = MimeType.ofAll();

    public MediaSetsDataSource setMimeTypeSet(BaseSelectConfig config) {
        isLoadImage = config.isShowImage();
        isLoadVideo = config.isShowVideo();
        mimeTypeSet = config.getMimeTypes();
        return this;
    }

    public MediaSetsDataSource setMimeTypeSet(Set<MimeType> mimeTypeSet) {
        this.mimeTypeSet = mimeTypeSet;
        for (MimeType mimeType : mimeTypeSet) {
            if (MimeType.ofVideo().contains(mimeType)) {
                isLoadVideo = true;
            }
            if (MimeType.ofImage().contains(mimeType)) {
                isLoadImage = true;
            }
        }
        return this;
    }

    public void loadMediaSets(MediaSetProvider mediaSetProvider) {
        this.mediaSetProvider = mediaSetProvider;
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public static MediaSetsDataSource create(FragmentActivity activity) {
        return new MediaSetsDataSource(activity);
    }

    private MediaSetsDataSource(FragmentActivity activity) {
        mContext = new WeakReference<>(activity);
        mLoaderManager = LoaderManager.getInstance(mContext.get());
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        return MediaSetsLoader.create(context, mimeTypeSet, isLoadVideo, isLoadImage);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        FragmentActivity context = mContext.get();
        if (context == null) {
            return;
        }
        ArrayList<ImageSet> imageSetList = new ArrayList<>();
        if (!context.isDestroyed() && cursor.moveToFirst() && !cursor.isClosed()) {
            do {
                ImageSet imageSet = new ImageSet();
                imageSet.id = getString(cursor, COLUMN_BUCKET_ID);
                imageSet.name = getString(cursor, COLUMN_BUCKET_DISPLAY_NAME);
                imageSet.coverPath = getString(cursor, COLUMN_URI);
                imageSet.count = getInt(cursor, COLUMN_COUNT);
                imageSetList.add(imageSet);
            } while (!context.isDestroyed() && cursor.moveToNext() && !cursor.isClosed());
        }

        if (mediaSetProvider != null) {
            mediaSetProvider.providerMediaSets(imageSetList);
        }

        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public interface MediaSetProvider {
        void providerMediaSets(ArrayList<ImageSet> imageSets);
    }

    private int getInt(Cursor data, String text) {
        int index = hasColumn(data, text);
        if (index != -1) {
            return data.getInt(index);
        } else {
            return 0;
        }
    }

    private String getString(Cursor data, String text) {
        int index = hasColumn(data, text);
        if (index != -1) {
            return data.getString(index);
        } else {
            return "";
        }
    }

    private int hasColumn(Cursor data, String id) {
        try {
            return data.getColumnIndexOrThrow(id);
        } catch (Exception e) {
            return -1;
        }
    }
}
