package com.ypx.imagepickerdemo;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.RelativeLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.impl.MediaObserver;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;
import com.ypx.imagepickerdemo.style.CustomImgPickerPresenter;
import com.ypx.imagepickerdemo.style.RedBookCropPresenter;
import com.ypx.imagepickerdemo.style.WXImgPickerPresenter;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_multiSelect, btn_singleSelect, btn_cropSelect, btn_takePhoto;
    private GridLayout gridLayout;
    private PicBrowseImageView iv_single;
    private CheckBox cb_redBook, cb_jhl, cb_wx, cb_showCamera, cb_showVideo, cb_showGif, cb_shield, cb_last;
    private ArrayList<ImageItem> picList = new ArrayList<>();

    int maxNum = 16;
    private WXImgPickerPresenter wxImgPickerPresenter;
    private CustomImgPickerPresenter customImgPickerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_multiSelect = findViewById(R.id.btn_multiSelect);
        btn_singleSelect = findViewById(R.id.btn_singleSelect);
        btn_cropSelect = findViewById(R.id.btn_cropSelect);
        gridLayout = findViewById(R.id.gridLayout);
        iv_single = findViewById(R.id.iv_single);
        cb_jhl = findViewById(R.id.cb_jhl);
        cb_wx = findViewById(R.id.cb_wx);
        cb_redBook = findViewById(R.id.cb_redBook);
        cb_showVideo = findViewById(R.id.cb_showVideo);
        cb_showCamera = findViewById(R.id.cb_showCamera);
        cb_showGif = findViewById(R.id.cb_showGif);
        cb_shield = findViewById(R.id.cb_shield);
        cb_last = findViewById(R.id.cb_last);
        wxImgPickerPresenter = new WXImgPickerPresenter();
        customImgPickerPresenter = new CustomImgPickerPresenter();
        iv_single.setMaxScale(5.0f);
        iv_single.enable();
        btn_takePhoto = findViewById(R.id.btn_takePhoto);
        cb_wx.setChecked(true);
        btn_multiSelect.setOnClickListener(this);
        btn_singleSelect.setOnClickListener(this);
        btn_cropSelect.setOnClickListener(this);
        btn_takePhoto.setOnClickListener(this);
        cb_redBook.setOnClickListener(this);
        cb_jhl.setOnClickListener(this);
        cb_wx.setOnClickListener(this);

        //媒体文件观察者
        MediaObserver.instance.register(getApplication());
    }


    @Override
    public void onClick(View v) {
        picList.clear();
        refreshGridLayout();
        if (v == btn_multiSelect) {
            pick(9 - picList.size());
        } else if (v == btn_singleSelect) {
            pick(1);
        } else if (v == btn_cropSelect) {
            crop();
        } else if (v == btn_takePhoto) {
            takePhoto();
        } else if (v == cb_redBook) {
            cb_jhl.setChecked(false);
            cb_wx.setChecked(false);
            cb_redBook.setChecked(true);
            btn_cropSelect.setVisibility(View.GONE);
            btn_takePhoto.setVisibility(View.GONE);
        } else if (v == cb_jhl) {
            cb_redBook.setChecked(false);
            cb_wx.setChecked(false);
            cb_jhl.setChecked(true);
            btn_cropSelect.setVisibility(View.VISIBLE);
            btn_takePhoto.setVisibility(View.VISIBLE);
        } else if (v == cb_wx) {
            cb_redBook.setChecked(false);
            cb_jhl.setChecked(false);
            cb_wx.setChecked(true);
            btn_cropSelect.setVisibility(View.VISIBLE);
            btn_takePhoto.setVisibility(View.VISIBLE);
        }
    }


    public void pick(final int selectCount) {
        if (selectCount > 9) {
            return;
        }
        if (cb_redBook.isChecked()) {
            redBookPick(selectCount);
        } else {
            wxPick(selectCount);
        }
    }

    private void redBookPick(int count) {
        ImagePicker.withCrop(new RedBookCropPresenter())
                //.setFirstImageItem(mList.size() > 0 ? mList.get(0) : null)
                .setFirstImageUrl(getUrlWithPos(0))
                .setMaxCount(count)
                .showBottomView(true)
                .showVideo(cb_showVideo.isChecked())
                .showCamera(cb_showCamera.isChecked())
                .setCropPicSaveFilePath(Environment.getExternalStorageDirectory().toString() +
                        File.separator + "MarsCrop" + File.separator)
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<com.ypx.imagepicker.bean.ImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }

    private void wxPick(int count) {
        ImagePicker.withMulti(cb_jhl.isChecked() ? customImgPickerPresenter : wxImgPickerPresenter)
                .showCamera(cb_showCamera.isChecked())
                .showVideo(cb_showVideo.isChecked())
                .setLastImageList(cb_last.isChecked() ? picList : null)
                .setShieldList(cb_shield.isChecked() ? picList : null)
                .setMaxCount(count)
                .setColumnCount(4)
                .showGif(cb_showGif.isChecked())
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }

    private void crop() {
        picList.clear();
        ImagePicker.withMulti(cb_jhl.isChecked() ? customImgPickerPresenter : wxImgPickerPresenter)
                .showCamera(cb_showCamera.isChecked())
                .setColumnCount(4)
                .showVideo(cb_showVideo.isChecked())
                .showGif(cb_showGif.isChecked())
                .setCropRatio(1, 1)
                .crop(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }

    public void takePhoto() {
        picList.clear();
        ImagePicker.withMulti(wxImgPickerPresenter).takePhoto(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> imageItems) {
                if (imageItems != null && imageItems.size() > 0) {
                    picList.addAll(imageItems);
                    refreshGridLayout();
                }
            }
        });
    }

    public void preview(int pos) {
        ImagePicker.withMulti(cb_jhl.isChecked() ? customImgPickerPresenter : wxImgPickerPresenter)
                .preview(this, picList, pos, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.clear();
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }


    /**
     * 刷新图片显示
     */
    private void refreshGridLayout() {
        iv_single.setVisibility(View.GONE);
        gridLayout.setVisibility(View.VISIBLE);
        gridLayout.removeAllViews();
        int num = picList.size();
        final int picSize = (getScreenWidth() - dp(20)) / 4;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(picSize, picSize);
        if (num == 0) {
            gridLayout.setVisibility(View.GONE);
        } else if (num >= maxNum) {
            gridLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.a_layout_pic_select, null);
                view.setLayoutParams(params);
                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                gridLayout.addView(view);
            }
        } else {
            gridLayout.setVisibility(View.VISIBLE);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.add_pic));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(dp(5), dp(5), dp(5), dp(5));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pick(9 - picList.size());
                }
            });
            for (int i = 0; i < num; i++) {
                RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.a_layout_pic_select, null);
                view.setLayoutParams(params);
                view.setPadding(dp(5), dp(5), dp(5), dp(5));
                setPicItemClick(view, i);
                gridLayout.addView(view);
            }
            gridLayout.addView(imageView);
        }
    }

    public void setPicItemClick(RelativeLayout layout, final int pos) {
        ImageView iv_pic = (ImageView) layout.getChildAt(0);
        ImageView iv_close = (ImageView) layout.getChildAt(1);
        Glide.with(this).load(getUrlWithPos(pos)).into(iv_pic);
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

    private String getUrlWithPos(int pos) {
        if (picList.size() == 0) {
            return "";
        }
        String url;
        if (picList.get(pos) instanceof ImageItem) {
            url = ((ImageItem) picList.get(pos)).path;
        } else {
            url = ((com.ypx.imagepicker.bean.ImageItem) picList.get(pos)).getCropUrl();
        }
        return url;
    }
}
