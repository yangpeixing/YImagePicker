package com.ypx.imagepicker.data.impl;

import android.app.Application;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;


public enum MediaObserver {
    instance;

    private boolean isMediaChanged = true;

    public boolean isMediaChanged() {
        return isMediaChanged;
    }

    public void setMediaChanged(boolean mediaChanged) {
        isMediaChanged = mediaChanged;
    }

    public void register(Application application) {
        PhotoObserver mPhotoObserver = new PhotoObserver(new Handler());
        // 注册观察者
        application.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true, mPhotoObserver);

        VideoObserver mVideoObserver = new VideoObserver(new Handler());
        application.getContentResolver().registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true, mVideoObserver);
    }


    // 监听图片变化
    class PhotoObserver extends ContentObserver {
        PhotoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            isMediaChanged = true;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            isMediaChanged = true;
        }
    }

    // 监听视频变化
    class VideoObserver extends ContentObserver {
        private VideoObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            isMediaChanged = true;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            isMediaChanged = true;
        }
    }

}
