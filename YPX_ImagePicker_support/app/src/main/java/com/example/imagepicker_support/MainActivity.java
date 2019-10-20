package com.example.imagepicker_support;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


import com.bumptech.glide.Glide;
import com.example.imagepicker_support.style.CustomImgPickerPresenter;
import com.example.imagepicker_support.style.RedBookCropPresenter;
import com.example.imagepicker_support.style.WXImgPickerPresenter;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickerError;
import com.ypx.imagepicker.builder.MultiPickerBuilder;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagePickCompleteListener2;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ImageItem> picList = new ArrayList<>();
    private RadioButton mRbRedBook;
    private RadioButton mRbWeChat;
    private RadioButton mRbCustom;
    private RadioGroup mRgStyle;
    private CheckBox mCbJPEG;
    private CheckBox mCbPNG;
    private CheckBox mCbGIF;
    private CheckBox mCbBMP;
    private CheckBox mCbWEBP;
    private CheckBox mCbMPEG;
    private CheckBox mCbMP4;
    private CheckBox mCbAVI;
    private CheckBox mCbMKV;
    private CheckBox mCbWEBM;
    private CheckBox mCbTS;
    private CheckBox mCbQUICKTIME;
    private CheckBox mCbTHREEGPP;
    private CheckBox mCbShowCamera;
    private CheckBox mCbClosePreview;
    private CheckBox mCbPreviewCanEdit;
    private CheckBox mCbVideoSingle;
    private CheckBox mCbImageOrVideo;
    private RadioButton mRbNew;
    private RadioButton mRbShield;
    private RadioButton mRbSave;
    private RadioButton mRbMulti;
    private RadioButton mRbCrop;
    private RadioButton mRbTakePhoto;
    private RadioButton mRbTakePhotoAndCrop;
    private CheckBox mCbCircle;
    private TextView mCropX;
    private SeekBar mXSeekBar;
    private TextView mCropY;
    private SeekBar mYSeekBar;
    private TextView mCropMargin;
    private SeekBar mMarginSeekBar;
    private LinearLayout mCropSetLayout;
    private RadioGroup mRgNextPickType;
    private RadioGroup mRgOpenType;
    private GridLayout mGridLayout;

    private int maxCount = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        picList.clear();
        refreshGridLayout();
    }

    private void initView() {
        mRbRedBook = findViewById(R.id.rb_redBook);
        mRbWeChat = findViewById(R.id.rb_weChat);
        mRbCustom = findViewById(R.id.rb_custom);
        mRgStyle = findViewById(R.id.rg_style);
        mCbJPEG = findViewById(R.id.cb_JPEG);
        mCbPNG = findViewById(R.id.cb_PNG);
        mCbGIF = findViewById(R.id.cb_GIF);
        mCbBMP = findViewById(R.id.cb_BMP);
        mCbWEBP = findViewById(R.id.cb_WEBP);
        mCbMPEG = findViewById(R.id.cb_MPEG);
        mCbMP4 = findViewById(R.id.cb_MP4);
        mCbAVI = findViewById(R.id.cb_AVI);
        mCbMKV = findViewById(R.id.cb_MKV);
        mCbWEBM = findViewById(R.id.cb_WEBM);
        mCbTS = findViewById(R.id.cb_TS);
        mCbQUICKTIME = findViewById(R.id.cb_QUICKTIME);
        mCbTHREEGPP = findViewById(R.id.cb_THREEGPP);
        mCbShowCamera = findViewById(R.id.cb_showCamera);
        mCbClosePreview = findViewById(R.id.cb_closePreview);
        mCbPreviewCanEdit = findViewById(R.id.cb_previewCanEdit);
        mCbVideoSingle = findViewById(R.id.cb_videoSingle);
        mCbImageOrVideo = findViewById(R.id.cb_imageOrVideo);
        mRbNew = findViewById(R.id.rb_new);
        mRbShield = findViewById(R.id.rb_shield);
        mRbSave = findViewById(R.id.rb_save);
        mRbMulti = findViewById(R.id.rb_multi);
        mRbCrop = findViewById(R.id.rb_crop);
        mRbTakePhoto = findViewById(R.id.rb_takePhoto);
        mRbTakePhotoAndCrop = findViewById(R.id.rb_takePhotoAndCrop);
        mCbCircle = findViewById(R.id.cb_circle);
        mCropX = findViewById(R.id.mCropX);
        mXSeekBar = findViewById(R.id.mXSeekBar);
        mCropY = findViewById(R.id.mCropY);
        mYSeekBar = findViewById(R.id.mYSeekBar);
        mCropMargin = findViewById(R.id.mCropMargin);
        mMarginSeekBar = findViewById(R.id.mMarginSeekBar);
        mCropSetLayout = findViewById(R.id.mCropSetLayout);
        mGridLayout = findViewById(R.id.gridLayout);
        mRgNextPickType = findViewById(R.id.rg_nextPickType);
        mRgOpenType = findViewById(R.id.rg_openType);
        mRgStyle.setOnCheckedChangeListener(listener);
        mRgOpenType.setOnCheckedChangeListener(listener2);
        mXSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mYSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mMarginSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == mRbRedBook.getId()) {
                mCbClosePreview.setEnabled(false);
                mCbImageOrVideo.setEnabled(false);
                mRbNew.setEnabled(false);
                mRbShield.setEnabled(false);
                mRbSave.setEnabled(false);
                mRbCrop.setEnabled(false);
                mCbVideoSingle.setEnabled(true);
                mRbMulti.setChecked(true);
            } else if (checkedId == mRbWeChat.getId()) {
                mCbClosePreview.setEnabled(true);
                mCbImageOrVideo.setEnabled(true);
                mRgNextPickType.setEnabled(true);
                mRbCrop.setEnabled(true);
                mCbVideoSingle.setEnabled(true);
                mRbMulti.setChecked(true);
                mRbNew.setEnabled(true);
                mRbShield.setEnabled(true);
                mRbSave.setEnabled(true);
            } else if (checkedId == mRbCustom.getId()) {
                mCbClosePreview.setEnabled(false);
                mCbVideoSingle.setEnabled(false);
                mCbImageOrVideo.setEnabled(true);
                mRgNextPickType.setEnabled(true);
                mRbCrop.setEnabled(true);
                mRbMulti.setChecked(true);
                mRbNew.setEnabled(true);
                mRbShield.setEnabled(true);
                mRbSave.setEnabled(true);
            }
        }
    };

    RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == mRbCrop.getId() || checkedId == mRbTakePhotoAndCrop.getId()) {
                mCropSetLayout.setVisibility(View.VISIBLE);
            } else {
                mCropSetLayout.setVisibility(View.GONE);
            }
        }
    };

    @SuppressLint("DefaultLocale")
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar == mXSeekBar) {
                mCropX.setText(String.format("cropX: %d", progress));
            } else if (seekBar == mYSeekBar) {
                mCropY.setText(String.format("cropY: %d", progress));
            } else if (seekBar == mMarginSeekBar) {
                mCropMargin.setText(String.format("剪裁框间距: %d", progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void redBookPick(int count) {
        ImagePicker.withCrop(new RedBookCropPresenter())//设置presenter
                .setMaxCount(count)//设置选择数量
                .showCamera(mCbShowCamera.isChecked())//设置显示拍照
                .setColumnCount(4)//设置列数
                .mimeType(getMimeTypes())//设置需要加载的文件类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉的文件类型
                .setFirstImageItem(picList.size() > 0 ? picList.get(0) : null)//设置上一次选中的图片
                // .setFirstImageUrl(null)//设置上一次选中的图片地址
                .setVideoSinglePick(mCbVideoSingle.isChecked())//设置视频单选
                .setCropPicSaveFilePath(ImagePicker.cropPicSaveFilePath)
                .setMaxVideoDuration(120000L)//设置可选区的最大视频时长
                .pick(this, new OnImagePickCompleteListener2() {
                    @Override
                    public void onPickFailed(PickerError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片剪裁回调，主线程
                        //注意：剪裁回调里的ImageItem中getCropUrl()才是剪裁过后的图片地址
                        for (ImageItem item : items) {
                            item.path = item.getCropUrl();
                        }
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void pick(int count) {
        IMultiPickerBindPresenter presenter = mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter();
        ImagePicker.withMulti(presenter)//指定presenter
                .setMaxCount(count)//设置选择的最大数
                .setColumnCount(4)//设置列数
                .mimeType(getMimeTypes())//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .showCamera(mCbShowCamera.isChecked())//显示拍照
                .setPreview(!mCbClosePreview.isChecked())//是否开启预览
                .setVideoSinglePick(mCbVideoSingle.isChecked())//设置视频单选
                .setSinglePickImageOrVideoType(mCbImageOrVideo.isChecked())//设置图片和视频单一类型选择
                .setMaxVideoDuration(12000L)//设置视频可选取的最大时长
                .setLastImageList(mRbSave.isChecked() ? picList : null)//设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
                .setShieldList(mRbShield.isChecked() ? picList : null)//设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片选择回调，主线程
                        if (mRbSave.isChecked()) {
                            picList.clear();
                        }
                        picList.addAll(items);
                        refreshGridLayout();
                    }
                });
    }

    private void preview(int pos) {
        IMultiPickerBindPresenter presenter = mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter();
        if (mCbPreviewCanEdit.isChecked()) {
            //开启编辑预览
            ImagePicker.preview(this, presenter, picList, pos, new OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    //图片编辑回调，主线程
                    picList.clear();
                    picList.addAll(items);
                    refreshGridLayout();
                }
            });
        } else {
            //开启普通预览
            ImagePicker.preview(this, presenter, picList, pos, null);
        }
    }

    private void crop() {
        IMultiPickerBindPresenter presenter = mRbWeChat.isChecked() ? new WXImgPickerPresenter() : new CustomImgPickerPresenter();
        MultiPickerBuilder builder = ImagePicker.withMulti(presenter)//指定presenter
                .setColumnCount(4)//设置列数
                .mimeType(getMimeTypes())//设置要加载的文件类型，可指定单一类型
                // .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .showCamera(mCbShowCamera.isChecked())//显示拍照
                .cropRectMinMargin(mMarginSeekBar.getProgress())
                .cropSaveFilePath(ImagePicker.cropPicSaveFilePath)
                .setCropRatio(mXSeekBar.getProgress(), mYSeekBar.getProgress());
        if (mCbCircle.isChecked()) {
            builder.cropAsCircle();
        }
        builder.crop(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片选择回调，主线程
                picList.addAll(items);
                refreshGridLayout();
            }
        });
    }

    private void takePhoto() {
        ImagePicker.takePhoto(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                picList.addAll(items);
                refreshGridLayout();
            }
        });
    }

    private void takePhotoAndCrop() {
        //配置剪裁属性
        MultiSelectConfig selectConfig = new MultiSelectConfig();
        selectConfig.setCropRatio(mXSeekBar.getProgress(), mYSeekBar.getProgress());//设置剪裁比例
        selectConfig.setCropRectMargin(mMarginSeekBar.getProgress());//设置剪裁框间距，单位px
        selectConfig.setCropSaveFilePath(ImagePicker.cropPicSaveFilePath);
        selectConfig.setCircle(mCbCircle.isChecked());//是否圆形剪裁
        ImagePicker.takePhotoAndCrop(this, new WXImgPickerPresenter(), selectConfig, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //剪裁回调，主线程
                picList.addAll(items);
                refreshGridLayout();
            }
        });
    }

    private void startPick() {
        if (mRbCrop.isChecked()) {
            crop();
        } else if (mRbTakePhoto.isChecked()) {
            takePhoto();
        } else if (mRbTakePhotoAndCrop.isChecked()) {
            takePhotoAndCrop();
        } else if (mRbRedBook.isChecked()) {
            redBookPick(maxCount - picList.size());
        } else if (mRbWeChat.isChecked()) {
            pick(maxCount - picList.size());
        } else if (mRbCustom.isChecked()) {
            pick(maxCount - picList.size());
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
        if (num >= maxCount) {
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

    private Set<MimeType> getMimeTypes() {
        Set<MimeType> mimeTypes = new HashSet<>();
        if (mCbJPEG.isChecked()) {
            mimeTypes.add(MimeType.JPEG);
        }
        if (mCbPNG.isChecked()) {
            mimeTypes.add(MimeType.PNG);
        }
        if (mCbGIF.isChecked()) {
            mimeTypes.add(MimeType.GIF);
        }
        if (mCbBMP.isChecked()) {
            mimeTypes.add(MimeType.BMP);
        }
        if (mCbWEBP.isChecked()) {
            mimeTypes.add(MimeType.WEBP);
        }
        if (mCbMPEG.isChecked()) {
            mimeTypes.add(MimeType.MPEG);
        }
        if (mCbMP4.isChecked()) {
            mimeTypes.add(MimeType.MP4);
        }
        if (mCbAVI.isChecked()) {
            mimeTypes.add(MimeType.AVI);
        }
        if (mCbMKV.isChecked()) {
            mimeTypes.add(MimeType.MKV);
        }
        if (mCbWEBM.isChecked()) {
            mimeTypes.add(MimeType.WEBM);
        }
        if (mCbTS.isChecked()) {
            mimeTypes.add(MimeType.TS);
        }
        if (mCbQUICKTIME.isChecked()) {
            mimeTypes.add(MimeType.QUICKTIME);
        }
        if (mCbTHREEGPP.isChecked()) {
            mimeTypes.add(MimeType.THREEGPP);
        }
        return mimeTypes;
    }
}
