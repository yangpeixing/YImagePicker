package com.ypx.imagepicker.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.config.ImagePickerConfig;
import com.ypx.imagepicker.data.DataSource;
import com.ypx.imagepicker.data.ImagePickerData;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.LocalDataSource;
import com.ypx.imagepicker.interf.ImageSelectMode;
import com.ypx.imagepicker.ui.adapter.ImageGridAdapter;
import com.ypx.imagepicker.ui.adapter.ImageSetAdapter;
import com.ypx.imagepicker.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：yangpeixing on 2018/5/5 16:35
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class YPXImageGridActivity2 extends FragmentActivity implements OnImagesLoadedListener, View.OnClickListener {
    public static final int REQ_CAMERA = 1431;
    public static final int REQ_PREVIEW = 2347;
    private List<ImageSet> imageSets;
    private List<ImageItem> imageItems;
    private GridView gridView;
    private TextView tv_title_count, tv_time;
    private ImagePickerConfig pickerConfig;
    private TextView btn_ok;
    private ImageView btn_backpress;
    private View v_masker;
    private Button btnDir;
    private ImageSetAdapter mImageSetAdapter;
    private ListView lv_imageSets;
    private ImageGridAdapter mAdapter;
    private int currentSetIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ypx_activity_images_grid);
        dealIntentData();
        findView();
        setListener();
        initAdapters();
        loadPicData();
    }

    /**
     * 接收传参
     */
    private void dealIntentData() {
        pickerConfig = (ImagePickerConfig) getIntent().getSerializableExtra("ImagePickerConfig");
    }

    /**
     * 初始化控件
     */
    private void findView() {
        v_masker = findViewById(R.id.v_masker);
        btnDir = (Button) findViewById(R.id.btn_dir);
        gridView = (GridView) findViewById(R.id.gridview);
        lv_imageSets = (ListView) findViewById(R.id.lv_imagesets);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_backpress = (ImageView) findViewById(R.id.btn_backpress);
        tv_title_count = (TextView) findViewById(R.id.tv_title_count);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setVisibility(View.GONE);
        btn_backpress.setColorFilter(Color.WHITE);
        if (pickerConfig.isImmersionBar()) {
            StatusBarUtils.setWindowStatusBarColor(this, Color.parseColor("#303030"));
        }
        btn_ok.setTextColor(pickerConfig.getThemeColor());
        resetBtnOKstate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageItems != null && mAdapter != null) {
            mAdapter.refreshData(imageItems);
            resetBtnOKstate();
        }
    }

    /**
     * 初始化监听
     */
    private void setListener() {
        btnDir.setOnClickListener(this);
        v_masker.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_backpress.setOnClickListener(this);
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
                    tv_time.setAnimation(AnimationUtils.loadAnimation(YPXImageGridActivity2.this, R.anim.abc_fade_out));
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
        mImageSetAdapter = new ImageSetAdapter(this, pickerConfig);
        mImageSetAdapter.refreshData(imageSets);
        lv_imageSets.setAdapter(mImageSetAdapter);

        mAdapter = new ImageGridAdapter(this, new ArrayList<ImageItem>(), pickerConfig);
        gridView.setAdapter(mAdapter);
        gridView.setNumColumns(pickerConfig.getColumnCount());
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
            lv_imageSets.setAnimation(AnimationUtils.loadAnimation(YPXImageGridActivity2.this, R.anim.ypx_show_from_bottom));
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
            lv_imageSets.setAnimation(AnimationUtils.loadAnimation(YPXImageGridActivity2.this, R.anim.ypx_hide2bottom));
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
        } else if (v == btn_ok) {
            if (ImagePickerData.getSelectImgs().size() == 0) {
                Toast.makeText(this, "请至少选择一张图片", Toast.LENGTH_SHORT).show();
                return;
            }

            ImagePicker.notifyOnImagePickComplete(ImagePickerData.getSelectImgs());
            finish();
        } else if (v == btn_backpress) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_PREVIEW) {
            btn_ok.performClick();
        }
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
        switch (pickerConfig.getSelectMode()) {
            //多选情况下，点击跳转预览
            case ImageSelectMode.MODE_MULTI:
                intent = new Intent(this, YPXImagePreviewActivity2.class);
                intent.putExtra("ImagePickerConfig", pickerConfig);
                intent.putExtra("selectIndex", position);
                startActivityForResult(intent, REQ_PREVIEW);
                break;
            //单选情况下，点击直接返回
            case ImageSelectMode.MODE_SINGLE:
                List<ImageItem> list = new ArrayList<>();
                list.add(item);
                ImagePicker.notifyOnImagePickComplete(list);
                finish();
                break;
            //剪裁情况下，点击跳转剪裁
            case ImageSelectMode.MODE_CROP:
                intent = new Intent(this, YPXImageCropActivity.class);
                intent.putExtra("key_pic_path", item);
                startActivity(intent);
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
            btn_ok.setClickable(true);
            btn_ok.setEnabled(true);
            btn_ok.setAlpha(0.6f);
            btn_ok.setText(getResources().getString(R.string.select_complete,
                    new Object[]{ImagePickerData.getSelectImgs().size(), pickerConfig.getSelectLimit()}));
        } else {
            btn_ok.setAlpha(0.4f);
            btn_ok.setText(getResources().getString(R.string.complete));
            btn_ok.setClickable(false);
            btn_ok.setEnabled(false);
        }
    }
}
