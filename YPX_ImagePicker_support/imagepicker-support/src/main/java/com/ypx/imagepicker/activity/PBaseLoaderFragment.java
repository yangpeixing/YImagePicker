package com.ypx.imagepicker.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.ICameraExecutor;
import com.ypx.imagepicker.data.ProgressSceneEnum;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.data.MediaItemsDataSource;
import com.ypx.imagepicker.data.MediaSetsDataSource;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PPermissionUtils;
import com.ypx.imagepicker.views.PickerUiProvider;
import com.ypx.imagepicker.views.base.PickerControllerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.ImagePicker.REQ_CAMERA;
import static com.ypx.imagepicker.ImagePicker.REQ_STORAGE;


/**
 * Description: 选择器加载基类，主要处理媒体文件的加载和权限管理
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x
 */
public abstract class PBaseLoaderFragment extends Fragment implements ICameraExecutor {
    //选中图片列表
    protected ArrayList<ImageItem> selectList = new ArrayList<>();

    /**
     * @return 获取选择器配置项，主要用于加载文件类型的指定
     */
    @NonNull
    protected abstract BaseSelectConfig getSelectConfig();

    /**
     * @return 获取presenter
     */
    @NonNull
    protected abstract IPickerPresenter getPresenter();

    /**
     * @return 获取presenter
     */
    @NonNull
    protected abstract PickerUiConfig getUiConfig();

    /**
     * 执行回调
     */
    protected abstract void notifyPickerComplete();

    /**
     * 切换文件夹
     */
    protected abstract void toggleFolderList();

    /**
     * 跳转预览页面
     *
     * @param isClickItem 是否是item点击
     * @param index       当前图片位于预览列表数据源的索引
     */
    protected abstract void intentPreview(boolean isClickItem, int index);

    /**
     * @param imageSetList 媒体文件夹加载完成回调
     */
    protected abstract void loadMediaSetsComplete(@Nullable List<ImageSet> imageSetList);

    /**
     * @param set 媒体文件夹内文件加载完成回调
     */
    protected abstract void loadMediaItemsComplete(@Nullable ImageSet set);

    /**
     * @param allVideoSet 刷新所有视频的文件夹
     */
    protected abstract void refreshAllVideoSet(@Nullable ImageSet allVideoSet);


    /**
     * @return 返回需要判断当前文件夹列表是否打开
     */
    public boolean onBackPressed() {
        return false;
    }


    /**
     * @param imageItem 回调一张图片
     */
    protected void notifyOnSingleImagePickComplete(ImageItem imageItem) {
        selectList.clear();
        selectList.add(imageItem);
        notifyPickerComplete();
    }


    /**
     * 是否超过最大限制数
     *
     * @return true:超过
     */
    private boolean isOverMaxCount() {
        if (selectList.size() >= getSelectConfig().getMaxCount()) {
            getPresenter().overMaxCountTip(getContext(), getSelectConfig().getMaxCount());
            return true;
        }
        return false;
    }

    /**
     * 检测当前拍照item是拍照还是录像
     */
    protected void checkTakePhotoOrVideo() {
        if (getSelectConfig().isShowVideo() && !getSelectConfig().isShowImage()) {
            takeVideo();
        } else {
            takePhoto();
        }
    }

    /**
     * 拍照
     */
    @Override
    public void takePhoto() {
        if (getActivity() == null || isOverMaxCount()) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        } else {
            ImagePicker.takePhoto(getActivity(), null,
                    true, new OnImagePickCompleteListener() {
                        @Override
                        public void onImagePickComplete(ArrayList<ImageItem> items) {
                            if (items != null && items.size() > 0 && items.get(0) != null) {
                                onTakePhotoResult(items.get(0));
                            }
                        }
                    });
        }
    }

    @Override
    public void takeVideo() {
        if (getActivity() == null || isOverMaxCount()) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        } else {
            ImagePicker.takeVideo(getActivity(), null, getSelectConfig().getMaxVideoDuration(),
                    true, new OnImagePickCompleteListener() {
                        @Override
                        public void onImagePickComplete(ArrayList<ImageItem> items) {
                            if (items != null && items.size() > 0 && items.get(0) != null) {
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
    protected void loadMediaItemsFromSet(final @NonNull ImageSet set) {
        if (set.imageItems == null || set.imageItems.size() == 0) {
            DialogInterface dialogInterface = null;
            if (!set.isAllMedia() && set.count > 1000) {
                dialogInterface = getPresenter().
                        showProgressDialog(getWeakActivity(), ProgressSceneEnum.loadMediaItem);
            }
            final BaseSelectConfig selectConfig = getSelectConfig();
            final DialogInterface finalDialogInterface = dialogInterface;
            ImagePicker.provideMediaItemsFromSetWithPreload(getActivity(), set, selectConfig.getMimeTypes(),
                    40, new MediaItemsDataSource.MediaItemPreloadProvider() {
                        @Override
                        public void providerMediaItems(ArrayList<ImageItem> imageItems) {
                            if (finalDialogInterface != null) {
                                finalDialogInterface.dismiss();
                            }
                            set.imageItems = imageItems;
                            loadMediaItemsComplete(set);
                        }
                    }, new MediaItemsDataSource.MediaItemProvider() {
                        @Override
                        public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
                            if (finalDialogInterface != null) {
                                finalDialogInterface.dismiss();
                            }
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
                PPermissionUtils.create(getContext()).showSetPermissionDialog(
                        getString(R.string.picker_str_camera_permission));
            }
        } else if (requestCode == REQ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                loadMediaSets();
            } else {
                PPermissionUtils.create(getContext()).
                        showSetPermissionDialog(getString(R.string.picker_str_storage_permission));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    protected PickerControllerView titleBar;
    protected PickerControllerView bottomBar;

    /**
     * 加载自定义控制器布局
     *
     * @param container 布局容器
     * @param isTitle   是否是顶部栏
     * @param uiConfig  ui配置
     * @return 当前需要记载的控制器
     */
    protected PickerControllerView inflateControllerView(ViewGroup container, boolean isTitle, PickerUiConfig uiConfig) {
        final BaseSelectConfig selectConfig = getSelectConfig();
        PickerUiProvider uiProvider = uiConfig.getPickerUiProvider();
        PickerControllerView view = isTitle ? uiProvider.getTitleBar(getWeakActivity()) :
                uiProvider.getBottomBar(getWeakActivity());
        if (view != null && view.isAddInParent()) {
            container.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (selectConfig.isShowVideo() && selectConfig.isShowImage()) {
                view.setTitle(getString(R.string.picker_str_title_all));
            } else if (selectConfig.isShowVideo()) {
                view.setTitle(getString(R.string.picker_str_title_video));
            } else {
                view.setTitle(getString(R.string.picker_str_title_image));
            }
            final PickerControllerView finalView = view;

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == finalView.getCanClickToCompleteView()) {
                        notifyPickerComplete();
                    } else if (v == finalView.getCanClickToToggleFolderListView()) {
                        toggleFolderList();
                    } else {
                        intentPreview(false, 0);
                    }
                }
            };

            if (view.getCanClickToCompleteView() != null) {
                view.getCanClickToCompleteView().setOnClickListener(clickListener);
            }

            if (view.getCanClickToToggleFolderListView() != null) {
                view.getCanClickToToggleFolderListView().setOnClickListener(clickListener);
            }

            if (view.getCanClickToIntentPreviewView() != null) {
                view.getCanClickToIntentPreviewView().setOnClickListener(clickListener);
            }
        }

        return view;
    }

    /**
     * 控制器view执行切换文件夹操作
     *
     * @param isOpen 是否是打开文件夹
     */
    protected void controllerViewOnTransitImageSet(boolean isOpen) {
        if (titleBar != null) {
            titleBar.onTransitImageSet(isOpen);
        }
        if (bottomBar != null) {
            bottomBar.onTransitImageSet(isOpen);
        }
    }

    /**
     * 控制器view执行文件夹选择完成
     *
     * @param set 当前选择文件夹
     */
    protected void controllerViewOnImageSetSelected(ImageSet set) {
        if (titleBar != null) {
            titleBar.onImageSetSelected(set);
        }
        if (bottomBar != null) {
            bottomBar.onImageSetSelected(set);
        }
    }

    /**
     * 刷新完成按钮
     */
    protected void refreshCompleteState() {
        if (titleBar != null) {
            titleBar.refreshCompleteViewState(selectList, getSelectConfig());
        }

        if (bottomBar != null) {
            bottomBar.refreshCompleteViewState(selectList, getSelectConfig());
        }
    }

    /**
     * 设置文件夹列表的高度
     *
     * @param mFolderListRecyclerView 文件夹列表
     * @param mImageSetMask           文件夹列表的灰色透明蒙层
     * @param isCrop                  是否是小红书样式
     */
    protected void setFolderListHeight(RecyclerView mFolderListRecyclerView, View mImageSetMask, boolean isCrop) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFolderListRecyclerView.getLayoutParams();
        RelativeLayout.LayoutParams maskParams = (RelativeLayout.LayoutParams) mImageSetMask.getLayoutParams();
        PickerUiConfig uiConfig = getUiConfig();
        int height = uiConfig.getFolderListOpenMaxMargin();
        if (uiConfig.getFolderListOpenDirection() == PickerUiConfig.DIRECTION_BOTTOM) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            if (isCrop) {
                params.bottomMargin = bottomBar != null ? bottomBar.getViewHeight() : 0;
                params.topMargin = (titleBar != null ? titleBar.getViewHeight() : 0) + height;
                maskParams.topMargin = (titleBar != null ? titleBar.getViewHeight() : 0);
                maskParams.bottomMargin = bottomBar != null ? bottomBar.getViewHeight() : 0;
            } else {
                params.bottomMargin = 0;
                params.topMargin = height;
            }
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            if (isCrop) {
                params.bottomMargin = height + (bottomBar != null ? bottomBar.getViewHeight() : 0);
                params.topMargin = titleBar != null ? titleBar.getViewHeight() : 0;
                maskParams.topMargin = (titleBar != null ? titleBar.getViewHeight() : 0);
                maskParams.bottomMargin = bottomBar != null ? bottomBar.getViewHeight() : 0;
            } else {
                params.bottomMargin = height;
                params.topMargin = 0;
            }
        }
        mFolderListRecyclerView.setLayoutParams(params);
        mImageSetMask.setLayoutParams(maskParams);
    }

    /**
     * 是否拦截不可点击的item
     *
     * @param disableItemCode     不可点击的item的code码
     * @param isCheckOverMaxCount 是否校验超过最大数量时候的item
     * @return 是否拦截掉
     */
    protected boolean interceptClickDisableItem(int disableItemCode, boolean isCheckOverMaxCount) {
        if (disableItemCode != PickerItemDisableCode.NORMAL) {
            if (!isCheckOverMaxCount && disableItemCode == PickerItemDisableCode.DISABLE_OVER_MAX_COUNT) {
                return false;
            }
            String message = PickerItemDisableCode.getMessageFormCode(getActivity(), disableItemCode, getPresenter(), getSelectConfig());
            if (message.length() > 0) {
                getPresenter().tip(getWeakActivity(), message);
            }
            return true;
        }
        return false;
    }


    /**
     * 添加一个图片到文件夹列表里。一般在拍照完成的回调里会执行该方法，用于手动添加
     * 一个item到指定的文件夹列表里
     *
     * @param imageSets  当前的文件夹列表
     * @param imageItems 当前文件夹列表里面的item数组
     * @param imageItem  当前要插入的文件
     */
    protected void addItemInImageSets(@NonNull List<ImageSet> imageSets,
                                      @NonNull List<ImageItem> imageItems,
                                      @NonNull ImageItem imageItem) {
        imageItems.add(0, imageItem);
        if (imageSets.size() == 0) {
            String firstImageSetName;
            if (imageItem.isVideo()) {
                firstImageSetName = getActivity().getString(R.string.picker_str_folder_item_video);
            } else {
                firstImageSetName = getActivity().getString(R.string.picker_str_folder_item_image);
            }
            ImageSet imageSet = ImageSet.allImageSet(firstImageSetName);
            imageSet.cover = imageItem;
            imageSet.coverPath = imageItem.path;
            imageSet.imageItems = (ArrayList<ImageItem>) imageItems;
            imageSet.count = imageSet.imageItems.size();
            imageSets.add(imageSet);
        } else {
            imageSets.get(0).imageItems = (ArrayList<ImageItem>) imageItems;
            imageSets.get(0).cover = imageItem;
            imageSets.get(0).coverPath = imageItem.path;
            imageSets.get(0).count = imageItems.size();
        }
    }

    private WeakReference<Activity> weakReference;

    /**
     * @return 获取弱引用的activity对象
     */
    protected Activity getWeakActivity() {
        if (getActivity() != null) {
            if (weakReference == null) {
                weakReference = new WeakReference<Activity>(getActivity());
            }
            return weakReference.get();
        }
        return null;
    }

    protected void tip(String msg) {
        getPresenter().tip(getWeakActivity(), msg);
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

        if (time > 300) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return !flag;
    }

    /**
     * 设置是否显示状态栏
     */
    protected void setStatusBar() {
        if (getActivity() != null) {
            //刘海屏幕需要适配状态栏颜色
            if (getUiConfig().isShowStatusBar() || PStatusBarUtil.hasNotchInScreen(getActivity())) {
                PStatusBarUtil.setStatusBar(getActivity(), getUiConfig().getStatusBarColor(),
                        false, PStatusBarUtil.isDarkColor(getUiConfig().getStatusBarColor()));
            } else {
                PStatusBarUtil.fullScreen(getActivity());
            }
        }
    }
}
