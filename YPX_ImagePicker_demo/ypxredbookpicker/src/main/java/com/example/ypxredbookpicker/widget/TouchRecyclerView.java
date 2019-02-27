package com.example.ypxredbookpicker.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Description: TODO
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
                if (y < getPaddingTop() && y < lastY) {
                    if (dragScrollListener != null) {
                        int distance = (int) ((y - lastY));
                        int defaultDis = (int) (lastY - getPaddingTop());
                        dragScrollListener.onScrollOverTop(Math.abs(distance + defaultDis));
                    }
                } else {
                    if (y - lastY > 0) {
                        if (dragScrollListener != null) {
                            dragScrollListener.onScrollDown((int) (y - lastY));
                        }
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
