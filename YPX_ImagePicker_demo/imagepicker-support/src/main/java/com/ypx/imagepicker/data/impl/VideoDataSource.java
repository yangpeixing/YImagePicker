package com.ypx.imagepicker.data.impl;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.utils.DateUtil;

import java.io.File;
import java.util.ArrayList;


/**
 * Description: 数据加载
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
class VideoDataSource implements DataSource, LoaderManager.LoaderCallbacks<Cursor> {
    private final int ID = 999;
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
        if (imagesLoadedListener == null || mContext == null) {
            return;
        }
        //如果缓存中有数据，则直接加载
        if (ImagePicker.isPreloadOk && mVideoSetList != null && mVideoSetList.size() > 0
                && MediaObserver.instance.isMediaNotChanged()) {
            imagesLoadedListener.onImagesLoaded(mVideoSetList);
            return;
        }

        if (mVideoSetList == null) {
            mVideoSetList = new ArrayList<>();
        } else {
            mVideoSetList.clear();
        }
        LoaderManager.getInstance(mContext).initLoader(ID, null, this);
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

    private void close(Cursor cursor) {
        try {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            MediaObserver.instance.setMediaChanged(false);
            LoaderManager.getInstance(mContext).destroyLoader(ID);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor data) {
        if (mContext.isDestroyed() || mContext.isFinishing()) {
            close(data);
            return;
        }

        if (data == null || data.getCount() <= 0 || data.isClosed()) {
            imagesLoadedListener.onImagesLoaded(mVideoSetList);
            close(data);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                compressToList(data);
            }
        }).start();
    }


    private void compressToList(final Cursor data) {
        if (mVideoSetList == null) {
            mVideoSetList = new ArrayList<>();
        }
        ArrayList<ImageItem> allImages = new ArrayList<>();
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            int videoId = getInt(data, MediaStore.Video.Media._ID);
            String imagePath = getString(data, MediaStore.Video.Media.DATA);
            long imageAddedTime = getLong(data, MediaStore.Video.Media.DATE_TAKEN);
            long duration = getLong(data, MediaStore.Video.Media.DURATION);
            if (duration == 0 || imagePath.length() == 0) {
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

            //是否是新的文件夹
            boolean isNewSet = true;
            //遍历文件夹列表，如果包含当前文件的文件夹，则直接把视频放进文件夹中
            //否则新增一个文件夹
            for (ImageSet set : mVideoSetList) {
                if (set.equals(imageSet)) {
                    isNewSet = false;
                    ArrayList<ImageItem> imageItems = set.imageItems;
                    if (imageItems == null) {
                        imageItems = new ArrayList<>();
                    }
                    imageItems.add(item);
                    break;
                }
            }

            //如果没有重复的文件夹，则新增一个文件夹
            if (isNewSet) {
                //生成视频文件夹
                ArrayList<ImageItem> imageList = new ArrayList<>();
                imageList.add(item);
                imageSet.imageItems = imageList;
                mVideoSetList.add(imageSet);
            }
        }
        if (mVideoSetList.size() > 0) {
            ImageSet imageSetAll = new ImageSet();
            imageSetAll.name = mContext.getString(R.string.str_allvideo);
            imageSetAll.cover = allImages.get(0);
            imageSetAll.imageItems = allImages;
            imageSetAll.path = "/";
            mVideoSetList.add(0, imageSetAll);
        }
        notifyLoadComplete(data);
    }

    private void notifyLoadComplete(final Cursor cursor) {
        if (mContext.isDestroyed() || mContext.isFinishing()) {
            return;
        }
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imagesLoadedListener.onImagesLoaded(mVideoSetList);
                close(cursor);
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private int hasColumn(Cursor data, String id) {
        return data.getColumnIndex(id);
    }

    private int getInt(Cursor data, String text) {
        int hasColumn = hasColumn(data, text);
        if (hasColumn != -1) {
            return data.getInt(hasColumn);
        } else {
            return 0;
        }
    }

    private long getLong(Cursor data, String text) {
        int index = hasColumn(data, text);
        if (index != -1) {
            return data.getLong(index);
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
}
