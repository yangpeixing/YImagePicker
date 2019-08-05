package com.ypx.imagepicker.data.impl;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.utils.DateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Description: 数据加载
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
class VideoDataSource implements DataSource, LoaderManager.LoaderCallbacks<Cursor> {

    private final String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_TAKEN
    };

    private OnImagesLoadedListener imagesLoadedListener;
    private FragmentActivity mContext;
    private static ArrayList<ImageSet> mVideoSetList = new ArrayList<>();

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (imagesLoadedListener == null) {
            return;
        }
        //如果缓存中有数据，则直接加载
        if (mVideoSetList != null && mVideoSetList.size() > 0
                && MediaObserver.instance.isMediaNotChanged()) {
            imagesLoadedListener.onImagesLoaded(mVideoSetList);
            return;
        }

        if (mVideoSetList != null) {
            mVideoSetList.clear();
        } else {
            mVideoSetList = new ArrayList<>();
        }

        if (mContext != null) {
            LoaderManager.getInstance(mContext).initLoader(999, null, this);
        }
    }

    VideoDataSource(FragmentActivity ctx) {
        this.mContext = ctx;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                VIDEO_PROJECTION,
                null,
                null,
                VIDEO_PROJECTION[6] + " DESC");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor data) {
        if (mContext.isDestroyed() || mVideoSetList.size() > 0) {
            return;
        }

        if (data == null || data.getCount() <= 0 || data.isClosed()) {
            if (imagesLoadedListener != null) {
                imagesLoadedListener.onImagesLoaded(null);
            }
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                compressToList(data);
            }
        }).start();
    }

    private void compressToList(Cursor data) {
        ArrayList<ImageItem> allImages = new ArrayList<>();
        data.moveToFirst();
        do {
            int videoId = data.getInt(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            String imagePath = data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
            long duration = data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            if (duration == 0) {
                continue;
            }

            ImageItem item = new ImageItem(imagePath, duration, "");
            item.setId(videoId);
            item.setTimeFormat(DateUtil.getStrTime(imageAddedTime));
            item.time = imageAddedTime;
            item.setDurationFormat(DateUtil.getVideoDuration(duration));
            item.setVideo(true);
            allImages.add(item);

            File imageFile = new File(imagePath);
            File imageParentFile = imageFile.getParentFile();

            ImageSet imageSet = new ImageSet();
            imageSet.name = imageParentFile.getName();
            imageSet.path = imageParentFile.getAbsolutePath();
            imageSet.cover = item;

            if (!mVideoSetList.contains(imageSet)) {
                ArrayList<ImageItem> imageList = new ArrayList<>();
                imageList.add(item);
                imageSet.imageItems = imageList;
                mVideoSetList.add(imageSet);
            } else {
                mVideoSetList.get(mVideoSetList.indexOf(imageSet)).imageItems.add(item);
            }

        } while (data.moveToNext());

        ImageSet imageSetAll = new ImageSet();
        imageSetAll.name = mContext.getString(R.string.str_allvideo);
        imageSetAll.cover = allImages.get(0);
        imageSetAll.imageItems = allImages;
        imageSetAll.path = "/";
        mVideoSetList.add(0, imageSetAll);

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mContext.isDestroyed()) {
                    return;
                }
                MediaObserver.instance.setMediaChanged(false);
                if (imagesLoadedListener != null) {
                    imagesLoadedListener.onImagesLoaded(mVideoSetList);
                }
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
