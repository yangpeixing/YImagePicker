package com.ypx.imagepicker.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.config.IImgPickerUIConfig;
import com.ypx.imagepicker.config.ImgPickerSelectConfig;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.ImagePickerData;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.LocalDataSource;
import com.ypx.imagepicker.interf.ImageSelectMode;
import com.ypx.imagepicker.ui.adapter.ImageGridAdapter;
import com.ypx.imagepicker.ui.adapter.ImageSetAdapter;
import com.ypx.imagepicker.utils.StatusBarUtils;
import com.ypx.imagepicker.utils.TakePhotoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yangpeixing on 2018/5/5 16:35
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class YPXImageGridActivity extends FragmentActivity implements OnImagesLoadedListener, View.OnClickListener {
    public static final int REQ_CAMERA = 1431;
    public static final int REQ_PREVIEW = 2347;
    public static final int REQ_CROP = 1432;
    private List<ImageSet> imageSets;
    private List<ImageItem> imageItems;
    private GridView gridView;

    private View v_masker;
    private Button btnDir;
    private TextView tv_time;
    private ImageSetAdapter mImageSetAdapter;
    private ListView lv_imageSets;
    private ImageGridAdapter mAdapter;
    private int currentSetIndex = 0;

    private RelativeLayout top_bar, footer_panel;
    private TextView tv_title;
    private TextView tv_rightBtn;
    private ImageView iv_back;

    // private ImagePickerPresenter pickerConfig;

    private ImgPickerSelectConfig selectConfig;
    private IImgPickerUIConfig uiConfig;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_images_grid);
        dealIntentData();
        if (selectConfig.getSelectMode() == ImageSelectMode.MODE_TAKEPHOTO) {
            takePhoto();
        } else {
            findView();
            setListener();
            initAdapters();
            loadPicData();
        }
    }

    public void takePhoto() {
        TakePhotoUtil.takePhoto(this, REQ_CAMERA);
        mCurrentPhotoPath = TakePhotoUtil.mCurrentPhotoPath;
    }


    /**
     * 接收传参
     */
    private void dealIntentData() {
        selectConfig = (ImgPickerSelectConfig) getIntent().getSerializableExtra("ImgPickerSelectConfig");
        uiConfig = (IImgPickerUIConfig) getIntent().getSerializableExtra("IImgPickerUIConfig");
    }

    /**
     * 初始化控件
     */
    private void findView() {
        v_masker = findViewById(R.id.v_masker);
        btnDir = (Button) findViewById(R.id.btn_dir);
        gridView = (GridView) findViewById(R.id.gridview);
        lv_imageSets = (ListView) findViewById(R.id.lv_imagesets);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setVisibility(View.GONE);
        setTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageItems != null && mAdapter != null) {
            mAdapter.refreshData(imageItems);
            resetBtnOKstate();
        }
    }

    private void setTitleBar() {
        top_bar = (RelativeLayout) findViewById(R.id.top_bar);
        footer_panel = (RelativeLayout) findViewById(R.id.footer_panel);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_rightBtn = (TextView) findViewById(R.id.tv_rightBtn);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        if (uiConfig.isImmersionBar() && uiConfig.getTopBarBackgroundColor() != 0) {
            StatusBarUtils.setWindowStatusBarColor(this, uiConfig.getTopBarBackgroundColor());
        }
        if (uiConfig.getBackIconID() != 0) {
            iv_back.setImageDrawable(getResources().getDrawable(uiConfig.getBackIconID()));
            iv_back.setColorFilter(Color.WHITE);
        }

        if (uiConfig.getTopBarBackgroundColor() != 0) {
            top_bar.setBackgroundColor(uiConfig.getTopBarBackgroundColor());
        }

        if (uiConfig.getGridViewBackgroundColor() != 0) {
            gridView.setBackgroundColor(uiConfig.getGridViewBackgroundColor());
        }

        if (uiConfig.getBottomBarBackgroundColor() != 0) {
            footer_panel.setBackgroundColor(uiConfig.getBottomBarBackgroundColor());
        }

        if (uiConfig.getRightBtnBackground() != null) {
            tv_rightBtn.setBackground(uiConfig.getRightBtnBackground());
        }

        if (uiConfig.getTitleColor() != 0) {
            tv_title.setTextColor(uiConfig.getTitleColor());
        }

        tv_title.setGravity(Gravity.CENTER | uiConfig.getTopBarTitleGravity());
        tv_rightBtn.setTextColor(uiConfig.getThemeColor());
        resetBtnOKstate();
    }

    /**
     * 初始化监听
     */
    private void setListener() {
        btnDir.setOnClickListener(this);
        v_masker.setOnClickListener(this);
        tv_rightBtn.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        lv_imageSets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectImageSet(position);
            }
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    tv_time.setVisibility(View.GONE);
                    tv_time.setAnimation(AnimationUtils.loadAnimation(YPXImageGridActivity.this, R.anim.abc_fade_out));
                } else {
                    tv_time.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (imageItems != null)
                    try {
                        tv_time.setText(imageItems.get(firstVisibleItem).getTimeFormat());
                    } catch (IndexOutOfBoundsException ig) {

                    }
            }
        });
    }


    /**
     * 初始化相关adapter
     */
    private void initAdapters() {
        mImageSetAdapter = new ImageSetAdapter(this, selectConfig, uiConfig);
        mImageSetAdapter.refreshData(imageSets);
        lv_imageSets.setAdapter(mImageSetAdapter);

        mAdapter = new ImageGridAdapter(this, new ArrayList<ImageItem>(), selectConfig, uiConfig);
        gridView.setAdapter(mAdapter);
        gridView.setNumColumns(selectConfig.getColumnCount());
    }

    /**
     * 异步加载图片数据
     * select all images from local database
     */
    public void loadPicData() {
        DataSource dataSource = new LocalDataSource(this);
        dataSource.provideMediaItems(this);
    }

    /**
     * 选择图片文件夹
     *
     * @param position 位置
     */
    private void selectImageSet(final int position) {
        this.currentSetIndex = position;
        this.imageItems = imageSets.get(position).imageItems;
        ImagePickerData.setCurrentImageSet(imageSets.get(position));
        showOrHideImageSetList();
        mImageSetAdapter.setSelectIndex(currentSetIndex);
        btnDir.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageSet imageSet = imageSets.get(position);
                if (null != imageSet) {
                    mAdapter.refreshData(imageSet.imageItems);
                    btnDir.setText(imageSet.name);
                }
                gridView.smoothScrollToPosition(0);
            }
        }, 100);
    }

    /**
     * 显示或隐藏图片文件夹选项列表
     */
    private void showOrHideImageSetList() {
        if (lv_imageSets.getVisibility() == View.GONE) {
            v_masker.setVisibility(View.VISIBLE);
            lv_imageSets.setVisibility(View.VISIBLE);
            lv_imageSets.setAnimation(AnimationUtils.loadAnimation(YPXImageGridActivity.this, R.anim.ypx_show_from_bottom));
            int index = mImageSetAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            lv_imageSets.setSelection(index);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lv_imageSets.getLayoutParams();
            if (params != null) {
                if (imageSets.size() > 5) {
                    params.height = (int) (getResources().getDisplayMetrics().heightPixels / 1.6f);
                } else {
                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                lv_imageSets.setLayoutParams(params);
            }

        } else {
            v_masker.setVisibility(View.GONE);
            lv_imageSets.setVisibility(View.GONE);
            lv_imageSets.setAnimation(AnimationUtils.loadAnimation(YPXImageGridActivity.this, R.anim.ypx_hide2bottom));
        }
    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        this.imageSets = imageSetList;
        this.imageItems = imageSetList.get(currentSetIndex).imageItems;
        ImagePickerData.setCurrentImageSet(imageSetList.get(currentSetIndex));
        btnDir.setText(imageSetList.get(currentSetIndex).name);
        mAdapter.refreshData(imageItems);
        mImageSetAdapter.refreshData(imageSetList);
    }

    @Override
    public void finish() {
        ImagePicker.clear();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (v == btnDir || v == v_masker) {
            showOrHideImageSetList();
        } else if (v == tv_rightBtn) {
            if (ImagePickerData.getSelectImgs().size() == 0) {
                uiConfig.tip(this, "请至少选择一张图片");
                return;
            }

            ImagePicker.notifyOnImagePickComplete(ImagePickerData.getSelectImgs());
            finish();
        } else if (v == iv_back) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_PREVIEW) {
            tv_rightBtn.performClick();
        } else if (resultCode == RESULT_OK && requestCode == REQ_CAMERA) {//拍照返回
            if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                refreshGalleryAddPic();
                ImageItem item = new ImageItem(mCurrentPhotoPath, "", -1);
                List<ImageItem> list = new ArrayList<>();
                list.add(item);
                ImagePicker.notifyOnImagePickComplete(list);
                finish();
            }
        } else if (resultCode == RESULT_OK && requestCode == REQ_CROP) {//拍照返回
            finish();
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

    /**
     * 图片单机事件
     *
     * @param item     点击的item
     * @param position 点击的位置
     */
    public void onImageClickListener(ImageItem item, int position) {
        gridView.setTag(item);
        Intent intent;
        switch (selectConfig.getSelectMode()) {
            //多选情况下，点击跳转预览
            case ImageSelectMode.MODE_MULTI:
                ImageSet imageSet = imageSets.get(currentSetIndex);
//                List<ImageItem> list=new ArrayList<>();
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                list.addAll(imageSet.imageItems);
//                imageSet.imageItems.addAll(list);
                ImagePickerData.setCurrentImageSet(imageSet);
                intent = new Intent(this, YPXImagePreviewActivity.class);
                intent.putExtra("ImgPickerSelectConfig", selectConfig);
                intent.putExtra("IImgPickerUIConfig", uiConfig);
                intent.putExtra("selectIndex", position);
                intent.putExtra("ImageSet", imageSet);
                startActivityForResult(intent, REQ_PREVIEW);
                break;
            //单选情况下，点击直接返回
            case ImageSelectMode.MODE_SINGLE:
                List<ImageItem> list2 = new ArrayList<>();
                list2.add(item);
                ImagePicker.notifyOnImagePickComplete(list2);
                finish();
                break;
            //剪裁情况下，点击跳转剪裁
            case ImageSelectMode.MODE_CROP:
                intent = new Intent(this, YPXImageCropActivity.class);
                intent.putExtra("IImgPickerUIConfig", uiConfig);
                intent.putExtra("imagePath", item.path);
                startActivityForResult(intent,REQ_CROP);
                break;
        }
    }

    public void imageSelectChange(ImageItem imageItem, boolean isChecked) {
        if (isChecked) {
            ImagePickerData.addImageItem(imageItem);
        } else {
            ImagePickerData.removeImageItem(imageItem);
        }
        resetBtnOKstate();
    }

    private void resetBtnOKstate() {
        if (ImagePickerData.getSelectImgs().size() > 0) {
            tv_rightBtn.setClickable(true);
            tv_rightBtn.setEnabled(true);
            tv_rightBtn.setAlpha(1f);
            tv_rightBtn.setText(getResources().getString(R.string.select_complete,
                    new Object[]{ImagePickerData.getSelectImgs().size(), selectConfig.getSelectLimit()}));
        } else {
            tv_rightBtn.setAlpha(0.6f);
            tv_rightBtn.setText(getResources().getString(R.string.complete));
            tv_rightBtn.setClickable(false);
            tv_rightBtn.setEnabled(false);
        }
    }
}
