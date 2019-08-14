package com.ypx.imagepicker.activity.multi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.multi.MultiPreviewAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.PickerFileProvider;
import com.ypx.imagepicker.helper.launcher.ActivityLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.StatusBarUtil;
import com.ypx.imagepicker.utils.ViewSizeUtils;
import com.ypx.imagepicker.widget.SuperCheckBox;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_CURRENT_INDEX;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG;
import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.INTENT_KEY_UI_CONFIG;

public class MultiImagePreviewActivity extends FragmentActivity {
    public static final String INTENT_KEY_PREVIEW_LIST = "previewList";
    public static final String INTENT_KEY_CAN_EDIT = "canEdit";
    private ViewPager mViewPager;
    private SuperCheckBox mCbSelected;
    private ArrayList<ImageItem> mPreviewList;
    private ArrayList<ImageItem> mImageList;
    private int mCurrentItemPosition = 0;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ViewGroup mTitleBar;
    private RelativeLayout mBottomBar;
    private PickerSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private PickerUiConfig uiConfig;

    private RecyclerView mPreviewRecyclerView;
    private MultiPreviewAdapter previewAdapter;
    private boolean isCanEdit = false;

    public static void preview(Activity context,
                               PickerSelectConfig selectConfig,
                               IMultiPickerBindPresenter presenter,
                               final boolean isPickerJump,
                               final ArrayList<ImageItem> previewList,
                               int currentPos,
                               final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(context, MultiImagePreviewActivity.class);
        if (previewList != null) {
            intent.putExtra(INTENT_KEY_PREVIEW_LIST, previewList);
        }
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, selectConfig);
        intent.putExtra(INTENT_KEY_UI_CONFIG, presenter);
        intent.putExtra(INTENT_KEY_CURRENT_INDEX, currentPos);
        intent.putExtra(INTENT_KEY_CAN_EDIT, listener != null);
        ActivityLauncher.init(context).startActivityForResult(intent, new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == RESULT_OK && listener != null) {
                    listener.onImagePickComplete(MultiPickerData.instance.getSelectImageList());
                    if (!isPickerJump) {
                        MultiPickerData.instance.clear();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_image_pre);
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_SELECT_CONFIG)
                || !getIntent().hasExtra(INTENT_KEY_UI_CONFIG)) {
            finish();
            return;
        }
        selectConfig = (PickerSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_UI_CONFIG);
        mCurrentItemPosition = getIntent().getIntExtra(INTENT_KEY_CURRENT_INDEX, 0);
        isCanEdit = getIntent().getBooleanExtra(INTENT_KEY_CAN_EDIT, false);
        mPreviewList = new ArrayList<>();
        mPreviewList.clear();
        if (getIntent().hasExtra(INTENT_KEY_PREVIEW_LIST)) {
            mImageList = (ArrayList<ImageItem>) getIntent().getSerializableExtra(INTENT_KEY_PREVIEW_LIST);
            mPreviewList.addAll(mImageList);
        } else {
            mImageList = MultiPickerData.instance.getCurrentImageSet().imageItems;
            mPreviewList.addAll(MultiPickerData.instance.getSelectImageList());
        }
        for (ImageItem imageItem : mPreviewList) {
            imageItem.setSelect(false);
        }
        if (mImageList == null || mImageList.size() == 0
                || selectConfig == null || presenter == null) {
            finish();
            return;
        }
        uiConfig = presenter.getUiConfig(this);
        initUI();
        initPreviewRecyclerView();
        initViewPager();
        setListener();
    }

    @Override
    public void finish() {
        if (mPreviewList != null) {
            MultiPickerData.instance.setSelectImageList(mPreviewList);
        }
        super.finish();
    }

    private void initPreviewRecyclerView() {
        mPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        previewAdapter = new MultiPreviewAdapter(mPreviewList, presenter);
        mPreviewRecyclerView.setAdapter(previewAdapter);
    }

    private void setListener() {
        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPreviewList.size() > selectConfig.getMaxCount() && selectConfig.getMaxCount() > 0) {
                    if (mCbSelected.isChecked()) {
                        mCbSelected.toggle();
                        String toast = getResources().getString(R.string.you_have_a_select_limit, selectConfig.getMaxCount() + "");
                        Toast.makeText(MultiImagePreviewActivity.this, toast, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectCurrent(isChecked);
            }
        });

    }

    /**
     * 初始化标题栏
     */
    private void initUI() {
        mPreviewRecyclerView = findViewById(R.id.mPreviewRecyclerView);
        mViewPager = findViewById(R.id.viewpager);
        mCbSelected = findViewById(R.id.btn_check);
        mTvTitle = findViewById(R.id.tv_title);
        mTvRight = findViewById(R.id.tv_rightBtn);
        mTitleBar = findViewById(R.id.top_bar);
        mBottomBar = findViewById(R.id.bottom_bar);

        if (uiConfig.isImmersionBar()) {
            StatusBarUtil.setStatusBar(this, Color.TRANSPARENT, true,
                    StatusBarUtil.isDarkColor(uiConfig.getTitleBarBackgroundColor()));

            mTitleBar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        }

        ImageView iv_back = findViewById(R.id.iv_back);
        mBottomBar.setClickable(true);
        mCbSelected.setLeftDrawable(getResources().getDrawable(uiConfig.getSelectedIconID()),
                getResources().getDrawable(uiConfig.getUnSelectIconID()));
        iv_back.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
        iv_back.setColorFilter(uiConfig.getBackIconColor());
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

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((LinearLayout) findViewById(R.id.mTitleRoot)).setGravity(uiConfig.getTitleBarGravity());
        resetBtnOKBtn();
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void initViewPager() {
        TouchImageAdapter mAdapter = new TouchImageAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);
        ImageItem item = mImageList.get(mCurrentItemPosition);
        onImagePageSelected(mCurrentItemPosition, hasItem(item));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                ImageItem item = mImageList.get(mCurrentItemPosition);
                onImagePageSelected(mCurrentItemPosition, hasItem(item));
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
        int i = 0;
        for (ImageItem item : mImageList) {
            if (item.equals(imageItem)) {
                mViewPager.setCurrentItem(i, false);
                notifyPreviewList(imageItem);
                return;
            }
            i++;
        }
    }

    public void selectCurrent(boolean isCheck) {
        ImageItem item = mImageList.get(mCurrentItemPosition);
        if (isCheck) {
            if (!hasItem(item)) {
                mPreviewList.add(item);
            }
        } else {
            if (hasItem(item)) {
                mPreviewList.remove(item);
            }
        }

        resetBtnOKBtn();
        notifyPreviewList(item);
    }


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

    @SuppressLint("DefaultLocale")
    public void onImagePageSelected(int position, boolean isSelected) {
        mTvTitle.setText(String.format("%d/%d", position + 1, mImageList.size()));
        mCbSelected.setChecked(isSelected);
        mCbSelected.setVisibility(View.VISIBLE);
        ImageItem imageItem = mImageList.get(position);
        notifyPreviewList(imageItem);
        resetBtnOKBtn();
        if (imageItem.duration > ImagePicker.MAX_VIDEO_DURATION) {
            mCbSelected.setVisibility(View.GONE);
            return;
        }
        if (selectConfig.isVideoSinglePick() && imageItem.isVideo()) {
            mCbSelected.setVisibility(View.GONE);
            if (mPreviewList.size() == 0) {
                mTvRight.setEnabled(true);
                mTvRight.setBackground(uiConfig.getOkBtnSelectBackground());
                mTvRight.setTextColor(uiConfig.getOkBtnSelectTextColor());
                mTvRight.setText(uiConfig.getOkBtnText());
            }
        } else {
            //如果图片和视频只能选择一种并且已选中列表中第一个item属性与当前图片或视频不符合，则隐藏底部选择框
            if (selectConfig.isSinglePickImageOrVideoType() && mPreviewList.size() > 0) {
                if (mPreviewList.get(0).isVideo() != imageItem.isVideo()) {
                    mCbSelected.setVisibility(View.GONE);
                } else {
                    mCbSelected.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void notifyPreviewList(ImageItem imageItem) {
        for (ImageItem mItem : mPreviewList) {
            if (mItem.equals(imageItem)) {
                mItem.setSelect(true);
                mPreviewRecyclerView.scrollToPosition(mPreviewList.indexOf(mItem));
            } else {
                mItem.setSelect(false);
            }
        }
        previewAdapter.notifyDataSetChanged();
    }

    @SuppressLint("DefaultLocale")
    private void resetBtnOKBtn() {
        if (mPreviewList != null && mPreviewList.size() > 0) {
            mTvRight.setEnabled(true);
            mTvRight.setBackground(uiConfig.getOkBtnSelectBackground());
            mTvRight.setTextColor(uiConfig.getOkBtnSelectTextColor());
        } else {
            mTvRight.setEnabled(false);
            mTvRight.setBackground(uiConfig.getOkBtnUnSelectBackground());
            mTvRight.setTextColor(uiConfig.getOkBtnUnSelectTextColor());
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

    public static class SinglePreviewFragment extends Fragment {
        static final String KEY_URL = "key_url";
        private RelativeLayout layout;
        private String url;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            if (bundle == null) {
                return;
            }
            final ImageItem imageItem = (ImageItem) bundle.getSerializable(KEY_URL);
            if (imageItem == null) {
                return;
            }
            url = imageItem.path;
            layout = new RelativeLayout(getContext());
            PicBrowseImageView imageView = new PicBrowseImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setBackgroundColor(0xff000000);
            // 启用图片缩放功能
            imageView.enable();
            imageView.setShowLine(false);
            imageView.setMaxScale(7.0f);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            layout.setLayoutParams(params);
            layout.addView(imageView);

            ImageView mVideoImg = new ImageView(getContext());
            mVideoImg.setImageDrawable(getResources().getDrawable(R.mipmap.picker_icon_video));
            RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewSizeUtils.dp(getContext(), 80), ViewSizeUtils.dp(getContext(), 80));
            mVideoImg.setLayoutParams(params1);
            params1.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(mVideoImg, params1);

            if (imageItem.isVideo()) {
                mVideoImg.setVisibility(View.VISIBLE);
            } else {
                mVideoImg.setVisibility(View.GONE);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageItem.isVideo()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File file = new File(imageItem.path);
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= 24) {
                            uri = PickerFileProvider.getUriForFile(v.getContext(),
                                    Objects.requireNonNull(getActivity())
                                            .getApplication().getPackageName() + ".picker.fileprovider", file);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            uri = Uri.fromFile(file);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        intent.setDataAndType(uri, "video/*");
                        startActivity(intent);
                        return;
                    }
                    if (getActivity() != null) {
                        ((MultiImagePreviewActivity) getActivity()).onImageSingleTap();
                    }
                }
            });
            if (getActivity() instanceof MultiImagePreviewActivity) {
                ((MultiImagePreviewActivity) getActivity()).getImgLoader().displayPerViewImage(imageView, url);
            }
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return layout;
        }

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

        @Override
        public Fragment getItem(int position) {
            SinglePreviewFragment fragment = new SinglePreviewFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SinglePreviewFragment.KEY_URL, mImageList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    public boolean hasItem(ImageItem imageItem) {
        if (imageItem == null || imageItem.path == null || mPreviewList == null) {
            return false;
        }
        for (ImageItem item : mPreviewList) {
            if (item.path != null && item.path.equals(imageItem.path)) {
                return true;
            }
        }
        return false;
    }
}
