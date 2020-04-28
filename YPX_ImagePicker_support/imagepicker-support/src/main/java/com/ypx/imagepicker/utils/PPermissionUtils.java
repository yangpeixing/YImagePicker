package com.ypx.imagepicker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.ypx.imagepicker.BuildConfig;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;

/**
 * Description: 权限工具类
 * <p>
 * Author: peixing.yang
 * Date: 2019/3/1
 */
public class PPermissionUtils {
    private Context context;

    public static boolean hasCameraPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, ImagePicker.REQ_CAMERA);
                return false;
            }
        }
        return true;
    }

    public static boolean hasStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ImagePicker.REQ_STORAGE);
                return false;
            }
        }
        return true;
    }

    public static PPermissionUtils create(Context context) {
        return new PPermissionUtils(context);
    }

    public PPermissionUtils(Context context) {
        this.context = context;
    }

    public void showSetPermissionDialog(final String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setNegativeButton(context.getString(R.string.picker_str_permission_refuse_setting),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (msg.equals(context.getString(R.string.picker_str_storage_permission))) {
                            ((Activity) context).finish();
                        }
                    }
                });
        builder.setPositiveButton(context.getString(R.string.picker_str_permission_go_setting),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        gotoPermissionSet();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void gotoPermissionSet() {
        String brand = Build.BRAND;//手机厂商
        if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
            gotoMiuiPermission();//小米
        } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
            gotoMeizuPermission();
        } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
            gotoHuaweiPermission();
        } else {
            context.startActivity(getAppDetailSettingIntent());
        }
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private void gotoMiuiPermission() {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                context.startActivity(getAppDetailSettingIntent());
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private void gotoMeizuPermission() {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent());
        }
    }

    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent());
        }

    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }

}
