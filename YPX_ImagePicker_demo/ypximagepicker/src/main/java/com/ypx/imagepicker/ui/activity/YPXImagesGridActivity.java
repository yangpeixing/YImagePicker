package com.ypx.imagepicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.YPXImagePicker;
import com.ypx.imagepicker.YPXImagePickerUiBuilder;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.LocalDataSource;
import com.ypx.imagepicker.interf.ImageSelectMode;
import com.ypx.imagepicker.interf.OnImageCropCompleteListener;
import com.ypx.imagepicker.interf.OnImageSelectedChangeListener;
import com.ypx.imagepicker.ui.adapter.ImageGridAdapter;
import com.ypx.imagepicker.ui.adapter.ImageSetAdapter;
import com.ypx.imagepicker.ui.view.DefaultTitleBar;
import com.ypx.imagepicker.utils.CornerUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class YPXImagesGridActivity extends FragmentActivity implements OnImageSelectedChangeListener, OnImagesLoadedListener, OnImageCropCompleteListener {
    public static final int REQ_CAMERA = 1431;
    public static final int REQ_PREVIEW = 2347;
    private YPXImagePicker androidImagePicker;
    private View v_masker;
    private Button btnDir;
    private GridView mGridView;
    private List<ImageSet> mImageSetList;//data of all ImageSets
    private ImageGridAdapter mAdapter;
    private ImageSetAdapter mImageSetAdapter;
    private ListView lv_imageSets;
    private String mCurrentPhotoPath;
    private YPXImagePickerUiBuilder uiBuilder;

    private TextView tv_complete;
    private ImageView iv_back;
    private TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidImagePicker = YPXImagePicker.getInstance();
        uiBuilder = androidImagePicker.getUiBuilder();
        if (uiBuilder == null) {
            uiBuilder = new YPXImagePickerUiBuilder(this);
        }
        //大多数时候需要清除老数据
        androidImagePicker.clearSelectedImages();
        androidImagePicker.addOnImageCropCompleteListener(this);
        androidImagePicker.addOnImageSelectedChangeListener(this);
        if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_TAKEPHOTO) {
            takePhoto();
        } else {
            setContentView(R.layout.ypx_activity_images_grid);
            initTitleBar();
            initView();
            setListener();
            loadPicData();
            initAdapter();
        }
    }

    /**
     * 异步加载图片数据
     */
    public void loadPicData() {
        DataSource dataSource = new LocalDataSource(this);
        dataSource.provideMediaItems(this);//select all images from local database
    }

    /**
     * 初始化页面
     */
    public void initView() {
        v_masker = findViewById(R.id.v_masker);
        btnDir = (Button) findViewById(R.id.btn_dir);
        mGridView = (GridView) findViewById(R.id.gridview);
        lv_imageSets = (ListView) findViewById(R.id.lv_imagesets);
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        RelativeLayout top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        top_bar.removeAllViews();
        DefaultTitleBar titleBar = new DefaultTitleBar(this);
        top_bar.addView(titleBar);
        tv_complete = titleBar.getCompleteTextView();
        iv_back = titleBar.getLeftIconImageView();
        tv_title = titleBar.getTitleTextView();
        tv_complete.setBackground(CornerUtils.halfAlphaSelector(titleBar.dp(3), uiBuilder.getThemeColor()));
        iv_back.setColorFilter(uiBuilder.getThemeColor());
        tv_complete.setText("完成");
        tv_title.setText("选择图片");
        tv_complete.setAlpha(0.5f);
        tv_complete.setClickable(false);
        tv_complete.setEnabled(false);
        if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_SINGLE
                || androidImagePicker.getSelectMode() == ImageSelectMode.MODE_CROP) {
            tv_complete.setVisibility(View.GONE);
        } else {
            tv_complete.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 初始化adapter
     */
    private void initAdapter() {
        mImageSetAdapter = new ImageSetAdapter(this,null);
        mImageSetAdapter.refreshData(mImageSetList);
        lv_imageSets.setAdapter(mImageSetAdapter);
        mAdapter = new ImageGridAdapter(this, new ArrayList<ImageItem>(), null);
        mGridView.setAdapter(mAdapter);
        mGridView.setNumColumns(uiBuilder.getRowCount());
    }

    /**
     * 初始化监听
     */
    public void setListener() {
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (androidImagePicker.getSelectImageCount() == 0) {
                    Toast.makeText(YPXImagesGridActivity.this, "请至少选择一张照片!", Toast.LENGTH_SHORT).show();
                    return;
                }
                finishWithResult();
                androidImagePicker.notifyOnImagePickComplete();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        v_masker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDir.performClick();
            }
        });

        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lv_imageSets.getVisibility() == View.GONE) {
                    v_masker.setVisibility(View.VISIBLE);
                    lv_imageSets.setVisibility(View.VISIBLE);
                    lv_imageSets.setAnimation(AnimationUtils.loadAnimation(YPXImagesGridActivity.this, R.anim.ypx_show_from_bottom));
                    int index = mImageSetAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    lv_imageSets.setSelection(index);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv_imageSets.getLayoutParams();
                    if (params != null) {
                        if (mImageSetList.size() > 5) {
                            params.height = (int) (getResources().getDisplayMetrics().heightPixels / 1.6f);
                        } else {
                            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                        }
                        lv_imageSets.setLayoutParams(params);
                    }

                } else {
                    v_masker.setVisibility(View.GONE);
                    lv_imageSets.setVisibility(View.GONE);
                    lv_imageSets.setAnimation(AnimationUtils.loadAnimation(YPXImagesGridActivity.this, R.anim.ypx_hide2bottom));
                }
            }
        });

        lv_imageSets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                btnDir.performClick();
                mImageSetAdapter.setSelectIndex(i);
                androidImagePicker.setCurrentSelectedImageSetPosition(i);
                final int index = i;
                final AdapterView tempAdapterView = parent;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ImageSet imageSet = (ImageSet) tempAdapterView.getAdapter().getItem(index);
                        if (null != imageSet) {
                            mAdapter.refreshData(imageSet.imageItems);
                            btnDir.setText(imageSet.name);
                        }
                        // scroll to the top
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if (tv_complete == null) {
            return;
        }
        if (selectedItemsCount > 0) {
            tv_complete.setClickable(true);
            tv_complete.setEnabled(true);
            tv_complete.setAlpha(1f);
            tv_complete.setText(getResources().getString(R.string.select_complete, new Object[]{selectedItemsCount, maxSelectLimit}));
        } else {
            tv_complete.setAlpha(0.5f);
            tv_complete.setText(getResources().getString(R.string.complete));
            tv_complete.setClickable(false);
            tv_complete.setEnabled(false);
        }
        mAdapter.refreshData(YPXImagePicker.getInstance().getImageItemsOfCurrentImageSet());
    }

    /**
     * 图片单机事件
     *
     * @param item     点击的item
     * @param position 点击的位置
     */
    public void onImageClickListener(ImageItem item, int position) {
        mGridView.setTag(item);
        position = androidImagePicker.isShouldShowCamera() ? position - 1 : position;
        //多选情况下，点击跳转预览
        if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_MULTI) {
            Intent intent = new Intent();
            intent.putExtra("key_pic_selected", position);
            intent.setClass(YPXImagesGridActivity.this, YPXImagePreviewActivity.class);
            startActivityForResult(intent, REQ_PREVIEW);
        }
        //单选情况下，点击直接返回
        else if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_SINGLE) {
            androidImagePicker.clearSelectedImages();
            androidImagePicker.addSelectedImageItem(position, androidImagePicker.getImageItemsOfCurrentImageSet().get(position));
            finishWithResult();
            androidImagePicker.notifyOnImagePickComplete();
        }
        //剪裁情况下，点击跳转剪裁
        else if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_CROP) {
            Intent intent = new Intent();
            intent.setClass(YPXImagesGridActivity.this, YPXImageCropActivity.class);
            intent.putExtra("key_pic_path", androidImagePicker.getImageItemsOfCurrentImageSet().get(position).path);
            startActivity(intent);
        }
    }


    /**
     * 图片加载完成回调
     *
     * @param imageSetList 单文件夹下数据源
     */
    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        mImageSetList = imageSetList;
        btnDir.setText(imageSetList.get(0).name);
        mAdapter.refreshData(imageSetList.get(0).imageItems);
        mImageSetAdapter.refreshData(mImageSetList);
    }

    /**
     * 剪裁完成直接销毁
     *
     * @param url   剪裁的图片url
     * @param bmp   剪裁的bitmap
     * @param ratio 剪裁的比例
     */
    @Override
    public void onImageCropComplete(String url, Bitmap bmp, float ratio) {
        finish();
    }

    /**
     * 通过ActivityResult返回数据
     */
    public void finishWithResult() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(YPXImagePicker.KEY_PICKIMAGELIST, androidImagePicker.getSelectedImages());
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED && requestCode == REQ_CAMERA && tv_complete == null) {
            finish();
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            //预览情况下
            if (requestCode == REQ_PREVIEW) {
                setResult(RESULT_OK);
                finish();
                androidImagePicker.notifyOnImagePickComplete();
            }
            //拍照返回
            else if (requestCode == REQ_CAMERA) {
                if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                    refreshGalleryAddPic();
                    if (androidImagePicker.getSelectMode() == ImageSelectMode.MODE_CROP) {//裁图模式
                        Intent intent = new Intent();
                        intent.setClass(this, YPXImageCropActivity.class);
                        intent.putExtra("key_pic_path", mCurrentPhotoPath);
                        startActivityForResult(intent, REQ_CAMERA);
                    } else {
                        ImageItem item = new ImageItem(mCurrentPhotoPath, "", -1);
                        androidImagePicker.clearSelectedImages();
                        androidImagePicker.addSelectedImageItem(-1, item);
                        androidImagePicker.notifyOnImagePickComplete();
                        finishWithResult();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        androidImagePicker.removeOnImageItemSelectedChangeListener(this);
        androidImagePicker.clearImageSets();
        super.onDestroy();
    }

    /**
     * create a file to save photo
     */
    private File createImageSaveFile() {
        String sdStatus = Environment.getExternalStorageState();
        File tmpFile;
        File cacheDir;
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = Environment.getDataDirectory();
        } else {
            // 已挂载
            cacheDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String fileName = "IMG_" + timeStamp;
        tmpFile = new File(cacheDir, fileName + ".jpg");
        mCurrentPhotoPath = tmpFile.getAbsolutePath();
        return tmpFile;
    }


    /**
     * 调用系统相机拍照
     */
    public void takePhoto() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = createImageSaveFile();
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
            }
            startActivityForResult(takePictureIntent, REQ_CAMERA);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    /**
     * 刷新相册
     */
    public void refreshGalleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }
}
