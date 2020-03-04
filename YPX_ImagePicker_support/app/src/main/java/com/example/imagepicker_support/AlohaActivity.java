package com.example.imagepicker_support;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.bumptech.glide.Glide;
import com.example.imagepicker_support.style.WeChatPresenter;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.selectconfig.CropConfig;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 高仿Aloha app的多图剪裁页面，支持对多张图进行统一剪裁尺寸修改
 */
public class AlohaActivity extends Activity {

    private final float ratio_3_4 = 3.00f / 4.00f;
    private static final float ratio_1_1 = 1.00f;
    private static final float ratio_4_3 = 4.00f / 3.00f;
    private static final float ratio_16_9 = 16.00f / 9.00f;
    private int currentRatio = 0;

    private ArrayList<ImageItem> imageItems = new ArrayList<>();
    private ArrayList<View> viewList = new ArrayList<>();
    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PStatusBarUtil.setStatusBar(this, Color.WHITE, false, true);
        setContentView(R.layout.activity_aloha);
        imageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);

        initView();
        setImageViewList(imageItems);
    }

    private void initView() {
        LinearLayout mControllerBar = findViewById(R.id.mControllerBar);
        ViewGroup[] mLayouts = new LinearLayout[]{findViewById(R.id.mFrameLayout1),
                findViewById(R.id.mFrameLayout2),
                findViewById(R.id.mFrameLayout3),
                findViewById(R.id.mFrameLayout4),};
        mViewPager = findViewById(R.id.mViewPager);
        for (ViewGroup viewGroup : mLayouts) {
            Drawable drawable = PCornerUtils.cornerDrawableAndStroke(Color.WHITE, dp(50),
                    dp(1), Color.parseColor("#E0E0E0"));
            viewGroup.setBackground(drawable);
            int width = (PViewSizeUtils.getScreenWidth(this) - dp(100)) / 4;
            PViewSizeUtils.setViewSize(viewGroup, width, 1.00f);
            Drawable drawable2 = PCornerUtils.cornerDrawableAndStroke(Color.WHITE, dp(1),
                    dp(2), Color.parseColor("#666666"));
            viewGroup.getChildAt(0).setBackground(drawable2);
        }

        for (int i = 0; i < mControllerBar.getChildCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) mControllerBar.getChildAt(i);
            final int finalI = i;
            viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transitImage(finalI);
                }
            });
        }

        mViewPager.setPageMargin(dp(20));
        mViewPager.setOffscreenPageLimit(imageItems.size());

        PViewSizeUtils.setViewSize(mViewPager,
                PViewSizeUtils.getScreenWidth(this) - dp(100), ratio_3_4);
    }


    private void transitImage(int i) {
        for (ImageItem imageItem : imageItems) {
            imageItem.setCropRestoreInfo(null);
        }
        final int endHeight = getHeightFromIndex(i);
        final int startHeight = mViewPager.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                int height = (int) ((endHeight - startHeight) * value + startHeight);
                PViewSizeUtils.setViewSize(mViewPager, mViewPager.getWidth(), height);
            }
        });
        valueAnimator.start();
    }

    private int getHeightFromIndex(int i) {
        int width = mViewPager.getWidth();
        currentRatio = i;
        if (i == 0) {
            return (int) (width * 1.00f / ratio_3_4);
        } else if (i == 1) {
            return (int) (width * 1.00f / ratio_1_1);
        } else if (i == 2) {
            return (int) (width * 1.00f / ratio_4_3);
        } else if (i == 3) {
            return (int) (width * 1.00f / ratio_16_9);
        }
        return mViewPager.getHeight();
    }


    /**
     * 设置简单图片适配器
     *
     * @param imageItems 图片信息列表
     */
    public void setImageViewList(@Nullable List<? extends ImageItem> imageItems) {
        if (imageItems == null) {
            return;
        }
        if (viewList == null) {
            viewList = new ArrayList<>();
        } else {
            viewList.clear();
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        for (final ImageItem entity : imageItems) {
            CardView cardView = new CardView(this);
            cardView.setCardElevation(dp(2));
            cardView.setRadius(dp(5));
            cardView.setLayoutParams(params);

            CropImageView imageView = new CropImageView(this);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (entity.getCropUrl() != null && entity.getCropUrl().length() > 0) {
                Glide.with(this).load(entity.getCropUrl()).into(imageView);
            } else {
                Glide.with(this).load(entity.path).into(imageView);
            }
            cardView.addView(imageView);
            viewList.add(cardView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentCrop(entity);
                }
            });
        }
        setViewList(viewList);
    }

    private int currentPosition;

    private void intentCrop(final ImageItem entity) {
        CropConfig cropConfig = new CropConfig();
        //设置剪裁比例
        switch (currentRatio) {
            case 0:
                cropConfig.setCropRatio(3, 4);
                break;
            case 1:
                cropConfig.setCropRatio(1, 1);
                break;
            case 2:
                cropConfig.setCropRatio(4, 3);
                break;
            case 3:
                cropConfig.setCropRatio(16, 9);
                break;
        }

        cropConfig.setCropRectMargin(100);
        cropConfig.saveInDCIM(false);
        cropConfig.setCircle(false);
        cropConfig.setCropStyle(CropConfig.STYLE_GAP);
        cropConfig.setCropGapBackgroundColor(Color.WHITE);
        cropConfig.setCropRestoreInfo(entity.getCropRestoreInfo());
        String needCropImageUrl = entity.getPath();
        currentPosition = mViewPager.getCurrentItem();
        ImagePicker.crop(this, new WeChatPresenter(), cropConfig, needCropImageUrl,
                new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //剪裁回调，主线程
                        ImageItem imageItem = items.get(0);
                        imageItems.set(imageItems.indexOf(entity), imageItem);
                        setImageViewList(imageItems);
                        mViewPager.setCurrentItem(currentPosition);
                    }
                });
    }


    public void setViewList(final List<? extends View> viewList) {
        PagerAdapter simpleAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = viewList.get(position);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (viewList.size() > position) {
                    container.removeView(viewList.get(position));
                }
            }
        };
        mViewPager.setAdapter(simpleAdapter);
    }

    public void complete(final View view) {
        final ProgressDialog dialog = ProgressDialog.show(this, null, "正在剪裁...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (ImageItem imageItem : imageItems) {
                    if (imageItem.getCropUrl() == null || imageItem.getCropUrl().length() == 0) {
                        ViewGroup viewGroup = (ViewGroup) viewList.get(imageItems.indexOf(imageItem));
                        CropImageView imageView = (CropImageView) viewGroup.getChildAt(0);
                        Bitmap bitmap = imageView.generateCropBitmap();
                        imageItem.setCropUrl(PBitmapUtils.saveBitmapToFile(AlohaActivity.this, bitmap,
                                System.currentTimeMillis() + "", Bitmap.CompressFormat.JPEG));
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        ImagePicker.closePickerWithCallback(imageItems);
                        finish();
                    }
                });
            }
        }).start();
    }

    private int dp(int dp) {
        return PViewSizeUtils.dp(this, dp);
    }
}
