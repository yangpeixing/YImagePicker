package com.ypx.imagepicker.activity.multi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.PBaseLoaderFragment;
import com.ypx.imagepicker.activity.preview.MultiImagePreviewActivity;
import com.ypx.imagepicker.adapter.PickerFolderAdapter;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.data.IReloadExecutor;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.adapter.PickerItemAdapter;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.selectconfig.MultiSelectConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.presenter.IPickerPresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;

/**
 * Description: 多选页
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x
 */
public class MultiImagePickerFragment extends PBaseLoaderFragment implements View.OnClickListener,
        PickerItemAdapter.OnActionResult, IReloadExecutor {
    private List<ImageSet> imageSets = new ArrayList<>();
    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private View v_masker;
    private TextView mTvTime;
    private PickerFolderAdapter mImageSetAdapter;
    private RecyclerView mFolderListRecyclerView;
    private PickerItemAdapter mAdapter;
    private ImageSet currentImageSet;
    private FrameLayout titleBarContainer;
    private FrameLayout bottomBarContainer;
    private MultiSelectConfig selectConfig;
    private IPickerPresenter presenter;
    private PickerUiConfig uiConfig;
    private FragmentActivity mContext;
    private GridLayoutManager layoutManager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picker_activity_multipick, container, false);
        return view;
    }

    /**
     * 校验传递数据是否合法
     */
    private boolean isIntentDataValid() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectConfig = (MultiSelectConfig) bundle.getSerializable(INTENT_KEY_SELECT_CONFIG);
            presenter = (IPickerPresenter) bundle.getSerializable(INTENT_KEY_PRESENTER);
            if (presenter == null) {
                PickerErrorExecutor.executeError(onImagePickCompleteListener,
                        PickerError.PRESENTER_NOT_FOUND.getCode());
                return false;
            }
            if (selectConfig == null) {
                PickerErrorExecutor.executeError(onImagePickCompleteListener,
                        PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        if (isIntentDataValid()) {
            ImagePicker.isOriginalImage = selectConfig.isDefaultOriginal();
            uiConfig = presenter.getUiConfig(getWeakActivity());
            setStatusBar();
            findView();
            if (selectConfig.getLastImageList() != null) {
                selectList.addAll(selectConfig.getLastImageList());
            }
            loadMediaSets();
            refreshCompleteState();
        }
    }

    private OnImagePickCompleteListener onImagePickCompleteListener;

    /**
     * 设置图片选择器完成回调
     *
     * @param onImagePickCompleteListener 完成回调
     */
    public void setOnImagePickCompleteListener(@NonNull OnImagePickCompleteListener onImagePickCompleteListener) {
        this.onImagePickCompleteListener = onImagePickCompleteListener;
    }

    /**
     * 初始化控件
     */
    private void findView() {
        v_masker = view.findViewById(R.id.v_masker);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mFolderListRecyclerView = view.findViewById(R.id.mSetRecyclerView);
        mTvTime = view.findViewById(R.id.tv_time);
        mTvTime.setVisibility(View.GONE);
        titleBarContainer = view.findViewById(R.id.titleBarContainer);
        bottomBarContainer = view.findViewById(R.id.bottomBarContainer);
        initAdapters();
        initUI();
        setListener();
        refreshCompleteState();
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mTvTime.getVisibility() == View.VISIBLE) {
                    mTvTime.setVisibility(View.GONE);
                    mTvTime.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.picker_fade_out));
                }
            } else {
                if (mTvTime.getVisibility() == View.GONE) {
                    mTvTime.setVisibility(View.VISIBLE);
                    mTvTime.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.picker_fade_in));
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (imageItems != null)
                try {
                    mTvTime.setText(imageItems.get(layoutManager.findFirstVisibleItemPosition()).getTimeFormat());
                } catch (Exception ignored) {

                }
        }
    };

    /**
     * 初始化UI界面
     */
    private void initUI() {
        mRecyclerView.setBackgroundColor(uiConfig.getPickerBackgroundColor());
        titleBar = inflateControllerView(titleBarContainer, true, uiConfig);
        bottomBar = inflateControllerView(bottomBarContainer, false, uiConfig);
        setFolderListHeight(mFolderListRecyclerView, v_masker, false);
    }

    /**
     * 初始化监听
     */
    private void setListener() {
        v_masker.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(onScrollListener);
        mImageSetAdapter.setFolderSelectResult(new PickerFolderAdapter.FolderSelectResult() {
            @Override
            public void folderSelected(ImageSet set, int pos) {
                selectImageFromSet(pos, true);
            }
        });
    }

    /**
     * 初始化相关adapter
     */
    private void initAdapters() {
        mFolderListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mImageSetAdapter = new PickerFolderAdapter(presenter, uiConfig);
        mFolderListRecyclerView.setAdapter(mImageSetAdapter);
        mImageSetAdapter.refreshData(imageSets);

        mAdapter = new PickerItemAdapter(selectList, new ArrayList<ImageItem>(), selectConfig, presenter, uiConfig);
        mAdapter.setHasStableIds(true);
        mAdapter.setOnActionResult(this);
        layoutManager = new GridLayoutManager(mContext, selectConfig.getColumnCount());
        if (mRecyclerView.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            mRecyclerView.getItemAnimator().setChangeDuration(0);// 通过设置动画执行时间为0来解决闪烁问题
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 选择图片文件夹
     *
     * @param position 位置
     */
    private void selectImageFromSet(final int position, boolean isTransit) {
        currentImageSet = imageSets.get(position);
        if (isTransit) {
            toggleFolderList();
        }
        for (ImageSet set1 : imageSets) {
            set1.isSelected = false;
        }
        currentImageSet.isSelected = true;
        mImageSetAdapter.notifyDataSetChanged();
        if(currentImageSet.isAllMedia()){
            if(selectConfig.isShowCameraInAllMedia()){
                selectConfig.setShowCamera(true);
            }
        }else {
            if(selectConfig.isShowCameraInAllMedia()){
                selectConfig.setShowCamera(false);
            }
        }
        loadMediaItemsFromSet(currentImageSet);
    }


    /**
     * 显示或隐藏图片文件夹选项列表
     */
    @Override
    protected void toggleFolderList() {
        if (mFolderListRecyclerView.getVisibility() == View.GONE) {
            controllerViewOnTransitImageSet(true);
            v_masker.setVisibility(View.VISIBLE);
            mFolderListRecyclerView.setVisibility(View.VISIBLE);
            mFolderListRecyclerView.setAnimation(AnimationUtils.loadAnimation(mContext,
                    uiConfig.isShowFromBottom() ? R.anim.picker_show2bottom : R.anim.picker_anim_in));
        } else {
            controllerViewOnTransitImageSet(false);
            v_masker.setVisibility(View.GONE);
            mFolderListRecyclerView.setVisibility(View.GONE);
            mFolderListRecyclerView.setAnimation(AnimationUtils.loadAnimation(mContext,
                    uiConfig.isShowFromBottom() ? R.anim.picker_hide2bottom : R.anim.picker_anim_up));
        }
    }

    @Override
    public void onClick(@NonNull View v) {
        if (onDoubleClick()) {
            return;
        }
        if (v == v_masker) {
            toggleFolderList();
        }
    }

    @Override
    protected void loadMediaSetsComplete(@Nullable List<ImageSet> imageSetList) {
        if (imageSetList == null || imageSetList.size() == 0 ||
                (imageSetList.size() == 1 && imageSetList.get(0).count == 0)) {
            tip(getString(R.string.picker_str_tip_media_empty));
            return;
        }
        this.imageSets = imageSetList;
        mImageSetAdapter.refreshData(imageSets);
        selectImageFromSet(0, false);
    }

    @Override
    protected void loadMediaItemsComplete(ImageSet set) {
        this.imageItems = set.imageItems;
        controllerViewOnImageSetSelected(set);
        mAdapter.refreshData(imageItems);
    }

    @Override
    protected void refreshAllVideoSet(ImageSet allVideoSet) {
        if (allVideoSet != null && allVideoSet.imageItems != null
                && allVideoSet.imageItems.size() > 0
                && !imageSets.contains(allVideoSet)) {
            imageSets.add(1, allVideoSet);
            mImageSetAdapter.refreshData(imageSets);
        }
    }

    @Override
    public void onTakePhotoResult(@NonNull ImageItem imageItem) {
        //剪裁模式下，直接跳转剪裁页面
        if (selectConfig.getSelectMode() == SelectMode.MODE_CROP) {
            intentCrop(imageItem);
            return;
        }
        //单选模式下，直接回调出去
        if (selectConfig.getSelectMode() == SelectMode.MODE_SINGLE) {
            notifyOnSingleImagePickComplete(imageItem);
            return;
        }
        //将拍照返回的imageItem手动添加到第一个item上并选中
        addItemInImageSets(imageSets, imageItems, imageItem);
        mAdapter.refreshData(imageItems);
        mImageSetAdapter.refreshData(imageSets);
        onCheckItem(imageItem, PickerItemDisableCode.NORMAL);
    }

    @Override
    public boolean onBackPressed() {
        if (mFolderListRecyclerView != null && mFolderListRecyclerView.getVisibility() == View.VISIBLE) {
            toggleFolderList();
            return true;
        }
        if (presenter != null && presenter.interceptPickerCancel(getWeakActivity(), selectList)) {
            return true;
        }
        PickerErrorExecutor.executeError(onImagePickCompleteListener, PickerError.CANCEL.getCode());
        return false;
    }


    @Override
    public void onClickItem(@NonNull ImageItem item, int position, int disableItemCode) {
        position = selectConfig.isShowCamera() ? position - 1 : position;
        //拍照
        if (position < 0 && selectConfig.isShowCamera()) {
            if (!presenter.interceptCameraClick(getWeakActivity(), this)) {
                checkTakePhotoOrVideo();
            }
            return;
        }

        //当前选中item是否不可以点击
        if (interceptClickDisableItem(disableItemCode, false)) {
            return;
        }

        mRecyclerView.setTag(item);

        //剪裁模式下，直接跳转剪裁
        if (selectConfig.getSelectMode() == SelectMode.MODE_CROP) {
            if (item.isGif() || item.isVideo()) {
                notifyOnSingleImagePickComplete(item);
            } else {
                intentCrop(item);
            }
            return;
        }

        //检测是否拦截了item点击
        if (!mAdapter.isPreformClick() && presenter.interceptItemClick(getWeakActivity(), item, selectList, imageItems,
                selectConfig, mAdapter, false, this)) {
            return;
        }

        //如果当前是视频，且视频只能单选，且单选情况下自动回调，则执行回调
        if (item.isVideo() && selectConfig.isVideoSinglePickAndAutoComplete()) {
            notifyOnSingleImagePickComplete(item);
            return;
        }

        //如果当前是单选模式，且单选模式下点击item直接回调，则直接回调
        if (selectConfig.getMaxCount() <= 1 && selectConfig.isSinglePickAutoComplete()) {
            notifyOnSingleImagePickComplete(item);
            return;
        }

        //如果当前是视频，且不支持视频预览，则拦截掉点击
        if (item.isVideo() && !selectConfig.isCanPreviewVideo()) {
            tip(getActivity().getString(R.string.picker_str_tip_cant_preview_video));
            return;
        }

        //如果开启了预览，则直接跳转预览
        if (selectConfig.isPreview()) {
            intentPreview(true, position);
        }
    }

    @Override
    public void onCheckItem(ImageItem imageItem, int disableItemCode) {
        if (selectConfig.getSelectMode() == SelectMode.MODE_SINGLE
                && selectConfig.getMaxCount() == 1
                && selectList != null && selectList.size() > 0) {
            if (selectList.contains(imageItem)) {
                selectList.clear();
            } else {
                selectList.clear();
                selectList.add(imageItem);
            }
        } else {
            //当前选中item是否不可以点击
            if (interceptClickDisableItem(disableItemCode, true)) {
                return;
            }

            //检测是否拦截了item点击
            if (!mAdapter.isPreformClick() && presenter.interceptItemClick(getWeakActivity(), imageItem, selectList, imageItems,
                    selectConfig, mAdapter, true, this)) {
                return;
            }

            //如果当前选中列表包含此item,则移除，否则添加
            if (selectList.contains(imageItem)) {
                selectList.remove(imageItem);
            } else {
                selectList.add(imageItem);
            }
        }
        mAdapter.notifyDataSetChanged();
        refreshCompleteState();
    }

    /**
     * 跳转剪裁页面
     *
     * @param imageItem 图片信息
     */
    private void intentCrop(ImageItem imageItem) {
        ImagePicker.crop(getActivity(), presenter, selectConfig, imageItem, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                selectList.clear();
                selectList.addAll(items);
                mAdapter.notifyDataSetChanged();
                notifyPickerComplete();
            }
        });
    }

    /**
     * 跳转预览
     *
     * @param position 默认选中的index
     */
    @Override
    protected void intentPreview(boolean isClickItem, int position) {
        if (!isClickItem && (selectList == null || selectList.size() == 0)) {
            return;
        }
        MultiImagePreviewActivity.intent(getActivity(), isClickItem ? currentImageSet : null,
                selectList, selectConfig, presenter, position, new MultiImagePreviewActivity.PreviewResult() {
                    @Override
                    public void onResult(ArrayList<ImageItem> mImageItems, boolean isCancel) {
                        if (isCancel) {
                            reloadPickerWithList(mImageItems);
                        } else {
                            selectList.clear();
                            selectList.addAll(mImageItems);
                            mAdapter.notifyDataSetChanged();
                            notifyPickerComplete();
                        }
                    }
                });
    }

    /**
     * 刷新选中图片列表，执行回调，退出页面
     */
    @Override
    protected void notifyPickerComplete() {
        if (presenter == null||presenter.interceptPickerCompleteClick(getWeakActivity(), selectList, selectConfig)) {
            return;
        }
        if (onImagePickCompleteListener != null) {
            for (ImageItem imageItem : selectList) {
                imageItem.isOriginalImage = ImagePicker.isOriginalImage;
            }
            onImagePickCompleteListener.onImagePickComplete(selectList);
        }
    }

    @Override
    protected BaseSelectConfig getSelectConfig() {
        return selectConfig;
    }

    @Override
    protected IPickerPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected PickerUiConfig getUiConfig() {
        return uiConfig;
    }

    @Override
    public void onDestroy() {
        uiConfig.setPickerUiProvider(null);
        uiConfig = null;
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void reloadPickerWithList(List<ImageItem> selectedList) {
        selectList.clear();
        selectList.addAll(selectedList);
        mAdapter.refreshData(imageItems);
        refreshCompleteState();
    }
}
