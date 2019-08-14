package com.ypx.imagepicker.activity.crop;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.crop.CropGridAdapter;
import com.ypx.imagepicker.adapter.crop.CropSetAdapter;
import com.ypx.imagepicker.bean.ImageCropMode;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.MediaDataSource;
import com.ypx.imagepicker.helper.RecyclerViewTouchHelper;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.utils.CornerUtils;
import com.ypx.imagepicker.utils.FileUtil;
import com.ypx.imagepicker.utils.PermissionUtils;
import com.ypx.imagepicker.utils.TakePhotoUtil;
import com.ypx.imagepicker.utils.ViewSizeUtils;
import com.ypx.imagepicker.widget.TouchRecyclerView;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_CROPPICSAVEFILEPATH;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_FIRSTIMAGEITEM;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_IMAGELOADER;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_MAXSELECTEDCOUNT;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_SHOWBOTTOMVIEW;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_SHOWCAMERA;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_SHOWDRAFTDIALOG;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_SHOWVIDEO;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.INTENT_KEY_STARTDIRECT;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.REQ_CAMERA;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.REQ_STORAGE;


/**
 * Description: 图片选择和剪裁fragment
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImagePickAndCropFragment extends Fragment implements
        OnImagesLoadedListener, View.OnClickListener {
    private TouchRecyclerView mGridImageRecyclerView;
    private RecyclerView mImageSetRecyclerView;
    private TextView mTvSetName;
    private TextView mTvSetName2;
    private TextView mTvFullOrFit;
    private TextView mTvNext;
    private TextView mTvSelectNum;
    private ImageView mArrowImg;
    private PicBrowseImageView mCropView;
    private ImageButton stateBtn;
    private LinearLayout mCroupContainer;
    private LinearLayout mInvisibleContainer;
    private View maskView;
    private ImageItem lastPressImageItem;
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
    //存储已选择的剪裁View
    private HashMap<ImageItem, PicBrowseImageView> cropViewList = new HashMap<>();

    //图片加载提供者
    private ICropPickerBindPresenter bindingProvider;
    //最大选中数量
    private int maxCount = 0;
    //默认剪裁模式
    private int cropMode = ImageCropMode.CropViewScale_FILL;
    //之前操作后选中的第一张图片
    private ImageItem firstSelectedImageItem = null;
    //是否显示底部自定义View
    private boolean isShowBottomView = false;
    private boolean isShowDraftDialog = false;
    private boolean isShowCamera = false;
    private boolean isShowVideo = false;
    // 编辑图片后是否直接启动
    private boolean startDirect = true;
    //剪裁后图片存储的路径
    private String mCropPicsCacheFilePath;
    private ImageItem currentImageItem;
    private View mContentView;
    // fragment 形式调用的图片选中回调
    private OnImagePickCompleteListener imageListener;

    public void setImageListener(OnImagePickCompleteListener imageListener) {
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
        dealWithIntentData();
        initView();
        initGridImagesAndImageSets();
        loadImageData();
    }

    private void dealWithIntentData() {
        Bundle argu = getArguments();
        if (null != argu) {
            bindingProvider = (ICropPickerBindPresenter) argu.getSerializable(INTENT_KEY_IMAGELOADER);
            maxCount = argu.getInt(INTENT_KEY_MAXSELECTEDCOUNT, 9);
            firstSelectedImageItem = (ImageItem) argu.getSerializable(INTENT_KEY_FIRSTIMAGEITEM);
            if (firstSelectedImageItem != null) {
                cropMode = firstSelectedImageItem.getCropMode();
            }
            isShowBottomView = argu.getBoolean(INTENT_KEY_SHOWBOTTOMVIEW, false);
            mCropPicsCacheFilePath = argu.getString(INTENT_KEY_CROPPICSAVEFILEPATH);
            isShowDraftDialog = argu.getBoolean(INTENT_KEY_SHOWDRAFTDIALOG, false);
            isShowCamera = argu.getBoolean(INTENT_KEY_SHOWCAMERA, false);
            isShowVideo = argu.getBoolean(INTENT_KEY_SHOWVIDEO, false);
            startDirect = argu.getBoolean(INTENT_KEY_STARTDIRECT, true);
        }
        if (isShowDraftDialog && bindingProvider != null) {
            bindingProvider.showDraftDialog(getContext());
        }
    }

    /**
     * 加载图片数据
     */
    private void loadImageData() {
        if (getActivity() == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
        } else {
            //异步加载图片数据\
            MediaDataSource dataSource = new MediaDataSource(getActivity());
            dataSource.setLoadVideo(isShowVideo);
            dataSource.setLoadGif(false);
            dataSource.setLoadImage(true);
            dataSource.provideMediaItems(this);
        }
    }

    private void initView() {
        mTvSetName = mContentView.findViewById(R.id.mTvSetName);
        mTvSetName2 = mContentView.findViewById(R.id.mTvSetName2);
        mTvFullOrFit = mContentView.findViewById(R.id.mTvFullOrFit);
        mTvNext = mContentView.findViewById(R.id.mTvNext);
        mTvSelectNum = mContentView.findViewById(R.id.mTvSelectNum);
        mArrowImg = mContentView.findViewById(R.id.mArrowImg);
        maskView = mContentView.findViewById(R.id.v_mask);
        mCroupContainer = mContentView.findViewById(R.id.mCroupContainer);
        mInvisibleContainer = mContentView.findViewById(R.id.mInvisibleContainer);
        RelativeLayout topView = mContentView.findViewById(R.id.topView);
        RelativeLayout titleBar = mContentView.findViewById(R.id.titleBar);
        RelativeLayout mCropLayout = mContentView.findViewById(R.id.mCropLayout);
        stateBtn = mContentView.findViewById(R.id.stateBtn);
        mGridImageRecyclerView = mContentView.findViewById(R.id.mRecyclerView);
        mImageSetRecyclerView = mContentView.findViewById(R.id.mImageSetRecyclerView);
        mTvFullOrFit.setBackground(CornerUtils.cornerDrawable(Color.parseColor("#80000000"), dp(15)));
        mContentView.findViewById(R.id.mBackImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackPressed()) {
                    return;
                }
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        //初始化监听
        stateBtn.setOnClickListener(this);
        mTvSetName.setOnClickListener(this);
        mTvSetName2.setOnClickListener(this);
        maskView.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvFullOrFit.setOnClickListener(this);
        //未选中状态时，下一步制灰
        mTvNext.setEnabled(false);
        mTvNext.setTextColor(Color.parseColor("#B0B0B0"));
        mTvSelectNum.setVisibility(View.GONE);
        mTvSelectNum.setBackground(CornerUtils.cornerDrawable(getResources().getColor(R.color.picker_theme_color), dp(10)));
        //防止点击穿透
        mCropLayout.setClickable(true);
        titleBar.setClickable(true);
        //蒙层隐藏
        maskView.setAlpha(0f);
        maskView.setVisibility(View.GONE);
        //初始化相关尺寸信息
        mCropSize = ViewSizeUtils.getScreenWidth(getActivity());
        ViewSizeUtils.setViewSize(mCropLayout, mCropSize, 1.0f);
        touchHelper = RecyclerViewTouchHelper.create(mGridImageRecyclerView)
                .setTopView(topView)
                .setMaskView(maskView)
                .setCanScrollHeight(mCropSize)
                .setStickHeight(dp(50))
                .build();

        if (isShowBottomView && bindingProvider != null &&
                bindingProvider.getBottomView(getContext()) != null) {
            LinearLayout mBottomViewLayout = mContentView.findViewById(R.id.mBottomViewLayout);
            mBottomViewLayout.removeAllViews();
            final View view = bindingProvider.getBottomView(getContext());
            mBottomViewLayout.addView(view);
            view.post(new Runnable() {
                @Override
                public void run() {
                    mGridImageRecyclerView.setPadding(mGridImageRecyclerView.getPaddingStart(),
                            mGridImageRecyclerView.getPaddingTop(),
                            mGridImageRecyclerView.getPaddingEnd(), view.getHeight() + dp(5));
                    mImageSetRecyclerView.setPadding(0, mImageSetRecyclerView.getPaddingTop(), 0, view.getHeight());
                }
            });
        }
    }

    /**
     * 初始化图片列表
     */
    private void initGridImagesAndImageSets() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        mGridImageRecyclerView.setLayoutManager(gridLayoutManager);
        imageGridAdapter = new CropGridAdapter(getContext(), isShowCamera,
                firstSelectedImageItem != null,
                imageItems, selectList, bindingProvider);
        imageGridAdapter.setHasStableIds(true);
        imageGridAdapter.setOnPressImageListener(new CropGridAdapter.OnPressImageListener() {
            @Override
            public void onPressImage(int position, boolean isShowTransit) {
                pressImage(position, isShowTransit);
            }
        });
        imageGridAdapter.setOnSelectImageSet(new CropGridAdapter.OnSelectImageListener() {
            @Override
            public void onSelectImage(int position) {
                selectImage(position);
            }
        });
        mGridImageRecyclerView.setAdapter(imageGridAdapter);
        if (mGridImageRecyclerView.getItemAnimator() instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) mGridImageRecyclerView.getItemAnimator()).
                    setSupportsChangeAnimations(false);
        }
        //初始化文件夹列表
        mImageSetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        imageSetAdapter = new CropSetAdapter(getContext(), imageSets, bindingProvider);
        imageSetAdapter.setOnSelectImageSetListener(new CropSetAdapter.OnSelectImageSetListener() {
            @Override
            public void onSelectImageSet(int position) {
                selectImageSet(position);
            }
        });
        mImageSetRecyclerView.setAdapter(imageSetAdapter);
        mImageSetRecyclerView.setVisibility(View.GONE);
    }

    /**
     * 图片信息加载回调
     *
     * @param imageSetList 图片文件夹列表
     */
    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        if (this.imageSets.size() > 0 || imageSetList == null ||
                imageSetList.size() <= 0) {
            return;
        }
        this.imageSets.clear();
        this.imageSets.addAll(imageSetList);
        imageSetAdapter.notifyDataSetChanged();

        mTvSetName.setText(imageSetList.get(0).name);
        mTvSetName2.setText(imageSetList.get(0).name);

        imageItems.clear();
        imageItems.addAll(imageSetList.get(0).imageItems);
        imageGridAdapter.notifyDataSetChanged();
        pressImage(isShowCamera ? getFirstImage() + 1 : getFirstImage(), true);
    }

    private int getFirstImage() {
        for (int i = 0; i < imageItems.size(); i++) {
            if (!imageItems.get(i).isVideo()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        if (imageItems == null || imageItems.size() == 0) {
            return;
        }
        if (view == stateBtn) {
            fullOrFit();
        } else if (view == mTvSetName || view == mTvSetName2) {
            toggleImageSet();
        } else if (view == maskView) {
            touchHelper.transitTopWithAnim(true, pressImageIndex);
        } else if (view == mTvNext) {
            next();
        } else if (view == mTvFullOrFit) {
            fullOrWhiteSpace();
        }
    }

    /**
     * 点击图片
     *
     * @param position 图片位置
     */
    private void pressImage(final int position, boolean isShowTransit) {
        if (isShowCamera && position == 0) {
            takePhoto();
            return;
        }
        if (position < 0) {
            return;
        }
        pressImageIndex = isShowCamera ? position - 1 : position;
        currentImageItem = imageItems.get(pressImageIndex);
        if (currentImageItem.isVideo()) {
            if (selectList.size() > 0 || firstSelectedImageItem != null ||
                    currentImageItem.duration > ImagePicker.MAX_VIDEO_DURATION) {
                return;
            }
            if (bindingProvider != null) {
                bindingProvider.clickVideo(getActivity(), currentImageItem, startDirect);
            }
            return;
        }
        if (lastPressImageItem != null &&
                lastPressImageItem != currentImageItem) {
            lastPressImageItem.setPress(false);
        }
        currentImageItem.setPress(true);
        imageGridAdapter.notifyDataSetChanged();
        if (lastPressImageItem != currentImageItem) {
            loadCropView();
        }
        if (isShowTransit) {
            touchHelper.transitTopWithAnim(true, position);
        }
        checkStateBtn();
        lastPressImageItem = currentImageItem;
    }

    /**
     * 选中图片
     *
     * @param position 图片索引
     */
    private void selectImage(final int position) {
        if (position < 0) {
            return;
        }
        ImageItem selectImageItem = imageItems.get(isShowCamera ? position - 1 : position);
        if (selectImageItem.isSelect()) {
            selectImageItem.setSelect(false);
            removeImageItemFromCropViewList(selectImageItem);
            checkStateBtn();
        } else {
            if (isOverMaxCount()) {
                return;
            }
            selectImageItem.setSelect(true);
            pressImage(position, false);
            addImageItemToCropViewList(selectImageItem);
        }
        imageGridAdapter.notifyDataSetChanged();
    }

    /**
     * 点击选中相册
     *
     * @param position 相册position
     */
    private void selectImageSet(int position) {
        ImageSet imageSet = imageSets.get(position);
        if (imageSet == null) {
            return;
        }
        for (ImageSet set : imageSets) {
            set.isSelected = false;
        }
        imageSet.isSelected = true;
        imageSetAdapter.notifyDataSetChanged();

        mTvSetName2.setText(imageSet.name);
        mTvSetName.setText(imageSet.name);
        imageItems.clear();
        imageItems.addAll(imageSet.imageItems);
        imageGridAdapter.notifyDataSetChanged();
        mGridImageRecyclerView.smoothScrollToPosition(0);
        toggleImageSet();
    }

    /**
     * 加载剪裁view
     */
    private void loadCropView() {
        if (cropViewList.containsKey(currentImageItem) && cropViewList.get(currentImageItem) != null) {
            mCropView = cropViewList.get(currentImageItem);
        } else {
            mCropView = new PicBrowseImageView(getContext());
            mCropView.setBackgroundColor(Color.WHITE);
            mCropView.setLayoutParams(new LinearLayout.LayoutParams(mCropSize, mCropSize));
            //设置剪裁view的属性
            mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mCropView.enable(); // 启用图片缩放功能
            mCropView.setMaxScale(7.0f);
            if (bindingProvider != null) {
                bindingProvider.displayCropImage(mCropView, currentImageItem.path);
            }
        }
        mCropView.setOnImageLoadListener(new PicBrowseImageView.onImageLoadListener() {
            @Override
            public void onImageLoaded(float w, float h) {
                if (w > 0 && currentImageItem.width != (int) w) {
                    currentImageItem.width = (int) w;
                }
                if (h > 0 && currentImageItem.height != (int) h) {
                    currentImageItem.height = (int) h;
                }
            }
        });
        mCroupContainer.removeAllViews();
        if (mCropView.getParent() != null) {
            ((ViewGroup) mCropView.getParent()).removeView(mCropView);
        }
        mCroupContainer.addView(mCropView);
        resetCropViewSize(mCropView, false);
    }

    /**
     * 添加当前图片信息到选中列表
     */
    @SuppressLint("DefaultLocale")
    private void addImageItemToCropViewList(ImageItem imageItem) {
        if (!selectList.contains(imageItem)) {
            selectList.add(imageItem);
        }

        if (!cropViewList.containsKey(imageItem)) {
            cropViewList.put(imageItem, mCropView);
        }
        refreshSelectCount();
    }

    /**
     * 从选种列表中移除当前图片信息
     */
    private void removeImageItemFromCropViewList(ImageItem imageItem) {
        selectList.remove(imageItem);
        cropViewList.remove(imageItem);
        refreshSelectCount();
    }

    @SuppressLint("DefaultLocale")
    private void refreshSelectCount() {
        if (selectList.size() == 0) {
            mTvNext.setEnabled(false);
            mTvNext.setTextColor(Color.parseColor("#B0B0B0"));
            mTvSelectNum.setVisibility(View.GONE);
        } else {
            mTvNext.setEnabled(true);
            mTvNext.setTextColor(getResources().getColor(R.color.picker_theme_color));
            mTvSelectNum.setVisibility(View.VISIBLE);
            mTvSelectNum.setText(String.format("%d", selectList.size()));
        }
    }

    /**
     * 检测显示填充、留白、充满和自适应图标
     */
    private void checkStateBtn() {
        //方形图，什么都不显示
        if (currentImageItem.getWidthHeightType() == 0) {
            stateBtn.setVisibility(View.GONE);
            mTvFullOrFit.setVisibility(View.GONE);
            return;
        }
        //如果已经存在了第一张选中图
        if (firstSelectedImageItem != null) {
            stateBtn.setVisibility(View.GONE);
            setImageScaleState();
            return;
        }

        //当选中图片数量大于0 时
        if (selectList.size() > 0) {
            //如果当前选中item就是第一个图片，显示stateBtn
            if (currentImageItem == selectList.get(0)) {
                stateBtn.setVisibility(View.VISIBLE);
                mTvFullOrFit.setVisibility(View.GONE);
                mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                currentImageItem.setCropMode(cropMode);
            } else {
                //如果当前选中item不是第一张图片，显示mTvFullOrFit
                stateBtn.setVisibility(View.GONE);
                setImageScaleState();
            }
        } else {//没有选中图片
            stateBtn.setVisibility(View.VISIBLE);
            mTvFullOrFit.setVisibility(View.GONE);
        }
    }

    /**
     * 重置剪裁宽高大小
     */
    private void resetCropViewSize(PicBrowseImageView view, boolean isShowAnim) {
        int height = mCropSize;
        int width = mCropSize;
        if (cropMode == ImageCropMode.CropViewScale_FIT) {
            ImageItem firstImageItem;
            //如果已经存在第一张图，则按照第一张图的剪裁模式改变大小
            if (firstSelectedImageItem != null) {
                firstImageItem = firstSelectedImageItem;
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
        animCropView(view, isShowAnim, width, height);
    }

    /**
     * 动画设置cropView的尺寸
     */
    @SuppressLint("ObjectAnimatorBinding")
    private void animCropView(final PicBrowseImageView view, boolean isShowAnim, final int endWidth, final int endHeight) {
        if (isShowAnim) {
            final int startWidth = ViewSizeUtils.getViewWidth(mCropView);
            final int startHeight = ViewSizeUtils.getViewHeight(mCropView);
            ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationY", 0.0f, 1.0f);
            anim.setDuration(200);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float ratio = (Float) animation.getAnimatedValue();
                    ViewSizeUtils.setViewSize(view, (int) ((endWidth - startWidth) * ratio + startWidth),
                            ((int) ((endHeight - startHeight) * ratio + startHeight)));
                    view.setImageDrawable(view.getDrawable());
                }
            });
            anim.start();
        } else {
            ViewSizeUtils.setViewSize(view, endWidth, endHeight);
        }
    }

    /**
     * 第一张图片剪裁区域充满或者自适应（是剪裁区域，不是图片填充和留白）
     */
    private void fullOrFit() {
        if (cropMode == ImageCropMode.CropViewScale_FIT) {
            cropMode = ImageCropMode.CropViewScale_FILL;
            stateBtn.setImageDrawable(getResources().getDrawable(R.mipmap.picker_icon_fit));
        } else {
            cropMode = ImageCropMode.CropViewScale_FIT;
            stateBtn.setImageDrawable(getResources().getDrawable(R.mipmap.picker_icon_fill));
        }
        if (currentImageItem != null) {
            currentImageItem.setCropMode(cropMode);
        }

        mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        resetCropViewSize(mCropView, true);
        //以下是重置所有选中图片剪裁模式
        mInvisibleContainer.removeAllViews();
        mInvisibleContainer.setVisibility(View.VISIBLE);
        for (ImageItem imageItem : selectList) {
            if (imageItem == currentImageItem) {
                continue;
            }
            PicBrowseImageView picBrowseImageView = cropViewList.get(imageItem);
            if (picBrowseImageView != null) {
                mInvisibleContainer.addView(picBrowseImageView);
                resetCropViewSize(picBrowseImageView, false);
                if (cropMode == ImageCropMode.CropViewScale_FIT) {
                    imageItem.setCropMode(ImageCropMode.ImageScale_FILL);
                    picBrowseImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                cropViewList.put(imageItem, picBrowseImageView);
            }
        }
        mInvisibleContainer.setVisibility(View.INVISIBLE);
    }

    /**
     * 填充状态
     */
    private void setHasWhiteSpaceState() {
        mTvFullOrFit.setText(R.string.picker_str_full);
        mTvFullOrFit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.picker_icon_full), null, null, null);
    }

    /**
     * 留白状态
     */
    private void setImageViewScaleFull() {
        mTvFullOrFit.setText(R.string.picker_str_haswhite);
        mTvFullOrFit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.picker_icon_haswhite), null, null, null);
    }

    private void setImageScaleState() {
        //如果当前模式为自适应模式
        if (cropMode == ImageCropMode.CropViewScale_FIT) {
            //如果当前图片和第一张选中图片的宽高类型一样，则不显示留白和充满
            mTvFullOrFit.setVisibility(View.GONE);
        } else {//如果第一张图为充满模式，则不论宽高比（除正方形外），都显示留白和充满
            mTvFullOrFit.setVisibility(View.VISIBLE);
            //如果当前已选中该图片，则恢复选择时的填充和留白状态
            if (currentImageItem.isSelect()) {
                if (currentImageItem.getCropMode() == ImageCropMode.ImageScale_FILL) {
                    setImageViewScaleFull();
                } else if (currentImageItem.getCropMode() == ImageCropMode.ImageScale_FIT) {
                    setHasWhiteSpaceState();
                }
            } else {
                //否则都按照默认填充的模式，显示留白提示
                setImageViewScaleFull();
                currentImageItem.setCropMode(ImageCropMode.ImageScale_FILL);
                mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }

    /**
     * 充满或者留白
     */
    private void fullOrWhiteSpace() {
        if (currentImageItem.getCropMode() == ImageCropMode.ImageScale_FILL) {
            //留白
            currentImageItem.setCropMode(ImageCropMode.ImageScale_FIT);
            mCropView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            setHasWhiteSpaceState();
        } else {
            //充满
            currentImageItem.setCropMode(ImageCropMode.ImageScale_FILL);
            mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            setImageViewScaleFull();
        }
        resetCropViewSize(mCropView, false);
    }

    /**
     * 切换文件夹选择
     */
    private void toggleImageSet() {
        if (mImageSetRecyclerView.getVisibility() == View.VISIBLE) {
            mImageSetRecyclerView.setVisibility(View.GONE);
            mArrowImg.setRotation(0);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.picker_anim_up);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) mTvSetName2.getParent()).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mImageSetRecyclerView.setAnimation(animation);
        } else {
            mArrowImg.setRotation(180);
            mImageSetRecyclerView.setVisibility(View.VISIBLE);
            ((ViewGroup) mTvSetName2.getParent()).setVisibility(View.VISIBLE);
            mImageSetRecyclerView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_anim_in));
        }
    }

    /**
     * 点击下一步
     */
    private void next() {
        if (mCropView.isShowLine()) {
            return;
        }
        if (currentImageItem.isSelect()
                && (mCropView.getDrawable() == null ||
                mCropView.getImgRect().height() == 0 ||
                mCropView.getImgRect().width() == 0)) {
            Toast.makeText(getActivity(), getString(R.string.wait_for_load), Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<ImageItem> cropUrlList = new ArrayList<>();
        for (ImageItem imageItem : selectList) {
            View view = cropViewList.get(imageItem);
            File f = new File(mCropPicsCacheFilePath, "crop_" + System.currentTimeMillis() + ".jpg");
            String cropUrl = FileUtil.saveBitmapToLocalWithJPEG(view, f.getAbsolutePath());
            imageItem.setCropUrl(cropUrl);
            imageItem.setCropMode(cropMode);
            imageItem.setSelect(false);
            imageItem.setPress(false);
            cropUrlList.add(imageItem);
        }

        if (null != imageListener) {
            imageListener.onImagePickComplete(cropUrlList);
        }
    }


    /**
     * 清空缓存数据选中状态
     */
    private void clearData() {
        for (ImageItem imageItem : selectList) {
            imageItem.setPress(false);
            imageItem.setSelect(false);
        }
        if (lastPressImageItem != null) {
            lastPressImageItem.setSelect(false);
            lastPressImageItem.setPress(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearData();
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        if (isOverMaxCount() || getActivity() == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
        } else {
            TakePhotoUtil.takePhoto(getActivity(), REQ_CAMERA);
        }
    }

    /**
     * 刷新相册
     */
    private void refreshGalleryAddPic() {
        if (getActivity() == null) {
            return;
        }
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(TakePhotoUtil.mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    /**
     * 是否超过最大限制数
     *
     * @return true:超过
     */
    private boolean isOverMaxCount() {
        if (selectList.size() >= maxCount) {
            showTipDialog(String.format(getString(R.string.picker_str_selectmaxcount), maxCount));
            return true;
        }
        return false;
    }

    private void showTipDialog(String msg) {
        if (getActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.picker_str_isee,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onTakePhotoResult(int requestCode, int resultCode) {
        if (resultCode == RESULT_OK && requestCode == REQ_CAMERA) {
            if (!TextUtils.isEmpty(TakePhotoUtil.mCurrentPhotoPath)) {
                refreshGalleryAddPic();
                ImageItem item = new ImageItem(TakePhotoUtil.mCurrentPhotoPath, System.currentTimeMillis());
                item.width = FileUtil.getImageWidthHeight(TakePhotoUtil.mCurrentPhotoPath)[0];
                item.height = FileUtil.getImageWidthHeight(TakePhotoUtil.mCurrentPhotoPath)[1];
                imageItems.add(0, item);
                if (imageSets != null && imageSets.size() > 0 && imageSets.get(0).imageItems != null) {
                    imageSets.get(0).imageItems.add(0, item);
                }
                selectImage(isShowCamera ? 1 : 0);
                imageGridAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                pressImage(0, true);
            } else {
                PermissionUtils.create(getContext()).showSetPermissionDialog(getString(R.string.picker_str_camerapermisson));
            }
        } else if (requestCode == REQ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                loadImageData();
            } else {
                PermissionUtils.create(getContext()).showSetPermissionDialog(getString(R.string.picker_str_storagepermisson));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 相册选择是否打开
     */
    public boolean onBackPressed() {
        if (mImageSetRecyclerView.getVisibility() == View.VISIBLE) {
            toggleImageSet();
            return true;
        }
        return false;
    }

    final public int dp(float dp) {
        if (getActivity() == null || getContext() == null) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }
}
