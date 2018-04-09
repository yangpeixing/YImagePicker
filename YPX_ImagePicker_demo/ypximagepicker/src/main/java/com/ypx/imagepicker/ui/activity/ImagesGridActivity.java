package com.ypx.imagepicker.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.YPXImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.LocalDataSource;
import com.ypx.imagepicker.imp.ImageSelectMode;
import com.ypx.imagepicker.imp.OnImageCropCompleteListener;
import com.ypx.imagepicker.imp.OnImageSelectedChangeListener;
import com.ypx.imagepicker.ui.adapter.ImageGridAdapter;
import com.ypx.imagepicker.ui.adapter.ImageSetAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ImagesGridActivity extends FragmentActivity implements OnImageSelectedChangeListener, OnImagesLoadedListener, OnImageCropCompleteListener {
    public static final int REQ_CAMERA = 1431;
    public static final int REQ_PREVIEW = 2347;
    private YPXImagePicker androidImagePicker;
    private View v_masker;
    private RelativeLayout mFooterView;
    private Button btnDir;
    private GridView mGridView;
    private List<ImageSet> mImageSetList;//data of all ImageSets
    private ImageGridAdapter mAdapter;
    private ImageSetAdapter mImageSetAdapter;
    private ListPopupWindow mFolderPopupWindow;//ImageSet PopupWindow
    private TextView mBtnOk;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        //大多数时候需要清除老数据
        YPXImagePicker.clearSelectedImages();
        androidImagePicker = YPXImagePicker.getInstance();
        androidImagePicker.addOnImageCropCompleteListener(this);
        androidImagePicker.addOnImageSelectedChangeListener(this);
        if (YPXImagePicker.selectMode == ImageSelectMode.MODE_TAKEPHOTO) {
            takePhoto();
        } else {
            setContentView(R.layout.ipk_activity_images_grid);
            initView();
            setListener();
            loadPicData();
            if (YPXImagePicker.selectMode == ImageSelectMode.MODE_SINGLE) {
                mBtnOk.setVisibility(View.GONE);
            } else {
                mBtnOk.setVisibility(View.VISIBLE);
            }
        }
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
        if (YPXImagePicker.selectMode == ImageSelectMode.MODE_MULTI) {
            Intent intent = new Intent();
            intent.putExtra("key_pic_selected", position);
            intent.setClass(ImagesGridActivity.this, ImagePreviewActivity.class);
            startActivityForResult(intent, REQ_PREVIEW);
        }
        //单选情况下，点击直接返回
        else if (YPXImagePicker.selectMode == ImageSelectMode.MODE_SINGLE) {
            YPXImagePicker.clearSelectedImages();
            androidImagePicker.addSelectedImageItem(position, androidImagePicker.getImageItemsOfCurrentImageSet().get(position));
            finishWithResult();
            androidImagePicker.notifyOnImagePickComplete();
        }
        //剪裁情况下，点击跳转剪裁
        else if (YPXImagePicker.selectMode == ImageSelectMode.MODE_CROP) {
            Intent intent = new Intent();
            intent.setClass(ImagesGridActivity.this, ImageCropActivity.class);
            intent.putExtra("key_pic_path", androidImagePicker.getImageItemsOfCurrentImageSet().get(position).path);
            startActivity(intent);
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
        mBtnOk = (TextView) findViewById(R.id.btn_ok);
        v_masker = findViewById(R.id.v_masker);
        mFooterView = (RelativeLayout) findViewById(R.id.footer_panel);
        btnDir = (Button) findViewById(R.id.btn_dir);
        mGridView = (GridView) findViewById(R.id.gridview);
        mImageSetAdapter = new ImageSetAdapter(this);
        mImageSetAdapter.refreshData(mImageSetList);
        mAdapter = new ImageGridAdapter(this, new ArrayList<ImageItem>(), androidImagePicker);
        mGridView.setAdapter(mAdapter);
    }

    /**
     * 初始化监听
     */
    public void setListener() {
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
                androidImagePicker.notifyOnImagePickComplete();
            }
        });
        findViewById(R.id.btn_backpress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFolderPopupWindow == null) {
                    createPopupFolderList();
                }
                //backgroundAlpha(0.3f);
                showMasker();
                mImageSetAdapter.refreshData(mImageSetList);
                mFolderPopupWindow.setAdapter(mImageSetAdapter);
                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mImageSetAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    if (mFolderPopupWindow.getListView() != null) {
                        mFolderPopupWindow.getListView().setSelection(index);
                    }

                }
            }
        });
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
    protected void onDestroy() {
        androidImagePicker.removeOnImageItemSelectedChangeListener(this);
        YPXImagePicker.clearImageSets();
        super.onDestroy();
    }

    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if (mBtnOk == null) {
            return;
        }
        if (selectedItemsCount > 0) {
            mBtnOk.setEnabled(true);
            mBtnOk.setText(getResources().getString(R.string.select_complete, new Object[]{selectedItemsCount, maxSelectLimit}));
        } else {
            mBtnOk.setText(getResources().getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }
        mAdapter.refreshData(YPXImagePicker.getInstance().getImageItemsOfCurrentImageSet());
    }

    /**
     * 显示阴影
     */
    public void showMasker() {
        if (v_masker != null) {
            v_masker.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏阴影
     */
    public void hideMasker() {
        if (v_masker != null) {
            v_masker.setVisibility(View.GONE);
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
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels;
        mFolderPopupWindow = new ListPopupWindow(this);
        //mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mImageSetAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnchorView(mFooterView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                hideMasker();
            }
        });
        mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mImageSetAdapter.setSelectIndex(i);
                androidImagePicker.setCurrentSelectedImageSetPosition(i);
                final int index = i;
                final AdapterView tempAdapterView = adapterView;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
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
                    //androidImagePicker.notifyPictureTaken();
                    if (YPXImagePicker.selectMode == ImageSelectMode.MODE_CROP) {//裁图模式
                        Intent intent = new Intent();
                        intent.setClass(this, ImageCropActivity.class);
                        intent.putExtra("key_pic_path", mCurrentPhotoPath);
                        startActivityForResult(intent, REQ_CAMERA);
                    } else {
                        ImageItem item = new ImageItem(mCurrentPhotoPath, "", -1);
                        YPXImagePicker.clearSelectedImages();
                        androidImagePicker.addSelectedImageItem(-1, item);
                        androidImagePicker.notifyOnImagePickComplete();
                        finishWithResult();
                    }
                }
            }
        }
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
