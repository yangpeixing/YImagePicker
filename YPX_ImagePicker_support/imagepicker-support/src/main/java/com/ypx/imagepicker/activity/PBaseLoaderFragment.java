package com.ypx.imagepicker.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.MediaItemsDataSource;
import com.ypx.imagepicker.data.MediaSetsDataSource;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.PPermissionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.ImagePicker.REQ_CAMERA;
import static com.ypx.imagepicker.ImagePicker.REQ_STORAGE;


/**
 * Description: 选择器加载基类，主要处理媒体文件的加载和权限管理
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public abstract class PBaseLoaderFragment extends Fragment {
    /**
     * @return 获取选择器配置项，主要用于加载文件类型的指定
     */
    protected abstract BaseSelectConfig getSelectConfig();

    /**
     * @param imageSetList 媒体文件夹加载完成回调
     */
    protected abstract void loadMediaSetsComplete(List<ImageSet> imageSetList);

    /**
     * @param set 媒体文件夹内文件加载完成回调
     */
    protected abstract void loadMediaItemsComplete(ImageSet set);

    /**
     * @param allVideoSet 刷新所有视频的文件夹
     */
    protected abstract void refreshAllVideoSet(ImageSet allVideoSet);

    /**
     * 拍照回调
     *
     * @param imageItem 拍照返回
     */
    protected abstract void onTakePhotoResult(ImageItem imageItem);

    /**
     * @return 返回需要判断当前文件夹列表是否打开
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 拍照
     */
    protected void takePhoto() {
        if (getActivity() == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        } else {
            ImagePicker.takePhoto(getActivity(), new OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    if (items != null && items.size() > 0) {
                        onTakePhotoResult(items.get(0));
                    }
                }
            });
        }
    }

    /**
     * 加载媒体文件夹
     */
    protected void loadMediaSets() {
        if (getActivity() == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
        } else {
            //从媒体库拿到数据
            MediaSetsDataSource.create(getActivity())
                    .setMimeTypeSet(getSelectConfig())
                    .loadMediaSets(new MediaSetsDataSource.MediaSetProvider() {
                        @Override
                        public void providerMediaSets(ArrayList<ImageSet> imageSets) {
                            loadMediaSetsComplete(imageSets);
                        }
                    });
        }
    }

    /**
     * 根据指定的媒体 文件夹加载文件
     *
     * @param set 文件夹
     */
    protected void loadMediaItemsFromSet(final ImageSet set) {
        if (set.imageItems == null || set.imageItems.size() == 0) {
            MediaItemsDataSource dataSource = MediaItemsDataSource.create(getActivity(), set)
                    .setMimeTypeSet(getSelectConfig());
            dataSource.loadMediaItems(new MediaItemsDataSource.MediaItemProvider() {
                @Override
                public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
                    set.imageItems = imageItems;
                    loadMediaItemsComplete(set);
                    refreshAllVideoSet(allVideoSet);
                }
            });
            dataSource.setPreloadProvider(new MediaItemsDataSource.MediaItemPreloadProvider() {
                @Override
                public void providerMediaItems(ArrayList<ImageItem> imageItems) {
                    set.imageItems = imageItems;
                    loadMediaItemsComplete(set);
                }
            });
        } else {
            loadMediaItemsComplete(set);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                takePhoto();
            } else {
                PPermissionUtils.create(getContext()).showSetPermissionDialog(getString(R.string.picker_str_camerapermisson));
            }
        } else if (requestCode == REQ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                loadMediaSets();
            } else {
                PPermissionUtils.create(getContext()).showSetPermissionDialog(getString(R.string.picker_str_storagepermisson));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    final public int dp(float dp) {
        if (getActivity() == null || getContext() == null) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    private long lastTime = 0L;

    protected boolean onDoubleClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;

        if (time > 500) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return !flag;
    }
}
