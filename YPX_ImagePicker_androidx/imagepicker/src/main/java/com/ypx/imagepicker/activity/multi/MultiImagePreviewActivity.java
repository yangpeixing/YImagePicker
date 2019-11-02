package com.ypx.imagepicker.activity.multi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.preview.SinglePreviewFragment;
import com.ypx.imagepicker.adapter.multi.MultiPreviewAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.MediaItemsDataSource;
import com.ypx.imagepicker.helper.recyclerviewitemhelper.SimpleItemTouchHelperCallback;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PConstantsUtil;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.widget.SuperCheckBox;

import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_CURRENT_INDEX;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;

/**
 * Description: 预览页面，其中包含编辑预览和普通预览
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
@SuppressLint("DefaultLocale")
public class MultiImagePreviewActivity extends FragmentActivity {
    static ImageSet currentImageSet;
    public static final String INTENT_KEY_SELECT_LIST = "selectList";
    private ViewPager mViewPager;
    private SuperCheckBox mCbSelected;
    private ArrayList<ImageItem> mPreviewList = new ArrayList<>();
    private ArrayList<ImageItem> mImageList = new ArrayList<>();
    private int mCurrentItemPosition = 0;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ViewGroup mTitleBar;
    private RelativeLayout mBottomBar;
    private MultiSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private PickerUiConfig uiConfig;

    private RecyclerView mPreviewRecyclerView;
    private MultiPreviewAdapter previewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isIntentDataFailed()) {
            finish();
        } else {
            setContentView(R.layout.picker_activity_image_pre);
            setUI();
            loadMediaPreviewList();
        }
    }

    private void notifyCallBack(boolean isOK) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, mPreviewList);
        setResult(isOK ? RESULT_OK : RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        notifyCallBack(false);
    }

    /**
     * @return 跳转数据是否合法
     */
    private boolean isIntentDataFailed() {
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_SELECT_CONFIG)
                || !getIntent().hasExtra(INTENT_KEY_PRESENTER)) {
            return true;
        }
        selectConfig = (MultiSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_PRESENTER);
        mCurrentItemPosition = getIntent().getIntExtra(INTENT_KEY_CURRENT_INDEX, 0);
        mPreviewList = (ArrayList<ImageItem>) getIntent().getSerializableExtra(INTENT_KEY_SELECT_LIST);
        if (presenter == null) {
            return true;
        }
        uiConfig = presenter.getUiConfig(this);
        return uiConfig == null;
    }

    /**
     * 加载媒体文件夹
     */
    private void loadMediaPreviewList() {
        if (currentImageSet != null && currentImageSet.imageItems != null && currentImageSet.imageItems.size() > 0
                && currentImageSet.imageItems.size() >= currentImageSet.count) {
            mImageList = new ArrayList<>(currentImageSet.imageItems);
            initData();
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this, null, PConstantsUtil.getString(this, presenter).picker_str_loading);
            ImagePicker.provideMediaItemsFromSet(this, currentImageSet, selectConfig.getMimeTypes(), new MediaItemsDataSource.MediaItemProvider() {
                @Override
                public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
                    dialog.dismiss();
                    mImageList = new ArrayList<>(imageItems);
                    initData();
                }
            });
        }
    }

    /**
     * 初始化预览列表
     */
    private void initData() {
        initPreviewRecyclerView();
        initViewPager();
    }

    /**
     * 初始化预览列表，支持排序
     */
    private void initPreviewRecyclerView() {
        mPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        previewAdapter = new MultiPreviewAdapter(mPreviewList, presenter);
        mPreviewRecyclerView.setAdapter(previewAdapter);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(previewAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mPreviewRecyclerView);
    }

    /**
     * 初始化标题栏
     */
    private void setUI() {
        mPreviewRecyclerView = findViewById(R.id.mPreviewRecyclerView);
        mViewPager = findViewById(R.id.viewpager);
        mCbSelected = findViewById(R.id.btn_check);
        mTvTitle = findViewById(R.id.tv_title);
        mTvRight = findViewById(R.id.tv_rightBtn);
        mTitleBar = findViewById(R.id.top_bar);
        mBottomBar = findViewById(R.id.bottom_bar);
        if (uiConfig.isImmersionBar()) {
            PStatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true,
                    PStatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));
            mTitleBar.setPadding(0, PStatusBarUtil.getStatusBarHeight(this), 0, 0);
        }
        ImageView mBackImg = findViewById(R.id.iv_back);
        mBackImg.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
        mBackImg.setColorFilter(uiConfig.getBackIconColor());
        mBottomBar.setClickable(true);
        mCbSelected.setClickable(true);
        mCbSelected.setEnabled(true);
        mCbSelected.setLeftDrawable(getResources().getDrawable(uiConfig.getSelectedIconID()),
                getResources().getDrawable(uiConfig.getUnSelectIconID()));
        mCbSelected.setTextColor(uiConfig.getPreviewTextColor());
        mTitleBar.setBackgroundColor(uiConfig.getTitleBarBackgroundColor());
        mBottomBar.setBackgroundColor(uiConfig.getBottomBarBackgroundColor());
        mPreviewRecyclerView.setBackgroundColor(uiConfig.getBottomBarBackgroundColor());
        mTvTitle.setTextColor(uiConfig.getTitleColor());
        if (uiConfig.getOkBtnSelectBackground() == null && uiConfig.getOkBtnUnSelectBackground() == null) {
            mTvRight.setPadding(0, 0, 0, 0);
        }
        ((LinearLayout) findViewById(R.id.mTitleRoot)).setGravity(uiConfig.getTitleBarGravity());
        resetRightBtn();
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PViewSizeUtils.onDoubleClick()) {
                    return;
                }
                notifyCallBack(true);
            }
        });
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyCallBack(false);
            }
        });
        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageItem item = mImageList.get(mCurrentItemPosition);
                if (isItemCantClick(mPreviewList, item)) {
                    mCbSelected.setChecked(false);
                    mCbSelected.setEnabled(false);
                    return;
                }
                if (mPreviewList.size() > selectConfig.getMaxCount()) {
                    presenter.overMaxCountTip(MultiImagePreviewActivity.this, selectConfig.getMaxCount());
                    mCbSelected.setChecked(false);
                    mCbSelected.setEnabled(false);
                    return;
                }
                mCbSelected.setEnabled(true);
            }
        });
        mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkItem(isChecked);
            }
        });
    }

    protected boolean isItemCantClick(List<ImageItem> selectList, ImageItem imageItem) {
        //在屏蔽列表中
        if (selectConfig.isShieldItem(imageItem)) {
            presenter.tip(this, PConstantsUtil.getString(this, presenter).picker_str_shield);
            return true;
        }
        if (imageItem.isVideo()) {
            if (selectConfig.isSinglePickImageOrVideoType() && selectList != null && selectList.size() > 0 && selectList.get(0).isImage()) {
                presenter.tip(this, PConstantsUtil.getString(this, presenter).picker_str_only_select_image);
                return true;
            } else if (imageItem.duration > selectConfig.getMaxVideoDuration()) {
                presenter.tip(this, String.format("%s%s", PConstantsUtil.getString(this, presenter).picker_str_video_over_max_duration,
                        selectConfig.getMaxVideoDurationFormat()));
                return true;
            } else if (imageItem.duration < selectConfig.getMinVideoDuration()) {
                presenter.tip(this, String.format("%s%s", PConstantsUtil.getString(this, presenter).picker_str_video_less_min_duration,
                        selectConfig.getMinVideoDurationFormat()));
                return true;
            }
        } else {
            if (selectConfig.isSinglePickImageOrVideoType() && selectList != null && selectList.size() > 0 && selectList.get(0).isVideo()) {
                presenter.tip(this, PConstantsUtil.getString(this, presenter).picker_str_only_select_video);
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        TouchImageAdapter mAdapter = new TouchImageAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);
        ImageItem item = mImageList.get(mCurrentItemPosition);
        onImagePageSelected(mCurrentItemPosition, item);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                ImageItem item = mImageList.get(mCurrentItemPosition);
                onImagePageSelected(mCurrentItemPosition, item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 预览列表点击
     *
     * @param imageItem 当前图片
     */
    public void onPreviewItemClick(ImageItem imageItem) {
        mViewPager.setCurrentItem(mImageList.indexOf(imageItem), false);
    }

    /**
     * @param isCheck item选中操作
     */
    public void checkItem(boolean isCheck) {
        ImageItem item = mImageList.get(mCurrentItemPosition);
        if (isCheck) {
            if (!mPreviewList.contains(item)) {
                mPreviewList.add(item);
            }
        } else {
            mPreviewList.remove(item);
        }
        resetRightBtn();
        notifyPreviewList(item);
    }

    /**
     * 单击图片
     */
    public void onImageSingleTap() {
        if (mTitleBar.getVisibility() == View.VISIBLE) {
            mTitleBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.picker_top_out));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.picker_fade_out));
            mPreviewRecyclerView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.picker_fade_out));
            mTitleBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
            mPreviewRecyclerView.setVisibility(View.GONE);
        } else {
            mTitleBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.picker_top_in));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.picker_fade_in));
            mPreviewRecyclerView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.picker_fade_in));
            mTitleBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
            mPreviewRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * viewpager滑动回调
     *
     * @param position  当前预览图片索引
     * @param imageItem 当前图片
     */
    public void onImagePageSelected(int position, ImageItem imageItem) {
        mTvTitle.setText(String.format("%d/%d", position + 1, mImageList.size()));
        mCbSelected.setEnabled(true);
        mCbSelected.setChecked(mPreviewList.contains(imageItem));
        notifyPreviewList(imageItem);
        resetRightBtn();
    }

    /**
     * 刷新预览编辑列表
     *
     * @param imageItem 当前预览的图片
     */
    private void notifyPreviewList(ImageItem imageItem) {
        previewAdapter.setPreviewImageItem(imageItem);
        if (mPreviewList.contains(imageItem)) {
            mPreviewRecyclerView.smoothScrollToPosition(mPreviewList.indexOf(imageItem));
        }
    }

    /**
     * 刷新右上角完成按钮
     */
    private void resetRightBtn() {
        if (mPreviewList != null && mPreviewList.size() > 0) {
            mTvRight.setEnabled(true);
            mTvRight.setBackground(uiConfig.getOkBtnSelectBackground());
            mTvRight.setTextColor(uiConfig.getOkBtnSelectTextColor());
        } else {
            mTvRight.setEnabled(false);
            mTvRight.setBackground(uiConfig.getOkBtnUnSelectBackground());
            mTvRight.setTextColor(uiConfig.getOkBtnUnSelectTextColor());
        }
        if (selectConfig == null) {
            return;
        }
        if (selectConfig.getMaxCount() < 0 || mPreviewList == null || mPreviewList.size() <= 0) {
            mTvRight.setText(uiConfig.getOkBtnText());
        } else {
            String text = String.format("%s(%d/%d)", uiConfig.getOkBtnText(),
                    mPreviewList.size(),
                    selectConfig.getMaxCount());
            mTvRight.setText(text);
        }
    }

    public IMultiPickerBindPresenter getImgLoader() {
        return presenter;
    }

    class TouchImageAdapter extends FragmentStatePagerAdapter {
        TouchImageAdapter(FragmentManager fm) {
            super(fm);
            if (mImageList == null) {
                mImageList = new ArrayList<>();
            }
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            SinglePreviewFragment fragment = new SinglePreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SinglePreviewFragment.KEY_URL, mImageList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentImageSet.imageItems.clear();
        currentImageSet = null;
    }
}
