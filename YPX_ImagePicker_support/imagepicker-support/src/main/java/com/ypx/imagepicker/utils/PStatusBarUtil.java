package com.ypx.imagepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * 状态栏工具类
 */
public class PStatusBarUtil {

    /**
     * 是否有刘海屏
     */
    public static boolean hasNotchInScreen(Activity activity) {
        // android  P 以上有标准 API 来判断是否有刘海屏
        if (Build.VERSION.SDK_INT >= 28) {
            try {
                DisplayCutout displayCutout = activity.getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
                if (displayCutout != null) {
                    // 说明有刘海屏
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
            String manufacturer = Build.MANUFACTURER;
            if (manufacturer == null || manufacturer.length() == 0) {
                return false;
            } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
                return hasNotchHw(activity);
            } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
                return hasNotchXiaoMi(activity);
            } else if (manufacturer.equalsIgnoreCase("oppo")) {
                return hasNotchOPPO(activity);
            } else if (manufacturer.equalsIgnoreCase("vivo")) {
                return hasNotchVIVO(activity);
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断vivo是否有刘海屏
     * https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     *
     * @param activity
     * @return
     */
    public static boolean hasNotchVIVO(Activity activity) {
        try {
            Class<?> c = Class.forName("android.util.FtFeature");
            Method get = c.getMethod("isFeatureSupport", int.class);
            return (boolean) (get.invoke(c, 0x20));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断oppo是否有刘海屏
     * https://open.oppomobile.com/wiki/doc#id=10159
     *
     * @param activity
     * @return
     */
    public static boolean hasNotchOPPO(Activity activity) {
        try {
            return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     *
     * @param activity
     * @return
     */
    public static boolean hasNotchXiaoMi(Activity activity) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getInt", String.class, int.class);
            return (int) (get.invoke(c, "ro.miui.notch", 1)) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断华为是否有刘海屏
     * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
     */
    public static boolean hasNotchHw(Activity activity) {
        try {
            ClassLoader cl = activity.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            return false;
        }
    }

    public static void setStatusBar(Activity activity, int bgColor, boolean isFullScreen, boolean isDarkStatusBarIcon) {
        //5.0以下不处理
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        int option = 0;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //只有在6.0以上才改变状态栏颜色，否则在5.0机器上，电量条图标是白色的，标题栏也是白色的，就看不见电量条了了
        //在5.0上显示默认灰色背景色
        if (Build.VERSION.SDK_INT >= 23) {
            // 设置状态栏底色颜色
            activity.getWindow().setStatusBarColor(bgColor);
            //浅色状态栏，则让状态栏图标变黑，深色状态栏，则让状态栏图标变白
            if (isDarkStatusBarIcon) {
                if (isFullScreen) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
            } else {
                if (isFullScreen) {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
                } else {
                    option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_VISIBLE;
                }
            }
        } else {
            if (isFullScreen) {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
                option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            } else {
                activity.getWindow().setStatusBarColor(bgColor);
                option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(option);
    }


    /**
     * 显示标题背景颜色
     */
    public static boolean isDarkColor(int colorInt) {
        int gray = (int) (Color.red(colorInt) * 0.299 + Color.green(colorInt) * 0.587 + Color.blue(colorInt) * 0.114);
        return gray >= 192;
    }

    private static int statusBarHeight;

    /**
     * 利用反射获取状态栏高度
     */
    public static int getStatusBarHeight(Context activity) {
        if (statusBarHeight != 0) {
            return statusBarHeight;
        }
        try {
            int result = 0;
            //获取状态栏高度的资源id
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = activity.getResources().getDimensionPixelSize(resourceId);
            }
            statusBarHeight = result;
            return result;
        } catch (Exception e) {
            return PViewSizeUtils.dp(activity, 20);
        }
    }


    public static void fullScreenWithCheckNotch(Activity activity, int statusBarColor) {
        if (PStatusBarUtil.hasNotchInScreen(activity)) {
            PStatusBarUtil.setStatusBar(activity, statusBarColor, false, true);
        } else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void fullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}