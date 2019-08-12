package com.ypx.imagepickerdemo;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepickerdemo.style.CustomImgPickerPresenter;
import com.ypx.imagepickerdemo.style.RedBookCropPresenter;
import com.ypx.imagepickerdemo.style.WXImgPickerPresenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<ImageItem> picList = new ArrayList<>();

    int maxNum = 16;
    private RadioButton mRbRedBook;
    private RadioButton mRbWeChat;
    private RadioButton mRbCustom;
    private RadioButton mRbAll;
    private RadioButton mRbImageOnly;
    private RadioButton mRbVideoOnly;
    private CheckBox mCbShowCamera;
    private CheckBox mCbShowGif;
    private CheckBox mCbClosePreview;
    private CheckBox mCbVideoSingle;
    private CheckBox mCbImageOrVideoMix;
    private RadioButton mRbNew;
    private RadioButton mRbShield;
    private RadioButton mRbSave;
    private Button mBtnMultiSelect;
    private Button mBtnSingleSelect;
    private Button mBtnCropSelect;
    private Button mBtnTakePhoto;
    private GridLayout mGridLayout;
    private RadioGroup mRgStyle;
    private RadioGroup mRgType;
    private TextView mTvSelectListTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //注册媒体文件观察者，可放入Application或首页中
        ImagePicker.registerMediaObserver(getApplication());
        //预加载选择器，需要APP先申请存储权限，否则无效
        //设置预加载后，可实现快速打开选择器（毫秒级加载千张大图）
        ImagePicker.preload(this, true, true, false);
    }

    private void initView() {
        mRgStyle = findViewById(R.id.rg_style);
        mRgType = findViewById(R.id.rg_type);
        mRbRedBook = findViewById(R.id.rb_redBook);
        mRbWeChat = findViewById(R.id.rb_weChat);
        mRbCustom = findViewById(R.id.rb_Custom);
        mRbAll = findViewById(R.id.rb_all);
        mRbImageOnly = findViewById(R.id.rb_imageOnly);
        mRbVideoOnly = findViewById(R.id.rb_VideoOnly);
        mCbShowCamera = findViewById(R.id.cb_showCamera);
        mCbShowGif = findViewById(R.id.cb_showGif);
        mCbClosePreview = findViewById(R.id.cb_closePreview);
        mCbVideoSingle = findViewById(R.id.cb_videoSingle);
        mCbImageOrVideoMix = findViewById(R.id.cb_imageOrVideoMix);
        mRbNew = findViewById(R.id.rb_new);
        mRbShield = findViewById(R.id.rb_shield);
        mRbSave = findViewById(R.id.rb_save);
        mBtnMultiSelect = findViewById(R.id.btn_multiSelect);
        mBtnSingleSelect = findViewById(R.id.btn_singleSelect);
        mBtnCropSelect = findViewById(R.id.btn_cropSelect);
        mBtnTakePhoto = findViewById(R.id.btn_takePhoto);
        mGridLayout = findViewById(R.id.gridLayout);
        mTvSelectListTitle = findViewById(R.id.mTvSelectListTitle);

        mBtnMultiSelect.setOnClickListener(this);
        mBtnSingleSelect.setOnClickListener(this);
        mBtnCropSelect.setOnClickListener(this);
        mBtnTakePhoto.setOnClickListener(this);


        mRgStyle.setOnCheckedChangeListener(listener);
        mRgType.setOnCheckedChangeListener(listener);

        mRbWeChat.setChecked(true);
        mRbAll.setChecked(true);
        mRbNew.setChecked(true);

    }

    RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            mCbVideoSingle.setEnabled(true);
            mCbImageOrVideoMix.setEnabled(true);
            if (checkedId == mRbRedBook.getId()) {
                mCbShowGif.setVisibility(View.GONE);
                mCbClosePreview.setVisibility(View.GONE);
                mCbVideoSingle.setVisibility(View.GONE);
                mCbImageOrVideoMix.setVisibility(View.GONE);
                mTvSelectListTitle.setVisibility(View.GONE);
                ((ViewGroup) mRbNew.getParent()).setVisibility(View.GONE);
                mCbClosePreview.setVisibility(View.GONE);
                mBtnSingleSelect.setVisibility(View.GONE);
                mBtnCropSelect.setVisibility(View.GONE);
                mBtnTakePhoto.setVisibility(View.GONE);
            } else if (checkedId == mRbWeChat.getId()) {
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.VISIBLE);
                mCbVideoSingle.setVisibility(View.VISIBLE);
                mCbImageOrVideoMix.setVisibility(View.VISIBLE);
                mTvSelectListTitle.setVisibility(View.VISIBLE);
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.VISIBLE);
                ((ViewGroup) mRbNew.getParent()).setVisibility(View.VISIBLE);
                mBtnSingleSelect.setVisibility(View.VISIBLE);
                mBtnCropSelect.setVisibility(View.VISIBLE);
                mBtnTakePhoto.setVisibility(View.VISIBLE);
            } else if (checkedId == mRbCustom.getId()) {
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbVideoSingle.setVisibility(View.VISIBLE);
                mCbImageOrVideoMix.setVisibility(View.VISIBLE);
                mTvSelectListTitle.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.VISIBLE);
                ((ViewGroup) mRbNew.getParent()).setVisibility(View.VISIBLE);
                mBtnSingleSelect.setVisibility(View.VISIBLE);
                mBtnCropSelect.setVisibility(View.VISIBLE);
                mBtnTakePhoto.setVisibility(View.VISIBLE);
                mCbVideoSingle.setChecked(true);
                mCbImageOrVideoMix.setChecked(true);
                mCbVideoSingle.setEnabled(false);
                mCbImageOrVideoMix.setEnabled(false);
            } else if (checkedId == mRbImageOnly.getId()) {
                if (!mRbRedBook.isChecked()) {
                    mCbShowGif.setVisibility(View.VISIBLE);
                }
                mCbImageOrVideoMix.setVisibility(View.GONE);
                mCbVideoSingle.setVisibility(View.GONE);
            } else if (checkedId == mRbVideoOnly.getId()) {
                mCbShowGif.setVisibility(View.GONE);
                if (mRbWeChat.isChecked()) {
                    mCbImageOrVideoMix.setVisibility(View.VISIBLE);
                    mCbVideoSingle.setVisibility(View.VISIBLE);
                }
            } else if (checkedId == mRbAll.getId()) {
                if (!mRbRedBook.isChecked()) {
                    mCbShowGif.setVisibility(View.VISIBLE);
                }
                if (mRbWeChat.isChecked()) {
                    mCbImageOrVideoMix.setVisibility(View.VISIBLE);
                    mCbVideoSingle.setVisibility(View.VISIBLE);
                }
            }
        }
    };


    @Override
    public void onClick(View v) {
        picList.clear();
        refreshGridLayout();
        if (v == mBtnMultiSelect) {
            if (mRbRedBook.isChecked()) {
                redBookPick(false);
            } else {
                pick(9, false);
            }
        } else if (v == mBtnSingleSelect) {
            pick(1, false);
        } else if (v == mBtnCropSelect) {
            crop();
        } else if (v == mBtnTakePhoto) {
            //takePhoto();
        }
    }


    private void redBookPick(final boolean isAdd) {
        ImagePicker.withCrop(new RedBookCropPresenter())
                .setMaxCount(9)
                .showCamera(mCbShowCamera.isChecked())
                .showVideo(!mRbImageOnly.isChecked())
                .showBottomView(false)
                .showDraftDialog(false)
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        for (ImageItem imageItem : items) {
                            imageItem.path = imageItem.getCropUrl();
                        }
                        if (!isAdd) {
                            picList.clear();
                        }
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void pick(int count, final boolean isAdd) {
        if (mRbSave.isChecked()) {
            count = 9;
        }
        ImagePicker.withMulti(mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter())
                .setMaxCount(count)
                .setColumnCount(4)
                .showVideo(!mRbImageOnly.isChecked())
                .showGif(!mCbShowGif.isChecked())
                .showCamera(mCbShowCamera.isChecked())
                .showImage(!mRbVideoOnly.isChecked())
                .setSinglePickImageOrVideoType(mCbImageOrVideoMix.isChecked())
                .setVideoSinglePick(mCbVideoSingle.isChecked())
                .setShieldList(mRbShield.isChecked() ? picList : null)
                .setLastImageList(mRbSave.isChecked() ? picList : null)
                .setPreview(!mCbClosePreview.isChecked())
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        if (!isAdd || mRbSave.isChecked()) {
                            picList.clear();
                        }
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void crop() {
        ImagePicker.withMulti(mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter())
                .setMaxCount(1)
                .setColumnCount(4)
                .showVideo(false)
                .showGif(false)
                .showCamera(mCbShowCamera.isChecked())
                .showImage(true)
                .setSinglePickImageOrVideoType(false)
                .setVideoSinglePick(false)
                .setLastImageList(null)
                .setLastImageList(null)
                .setPreview(false)
                .setCropRatio(1, 1)
                .crop(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        picList.clear();
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }


    /**
     * 刷新图片显示
     */
    private void refreshGridLayout() {
        mGridLayout.setVisibility(View.VISIBLE);
        mGridLayout.removeAllViews();
        int num = picList.size();
        final int picSize = (getScreenWidth() - dp(20)) / 4;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(picSize, picSize);
        if (num == 0) {
            mGridLayout.setVisibility(View.GONE);
        } else if (num >= maxNum) {
            mGridLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.a_layout_pic_select, null);
                view.setLayoutParams(params);
                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                mGridLayout.addView(view);
            }
        } else {
            mGridLayout.setVisibility(View.VISIBLE);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.add_pic));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(dp(5), dp(5), dp(5), dp(5));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRbRedBook.isChecked()) {
                        redBookPick(true);
                    } else {
                        pick(9 - picList.size(), true);
                    }
                }
            });
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.a_layout_pic_select, null);
                view.setLayoutParams(params);
                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                mGridLayout.addView(view);
            }
            mGridLayout.addView(imageView);
        }
    }

    public void setPicItemClick(RelativeLayout layout, final int pos) {
        ImageView iv_pic = (ImageView) layout.getChildAt(0);
        ImageView iv_close = (ImageView) layout.getChildAt(1);
        Glide.with(this).load(picList.get(pos).path).into(iv_pic);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picList.remove(pos);
                refreshGridLayout();
            }
        });
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // preview(pos);
            }
        });
    }

    public int dp(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, this.getResources().getDisplayMetrics());
    }


    /**
     * 获得屏幕宽度
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
