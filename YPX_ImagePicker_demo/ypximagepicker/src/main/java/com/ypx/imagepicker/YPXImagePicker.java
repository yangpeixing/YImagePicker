package com.ypx.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.interf.ImageSelectMode;
import com.ypx.imagepicker.interf.ImgLoader;
import com.ypx.imagepicker.interf.OnImageCropCompleteListener;
import com.ypx.imagepicker.interf.OnImagePickCompleteListener;
import com.ypx.imagepicker.interf.OnImageSelectedChangeListener;
import com.ypx.imagepicker.ui.activity.YPXImagesGridActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * 图片选择器
 */
@SuppressWarnings("unused")
public class YPXImagePicker {
    /**
     * 图片选择ActivityResult的总接收Code
     */
    public static final int REQ_PICKRESULT = 1234;
    /**
     * 选中数据键，用于在Activity的onresult方法中用此键名称获取选中的图片
     */
    public static final String KEY_PICKIMAGELIST = "key_pickImageList";
    private volatile static YPXImagePicker mInstance;
    /**
     * 图片加载器
     */
    private ImgLoader imgLoader;
    /**
     * 选择模式，默认为多选
     */
    private int selectMode = ImageSelectMode.MODE_MULTI;
    private YPXImagePickerUiBuilder uiBuilder;
    /**
     * 图片选择数量
     */
    private int selectLimit = 9;
    /**
     * 是否在第一项中显示拍照
     */
    private boolean shouldShowCamera = true;
    /**
     * 图片选择监听类，用于监听选中图片 
     */
    private List<OnImageSelectedChangeListener> mImageSelectedChangeListeners;
    /**
     * 图片剪裁完成回调
     */
    private List<OnImageCropCompleteListener> mImageCropCompleteListeners;
    /**
     * 图片选择完成回调
     */
    private OnImagePickCompleteListener mOnImagePickCompleteListener;
    /**
     * 选中图片数据源
     */
    private Set<ImageItem> mSelectedImages = new LinkedHashSet<>();
    /**
     * 图片总数据源
     */
    private List<ImageSet> mImageSets = new ArrayList<>();
    /**
     * 当前选中的图片文件夹索引
     */
    private int mCurrentSelectedImageSetPosition = 0;

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

//    /**
//     * 创建，可以放在Application或者需要调用图片选择器的Activity的onCreate中
//     *
//     * @param imgLoader 图片加载器
//     */
//    public static void create(ImgLoader imgLoader, YPXImagePickerUiBuilder uiBuilder) {
//        if (mInstance == null) {
//            synchronized (YPXImagePicker.class) {
//                if (mInstance == null) {
//                    mInstance = new YPXImagePicker();
//                    mInstance.imgLoader = imgLoader;
//                    mInstance.uiBuilder = uiBuilder;
//                }
//            }
//        }
//    }

    /**
     * 销毁所有选择器数据，放在activity的onDestory中调用
     */
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
        mInstance.clearImageSets();
        mInstance.clearSelectedImages();
        mInstance.mCurrentSelectedImageSetPosition = 0;
    }

    public YPXImagePickerUiBuilder getUiBuilder() {
        return uiBuilder;
    }

    /**
     * 清除所选数据
     */
    public void clearSelectedImages() {
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
    }

    /**
     * 清除源数据
     */
    public void clearImageSets() {
        if (mImageSets != null) {
            mImageSets.clear();
            mImageSets = null;
        }
    }

    /**
     * 设置自定义图片加载类
     *
     * @param imgLoader 图片加载者
     */
    public YPXImagePicker withImgLoader(ImgLoader imgLoader) {
        mInstance.imgLoader = imgLoader;
        return this;
    }

    /**
     * 设置自定义图片加载类
     *
     * @param uiBuilder 图片加载者
     */
    public YPXImagePicker withUiBuilder(YPXImagePickerUiBuilder uiBuilder) {
        this.uiBuilder = uiBuilder;
        return this;
    }

    /**
     * @return 获取图片加载类
     */
    public ImgLoader getImgLoader() {
        return mInstance.imgLoader;
    }

    /**
     * @return 获取选择模式
     */
    public int getSelectMode() {
        return selectMode;
    }

    /**
     * 添加图片选择数量，如果为1代表单选
     *
     * @param selectLimit 选择数量
     */
    public YPXImagePicker withSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
        return this;
    }

    /**
     * 添加是否显示照相机item
     *
     * @param isShowCamera 是否显示拍照
     */
    public YPXImagePicker withShowCamera(boolean isShowCamera) {
        this.shouldShowCamera = isShowCamera;
        return this;
    }

    /**
     * 使用OnImagePickCompleteListener 回调来完成图片单选或者多选
     *
     * @param context 上下文
     * @param l       图片选择完成回调
     */
    public void pick(Context context, OnImagePickCompleteListener l) {
        if (selectLimit == 1) {
            selectMode = ImageSelectMode.MODE_SINGLE;
        } else {
            selectMode = ImageSelectMode.MODE_MULTI;
        }
        mOnImagePickCompleteListener = l;
        mCurrentSelectedImageSetPosition = 0;
        context.startActivity(new Intent(context, YPXImagesGridActivity.class));
    }

    /**
     * 剪裁图片
     *
     * @param context 上下文
     * @param l       剪裁图片完成回调
     */
    public void crop(Context context, OnImageCropCompleteListener l) {
        selectMode = ImageSelectMode.MODE_CROP;
        addOnImageCropCompleteListener(l);
        mCurrentSelectedImageSetPosition = 0;
        context.startActivity(new Intent(context, YPXImagesGridActivity.class));
    }

    /**
     * 直接拍照
     *
     * @param context 上下文
     * @param l       图片选择完成回调
     */
    public void takePhoto(Context context, OnImagePickCompleteListener l) {
        selectMode = ImageSelectMode.MODE_TAKEPHOTO;
        mOnImagePickCompleteListener = l;
        mCurrentSelectedImageSetPosition = 0;
        context.startActivity(new Intent(context, YPXImagesGridActivity.class));
    }

    /**
     * 用于ActivityResult接收的图片选择器，支持跨进程调用
     * 需要activity实现onActivityResult来完成图片选择的结果
     * <p>
     * <p></p>
     * //获取数据源示范代码
     * <br></br>
     * if(resultCode == RESULT_OK&&requestCode==YPXImagePicker.REQ_PICKRESULT){
     * <br></br>
     * ArrayList< ImageItem > imageItems = (ArrayList< ImageItem >) bundle0.getSerializable(YPXImagePicker.KEY_PICKIMAGELIST);
     * <br></br>
     * }
     * <br></br>
     *
     * @param activity 调用的activity
     */
    public void pickWithActivityResult(Activity activity) {
        if (selectLimit == 1) {
            selectMode = ImageSelectMode.MODE_SINGLE;
        } else {
            selectMode = ImageSelectMode.MODE_MULTI;
        }
        mCurrentSelectedImageSetPosition = 0;
        activity.startActivityForResult(new Intent(activity, YPXImagesGridActivity.class), REQ_PICKRESULT);
    }

    /**
     * 用于ActivityResult接收的图片剪裁器，支持跨进程调用
     * 需要activity实现onActivityResult来完成图片剪裁的结果
     * <p>
     * <p></p>
     * //获取数据源示范代码
     * <br></br>
     * if(resultCode == RESULT_OK&&requestCode==YPXImagePicker.REQ_PICKRESULT){
     * <br></br>
     * ArrayList< ImageItem > imageItems = (ArrayList< ImageItem >) bundle0.getSerializable(YPXImagePicker.KEY_PICKIMAGELIST);
     * <br></br>
     * }
     * <br></br>
     *
     * @param activity 调用的activity
     */
    public void cropWithActivityResult(Activity activity) {
        selectMode = ImageSelectMode.MODE_CROP;
        mCurrentSelectedImageSetPosition = 0;
        activity.startActivityForResult(new Intent(activity, YPXImagesGridActivity.class), REQ_PICKRESULT);
    }

    /**
     * 用于ActivityResult接收的拍照返回的数据，支持跨进程调用
     * 需要activity实现onActivityResult来完成拍照返回的结果
     * <p>
     * <p></p>
     * //获取数据源示范代码
     * <br></br>
     * if(resultCode == RESULT_OK&&requestCode==YPXImagePicker.REQ_PICKRESULT){
     * <br></br>
     * ArrayList< ImageItem > imageItems = (ArrayList< ImageItem >) bundle0.getSerializable(YPXImagePicker.KEY_PICKIMAGELIST);
     * <br></br>
     * }
     * <br></br>
     *
     * @param activity 调用的activity
     */
    public void takePhotoWithActivityResult(Activity activity) {
        selectMode = ImageSelectMode.MODE_TAKEPHOTO;
        mCurrentSelectedImageSetPosition = 0;
        activity.startActivityForResult(new Intent(activity, YPXImagesGridActivity.class), REQ_PICKRESULT);
    }

    /**
     * 添加图片选择变化监听器
     *
     * @param l 图片选择监听器
     */
    public void addOnImageSelectedChangeListener(OnImageSelectedChangeListener l) {
        if (mImageSelectedChangeListeners == null) {
            mImageSelectedChangeListeners = new ArrayList<>();
        }
        this.mImageSelectedChangeListeners.add(l);
    }

    /**
     * 移除指定的图片变化监听
     *
     * @param l 监听器
     */
    public void removeOnImageItemSelectedChangeListener(OnImageSelectedChangeListener l) {
        if (mImageSelectedChangeListeners != null) {
            this.mImageSelectedChangeListeners.remove(l);
        }
    }

    /**
     * 添加图片剪裁监听器
     *
     * @param l 图片剪裁监听者
     */
    public void addOnImageCropCompleteListener(OnImageCropCompleteListener l) {
        if (mImageCropCompleteListeners == null) {
            mImageCropCompleteListeners = new ArrayList<>();
        }
        this.mImageCropCompleteListeners.add(l);
    }

    /**
     * 移除图片剪裁监听器
     *
     * @param l 剪裁监听器
     */
    public void removeOnImageCropCompleteListener(OnImageCropCompleteListener l) {
        if (mImageCropCompleteListeners != null) {
            this.mImageCropCompleteListeners.remove(l);
        }
    }

    /**
     * 删除图片选择完成监听
     *
     * @param l 图片选择完成监听器
     */
    public void deleteOnImagePickCompleteListener(OnImagePickCompleteListener l) {
        if (l.getClass().getName().equals(mOnImagePickCompleteListener.getClass().getName())) {
            mOnImagePickCompleteListener = null;
            System.gc();
        }
    }

    /**
     * 图片选择发生变化提醒器
     *
     * @param position 图片文件夹的位置
     * @param item     发生变动的图片的信息
     * @param isAdd    是否是添加
     */
    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if ((!isAdd || getSelectImageCount() <= selectLimit) && (isAdd || getSelectImageCount() != selectLimit)) {
            if (mImageSelectedChangeListeners != null) {
                for (OnImageSelectedChangeListener l : mImageSelectedChangeListeners) {
                    l.onImageSelectChange(position, item, mSelectedImages.size(), selectLimit);
                }
            }
        }
    }

    /**
     * 图片剪裁完成提醒器
     *
     * @param url   图片剪裁后的url地址，这里为空
     * @param bmp   剪裁完成后的bitmap
     * @param ratio 剪裁比例
     */
    public void notifyImageCropComplete(String url, Bitmap bmp, int ratio) {
        if (mImageCropCompleteListeners != null) {
            for (OnImageCropCompleteListener l : mImageCropCompleteListeners) {
                l.onImageCropComplete(url, bmp, ratio);
            }
        }
    }


    /**
     * 图片选择完成提醒
     */
    public void notifyOnImagePickComplete() {
        if (mOnImagePickCompleteListener != null) {
            ArrayList<ImageItem> list = getSelectedImages();
            mOnImagePickCompleteListener.onImagePickComplete(list);
        }
    }

    /**
     * 获取图片选择数量
     *
     * @return 数量
     */
    public int getSelectLimit() {
        return selectLimit;
    }

    /**
     * 是否显示拍照item
     *
     * @return true or false
     */
    public boolean isShouldShowCamera() {
        return shouldShowCamera;
    }

    /**
     * 获取数据源
     *
     * @return 数据源
     */
    public List<ImageSet> getImageSets() {
        return mImageSets;
    }

    /**
     * 设置数据源
     *
     * @param mImageSets 图片数据源
     */
    public void setImageSets(List<ImageSet> mImageSets) {
        this.mImageSets = mImageSets;
    }

    /**
     * 获取当前图片文件夹的图片集合
     *
     * @return 当前选中的图片文件夹的图片集合
     */
    public List<ImageItem> getImageItemsOfCurrentImageSet() {
        if (mImageSets != null && mImageSets.size() > mCurrentSelectedImageSetPosition) {
            return mImageSets.get(mCurrentSelectedImageSetPosition).imageItems;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取当前文件夹的索引
     *
     * @return 索引
     */
    public int getCurrentSelectedImageSetPosition() {
        return mCurrentSelectedImageSetPosition;
    }

    /**
     * 设置当前选中的文件夹索引
     *
     * @param mCurrentSelectedImageSetPosition 文件夹索引
     */
    public void setCurrentSelectedImageSetPosition(int mCurrentSelectedImageSetPosition) {
        this.mCurrentSelectedImageSetPosition = mCurrentSelectedImageSetPosition;
    }

    /**
     * 添加所选图片
     *
     * @param position 文件夹位置
     * @param item     图片信息
     */
    public void addSelectedImageItem(int position, ImageItem item) {
        mSelectedImages.add(item);
        notifyImageSelectedChanged(position, item, true);
    }

    /**
     * 删除选中的图片
     *
     * @param position 文件夹位置
     * @param item     图片信息
     */
    public void deleteSelectedImageItem(int position, ImageItem item) {
        mSelectedImages.remove(item);
        notifyImageSelectedChanged(position, item, false);
    }

    /**
     * 是否选中
     *
     * @param position 文件夹索引
     * @param item     图片信息
     * @return true：已选中  false：未选中
     */
    public boolean isSelect(int position, ImageItem item) {
        return mSelectedImages.contains(item);
    }

    /**
     * 获取图片选中数量
     *
     * @return 数量
     */
    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    /**
     * 获取选中数据（已序列化）
     *
     * @return 选中图片列表
     */
    public ArrayList<ImageItem> getSelectedImages() {
        ArrayList<ImageItem> list = new ArrayList<>();
        list.addAll(mSelectedImages);
        return list;
    }
}
