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
import com.ypx.imagepicker.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;


/**
 * Description: 数据加载
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
class ImageDataSource implements DataSource, LoaderManager.LoaderCallbacks<Cursor> {
    private final int ID = 888;
    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media._ID};

    private OnImagesLoadedListener imagesLoadedListener;
    private FragmentActivity mContext;
    private static ArrayList<ImageSet> mImageSetList = new ArrayList<>();
    private static boolean lastIsLoadGif = true;

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (imagesLoadedListener == null || mContext == null) {
            return;
        }
        //如果缓存中有数据，并且媒体库没有数据更新，则直接加载缓存数据
        if (mImageSetList != null && mImageSetList.size() > 0
                && MediaObserver.instance.isMediaNotChanged()) {
            imagesLoadedListener.onImagesLoaded(mImageSetList);
            return;
        }

        clearList();
        LoaderManager.getInstance(mContext).initLoader(ID, null, this);
    }

    private boolean isLoadGif;

    void setLoadGif(boolean loadGif) {
        isLoadGif = loadGif;
        if (isLoadGif != lastIsLoadGif) {
            lastIsLoadGif = isLoadGif;
            clearList();
        }
    }

    private void clearList() {
        if (mImageSetList != null) {
            mImageSetList.clear();
        } else {
            mImageSetList = new ArrayList<>();
        }
    }

    ImageDataSource(FragmentActivity ctx) {
        this.mContext = ctx;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs;
        if (isLoadGif) {
            selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/webp", "image/gif"};
            return new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[5] + ">0 AND  " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=?  ",
                    selectionArgs,
                    IMAGE_PROJECTION[2] + " DESC");
        } else {
            selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/webp"};
            return new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[5] + ">0 AND  " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=? OR " +
                            IMAGE_PROJECTION[6] + "=?  ",
                    selectionArgs,
                    IMAGE_PROJECTION[2] + " DESC");
        }
    }


    private void close(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        MediaObserver.instance.setMediaChanged(false);
        LoaderManager.getInstance(mContext).destroyLoader(ID);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor data) {
        if (mContext.isDestroyed()) {
            close(data);
            return;
        }

        if (data == null || data.getCount() <= 0 || data.isClosed()) {
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

    private void compressToList(Cursor data) {
        ArrayList<ImageItem> allImages = new ArrayList<>();
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            String imagePath = getString(data, IMAGE_PROJECTION[0]);
            int imageWidth = getInt(data, IMAGE_PROJECTION[3]);
            int imageHeight = getInt(data, IMAGE_PROJECTION[4]);
            int size = getInt(data, IMAGE_PROJECTION[5]);
            long imageAddedTime = getLong(data, IMAGE_PROJECTION[2]);
            if (size == 0 || imagePath.length() == 0) {
                continue;
            }

            if (imageWidth == 0 || imageHeight == 0) {
                int[] imageSize = FileUtil.getImageWidthHeight(imagePath);
                if (imageSize[0] == 0 || imageSize[1] == 0) {
                    continue;
                } else {
                    imageWidth = imageSize[0];
                    imageHeight = imageSize[1];
                }
            }

            ImageItem item = new ImageItem(imagePath, imageWidth, imageHeight, imageAddedTime);
            item.setVideo(false);
            item.setTimeFormat(DateUtil.getStrTime(imageAddedTime));
            allImages.add(item);

            File imageFile = new File(imagePath);
            File imageParentFile = imageFile.getParentFile();

            ImageSet imageSet = new ImageSet();
            imageSet.name = imageParentFile.getName();
            imageSet.path = imageParentFile.getAbsolutePath();
            imageSet.cover = item;

            if (mImageSetList == null) {
                mImageSetList = new ArrayList<>();
            }
            if (mImageSetList.size() > 0 && mImageSetList.contains(imageSet)) {
                int index = mImageSetList.indexOf(imageSet);
                if (index >= 0 && index < mImageSetList.size()) {
                    ArrayList<ImageItem> imageItems = mImageSetList.get(index).imageItems;
                    if (imageItems != null) {
                        imageItems.add(item);
                    }
                }
            } else {
                ArrayList<ImageItem> imageList = new ArrayList<>();
                imageList.add(item);
                imageSet.imageItems = imageList;
                mImageSetList.add(imageSet);
            }

        }

        if (mImageSetList.size() > 0) {
            ImageSet imageSetAll = new ImageSet();
            imageSetAll.name = mContext.getResources().getString(R.string.all_images);
            imageSetAll.cover = allImages.get(0);
            imageSetAll.imageItems = allImages;
            imageSetAll.path = "/";
            mImageSetList.add(0, imageSetAll);
        }

        notifyLoadComplete(data);
    }

    private void notifyLoadComplete(final Cursor cursor) {
        if (!mContext.isDestroyed()) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imagesLoadedListener.onImagesLoaded(mImageSetList);
                    close(cursor);
                }
            });
        }
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
