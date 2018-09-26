package com.ypx.imagepicker.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.config.ImagePickerConfig;
import com.ypx.imagepicker.data.ImagePickerData;
import com.ypx.imagepicker.interf.ImgLoader;
import com.ypx.imagepicker.utils.StatusBarUtils;
import com.ypx.imagepicker.widget.SuperCheckBox;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class YPXImagePreviewActivity2 extends FragmentActivity {
    ViewPager mViewPager;
    SuperCheckBox mCbSelected;
    List<ImageItem> mImageList;
    ImagePickerConfig pickerConfig;
    ImgLoader imgLoader;
    TextView tv_title_count;
    TextView btn_ok;
    private int mCurrentItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_image_pre);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        pickerConfig = (ImagePickerConfig) getIntent().getSerializableExtra("ImagePickerConfig");
        mCurrentItemPosition = getIntent().getIntExtra("selectIndex", 0);
        imgLoader = pickerConfig.getImgLoader();
        mCbSelected = (SuperCheckBox) findViewById(R.id.btn_check);
        mCbSelected.setLeftDrawable(getResources().getDrawable(pickerConfig.getSelectIcon()),
                getResources().getDrawable(pickerConfig.getUnSelectIcon()));
        mImageList = ImagePickerData.getCurrentImageSet().imageItems;
        if (mImageList == null || mImageList.size() == 0) {
            finish();
            return;
        }
        initTitleBar();
        initViewPager();

        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImagePickerData.isOverLimit(pickerConfig.getSelectLimit())) {
                    if (mCbSelected.isChecked()) {
                        mCbSelected.toggle();
                        String toast = getResources().getString(R.string.you_have_a_select_limit, pickerConfig.getSelectLimit() + "");
                        Toast.makeText(YPXImagePreviewActivity2.this, toast, Toast.LENGTH_SHORT).show();
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
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        ImageView btn_backpress = (ImageView) findViewById(R.id.btn_backpress);
        tv_title_count = (TextView) findViewById(R.id.tv_title_count);
        btn_backpress.setColorFilter(Color.WHITE);
        if (pickerConfig.isImmersionBar()) {
            StatusBarUtils.setWindowStatusBarColor(this, Color.parseColor("#303030"));
        }
        btn_ok.setTextColor(pickerConfig.getThemeColor());
        resetBtnOKstate();
        btn_ok.setOnClickListener(new View.OnClickListener() {
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
        boolean isSelected = ImagePickerData.hasItem(item);
        onImagePageSelected(mCurrentItemPosition, mImageList.get(mCurrentItemPosition), isSelected);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCurrentItemPosition = position;
                ImageItem item = mImageList.get(mCurrentItemPosition);
                onImagePageSelected(mCurrentItemPosition, item, ImagePickerData.hasItem(item));
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
        boolean isSelect = ImagePickerData.hasItem(item);
        if (isCheck) {
            if (!isSelect) {
                ImagePickerData.addImageItem(item);
            }
        } else {
            if (isSelect) {
                ImagePickerData.removeImageItem(item);
            }
        }

        resetBtnOKstate();

    }


    public void onImageSingleTap() {
        View topBar = findViewById(R.id.top_bar);
        View bottomBar = findViewById(R.id.bottom_bar);
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity2.this, R.anim.ypx_top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity2.this, R.anim.ypx_fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity2.this, R.anim.ypx_top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(YPXImagePreviewActivity2.this, R.anim.ypx_fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }

    }

    public void onImagePageSelected(int position, ImageItem item, boolean isSelected) {
        tv_title_count.setText(String.format("%d/%d", position + 1, mImageList.size()));
        mCbSelected.setChecked(isSelected);
    }

    private void resetBtnOKstate() {
        if (ImagePickerData.getSelectImgs().size() > 0) {
            btn_ok.setClickable(true);
            btn_ok.setEnabled(true);
            btn_ok.setAlpha(0.6f);
            btn_ok.setText(getResources().getString(R.string.select_complete,
                    new Object[]{ImagePickerData.getSelectImgs().size(), pickerConfig.getSelectLimit()}));
        } else {
            btn_ok.setAlpha(0.4f);
            btn_ok.setText(getResources().getString(R.string.complete));
            btn_ok.setClickable(false);
            btn_ok.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public ImgLoader getImgLoader() {
        return imgLoader;
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
                    ((YPXImagePreviewActivity2) getActivity()).onImageSingleTap();
                }
            });
            if (getActivity() instanceof YPXImagePreviewActivity2) {
                ((YPXImagePreviewActivity2) getActivity()).getImgLoader().onPresentImageDetail(imageView, url);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return imageView;
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
}
