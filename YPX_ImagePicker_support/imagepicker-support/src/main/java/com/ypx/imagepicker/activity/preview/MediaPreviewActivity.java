package com.ypx.imagepicker.activity.preview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.multi.MultiPreviewAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.PLauncher;
import com.ypx.imagepicker.helper.recyclerviewitemhelper.SimpleItemTouchHelperCallback;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.widget.SuperCheckBox;

import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_CURRENT_INDEX;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_PRESENTER;

/**
 * Description: 预览页面，其中包含编辑预览和普通预览
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 * 使用文档 ：https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档
 */
@SuppressLint("DefaultLocale")
public class MediaPreviewActivity extends FragmentActivity {
    public static final String INTENT_KEY_PREVIEW_LIST = "previewList";
    public static final String INTENT_KEY_CAN_EDIT = "canEdit";
    private ViewPager mViewPager;
    private SuperCheckBox mCbSelected;
    private ArrayList<ImageItem> mPreviewList = new ArrayList<>();
    private ArrayList<ImageItem> mImageList = new ArrayList<>();
    private int mCurrentItemPosition = 0;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ViewGroup mTitleBar;
    private RelativeLayout mBottomBar;
    private IMultiPickerBindPresenter presenter;
    private PickerUiConfig uiConfig;

    private RecyclerView mPreviewRecyclerView;
    private MultiPreviewAdapter previewAdapter;
    private boolean isCanEdit = false;

    /**
     * 跳转预览页面
     *
     * @param context     跳转的activity
     * @param presenter   IMultiPickerBindPresenter 负责提供UI展示
     * @param previewList 需要预览的图片列表
     * @param currentPos  默认选中项
     * @param listener    预览编辑完成回调，如果传null,代表普通编辑
     */
    public static void intent(Activity context,
                              IMultiPickerBindPresenter presenter,
                              final ArrayList<ImageItem> previewList,
                              int currentPos,
                              final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(context, MediaPreviewActivity.class);
        intent.putExtra(INTENT_KEY_PREVIEW_LIST, previewList);
        intent.putExtra(INTENT_KEY_PRESENTER, presenter);
        intent.putExtra(INTENT_KEY_CURRENT_INDEX, currentPos);
        intent.putExtra(INTENT_KEY_CAN_EDIT, listener != null);
        PLauncher.init(context).startActivityForResult(intent, new PLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == RESULT_OK && listener != null) {
                    ArrayList mList = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
                    listener.onImagePickComplete(mList);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isIntentDataFailed()) {
            finish();
        } else {
            setContentView(R.layout.picker_activity_image_pre);
            setUI();
            List list = (List) getIntent().getSerializableExtra(INTENT_KEY_PREVIEW_LIST);
            if (list != null && list.size() > 0) {
                mImageList = new ArrayList<ImageItem>(list);
                mPreviewList.addAll(mImageList);
                initData();
            }
        }
    }

    /**
     * @return 跳转数据是否合法
     */
    private boolean isIntentDataFailed() {
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_PRESENTER)) {
            return true;
        }
        presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_PRESENTER);
        mCurrentItemPosition = getIntent().getIntExtra(INTENT_KEY_CURRENT_INDEX, 0);
        isCanEdit = getIntent().getBooleanExtra(INTENT_KEY_CAN_EDIT, false);
        mPreviewList = new ArrayList<>();
        if (presenter == null) {
            return true;
        }
        uiConfig = presenter.getUiConfig(this);
        return uiConfig == null;
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
        if (!isCanEdit) {
            mCbSelected.setVisibility(View.GONE);
            mPreviewRecyclerView.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
            mTvRight.setVisibility(View.GONE);
        }
        ((LinearLayout) findViewById(R.id.mTitleRoot)).setGravity(uiConfig.getTitleBarGravity());
        resetRightBtn();
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PViewSizeUtils.onDoubleClick()) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, mPreviewList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkItem(isChecked);
            }
        });
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
            if (isCanEdit) {
                mBottomBar.setVisibility(View.VISIBLE);
                mPreviewRecyclerView.setVisibility(View.VISIBLE);
            }
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
        mCbSelected.setChecked(mPreviewList.contains(imageItem));
        mCbSelected.setVisibility(View.VISIBLE);
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

}
