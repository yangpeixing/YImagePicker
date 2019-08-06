package com.ypx.imagepicker.activity.multi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.adapter.multi.MultiSetAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.MultiUiConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.MediaDataSource;
import com.ypx.imagepicker.helper.launcher.ActivityLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PermissionUtils;
import com.ypx.imagepicker.utils.StatusBarUtil;
import com.ypx.imagepicker.utils.TakePhotoUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.REQ_STORAGE;

/**
 * Description: 多选页
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiImagePickerActivity extends FragmentActivity implements OnImagesLoadedListener, View.OnClickListener {

    public static final String INTENT_KEY_SELECT_CONFIG = "MultiSelectConfig";
    public static final String INTENT_KEY_UI_CONFIG = "IMultiPickerBindPresenter";
    public static final String INTENT_KEY_CURRENT_INDEX = "currentIndex";
    public static final String INTENT_KEY_CURRENT_IMAGE = "currentImage";

    public static final int REQ_CAMERA = 1431;

    private List<ImageSet> imageSets;
    private List<ImageItem> imageItems;
    private RecyclerView mRecyclerView;

    private View v_masker;
    private Button btnDir;
    private TextView mTvTime;
    private MultiSetAdapter mImageSetAdapter;
    private ListView mImageSetListView;
    private MultiGridAdapter mAdapter;
    private int currentSetIndex = 0;

    private TextView mTvPreview;
    private TextView mTvRight;

    private MultiSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private MultiUiConfig multiUiConfig;
    private String mCurrentPhotoPath;

    public static void intent(Activity activity,
                              MultiSelectConfig selectConfig,
                              IMultiPickerBindPresenter presenter,
                              final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, MultiImagePickerActivity.class);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_UI_CONFIG, presenter);
        ActivityLauncher.init(activity).startActivityForResult(intent, new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKERRESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKERRESULT);
                    listener.onImagePickComplete(list);
                    MultiPickerData.instance.clear();
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_images_grid);
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_SELECT_CONFIG)
                || !getIntent().hasExtra(INTENT_KEY_UI_CONFIG)) {
            finish();
            return;
        }
        selectConfig = (MultiSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_UI_CONFIG);
        if (selectConfig == null || presenter == null) {
            finish();
            return;
        }

        if (selectConfig.getLastImageList() != null && selectConfig.getLastImageList().size() > 0) {
            MultiPickerData.instance.addAllImageItems(selectConfig.getLastImageList());
        }

        multiUiConfig = presenter.getUiConfig(this);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
            }
        } else {
            TakePhotoUtil.mCurrentPhotoPath = "";
            TakePhotoUtil.takePhoto(this, REQ_CAMERA);
            mCurrentPhotoPath = TakePhotoUtil.mCurrentPhotoPath;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MultiPickerData.instance.clear();
    }

    /**
     * 初始化控件
     */
    private void findView() {
        v_masker = findViewById(R.id.v_masker);
        btnDir = findViewById(R.id.btn_dir);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mImageSetListView = findViewById(R.id.lv_imagesets);
        mTvTime = findViewById(R.id.tv_time);
        mTvTime.setVisibility(View.GONE);
        setTitleBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (imageItems != null && mAdapter != null) {
            mAdapter.refreshData(imageItems);
            resetBtnOKBtn();
        }
    }

    private void setTitleBar() {
        RelativeLayout top_bar = findViewById(R.id.top_bar);
        RelativeLayout footer_panel = findViewById(R.id.footer_panel);
        TextView tv_title = findViewById(R.id.tv_title);
        mTvRight = findViewById(R.id.tv_rightBtn);
        ImageView iv_back = findViewById(R.id.iv_back);
        mTvPreview = findViewById(R.id.tv_preview);
        mTvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty()) {
                    return;
                }
                MultiImagePreviewActivity.preview(MultiImagePickerActivity.this,
                        selectConfig, presenter,
                        true,
                        MultiPickerData.instance.getSelectImageList(),
                        0,
                        new OnImagePickCompleteListener() {
                            @Override
                            public void onImagePickComplete(ArrayList<ImageItem> items) {
                                notifyOnImagePickComplete(items);
                            }
                        });
            }
        });
        if (multiUiConfig.isImmersionBar() && multiUiConfig.getTopBarBackgroundColor() != 0) {
            StatusBarUtil.setStatusBar(this, multiUiConfig.getTopBarBackgroundColor(), false,
                    StatusBarUtil.isDarkColor(multiUiConfig.getTopBarBackgroundColor()));
        }

        if (multiUiConfig.getBackIconID() != 0) {
            iv_back.setImageDrawable(getResources().getDrawable(multiUiConfig.getBackIconID()));
        }

        if (multiUiConfig.getBackIconColor() != 0) {
            iv_back.setColorFilter(multiUiConfig.getBackIconColor());
        }

        if (multiUiConfig.getTopBarBackgroundColor() != 0) {
            top_bar.setBackgroundColor(multiUiConfig.getTopBarBackgroundColor());
        }

        if (multiUiConfig.getPickerBackgroundColor() != 0) {
            mRecyclerView.setBackgroundColor(multiUiConfig.getPickerBackgroundColor());
        }

        if (multiUiConfig.getBottomBarBackgroundColor() != 0) {
            footer_panel.setBackgroundColor(multiUiConfig.getBottomBarBackgroundColor());
        }

        if (multiUiConfig.getTitleColor() != 0) {
            tv_title.setTextColor(multiUiConfig.getTitleColor());
        }
        if (selectConfig.isShowVideo() && selectConfig.isShowImage()) {
            tv_title.setText(getResources().getString(R.string.str_image_video));
        } else if (selectConfig.isShowVideo()) {
            tv_title.setText(getResources().getString(R.string.str_video));
        } else {
            tv_title.setText(getResources().getString(R.string.str_image));
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_title.setGravity(Gravity.CENTER | multiUiConfig.getTopBarTitleGravity());
        if (selectConfig.getSelectMode() != ImageSelectMode.MODE_MULTI) {
            mTvRight.setVisibility(View.GONE);
        } else {
            mTvRight.setVisibility(View.VISIBLE);
        }

        resetBtnOKBtn();
    }

    /**
     * 初始化监听
     */
    private void setListener() {
        btnDir.setOnClickListener(this);
        v_masker.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mImageSetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectImageSet(position);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mTvTime.getVisibility() == View.VISIBLE) {
                        mTvTime.setVisibility(View.GONE);
                        mTvTime.startAnimation(AnimationUtils.loadAnimation(MultiImagePickerActivity.this, R.anim.picker_fade_out));
                    }
                } else {
                    if (mTvTime.getVisibility() == View.GONE) {
                        mTvTime.setVisibility(View.VISIBLE);
                        mTvTime.startAnimation(AnimationUtils.loadAnimation(MultiImagePickerActivity.this, R.anim.picker_fade_in));
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (imageItems != null)
                    try {
                        mTvTime.setText(imageItems.get(layoutManager.findFirstVisibleItemPosition()).getTimeFormat());
                    } catch (Exception ignored) {

                    }
            }
        });
    }

    private GridLayoutManager layoutManager;

    /**
     * 初始化相关adapter
     */
    private void initAdapters() {
        mImageSetAdapter = new MultiSetAdapter(this, presenter);
        mImageSetAdapter.refreshData(imageSets);
        mImageSetListView.setAdapter(mImageSetAdapter);

        mAdapter = new MultiGridAdapter(this, new ArrayList<ImageItem>(), selectConfig, presenter);
        layoutManager = new GridLayoutManager(this, selectConfig.getColumnCount());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 异步加载图片数据
     */
    public void loadPicData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
            }
        } else {
            //从媒体库拿到数据
            MediaDataSource dataSource = new MediaDataSource(this);
            dataSource.setLoadImage(selectConfig.isShowImage());
            dataSource.setLoadGif(selectConfig.isLoadGif());
            dataSource.setLoadVideo(selectConfig.isShowVideo());
            dataSource.provideMediaItems(this);
        }
    }

    /**
     * 选择图片文件夹
     *
     * @param position 位置
     */
    private void selectImageSet(final int position) {
        this.currentSetIndex = position;
        this.imageItems = imageSets.get(position).imageItems;
        MultiPickerData.instance.setCurrentImageSet(imageSets.get(position));
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
                mRecyclerView.smoothScrollToPosition(0);
            }
        }, 100);
    }

    /**
     * 显示或隐藏图片文件夹选项列表
     */
    private void showOrHideImageSetList() {
        if (mImageSetListView.getVisibility() == View.GONE) {
            v_masker.setVisibility(View.VISIBLE);
            mImageSetListView.setVisibility(View.VISIBLE);
            mImageSetListView.setAnimation(AnimationUtils.loadAnimation(MultiImagePickerActivity.this, R.anim.picker_show2bottom));
            int index = mImageSetAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            mImageSetListView.setSelection(index);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageSetListView.getLayoutParams();
            if (params != null) {
                if (imageSets.size() > 5) {
                    params.height = (int) (getResources().getDisplayMetrics().heightPixels / 1.6f);
                } else {
                    params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                }
                mImageSetListView.setLayoutParams(params);
            }

        } else {
            v_masker.setVisibility(View.GONE);
            mImageSetListView.setVisibility(View.GONE);
            mImageSetListView.setAnimation(AnimationUtils.loadAnimation(MultiImagePickerActivity.this, R.anim.picker_hide2bottom));
        }
    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        this.imageSets = imageSetList;
        this.imageItems = imageSetList.get(currentSetIndex).imageItems;
        MultiPickerData.instance.setCurrentImageSet(imageSetList.get(currentSetIndex));
        btnDir.setText(imageSetList.get(currentSetIndex).name);
        mAdapter.refreshData(imageItems);
        mImageSetAdapter.refreshData(imageSetList);
    }

    @Override
    public void onClick(View v) {
        if (v == btnDir || v == v_masker) {
            showOrHideImageSetList();
        } else if (v == mTvRight) {
            if (isEmpty()) {
                return;
            }
            notifyOnImagePickComplete(MultiPickerData.instance.getSelectImageList());
        }
    }

    private boolean isEmpty() {
        if (MultiPickerData.instance.isEmpty()) {
            presenter.tip(this, getResources()
                    .getString(R.string.str_emptytip));
            return true;
        }
        return false;
    }

    private void notifyOnImagePickComplete(List<ImageItem> list) {
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.INTENT_KEY_PICKERRESULT, (Serializable) list);
        setResult(ImagePicker.REQ_PICKER_RESULT_CODE, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CAMERA) {//拍照返回
            if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                if (selectConfig.getSelectMode() == ImageSelectMode.MODE_CROP) {
                    intentCrop(mCurrentPhotoPath);
                    return;
                }
                refreshGalleryAddPic();
                ImageItem item = new ImageItem();
                item.path = mCurrentPhotoPath;
                List<ImageItem> list = new ArrayList<>();
                list.add(item);
                notifyOnImagePickComplete(list);
            }
        } else {
            if (selectConfig.getSelectMode() == ImageSelectMode.MODE_TAKEPHOTO) {
                finish();
            }
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
        mRecyclerView.setTag(item);
        switch (selectConfig.getSelectMode()) {
            //多选情况下，点击跳转预览
            case ImageSelectMode.MODE_MULTI:
                ImageSet imageSet = imageSets.get(currentSetIndex);
                MultiPickerData.instance.setCurrentImageSet(imageSet);
                MultiImagePreviewActivity.preview(this,
                        selectConfig,
                        presenter,
                        true,
                        null,
                        position,
                        new OnImagePickCompleteListener() {
                            @Override
                            public void onImagePickComplete(ArrayList<ImageItem> items) {
                                notifyOnImagePickComplete(items);
                            }
                        });
                break;
            //单选情况下，点击直接返回
            case ImageSelectMode.MODE_SINGLE:
                List<ImageItem> list2 = new ArrayList<>();
                list2.add(item);
                notifyOnImagePickComplete(list2);
                break;
            //剪裁情况下，点击跳转剪裁
            case ImageSelectMode.MODE_CROP:
                intentCrop(item.path);
                break;
        }
    }

    private void intentCrop(String path) {
        Intent intent = new Intent(this, SingleCropActivity.class);
        intent.putExtra(INTENT_KEY_UI_CONFIG, presenter);
        intent.putExtra(INTENT_KEY_SELECT_CONFIG, selectConfig);
        intent.putExtra(INTENT_KEY_CURRENT_IMAGE, path);
        ActivityLauncher.init(this).startActivityForResult(intent, new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKERRESULT)) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKERRESULT);
                    notifyOnImagePickComplete(list);
                }
            }
        });
    }

    public void imageSelectChange(ImageItem imageItem, boolean isChecked) {
        if (isChecked) {
            MultiPickerData.instance.addImageItem(imageItem);
        } else {
            MultiPickerData.instance.removeImageItem(imageItem);
        }
        resetBtnOKBtn();
    }

    @SuppressLint("DefaultLocale")
    private void resetBtnOKBtn() {
        if (!MultiPickerData.instance.isEmpty()) {
            mTvRight.setClickable(true);
            mTvRight.setEnabled(true);
            mTvRight.setAlpha(1f);
            String text = String.format("%s(%d/%d)", multiUiConfig.getOkBtnText(),
                    MultiPickerData.instance.getSelectCount(),
                    selectConfig.getMaxCount());
            mTvRight.setText(text);
            mTvPreview.setText(String.format("预览(%d)",
                    MultiPickerData.instance.getSelectCount()));
            mTvPreview.setVisibility(View.VISIBLE);
            if (multiUiConfig.getOkBtnSelectBackground() != null) {
                mTvRight.setBackground(multiUiConfig.getOkBtnSelectBackground());
            } else {
                mTvRight.setBackground(getResources().getDrawable(R.drawable.picker_wechat_okbtn_select));
            }
            if (multiUiConfig.getOkBtnSelectTextColor() != 0) {
                mTvRight.setTextColor(multiUiConfig.getOkBtnSelectTextColor());
            }
        } else {
            mTvRight.setText(multiUiConfig.getOkBtnText());
            mTvRight.setClickable(false);
            mTvRight.setEnabled(false);
            mTvPreview.setText("预览");
            mTvPreview.setVisibility(View.GONE);
            if (multiUiConfig.getOkBtnUnSelectBackground() != null) {
                mTvRight.setBackground(multiUiConfig.getOkBtnUnSelectBackground());
            } else {
                mTvRight.setBackground(getResources().getDrawable(R.drawable.picker_wechat_okbtn_unselect));
            }
            if (multiUiConfig.getOkBtnUnSelectTextColor() != 0) {
                mTvRight.setTextColor(multiUiConfig.getOkBtnUnSelectTextColor());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                takePhoto();
            } else {
                PermissionUtils.create(this).showSetPermissionDialog(getString(R.string.picker_str_camerapermisson));
            }
        } else if (requestCode == REQ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPicData();
            } else {
                PermissionUtils.create(this).showSetPermissionDialog(getString(R.string.picker_str_storagepermisson));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
