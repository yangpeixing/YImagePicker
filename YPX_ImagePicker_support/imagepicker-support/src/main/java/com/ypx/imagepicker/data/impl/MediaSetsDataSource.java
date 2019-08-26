package com.ypx.imagepicker.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;


import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerSelectConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;


/**
 * Description: 媒体数据
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

    public MediaSetsDataSource setMimeTypeSet(PickerSelectConfig config) {
        isLoadImage = config.isShowImage();
        isLoadVideo = config.isShowVideo();

        mimeTypeSet = MimeType.ofAll();
        if (!config.isShowImage()) {
            mimeTypeSet.removeAll(MimeType.ofImage());
        } else {
            if (!config.isLoadGif()) {
                mimeTypeSet.remove(MimeType.GIF);
            }
        }

        if (!config.isShowVideo()) {
            mimeTypeSet.removeAll(MimeType.ofVideo());
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
        if (context == null) {
            return null;
        }
        return MediaSetsLoader.newInstance(context, mimeTypeSet,isLoadVideo,isLoadImage);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        ArrayList<ImageSet> imageSetList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ImageSet imageSet = new ImageSet();
                imageSet.id = cursor.getString(cursor.getColumnIndex("bucket_id"));
                imageSet.name = cursor.getString(cursor.getColumnIndex("bucket_display_name"));
                imageSet.coverPath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                imageSet.count = cursor.getInt(cursor.getColumnIndex("count"));
                imageSetList.add(imageSet);
            } while (cursor.moveToNext());
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

}
