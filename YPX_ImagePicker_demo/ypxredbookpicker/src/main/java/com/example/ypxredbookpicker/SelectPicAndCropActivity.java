package com.example.ypxredbookpicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ypxredbookpicker.adapter.SelectPicAdapter;
import com.example.ypxredbookpicker.bean.ImageItem;
import com.example.ypxredbookpicker.bean.ImageSet;
import com.example.ypxredbookpicker.utils.FileUtil;
import com.example.ypxredbookpicker.widget.ScrollableRecyclerView;
import com.example.ypxredbookpicker.widget.browseimage.PicBrowseImageView;
import com.example.ypxredbookpicker.data.DataSource;
import com.example.ypxredbookpicker.data.OnImagesLoadedListener;
import com.example.ypxredbookpicker.data.impl.LocalDataSource;
import com.example.ypxredbookpicker.utils.ViewSizeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class SelectPicAndCropActivity extends FragmentActivity implements OnImagesLoadedListener {
    public static String INTENT_KEY = "ImageLoaderProvider";
    private ScrollableRecyclerView mRecyclerView;
    private LinearLayout topView;
    private TextView mTvSetName;
    private RelativeLayout mCropLayout;
    private RelativeLayout titleBar;
    private ImageLoaderProvider imageLoader;
    private SelectPicAdapter adapter;
    private List<ImageSet> imageSets = new ArrayList<>();
    private List<ImageItem> imageItems = new ArrayList<>();
    private PicBrowseImageView mCropView;
    private ImageButton stateBtn;
    private int mCropSize;
    private View maskView;
    private GridLayoutManager gridLayoutManager;

    private int cropMode = ImageCropMode.FILL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectpicandcrop);
        if (getIntent().hasExtra(INTENT_KEY)) {
            imageLoader = (ImageLoaderProvider) getIntent().getSerializableExtra(INTENT_KEY);
        }
        initView();
        loadPicData();
    }

    private boolean isTopViewStick = false;

    private void initView() {
        mTvSetName = findViewById(R.id.mTvSetName);
        maskView = findViewById(R.id.v_mask);
        topView = findViewById(R.id.topView);
        titleBar = findViewById(R.id.titleBar);
        mCropLayout = findViewById(R.id.mCropLayout);
        stateBtn = findViewById(R.id.stateBtn);
        mCropView = findViewById(R.id.mCropView);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        maskView.setAlpha(0f);
        gridLayoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        adapter = new SelectPicAdapter(this, imageItems, imageLoader);
        mRecyclerView.setAdapter(adapter);
        mCropView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // 启用图片缩放功能
        mCropView.enable();
        mCropView.setMaxScale(7.0f);
        mCropSize = ViewSizeUtils.getScreenWidth(this);
        ViewSizeUtils.setViewSize(mCropLayout, mCropSize, 1.0f);
        ViewSizeUtils.setViewSize(mCropView, mCropSize, 1.0f);
        stateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullOrFit(stateBtn);
            }
        });
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setPadding(0, topView.getHeight(), 0, 0);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isRecovery()) {
                    isTopViewStick = false;
                }

                if (isTopViewStick) {
                    topView.setTranslationY(-getScollYDistance() - topView.getHeight());
                    float ratio = topView.getTranslationY() * 1.00f / (-topView.getHeight() * 1.00f);
                    setMaskAlpha(ratio);
                }
            }
        });

        mRecyclerView.setDragScrollListener(new ScrollableRecyclerView.onDragScrollListener() {
            @Override
            public void onScrollOverTop(int distance) {
                isTouchToCropView = true;
                if (distance > mCropSize) {
                    maskView.setAlpha(1.0f);
                    topView.setTranslationY(-mCropSize);
                    mRecyclerView.setPadding(0, titleBar.getHeight(), 0, 0);
                } else {
                    if (topView.getTranslationY() != -mCropSize) {
                        float ratio = -distance * 1.00f / (-mCropSize * 1.00f);
                        setMaskAlpha(ratio);
                        topView.setTranslationY(-distance);
                    }
                }

            }

            @Override
            public void onScrollDown(int distance) {
                if (!mRecyclerView.canScrollVertically(-1)) {
                    mRecyclerView.setPadding(0, topView.getHeight(), 0, 0);
                    isTopViewStick = true;
                }
            }


            @Override
            public void onScrollUp() {
                if (isTouchToCropView && !isTopViewStick) {
                    animToTop(false, -1);
                }
                if (isTopViewStick && !isRecovery() && !isTouchToCropView) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
                isTouchToCropView = false;
            }
        });

        mTvSetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setMaskAlpha(float ratio) {
        if (ratio < 0) {
            ratio = 0;
        } else if (ratio > 1) {
            ratio = 1;
        }
        maskView.setAlpha(ratio);
    }

    private boolean isRecovery() {
        return (topView.getTranslationY() < dp(3) && topView.getTranslationY() > -dp(3));
    }

    private int getScollYDistance() {
        int position = gridLayoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = gridLayoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position / 4) * itemHeight - firstVisiableChildView.getTop();
    }

    public int endTop;

    @SuppressLint("ObjectAnimatorBinding")
    public void animToTop(boolean isShow, final int scrollToPosition) {
        final int startTop = (int) topView.getTranslationY();
        if (isShow || (startTop > -titleBar.getHeight())) {
            endTop = 0;
            setMaskAlpha(0);
        } else {
            setMaskAlpha(1);
            endTop = -mCropLayout.getHeight();
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "ypx", 0.0f, 1.0f);
        anim.setDuration(200);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (Float) animation.getAnimatedValue();
                int dis = (int) ((endTop - startTop) * ratio + startTop);
                topView.setTranslationY(dis);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRecyclerView.setPadding(0, (int) (endTop == 0 ? topView.getHeight() : titleBar.getHeight()), 0, 0);
                if (scrollToPosition == 0) {
                    mRecyclerView.scrollToPosition(0);
                }
                if (scrollToPosition != -1) {
                    mRecyclerView.smoothScrollToPosition(scrollToPosition);
                }
            }
        });
        anim.start();
    }

    private boolean isTouchToCropView = false;

    private int picIndex = 0;


    /**
     * 异步加载图片数据
     * select all images from local database
     */
    public void loadPicData() {
        DataSource dataSource = new LocalDataSource(this);
        dataSource.provideMediaItems(this);
    }

    public void selectImage(final int position) {
        this.picIndex = position - 1;
        imageItems.get(picIndex).setSelect(!imageItems.get(picIndex).isSelect());
        adapter.notifyDataSetChanged();
        resetCropViewSize();
        loadImage();
        setMaskAlpha(0);
        if (isTopViewScrolled()) {
            animToTop(true, position);
        }
    }

    private boolean isTopViewScrolled() {
        return ViewSizeUtils.getMarginTop(topView) != 0 || topView.getTranslationY() != 0;
    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {
        this.imageSets = imageSetList;
        imageItems.clear();
        imageItems.addAll(imageSetList.get(0).imageItems);
        adapter.notifyDataSetChanged();
        loadImage();
    }

    private void loadImage() {
        if (imageItems.size() != 0) {
            imageLoader.displayListImage(mCropView, imageItems.get(picIndex).path, 0);
        }
    }

    public void next(View view) {
        String path = Environment.getExternalStorageDirectory().toString() + File.separator + "Crop" + File.separator;
        File f = new File(path, "crop_" + System.currentTimeMillis() + ".jpg");
        String cropUrl = saveBitmapToLocalWithJPEG(getViewBitmap(mCropView), f.getAbsolutePath());

        Intent intent = new Intent(this, ImageEditActivity.class);
        intent.putExtra(ImageEditActivity.INTENT_KEY_URL, cropUrl);
        intent.putExtra(INTENT_KEY, imageLoader);
        startActivity(intent);
    }

    private void resetCropViewSize() {
        ImageItem imageItem = imageItems.get(picIndex);
        if (imageItem.width > imageItem.height + 10) {//宽图
            stateBtn.setVisibility(View.VISIBLE);
            if (cropMode == ImageCropMode.FIT) {
                ViewSizeUtils.setViewSize(mCropView, mCropSize, (mCropSize * 3) / 4);
            } else {
                ViewSizeUtils.setViewSize(mCropView, mCropSize, mCropSize);
            }
        } else if (imageItem.height > imageItem.width + 10) {//高图
            stateBtn.setVisibility(View.VISIBLE);
            if (cropMode == ImageCropMode.FIT) {
                ViewSizeUtils.setViewSize(mCropView, (mCropSize * 3) / 4, mCropSize);
            } else {
                ViewSizeUtils.setViewSize(mCropView, mCropSize, mCropSize);
            }
        } else {
            stateBtn.setVisibility(View.GONE);
            ViewSizeUtils.setViewSize(mCropView, mCropSize, mCropSize);
        }
    }

    public void fullOrFit(ImageButton button) {
        if (cropMode == ImageCropMode.FIT) {
            cropMode = ImageCropMode.FILL;
            button.setImageDrawable(getResources().getDrawable(R.mipmap.icon_fit));
        } else {
            cropMode = ImageCropMode.FIT;
            button.setImageDrawable(getResources().getDrawable(R.mipmap.icon_fill));
        }
        mCropView.setVisibility(View.INVISIBLE);
        resetCropViewSize();
        button.post(new Runnable() {
            @Override
            public void run() {
                loadImage();
                mCropView.setVisibility(View.VISIBLE);
            }
        });
    }


    /**
     * 保存一张图片到本地
     *
     * @param bmp
     * @param localPath
     */
    public static String saveBitmapToLocalWithJPEG(Bitmap bmp, String localPath) {
        if (bmp == null || localPath == null || localPath.length() == 0) {
            return "";
        }
        FileOutputStream b = null;
        FileUtil.createFile(localPath);
        try {
            b = new FileOutputStream(localPath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                }
                if (b != null) {
                    b.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    public static Bitmap getViewBitmap(View view) {
        Bitmap bkg = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bkg));
        return bkg;
    }

    public int dp(int dp) {
        return ViewSizeUtils.dp(this, dp);
    }
}
