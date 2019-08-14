package com.ypx.imagepicker.activity.multi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.adapter.multi.MultiSetAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.MediaDataSource;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PermissionUtils;
import com.ypx.imagepicker.utils.StatusBarUtil;
import com.ypx.imagepicker.utils.TakePhotoUtil;
import com.ypx.imagepicker.utils.ViewSizeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.REQ_STORAGE;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_UI_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.REQ_CAMERA;

/**
 * Description: 多选页
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiImagePickerFragment extends Fragment implements OnImagesLoadedListener, View.OnClickListener, MultiGridAdapter.OnActionResult {
    private List<ImageSet> imageSets;
    private List<ImageItem> imageItems;

    private RecyclerView mRecyclerView;
    private View v_masker;
    private Button btnDir;
    private TextView mTvTime;
    private MultiSetAdapter mImageSetAdapter;
    private ListView mImageSetListView;
    private MultiGridAdapter mAdapter;
    private int currentSetIndex = 0;

    private TextView mTvPreview;
    private TextView mTvRight;
    private ImageView mSetArrowImg;
    private TextView mTvTitle;
    private ImageView mBckImg;
    private ViewGroup mTitleLayout;
    private RelativeLayout mBottomLayout;

    private PickerSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private PickerUiConfig uiConfig;
    private FragmentActivity mContext;
    private GridLayoutManager layoutManager;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.picker_activity_images_grid, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        dealWithData();
        if (selectConfig == null || presenter == null) {
            mContext.finish();
            return;
        }

        if (selectConfig.getLastImageList() != null && selectConfig.getLastImageList().size() > 0) {
            MultiPickerData.instance.addAllImageItems(selectConfig.getLastImageList());
        }

        uiConfig = presenter.getUiConfig(mContext);
        if (selectConfig.getSelectMode() == ImageSelectMode.MODE_TAKEPHOTO) {
            TakePhotoUtil.takePhoto(mContext, REQ_CAMERA);
        } else {
            findView();
            initAdapters();
            loadPicData();
        }
    }

    private void dealWithData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectConfig = (PickerSelectConfig) bundle.getSerializable(INTENT_KEY_SELECT_CONFIG);
            presenter = (IMultiPickerBindPresenter) bundle.getSerializable(INTENT_KEY_UI_CONFIG);
        }
    }

    /**
     * 初始化控件
     */
    private void findView() {
        v_masker = view.findViewById(R.id.v_masker);
        btnDir = view.findViewById(R.id.btn_dir);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mImageSetListView = view.findViewById(R.id.lv_imagesets);
        mTvTime = view.findViewById(R.id.tv_time);
        mTvTime.setVisibility(View.GONE);
        mSetArrowImg = view.findViewById(R.id.mSetArrowImg);
        mTvTitle = view.findViewById(R.id.tv_title);
        mTvRight = view.findViewById(R.id.tv_rightBtn);
        mTitleLayout = view.findViewById(R.id.top_bar);
        mBottomLayout = view.findViewById(R.id.footer_panel);
        mBckImg = view.findViewById(R.id.iv_back);
        mTvPreview = view.findViewById(R.id.tv_preview);
        setUi();
        setListener();
        refreshOKBtn();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageItems != null && mAdapter != null) {
            mAdapter.refreshData(imageItems);
            refreshOKBtn();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MultiPickerData.instance.clear();
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

    private void setUi() {
        if (uiConfig.isImmersionBar()) {
            StatusBarUtil.setStatusBar(mContext, Color.TRANSPARENT, true,
                    StatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));

            mTitleLayout.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        }
        mBckImg.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
        mBckImg.setColorFilter(uiConfig.getBackIconColor());
        mTitleLayout.setBackgroundColor(uiConfig.getTitleBarBackgroundColor());
        mRecyclerView.setBackgroundColor(uiConfig.getPickerBackgroundColor());
        mBottomLayout.setBackgroundColor(uiConfig.getBottomBarBackgroundColor());
        mTvTitle.setTextColor(uiConfig.getTitleColor());
        if (uiConfig.getOkBtnSelectBackground() == null && uiConfig.getOkBtnUnSelectBackground() == null) {
            mTvRight.setPadding(0, 0, 0, 0);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageSetListView.getLayoutParams();
        int height = (int) (getResources().getDisplayMetrics().heightPixels / 4f);
        if (uiConfig.getPickStyle() == PickerUiConfig.PICK_STYLE_BOTTOM) {
            mBottomLayout.setVisibility(View.VISIBLE);
            v_masker.setPadding(0, 0, 0, ViewSizeUtils.dp(mContext, 51));
            mRecyclerView.setPadding(0, 0, 0, ViewSizeUtils.dp(mContext, 51));
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.bottomMargin = mRecyclerView.getPaddingBottom();
            params.topMargin = height;
        } else {
            mBottomLayout.setVisibility(View.GONE);
            v_masker.setPadding(0, 0, 0, 0);
            mRecyclerView.setPadding(0, 0, 0, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.bottomMargin = height;
            params.topMargin = 0;
        }

        ((LinearLayout) view.findViewById(R.id.mTitleRoot)).setGravity(uiConfig.getTitleBarGravity());
        mSetArrowImg.setImageDrawable(uiConfig.getTitleDrawableRight());

        if (selectConfig.isShowVideo() && selectConfig.isShowImage()) {
            mTvTitle.setText(getResources().getString(R.string.str_image_video));
        } else if (selectConfig.isShowVideo()) {
            mTvTitle.setText(getResources().getString(R.string.str_video));
        } else {
            mTvTitle.setText(getResources().getString(R.string.str_image));
        }
    }

    /**
     * 初始化监听
     */
    private void setListener() {
        btnDir.setOnClickListener(this);
        v_masker.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mTvTitle.setOnClickListener(this);
        mSetArrowImg.setOnClickListener(this);
        mTvPreview.setOnClickListener(this);
        mRecyclerView.addOnScrollListener(onScrollListener);
        mBckImg.setOnClickListener(this);
        mImageSetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectImageSet(position);
            }
        });

    }


    /**
     * 初始化相关adapter
     */
    private void initAdapters() {
        mImageSetAdapter = new MultiSetAdapter(mContext, presenter);
        mImageSetAdapter.refreshData(imageSets);
        mImageSetListView.setAdapter(mImageSetAdapter);

        mAdapter = new MultiGridAdapter(mContext, new ArrayList<ImageItem>(), selectConfig, presenter);
        mAdapter.setOnActionResult(this);
        layoutManager = new GridLayoutManager(mContext, selectConfig.getColumnCount());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 异步加载图片数据
     */
    private void loadPicData() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
            }
        } else {
            //从媒体库拿到数据
            MediaDataSource dataSource = new MediaDataSource(mContext);
            dataSource.setLoadImage(selectConfig.isShowImage());
            dataSource.setLoadGif(selectConfig.isLoadGif());
            dataSource.setLoadVideo(selectConfig.isShowVideo());
            dataSource.provideMediaItems(this);
        }
    }

    /**
     * 选择图片文件夹
     *
     * @param position 位置
     */
    private void selectImageSet(final int position) {
        this.currentSetIndex = position;
        this.imageItems = imageSets.get(position).imageItems;
        MultiPickerData.instance.setCurrentImageSet(imageSets.get(position));
        showOrHideImageSetList();
        mImageSetAdapter.setSelectIndex(currentSetIndex);
        ImageSet imageSet = imageSets.get(position);
        if (null != imageSet) {
            mAdapter.refreshData(imageSet.imageItems);
            btnDir.setText(imageSet.name);
            mTvTitle.setText(imageSet.name);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 显示或隐藏图片文件夹选项列表
     */
    private void showOrHideImageSetList() {
        if (mImageSetListView.getVisibility() == View.GONE) {
            mSetArrowImg.setRotation(180);
            v_masker.setVisibility(View.VISIBLE);
            mImageSetListView.setVisibility(View.VISIBLE);
            mImageSetListView.setAnimation(AnimationUtils.loadAnimation(mContext,
                    uiConfig.isBottomStyle() ? R.anim.picker_show2bottom : R.anim.picker_anim_in));
            int index = mImageSetAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            mImageSetListView.setSelection(index);
        } else {
            mSetArrowImg.setRotation(0);
            v_masker.setVisibility(View.GONE);
            mImageSetListView.setVisibility(View.GONE);
            mImageSetListView.setAnimation(AnimationUtils.loadAnimation(mContext,
                    uiConfig.isBottomStyle() ? R.anim.picker_hide2bottom : R.anim.picker_anim_up));
        }
    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        if (imageSetList == null || imageSetList.size() == 0) {
            btnDir.setText("无媒体文件");
            return;
        }
        this.imageSets = imageSetList;
        this.imageItems = imageSetList.get(currentSetIndex).imageItems;
        MultiPickerData.instance.setCurrentImageSet(imageSets.get(currentSetIndex));
        btnDir.setText(imageSets.get(currentSetIndex).name);
        mTvTitle.setText(imageSets.get(currentSetIndex).name);
        mAdapter.refreshData(imageItems);
        mImageSetAdapter.refreshData(imageSets);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDir || v == v_masker) {
            showOrHideImageSetList();
        } else if (v == mTvRight) {
            if (isEmpty()) {
                return;
            }
            notifyOnImagePickComplete(MultiPickerData.instance.getSelectImageList());
        } else if (v == mBckImg) {
            mContext.onBackPressed();
        } else if (v == mTvTitle || v == mSetArrowImg) {
            if (!uiConfig.isBottomStyle()) {
                showOrHideImageSetList();
            }
        } else if (v == mTvPreview) {
            if (isEmpty()) {
                return;
            }
            intentPreview(0, MultiPickerData.instance.getSelectImageList());
        }
    }

    /**
     * 是否未选择
     *
     * @return true：未选择图片 false:选择了
     */
    private boolean isEmpty() {
        if (MultiPickerData.instance.isEmpty()) {
            presenter.tip(mContext, getResources()
                    .getString(R.string.str_emptytip));
            return true;
        }
        return false;
    }

    /**
     * 刷新选中图片列表，执行回调，退出页面
     *
     * @param list 选中图片列表
     */
    private void notifyOnImagePickComplete(List<ImageItem> list) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKERRESULT, (Serializable) list);
        mContext.setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        mContext.finish();
    }

    /**
     * 拍照回调
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     */
    public void onTakePhotoResult(int requestCode, int resultCode) {
        if (resultCode == RESULT_OK && requestCode == REQ_CAMERA) {//拍照返回
            if (!TextUtils.isEmpty(TakePhotoUtil.mCurrentPhotoPath)) {
                if (selectConfig.getSelectMode() == ImageSelectMode.MODE_CROP) {
                    intentCrop(TakePhotoUtil.mCurrentPhotoPath);
                    return;
                }
                TakePhotoUtil.refreshGalleryAddPic(mContext);
                ImageItem item = new ImageItem();
                item.path = TakePhotoUtil.mCurrentPhotoPath;
                List<ImageItem> list = new ArrayList<>();
                list.add(item);
                notifyOnImagePickComplete(list);
            }
        }
    }

    /**
     * 当前文件夹是否打开
     *
     * @return 当前文件夹是否打开
     */
    public boolean isImageSetShow() {
        if (mImageSetListView != null && mImageSetListView.getVisibility() == View.VISIBLE) {
            showOrHideImageSetList();
            return true;
        }
        return false;
    }

    /**
     * 跳转剪裁页面
     *
     * @param path 图片路径
     */
    private void intentCrop(String path) {
        SingleCropActivity.intentCrop(mContext, presenter, selectConfig, path, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                notifyOnImagePickComplete(items);
            }
        });
    }

    /**
     * 跳转预览
     *
     * @param position 默认选中的index
     */
    private void intentPreview(int position, ArrayList<ImageItem> previewList) {
        MultiImagePreviewActivity.preview(mContext, selectConfig, presenter, true, previewList, position,
                new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        notifyOnImagePickComplete(items);
                    }
                });
    }


    @SuppressLint("DefaultLocale")
    private void refreshOKBtn() {
        if (selectConfig.getSelectMode() != ImageSelectMode.MODE_MULTI) {
            mTvRight.setVisibility(View.GONE);
            return;
        }
        int selectCount = MultiPickerData.instance.getSelectCount();
        if (MultiPickerData.instance.isEmpty()) {
            mTvRight.setEnabled(false);
            mTvRight.setText(uiConfig.getOkBtnText());
            mTvRight.setBackground(uiConfig.getOkBtnUnSelectBackground());
            mTvRight.setTextColor(uiConfig.getOkBtnUnSelectTextColor());
            mTvPreview.setVisibility(View.GONE);
        } else {
            mTvRight.setEnabled(true);
            mTvRight.setText(String.format("%s(%d/%d)", uiConfig.getOkBtnText(),
                    selectCount, selectConfig.getMaxCount()));
            mTvRight.setBackground(uiConfig.getOkBtnSelectBackground());
            mTvRight.setTextColor(uiConfig.getOkBtnSelectTextColor());
            mTvPreview.setText(String.format("预览(%d)", selectCount));
            //可以预览时才显示预览按钮
            if (selectConfig.isPreview()) {
                mTvPreview.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClickItem(ImageItem item, int position) {
        if (selectConfig.isShieldItem(item)) {
            presenter.tip(getContext(), getResources().getString(R.string.str_shield));
            return;
        }

        mRecyclerView.setTag(item);
        switch (selectConfig.getSelectMode()) {
            //多选情况下，点击跳转预览
            case ImageSelectMode.MODE_MULTI:
                //如果只能选择一个是视频，且当前是视频的时候直接返回
                if (selectConfig.isVideoSinglePick() && item.isVideo()) {
                    ArrayList<ImageItem> list = new ArrayList<>();
                    list.add(item);
                    notifyOnImagePickComplete(list);
                    return;
                }
                //打开了预览，则跳转预览
                if (selectConfig.isPreview()) {
                    ImageSet imageSet = imageSets.get(currentSetIndex);
                    MultiPickerData.instance.setCurrentImageSet(imageSet);
                    intentPreview(position, null);
                } else {
                    presenter.imageItemClick(mContext, item, MultiPickerData.instance.getSelectImageList()
                            , imageSets.get(currentSetIndex).imageItems, mAdapter);
                }
                break;
            //单选情况下，点击直接返回
            case ImageSelectMode.MODE_SINGLE:
                List<ImageItem> list2 = new ArrayList<>();
                list2.add(item);
                notifyOnImagePickComplete(list2);
                break;
            //剪裁情况下，点击跳转剪裁
            case ImageSelectMode.MODE_CROP:
                intentCrop(item.path);
                break;
        }
    }

    @Override
    public void onCheckItem(ImageItem imageItem, boolean isChecked) {
        if (!MultiPickerData.instance.hasItem(imageItem) &&
                MultiPickerData.instance.isOverLimit(selectConfig.getMaxCount())) {
            presenter.tip(getContext(), String.format(Objects.requireNonNull(getContext())
                            .getResources().getString(R.string.str_limit),
                    selectConfig.getMaxCount()));
            return;
        }
        if (isChecked) {
            MultiPickerData.instance.addImageItem(imageItem);
        } else {
            MultiPickerData.instance.removeImageItem(imageItem);
            if (selectConfig.isLastItem(imageItem)) {
                selectConfig.getLastImageList().remove(imageItem);
            }
        }
        refreshOKBtn();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                TakePhotoUtil.takePhoto(mContext, REQ_CAMERA);
            } else {
                PermissionUtils.create(mContext).showSetPermissionDialog(getString(R.string.picker_str_camerapermisson));
            }
        } else if (requestCode == REQ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPicData();
            } else {
                PermissionUtils.create(mContext).showSetPermissionDialog(getString(R.string.picker_str_storagepermisson));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
