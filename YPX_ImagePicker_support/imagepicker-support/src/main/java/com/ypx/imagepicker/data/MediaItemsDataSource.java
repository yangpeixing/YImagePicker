package com.ypx.imagepicker.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.utils.PDateUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;


import static com.ypx.imagepicker.data.MediaStoreConstants.DATA;
import static com.ypx.imagepicker.data.MediaStoreConstants.DATE_MODIFIED;
import static com.ypx.imagepicker.data.MediaStoreConstants.DISPLAY_NAME;
import static com.ypx.imagepicker.data.MediaStoreConstants.DURATION;
import static com.ypx.imagepicker.data.MediaStoreConstants.HEIGHT;
import static com.ypx.imagepicker.data.MediaStoreConstants.MIME_TYPE;
import static com.ypx.imagepicker.data.MediaStoreConstants.WIDTH;
import static com.ypx.imagepicker.data.MediaStoreConstants._ID;


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
    private int preloadSize = 40;
    private Set<MimeType> mimeTypeSet = MimeType.ofAll();

    public MediaItemsDataSource setMimeTypeSet(BaseSelectConfig config) {
        mimeTypeSet = config.getMimeTypes();
        return this;
    }

    public MediaItemsDataSource setMimeTypeSet(Set<MimeType> mimeTypeSet) {
        this.mimeTypeSet = mimeTypeSet;
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

    private Cursor cursor;
    private Thread thread;

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor cursor) {
        final FragmentActivity context = mContext.get();
        if (context == null | cursor == null || cursor.isClosed()) {
            return;
        }
        this.cursor = cursor;
        if (thread != null && thread.isAlive()) {
            return;
        }
        thread = new Thread(runnable);
        thread.start();
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final FragmentActivity context = mContext.get();
            final ArrayList<ImageItem> imageItems = new ArrayList<>();
            ArrayList<ImageItem> allVideoItems = new ArrayList<>();
            if (!context.isDestroyed() && !cursor.isClosed() && cursor.moveToFirst()) {
                do {
                    ImageItem item = new ImageItem();
                    try {
                        item.id = getLong(cursor, _ID);
                        item.mimeType = getString(cursor, MIME_TYPE);
                        item.displayName = getString(cursor, DISPLAY_NAME);
                        //androidQ上废弃了DATA绝对路径，需要手动拼凑Uri，这里为了兼容大部分项目还没有适配androidQ的情况
                        //默认path还是先取绝对路径，取不到或者异常才去取Uri路径
                        /*if (MediaStoreConstants.isBeforeAndroidQ()) {
                            item.path = getConstants(cursor, MediaStore.Files.FileColumns.DATA);
                        } else {
                            item.path = getUri(item.id, item.mimeType).toString();
                        }*/
                        try {
                            item.path = getString(cursor, DATA);
                        } catch (Exception ignored) {

                        }

                        Uri urlPath = item.getUri();
                        if (urlPath != null) {
                            item.setUriPath(urlPath.toString());
                        }

                        if (item.path == null || item.path.length() == 0) {
                            item.path = urlPath.toString();
                        }

                        item.width = getInt(cursor, WIDTH);
                        item.height = getInt(cursor, HEIGHT);
                        item.setVideo(MimeType.isVideo(item.mimeType));
                        item.time = getLong(cursor, DATE_MODIFIED);
                        item.timeFormat = PDateUtil.getStrTime(context, item.time);
                    } catch (Exception e) {
                        continue;
                    }

                    //没有查询到路径
                    if (item.path == null || item.path.length() == 0) {
                        continue;
                    }

                    //视频
                    if (item.isVideo()) {
                        item.duration = getLong(cursor, DURATION);
                        if (item.duration == 0) {
                            continue;
                        }
                        item.durationFormat = PDateUtil.getVideoDuration(item.duration);

                        //如果当前加载的是全部文件，需要拼凑一个全部视频的虚拟文件夹
                        if (set.isAllMedia()) {
                            allVideoItems.add(item);
                        }
                    }
                    //图片
                    else {
                        //如果媒体信息中不包含图片的宽高，则手动获取文件宽高
                        if (item.width == 0 || item.height == 0) {
                            if (!item.isUriPath()) {
                                int[] size = PBitmapUtils.getImageWidthHeight(item.path);
                                item.width = size[0];
                                item.height = size[1];
                            }
                        }
                    }
                    //添加到文件列表中
                    imageItems.add(item);
                    //回调预加载数据源
                    if (preloadProvider != null && imageItems.size() == preloadSize) {
                        notifyPreloadItem(context, imageItems);
                    }
                } while (!context.isDestroyed() && !cursor.isClosed() && cursor.moveToNext());
            }
            //手动生成一个虚拟的全部视频文件夹
            ImageSet allVideoSet = null;
            if (allVideoItems.size() > 0) {
                allVideoSet = new ImageSet();
                allVideoSet.id = ImageSet.ID_ALL_VIDEO;
                allVideoSet.coverPath = allVideoItems.get(0).path;
                allVideoSet.cover = allVideoItems.get(0);
                allVideoSet.count = allVideoItems.size();
                allVideoSet.imageItems = allVideoItems;
                allVideoSet.name = context.getString(R.string.picker_str_folder_item_video);
            }
            //回调所有数据
            notifyMediaItem(context, imageItems, allVideoSet);
        }
    };

    /**
     * 回调预加载的媒体文件，主线程
     *
     * @param context    FragmentActivity
     * @param imageItems 预加载列表
     */
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

    /**
     * 回调所有数据
     *
     * @param context     FragmentActivity
     * @param imageItems  所有文件
     * @param allVideoSet 当加载所有媒体库文件时，默认会生成一个全部视频的文件夹，是本地虚拟的文件夹
     */
    private void notifyMediaItem(final FragmentActivity context, final ArrayList<ImageItem> imageItems,
                                 final ImageSet allVideoSet) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (context.isDestroyed()) {
                    return;
                }
                if (mediaItemProvider != null) {
                    mediaItemProvider.providerMediaItems(imageItems, allVideoSet);
                }

                if (mLoaderManager != null) {
                    mLoaderManager.destroyLoader(LOADER_ID);
                }
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
        try {
            return data.getColumnIndexOrThrow(id);
        } catch (Exception e) {
            return -1;
        }
    }
}
