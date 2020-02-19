package com.ypx.imagepickerdemo.preview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 圆形小球指示器
 * <p>
 * Author: peixing.yang
 * Date: 2019/1/25
 */
public class CircleImageIndicator extends View {
    private int imageCount = 6;
    private Paint mCirclePaint;
    private int mCirclePadding;
    private int mCircleMaxRadius;
    private int mCircleMinRadius;
    private int selectIndex = 0;
    private int childCircleWidth;
    private int showCircleCount = 5;
    private int normalColor;
    private int pressColor;
    private List<CircleModel> points = new ArrayList<>();
    private boolean isBlendColors = true;

    public CircleImageIndicator(Context context) {
        super(context);
        init();
    }

    public CircleImageIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        normalColor = Color.GREEN;
        pressColor = Color.RED;
        mCirclePadding = dp(5);
        mCircleMaxRadius = dp(3);
        mCircleMinRadius = dp(2);
        childCircleWidth = mCircleMaxRadius * 2 + mCirclePadding;
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        setImageCount(0);
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
        invalidate();
    }

    public void setPressColor(int pressColor) {
        this.pressColor = pressColor;
        invalidate();
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
        if (imageCount == 0 || imageCount == 1) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }

        int width = (imageCount > showCircleCount ? showCircleCount : imageCount) * childCircleWidth;
        int height = mCircleMaxRadius * 2 + dp(4);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        setLayoutParams(params);
        offsetX = 0;
        reloadPoint();
        invalidate();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        invalidate();
    }

    private void reloadPoint() {
        points.clear();
        for (int i = 0; i < imageCount; i++) {
            CircleModel circleModel = new CircleModel();
            circleModel.color = i == selectIndex ? pressColor : normalColor;
            if (imageCount <= showCircleCount) {
                circleModel.radius = mCircleMaxRadius;
            } else {
                if (selectIndex > 2 && selectIndex < imageCount - 3) {
                    if (Math.abs(selectIndex - i) > 1) {
                        circleModel.radius = mCircleMinRadius;
                    } else {
                        circleModel.radius = mCircleMaxRadius;
                    }
                } else if (selectIndex <= 2) {
                    if (i > showCircleCount - 2) {
                        circleModel.radius = mCircleMinRadius;
                    } else {
                        circleModel.radius = mCircleMaxRadius;
                    }
                } else {
                    if (i > imageCount - showCircleCount) {
                        circleModel.radius = mCircleMaxRadius;
                    } else {
                        circleModel.radius = mCircleMinRadius;
                    }
                }
            }
            circleModel.position = i;
            points.add(circleModel);
        }
    }


    public void setBlendColors(boolean blendColors) {
        isBlendColors = blendColors;
    }

    public void bindViewPager(final ViewPager viewPager) {
        selectIndex = viewPager.getCurrentItem();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position >= imageCount) {
                    return;
                }

                if (isBlendColors) {
                    points.get(position).color = blendColors(pressColor, normalColor, 1 - positionOffset);
                }

                if (position + 1 < imageCount) {
                    if (isBlendColors) {
                        points.get(position + 1).color = blendColors(pressColor, normalColor, positionOffset);
                    }
                }

                //当小球少于小球显示数量时，不滑动
                if (imageCount <= showCircleCount) {
                    invalidate();
                    return;
                }

                //当最后一个小球显示时
                if (position + positionOffset >= imageCount - showCircleCount + 2) {
                    invalidate();
                    offsetX = childCircleWidth * (imageCount - showCircleCount);
                    return;
                }

                //当小球可滑动时，则一次偏移一个小球
                if (position + positionOffset >= 2) {
                    offsetX = (int) (childCircleWidth * (position + positionOffset - 2));
                }

                invalidate();
            }

            @Override
            public void onPageSelected(int position) {
                if (position >= imageCount) {
                    return;
                }
                selectIndex = position;
                reloadPoint();
                invalidate();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    private int offsetX;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        for (CircleModel model : points) {
            mCirclePaint.setColor(model.color);
            canvas.drawCircle(childCircleWidth - mCircleMaxRadius - offsetX + childCircleWidth * model.position,
                    getHeight() / 2,
                    model.radius,
                    mCirclePaint);
        }
        super.onDraw(canvas);
    }

    /**
     * 两个大小渐变
     *
     * @param minRadius 最小尺寸
     * @param maxRadius 最大尺寸
     * @param ratio     渐变率
     * @return 计算后的尺寸
     */
    private float blendRadius(int minRadius, int maxRadius, float ratio) {
        return (minRadius + (maxRadius - minRadius) * ratio * 1.0f);
    }

    /**
     * 两个颜色渐变转化
     *
     * @param color1 默认色
     * @param color2 目标色
     * @param ratio  渐变率（0~1）
     * @return 计算后的颜色
     */
    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio)
                + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio)
                + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio)
                + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public class CircleModel {
        public int radius;
        public int color;
        public int position;
    }

    public int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }
}
