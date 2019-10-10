package com.ypx.imagepickerdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepickerdemo.style.CustomImgPickerPresenter;
import com.ypx.imagepickerdemo.style.RedBookCropPresenter;
import com.ypx.imagepickerdemo.style.WXImgPickerPresenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
    private GridLayout mGridLayout;
    private RadioGroup mRgStyle;
    private RadioGroup mRgType;
    private TextView mTvSelectListTitle;
    private RadioButton mRbMulti;
    private RadioButton mRbSingle;
    private RadioButton mRbCrop;
    private RadioButton mRbTakePhoto;
    private RadioGroup mRgOpenType;
    private CheckBox mCbPreviewCanEdit;
    private TextView mCropX;
    private SeekBar mXSeekBar;
    private TextView mCropY;
    private SeekBar mYSeekBar;
    private TextView mCropMargin;
    private SeekBar mMarginSeekBar;
    private LinearLayout mCropSetLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
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
        mGridLayout = findViewById(R.id.gridLayout);
        mTvSelectListTitle = findViewById(R.id.mTvSelectListTitle);

        mRbMulti = findViewById(R.id.rb_multi);
        mRbSingle = findViewById(R.id.rb_single);
        mRbCrop = findViewById(R.id.rb_crop);
        mRbTakePhoto = findViewById(R.id.rb_takePhoto);
        mRgOpenType = findViewById(R.id.rg_openType);
        mCbPreviewCanEdit = findViewById(R.id.cb_previewCanEdit);
        mCropX = findViewById(R.id.mCropX);
        mXSeekBar = findViewById(R.id.mXSeekBar);
        mCropY = findViewById(R.id.mCropY);
        mYSeekBar = findViewById(R.id.mYSeekBar);
        mCropMargin = findViewById(R.id.mCropMargin);
        mMarginSeekBar = findViewById(R.id.mMarginSeekBar);
        mCropSetLayout = findViewById(R.id.mCropSetLayout);

        mRgStyle.setOnCheckedChangeListener(listener);
        mRgType.setOnCheckedChangeListener(listener);
        mRgOpenType.setOnCheckedChangeListener(listener);
        mXSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mYSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mMarginSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        mRbWeChat.setChecked(true);
        mRbAll.setChecked(true);
        mRbNew.setChecked(true);
        mRbMulti.setChecked(true);

        mCropSetLayout.setVisibility(View.GONE);

        picList.clear();
        refreshGridLayout();

    }

    @SuppressLint("DefaultLocale")
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == mXSeekBar) {
                mCropX.setText(String.format("cropX: %d", progress));
            } else if (seekBar == mYSeekBar) {
                mCropY.setText(String.format("cropY: %d", progress));
            } else if (seekBar == mMarginSeekBar) {
                mCropMargin.setText(String.format("cropMargin: %d", progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (group == mRgOpenType) {
                if (checkedId == mRbMulti.getId()) {//多选
                    mRbAll.setChecked(true);
                    mRbAll.setEnabled(true);
                    mRbVideoOnly.setEnabled(true);
                    mRbImageOnly.setEnabled(true);
                } else {
                    mRbAll.setChecked(false);
                    mRbImageOnly.setChecked(true);
                    mRbAll.setEnabled(false);
                    mRbVideoOnly.setEnabled(false);
                    mRbImageOnly.setEnabled(false);
                    if (checkedId == mRbCrop.getId()) {
                        mCropSetLayout.setVisibility(View.VISIBLE);
                    } else {
                        mCropSetLayout.setVisibility(View.GONE);
                    }
                }
                refreshGridLayout();
                return;
            }
            mCbVideoSingle.setEnabled(true);
            mCbImageOrVideoMix.setEnabled(true);
            if (checkedId == mRbRedBook.getId()) {
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.GONE);
                mCbVideoSingle.setVisibility(View.VISIBLE);
                mCbImageOrVideoMix.setVisibility(View.GONE);
                mTvSelectListTitle.setVisibility(View.GONE);
                ((ViewGroup) mRbNew.getParent()).setVisibility(View.GONE);
                mCbClosePreview.setVisibility(View.GONE);
                mRbCrop.setVisibility(View.GONE);
            } else if (checkedId == mRbWeChat.getId()) {
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.VISIBLE);
                mCbVideoSingle.setVisibility(View.VISIBLE);
                mCbImageOrVideoMix.setVisibility(View.VISIBLE);
                mTvSelectListTitle.setVisibility(View.VISIBLE);
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.VISIBLE);
                ((ViewGroup) mRbNew.getParent()).setVisibility(View.VISIBLE);
                mRbCrop.setVisibility(View.VISIBLE);
            } else if (checkedId == mRbCustom.getId()) {
                mCbShowGif.setVisibility(View.VISIBLE);
                mCbVideoSingle.setVisibility(View.VISIBLE);
                mCbImageOrVideoMix.setVisibility(View.VISIBLE);
                mTvSelectListTitle.setVisibility(View.VISIBLE);
                mCbClosePreview.setVisibility(View.VISIBLE);
                ((ViewGroup) mRbNew.getParent()).setVisibility(View.VISIBLE);
                mRbCrop.setVisibility(View.VISIBLE);
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


    private void redBookPick(int count) {
        if (mRbSave.isChecked()) {
            count = 9;
        }
        ImagePicker.withCrop(new RedBookCropPresenter())
                .setMaxCount(count)
                .showCamera(mCbShowCamera.isChecked())
                .showImage(!mRbVideoOnly.isChecked())
                .showVideo(!mRbImageOnly.isChecked())
                .showGif(!mCbShowGif.isChecked())
                .setVideoSinglePick(mCbVideoSingle.isChecked())
                .setCropPicSaveFilePath(ImagePicker.cropPicSaveFilePath)
                .setFirstImageItem(picList != null && picList.size() > 0 ? picList.get(0) : null)
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        for (ImageItem imageItem : items) {
                            if (!imageItem.isVideo()) {
                                imageItem.path = imageItem.getCropUrl();
                            }
                        }
                        if (mRbSave.isChecked()) {
                            picList.clear();
                        }
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void pick(int count) {
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
                .setSelectMode(mRbSingle.isChecked() ? SelectMode.MODE_SINGLE :
                        SelectMode.MODE_MULTI)
                .setMaxVideoDuration(120000)
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        if (mRbSave.isChecked()) {
                            picList.clear();
                        }
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void crop() {
        ImagePicker.withMulti(mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter())
                .setColumnCount(4)
                .showCamera(mCbShowCamera.isChecked())
                .showImage(true)
                .setCropRatio(mXSeekBar.getProgress(), mYSeekBar.getProgress())
                .cropRectMinMargin(dp(mMarginSeekBar.getProgress()))
                .cropSaveFilePath(ImagePicker.cropPicSaveFilePath)
                .crop(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        picList.clear();
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void preview(int pos) {
        ImagePicker.withMulti(mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter())
                .preview(this, picList, pos, mCbPreviewCanEdit.isChecked() ? new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        picList.clear();
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                } : null);
    }

    private void startPick() {
        if (mRbRedBook.isChecked()) {
            redBookPick(9 - picList.size());
        } else {
            if (mRbCrop.isChecked()) {
                crop();
            } else if (mRbSingle.isChecked()) {
                pick(1);
            } else if (mRbMulti.isChecked()) {
                pick(9 - picList.size());
            } else {
                Toast.makeText(this, "拍照功能暂未开放!", Toast.LENGTH_SHORT).show();
            }
        }
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
        if (num >= maxNum) {
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
                    startPick();
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
                preview(pos);
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
