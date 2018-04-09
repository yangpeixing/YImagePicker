package com.ypx.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.imp.ImageSelectMode;
import com.ypx.imagepicker.imp.ImgLoader;
import com.ypx.imagepicker.imp.OnImageCropCompleteListener;
import com.ypx.imagepicker.imp.OnImagePickCompleteListener;
import com.ypx.imagepicker.imp.OnImageSelectedChangeListener;
import com.ypx.imagepicker.ui.activity.ImagesGridActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * <b>The main Entrance of this lib</b><br/>
 * Created by Eason.Lai on 2015/11/1 10:42 <br/>
 * contact：easonline7@gmail.com <br/>
 */
public class YPXImagePicker {
    public static final int REQ_PICKRESULT = 1234;
    public static final String KEY_PICKIMAGELIST = "key_pickImageList";
    public static int selectMode = ImageSelectMode.MODE_MULTI;
    private static YPXImagePicker mInstance;


    private ImgLoader imgLoader;
    private int cropSize = 60 * 2;

    private Set<ImageItem> mSelectedImages = new LinkedHashSet<>();
    private int selectLimit = 9;//can select 9 at most,you can change it yourself
    private boolean shouldShowCamera = true;//indicate whether to show the camera item
    /**
     * Listeners of image selected changes,if you want to custom the Activity of ImagesGrid or ImagePreview,you might use it.
     */
    private List<OnImageSelectedChangeListener> mImageSelectedChangeListeners;
    /**
     * listeners of image crop complete
     */
    private List<OnImageCropCompleteListener> mImageCropCompleteListeners;
    /**
     * Listener when image pick completed
     */
    private OnImagePickCompleteListener mOnImagePickCompleteListener;
    //All Images collect by Set
    private List<ImageSet> mImageSets;
    private int mCurrentSelectedImageSetPosition = 0;//Item 0: all images

    public static YPXImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (YPXImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new YPXImagePicker();
                }
            }
        }
        return mInstance;
    }

    public static void create(ImgLoader imgLoader) {
        if (mInstance == null) {
            synchronized (YPXImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new YPXImagePicker();
                    mInstance.imgLoader = imgLoader;
                }
            }
        }
    }

    public static void destroy() {
        if (mInstance == null) {
            return;
        }
        if (mInstance.mImageSelectedChangeListeners != null) {
            mInstance.mImageSelectedChangeListeners.clear();
            mInstance.mImageSelectedChangeListeners = null;
        }
        if (mInstance.mImageCropCompleteListeners != null) {
            mInstance.mImageCropCompleteListeners.clear();
            mInstance.mImageCropCompleteListeners = null;
        }
        clearImageSets();
        clearSelectedImages();
        mInstance.mCurrentSelectedImageSetPosition = 0;
    }


    public static void clearSelectedImages() {
        if (mInstance != null && mInstance.mSelectedImages != null) {
            mInstance.mSelectedImages.clear();
        }
    }

    public static void clearImageSets() {
        if (mInstance != null && mInstance.mImageSets != null) {
            mInstance.mImageSets.clear();
            mInstance.mImageSets = null;
        }
    }

    public YPXImagePicker withImgLoader(ImgLoader imgLoader) {
        mInstance.imgLoader = imgLoader;
        return mInstance;
    }

    public ImgLoader getImgLoader() {
        return mInstance.imgLoader;
    }

    public YPXImagePicker withSelectLimit(int selectLimit) {
        mInstance.selectLimit = selectLimit;
        return mInstance;
    }

    public YPXImagePicker withShowCamera(boolean isShowCamera) {
        mInstance.shouldShowCamera = isShowCamera;
        return mInstance;
    }

    public void pick(Context context, OnImagePickCompleteListener l) {
        if (selectLimit == 1) {
            selectMode = ImageSelectMode.MODE_SINGLE;
        } else {
            selectMode = ImageSelectMode.MODE_MULTI;
        }
        mOnImagePickCompleteListener = l;
        context.startActivity(new Intent(context, ImagesGridActivity.class));
    }

    public void crop(Context context, OnImageCropCompleteListener l) {
        selectMode = ImageSelectMode.MODE_CROP;
        addOnImageCropCompleteListener(l);
        context.startActivity(new Intent(context, ImagesGridActivity.class));
    }


    public void takePhoto(Context context, OnImagePickCompleteListener l) {
        selectMode = ImageSelectMode.MODE_TAKEPHOTO;
        mOnImagePickCompleteListener = l;
        context.startActivity(new Intent(context, ImagesGridActivity.class));
    }


    public void pickWithActivityResult(Activity activity) {
        if (selectLimit == 1) {
            selectMode = ImageSelectMode.MODE_SINGLE;
        } else {
            selectMode = ImageSelectMode.MODE_MULTI;
        }
        activity.startActivityForResult(new Intent(activity, ImagesGridActivity.class), REQ_PICKRESULT);
    }

    public void cropWithActivityResult(Activity activity) {
        selectMode = ImageSelectMode.MODE_CROP;
        activity.startActivityForResult(new Intent(activity, ImagesGridActivity.class), REQ_PICKRESULT);
    }

    public void takePhotoWithActivityResult(Activity activity) {
        selectMode = ImageSelectMode.MODE_TAKEPHOTO;
        activity.startActivityForResult(new Intent(activity, ImagesGridActivity.class), REQ_PICKRESULT);
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public boolean isShouldShowCamera() {
        return shouldShowCamera;
    }


    public void addOnImageSelectedChangeListener(OnImageSelectedChangeListener l) {
        if (mImageSelectedChangeListeners == null) {
            mImageSelectedChangeListeners = new ArrayList<>();
        }
        this.mImageSelectedChangeListeners.add(l);
    }

    public void removeOnImageItemSelectedChangeListener(OnImageSelectedChangeListener l) {
        if (mImageSelectedChangeListeners == null) {
            return;
        }
        this.mImageSelectedChangeListeners.remove(l);
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        //do not call the listeners if reached the select limit when selecting
        if ((!isAdd || getSelectImageCount() <= selectLimit) && (isAdd || getSelectImageCount() != selectLimit)) {
            if (mImageSelectedChangeListeners == null) {
                return;
            }
            for (OnImageSelectedChangeListener l : mImageSelectedChangeListeners) {
                l.onImageSelectChange(position, item, mSelectedImages.size(), selectLimit);
            }
        }
    }

    public void addOnImageCropCompleteListener(OnImageCropCompleteListener l) {
        if (mImageCropCompleteListeners == null) {
            mImageCropCompleteListeners = new ArrayList<>();
        }
        this.mImageCropCompleteListeners.add(l);
    }

    public void removeOnImageCropCompleteListener(OnImageCropCompleteListener l) {
        if (mImageCropCompleteListeners == null) {
            return;
        }
        this.mImageCropCompleteListeners.remove(l);
    }

    public void notifyImageCropComplete(String url, Bitmap bmp, int ratio) {
        if (mImageCropCompleteListeners != null) {
            for (OnImageCropCompleteListener l : mImageCropCompleteListeners) {
                l.onImageCropComplete(url, bmp, ratio);
            }
        }
    }

    public void deleteOnImagePickCompleteListener(OnImagePickCompleteListener l) {
        if (l.getClass().getName().equals(mOnImagePickCompleteListener.getClass().getName())) {
            mOnImagePickCompleteListener = null;
            System.gc();
        }
    }

    public void notifyOnImagePickComplete() {
        if (mOnImagePickCompleteListener != null) {
            List<ImageItem> list = getSelectedImages();
            mOnImagePickCompleteListener.onImagePickComplete(list);
        }
    }

    public List<ImageSet> getImageSets() {
        return mImageSets;
    }

    public void setImageSets(List<ImageSet> mImageSets) {
        this.mImageSets = mImageSets;
    }

    public List<ImageItem> getImageItemsOfCurrentImageSet() {
        if (mImageSets != null) {
            return mImageSets.get(mCurrentSelectedImageSetPosition).imageItems;
        } else {
            return null;
        }
    }

    public int getCurrentSelectedImageSetPosition() {
        return mCurrentSelectedImageSetPosition;
    }

    public void setCurrentSelectedImageSetPosition(int mCurrentSelectedImageSetPosition) {
        this.mCurrentSelectedImageSetPosition = mCurrentSelectedImageSetPosition;
    }

    public void addSelectedImageItem(int position, ImageItem item) {
        mSelectedImages.add(item);
        notifyImageSelectedChanged(position, item, true);
    }

    public void deleteSelectedImageItem(int position, ImageItem item) {
        mSelectedImages.remove(item);
        notifyImageSelectedChanged(position, item, false);
    }

    public boolean isSelect(int position, ImageItem item) {
        return mSelectedImages.contains(item);
    }

    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    public ArrayList<ImageItem> getSelectedImages() {
        ArrayList<ImageItem> list = new ArrayList<>();
        list.addAll(mSelectedImages);
        return list;
    }
}
