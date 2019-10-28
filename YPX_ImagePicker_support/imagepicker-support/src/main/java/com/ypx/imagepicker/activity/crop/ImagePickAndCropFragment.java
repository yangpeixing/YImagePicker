package com.ypx.imagepicker.activity.crop;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.PBaseLoaderFragment;
import com.ypx.imagepicker.helper.PickerErrorExecutor;
import com.ypx.imagepicker.adapter.crop.CropGridAdapter;
import com.ypx.imagepicker.adapter.crop.CropSetAdapter;
import com.ypx.imagepicker.bean.BaseSelectConfig;
import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.CropViewContainerHelper;
import com.ypx.imagepicker.helper.RecyclerViewTouchHelper;
import com.ypx.imagepicker.helper.VideoViewContainerHelper;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PFileUtil;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.widget.cropimage.CropImageView;
import com.ypx.imagepicker.widget.TouchRecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_DATA_PRESENTER;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_SELECT_CONFIG;

/**
 * Description: 图片选择和剪裁fragment
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
public class ImagePickAndCropFragment extends PBaseLoaderFragment implements View.OnClickListener,
        CropSetAdapter.OnSelectImageSetListener,
        CropGridAdapter.OnPressImageListener,
        CropGridAdapter.OnSelectImageListener {
    private TouchRecyclerView mGridImageRecyclerView;
    private RecyclerView mImageSetRecyclerView;
    private TextView mTvSetName;
    private TextView mTvFullOrGap;
    private TextView mTvNext;
    private TextView mTvSelectNum;
    private ImageView mArrowImg;
    private RelativeLayout titleBar;
    private CropImageView mCropView;
    private ImageButton stateBtn;
    private ImageView mBackImg;
    private FrameLayout mCropContainer;
    private LinearLayout mInvisibleContainer;
    private View maskView;
    private CropGridAdapter imageGridAdapter;
    private CropSetAdapter imageSetAdapter;
    private List<ImageSet> imageSets = new ArrayList<>();
    private List<ImageItem> imageItems = new ArrayList<>();
    private int mCropSize;
    private int pressImageIndex = 0;
    //滑动辅助类
    private RecyclerViewTouchHelper touchHelper;
    //选中图片列表
    private List<ImageItem> selectList = new ArrayList<>();
    //图片加载提供者
    private ICropPickerBindPresenter presenter;
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
    private CropUiConfig uiConfig;

    /**
     * @param imageListener 选择回调监听
     */
    public void setOnImagePickCompleteListener(OnImagePickCompleteListener imageListener) {
        this.imageListener = imageListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.picker_activity_selectpicandcrop, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isIntentDataValid()) {
            initView();
            initUI();
            initGridImagesAndImageSets();
            loadMediaSets();
        }
    }

    /**
     * 校验传递数据是否合法
     */
    private boolean isIntentDataValid() {
        Bundle arguments = getArguments();
        if (null != arguments) {
            presenter = (ICropPickerBindPresenter) arguments.getSerializable(INTENT_KEY_DATA_PRESENTER);
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
        mTvSetName = mContentView.findViewById(R.id.mTvSetName);
        mTvFullOrGap = mContentView.findViewById(R.id.mTvFullOrGap);
        mTvNext = mContentView.findViewById(R.id.mTvNext);
        mTvSelectNum = mContentView.findViewById(R.id.mTvSelectNum);
        mArrowImg = mContentView.findViewById(R.id.mArrowImg);
        maskView = mContentView.findViewById(R.id.v_mask);
        mCropContainer = mContentView.findViewById(R.id.mCroupContainer);
        mInvisibleContainer = mContentView.findViewById(R.id.mInvisibleContainer);
        RelativeLayout topView = mContentView.findViewById(R.id.topView);
        titleBar = mContentView.findViewById(R.id.titleBar);
        RelativeLayout mCropLayout = mContentView.findViewById(R.id.mCropLayout);
        stateBtn = mContentView.findViewById(R.id.stateBtn);
        mGridImageRecyclerView = mContentView.findViewById(R.id.mRecyclerView);
        mImageSetRecyclerView = mContentView.findViewById(R.id.mImageSetRecyclerView);
        mTvFullOrGap.setBackground(PCornerUtils.cornerDrawable(Color.parseColor("#80000000"), dp(15)));
        //初始化监听
        mBackImg = mContentView.findViewById(R.id.mBackImg);
        mBackImg.setOnClickListener(this);
        stateBtn.setOnClickListener(this);
        mTvSetName.setOnClickListener(this);
        maskView.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvFullOrGap.setOnClickListener(this);
        //未选中状态时，下一步制灰
        mTvNext.setEnabled(false);
        mTvNext.setTextColor(Color.parseColor("#B0B0B0"));
        mTvSelectNum.setVisibility(View.GONE);
        //防止点击穿透
        mCropLayout.setClickable(true);
        titleBar.setClickable(true);
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
                .setStickHeight(dp(55))
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
        uiConfig = presenter.getUiConfig(getActivity());
        if (uiConfig == null) {
            uiConfig = new CropUiConfig();
        }
        mBackImg.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
        mBackImg.setColorFilter(uiConfig.getBackIconColor());
        titleBar.setBackgroundColor(uiConfig.getTitleBarBackgroundColor());
        mCropContainer.setBackgroundColor(uiConfig.getCropViewBackgroundColor());
        mTvSetName.setTextColor(uiConfig.getTitleTextColor());
        mArrowImg.setImageDrawable(getResources().getDrawable(uiConfig.getTitleArrowIconID()));
        mArrowImg.setColorFilter(uiConfig.getTitleTextColor());
        mTvSelectNum.setBackground(PCornerUtils.cornerDrawable(uiConfig.getNextBtnSelectedTextColor(), dp(10)));
        mGridImageRecyclerView.setBackgroundColor(uiConfig.getGridBackgroundColor());
        mTvNext.setText(uiConfig.getNextBtnText());
        mTvNext.setBackground(uiConfig.getNextBtnUnSelectBackground());
        mTvNext.setTextColor(uiConfig.getNextBtnUnSelectTextColor());
    }

    /**
     * 初始化图片列表
     */
    private void initGridImagesAndImageSets() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), selectConfig.getColumnCount());
        mGridImageRecyclerView.setLayoutManager(gridLayoutManager);
        imageGridAdapter = new CropGridAdapter(mContentView.getContext(), selectConfig, imageItems, selectList, presenter, uiConfig);
        imageGridAdapter.setHasStableIds(true);
        mGridImageRecyclerView.setAdapter(imageGridAdapter);
        //初始化文件夹列表
        mImageSetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imageSetAdapter = new CropSetAdapter(getContext(), imageSets, presenter);
        mImageSetRecyclerView.setAdapter(imageSetAdapter);
        mImageSetRecyclerView.setVisibility(View.GONE);
        imageSetAdapter.setOnSelectImageSetListener(this);
        imageGridAdapter.setOnPressImageListener(this);
        imageGridAdapter.setOnSelectImageSet(this);
    }

    @Override
    public void onSelectImageSet(int position) {
        selectImageSet(position, true);
    }

    @Override
    public void onClick(View view) {
        if (imageItems == null || imageItems.size() == 0) {
            return;
        }
        if (view == stateBtn) {
            fullOrFit();
        } else if (view == mTvSetName) {
            toggleImageSet();
        } else if (view == maskView) {
            touchHelper.transitTopWithAnim(true, pressImageIndex, true);
        } else if (view == mTvNext) {
            next();
        } else if (view == mTvFullOrGap) {
            fullOrGap();
        } else if (view.getId() == R.id.mBackImg) {
            if (onBackPressed()) {
                return;
            }
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }

    private ImageItem lastPressItem;

    /**
     * 点击图片
     *
     * @param position 图片位置
     */
    @Override
    public void onPressImage(final int position, boolean isShowTransit) {
        if (selectConfig.isShowCamera() && position == 0) {
            takePhoto();
            return;
        }
        if (position < 0) {
            return;
        }
        //得到当前选中的item索引
        pressImageIndex = selectConfig.isShowCamera() ? position - 1 : position;
        //防止数组越界
        if (imageItems == null || imageItems.size() == 0 ||
                imageItems.size() <= pressImageIndex) {
            return;
        }
        currentImageItem = imageItems.get(pressImageIndex);
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
            //如果当前视频只支持单选的话，执行presenter的clickVideo方法
            if (selectConfig.isVideoSinglePick()) {
                if (presenter != null) {
                    presenter.clickVideo(getActivity(), currentImageItem);
                }
            } else {
                if (currentImageItem.duration == 0 || !PFileUtil.exists(currentImageItem.path)) {
                    Toast.makeText(getActivity(), R.string.str_video_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                //执行预览视频操作
                videoViewContainerHelper.loadVideoView(mCropContainer, currentImageItem, presenter, uiConfig);
                checkStateBtn();
            }
        } else {
            //加载图片
            loadCropView();
            checkStateBtn();
            if (mTvFullOrGap.getVisibility() == View.VISIBLE) {
                if (mTvFullOrGap.getText().toString().equals(getString(R.string.picker_str_haswhite))) {
                    mCropView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    mCropView.setBackgroundColor(Color.WHITE);
                }
            } else {
                mCropView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        imageGridAdapter.notifyDataSetChanged();
        touchHelper.transitTopWithAnim(true, position, isShowTransit);
        lastPressItem = currentImageItem;
    }

    /**
     * 选中图片
     *
     * @param position 图片索引
     */
    @Override
    public void onSelectImage(int position) {
        if (position < 0) {
            return;
        }
        ImageItem selectImageItem = imageItems.get(selectConfig.isShowCamera() ? position - 1 : position);
        if (selectList.contains(selectImageItem)) {
            removeImageItemFromCropViewList(selectImageItem);
            checkStateBtn();
        } else {
            if (isOverMaxCount()) {
                return;
            }
            onPressImage(position, false);
            addImageItemToCropViewList(selectImageItem);
        }
        imageGridAdapter.notifyDataSetChanged();
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
        imageSetAdapter.notifyDataSetChanged();
        mTvSetName.setText(imageSet.name);
        if (isTransit) {
            toggleImageSet();
        }
        loadMediaItemsFromSet(imageSet);
    }

    /**
     * 加载剪裁view
     */
    private void loadCropView() {
        mCropView = cropViewContainerHelper.loadCropView(getContext(), currentImageItem, mCropSize, presenter);
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
        refreshSelectCount();
    }

    /**
     * 从选种列表中移除当前图片信息
     */
    private void removeImageItemFromCropViewList(ImageItem imageItem) {
        selectList.remove(imageItem);
        cropViewContainerHelper.removeCropView(imageItem);
        refreshSelectCount();
    }

    /**
     * 刷新选中数量
     */
    private void refreshSelectCount() {
        if (selectList.size() == 0) {
            mTvNext.setEnabled(false);
            mTvNext.setTextColor(uiConfig.getNextBtnUnSelectTextColor());
            mTvNext.setBackground(uiConfig.getNextBtnUnSelectBackground());
            mTvSelectNum.setVisibility(View.GONE);
        } else {
            mTvNext.setEnabled(true);
            mTvNext.setTextColor(uiConfig.getNextBtnSelectedTextColor());
            mTvNext.setBackground(uiConfig.getNextBtnSelectedBackground());
            if (uiConfig.getNextBtnSelectedBackground() == null) {
                mTvNext.setPadding(0, dp(4), dp(10), dp(4));
            }
            if (uiConfig.isShowNextCount()) {
                mTvSelectNum.setVisibility(View.VISIBLE);
                mTvSelectNum.setText(String.valueOf(selectList.size()));
            }
        }
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
            setImageScaleState();
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
        mTvFullOrGap.setText(R.string.picker_str_full);
        cropViewContainerHelper.setBackgroundColor(Color.WHITE);
        mTvFullOrGap.setCompoundDrawablesWithIntrinsicBounds(getResources().
                getDrawable(uiConfig.getFillIconID()), null, null, null);
    }

    /**
     * 充满情况下，显示留白状态
     */
    private void fullState() {
        mTvFullOrGap.setText(R.string.picker_str_haswhite);
        cropViewContainerHelper.setBackgroundColor(Color.TRANSPARENT);
        mTvFullOrGap.setCompoundDrawablesWithIntrinsicBounds(getResources().
                getDrawable(uiConfig.getGapIconID()), null, null, null);
    }

    /**
     * 切换文件夹选择
     */
    private void toggleImageSet() {
        boolean isVisible = mImageSetRecyclerView.getVisibility() == View.VISIBLE;
        mImageSetRecyclerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        mArrowImg.setRotation(isVisible ? 0 : 180);
        mImageSetRecyclerView.startAnimation(AnimationUtils.loadAnimation(getContext(),
                isVisible ? R.anim.picker_anim_up : R.anim.picker_anim_in));
    }

    /**
     * 点击下一步
     */
    private void next() {
        if (mCropView.isEditing()) {
            return;
        }
        if (selectList.contains(currentImageItem)
                && (mCropView.getDrawable() == null ||
                mCropView.getDrawable().getIntrinsicHeight() == 0 ||
                mCropView.getDrawable().getIntrinsicWidth() == 0)) {
            Toast.makeText(getActivity(), getString(R.string.wait_for_load), Toast.LENGTH_SHORT).show();
            return;
        }
        //如果当前选择的都是视频
        if (selectList.size() > 0 && selectList.get(0).isVideo() && !selectConfig.isVideoSinglePick()) {
            imageListener.onImagePickComplete((ArrayList<ImageItem>) selectList);
        } else {
            ArrayList<ImageItem> cropUrlList = cropViewContainerHelper.
                    generateCropUrls(selectList, selectConfig.getCropSaveFilePath(), cropMode);
            if (null != imageListener) {
                imageListener.onImagePickComplete(cropUrlList);
            }
        }
    }

    /**
     * 是否超过最大限制数
     *
     * @return true:超过
     */
    private boolean isOverMaxCount() {
        if (selectList.size() >= selectConfig.getMaxCount()) {
            String tip = String.format(getString(R.string.picker_str_selectmaxcount), selectConfig.getMaxCount());
            if (presenter != null) {
                presenter.overMaxCountTip(getContext(), selectConfig.getMaxCount(), tip);
            } else {
                Toast.makeText(getContext(), tip, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void takePhoto() {
        if (isOverMaxCount()) {
            return;
        }
        super.takePhoto();
    }

    @Override
    protected BaseSelectConfig getSelectConfig() {
        return selectConfig;
    }

    @Override
    protected void loadMediaSetsComplete(List<ImageSet> imageSetList) {
        if (imageSetList == null || imageSetList.size() == 0 || (imageSetList.size() == 1 && imageSetList.get(0).count == 0)) {
            PickerErrorExecutor.executeError(imageListener, PickerError.MEDIA_NOT_FOUND.getCode());
            return;
        }
        this.imageSets.clear();
        this.imageSets.addAll(imageSetList);
        imageSetAdapter.notifyDataSetChanged();
        selectImageSet(0, false);
    }

    @Override
    protected void loadMediaItemsComplete(ImageSet set) {
        imageItems.clear();
        if (set.imageItems != null && set.imageItems.size() > 0) {
            imageItems.addAll(set.imageItems);
            imageGridAdapter.notifyDataSetChanged();
            int firstImageIndex = getFirstImage();
            if (firstImageIndex < 0) {
                return;
            }
            int index = selectConfig.isShowCamera() ? firstImageIndex + 1 : firstImageIndex;
            onPressImage(index, true);
        }
    }

    private int getFirstImage() {
        for (int i = 0; i < imageItems.size(); i++) {
            if (isItemCanPress(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 检测item是否可以被选中
     *
     * @param position 当前item的索引
     * @return 是否可以选中
     */
    private boolean isItemCanPress(int position) {
        ImageItem item = imageItems.get(position);
        //如果是图片，则不校验，默认选中第一个即可
        if (!item.isVideo()) {
            return true;
        }
        //如果当前选中的文件中第一个是图片，则代表只能选择图片，则该项跳过
        if (selectList.size() > 0 && selectList.get(0) != null && !selectList.get(0).isVideo()) {
            return false;
        }
        //如果视频单选，跳过
        if (selectConfig.isVideoSinglePick()) {
            return false;
        }
        //如果该视频超过了可选择的最大时长，跳过
        return item.duration <= ImagePicker.MAX_VIDEO_DURATION;
    }

    @Override
    protected void refreshAllVideoSet(ImageSet allVideoSet) {
        if (allVideoSet != null &&
                allVideoSet.imageItems != null
                && allVideoSet.imageItems.size() > 0
                && !imageSets.contains(allVideoSet)) {
            imageSets.add(1, allVideoSet);
            imageSetAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 相册选择是否打开
     */
    @Override
    public boolean onBackPressed() {
        if (mImageSetRecyclerView != null && mImageSetRecyclerView.getVisibility() == View.VISIBLE) {
            toggleImageSet();
            return true;
        }
        PickerErrorExecutor.executeError(imageListener, PickerError.CANCEL.getCode());
        return false;
    }

    @Override
    protected void onTakePhotoResult(ImageItem imageItem) {
        if (imageItem != null) {
            imageItems.add(0, imageItem);
            if (imageSets != null && imageSets.size() > 0 && imageSets.get(0).imageItems != null) {
                imageSets.get(0).imageItems.add(0, imageItem);
            }
            onSelectImage(selectConfig.isShowCamera() ? 1 : 0);
            imageGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //将VideoView所占用的资源释放掉
        if (videoViewContainerHelper != null) {
            videoViewContainerHelper.onDestroy();
        }
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
