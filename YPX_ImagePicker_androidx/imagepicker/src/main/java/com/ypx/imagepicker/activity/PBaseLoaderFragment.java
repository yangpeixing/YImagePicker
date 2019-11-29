package com.ypx.imagepicker.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.PickConstants;
import com.ypx.imagepicker.data.ITakePhoto;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.data.MediaItemsDataSource;
import com.ypx.imagepicker.data.MediaSetsDataSource;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PConstantsUtil;
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
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public abstract class PBaseLoaderFragment extends Fragment implements ITakePhoto {
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

    protected abstract void notifyOnImagePickComplete();

    protected abstract void toggleFolderList();

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
    public void notifyOnSingleImagePickComplete(ImageItem imageItem) {
        selectList.clear();
        selectList.add(imageItem);
        notifyOnImagePickComplete();
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
            ImagePicker.takePhoto(getActivity(), new OnImagePickCompleteListener() {
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
            ImagePicker.takeVideo(getActivity(), new OnImagePickCompleteListener() {
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


    protected PickerControllerView titleBar;
    protected PickerControllerView bottomBar;

    protected PickerControllerView inflateControllerView(ViewGroup container, boolean isTitle, PickerUiConfig uiConfig) {
        final BaseSelectConfig selectConfig = getSelectConfig();
        final IPickerPresenter presenter = getPresenter();
        PickerUiProvider uiProvider = uiConfig.getPickerUiProvider();
        PickerControllerView view = isTitle ? uiProvider.getTitleBar(getWeakActivity()) :
                uiProvider.getBottomBar(getWeakActivity());
        if (view != null && view.isAddInParent()) {
            container.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (selectConfig.isShowVideo() && selectConfig.isShowImage()) {
                view.setTitle(PConstantsUtil.getString(getContext(), presenter).picker_str_multi_title);
            } else if (selectConfig.isShowVideo()) {
                view.setTitle(PConstantsUtil.getString(getContext(), presenter).picker_str_multi_title_video);
            } else {
                view.setTitle(PConstantsUtil.getString(getContext(), presenter).picker_str_multi_title_image);
            }
            final PickerControllerView finalView = view;

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == finalView.getCanClickToCompleteView()) {
                            notifyOnImagePickComplete();
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

    protected void controllerViewOnTransitImageSet(boolean isOpen) {
        if (titleBar != null) {
            titleBar.onTransitImageSet(isOpen);
        }
        if (bottomBar != null) {
            bottomBar.onTransitImageSet(isOpen);
        }
    }

    protected void controllerViewOnImageSetSelected(ImageSet set) {
        if (titleBar != null) {
            titleBar.onImageSetSelected(set);
        }
        if (bottomBar != null) {
            bottomBar.onImageSetSelected(set);
        }
    }

    protected void refreshCompleteState() {
        if (titleBar != null) {
            titleBar.refreshCompleteViewState(selectList, getSelectConfig());
        }

        if (bottomBar != null) {
            bottomBar.refreshCompleteViewState(selectList, getSelectConfig());
        }
    }

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


    protected void addItemInImageSets(@NonNull List<ImageSet> imageSets,
                                      @NonNull List<ImageItem> imageItems,
                                      @NonNull ImageItem imageItem,
                                      @NonNull String firstImageSetName) {
        imageItems.add(0, imageItem);
        if (imageSets.size() == 0) {
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

    protected Activity getWeakActivity() {
        if (getActivity() != null) {
            if (weakReference == null) {
                weakReference = new WeakReference<Activity>(getActivity());
            }
            return weakReference.get();
        }
        return null;
    }

    protected PickConstants getPickConstants() {
        return PConstantsUtil.getString(getActivity(), getPresenter());
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


    protected void traverse(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                final View child = parent.getChildAt(i);
                if (child instanceof ViewGroup) {
                    child.setBackground(null);
                    traverse((ViewGroup) child);
                } else {
                    if (child != null) {
                        child.setBackground(null);
                    }
                    if (child instanceof ImageView) {
                        ((ImageView) child).setImageDrawable(null);
                    }
                }
            }
            parent.removeAllViews();
        }
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
