package com.ypx.imagepicker.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.YPXImagePicker;
import com.ypx.imagepicker.YPXImagePickerUiBuilder;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.interf.ImageSelectMode;
import com.ypx.imagepicker.interf.ImgLoader;
import com.ypx.imagepicker.interf.OnImageSelectedChangeListener;
import com.ypx.imagepicker.ui.view.DefaultTitleBar;
import com.ypx.imagepicker.utils.CornerUtils;
import com.ypx.imagepicker.widget.SuperCheckBox;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

import java.util.List;

@SuppressLint("DefaultLocale")
public class YPXImagePreviewActivity extends FragmentActivity implements
        OnImageSelectedChangeListener {
    private static final String TAG = YPXImagePreviewActivity.class.getSimpleName();
    public ImgLoader imgLoader;
    ViewPager mViewPager;
    TextView tv_title;
    SuperCheckBox mCbSelected;
    TextView tv_complete;
    List<ImageItem> mImageList;
    YPXImagePicker androidImagePicker;
    private int mCurrentItemPosition = 0;
    private YPXImagePickerUiBuilder uiBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_image_pre);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        androidImagePicker = YPXImagePicker.getInstance();
        androidImagePicker.addOnImageSelectedChangeListener(this);
        imgLoader = androidImagePicker.getImgLoader();
        uiBuilder = androidImagePicker.getUiBuilder();
        if (uiBuilder == null) {
            uiBuilder = new YPXImagePickerUiBuilder(this);
        }
        mImageList = YPXImagePicker.getInstance().getImageItemsOfCurrentImageSet();
        mCurrentItemPosition = getIntent().getIntExtra("key_pic_selected", 0);
        mCbSelected = (SuperCheckBox) findViewById(R.id.btn_check);
        mCbSelected.setLeftDrawable(uiBuilder.getSelectIcon(), uiBuilder.getUnSelectIcon());

        initTitleBar();
        initViewPager();

        int selectedCount = androidImagePicker.getSelectImageCount();
        onImageSelectChange(0, null, selectedCount, androidImagePicker.getSelectLimit());
        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (androidImagePicker.getSelectImageCount() > androidImagePicker.getSelectLimit()) {
                    if (mCbSelected.isChecked()) {
                        //holder.cbSelected.setCanChecked(false);
                        mCbSelected.toggle();
                        @SuppressLint("StringFormatMatches")
                        String toast = getResources().getString(R.string.you_have_a_select_limit, androidImagePicker.getSelectLimit());
                        Toast.makeText(YPXImagePreviewActivity.this, toast, Toast.LENGTH_SHORT).show();
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
    private void initTitleBar() {
        RelativeLayout top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        top_bar.removeAllViews();
        DefaultTitleBar titleBar = new DefaultTitleBar(this);
        top_bar.addView(titleBar);
        tv_complete = titleBar.getCompleteTextView();
        ImageView iv_back = titleBar.getLeftIconImageView();
        tv_title = titleBar.getTitleTextView();
        tv_complete.setBackground(CornerUtils.halfAlphaSelector(titleBar.dp(3), uiBuilder.getThemeColor()));
        iv_back.setColorFilter(uiBuilder.getThemeColor());
        tv_complete.setText("完成");
        tv_title.setText(String.format("1/%d", mImageList.size()));
        tv_complete.setAlpha(0.5f);
        tv_complete.setClickable(false);
        tv_complete.setEnabled(false);
        if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_SINGLE
                || androidImagePicker.getSelectMode() == ImageSelectMode.MODE_CROP) {
            tv_complete.setVisibility(View.GONE);
        } else {
            tv_complete.setVisibility(View.VISIBLE);
        }
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViewPager() {
        TouchImageAdapter mAdapter = new TouchImageAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);
        ImageItem item = mImageList.get(mCurrentItemPosition);

        boolean isSelected = false;
        if (androidImagePicker.isSelect(mCurrentItemPosition, item)) {
            isSelected = true;
        }
        onImagePageSelected(mCurrentItemPosition, mImageList.get(mCurrentItemPosition), isSelected);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                boolean isSelected = false;
                ImageItem item = mImageList.get(mCurrentItemPosition);
                if (androidImagePicker.isSelect(position, item)) {
                    isSelected = true;
                }
                onImagePageSelected(mCurrentItemPosition, item, isSelected);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

    }

    /**
     * public method:select the current show image
     */
    public void selectCurrent(boolean isCheck) {
        ImageItem item = mImageList.get(mCurrentItemPosition);
        boolean isSelect = androidImagePicker.isSelect(mCurrentItemPosition, item);
        if (isCheck) {
            if (!isSelect) {
                androidImagePicker.addSelectedImageItem(mCurrentItemPosition, item);
            }
        } else {
            if (isSelect) {
                androidImagePicker.deleteSelectedImageItem(mCurrentItemPosition, item);
            }
        }

    }


    public void onImageSingleTap() {
        View topBar = findViewById(R.id.top_bar);
        View bottomBar = findViewById(R.id.bottom_bar);
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity.this, R.anim.ypx_top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity.this, R.anim.ypx_fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity.this, R.anim.ypx_top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity.this, R.anim.ypx_fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }

    }

    public void onImagePageSelected(int position, ImageItem item, boolean isSelected) {
        tv_title.setText(String.format("%d/%d", position + 1, mImageList.size()));
        mCbSelected.setChecked(isSelected);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if (selectedItemsCount > 0) {
            tv_complete.setEnabled(true);
            tv_complete.setAlpha(1f);
            tv_complete.setClickable(true);
            tv_complete.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            tv_complete.setAlpha(0.5f);
            tv_complete.setClickable(false);
            tv_complete.setText(getResources().getString(R.string.complete));
            tv_complete.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        androidImagePicker.removeOnImageItemSelectedChangeListener(this);
        super.onDestroy();
    }


    @SuppressLint("ValidFragment")
    public static class SinglePreviewFragment extends Fragment {
        public static final String KEY_URL = "key_url";
        private PicBrowseImageView imageView;
        private String url;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle bundle = getArguments();
            ImageItem imageItem = (ImageItem) bundle.getSerializable(KEY_URL);
            if (imageItem == null) {
                return;
            }
            url = imageItem.path;
            imageView = new PicBrowseImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setBackgroundColor(0xff000000);
            // 启用图片缩放功能
            imageView.enable();
            imageView.setMaxScale(5.0f);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((YPXImagePreviewActivity) getActivity()).onImageSingleTap();
                }
            });
          //  YPXImagePicker.getInstance().getImgLoader().onPresentImage(imageView, url, 0);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return imageView;
        }

    }

    class TouchImageAdapter extends FragmentStatePagerAdapter {
        TouchImageAdapter(FragmentManager fm) {
            super(fm);
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
}
