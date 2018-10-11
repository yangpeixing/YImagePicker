package com.ypx.imagepicker.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
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
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.config.IImgPickerUIConfig;
import com.ypx.imagepicker.config.ImgPickerSelectConfig;
import com.ypx.imagepicker.data.ImagePickerData;
import com.ypx.imagepicker.utils.StatusBarUtils;
import com.ypx.imagepicker.widget.SuperCheckBox;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class YPXImagePreviewActivity extends FragmentActivity {
    ViewPager mViewPager;
    SuperCheckBox mCbSelected;
    List<ImageItem> mImageList;
    private int mCurrentItemPosition = 0;

    private RelativeLayout top_bar, footer_panel;
    private TextView tv_title;
    private TextView tv_rightBtn;
    private ImageView iv_back;


    private ImgPickerSelectConfig selectConfig;
    private IImgPickerUIConfig uiConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_image_pre);
        dealIntentData();
        initView();
        if (mImageList == null || mImageList.size() == 0) {
            finish();
            return;
        }
        initTitleBar();
        initViewPager();
        setListener();
    }

    private void setListener() {
        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImagePickerData.isOverLimit(selectConfig.getSelectLimit())) {
                    if (mCbSelected.isChecked()) {
                        mCbSelected.toggle();
                        String toast = getResources().getString(R.string.you_have_a_select_limit, selectConfig.getSelectLimit() + "");
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

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mCbSelected = (SuperCheckBox) findViewById(R.id.btn_check);
        mCbSelected.setLeftDrawable(getResources().getDrawable(uiConfig.getSelectedIconID()),
                getResources().getDrawable(uiConfig.getUnSelectIconID()));
    }

    /**
     * 接收传参
     */
    private void dealIntentData() {
        selectConfig = (ImgPickerSelectConfig) getIntent().getSerializableExtra("ImgPickerSelectConfig");
        uiConfig = (IImgPickerUIConfig) getIntent().getSerializableExtra("IImgPickerUIConfig");
        mCurrentItemPosition = getIntent().getIntExtra("selectIndex", 0);
        // mImageList = ImagePickerData.getCurrentImageSet().imageItems;
        mImageList = ((ImageSet) (getIntent().getSerializableExtra("ImageSet"))).imageItems;
    }


    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        footer_panel = (RelativeLayout) findViewById(R.id.bottom_bar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        if (uiConfig.isImmersionBar() && uiConfig.getTopBarBackgroundColor() != 0) {
            StatusBarUtils.setWindowStatusBarColor(this, uiConfig.getTopBarBackgroundColor());
        }
        if (uiConfig.getBackIconID() != 0) {
            iv_back.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
            // iv_back.setColorFilter(Color.WHITE);
        }

        if (uiConfig.getTopBarBackgroundColor() != 0) {
            top_bar.setBackgroundColor(uiConfig.getTopBarBackgroundColor());
        }

        if (uiConfig.getBottomBarBackgroundColor() != 0) {
            footer_panel.setBackgroundColor(uiConfig.getBottomBarBackgroundColor());
        }

        if (uiConfig.getRightBtnBackground() != null) {
            tv_rightBtn.setBackground(uiConfig.getRightBtnBackground());
        }

        if (uiConfig.getTitleColor() != 0) {
            tv_title.setTextColor(uiConfig.getTitleColor());
        }

        tv_title.setGravity(Gravity.CENTER | uiConfig.getTopBarTitleGravity());
        tv_rightBtn.setTextColor(uiConfig.getThemeColor());
        resetBtnOKstate();
        tv_rightBtn.setOnClickListener(new View.OnClickListener() {
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

    private void resetBtnOKstate() {
        if (ImagePickerData.getSelectImgs().size() > 0) {
            tv_rightBtn.setClickable(true);
            tv_rightBtn.setEnabled(true);
            tv_rightBtn.setAlpha(1f);
            tv_rightBtn.setText(getResources().getString(R.string.select_complete,
                    new Object[]{ImagePickerData.getSelectImgs().size(), selectConfig.getSelectLimit()}));
        } else {
            tv_rightBtn.setAlpha(0.6f);
            tv_rightBtn.setText(getResources().getString(R.string.complete));
            tv_rightBtn.setClickable(false);
            tv_rightBtn.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public IImgPickerUIConfig getImgLoader() {
        return uiConfig;
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
            imageView.setMaxScale(7.0f);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((YPXImagePreviewActivity) getActivity()).onImageSingleTap();
                }
            });
            if (getActivity() instanceof YPXImagePreviewActivity) {
                ((YPXImagePreviewActivity) getActivity()).getImgLoader().displayPerViewImage(imageView, url);
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
