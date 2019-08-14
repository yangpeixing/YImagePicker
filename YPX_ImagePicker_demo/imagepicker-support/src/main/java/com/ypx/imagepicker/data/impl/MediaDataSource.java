package com.ypx.imagepicker.data.impl;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Description: 媒体数据
 * <p>
 * Author: peixing.yang
 * Date: 2019/4/11
 */
public class MediaDataSource implements DataSource {
    private VideoDataSource videoDataSource;
    private ImageDataSource imageDataSource;
    private OnImagesLoadedListener imagesLoadedListener;
    private FragmentActivity context;
    private boolean isImageLoaded = false;
    private boolean isVideoLoaded = false;
    private ArrayList<ImageSet> imageSetList;
    private ArrayList<ImageSet> videoSetList;
    private ArrayList<ImageSet> allSetList = new ArrayList<>();

    public MediaDataSource(FragmentActivity context) {
        this.context = context;
        imageSetList = new ArrayList<>();
        videoSetList = new ArrayList<>();
        videoDataSource = new VideoDataSource(context);
        imageDataSource = new ImageDataSource(context);
    }

    private boolean isLoadGif = true;
    private boolean isLoadImage = true;
    private boolean isLoadVideo = true;

    public void setLoadGif(boolean isLoadGif) {
        this.isLoadGif = isLoadGif;
    }

    public void setLoadImage(boolean isLoadImage) {
        this.isLoadImage = isLoadImage;
    }

    public void setLoadVideo(boolean isLoadVideo) {
        this.isLoadVideo = isLoadVideo;
    }

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (isLoadImage) {//加载图片
            imageDataSource.setLoadGif(isLoadGif);
            imageDataSource.provideMediaItems(new OnImagesLoadedListener() {
                @Override
                public void onImagesLoaded(List<ImageSet> mImageSetList) {
                    resetList(imageSetList, mImageSetList);
                    isImageLoaded = true;
                    if (isLoadVideo) {//如果加载视频，则整合图片和视频
                        compressImageAndVideo();
                    } else {
                        ImagePicker.isPreloadOk = true;
                        imagesLoadedListener.onImagesLoaded(mImageSetList);
                    }
                }
            });
        }

        if (isLoadVideo) {//加载视频
            videoDataSource.provideMediaItems(new OnImagesLoadedListener() {
                @Override
                public void onImagesLoaded(List<ImageSet> mImageSetList) {
                    resetList(videoSetList, mImageSetList);
                    isVideoLoaded = true;
                    if (isLoadImage) {//如果加载图片，则整合图片和视频
                        compressImageAndVideo();
                    } else {
                        ImagePicker.isPreloadOk = true;
                        imagesLoadedListener.onImagesLoaded(mImageSetList);
                    }
                }
            });
        }
    }

    private void resetList(ArrayList<ImageSet> newList, List<ImageSet> oldList) {
        newList.clear();
        for (ImageSet imageSet : oldList) {
            newList.add(imageSet.copy());
        }
    }


    /**
     * 整合图片和视频，按照时间顺序排序
     */
    private synchronized void compressImageAndVideo() {
        if (!isImageLoaded || !isVideoLoaded) {
            return;
        }

        boolean isHasVideo = videoSetList != null && videoSetList.size() > 0;
        boolean isHasImage = imageSetList != null && imageSetList.size() > 0;
        if (!isHasVideo && !isHasImage) {
            Toast.makeText(context, "未找到媒体文件!", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context.isDestroyed()) {
                    return;
                }
                compress();
            }
        }).start();
    }

    /**
     * 数据整合
     */
    private void compress() {
        if (imageSetList == null) {
            imageSetList = new ArrayList<>();
        }

        if (videoSetList == null) {
            videoSetList = new ArrayList<>();
        }

        ImageSet allImageSet = null;
        ImageSet allVideoSet = null;

        //将所有图片打包到全部媒体文件列表
        ArrayList<ImageItem> allMediaItems = new ArrayList<>();
        if (imageSetList.size() > 0) {
            allImageSet = imageSetList.get(0);
            if (allImageSet.imageItems != null) {
                allMediaItems.addAll(allImageSet.imageItems);
            }
        }
        //将所有视频打包到全部媒体文件列表
        if (videoSetList.size() > 0) {
            allVideoSet = videoSetList.get(0);
            if (allVideoSet.imageItems != null) {
                allMediaItems.addAll(allVideoSet.imageItems);
            }
        }
        //排序全部媒体文件列表
        sort(allMediaItems);

        //清空所有文件夹
        allSetList.clear();
        if (allMediaItems.size() == 0) {
            notifyOnLoaded();
            return;
        }

        //添加第一个本地文件夹
        ImageSet allMediaSet = new ImageSet();
        allMediaSet.name = context.getString(R.string.str_allmedia);
        allMediaSet.imageItems = allMediaItems;
        allMediaSet.cover = allMediaItems.get(0);
        allMediaSet.isSelected = true;
        allSetList.add(allMediaSet);

        if (allImageSet != null) {
            allSetList.add(allImageSet);
        }

        if (allVideoSet != null) {
            allSetList.add(allVideoSet);
        }

        if (videoSetList.size() > 0) {
            compressImageSet(videoSetList, imageSetList);
        } else if (imageSetList.size() > 0) {
            compressImageSet(imageSetList, videoSetList);
        }

        notifyOnLoaded();
    }

    /**
     * 整合文件夹文件
     *
     * @param list1 视频或图片文件夹列表
     * @param list2 视频或图片文件夹列表
     */
    private void compressImageSet(ArrayList<ImageSet> list1, ArrayList<ImageSet> list2) {
        //遍历视频文件夹
        for (ImageSet videoSet : list1) {
            //遍历图片文件夹
            for (ImageSet imageSet : list2) {
                //如果视频和图片是同一文件夹，则需要合并并排序
                //否则将视频文件夹加到全部文件夹列表中
                if (videoSet.equals(imageSet)) {
                    imageSet.imageItems.addAll(videoSet.imageItems);
                    sort(imageSet.imageItems);
                }

                if (!allSetList.contains(imageSet)) {
                    allSetList.add(imageSet);
                }
            }

            if (!allSetList.contains(videoSet)) {
                allSetList.add(videoSet);
            }
        }
    }

    private void notifyOnLoaded() {
        if (context.isDestroyed() || context.isFinishing()) {
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImagePicker.isPreloadOk = true;
                if (imagesLoadedListener != null) {
                    imagesLoadedListener.onImagesLoaded(allSetList);
                }
            }
        });
    }


    private void sort(List<ImageItem> imageItemList) {
        //对媒体数据进行排序
        Collections.sort(imageItemList, new Comparator<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) {
                return Long.compare(o2.time, o1.time);
            }
        });
    }
}
