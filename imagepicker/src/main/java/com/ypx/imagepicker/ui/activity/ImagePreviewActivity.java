package com.ypx.imagepicker.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.AndroidImagePicker;
import com.ypx.imagepicker.GlideImgLoader;
import com.ypx.imagepicker.bean.ImageItem;

import java.util.List;

@SuppressLint("DefaultLocale")
public class ImagePreviewActivity extends FragmentActivity implements View.OnClickListener
        , AndroidImagePicker.OnImageSelectedChangeListener {
    private static final String TAG = ImagePreviewActivity.class.getSimpleName();
    ViewPager mViewPager;
    TextView mTitleCount;
    CheckBox mCbSelected;
    TextView mBtnOk;

    List<ImageItem> mImageList;
    int mShowItemPosition = 0;
    AndroidImagePicker androidImagePicker;

    private int mCurrentItemPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        setContentView(R.layout.ipk_activity_image_pre);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        androidImagePicker = AndroidImagePicker.getInstance();
        androidImagePicker.addOnImageSelectedChangeListener(this);

        mImageList = AndroidImagePicker.getInstance().getImageItemsOfCurrentImageSet();
        mShowItemPosition = getIntent().getIntExtra(AndroidImagePicker.KEY_PIC_SELECTED_POSITION, 0);

        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);

        mCbSelected = (CheckBox) findViewById(R.id.btn_check);
        mTitleCount = (TextView) findViewById(R.id.tv_title_count);
        mTitleCount.setText(String.format("1/%d", mImageList.size()));

        int selectedCount = AndroidImagePicker.getInstance().getSelectImageCount();

        onImageSelectChange(0, null, selectedCount, androidImagePicker.getSelectLimit());

        //back press
        findViewById(R.id.btn_backpress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (androidImagePicker.getSelectImageCount() > androidImagePicker.getSelectLimit()) {
                    if (mCbSelected.isChecked()) {
                        //holder.cbSelected.setCanChecked(false);
                        mCbSelected.toggle();
                        @SuppressLint("StringFormatMatches")
                        String toast = getResources().getString(R.string.you_have_a_select_limit, androidImagePicker.getSelectLimit());
                        Toast.makeText(ImagePreviewActivity.this, toast, Toast.LENGTH_SHORT).show();
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
        initViewPager();
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
                AndroidImagePicker.getInstance().addSelectedImageItem(mCurrentItemPosition, item);
            }
        } else {
            if (isSelect) {
                AndroidImagePicker.getInstance().deleteSelectedImageItem(mCurrentItemPosition, item);
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            setResult(RESULT_OK);// select complete
            finish();
        } else if (v.getId() == R.id.btn_pic_rechoose) {
            finish();
        }
    }


    public void onImageSingleTap() {
        View topBar = findViewById(R.id.top_bar);
        View bottomBar = findViewById(R.id.bottom_bar);
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }

    }

    public void onImagePageSelected(int position, ImageItem item, boolean isSelected) {
        mTitleCount.setText(String.format("%d/%d", position + 1, mImageList.size()));
        mCbSelected.setChecked(isSelected);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if (selectedItemsCount > 0) {
            mBtnOk.setEnabled(true);
            mBtnOk.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            mBtnOk.setText(getResources().getString(R.string.complete));
            mBtnOk.setEnabled(false);
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
        private ImageView imageView;
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
            Log.i(TAG, "=====current show image path:" + url);
            imageView = new ImageView(getActivity());
            imageView.setBackgroundColor(0xff000000);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ImagePreviewActivity) getActivity()).onImageSingleTap();
                }
            });
            new GlideImgLoader().onPresentImage(imageView, url, 0);
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
