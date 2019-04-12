package com.ypx.imagepicker.data.impl;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.utils.DateUtil;
import com.ypx.imagepicker.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据加载
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class VideoDataSource implements DataSource, LoaderManager.LoaderCallbacks<Cursor> {

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
    private Context mContext;
    private ArrayList<ImageSet> mImageSetList = new ArrayList<>();

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (mContext instanceof FragmentActivity) {
            ((FragmentActivity) mContext).getSupportLoaderManager().initLoader(999, null, this);
        } else {
            throw new RuntimeException("your activity must be instance of FragmentActivity");
        }
    }

    public VideoDataSource(Context ctx) {
        this.mContext = ctx;
    }

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mImageSetList.size() > 0) {
            return;
        }
        mImageSetList.clear();
        if (data != null) {
            List<ImageItem> allImages = new ArrayList<>();
            String thumbPath = "";
            int count = data.getCount();
            if (count <= 0) {
                return;
            }

            data.moveToFirst();
            do {
                int videoId = data.getInt(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String imagePath = data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
                long duration = data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                if (duration == 0) {
                    continue;
                }
                MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
                Cursor cursor = mContext.getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                        , projection
                        , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                        , new String[]{videoId + ""}
                        , null);

                while (cursor.moveToNext()) {
                    thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }
                cursor.close();

                ImageItem item = new ImageItem(imagePath, duration, thumbPath);
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
            imageSetAll.name = "所有视频";
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
