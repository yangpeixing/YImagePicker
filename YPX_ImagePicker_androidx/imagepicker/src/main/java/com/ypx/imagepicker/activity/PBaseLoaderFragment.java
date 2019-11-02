package com.ypx.imagepicker.activity;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickConstants;
import com.ypx.imagepicker.data.MediaItemsDataSource;
import com.ypx.imagepicker.data.MediaSetsDataSource;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnPickerCompleteListener;
import com.ypx.imagepicker.presenter.PBasePresenter;
import com.ypx.imagepicker.utils.PConstantsUtil;
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
    //选中图片列表
    protected ArrayList<ImageItem> selectList = new ArrayList<>();

    /**
     * @return 获取选择器配置项，主要用于加载文件类型的指定
     */
    protected abstract BaseSelectConfig getSelectConfig();

    protected abstract PBasePresenter getPresenter();

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
     * 是否超过最大限制数
     *
     * @return true:超过
     */
    protected boolean isOverMaxCount() {
        if (selectList.size() >= getSelectConfig().getMaxCount()) {
            getPresenter().overMaxCountTip(getContext(), getSelectConfig().getMaxCount());
            return true;
        }
        return false;
    }

    /**
     * 拍照
     */
    protected void takePhoto() {
        if (isOverMaxCount()) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        } else {
            //如果只加载视频,则调用拍视频
            if (getSelectConfig().isShowVideo() && !getSelectConfig().isShowImage()) {
                ImagePicker.takeVideo(getActivity(), new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        if (items != null && items.size() > 0) {
                            onTakePhotoResult(items.get(0));
                        }
                    }
                });
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
            //从媒体库拿到文件夹列表
            ImagePicker.provideMediaSets(getActivity(), getSelectConfig().getMimeTypes(), new MediaSetsDataSource.MediaSetProvider() {
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
            final BaseSelectConfig selectConfig = getSelectConfig();
            ImagePicker.provideMediaItemsFromSetWithPreload(getActivity(), set, selectConfig.getMimeTypes(),
                    40, new MediaItemsDataSource.MediaItemPreloadProvider() {
                        @Override
                        public void providerMediaItems(ArrayList<ImageItem> imageItems) {
                            set.imageItems = imageItems;
                            loadMediaItemsComplete(set);
                        }
                    }, new MediaItemsDataSource.MediaItemProvider() {
                        @Override
                        public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
                            set.imageItems = imageItems;
                            loadMediaItemsComplete(set);
                            if (selectConfig.isShowImage() && selectConfig.isShowVideo()) {
                                refreshAllVideoSet(allVideoSet);
                            }
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
                PPermissionUtils.create(getContext()).showSetPermissionDialog(getPickConstants().picker_str_camera_permission);
            }
        } else if (requestCode == REQ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                loadMediaSets();
            } else {
                PPermissionUtils.create(getContext()).showSetPermissionDialog(getPickConstants().picker_str_storage_permission);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected boolean isItemCantClick(List<ImageItem> selectList, ImageItem imageItem) {
        if (getPresenter() == null) {
            return true;
        }

        if (getSelectConfig() == null) {
            return true;
        }
        if (getSelectConfig() instanceof MultiSelectConfig) {
            //在屏蔽列表中
            if (((MultiSelectConfig) getSelectConfig()).isShieldItem(imageItem)) {
                getPresenter().tip(getContext(), getPickConstants().picker_str_shield);
                return true;
            }
        }
        if (imageItem.isVideo()) {
            if (getSelectConfig().isSinglePickImageOrVideoType() && selectList != null && selectList.size() > 0 && selectList.get(0).isImage()) {
                getPresenter().tip(getActivity(), getPickConstants().picker_str_only_select_image);
                return true;
            } else if (imageItem.duration > getSelectConfig().getMaxVideoDuration()) {
                getPresenter().tip(getActivity(), String.format("%s%s", getPickConstants().picker_str_video_over_max_duration,
                        getSelectConfig().getMaxVideoDurationFormat()));
                return true;
            } else if (imageItem.duration < getSelectConfig().getMinVideoDuration()) {
                getPresenter().tip(getActivity(), String.format("%s%s", getPickConstants().picker_str_video_less_min_duration,
                        getSelectConfig().getMinVideoDurationFormat()));
                return true;
            }
        } else {
            if (getSelectConfig().isSinglePickImageOrVideoType() && selectList != null && selectList.size() > 0 && selectList.get(0).isVideo()) {
                getPresenter().tip(getActivity(), getPickConstants().picker_str_only_select_video);
                return true;
            }
        }
        return false;
    }

    protected PickConstants getPickConstants() {
        return PConstantsUtil.getString(getActivity(), getPresenter());
    }

    protected void tip(String msg) {
        if (getPresenter() != null) {
            getPresenter().tip(getActivity(), msg);
        }
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
