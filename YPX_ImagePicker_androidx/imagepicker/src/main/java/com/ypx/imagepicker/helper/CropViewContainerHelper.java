package com.ypx.imagepicker.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

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

    public CropImageView loadCropView(Context context, final ImageItem imageItem, int mCropSize,
                                      IPickerPresenter presenter, final onLoadComplete loadComplete) {
        if (cropViewList.containsKey(imageItem) && cropViewList.get(imageItem) != null) {
            mCropView = cropViewList.get(imageItem);
        } else {
            mCropView = new CropImageView(context);
            //设置剪裁view的属性
            mCropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mCropView.enable(); // 启用图片缩放功能
            mCropView.setMaxScale(7.0f);
            mCropView.setCanShowTouchLine(true);
            mCropView.setShowImageRectLine(true);
            if (imageItem.width == 0 || imageItem.height == 0) {
                mCropView.setOnImageLoadListener(new CropImageView.onImageLoadListener() {
                    @Override
                    public void onImageLoaded(float w, float h) {
                        imageItem.width = (int) w;
                        imageItem.height = (int) h;
                        if (loadComplete != null) {
                            loadComplete.loadComplete();
                        }
                    }
                });
            }

            if (presenter != null) {
                presenter.displayImage(mCropView, imageItem, mCropSize, false);
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

    public interface onLoadComplete {
        void loadComplete();
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

    public ArrayList<ImageItem> generateCropUrls(List<ImageItem> selectList, int cropMode) {
        ArrayList<ImageItem> cropUrlList = new ArrayList<>();
        for (ImageItem imageItem : selectList) {
            CropImageView view = cropViewList.get(imageItem);
            if (view == null) {
                continue;
            }
            Bitmap bitmap =PBitmapUtils.getViewBitmap(view);
            String cropUrl = PBitmapUtils.saveBitmapToFile(view.getContext(), bitmap,
                    "crop_" + System.currentTimeMillis(),
                    Bitmap.CompressFormat.JPEG);
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
