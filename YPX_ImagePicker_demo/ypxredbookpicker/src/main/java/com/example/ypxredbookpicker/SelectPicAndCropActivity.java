package com.example.ypxredbookpicker;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ypxredbookpicker.adapter.ImageGridAdapter;
import com.example.ypxredbookpicker.adapter.ImageSetAdapter;
import com.example.ypxredbookpicker.bean.ImageItem;
import com.example.ypxredbookpicker.bean.ImageSet;
import com.example.ypxredbookpicker.helper.RecyclerViewTouchHelper;
import com.example.ypxredbookpicker.utils.CornerUtils;
import com.example.ypxredbookpicker.utils.FileUtil;
import com.example.ypxredbookpicker.utils.TakePhotoUtil;
import com.example.ypxredbookpicker.widget.TouchRecyclerView;
import com.example.ypxredbookpicker.widget.browseimage.PicBrowseImageView;
import com.example.ypxredbookpicker.data.DataSource;
import com.example.ypxredbookpicker.data.OnImagesLoadedListener;
import com.example.ypxredbookpicker.data.impl.LocalDataSource;
import com.example.ypxredbookpicker.utils.ViewSizeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class SelectPicAndCropActivity extends FragmentActivity implements OnImagesLoadedListener, View.OnClickListener {
    public static final String INTENT_KEY = "ImageLoaderProvider";
    public static final int REQ_CAMERA = 1431;
    private TouchRecyclerView mGridImageRecyclerView;
    private RecyclerView mImageSetRecyclerView;
    private TextView mTvSetName;
    private TextView mTvSetName2;
    private TextView mTvFullOrFit;
    private TextView mTvNext;
    private ImageView mArrowImg;
    private ImageLoaderProvider imageLoader;
    private ImageGridAdapter imageGridAdapter;
    private ImageSetAdapter imageSetAdapter;
    private List<ImageSet> imageSets = new ArrayList<>();
    private List<ImageItem> imageItems = new ArrayList<>();
    private PicBrowseImageView mCropView;
    private ImageButton stateBtn;
    private int mCropSize;
    private View maskView;
    private int cropMode = ImageCropMode.FILL;
    private int picIndex = 0;
    private int setIndex = 0;
    private RecyclerViewTouchHelper touchHelper;
    private List<ImageItem> selectList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(INTENT_KEY)) {
            imageLoader = (ImageLoaderProvider) getIntent().getSerializableExtra(INTENT_KEY);
        }
        setContentView(R.layout.activity_selectpicandcrop);
        initView();
        initGridImages();
        initImageSets();
        loadPicData();
    }

    /**
     * 异步加载图片数据
     */
    public void loadPicData() {
        DataSource dataSource = new LocalDataSource(this);
        dataSource.provideMediaItems(this);
    }

    private void initView() {
        mTvSetName = findViewById(R.id.mTvSetName);
        mTvSetName2 = findViewById(R.id.mTvSetName2);
        mTvFullOrFit = findViewById(R.id.mTvFullOrFit);
        mTvNext = findViewById(R.id.mTvNext);
        mArrowImg = findViewById(R.id.mArrowImg);
        maskView = findViewById(R.id.v_mask);
        RelativeLayout topView = findViewById(R.id.topView);
        RelativeLayout titleBar = findViewById(R.id.titleBar);
        RelativeLayout mCropLayout = findViewById(R.id.mCropLayout);
        stateBtn = findViewById(R.id.stateBtn);
        mCropView = findViewById(R.id.mCropView);
        mGridImageRecyclerView = findViewById(R.id.mRecyclerView);
        mImageSetRecyclerView = findViewById(R.id.mImageSetRecyclerView);
        mTvFullOrFit.setBackground(CornerUtils.cornerDrawable(Color.parseColor("#80000000"), dp(15)));
        //初始化监听
        stateBtn.setOnClickListener(this);
        mTvSetName.setOnClickListener(this);
        mTvSetName2.setOnClickListener(this);
        maskView.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mTvFullOrFit.setOnClickListener(this);
        //防止点击穿透
        mCropLayout.setClickable(true);
        titleBar.setClickable(true);
        //蒙层隐藏
        maskView.setAlpha(0f);
        maskView.setVisibility(View.GONE);
        //设置剪裁view的属性
        mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mCropView.enable(); // 启用图片缩放功能
        mCropView.setMaxScale(7.0f);
        //初始化相关尺寸信息
        mCropSize = ViewSizeUtils.getScreenWidth(this);
        ViewSizeUtils.setViewSize(mCropLayout, mCropSize, 1.0f);
        ViewSizeUtils.setViewSize(mCropView, mCropSize, 1.0f);
        touchHelper = RecyclerViewTouchHelper.create(mGridImageRecyclerView)
                .setTopView(topView)
                .setMaskView(maskView)
                .setCanScrollHeight(mCropSize)
                .setStickHeight(dp(50))
                .build();
    }

    /**
     * 初始化图片列表
     */
    private void initGridImages() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mGridImageRecyclerView.setLayoutManager(gridLayoutManager);
        imageGridAdapter = new ImageGridAdapter(this, imageItems, selectList, imageLoader);
        mGridImageRecyclerView.setAdapter(imageGridAdapter);
    }

    /**
     * 初始化文件夹列表
     */
    private void initImageSets() {
        mImageSetRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageSetAdapter = new ImageSetAdapter(this, imageSets, imageLoader);
        mImageSetRecyclerView.setAdapter(imageSetAdapter);
        mImageSetRecyclerView.setVisibility(View.GONE);
    }


    /**
     * 图片信息加载回调
     *
     * @param imageSetList 图片文件夹列表
     */
    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        if (this.imageSets.size() > 0) {
            return;
        }
        this.imageSets.clear();
        this.imageSets.addAll(imageSetList);
        imageSetAdapter.notifyDataSetChanged();

        imageItems.clear();
        imageItems.addAll(imageSetList.get(setIndex).imageItems);
        imageGridAdapter.notifyDataSetChanged();
        loadImage();
    }

    @Override
    public void onClick(View view) {
        if (view == stateBtn) {
            fullOrFit();
        } else if (view == mTvSetName || view == mTvSetName2) {
            toggleImageSet();
        } else if (view == maskView) {
            touchHelper.transitTopWithAnim(true, picIndex);
        } else if (view == mTvNext) {
            next();
        } else if (view == mTvFullOrFit) {
            fullOrWhiteSpace();
        }
    }


    /**
     * 点击图片
     *
     * @param position 图片位置
     */
    public void pressImage(final int position) {
        if (position == 0) {
            takePhoto();
            return;
        }
        this.picIndex = position - 1;
        ImageItem imageItem = imageItems.get(picIndex);
        if (imageItem == null) {
            return;
        }

        if (imageItem.getSelectIndex() == 1) {
            mTvFullOrFit.setVisibility(View.GONE);
            stateBtn.setVisibility(View.VISIBLE);
        } else {
            if (selectList.size() > 0) {
                mTvFullOrFit.setVisibility(View.VISIBLE);
                stateBtn.setVisibility(View.GONE);
            } else {
                mTvFullOrFit.setVisibility(View.GONE);
                stateBtn.setVisibility(View.VISIBLE);
            }
        }

        for (ImageItem imageItem1 : imageItems) {
            if (imageItem1 == imageItem) {
                imageItem1.setPress(true);
            } else {
                imageItem1.setPress(false);
            }
        }
        imageGridAdapter.notifyDataSetChanged();
        resetCropViewSize(false);
        checkStateBtn(imageItem);
        loadImage();
        touchHelper.transitTopWithAnim(true, position);
    }

    private void checkStateBtn(ImageItem imageItem) {
        //方形图，什么都不显示
        if (imageItem.getWidthHeightRatio() > 0.99f && imageItem.getWidthHeightRatio() < 1.1f) {
            stateBtn.setVisibility(View.GONE);
            mTvFullOrFit.setVisibility(View.GONE);
            return;
        }
        //当选中图片数量大于0 时
        if (selectList.size() > 0) {
            //如果当前选中item就是第一个图片，显示stateBtn
            if (imageItem.path.equals(selectList.get(0).path)) {
                stateBtn.setVisibility(View.VISIBLE);
                mTvFullOrFit.setVisibility(View.GONE);
            } else {
                //如果当前选中item不是第一张图片，显示mTvFullOrFit
                stateBtn.setVisibility(View.GONE);
                mTvFullOrFit.setVisibility(View.VISIBLE);
                //TODO 以下是控制充满和留白逻辑
                ImageItem firstImageItem = selectList.get(0);
                //如果当前模式为自适应模式
                if (cropMode == ImageCropMode.FIT) {
                    //如果当前图片和第一张选中图片的宽高类型一样，则不显示留白和充满
                    if (firstImageItem.getWidthHeightType() == imageItem.getWidthHeightType()) {
                        mTvFullOrFit.setVisibility(View.GONE);
                    } else {
                        //TODO 处理充满和留白显示类型
                        mTvFullOrFit.setVisibility(View.VISIBLE);
                    }
                } else {//如果第一张图为充满模式，则不论宽高比（除正方形外），都显示留白和充满
                    mTvFullOrFit.setVisibility(View.VISIBLE);
                }
            }
        } else {//没有选中图片
            stateBtn.setVisibility(View.VISIBLE);
            mTvFullOrFit.setVisibility(View.GONE);
        }
    }

    /**
     * 选中图片
     *
     * @param position 图片索引
     */
    public void selectImage(final int position) {
        ImageItem imageItem = imageItems.get(position - 1);
        if (imageItem.isSelect()) {
            imageItem.setSelect(false);
            selectList.remove(imageItem);
        } else {
            selectList.add(imageItem);
            imageItem.setSelect(true);
            pressImage(position);
        }
        imageGridAdapter.notifyDataSetChanged();
        checkStateBtn(imageItem);
    }

    /**
     * 点击选中相册
     *
     * @param position 相册position
     */
    public void selectImageSet(int position) {
        setIndex = position;
        ImageSet imageSet = imageSets.get(position);
        if (imageSet == null) {
            return;
        }
        mTvSetName2.setText(imageSet.name);
        mTvSetName.setText(imageSet.name);
        imageItems.clear();
        imageItems.addAll(imageSet.imageItems);
        imageGridAdapter.notifyDataSetChanged();
        mGridImageRecyclerView.smoothScrollToPosition(0);
        toggleImageSet();
    }


    private String mCurrentPhotoPath;

    public void takePhoto() {
        TakePhotoUtil.mCurrentPhotoPath = "";
        TakePhotoUtil.takePhoto(this, REQ_CAMERA);
        mCurrentPhotoPath = TakePhotoUtil.mCurrentPhotoPath;
    }

    /**
     * 加载图片
     */
    private void loadImage() {
        if (imageItems.size() != 0) {
            imageLoader.displayListImage(mCropView, imageItems.get(picIndex).path, 0);
        }
    }


    /**
     * 切换文件夹选择
     */
    private void toggleImageSet() {
        if (mImageSetRecyclerView.getVisibility() == View.VISIBLE) {
            mImageSetRecyclerView.setVisibility(View.GONE);
            mArrowImg.setRotation(180);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.dd_menu_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ((ViewGroup) mTvSetName2.getParent()).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mImageSetRecyclerView.setAnimation(animation);
        } else {
            mArrowImg.setRotation(0);
            mImageSetRecyclerView.setVisibility(View.VISIBLE);
            ((ViewGroup) mTvSetName2.getParent()).setVisibility(View.VISIBLE);
            mImageSetRecyclerView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dd_menu_in));
        }
    }


    /**
     * 点击下一步
     */
    public void next() {
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "Crop" + File.separator;
        File f = new File(path, "crop_" + System.currentTimeMillis() + ".jpg");
        String cropUrl = FileUtil.saveBitmapToLocalWithJPEG(FileUtil.getViewBitmap(mCropView), f.getAbsolutePath());

        Intent intent = new Intent(this, ImageEditActivity.class);
        intent.putExtra(ImageEditActivity.INTENT_KEY_URL, cropUrl);
        intent.putExtra(INTENT_KEY, imageLoader);
        startActivity(intent);
    }

    private void animCropView(boolean isShowAnim, final int width, final int height) {
        if (!isShowAnim) {
            ViewSizeUtils.setViewSize(mCropView, width, height);
            //mCropView.setImageDrawable(mCropView.getDrawable());
            return;
        }
        final int startWidth = ViewSizeUtils.getViewWidth(this.mCropView);
        final int startHeight = ViewSizeUtils.getViewHeight(this.mCropView);
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "ypx", 0.0f, 1.0f);
        anim.setDuration(200);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (Float) animation.getAnimatedValue();
                ViewSizeUtils.setViewSize(mCropView, (int) ((width - startWidth) * ratio + startWidth),
                        ((int) ((height - startHeight) * ratio + startHeight)));
                mCropView.setImageDrawable(mCropView.getDrawable());
            }
        });
        anim.start();
    }

    /**
     * 重置剪裁宽高大小
     */
    private void resetCropViewSize(boolean isShowAnim) {
        ImageItem imageItem = imageItems.get(picIndex);
        if (cropMode == ImageCropMode.FILL) {
            animCropView(isShowAnim, mCropSize, mCropSize);
        } else {
            if (imageItem.getWidthHeightRatio() > 1.1f) {//宽图
                animCropView(isShowAnim, mCropSize, (mCropSize * 3) / 4);
            } else if (imageItem.getWidthHeightRatio() < 0.99f) {//高图
                animCropView(isShowAnim, (mCropSize * 3) / 4, mCropSize);
            } else {
                animCropView(isShowAnim, mCropSize, mCropSize);
            }
        }
    }

    /**
     * 第一张图片剪裁区域充满或者自适应（是剪裁区域，不是图片填充和留白）
     */
    public void fullOrFit() {
        if (cropMode == ImageCropMode.FIT) {
            cropMode = ImageCropMode.FILL;
            stateBtn.setImageDrawable(getResources().getDrawable(R.mipmap.icon_fit));
        } else {
            cropMode = ImageCropMode.FIT;
            stateBtn.setImageDrawable(getResources().getDrawable(R.mipmap.icon_fill));
        }
        mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        resetCropViewSize(true);
    }

    /**
     * 充满或者留白
     */
    public void fullOrWhiteSpace() {
        if (mTvFullOrFit.getText().equals("充满")) {
            //留白
            mTvFullOrFit.setText("留白");
            mTvFullOrFit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_full), null, null, null);
            mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            //充满
            mTvFullOrFit.setText("充满");
            mTvFullOrFit.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.icon_haswhite), null, null, null);
            mCropView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        resetCropViewSize(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CAMERA) {//拍照返回
            if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                refreshGalleryAddPic();
                ImageItem item = new ImageItem(mCurrentPhotoPath, "", -1);
                imageItems.add(0, item);
                imageGridAdapter.notifyDataSetChanged();
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

    public int dp(int dp) {
        return ViewSizeUtils.dp(this, dp);
    }
}
