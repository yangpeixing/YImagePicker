package com.ypx.imagepickerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import com.ypx.imagepicker.CropImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.wximagepicker.YPXImagePicker;
import com.ypx.wximagepicker.bean.SimpleImageItem;
import com.ypx.wximagepicker.interf.OnImagePickCompleteListener;
import com.ypx.wximagepicker.widget.browseimage.PicBrowseImageView;
import com.ypx.imagepickerdemo.style.JHLImgPickerUIConfig;
import com.ypx.imagepickerdemo.style.RedBookDataBingProvider;
import com.ypx.imagepickerdemo.style.WXImgPickerUIConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_multiSelect, btn_singleSelect, btn_cropSelect, btn_takePhoto;
    GridLayout gridLayout;
    PicBrowseImageView iv_single;
    CheckBox cb_redBook, cb_jhl, cb_wx, cb_showCamera, cb_showVideo;
    List<Object> picList = new ArrayList<>();

    int maxNum = 16;
    WXImgPickerUIConfig wxImgPickerUIConfig;
    JHLImgPickerUIConfig jhlImgPickerUIConfig;

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
        wxImgPickerUIConfig = new WXImgPickerUIConfig();
        jhlImgPickerUIConfig = new JHLImgPickerUIConfig();
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
        CropImagePicker.create(new RedBookDataBingProvider())
                //.setFirstImageItem(mList.size() > 0 ? mList.get(0) : null)
                .setFirstImageUrl(getUrlWithPos(0))
                .setMaxCount(count)
                .showBottomView(true)
                .showVideo(cb_showVideo.isChecked())
                .showCamera(cb_showCamera.isChecked())
                .setCropPicSaveFilePath(Environment.getExternalStorageDirectory().toString() +
                        File.separator + "MarsCrop" + File.separator)
                .pick(this, new com.ypx.imagepicker.data.OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }

    private void wxPick(int count) {
        YPXImagePicker.with(cb_jhl.isChecked() ? jhlImgPickerUIConfig : wxImgPickerUIConfig)
                .showCamera(cb_showCamera.isChecked())
                .selectLimit(count)
                .columnCount(4)
                .canEditPic(true)
                .showOriginalCheckBox(true)
                .pick(MainActivity.this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<SimpleImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }

    private void crop() {
        picList.clear();
        YPXImagePicker.with(cb_jhl.isChecked() ? jhlImgPickerUIConfig : wxImgPickerUIConfig)
                .showCamera(cb_showCamera.isChecked())
                .columnCount(4)
                .canEditPic(true)
                .showOriginalCheckBox(true)
                .crop(MainActivity.this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<SimpleImageItem> imageItems) {
                        if (imageItems != null && imageItems.size() > 0) {
                            picList.addAll(imageItems);
                            refreshGridLayout();
                        }
                    }
                });
    }

    public void takePhoto() {
        picList.clear();
        YPXImagePicker.with(wxImgPickerUIConfig).takePhoto(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(List<SimpleImageItem> imageItems) {
                if (imageItems != null && imageItems.size() > 0) {
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
        new GlideImgLoader().onPresentImage(iv_pic, getUrlWithPos(pos), 0);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picList.remove(pos);
                refreshGridLayout();
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
        if (picList.get(pos) instanceof SimpleImageItem) {
            url = ((SimpleImageItem) picList.get(pos)).path;
        } else {
            url = ((ImageItem) picList.get(pos)).getCropUrl();
        }
        return url;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == CropImagePicker.REQ_PICKER_RESULT_CODE &&
//                data != null && data.hasExtra(CropImagePicker.INTENT_KEY_PICKERRESULT)) {
//            List<ImageItem> imageItems = (List<ImageItem>) data.getSerializableExtra(CropImagePicker.INTENT_KEY_PICKERRESULT);
//            if (imageItems != null && imageItems.size() > 0) {
//                for (ImageItem item : imageItems) {
//                    mList.add(item);
//                    picList.add(item.getCropUrl());
//                    refreshGridLayout();
//                }
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CropImagePicker.clearCropFiles();
    }

}
