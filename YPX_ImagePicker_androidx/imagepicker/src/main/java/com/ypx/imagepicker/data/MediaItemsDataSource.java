package com.ypx.imagepicker.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.utils.PDateUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Set;

import static com.ypx.imagepicker.data.MediaStoreConstants.QUERY_URI;


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
    int i=0;

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
                        item.id = getLong(cursor, MediaStore.Files.FileColumns._ID);
                        item.mimeType = getString(cursor, MediaStore.MediaColumns.MIME_TYPE);
                        item.path = getUri(item.id, item.mimeType).toString();
                        //androidQ上废弃了DATA绝对路径，需要手动拼凑Uri
//                        if (MediaStoreConstants.isBeforeAndroidQ()) {
//                            item.path = getString(cursor, MediaStore.Files.FileColumns.DATA);
//                        } else {
//                            item.path = item.uri.toString();
//                        }
                        item.width = getInt(cursor, MediaStore.Files.FileColumns.WIDTH);
                        item.height = getInt(cursor, MediaStore.Files.FileColumns.HEIGHT);
                        item.setVideo(MimeType.isVideo(item.mimeType));
                        item.time = getLong(cursor, MediaStore.Files.FileColumns.DATE_MODIFIED);
                        item.timeFormat = PDateUtil.getStrTime(item.time);
                    } catch (Exception e) {
                        continue;
                    }

                    //没有查询到路径
                    if (item.path == null || item.path.length() == 0) {
                        continue;
                    }

                    //视频
                    if (item.isVideo()) {
                        item.duration = getLong(cursor, "duration");
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
//                        if (item.width == 0 || item.height == 0) {
//                            int[] size = PBitmapUtils.getImageWidthHeight(context, item.getUri());
//                            item.width = size[0];
//                            item.height = size[1];
//                        }

                        //如果手动获取的宽或高为0，则不加载此item
//                        if (item.width == 0 || item.height == 0) {
//                            continue;
//                        }
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
                    Log.e("run", "run: "+i );
                    if (mediaItemProvider != null) {
                        mediaItemProvider.providerMediaItems(imageItems, finalAllVideoSet);
                    }

                    if (mLoaderManager != null) {
                        mLoaderManager.destroyLoader(LOADER_ID);
                    }
                }
            });
        }
    };

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

    private static Uri getUri(long id, String mimeType) {
        Uri contentUri;
        if (MimeType.isImage(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (MimeType.isVideo(mimeType)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = QUERY_URI;
        }
        return ContentUris.withAppendedId(contentUri, id);
    }


}
