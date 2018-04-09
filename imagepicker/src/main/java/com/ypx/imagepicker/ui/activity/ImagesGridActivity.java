package com.ypx.imagepicker.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.AndroidImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.ui.ImagesGridFragment;


public class ImagesGridActivity extends FragmentActivity
        implements View.OnClickListener
        , AndroidImagePicker.OnImageSelectedChangeListener {
    ImagesGridFragment mFragment;
    AndroidImagePicker androidImagePicker;
    String imagePath;
    View v_masker;
    private TextView mBtnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        setContentView(R.layout.ipk_activity_images_grid);

        androidImagePicker = AndroidImagePicker.getInstance();
        //most of the time you need to clear the last selected images or you can comment out this line
        //大多数时候需要清除老数据
        androidImagePicker.clearSelectedImages();

        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        v_masker = findViewById(R.id.v_masker);
        mBtnOk.setOnClickListener(this);

        if (androidImagePicker.getSelectMode() == AndroidImagePicker.Select_Mode.MODE_SINGLE) {
            mBtnOk.setVisibility(View.GONE);
        } else {
            mBtnOk.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.btn_backpress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //final boolean isCrop = getIntent().getBooleanExtra("isCrop",false);
        final boolean isCrop = androidImagePicker.cropMode;
        imagePath = getIntent().getStringExtra(AndroidImagePicker.KEY_PIC_PATH);
        mFragment = new ImagesGridFragment();
        mFragment.setOnImageItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = androidImagePicker.isShouldShowCamera() ? position - 1 : position;

                if (androidImagePicker.getSelectMode() == AndroidImagePicker.Select_Mode.MODE_MULTI) {
                    go2Preview(position);
                } else if (androidImagePicker.getSelectMode() == AndroidImagePicker.Select_Mode.MODE_SINGLE) {
                    if (isCrop) {
                        Intent intent = new Intent();
                        intent.setClass(ImagesGridActivity.this, ImageCropActivity.class);
                        intent.putExtra(AndroidImagePicker.KEY_PIC_PATH, androidImagePicker.getImageItemsOfCurrentImageSet().get(position).path);
                        startActivity(intent);
                    } else {
                        androidImagePicker.clearSelectedImages();
                        androidImagePicker.addSelectedImageItem(position, androidImagePicker.getImageItemsOfCurrentImageSet().get(position));
                        //  setResult(RESULT_OK);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("imageItem", androidImagePicker.getImageItemsOfCurrentImageSet().get(position));
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                        androidImagePicker.notifyOnImagePickComplete();
                    }
                }

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();

        androidImagePicker.addOnImageSelectedChangeListener(this);

        int selectedCount = androidImagePicker.getSelectImageCount();
        onImageSelectChange(0, null, selectedCount, androidImagePicker.getSelectLimit());

    }

    /**
     * 预览页面
     *
     * @param position 索引
     */
    private void go2Preview(int position) {
        Intent intent = new Intent();
        intent.putExtra(AndroidImagePicker.KEY_PIC_SELECTED_POSITION, position);
        intent.setClass(ImagesGridActivity.this, ImagePreviewActivity.class);
        startActivityForResult(intent, AndroidImagePicker.REQ_PREVIEW);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_ok) {
            finish();
            androidImagePicker.notifyOnImagePickComplete();
        } else if (v.getId() == R.id.btn_pic_rechoose) {
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        androidImagePicker.removeOnImageItemSelectedChangeListener(this);
        androidImagePicker.clearImageSets();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == AndroidImagePicker.REQ_PREVIEW) {
                setResult(RESULT_OK);
                finish();
                androidImagePicker.notifyOnImagePickComplete();
            }

        }

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

    public void showMasker() {
        if (v_masker != null) {
            v_masker.setVisibility(View.VISIBLE);
        }
    }

    public void hideMasker() {
        if (v_masker != null) {
            v_masker.setVisibility(View.GONE);
        }
    }
}
