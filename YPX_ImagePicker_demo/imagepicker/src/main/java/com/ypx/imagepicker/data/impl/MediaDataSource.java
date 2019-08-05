package com.ypx.imagepicker.data.impl;

import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

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
    private List<ImageSet> imageSetList;
    private List<ImageSet> videoSetList;
    private List<ImageSet> allSetList = new ArrayList<>();

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
                    imageSetList = mImageSetList;
                    isImageLoaded = true;
                    if (isLoadVideo) {//如果加载视频，则整合图片和视频
                        compressImageAndVideo();
                    } else {
                        imagesLoadedListener.onImagesLoaded(mImageSetList);
                    }
                }
            });
        }

        if (isLoadVideo) {//加载视频
            videoDataSource.provideMediaItems(new OnImagesLoadedListener() {
                @Override
                public void onImagesLoaded(List<ImageSet> mImageSetList) {
                    videoSetList = mImageSetList;
                    isVideoLoaded = true;
                    if (isLoadImage) {//如果加载图片，则整合图片和视频
                        compressImageAndVideo();
                    } else {
                        imagesLoadedListener.onImagesLoaded(mImageSetList);
                    }
                }
            });
        }
    }


    /**
     * 整合图片和视频，按照时间顺序排序
     */
    private void compressImageAndVideo() {
        if (!isImageLoaded || !isVideoLoaded) {
            return;
        }
        boolean isHasVideo = videoSetList != null && videoSetList.size() > 0;
        boolean isHasImage = imageSetList != null && imageSetList.size() > 0;
        if (!isHasVideo && !isHasImage) {
            Toast.makeText(context, "未找到相关文件!", Toast.LENGTH_SHORT).show();
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
        allSetList.clear();

        if (videoSetList != null && videoSetList.size() > 0) {
            fore(videoSetList, imageSetList);
        } else if (imageSetList != null && imageSetList.size() > 0) {
            fore(imageSetList, videoSetList);
        }

        ArrayList<ImageItem> allMediaItems = new ArrayList<>();
        if (imageSetList != null && imageSetList.size() > 0) {
            allMediaItems.addAll(imageSetList.get(0).imageItems);
        }

        if (videoSetList != null && videoSetList.size() > 0) {
            allMediaItems.addAll(videoSetList.get(0).imageItems);
        }

        sort(allMediaItems);

        ImageSet allMediaSet = new ImageSet();
        allMediaSet.name = context.getString(R.string.str_allmedia);
        allMediaSet.imageItems = allMediaItems;
        allMediaSet.cover = allMediaItems.get(0);
        allMediaSet.isSelected = true;
        allSetList.add(0, allMediaSet);

        for (ImageSet set : allSetList) {
            if (set.name.equals(context.getString(R.string.str_allvideo))) {
                allSetList.remove(set);
                allSetList.add(2, set);
                break;
            }
        }


        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (context.isDestroyed()) {
                    return;
                }
                if (imagesLoadedListener != null) {
                    imagesLoadedListener.onImagesLoaded(allSetList);
                }
            }
        });
    }

    private void fore(List<ImageSet> a, List<ImageSet> b) {
        for (ImageSet videoSet : a) {
            if (b != null && b.size() > 0) {
                for (ImageSet imageSet : b) {
                    if (videoSet.equals(imageSet)) {
                        //同一个文件夹
                        compressImageSet(imageSet.imageItems, videoSet.imageItems);
                        sort(imageSet.imageItems);
                    }
                    if (isNotContainsImageSet(imageSet)) {
                        allSetList.add(imageSet);
                    }
                }
            }
            if (isNotContainsImageSet(videoSet)) {
                allSetList.add(videoSet);
            }
        }
    }

    /**
     * 整合文件夹中所有图片和视频，要去重
     *
     * @param items  文件夹1
     * @param items2 文件夹2
     */
    private void compressImageSet(ArrayList<ImageItem> items, ArrayList<ImageItem> items2) {
        for (ImageItem imageItem : items2) {
            if (!items.contains(imageItem)) {
                items.add(imageItem);
            }
        }
    }

    private boolean isNotContainsImageSet(ImageSet imageSet) {
        for (ImageSet imageSet1 : allSetList) {
            if (imageSet1.equals(imageSet)) {
                return false;
            }
        }
        return true;
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
