package com.example.ypxredbookpicker.helper;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.ypxredbookpicker.utils.ViewSizeUtils;
import com.example.ypxredbookpicker.widget.TouchRecyclerView;

/**
 * Description: TODO
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

    public RecyclerViewTouchHelper build() {
        recyclerView.setPadding(0, canScrollHeight + stickHeight, 0, 0);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isImageGridCantScroll()) {
                    return;
                }

                if (isTopViewFullShow()) {
                    isTopViewStick = false;
                    setMaskAlpha(0);
                }

                if (isTopViewStick) {
                    int translate = -getScrollYDistance() - topView.getHeight();
                    if (translate < -canScrollHeight) {
                        topView.setTranslationY(-canScrollHeight);
                    } else {
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
                if (!recyclerView.canScrollVertically(1)) {
                    return;
                }
                if (distance > canScrollHeight) {
                    maskView.setVisibility(View.VISIBLE);
                    maskView.setAlpha(1.0f);
                    topView.setTranslationY(-canScrollHeight);
                    recyclerView.setPadding(0, stickHeight, 0, 0);
                } else {
                    if (topView.getTranslationY() != -canScrollHeight) {
                        float ratio = -distance * 1.00f / (-canScrollHeight * 1.00f);
                        setMaskAlpha(ratio);
                        topView.setTranslationY(-distance);
                    }
                }
            }

            @Override
            public void onScrollDown(int distance) {
                if (isImageGridCantScroll()) {
                    return;
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    recyclerView.setPadding(0, topView.getHeight(), 0, 0);
                    isTopViewStick = true;
                }
            }

            @Override
            public void onScrollUp() {
                if (isImageGridCantScroll()) {
                    return;
                }
                if (isScrollTopView && !isTopViewStick) {
                    if (!recyclerView.canScrollVertically(1)) {
                        transitTopWithAnim(true, -1);
                        return;
                    }
                    transitTopWithAnim(false, -1);
                }
                if (isTopViewStick && !isTopViewFullShow() && !isScrollTopView) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(0);
                            maskView.setAlpha(0);
                            maskView.setVisibility(View.GONE);
                        }
                    });
                }
                isScrollTopView = false;
            }
        });
        return this;
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
        return (topView.getTranslationY() < dp(3) && topView.getTranslationY() > -dp(3));
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
        int itemHeight = firstVisibleChildView.getHeight();
        return (position / 4) * itemHeight - firstVisibleChildView.getTop();
    }

    /**
     * 动画控制是否展开还是完整显示topView
     *
     * @param isFocusShow      是否强制完全展示
     * @param scrollToPosition 滑动到制定的位置
     */
    @SuppressLint("ObjectAnimatorBinding")
    public void transitTopWithAnim(boolean isFocusShow, final int scrollToPosition) {
        if (isTopViewFullShow()) {
            return;
        }
        final int startTop = (int) topView.getTranslationY();
        final int startPadding = recyclerView.getPaddingTop();
        final int endTop;
        //如果滑动区域小于标题栏高度的一半，则完全展示，否则收回剪裁区域到顶部
        if (isFocusShow || (startTop > -stickHeight / 2)) {
            endTop = 0;
            setMaskAlpha(0);
        } else {
            setMaskAlpha(1);
            endTop = -canScrollHeight;
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
                int padding = (int) (((endTop == 0 ? topView.getHeight() : stickHeight) - startPadding) * ratio + startPadding);
                recyclerView.setPadding(0, padding, 0, 0);
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
        return ViewSizeUtils.dp(recyclerView.getContext(), dp);
    }

}
