package com.ypx.imagepicker.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.utils.PDateUtil;
import com.ypx.imagepicker.utils.PFileUtil;

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
    private int preloadSize = 40;
    private Set<MimeType> mimeTypeSet = MimeType.ofAll();

    public MediaItemsDataSource setMimeTypeSet(BaseSelectConfig config) {
        mimeTypeSet = config.getMimeTypes();
        isLoadVideo = config.isShowVideo();
        return this;
    }

    public MediaItemsDataSource setMimeTypeSet(Set<MimeType> mimeTypeSet) {
        this.mimeTypeSet = mimeTypeSet;
        for (MimeType mimeType : mimeTypeSet) {
            if (MimeType.ofVideo().contains(mimeType)) {
                isLoadVideo = true;
            }
        }
        return this;
    }

    public MediaItemsDataSource preloadSize(int preloadSize) {
        this.preloadSize = preloadSize;
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
                if (!context.isDestroyed() && !cursor.isClosed() && cursor.moveToFirst()) {
                    do {
                        ImageItem item = new ImageItem();
                        item.id = getLong(cursor, MediaStore.Files.FileColumns._ID);
                        item.mimeType = getString(cursor, MediaStore.MediaColumns.MIME_TYPE);
                        item.path = getString(cursor, MediaStore.Files.FileColumns.DATA);
                        item.width = getInt(cursor, MediaStore.Files.FileColumns.WIDTH);
                        item.height = getInt(cursor, MediaStore.Files.FileColumns.HEIGHT);
                        item.duration = getLong(cursor, MediaStore.Video.Media.DURATION);
                        item.setVideo(MimeType.isVideo(item.mimeType));
                        if (item.path == null || item.path.length() == 0) {
                            continue;
                        }
                        if (item.isVideo() && item.duration == 0) {
                            continue;
                        }
                        if (item.duration > 0) {
                            item.durationFormat = PDateUtil.getVideoDuration(item.duration);
                        }
                        item.time = getLong(cursor, MediaStore.Files.FileColumns.DATE_MODIFIED);
                        if (item.time > 0) {
                            item.timeFormat = PDateUtil.getStrTime(item.time);
                        }

                        if (item.width == 0 || item.height == 0) {
                            int[] size = PFileUtil.getImageWidthHeight(item.path);
                            item.width = size[0];
                            item.height = size[1];
                        }

                        if (set.isAllMedia() && isLoadVideo) {
                            if (item.isVideo()) {
                                allVideoItems.add(item);
                            }
                        }
                        imageItems.add(item);

                        if (preloadProvider != null && imageItems.size() == preloadSize) {
                            notifyPreloadItem(context, imageItems);
                        }
                    } while (!context.isDestroyed() && !cursor.isClosed() && cursor.moveToNext());
                }

                ImageSet allVideoSet = null;
                if (allVideoItems.size() > 0) {
                    allVideoSet = new ImageSet();
                    allVideoSet.id = ImageSet.ID_ALL_VIDEO;
                    allVideoSet.coverPath = allVideoItems.get(0).path;
                    allVideoSet.cover = allVideoItems.get(0);
                    allVideoSet.count = allVideoItems.size();
                    allVideoSet.imageItems = allVideoItems;
                    allVideoSet.name = context.getResources().getString(R.string.picker_str_all_video);
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
        return data.getColumnIndex(id);
    }

}
