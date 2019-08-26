package com.ypx.imagepicker.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Description: 可监听滑动的recyclerView
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/22
 */
public class TouchRecyclerView extends RecyclerView {
    public TouchRecyclerView(@NonNull Context context) {
        super(context);
    }

    public TouchRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    float firstY = 0;
    float lastY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();

                if (y < lastY) {
                    if (isTouchPointInView(touchView, ev.getX(), ev.getY())) {
                        if (dragScrollListener != null) {
                            int distance = (int) ((y - lastY));
                            int defaultDis = (int) (lastY - getPaddingTop());
                            dragScrollListener.onScrollOverTop(Math.abs(distance + defaultDis));
                        }
                    }
                } else {
                    if (dragScrollListener != null) {
                        dragScrollListener.onScrollDown((int) (y - lastY));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastY = 0;
                if (dragScrollListener != null) {
                    dragScrollListener.onScrollUp();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private View touchView;

    public void setTouchView(View view) {
        this.touchView = view;
    }

    //(x,y)是否在view的区域内
    private boolean isTouchPointInView(View view, float x, float y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        //view.isClickable() &&
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    private onDragScrollListener dragScrollListener;

    public void setDragScrollListener(onDragScrollListener dragScrollListener) {
        this.dragScrollListener = dragScrollListener;
    }

    public interface onDragScrollListener {
        void onScrollOverTop(int distance);

        void onScrollDown(int distance);

        void onScrollUp();
    }
}
