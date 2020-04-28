package com.ypx.imagepicker.helper;

import android.animation.ValueAnimator;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.widget.TouchRecyclerView;


/**
 * Description: 滑动辅助类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/26
 */
public class RecyclerViewTouchHelper {
    private TouchRecyclerView recyclerView;
    private View topView;
    private View maskView;
    private boolean isScrollTopView = false;
    private boolean isTopViewStick = false;
    private int canScrollHeight;
    private int stickHeight;

    public static RecyclerViewTouchHelper create(TouchRecyclerView recyclerView) {
        return new RecyclerViewTouchHelper(recyclerView);
    }

    private RecyclerViewTouchHelper(TouchRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public RecyclerViewTouchHelper setTopView(View topView) {
        this.topView = topView;
        return this;
    }

    public RecyclerViewTouchHelper setMaskView(View maskView) {
        this.maskView = maskView;
        return this;
    }

    public RecyclerViewTouchHelper setCanScrollHeight(int canScrollHeight) {
        this.canScrollHeight = canScrollHeight;
        return this;
    }

    public RecyclerViewTouchHelper setStickHeight(int stickHeight) {
        this.stickHeight = stickHeight;
        return this;
    }

    private void setRecyclerViewPaddingTop(int top) {
        recyclerView.setPadding(recyclerView.getPaddingStart(), top,
                recyclerView.getPaddingEnd(), recyclerView.getPaddingBottom());
    }

    private int lastScrollY = 0;

    public RecyclerViewTouchHelper build() {
        setRecyclerViewPaddingTop(canScrollHeight + stickHeight);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                setRecyclerViewPaddingTop(topView.getHeight());
            }
        });
        recyclerView.setTouchView(topView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isImageGridCantScroll()) {
                    return;
                }
                int scrollY = getScrollYDistance();
                if (isScrollTopView && topView.getTranslationY() != -canScrollHeight) {
                    if (lastScrollY == 0) {
                        lastScrollY = scrollY;
                    }
                    int distance = scrollY - lastScrollY;
                    if (distance >= canScrollHeight) {
                        setMaskAlpha(1);
                        topView.setTranslationY(-canScrollHeight);
                        setRecyclerViewPaddingTop(stickHeight);
                    } else {
                        if (distance <= 0) {
                            setMaskAlpha(0);
                            topView.setTranslationY(0);
                        } else {
                            float ratio = -distance * 1.00f / (-canScrollHeight * 1.00f);
                            setMaskAlpha(ratio);
                            topView.setTranslationY(-distance);
                        }
                    }
                    return;
                }
                if (isTopViewFullShow()) {
                    isTopViewStick = false;
                    setMaskAlpha(0);
                }

                if (isTopViewStick) {
                    int translate = -scrollY - topView.getHeight();
                    if (translate <= -canScrollHeight) {
                        topView.setTranslationY(-canScrollHeight);
                        setRecyclerViewPaddingTop(stickHeight);
                        isTopViewStick = false;
                    } else {
                        if (translate >= -20) {
                            translate = 0;
                        }
                        topView.setTranslationY(translate);
                        float ratio = topView.getTranslationY() * 1.00f / (-topView.getHeight() * 1.00f);
                        setMaskAlpha(ratio);
                    }
                }
            }
        });

        recyclerView.setDragScrollListener(new TouchRecyclerView.onDragScrollListener() {
            @Override
            public void onScrollOverTop(int distance) {
                if (isImageGridCantScroll()) {
                    return;
                }
                isScrollTopView = true;

            }

            @Override
            public void onScrollDown(int distance) {
                if (isImageGridCantScroll()) {
                    return;
                }
                if (isRecyclerViewScrollToTop() && !isScrollTopView) {
                    setRecyclerViewPaddingTop(topView.getHeight());
                    isTopViewStick = true;
                }
            }

            @Override
            public void onScrollUp() {
                lastScrollY = 0;
                if (isImageGridCantScroll()) {
                    return;
                }
                if (isScrollTopView) {
                    transitTopWithAnim(!isRecyclerViewCanScrollOverScreen(), -1, true);
                } else {
                    if (isTopViewStick && !isTopViewFullShow()) {
                        reset();
                    }
                }
                isScrollTopView = false;
            }
        });
        return this;
    }

    private boolean isRecyclerViewScrollToTop() {
        return !recyclerView.canScrollVertically(-1);
    }

    private boolean isRecyclerViewScrollToBottom() {
        return !recyclerView.canScrollVertically(1);
    }

    private boolean isRecyclerViewCanScrollOverScreen() {
        if (isImageGridCantScroll()) {
            return false;
        }
        int count = 0;
        if (recyclerView.getAdapter() != null) {
            count = recyclerView.getAdapter().getItemCount();
        }
        int itemHeight = getItemHeight();
        if (count < getSpanCount()) {
            return false;
        }
        int lineCount = count % getSpanCount() == 0 ? count / getSpanCount() : count / getSpanCount() + 1;
        return lineCount * itemHeight + recyclerView.getPaddingBottom() >
                PViewSizeUtils.getScreenHeight(recyclerView.getContext()) - stickHeight;
    }

    private int spanCount = 0;

    private int getSpanCount() {
        if (spanCount != 0) {
            return spanCount;
        }
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        if (gridLayoutManager != null) {
            spanCount = gridLayoutManager.getSpanCount();
            return spanCount;
        }
        return 0;
    }

    /**
     * 设置剪裁区域阴影
     *
     * @param ratio 阴影比例
     */
    private void setMaskAlpha(float ratio) {
        maskView.setVisibility(View.VISIBLE);
        if (ratio <= 0) {
            ratio = 0;
            maskView.setVisibility(View.GONE);
        } else if (ratio >= 1) {
            ratio = 1;
        }
        maskView.setAlpha(ratio);
    }

    /**
     * 剪裁区域+标题栏 是否完整显示
     */
    private boolean isTopViewFullShow() {
        return (topView.getTranslationY() == 0);
    }


    /**
     * 选择图片recyclerView是否不可以滑动（数量少）
     */
    private boolean isImageGridCantScroll() {
        return !recyclerView.canScrollVertically(1) &&
                !recyclerView.canScrollVertically(-1);
    }


    /**
     * 获取recyclerView滑动距离
     */
    private int getScrollYDistance() {
        if (!(recyclerView.getLayoutManager() instanceof GridLayoutManager)) {
            return 0;
        }
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int position = gridLayoutManager.findFirstVisibleItemPosition();
        if (position < 0) {
            position = 0;
        }
        View firstVisibleChildView = gridLayoutManager.findViewByPosition(position);
        if (firstVisibleChildView == null) {
            return 0;
        }

        int itemHeight = firstVisibleChildView.getHeight() + PViewSizeUtils.dp(recyclerView.getContext(), 2);
        return (position / getSpanCount()) * itemHeight - firstVisibleChildView.getTop();
    }

    private int getItemHeight() {
        if (!(recyclerView.getLayoutManager() instanceof GridLayoutManager)) {
            return 0;
        }
        GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int position = gridLayoutManager.findFirstVisibleItemPosition();
        if (position < 0) {
            position = 0;
        }
        View firstVisibleChildView = gridLayoutManager.findViewByPosition(position);
        if (firstVisibleChildView == null) {
            return 0;
        }
        return firstVisibleChildView.getHeight();
    }

    private void reset() {
        final int scrollY = getScrollYDistance();
        if (scrollY == 0) {
            return;
        }
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (Float) animation.getAnimatedValue();
                recyclerView.scrollBy(0, (int) (scrollY * ratio));
            }
        });
        anim.start();
    }

    /**
     * 动画控制是否展开还是完整显示topView
     *
     * @param isFocusShow      是否强制完全展示
     * @param scrollToPosition 滑动到制定的位置
     */
    public void transitTopWithAnim(boolean isFocusShow, final int scrollToPosition, boolean isShowTransit) {
        if (!isShowTransit) {
            return;
        }
        if (isTopViewFullShow()) {
            return;
        }
        final int startTop = (int) topView.getTranslationY();
        //如果滑动区域小于标题栏高度的一半，则完全展示，否则收回剪裁区域到顶部
        final int endTop = (isFocusShow || (startTop > -stickHeight / 2)) ? 0 : -canScrollHeight;
        final int startPadding = recyclerView.getPaddingTop();
        final float startAlpha = maskView.getAlpha();
        ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (Float) animation.getAnimatedValue();
                int dis = (int) ((endTop - startTop) * ratio + startTop);
                topView.setTranslationY(dis);
                float maskAlpha = endTop == 0 ? (-startAlpha) * ratio + startAlpha : (1 - startAlpha) * ratio + startAlpha;
                setMaskAlpha(maskAlpha);
                int padding = (int) (((endTop == 0 ? topView.getHeight() : stickHeight) - startPadding) * ratio + startPadding);
                setRecyclerViewPaddingTop(padding);
                if (ratio == 1.0f) {
                    if (scrollToPosition == 0) {
                        recyclerView.scrollToPosition(0);
                    } else if (scrollToPosition != -1) {
                        recyclerView.smoothScrollToPosition(scrollToPosition);
                    }
                }
            }
        });
        anim.start();
    }

    public int dp(int dp) {
        return PViewSizeUtils.dp(recyclerView.getContext(), dp);
    }
}
