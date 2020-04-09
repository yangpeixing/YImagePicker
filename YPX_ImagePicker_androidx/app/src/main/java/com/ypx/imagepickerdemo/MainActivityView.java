package com.ypx.imagepickerdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepickerdemo.style.WeChatPresenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivityView {
    interface MainViewCallBack {
        void weChatPick(int count);

        void redBookPick(int count);

        void pickAndCrop();

        void autoCrop();

        void takePhoto();

        void takePhotoAndCrop();

        void preview(int pos);
    }

    private MainViewCallBack mainViewCallBack;
    final int maxCount = 9;
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
    private CheckBox mCbShowOriginal;
    private RadioButton mRbNew;
    private RadioButton mRbShield;
    private RadioButton mRbSave;
    private RadioButton mRbMulti;
    private RadioButton mRbCrop;
    private RadioButton mRbTakePhoto;
    private RadioButton mRbTakePhotoAndCrop;
    private RadioButton mRbSingle;
    private CheckBox mCbFilterVideoPreview;
    private CheckBox mCbSingleAutoComplete;
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
    private RadioButton mRbAutoCrop;
    private RadioGroup mRgOpenType2;
    private GridLayout mGridLayout;
    private CheckBox mCbGap;
    private CheckBox mCbGapBackground;
    private CheckBox mCbAutoJumpAloha;

    public CheckBox getmCbBMP() {
        return mCbBMP;
    }

    private WeakReference<Activity> activityWeakReference;

    public static MainActivityView create(Activity activity, MainViewCallBack mainViewCallBack) {
        return new MainActivityView(activity, mainViewCallBack);
    }

    private MainActivityView(Activity activity, MainViewCallBack mainViewCallBack) {
        this.mainViewCallBack = mainViewCallBack;
        activityWeakReference = new WeakReference<>(activity);
        activity.setContentView(R.layout.activity_main);
        initView(activity);
        picList = new ArrayList<>();
        refreshGridLayout();
    }

    private void initView(Activity activity) {
        mRbRedBook = activity.findViewById(R.id.rb_redBook);
        mRbWeChat = activity.findViewById(R.id.rb_weChat);
        mRbCustom = activity.findViewById(R.id.rb_custom);
        mRgStyle = activity.findViewById(R.id.rg_style);
        mCbJPEG = activity.findViewById(R.id.cb_JPEG);
        mCbPNG = activity.findViewById(R.id.cb_PNG);
        mCbGIF = activity.findViewById(R.id.cb_GIF);
        mCbBMP = activity.findViewById(R.id.cb_BMP);
        mCbWEBP = activity.findViewById(R.id.cb_WEBP);
        mCbMPEG = activity.findViewById(R.id.cb_MPEG);
        mCbMP4 = activity.findViewById(R.id.cb_MP4);
        mCbAVI = activity.findViewById(R.id.cb_AVI);
        mCbMKV = activity.findViewById(R.id.cb_MKV);
        mCbWEBM = activity.findViewById(R.id.cb_WEBM);
        mCbTS = activity.findViewById(R.id.cb_TS);
        mCbQUICKTIME = activity.findViewById(R.id.cb_QUICKTIME);
        mCbTHREEGPP = activity.findViewById(R.id.cb_THREEGPP);
        mCbShowCamera = activity.findViewById(R.id.cb_showCamera);
        mCbClosePreview = activity.findViewById(R.id.cb_closePreview);
        mCbPreviewCanEdit = activity.findViewById(R.id.cb_previewCanEdit);
        mCbVideoSingle = activity.findViewById(R.id.cb_videoSingle);
        mCbImageOrVideo = activity.findViewById(R.id.cb_imageOrVideo);
        mCbShowOriginal = activity.findViewById(R.id.cb_showOriginal);
        mRbNew = activity.findViewById(R.id.rb_new);
        mRbShield = activity.findViewById(R.id.rb_shield);
        mRbSave = activity.findViewById(R.id.rb_save);
        mRbMulti = activity.findViewById(R.id.rb_multi);
        mRbCrop = activity.findViewById(R.id.rb_crop);
        mRbSingle = activity.findViewById(R.id.rb_single);
        mRbTakePhoto = activity.findViewById(R.id.rb_takePhoto);
        mRbTakePhotoAndCrop = activity.findViewById(R.id.rb_takePhotoAndCrop);
        mCbCircle = activity.findViewById(R.id.cb_circle);
        mCbFilterVideoPreview = activity.findViewById(R.id.cb_filterVideoPreview);
        mCropX = activity.findViewById(R.id.mCropX);
        mXSeekBar = activity.findViewById(R.id.mXSeekBar);
        mCropY = activity.findViewById(R.id.mCropY);
        mYSeekBar = activity.findViewById(R.id.mYSeekBar);
        mCropMargin = activity.findViewById(R.id.mCropMargin);
        mMarginSeekBar = activity.findViewById(R.id.mMarginSeekBar);
        mCropSetLayout = activity.findViewById(R.id.mCropSetLayout);
        mGridLayout = activity.findViewById(R.id.gridLayout);
        mRgNextPickType = activity.findViewById(R.id.rg_nextPickType);
        mRgOpenType = activity.findViewById(R.id.rg_openType);
        mRbAutoCrop = activity.findViewById(R.id.rb_autoCrop);
        mRgOpenType2 = activity.findViewById(R.id.rg_openType2);
        mCbSingleAutoComplete = activity.findViewById(R.id.cb_singleAutoComplete);
        mCbAutoJumpAloha = activity.findViewById(R.id.cb_autoJumpAloha);

        mRgStyle.setOnCheckedChangeListener(listener);
        mRbMulti.setOnCheckedChangeListener(listener4);
        mRbCrop.setOnCheckedChangeListener(listener4);
        mRbAutoCrop.setOnCheckedChangeListener(listener4);
        mRbTakePhoto.setOnCheckedChangeListener(listener4);
        mRbTakePhotoAndCrop.setOnCheckedChangeListener(listener4);
        mRbSingle.setOnCheckedChangeListener(listener4);
        mXSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mYSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        mMarginSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        mCbGap = activity.findViewById(R.id.cb_gap);
        mCbGapBackground = activity.findViewById(R.id.cb_gapBackground);

        mCbAutoJumpAloha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.isAutoJumpAlohaActivity = isChecked;
            }
        });
    }


    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == mRbRedBook.getId()) {
                mCbClosePreview.setEnabled(false);
                mCbImageOrVideo.setEnabled(false);
                mRbNew.setEnabled(false);
                mRbShield.setEnabled(false);
                mRbSave.setEnabled(false);
                mRbCrop.setEnabled(false);
                mCbShowOriginal.setEnabled(false);
                mRbMulti.setChecked(true);
            } else if (checkedId == mRbWeChat.getId()) {
                mCbClosePreview.setEnabled(true);
                mCbImageOrVideo.setEnabled(true);
                mRgNextPickType.setEnabled(true);
                mRbCrop.setEnabled(true);
                mCbShowOriginal.setEnabled(true);
                mRbMulti.setChecked(true);
                mRbNew.setEnabled(true);
                mRbShield.setEnabled(true);
                mRbSave.setEnabled(true);
            } else if (checkedId == mRbCustom.getId()) {
                mCbClosePreview.setEnabled(false);
                mCbImageOrVideo.setEnabled(true);
                mRgNextPickType.setEnabled(true);
                mCbShowOriginal.setEnabled(false);
                mRbCrop.setEnabled(true);
                mRbMulti.setChecked(true);
                mRbNew.setEnabled(true);
                mRbShield.setEnabled(true);
                mRbSave.setEnabled(true);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener listener4 = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView.getParent() == mRgOpenType) {
                    mRgOpenType2.clearCheck();
                } else {
                    mRgOpenType.clearCheck();
                }

                if (buttonView == mRbTakePhotoAndCrop || buttonView == mRbAutoCrop || buttonView == mRbCrop) {
                    mCropSetLayout.setVisibility(View.VISIBLE);
                } else {
                    mCropSetLayout.setVisibility(View.GONE);
                }
            }
        }
    };

    @SuppressLint("DefaultLocale")
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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


    public int getSelectMode() {
        if (mRbSingle.isChecked()) {
            return SelectMode.MODE_SINGLE;
        }

        if (mRbMulti.isChecked()) {
            return SelectMode.MODE_MULTI;
        }

        if (mRbCrop.isChecked()) {
            return SelectMode.MODE_CROP;
        }

        return SelectMode.MODE_MULTI;
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
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.a_layout_pic_select, null);
                view.setLayoutParams(params);
                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                mGridLayout.addView(view);
            }
        } else {
            mGridLayout.setVisibility(View.VISIBLE);
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(params);
            imageView.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.add_pic));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(dp(5), dp(5), dp(5), dp(5));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPick();
                }
            });
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.a_layout_pic_select, null);
                view.setLayoutParams(params);
                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                mGridLayout.addView(view);
            }
            mGridLayout.addView(imageView);
        }
    }

    private void setPicItemClick(RelativeLayout layout, final int pos) {
        ImageView iv_pic = (ImageView) layout.getChildAt(0);
        ImageView iv_close = (ImageView) layout.getChildAt(1);
        displayImage(picList.get(pos), iv_pic);
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
                mainViewCallBack.preview(pos);
            }
        });
    }

    private void displayImage(ImageItem imageItem, ImageView imageView) {
        if (imageItem.getCropUrl() != null && imageItem.getCropUrl().length() > 0) {
            Glide.with(getActivity()).load(imageItem.getCropUrl()).into(imageView);
        } else {
            if (imageItem.getUri() != null) {
                Glide.with(getActivity()).load(imageItem.getUri()).into(imageView);
            } else {
                Glide.with(getActivity()).load(imageItem.path).into(imageView);
            }
        }
    }


    private Activity getActivity() {
        if (activityWeakReference != null) {
            return activityWeakReference.get();
        }
        return (Activity) mCropSetLayout.getContext();
    }

    private int dp(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getActivity().getResources().getDisplayMetrics());
    }

    /**
     * 获得屏幕宽度
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public Set<MimeType> getMimeTypes() {
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

    public void startPick() {
        if (mRbCrop.isChecked()) {
            mainViewCallBack.pickAndCrop();
        } else if (mRbTakePhoto.isChecked()) {
            mainViewCallBack.takePhoto();
        } else if (mRbAutoCrop.isChecked()) {
            mainViewCallBack.autoCrop();
        } else if (mRbTakePhotoAndCrop.isChecked()) {
            mainViewCallBack.takePhotoAndCrop();
        } else if (mRbSingle.isChecked()) {
            if (mRbRedBook.isChecked()) {
                mainViewCallBack.redBookPick(1);
            } else {
                mainViewCallBack.weChatPick(1);
            }
        } else if (mRbRedBook.isChecked()) {
            mainViewCallBack.redBookPick(maxCount - picList.size());
        } else if (mRbWeChat.isChecked()) {
            mainViewCallBack.weChatPick(maxCount - picList.size());
        } else if (mRbCustom.isChecked()) {
            mainViewCallBack.weChatPick(maxCount - picList.size());
        }
    }

    public ArrayList<ImageItem> getPicList() {
        return picList;
    }


    public boolean isCustom() {
        return mRbCustom.isChecked();
    }

    public boolean isShowOriginal() {
        return mCbShowOriginal.isChecked();
    }

    public boolean isCanPreviewVideo() {
        return !mCbFilterVideoPreview.isChecked();
    }

    public boolean isPreviewEnable() {
        return !mCbClosePreview.isChecked();
    }

    public boolean isShowCamera() {
        return mCbShowCamera.isChecked();
    }

    public boolean isVideoSinglePick() {
        return mCbVideoSingle.isChecked();
    }

    public boolean isSinglePickWithAutoComplete() {
        return mCbSingleAutoComplete.isChecked();
    }

    public boolean isSinglePickImageOrVideoType() {
        return mCbImageOrVideo.isChecked();
    }

    public boolean isCheckLastImageList() {
        return mRbSave.isChecked();
    }

    public boolean isCheckShieldList() {
        return mRbShield.isChecked();
    }

    public int getMinMarginProgress() {
        return mMarginSeekBar.getProgress();
    }

    public boolean isGap() {
        return mCbGap.isChecked();
    }

    public int getCropGapBackgroundColor() {
        return mCbGapBackground.isChecked() ? Color.TRANSPARENT : Color.RED;
    }

    public int getCropRatioX() {
        return mXSeekBar.getProgress();
    }

    public int getCropRatioY() {
        return mYSeekBar.getProgress();
    }

    public boolean isNeedCircle() {
        return mCbCircle.isChecked();
    }

    public void notifyImageItemsCallBack(ArrayList<ImageItem> imageItems) {
        //图片选择回调，主线程
        if (mRbSave.isChecked()) {
            picList.clear();
        }
        picList.addAll(imageItems);
        refreshGridLayout();
    }
}
