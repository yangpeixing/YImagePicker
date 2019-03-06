package com.example.ypxredbookpicker.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.example.ypxredbookpicker.R;
import com.example.ypxredbookpicker.bean.ImageItem;
import com.example.ypxredbookpicker.bean.ImageSet;
import com.example.ypxredbookpicker.data.DataSource;
import com.example.ypxredbookpicker.data.OnImagesLoadedListener;
import com.example.ypxredbookpicker.utils.DateUtil;
import com.example.ypxredbookpicker.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>DataSource of Android local Database</b><br/>
 * Created by Eason.Lai on 2015/11/1 10:42
 * contact：easonline7@gmail.com
 */
public class LocalDataSource implements DataSource, LoaderManager.LoaderCallbacks<Cursor> {

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media._ID};

    // different loader define
    public static final int LOADER_ALL = 0;
    public static final int LOADER_CATEGORY = 1;

    private OnImagesLoadedListener imagesLoadedListener;
    private Context mContext;
    private ArrayList<ImageSet> mImageSetList = new ArrayList<>();

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (mContext instanceof FragmentActivity) {
            ((FragmentActivity) mContext).getSupportLoaderManager().initLoader(LOADER_ALL, null, this);
        } else {
            throw new RuntimeException("your activity must be instance of FragmentActivity");
        }
    }

    public LocalDataSource(Context ctx) {
        this.mContext = ctx;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = new String[]{"image/jpeg", "image/png"};
        if (id == LOADER_ALL) {
            //scan all
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[5] + ">0 AND  " + IMAGE_PROJECTION[6] + "=? OR " + IMAGE_PROJECTION[6] + "=?  ",
                    selectionArgs,
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        } else if (id == LOADER_CATEGORY) {
            //scan one dir
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[5] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'",
                    null,
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageSetList.clear();
        if (data != null) {
            List<ImageItem> allImages = new ArrayList<>();
            int count = data.getCount();
            if (count <= 0) {
                return;
            }

            data.moveToFirst();
            do {
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                if (size == 0) {
                    continue;
                }

                if (imageWidth == 0 || imageHeight == 0) {
                    int[] imageSize =FileUtil.getImageWidthHeight(imagePath);
                    if (imageSize[0] == 0 || imageSize[1] == 0) {
                        continue;
                    } else {
                        imageWidth = imageSize[0];
                        imageHeight = imageSize[1];
                    }
                }

                ImageItem item = new ImageItem(imagePath, imageName, imageWidth, imageHeight, imageAddedTime);
                item.setTimeFormat(DateUtil.getStrTime(imageAddedTime));
                allImages.add(item);

                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();

                ImageSet imageSet = new ImageSet();
                imageSet.name = imageParentFile.getName();
                imageSet.path = imageParentFile.getAbsolutePath();
                imageSet.cover = item;

                if (!mImageSetList.contains(imageSet)) {
                    List<ImageItem> imageList = new ArrayList<>();
                    imageList.add(item);
                    imageSet.imageItems = imageList;
                    mImageSetList.add(imageSet);
                } else {
                    mImageSetList.get(mImageSetList.indexOf(imageSet)).imageItems.add(item);
                }

            } while (data.moveToNext());

            ImageSet imageSetAll = new ImageSet();
            imageSetAll.name = mContext.getResources().getString(R.string.all_images);
            imageSetAll.cover = allImages.get(0);
            imageSetAll.imageItems = allImages;
            imageSetAll.path = "/";

            if (mImageSetList.contains(imageSetAll)) {
                mImageSetList.remove(imageSetAll);
            }
            mImageSetList.add(0, imageSetAll);
            imagesLoadedListener.onImagesLoaded(mImageSetList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
