package com.ypx.imagepicker.activity.crop;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.PBaseLoaderFragment;
import com.ypx.imagepicker.adapter.PickerFolderAdapter;
import com.ypx.imagepicker.adapter.PickerItemAdapter;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.selectconfig.CropSelectConfig;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.CropViewContainerHelper;
import com.ypx.imagepicker.helper.RecyclerViewTouchHelper;
import com.ypx.imagepicker.helper.VideoViewContainerHelper;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.widget.cropimage.CropImageView;
import com.ypx.imagepicker.widget.TouchRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.crop.MultiImageCropActivity.INTENT_KEY_DATA_PRESENTER;
import static com.ypx.imagepicker.activity.crop.MultiImageCropActivity.INTENT_KEY_SELECT_CONFIG;

/**
 * Description: 图片选择和剪裁fragment
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x
 */
public class MultiImageCropFragment extends PBaseLoaderFragment implements View.OnClickListener,
        PickerFolderAdapter.FolderSelectResult,
        PickerItemAdapter.OnActionResult {
    private TouchRecyclerView mGridImageRecyclerView;
    private RecyclerView mFolderListRecyclerView;
    private TextView mTvFullOrGap;
    private CropImageView mCropView;
    private ImageButton stateBtn;
    private FrameLayout mCropContainer;
    private RelativeLayout mCropLayout;
    private LinearLayout mInvisibleContainer;
    private View maskView, mImageSetMasker;
    private PickerItemAdapter imageGridAdapter;
    private PickerFolderAdapter folderAdapter;
    private List<ImageSet> imageSets = new ArrayList<>();
    private List<ImageItem> imageItems = new ArrayList<>();
    private int mCropSize;
    private int pressImageIndex = 0;
    //滑动辅助类
    private RecyclerViewTouchHelper touchHelper;
    //图片加载提供者
    private IPickerPresenter presenter;
    //选择配置项
    private CropSelectConfig selectConfig;
    // 默认剪裁模式：充满
    private int cropMode = ImageCropMode.CropViewScale_FULL;
    private ImageItem currentImageItem;
    private View mContentView;
    // fragment 形式调用的图片选中回调
    private OnImagePickCompleteListener imageListener;
    //剪裁view或videoView填充辅助类
    private CropViewContainerHelper cropViewContainerHelper;
    private VideoViewContainerHelper videoViewContainerHelper;
    //UI配置类
    private PickerUiConfig uiConfig;

    private FrameLayout titleBarContainer;
    private FrameLayout bottomBarContainer;
    private FrameLayout titleBarContainer2;

    private ImageItem lastPressItem;

    /**
     * @param imageListener 选择回调监听
     */
    public void setOnImagePickCompleteListener(@NonNull OnImagePickCompleteListener imageListener) {
        this.imageListener = imageListener;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.picker_activity_multi_crop, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isIntentDataValid()) {
            ImagePicker.isOriginalImage = false;
            uiConfig = presenter.getUiConfig(getWeakActivity());
            setStatusBar();
            initView();
            initUI();
            initGridImagesAndImageSets();
            loadMediaSets();
        }
    }

    /**
     * 校验传递数据
     */
    private boolean isIntentDataValid() {
        Bundle arguments = getArguments();
        if (null != arguments) {
            presenter = (IPickerPresenter) arguments.getSerializable(INTENT_KEY_DATA_PRESENTER);
            selectConfig = (CropSelectConfig) arguments.getSerializable(INTENT_KEY_SELECT_CONFIG);
        }

        if (presenter == null) {
            PickerErrorExecutor.executeError(imageListener, PickerError.PRESENTER_NOT_FOUND.getCode());
            return false;
        }

        if (selectConfig == null) {
            PickerErrorExecutor.executeError(imageListener, PickerError.SELECT_CONFIG_NOT_FOUND.getCode());
            return false;
        }
        return true;
    }

    /**
     * 初始化界面
     */
    private void initView() {
        titleBarContainer = mContentView.findViewById(R.id.titleBarContainer);
        titleBarContainer2 = mContentView.findViewById(R.id.titleBarContainer2);
        bottomBarContainer = mContentView.findViewById(R.id.bottomBarContainer);
        mTvFullOrGap = mContentView.findViewById(R.id.mTvFullOrGap);
        mImageSetMasker = mContentView.findViewById(R.id.mImageSetMasker);
        maskView = mContentView.findViewById(R.id.v_mask);
        mCropContainer = mContentView.findViewById(R.id.mCroupContainer);
        mInvisibleContainer = mContentView.findViewById(R.id.mInvisibleContainer);
        RelativeLayout topView = mContentView.findViewById(R.id.topView);
        mCropLayout = mContentView.findViewById(R.id.mCropLayout);
        stateBtn = mContentView.findViewById(R.id.stateBtn);
        mGridImageRecyclerView = mContentView.findViewById(R.id.mRecyclerView);
        mFolderListRecyclerView = mContentView.findViewById(R.id.mImageSetRecyclerView);
        mTvFullOrGap.setBackground(PCornerUtils.cornerDrawable(Color.parseColor("#80000000"), dp(15)));
        //初始化监听
        stateBtn.setOnClickListener(this);
        maskView.setOnClickListener(this);
        mImageSetMasker.setOnClickListener(this);
        mTvFullOrGap.setOnClickListener(this);
        //防止点击穿透
        mCropLayout.setClickable(true);
        //蒙层隐藏
        maskView.setAlpha(0f);
        maskView.setVisibility(View.GONE);
        //初始化相关尺寸信息
        mCropSize = PViewSizeUtils.getScreenWidth(getActivity());
        PViewSizeUtils.setViewSize(mCropLayout, mCropSize, 1.0f);
        //recyclerView和topView的联动效果辅助类
        touchHelper = RecyclerViewTouchHelper.create(mGridImageRecyclerView)
                .setTopView(topView)
                .setMaskView(maskView)
                .setCanScrollHeight(mCropSize)
                .build();
        //剪裁控件辅助类
        cropViewContainerHelper = new CropViewContainerHelper(mCropContainer);
        //视频控件辅助类
        videoViewContainerHelper = new VideoViewContainerHelper();
        //指定默认剪裁模式
        if (selectConfig.hasFirstImageItem()) {
            cropMode = selectConfig.getFirstImageItem().getCropMode();
        }
    }

    /**
     * 初始化自定义样式
     */
    private void initUI() {
        //拿到自定义标题栏和底部栏
        titleBar = inflateControllerView(titleBarContainer, true, uiConfig);
        bottomBar = inflateControllerView(bottomBarContainer, false, uiConfig);
        //如果包含标题栏
        if (titleBar != null) {
            PViewSizeUtils.setMarginTop(mCropLayout, titleBar.getViewHeight());
            touchHelper.setStickHeight(titleBar.getViewHeight());
        }
        //如果包含底部栏
        if (bottomBar != null) {
            PViewSizeUtils.setMarginTopAndBottom(mGridImageRecyclerView, 0, bottomBar.getViewHeight());
        }
        //设置基础样式
        mCropContainer.setBackgroundColor(uiConfig.getCropViewBackgroundColor());
        mGridImageRecyclerView.setBackgroundColor(uiConfig.getPickerBackgroundColor());
        stateBtn.setImageDrawable(getResources().getDrawable(uiConfig.getFullIconID()));
        mTvFullOrGap.setCompoundDrawablesWithIntrinsicBounds(getResources().
                getDrawable(uiConfig.getFillIconID()), null, null, null);
        //设置相册列表高度
        setFolderListHeight(mFolderListRecyclerView, mImageSetMasker, true);
    }

    /**
     * 初始化图片列表
     */
    private void initGridImagesAndImageSets() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), selectConfig.getColumnCount());
        mGridImageRecyclerView.setLayoutManager(gridLayoutManager);
        imageGridAdapter = new PickerItemAdapter(selectList, imageItems, selectConfig, presenter, uiConfig);
        imageGridAdapter.setHasStableIds(true);
        mGridImageRecyclerView.setAdapter(imageGridAdapter);
        //初始化文件夹列表
        mFolderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        folderAdapter = new PickerFolderAdapter(presenter, uiConfig);
        mFolderListRecyclerView.setAdapter(folderAdapter);
        folderAdapter.refreshData(imageSets);
        mFolderListRecyclerView.setVisibility(View.GONE);
        folderAdapter.setFolderSelectResult(this);
        imageGridAdapter.setOnActionResult(this);
    }

    @Override
    public void onClick(@NonNull View view) {
        if (imageItems == null || imageItems.size() == 0) {
            return;
        }
        if (onDoubleClick()) {
            tip(getActivity().getString(R.string.picker_str_tip_action_frequently));
            return;
        }
        if (view == stateBtn) {
            fullOrFit();
        } else if (view == maskView) {
            touchHelper.transitTopWithAnim(true, pressImageIndex, true);
        } else if (view == mTvFullOrGap) {
            fullOrGap();
        } else if (mImageSetMasker == view) {
            toggleFolderList();
        }
    }


    /**
     * 点击操作
     *
     * @param imageItem 当前item
     * @param position  当前item的position
     */
    @Override
    public void onClickItem(@NonNull ImageItem imageItem, int position, int disableItemCode) {
        //拍照
        if (position <= 0 && selectConfig.isShowCamera()) {
            //拦截拍照点击
            if (presenter.interceptCameraClick(getWeakActivity(), this)) {
                return;
            }
            checkTakePhotoOrVideo();
            return;
        }

        //当前选中item是否不可以点击
        if (interceptClickDisableItem(disableItemCode, false)) {
            return;
        }

        //得到当前选中的item索引
        pressImageIndex = position;
        //防止数组越界
        if (imageItems == null || imageItems.size() == 0 ||
                imageItems.size() <= pressImageIndex) {
            return;
        }

        //是否拦截当前item的点击事件
        if (isInterceptItemClick(imageItem, false)) {
            return;
        }

        //选中当前item
        onPressImage(imageItem, true);
    }


    private boolean isInterceptItemClick(ImageItem imageItem, boolean isClickCheckbox) {
        return !imageGridAdapter.isPreformClick() && presenter.interceptItemClick(getWeakActivity(), imageItem, selectList,
                (ArrayList<ImageItem>) imageItems, selectConfig, imageGridAdapter, isClickCheckbox,
                null);
    }

    /**
     * 点击图片
     *
     * @param imageItem 图片
     */
    private void onPressImage(ImageItem imageItem, boolean isShowTransit) {
        currentImageItem = imageItem;
        if (lastPressItem != null) {
            //如果当前选中的item和上一次选中的一致，则不处理
            if (lastPressItem.equals(currentImageItem)) {
                return;
            }
            //取消上次选中
            lastPressItem.setPress(false);
        }
        currentImageItem.setPress(true);
        //当前选中视频
        if (currentImageItem.isVideo()) {
            if (selectConfig.isVideoSinglePickAndAutoComplete()) {
                notifyOnSingleImagePickComplete(imageItem);
                return;
            }
            //执行预览视频操作
            videoViewContainerHelper.loadVideoView(mCropContainer, currentImageItem, presenter, uiConfig);
        } else {
            //加载图片
            loadCropView();
        }
        checkStateBtn();
        imageGridAdapter.notifyDataSetChanged();
        touchHelper.transitTopWithAnim(true, pressImageIndex, isShowTransit);
        lastPressItem = currentImageItem;
    }


    /**
     * 执行选中（取消选中）操作
     *
     * @param imageItem 当前item
     */
    @Override
    public void onCheckItem(ImageItem imageItem, int disableItemCode) {
        //当前选中item是否不可以点击
        if (interceptClickDisableItem(disableItemCode, true)) {
            return;
        }

        //是否拦截当前item的点击事件
        if (isInterceptItemClick(imageItem, true)) {
            return;
        }

        //如果当前选中列表已经包含了此item，则移除并刷新
        if (selectList.contains(imageItem)) {
            removeImageItemFromCropViewList(imageItem);
            checkStateBtn();
        } else {
            onPressImage(imageItem, false);
            addImageItemToCropViewList(imageItem);
        }
        imageGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void folderSelected(ImageSet set, int pos) {
        selectImageSet(pos, true);
    }

    /**
     * 点击选中相册
     *
     * @param position 相册position
     */
    private void selectImageSet(int position, boolean isTransit) {
        ImageSet imageSet = imageSets.get(position);
        if (imageSet == null) {
            return;
        }
        for (ImageSet set : imageSets) {
            set.isSelected = false;
        }
        imageSet.isSelected = true;
        folderAdapter.notifyDataSetChanged();
        if (titleBar != null) {
            titleBar.onImageSetSelected(imageSet);
        }
        if (bottomBar != null) {
            bottomBar.onImageSetSelected(imageSet);
        }
        if (isTransit) {
            toggleFolderList();
        }
        loadMediaItemsFromSet(imageSet);
    }

    /**
     * 加载剪裁view
     */
    private void loadCropView() {
        mCropView = cropViewContainerHelper.loadCropView(getContext(), currentImageItem, mCropSize,
                presenter, new CropViewContainerHelper.onLoadComplete() {
                    @Override
                    public void loadComplete() {
                        checkStateBtn();
                    }
                });
        resetCropViewSize(mCropView, false);
    }

    /**
     * 添加当前图片信息到选中列表
     */
    private void addImageItemToCropViewList(ImageItem imageItem) {
        if (!selectList.contains(imageItem)) {
            selectList.add(imageItem);
        }
        cropViewContainerHelper.addCropView(mCropView, imageItem);
        refreshCompleteState();
    }

    /**
     * 从选种列表中移除当前图片信息
     */
    private void removeImageItemFromCropViewList(ImageItem imageItem) {
        selectList.remove(imageItem);
        cropViewContainerHelper.removeCropView(imageItem);
        refreshCompleteState();
    }

    /**
     * 检测显示填充、留白、充满和自适应图标
     */
    private void checkStateBtn() {
        //选中的第一个item是视频，则隐藏所有按钮
        if (currentImageItem.isVideo()) {
            stateBtn.setVisibility(View.GONE);
            mTvFullOrGap.setVisibility(View.GONE);
            return;
        }
        //方形图，什么都不显示
        if (currentImageItem.getWidthHeightType() == 0) {
            stateBtn.setVisibility(View.GONE);
            mTvFullOrGap.setVisibility(View.GONE);
            return;
        }
        //如果已经存在了第一张选中图
        if (selectConfig.hasFirstImageItem()) {
            stateBtn.setVisibility(View.GONE);
            if (selectConfig.isAssignGapState()) {
                if (selectList.size() == 0 || (selectList.get(0) != null
                        && selectList.get(0).equals(currentImageItem))) {
                    setImageScaleState();
                } else {
                    mTvFullOrGap.setVisibility(View.GONE);
                    if (selectList.get(0).getCropMode() == ImageCropMode.ImageScale_GAP) {
                        mCropView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        mCropView.setBackgroundColor(Color.WHITE);
                    } else {
                        mCropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        mCropView.setBackgroundColor(Color.TRANSPARENT);
                    }
                }
            } else {
                setImageScaleState();
            }
            return;
        }

        //当选中图片数量大于0 时
        if (selectList.size() > 0) {
            //如果当前选中item就是第一个图片，显示stateBtn
            if (currentImageItem == selectList.get(0)) {
                stateBtn.setVisibility(View.VISIBLE);
                mTvFullOrGap.setVisibility(View.GONE);
                mCropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                currentImageItem.setCropMode(cropMode);
            } else {
                //如果当前选中item不是第一张图片，显示mTvFullOrGap
                stateBtn.setVisibility(View.GONE);
                setImageScaleState();
            }
        } else {//没有选中图片
            stateBtn.setVisibility(View.VISIBLE);
            mTvFullOrGap.setVisibility(View.GONE);
        }
    }

    /**
     * 重置剪裁宽高大小
     */
    private void resetCropViewSize(CropImageView view, boolean isShowAnim) {
        int height = mCropSize;
        int width = mCropSize;
        if (cropMode == ImageCropMode.CropViewScale_FIT) {
            ImageItem firstImageItem;
            //如果已经存在第一张图，则按照第一张图的剪裁模式改变大小
            if (selectConfig.hasFirstImageItem()) {
                firstImageItem = selectConfig.getFirstImageItem();
            } else {
                //没有已经存在的第一张图信息，则获取选中的第一张图的剪裁模式作为全局的剪裁模式
                if (selectList.size() > 0) {
                    firstImageItem = selectList.get(0);
                } else {
                    firstImageItem = currentImageItem;
                }
            }
            //如果是宽图，高*3/4
            height = firstImageItem.getWidthHeightType() > 0 ? ((mCropSize * 3) / 4) : mCropSize;
            //如果是高图，宽*3/4
            width = firstImageItem.getWidthHeightType() < 0 ? ((mCropSize * 3) / 4) : mCropSize;
        }
        view.changeSize(isShowAnim, width, height);
    }


    /**
     * 第一张图片剪裁区域充满或者自适应（是剪裁区域，不是图片填充和留白）
     */
    private void fullOrFit() {
        if (cropMode == ImageCropMode.CropViewScale_FIT) {
            cropMode = ImageCropMode.CropViewScale_FULL;
            stateBtn.setImageDrawable(getResources().getDrawable(uiConfig.getFitIconID()));
        } else {
            cropMode = ImageCropMode.CropViewScale_FIT;
            stateBtn.setImageDrawable(getResources().getDrawable(uiConfig.getFullIconID()));
        }
        if (currentImageItem != null) {
            currentImageItem.setCropMode(cropMode);
        }

        mCropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        resetCropViewSize(mCropView, true);
        //以下是重置所有选中图片剪裁模式
        cropViewContainerHelper.refreshAllState(currentImageItem, selectList, mInvisibleContainer,
                cropMode == ImageCropMode.CropViewScale_FIT,
                new CropViewContainerHelper.ResetSizeExecutor() {
                    @Override
                    public void resetAllCropViewSize(CropImageView view) {
                        resetCropViewSize(view, false);
                    }
                });
    }


    /**
     * 设置留白还是填充
     */
    private void setImageScaleState() {
        //如果当前模式为自适应模式
        if (cropMode == ImageCropMode.CropViewScale_FIT) {
            //如果当前图片和第一张选中图片的宽高类型一样，则不显示留白和充满
            mTvFullOrGap.setVisibility(View.GONE);
        } else {
            //如果第一张图为充满模式，则不论宽高比（除正方形外），都显示留白和充满
            mTvFullOrGap.setVisibility(View.VISIBLE);
            //如果当前已选中该图片，则恢复选择时的填充和留白状态
            if (selectList.contains(currentImageItem)) {
                if (currentImageItem.getCropMode() == ImageCropMode.ImageScale_FILL) {
                    fullState();
                } else if (currentImageItem.getCropMode() == ImageCropMode.ImageScale_GAP) {
                    gapState();
                }
            } else {
                //否则都按照默认填充的模式，显示留白提示
                fullState();
                currentImageItem.setCropMode(ImageCropMode.ImageScale_FILL);
                mCropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        }
    }

    /**
     * 充满或者留白
     */
    private void fullOrGap() {
        //留白
        if (currentImageItem.getCropMode() == ImageCropMode.ImageScale_FILL) {
            currentImageItem.setCropMode(ImageCropMode.ImageScale_GAP);
            mCropView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            gapState();
        } else {
            //充满
            currentImageItem.setCropMode(ImageCropMode.ImageScale_FILL);
            mCropView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            fullState();
        }
        resetCropViewSize(mCropView, false);
    }

    /**
     * 留白情况下，显示充满状态
     */
    private void gapState() {
        mTvFullOrGap.setText(getString(R.string.picker_str_redBook_full));
        mCropView.setBackgroundColor(Color.WHITE);
        mTvFullOrGap.setCompoundDrawablesWithIntrinsicBounds(getResources().
                getDrawable(uiConfig.getFillIconID()), null, null, null);
    }

    /**
     * 充满情况下，显示留白状态
     */
    private void fullState() {
        mTvFullOrGap.setText(getString(R.string.picker_str_redBook_gap));
        mCropView.setBackgroundColor(Color.TRANSPARENT);
        mTvFullOrGap.setCompoundDrawablesWithIntrinsicBounds(getResources().
                getDrawable(uiConfig.getGapIconID()), null, null, null);
    }


    /**
     * 刷新选中图片列表，执行回调，退出页面
     */
    @Override
    protected void notifyPickerComplete() {
        //如果当前选择的都是视频
        if (selectList.size() > 0 && selectList.get(0).isVideo()) {
        } else {
            //正在编辑
            if (mCropView.isEditing()) {
                return;
            }
            //未加载出图片
            if (selectList.contains(currentImageItem)
                    && (mCropView.getDrawable() == null ||
                    mCropView.getDrawable().getIntrinsicHeight() == 0 ||
                    mCropView.getDrawable().getIntrinsicWidth() == 0)) {
                tip(getString(R.string.picker_str_tip_shield));
                return;
            }
            selectList = cropViewContainerHelper.generateCropUrls(selectList, cropMode);
        }

        //如果拦截了完成操作，则执行自定义的拦截操作
        if (!presenter.interceptPickerCompleteClick(getWeakActivity(), selectList, selectConfig)) {
            if (null != imageListener) {
                imageListener.onImagePickComplete(selectList);
            }
        }
    }


    @Override
    protected void toggleFolderList() {
        if (mFolderListRecyclerView.getVisibility() == View.GONE) {
            View view = titleBarContainer.getChildAt(0);
            if (view == null) {
                return;
            }
            titleBarContainer.removeAllViews();
            titleBarContainer2.removeAllViews();
            titleBarContainer2.addView(view);

            mImageSetMasker.setVisibility(View.VISIBLE);
            controllerViewOnTransitImageSet(true);
            mFolderListRecyclerView.setVisibility(View.VISIBLE);
            mFolderListRecyclerView.setAnimation(AnimationUtils.loadAnimation(getActivity(),
                    uiConfig.isShowFromBottom() ? R.anim.picker_show2bottom : R.anim.picker_anim_in));

        } else {
            final View view = titleBarContainer2.getChildAt(0);
            if (view == null) {
                return;
            }
            mImageSetMasker.setVisibility(View.GONE);
            controllerViewOnTransitImageSet(false);
            mFolderListRecyclerView.setVisibility(View.GONE);
            mFolderListRecyclerView.setAnimation(AnimationUtils.loadAnimation(getActivity(),
                    uiConfig.isShowFromBottom() ? R.anim.picker_hide2bottom : R.anim.picker_anim_up));

            titleBarContainer2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    titleBarContainer2.removeAllViews();
                    titleBarContainer.removeAllViews();
                    titleBarContainer.addView(view);
                }
            }, 300);
        }

    }

    @Override
    protected void intentPreview(boolean isFolderListPreview, int index) {

    }

    @Override
    protected void loadMediaSetsComplete(@Nullable List<ImageSet> imageSetList) {
        if (imageSetList == null || imageSetList.size() == 0 ||
                (imageSetList.size() == 1 && imageSetList.get(0).count == 0)) {
            tip(getString(R.string.picker_str_tip_media_empty));
            return;
        }
        this.imageSets = imageSetList;
        folderAdapter.refreshData(imageSets);
        selectImageSet(0, false);
    }

    @Override
    protected void loadMediaItemsComplete(@NonNull ImageSet set) {
        if (set.imageItems != null && set.imageItems.size() > 0) {
            imageItems.clear();
            imageItems.addAll(set.imageItems);
            imageGridAdapter.notifyDataSetChanged();
            int firstImageIndex = getCanPressItemPosition();
            if (firstImageIndex < 0) {
                return;
            }
            int index = selectConfig.isShowCamera() ? firstImageIndex + 1 : firstImageIndex;
            onClickItem(imageItems.get(firstImageIndex), index, PickerItemDisableCode.NORMAL);
        }
    }

    /**
     * @return 获取第一个有效的item（可以选择的）
     */
    private int getCanPressItemPosition() {
        for (int i = 0; i < imageItems.size(); i++) {
            ImageItem imageItem = imageItems.get(i);
            if (imageItem.isVideo() && selectConfig.isVideoSinglePickAndAutoComplete()) {
                continue;
            }
            int code = PickerItemDisableCode.getItemDisableCode(imageItem, selectConfig,
                    selectList, false);
            if (code == PickerItemDisableCode.NORMAL) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void refreshAllVideoSet(@Nullable ImageSet allVideoSet) {
        if (allVideoSet != null &&
                allVideoSet.imageItems != null
                && allVideoSet.imageItems.size() > 0
                && !imageSets.contains(allVideoSet)) {
            imageSets.add(1, allVideoSet);
            folderAdapter.refreshData(imageSets);
        }
    }

    /**
     * 相册选择是否打开
     */
    @Override
    public boolean onBackPressed() {
        if (mFolderListRecyclerView != null && mFolderListRecyclerView.getVisibility() == View.VISIBLE) {
            toggleFolderList();
            return true;
        }
        if (presenter != null && presenter.interceptPickerCancel(getWeakActivity(), selectList)) {
            return true;
        }
        PickerErrorExecutor.executeError(imageListener, PickerError.CANCEL.getCode());
        return false;
    }


    @Override
    public void onTakePhotoResult(@Nullable ImageItem imageItem) {
        if (imageItem != null) {
            addItemInImageSets(imageSets, imageItems, imageItem);
            onCheckItem(imageItem, PickerItemDisableCode.NORMAL);
            imageGridAdapter.notifyDataSetChanged();
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
        //将VideoView所占用的资源释放掉
        if (videoViewContainerHelper != null) {
            videoViewContainerHelper.onDestroy();
        }
        uiConfig.setPickerUiProvider(null);
        uiConfig = null;
        presenter = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoViewContainerHelper != null) {
            videoViewContainerHelper.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoViewContainerHelper != null) {
            videoViewContainerHelper.onPause();
        }
    }
}
