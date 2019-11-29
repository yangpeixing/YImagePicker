package com.ypx.imagepicker.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * Description: View尺寸相关工具类
 * <p>
 * Author: peixing.yang
 * Date: 2018/12/24-15:40
 */
final public class PViewSizeUtils {
    public static void setViewSize(View view, int width, int height) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(width, height);
            } else {
                if (width != -1) {
                    params.width = width;
                }
                if (height != -1) {
                    params.height = height;
                }
            }
            viewWeakReference.get().setLayoutParams(params);
        }
    }


    public static void setViewSize(View view, int width, float widthHeightRatio) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(width, (int) (width / widthHeightRatio));
            } else {
                if (width != -1) {
                    params.width = width;
                }
                if (widthHeightRatio != 0) {
                    params.height = (int) (width / widthHeightRatio);
                }
            }
            viewWeakReference.get().setLayoutParams(params);
        }
    }

    public static void setViewSize(View view, int width, int height, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            if (viewWeakReference.get().getLayoutParams() != null &&
                    (viewWeakReference.get().getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                params.width = width;
                params.height = height;
                if (marginLeft != -1) {
                    params.leftMargin = marginLeft;
                }
                if (marginRight != -1) {
                    params.rightMargin = marginRight;
                }
                if (marginTop != -1) {
                    params.topMargin = marginTop;
                }
                if (marginBottom != -1) {
                    params.bottomMargin = marginBottom;
                }
                viewWeakReference.get().setLayoutParams(params);
            }
        }
    }


    public static void setViewMargin(View view, int margin) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            if (viewWeakReference.get().getLayoutParams() != null &&
                    (viewWeakReference.get().getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                if (margin != -1) {
                    params.leftMargin = margin;
                    params.rightMargin = margin;
                    params.topMargin = margin;
                    params.bottomMargin = margin;
                }
                viewWeakReference.get().setLayoutParams(params);
            }
        }
    }

    /**
     * 获取View的高度
     *
     * @param v view
     * @return 高度
     */
    public static int getViewHeight(View v) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if (params != null) {
            return params.height;
        }
        return v.getHeight();
    }

    /**
     * 获取View的宽度
     *
     * @param v view
     * @return 宽度
     */
    public static int getViewWidth(View v) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if (params != null) {
            return params.width;
        }
        return v.getWidth();
    }

    public static void setMarginStart(View view, int marginStart) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (params != null) {
                params.leftMargin = marginStart;
                viewWeakReference.get().setLayoutParams(params);
            }
        }
    }

    public static void setMarginStartAndEnd(View view, int marginStart, int marginEnd) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (params != null) {
                params.leftMargin = marginStart;
                params.rightMargin = marginEnd;
                viewWeakReference.get().setLayoutParams(params);
            }
        }
    }

    public static void setMarginTopAndBottom(View view, int marginTop, int marginBottom) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (params != null) {
                params.topMargin = marginTop;
                params.bottomMargin = marginBottom;
                viewWeakReference.get().setLayoutParams(params);
            }
        }
    }

    public static void setMarginTop(View view, int marginTop) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (params != null) {
                params.topMargin = marginTop;
                viewWeakReference.get().setLayoutParams(params);
            }
        }
    }

    public static int getMarginTop(View view) {
        WeakReference<View> viewWeakReference = new WeakReference<>(view);
        if (viewWeakReference.get() != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            if (params != null) {
                return params.topMargin;
            }
        }
        return 0;
    }

    public static int dp(Context context, float dp) {
        if (context == null) {
            return 0;
        }
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static int sp(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 两个颜色渐变转化
     *
     * @param color1 默认色
     * @param color2 目标色
     * @param ratio  渐变率（0~1）
     * @return 计算后的颜色
     */
    public static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio)
                + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio)
                + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio)
                + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }


    private static long lastTime = 0L;

    public static boolean onDoubleClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;

        if (time > 300) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return !flag;
    }
}
