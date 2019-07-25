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
    public static List<ImageSet> allSetList = new ArrayList<>();

    public MediaDataSource(FragmentActivity context) {
        this.context = context;
        imageSetList = new ArrayList<>();
        videoSetList = new ArrayList<>();
        videoDataSource = new VideoDataSource(context);
        imageDataSource = new ImageDataSource(context);
    }

    public MediaDataSource(FragmentActivity context,boolean isLoadGif) {
        this.context = context;
        imageSetList = new ArrayList<>();
        videoSetList = new ArrayList<>();
        videoDataSource = new VideoDataSource(context);
        imageDataSource = new ImageDataSource(context,isLoadGif);
    }

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (allSetList != null && allSetList.size() > 0 && imagesLoadedListener != null && !MediaObserver.instance.isMediaChanged()) {
            for (ImageSet set : allSetList) {
                set.isSelected = false;
            }
            allSetList.get(0).isSelected = true;
            for (ImageItem imageItem : allSetList.get(0).imageItems) {
                imageItem.setSelect(false);
                imageItem.setPress(false);
            }
            imagesLoadedListener.onImagesLoaded(allSetList);
            return;
        }
        MediaObserver.instance.setMediaChanged(false);
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
                        imageSet.imageItems.addAll(videoSet.imageItems);
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
