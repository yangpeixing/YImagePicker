package com.ypx.imagepicker;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.ypx.imagepicker.bean.ImageItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btn_multiSelect, btn_singleSelect, btn_cropSelect;
    GridLayout gridLayout;
    ImageView iv_single;
    List<String> picList = new ArrayList<>();

    int maxNum = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_multiSelect = (Button) findViewById(R.id.btn_multiSelect);
        btn_singleSelect = (Button) findViewById(R.id.btn_singleSelect);
        btn_cropSelect = (Button) findViewById(R.id.btn_cropSelect);
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        iv_single = (ImageView) findViewById(R.id.iv_single);
        btn_multiSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidImagePicker.getInstance().pickMulti(MainActivity.this,
                        false, maxNum - picList.size(), new AndroidImagePicker.OnImagePickCompleteListener() {
                            @Override
                            public void onImagePickComplete(List<ImageItem> items) {
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
        });
        btn_singleSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidImagePicker.getInstance().pickSingle(MainActivity.this, false, new AndroidImagePicker.OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(List<ImageItem> items) {
                        iv_single.setVisibility(View.VISIBLE);
                        gridLayout.setVisibility(View.GONE);
                        new GlideImgLoader().onPresentImage(iv_single, items.get(0).path, 0);
                    }
                });
            }
        });

        btn_cropSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidImagePicker.getInstance().pickAndCrop(MainActivity.this, false, 30, new AndroidImagePicker.OnImageCropCompleteListener() {
                    @Override
                    public void onImageCropComplete(String url, Bitmap bmp, float ratio) {
                        iv_single.setVisibility(View.VISIBLE);
                        gridLayout.setVisibility(View.GONE);
                        iv_single.setImageBitmap(bmp);
                    }
                });
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

}
