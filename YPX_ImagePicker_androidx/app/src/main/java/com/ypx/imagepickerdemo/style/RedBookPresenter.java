package com.ypx.imagepickerdemo.style;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.adapter.PickerItemAdapter;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickConstants;
import com.ypx.imagepicker.data.ITakePhoto;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.views.PickerUiProvider;
import com.ypx.imagepicker.views.base.PickerControllerView;
import com.ypx.imagepicker.views.base.PickerFolderItemView;
import com.ypx.imagepicker.views.base.PickerItemView;
import com.ypx.imagepicker.views.redbook.RedBookItemView;
import com.ypx.imagepicker.views.redbook.RedBookTitleBar;
import com.ypx.imagepicker.views.redbook.RedBookUiProvider;
import com.ypx.imagepickerdemo.R;

import java.util.ArrayList;


/**
 * 小红书剪裁样式Presenter实现类
 */
public class RedBookPresenter implements IPickerPresenter {

    @Override
    public void displayImage(View view, ImageItem item, int size, boolean isThumbnail) {
        if (isThumbnail) {
            Glide.with(view.getContext()).load(item.path).override(size).into((ImageView) view);
        } else {
            Glide.with(view.getContext()).load(item.path).apply(new RequestOptions()
                    .format(DecodeFormat.PREFER_ARGB_8888)).into((ImageView) view);
        }
    }

    /**
     * @param context 上下文
     * @return PickerUiConfig UI配置类
     */
    @Override
    public PickerUiConfig getUiConfig(Context context) {
        PickerUiConfig uiConfig = new PickerUiConfig();
        uiConfig.setShowStatusBar(false);
        uiConfig.setStatusBarColor(Color.BLACK);
        uiConfig.setPickerBackgroundColor(Color.BLACK);
        uiConfig.setFolderListOpenDirection(PickerUiConfig.DIRECTION_TOP);
        uiConfig.setFolderListOpenMaxMargin(PViewSizeUtils.dp(context, 200));

        uiConfig.setPickerUiProvider(new RedBookUiProvider());
        return uiConfig;
    }

    @Override
    public void tip(Context context, String msg) {
        if (context == null) {
            return;
        }
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    @Override
    public void overMaxCountTip(Context context, int maxCount) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("最多选择" + maxCount + "个文件");
        builder.setPositiveButton(R.string.picker_str_sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean interceptPickerCompleteClick(Activity activity, ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
        return false;
    }

    /**
     * 拦截选择器取消操作，用于弹出二次确认框
     *
     * @param activity     当前选择器页面
     * @param selectedList 当前已经选择的文件列表
     * @return true:则拦截选择器取消， false，不处理选择器取消操作
     */
    @Override
    public boolean interceptPickerCancel(final Activity activity, ArrayList<ImageItem> selectedList) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("是否放弃选择？");
        builder.setPositiveButton(R.string.picker_str_sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        activity.finish();
                    }
                });
        builder.setNegativeButton(R.string.picker_str_error,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public boolean interceptItemClick(@Nullable Context context, ImageItem imageItem,
                                      ArrayList<ImageItem> selectImageList,
                                      ArrayList<ImageItem> allSetImageList,
                                      BaseSelectConfig selectConfig,
                                      PickerItemAdapter adapter) {
        return false;
    }

    @Override
    public boolean interceptCameraClick(@Nullable Activity activity, ITakePhoto takePhoto) {
        return false;
    }

    /**
     * @param context context
     * @return 配置选择器一些提示文本和常量
     */
    @NonNull
    @Override
    public PickConstants getPickConstants(Context context) {
        PickConstants pickConstants = new PickConstants(context);
        pickConstants.picker_str_only_select_image = "我是自定义文本";
        //以下省略若干常量配置
        return new PickConstants(context);
    }
}
