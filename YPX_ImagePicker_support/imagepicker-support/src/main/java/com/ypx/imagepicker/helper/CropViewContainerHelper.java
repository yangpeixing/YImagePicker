package com.ypx.imagepicker.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.PFileUtil;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Time: 2019/9/30 9:45
 * Author:ypx
 * Description: 剪裁View封装
 */
public class CropViewContainerHelper {
    private WeakReference<ViewGroup> parentReference;
    //存储已选择的剪裁View
    private HashMap<ImageItem, CropImageView> cropViewList = new HashMap<>();

    public CropViewContainerHelper(@NonNull ViewGroup parent) {
        parentReference = new WeakReference<>(parent);
    }

    private ViewGroup getParent() {
        if (parentReference != null && parentReference.get() != null) {
            return parentReference.get();
        }
        return null;
    }

    public void setBackgroundColor(int color) {
        if (mCropView != null) {
            mCropView.setBackgroundColor(color);
        }
    }

    private CropImageView mCropView;

    public CropImageView loadCropView(Context context, ImageItem imageItem, int mCropSize, ICropPickerBindPresenter presenter) {
        if (cropViewList.containsKey(imageItem) && cropViewList.get(imageItem) != null) {
            mCropView = cropViewList.get(imageItem);
        } else {
            mCropView = new CropImageView(context);
            //设置剪裁view的属性
            mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mCropView.enable(); // 启用图片缩放功能
            mCropView.setMaxScale(7.0f);
            mCropView.setCanShowTouchLine(true);
            mCropView.setShowImageRectLine(true);
            if (presenter != null) {
                presenter.displayCropImage(mCropView, imageItem);
            }
        }
        if (getParent() != null) {
            getParent().removeAllViews();
            if (mCropView.getParent() != null) {
                ((ViewGroup) mCropView.getParent()).removeView(mCropView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mCropSize, mCropSize);
            params.gravity = Gravity.CENTER;
            getParent().addView(mCropView, params);
        }
        return mCropView;
    }

    public void addCropView(CropImageView view, ImageItem imageItem) {
        if (!cropViewList.containsKey(imageItem)) {
            cropViewList.put(imageItem, view);
        }
    }

    public void removeCropView(ImageItem imageItem) {
        cropViewList.remove(imageItem);
    }

    public void refreshAllState(ImageItem currentImageItem, List<ImageItem> selectList,
                                ViewGroup invisibleContainer,
                                boolean isFitState,
                                ResetSizeExecutor executor) {
        invisibleContainer.removeAllViews();
        invisibleContainer.setVisibility(View.VISIBLE);
        for (ImageItem imageItem : selectList) {
            if (imageItem == currentImageItem) {
                continue;
            }
            CropImageView picBrowseImageView = cropViewList.get(imageItem);
            if (picBrowseImageView != null) {
                invisibleContainer.addView(picBrowseImageView);
                if (executor != null) {
                    executor.resetAllCropViewSize(picBrowseImageView);
                }
                if (isFitState) {
                    imageItem.setCropMode(ImageCropMode.ImageScale_FILL);
                    picBrowseImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                cropViewList.put(imageItem, picBrowseImageView);
            }
        }
        invisibleContainer.setVisibility(View.INVISIBLE);
    }

    public ArrayList<ImageItem> generateCropUrls(List<ImageItem> selectList, String savePath, int cropMode) {
        ArrayList<ImageItem> cropUrlList = new ArrayList<>();
        for (ImageItem imageItem : selectList) {
            View view = cropViewList.get(imageItem);
            File f = new File(savePath, "crop_" + System.currentTimeMillis() + ".jpg");
            String cropUrl = PFileUtil.saveBitmapToLocalWithJPEG(view, f.getAbsolutePath());
            imageItem.setCropUrl(cropUrl);
            imageItem.setCropMode(cropMode);
            imageItem.setPress(false);
            cropUrlList.add(imageItem);
        }
        return cropUrlList;
    }


    public interface ResetSizeExecutor {
        void resetAllCropViewSize(CropImageView view);
    }
}
