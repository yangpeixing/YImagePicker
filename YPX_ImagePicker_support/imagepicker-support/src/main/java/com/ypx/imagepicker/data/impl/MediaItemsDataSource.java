package com.ypx.imagepicker.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;


import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.utils.PDateUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;


/**
 * Description: 媒体数据
 * <p>
 * Author: peixing.yang
 * Date: 2019/4/11
 */
public class MediaItemsDataSource implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private WeakReference<FragmentActivity> mContext;
    private LoaderManager mLoaderManager;
    private MediaItemProvider mediaItemProvider;
    private boolean isLoadVideo;
    private boolean isLoadImage;

    private Set<MimeType> mimeTypeSet = MimeType.ofAll();

    public MediaItemsDataSource setMimeTypeSet(PickerSelectConfig config) {
        mimeTypeSet = MimeType.ofAll();
        if (!config.isShowImage()) {
            mimeTypeSet.removeAll(MimeType.ofImage());
        } else {
            if (!config.isLoadGif()) {
                mimeTypeSet.remove(MimeType.GIF);
            }
        }
        isLoadImage = config.isShowImage();
        isLoadVideo = config.isShowVideo();
        if (!config.isShowVideo()) {
            mimeTypeSet.removeAll(MimeType.ofVideo());
        }

        return this;
    }

    public void loadMediaItems(MediaItemProvider mediaItemProvider) {
        this.mediaItemProvider = mediaItemProvider;
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public static MediaItemsDataSource create(FragmentActivity activity, ImageSet set) {
        return new MediaItemsDataSource(activity, set);
    }

    private ImageSet set;

    private MediaItemsDataSource(FragmentActivity activity, ImageSet set) {
        this.set = set;
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
        return MediaItemsLoader.newInstance(context, set, mimeTypeSet);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor cursor) {

        final FragmentActivity context = mContext.get();
        if (context == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<ImageItem> imageItems = new ArrayList<>();
                ArrayList<ImageItem> allVideoItems = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        ImageItem item = new ImageItem();
                        item.id = getLong(cursor, MediaStore.Files.FileColumns._ID);
                        item.mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                        item.path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                        item.width = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                        item.height = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT));
                        item.duration = getLong(cursor, MediaStore.Video.Media.DURATION);
                        item.setVideo(MimeType.isVideo(item.mimeType));
                        if (item.duration > 0) {
                            item.durationFormat = PDateUtil.getVideoDuration(item.duration);
                        }
                        item.time = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                        if (item.time > 0) {
                            item.timeFormat = PDateUtil.getStrTime(item.time);
                        }

                        if (set.isAllMedia() && isLoadVideo && isLoadImage) {
                            if (item.isVideo()) {
                                allVideoItems.add(item);
                            }
                        }
                        imageItems.add(item);

                        if (preloadProvider != null && imageItems.size() == 40) {
                            notifyPreloadItem(context, imageItems);
                        }

                    } while (!context.isDestroyed() && cursor.moveToNext());
                }

                ImageSet allVideoSet = null;
                if (allVideoItems.size() > 0) {
                    allVideoSet = new ImageSet();
                    allVideoSet.id = ImageSet.ID_ALL_VIDEO;
                    allVideoSet.coverPath = allVideoItems.get(0).path;
                    allVideoSet.cover = allVideoItems.get(0);
                    allVideoSet.count = allVideoItems.size();
                    allVideoSet.imageItems = allVideoItems;
                    allVideoSet.name = context.getResources().getString(R.string.str_allvideo);
                }

                final ImageSet finalAllVideoSet = allVideoSet;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (context.isDestroyed()) {
                            return;
                        }
                        if (mediaItemProvider != null) {
                            mediaItemProvider.providerMediaItems(imageItems, finalAllVideoSet);
                        }

                        if (mLoaderManager != null) {
                            mLoaderManager.destroyLoader(LOADER_ID);
                        }
                    }
                });
            }
        }).start();

    }

    private void notifyPreloadItem(final FragmentActivity context, final ArrayList<ImageItem> imageItems) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (context.isDestroyed()) {
                    return;
                }
                preloadProvider.providerMediaItems(imageItems);
                preloadProvider = null;
            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    public interface MediaItemProvider {
        void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet);
    }

    private MediaItemPreloadProvider preloadProvider;

    public void setPreloadProvider(MediaItemPreloadProvider preloadProvider) {
        this.preloadProvider = preloadProvider;
    }

    public interface MediaItemPreloadProvider {
        void providerMediaItems(ArrayList<ImageItem> imageItems);
    }

    private long getLong(Cursor data, String text) {
        int index = hasColumn(data, text);
        if (index != -1) {
            return data.getLong(index);
        } else {
            return 0;
        }
    }

    private int hasColumn(Cursor data, String id) {
        return data.getColumnIndex(id);
    }

}
