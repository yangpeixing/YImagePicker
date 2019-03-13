package com.ypx.imagepickerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ypx.imagepicker.MarsImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.wximagepicker.YPXImagePicker;
import com.ypx.wximagepicker.bean.SimpleImageItem;
import com.ypx.wximagepicker.interf.OnImagePickCompleteListener;
import com.ypx.wximagepicker.utils.ProcessUtil;
import com.ypx.wximagepicker.widget.browseimage.PicBrowseImageView;
import com.ypx.imagepickerdemo.style.JHLImgPickerUIConfig;
import com.ypx.imagepickerdemo.style.RedBookImageLoader;
import com.ypx.imagepickerdemo.style.WXImgPickerUIConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btn_multiSelect, btn_singleSelect, btn_cropSelect, btn_takePhoto;
    GridLayout gridLayout;
    PicBrowseImageView iv_single;
    CheckBox cb_jhl, cb_wx, cb_showCamera;
    List<String> picList = new ArrayList<>();

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
        cb_showCamera = findViewById(R.id.cb_showCamera);
        wxImgPickerUIConfig = new WXImgPickerUIConfig();
        jhlImgPickerUIConfig = new JHLImgPickerUIConfig();
        iv_single.setMaxScale(5.0f);
        iv_single.enable();
        btn_takePhoto = findViewById(R.id.btn_takePhoto);
        btn_multiSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick(9 - picList.size());
            }
        });
        btn_singleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picList.clear();
                pick(1);
            }
        });
        btn_cropSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picList.clear();
                crop();
            }
        });
        btn_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picList.clear();
                takePhoto();
            }
        });
        Log.e("process", "MainActivity: " + ProcessUtil.getAppName(this));

        cb_wx.setChecked(true);
        cb_jhl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb_wx.setChecked(!cb_jhl.isChecked());
            }
        });

        cb_wx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb_jhl.setChecked(!cb_wx.isChecked());
            }
        });
    }


    public void pick(final int selectCount) {
        YPXImagePicker.with(cb_jhl.isChecked() ? jhlImgPickerUIConfig : wxImgPickerUIConfig)
                .showCamera(cb_showCamera.isChecked())
                .selectLimit(selectCount)
                .columnCount(4)
                .canEditPic(true)
                .showOriginalCheckBox(true)
                .pick(MainActivity.this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<SimpleImageItem> items) {
                        if (selectCount == 1) {
                            iv_single.setVisibility(View.VISIBLE);
                            gridLayout.setVisibility(View.GONE);
                            new GlideImgLoader().onPresentImage(iv_single, items.get(0).path, 0);
                            return;
                        }
                        iv_single.setVisibility(View.GONE);
                        gridLayout.setVisibility(View.VISIBLE);
                        if (items != null && items.size() > 0) {
                            for (SimpleImageItem item : items) {
                                picList.add(item.path);
                                refreshGridLayout();
                            }
                        }
                    }
                });
    }

    public void crop() {
        YPXImagePicker.with(cb_jhl.isChecked() ? jhlImgPickerUIConfig : wxImgPickerUIConfig)
                .showCamera(cb_showCamera.isChecked())
                .columnCount(4)
                .canEditPic(true)
                .showOriginalCheckBox(true)
                .crop(MainActivity.this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<SimpleImageItem> items) {
                        iv_single.setVisibility(View.VISIBLE);
                        gridLayout.setVisibility(View.GONE);
                        new GlideImgLoader().onPresentImage(iv_single, items.get(0).path, 0);
                    }
                });
    }

    public void takePhoto() {
//        YPXImagePicker.with(cb_jhl.isChecked() ? jhlImgPickerUIConfig : wxImgPickerUIConfig)
//                .showCamera(false)
//                .columnCount(4)
//                .canEditPic(true)
//                .showOriginalCheckBox(true)
//                .takePhoto(this, new OnImagePickCompleteListener() {
//                    @Override
//                    public void onImagePickComplete(List<SimpleImageItem> items) {
//                        iv_single.setVisibility(View.VISIBLE);
//                        gridLayout.setVisibility(View.GONE);
//                        new GlideImgLoader().onPresentImage(iv_single, items.get(0).path, 0);
//                    }
//                });


        YPXImagePicker.with(wxImgPickerUIConfig).takePhoto(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(List<SimpleImageItem> items) {
                iv_single.setVisibility(View.VISIBLE);
                gridLayout.setVisibility(View.GONE);
                new GlideImgLoader().onPresentImage(iv_single, items.get(0).path, 0);
            }
        });
    }


    /**
     * 刷新图片显示
     */
    public void refreshGridLayout() {
        iv_single.setVisibility(View.GONE);
        gridLayout.setVisibility(View.VISIBLE);
        gridLayout.removeAllViews();
        int num = picList.size();
        int picSize = (getScreenWidth() - dp(20)) / 4;
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
                    btn_multiSelect.performClick();
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
        new GlideImgLoader().onPresentImage(iv_pic, picList.get(pos), 0);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ImageItem imageItem : mList) {
                    if (imageItem.getCropUrl().equals(picList.get(pos))) {
                        mList.remove(imageItem);
                        break;
                    }
                }
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


    List<ImageItem> mList = new ArrayList<>();

    public void redBook(View view) {
        MarsImagePicker.create(new RedBookImageLoader())
                //.setFirstImageItem(mList.size() > 0 ? mList.get(0) : null)
                .setFirstImageUrl(picList.size() > 0 ? picList.get(0) : null)
                .setMaxCount(9 - mList.size())
                .showBottomView(true)
                .setCropPicSaveFilePath(Environment.getExternalStorageDirectory().toString() +
                        File.separator + "MarsCrop" + File.separator)
                .pick(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MarsImagePicker.REQ_PICKER_RESULT_CODE &&
                data != null && data.hasExtra(MarsImagePicker.INTENT_KEY_PICKERRESULT)) {
            List<ImageItem> imageItems= (List<ImageItem>) data.getSerializableExtra(MarsImagePicker.INTENT_KEY_PICKERRESULT);
            if (imageItems != null && imageItems.size() > 0) {
                for (ImageItem item : imageItems) {
                    mList.add(item);
                    picList.add(item.getCropUrl());
                    refreshGridLayout();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MarsImagePicker.clearCropFiles();
    }
}
