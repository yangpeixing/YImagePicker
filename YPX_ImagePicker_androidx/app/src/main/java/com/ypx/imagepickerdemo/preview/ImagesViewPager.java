package com.ypx.imagepickerdemo.preview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.helper.DetailImageLoadHelper;
import com.ypx.imagepicker.widget.cropimage.CropImageView;
import com.ypx.imagepickerdemo.style.RedBookPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 图片区
 * <p>
 * Author: peixing.yang
 * Date: 2019/1/28
 */
public class ImagesViewPager extends RelativeLayout {
    private ViewPager viewPager;
    private CircleImageIndicator circleImageIndicator;
    private LinearLayout indicatorLayout;

    public ImagesViewPager(Context context) {
        super(context);
        style1();
    }

    public ImagesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        style1();
    }

    public ImagesViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        style1();
    }


    /**
     * 普通样式
     */
    public void style1() {
        isStyle1 = true;
        removeAllViews();
        viewPager = new ViewPager(getContext());
        addView(viewPager, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        indicatorLayout = new LinearLayout(getContext());
        indicatorLayout.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(BELOW, viewPager.getId());
        indicatorLayout.setPadding(0, dp(8), 0, dp(8));
        indicatorLayout.setBackgroundColor(Color.TRANSPARENT);
        addView(indicatorLayout, params);

        circleImageIndicator = new CircleImageIndicator(getContext());
        circleImageIndicator.setBackgroundColor(Color.TRANSPARENT);
        circleImageIndicator.setNormalColor(Color.RED);
        circleImageIndicator.setPressColor(Color.GREEN);
        circleImageIndicator.setBlendColors(true);
        indicatorLayout.addView(circleImageIndicator, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        circleImageIndicator.bindViewPager(viewPager);
    }

    public void setIndicatorBackgroundColor(int color) {
        if (null != indicatorLayout) {
            indicatorLayout.setBackgroundColor(color);
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    private List<View> viewList = new ArrayList<>();

    private boolean isStyle1 = false;

    private boolean isGrayImage = false;

    public void setGrayImage(boolean grayImage) {
        isGrayImage = grayImage;
    }

    /**
     * 设置简单图片适配器
     *
     * @param pictureInfoEntities 图片信息列表
     */
    public void setImageViewList(@Nullable List<? extends ImageItem> pictureInfoEntities) {
        if (pictureInfoEntities == null) {
            return;
        }
        if (viewList == null) {
            viewList = new ArrayList<>();
        } else {
            viewList.clear();
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        for (ImageItem entity : pictureInfoEntities) {
            CropImageView imageView = new CropImageView(getContext());
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setTag(entity);
            imageView.enable();
            imageView.setRotateEnable(true);
            imageView.setBounceEnable(true);
            imageView.setCanShowTouchLine(false);
            imageView.setMaxScale(7.0f);
            if (entity.getCropUrl() != null && entity.getCropUrl().length() > 0) {
                entity.path = entity.getCropUrl();
            }
            //DetailImageLoadHelper.displayDetailImage(false, imageView, new RedBookPresenter(), entity);
            viewList.add(imageView);
        }
        setViewList(viewList);
    }

    public void setViewList(final List<? extends View> viewList) {
        circleImageIndicator.setSelectIndex(0);
        circleImageIndicator.setImageCount(viewList.size());
        if (indicatorLayout != null) {
            indicatorLayout.setVisibility(viewList.size() > 1 ? View.VISIBLE : View.GONE);
            indicatorLayout.setGravity(Gravity.CENTER);
        }

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
        viewPager.setAdapter(simpleAdapter);
    }


    public int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }
}
