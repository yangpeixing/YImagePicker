package com.ypx.imagepickerdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.YPXImagePicker;
import com.ypx.imagepicker.YPXImagePickerUiBuilder;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.interf.OnImageCropCompleteListener;
import com.ypx.imagepicker.interf.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.ProcessUtil;
import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btn_multiSelect, btn_singleSelect, btn_cropSelect, btn_takePhoto;
    GridLayout gridLayout;
    PicBrowseImageView iv_single;
    List<String> picList = new ArrayList<>();

    int maxNum = 16;
    YPXImagePickerUiBuilder uiBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiBuilder = new YPXImagePickerUiBuilder(this)
                //.withTitleBar(new CustomTitleBar(this))
                .withRowCount(4)
                .withSelectIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .withThemeColor(Color.BLUE)
                .withCameraIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        YPXImagePicker.getInstance().withUiBuilder(uiBuilder);
        btn_multiSelect = findViewById(R.id.btn_multiSelect);
        btn_singleSelect = findViewById(R.id.btn_singleSelect);
        btn_cropSelect = findViewById(R.id.btn_cropSelect);
        gridLayout = findViewById(R.id.gridLayout);
        iv_single = findViewById(R.id.iv_single);
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
                crop();
            }
        });
        btn_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        Log.e("process", "MainActivity: " + ProcessUtil.getAppName(this));
    }

    public void pick(final int selectCount) {
        //  startActivity(new Intent(this, SecondActivity.class));
        ImagePicker.withImageLoader(new GlideImgLoader())
                .themeColor(Color.GREEN)
                .showCamera(false)
                .selectLimit(selectCount)
                .columnCount(4)
                .immersionBar(true)
                // .unSelectIcon(R.mipmap.ic_launcher)
                .pick(MainActivity.this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> items) {
                        if (selectCount == 1) {
                            iv_single.setVisibility(View.VISIBLE);
                            gridLayout.setVisibility(View.GONE);
                            new GlideImgLoader().onPresentImage(iv_single, items.get(0).path, 0);
                            return;
                        }
                        iv_single.setVisibility(View.GONE);
                        if (items != null && items.size() > 0) {
                            for (ImageItem item : items) {
                                picList.add(item.path);
                                refreshGridLayout();
                            }
                        }
                    }
                });
    }

    public void crop() {
        YPXImagePicker.getInstance()
                .withImgLoader(new GlideImgLoader())
                .withShowCamera(true)
                .crop(MainActivity.this, new OnImageCropCompleteListener() {
                    @Override
                    public void onImageCropComplete(String url, Bitmap bmp, float ratio) {
                        iv_single.setVisibility(View.VISIBLE);
                        gridLayout.setVisibility(View.GONE);
                        iv_single.setImageBitmap(bmp);
                    }
                });
    }

    public void takePhoto() {
        YPXImagePicker.getInstance()
                .takePhoto(MainActivity.this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> items) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case YPXImagePicker.REQ_PICKRESULT:
                    // List<ImageItem> imageItems=YPXImagePicker.getInstance().getSelectedImages();
                    Bundle bundle0 = data.getExtras();
                    assert bundle0 != null;
                    ArrayList<ImageItem> imageItems = (ArrayList<ImageItem>) bundle0.getSerializable(YPXImagePicker.KEY_PICKIMAGELIST);
                    iv_single.setVisibility(View.VISIBLE);
                    gridLayout.setVisibility(View.GONE);
                    if (imageItems != null && imageItems.size() > 0) {
                        new GlideImgLoader().onPresentImage(iv_single, imageItems.get(0).path, 0);
                    }
//                    iv_single.setVisibility(View.GONE);
//                    if (imageItems != null && imageItems.size() > 0) {
//                        for (ImageItem item : imageItems) {
//                            picList.add(item.path);
//                            refreshGridLayout();
//                        }
//                    }
                    break;
            }
        }
    }

}
