package com.ypx.imagepicker.data.impl;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/4/11
 */
public class MediaDataSource implements DataSource {
    private VideoDataSource videoDataSource;
    private ImageDataSource imageDataSource;
    private OnImagesLoadedListener imagesLoadedListener;
    private Context context;
    private boolean isImageLoaded = false;
    private boolean isVideoLoaded = false;
    private List<ImageSet> imageSetList;
    private List<ImageSet> videoSetList;
    private List<ImageSet> allSetList;

    public MediaDataSource(Context context) {
        this.context = context;
        allSetList = new ArrayList<>();
        imageSetList = new ArrayList<>();
        videoSetList = new ArrayList<>();
        videoDataSource = new VideoDataSource(context);
        imageDataSource = new ImageDataSource(context);
    }

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        videoDataSource.provideMediaItems(new OnImagesLoadedListener() {
            @Override
            public void onImagesLoaded(List<ImageSet> mImageSetList) {
                videoSetList = mImageSetList;
                isVideoLoaded = true;
                compressImageAndVideo();
            }
        });

        imageDataSource.provideMediaItems(new OnImagesLoadedListener() {
            @Override
            public void onImagesLoaded(List<ImageSet> mImageSetList) {
                imageSetList = mImageSetList;
                isImageLoaded = true;
                compressImageAndVideo();
            }
        });
    }


    private void compressImageAndVideo() {
        if (!isImageLoaded || !isVideoLoaded) {
            return;
        }

        if (videoSetList == null || imageSetList == null) {
            return;
        }

        if (videoSetList.size() == 0 && imageSetList.size() == 0) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                compress();
            }
        }).start();

    }

    private void compress() {
        allSetList.clear();
        if (videoSetList.size() > 0) {
            fore(videoSetList, imageSetList);
        } else {
            fore(imageSetList, videoSetList);
        }

        List<ImageItem> allMediaItems = new ArrayList<>();
        if (imageSetList.size() > 0) {
            allMediaItems.addAll(imageSetList.get(0).imageItems);
        }
        if (videoSetList.size() > 0) {
            allMediaItems.addAll(videoSetList.get(0).imageItems);
        }

        sort(allMediaItems);

        ImageSet allMediaSet = new ImageSet();
        allMediaSet.name = "所有文件";
        allMediaSet.imageItems = allMediaItems;
        allMediaSet.cover = allMediaItems.get(0);

        allSetList.add(0, allMediaSet);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imagesLoadedListener != null) {
                    imagesLoadedListener.onImagesLoaded(allSetList);
                }
            }
        });
    }

    private void fore(List<ImageSet> a, List<ImageSet> b) {
        for (ImageSet videoSet : a) {
            for (ImageSet imageSet : b) {
                if (videoSet.equals(imageSet)) {
                    //同一个文件夹
                    imageSet.imageItems.addAll(videoSet.imageItems);
                    sort(imageSet.imageItems);
                }
                if (!isContainsImageSet(imageSet)) {
                    allSetList.add(imageSet);
                }
            }
            if (!isContainsImageSet(videoSet)) {
                allSetList.add(videoSet);
            }
        }
    }

    private boolean isContainsImageSet(ImageSet imageSet) {
        for (ImageSet imageSet1 : allSetList) {
            if (imageSet1.equals(imageSet)) {
                return true;
            }
        }
        return false;
    }

    private void sort(List<ImageItem> imageItemList) {
        //对媒体数据进行排序
        Collections.sort(imageItemList, new Comparator<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) {
                if (o1.time > o2.time) {
                    return -1;
                } else if (o1.time < o2.time) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }
}
