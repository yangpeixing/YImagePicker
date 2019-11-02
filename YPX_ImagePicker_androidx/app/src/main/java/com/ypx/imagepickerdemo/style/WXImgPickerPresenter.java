package com.ypx.imagepickerdemo.style;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickConstants;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepickerdemo.R;

import java.util.ArrayList;

/**
 * Description:<br> 多选选择器数据绑定类
 *
 * @author honglin.zhang@yoho.cn<br>
 * @version 1.0.0 <br>
 * @date 2019/2/27 13:49<br>
 */
public class WXImgPickerPresenter implements IMultiPickerBindPresenter {

    @Override
    public void displayListImage(ImageView imageView, ImageItem item, int size) {
        Glide.with(imageView.getContext()).load(item.path).into(imageView);
    }

    @Override
    public void displayPerViewImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).apply(new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)).into(imageView);
    }

    @Override
    public PickerUiConfig getUiConfig(Context context) {
        PickerUiConfig config = new PickerUiConfig();
        //是否沉浸式状态栏，状态栏颜色将根据TopBarBackgroundColor指定，
        // 并动态更改状态栏图标颜色
        config.setImmersionBar(true);
        //设置主题色
        config.setThemeColor(Color.parseColor("#09C768"));
        //设置选中和未选中时图标
        config.setSelectedIconID(R.mipmap.picker_wechat_select);
        config.setUnSelectIconID(R.mipmap.picker_wechat_unselect);
        //设置返回图标以及返回图标颜色
        config.setBackIconID(R.mipmap.picker_icon_back_black);
        config.setBackIconColor(Color.BLACK);
        //设置标题栏背景色和对齐方式，设置标题栏文本颜色
        config.setTitleBarBackgroundColor(Color.parseColor("#F1F1F1"));
        config.setTitleBarGravity(Gravity.START);
        config.setTitleColor(Color.BLACK);
        //设置标题栏右上角完成按钮选中和未选中样式，以及文字颜色
        int r = PViewSizeUtils.dp(context, 2);
        config.setOkBtnSelectBackground(PCornerUtils.cornerDrawable(Color.parseColor("#09C768"), r));
        config.setOkBtnUnSelectBackground(PCornerUtils.cornerDrawable(Color.parseColor("#B4ECCE"), r));
        config.setOkBtnSelectTextColor(Color.WHITE);
        config.setOkBtnUnSelectTextColor(Color.parseColor("#50ffffff"));
        config.setOkBtnText("完成");
        //设置选择器背景色
        config.setPickerBackgroundColor(Color.WHITE);
        //设置选择器item背景色
        config.setPickerItemBackgroundColor(Color.parseColor("#484848"));
        //设置底部栏颜色
        config.setBottomBarBackgroundColor(Color.parseColor("#333333"));
        //设置拍照按钮图标和背景色
        config.setCameraIconID(R.mipmap.picker_ic_camera);
        config.setCameraBackgroundColor(Color.parseColor("#484848"));
        return config;
    }

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    @Override
    public void tip(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    @Override
    public void overMaxCountTip(Context context, int maxCount) {
        tip(context, "最多选择" + maxCount + "个文件");
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

    /**
     * 满足selectConfig.isVideoSinglePick()==true 时，才会触发此方法
     * 在单选视频里，点击视频item会触发此方法
     *
     * @param activity  页面
     * @param imageItem 当前选中视频
     * @return true:则拦截外部回调，直接执行该方法， false，不处理视频点击
     */
    @Override
    public boolean interceptVideoClick(Activity activity, ImageItem imageItem) {
        Toast.makeText(activity, "点击视频啦" + imageItem.path, Toast.LENGTH_SHORT).show();
        return true;
    }

    @NonNull
    @Override
    public PickConstants getPickConstants(Context context) {
        return new PickConstants(context);
    }


    /**
     * 图片点击事件
     *
     * @param context         上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param adapter         当前列表适配器，用于刷新数据
     *                        <p>
     *                        该方法只有在setPreview(false)的时候才会调用，默认点击图片会跳转预览页面。如果指定了剪裁模式，则不走该方法
     */
    @Override
    public void imageItemClick(Context context, ImageItem imageItem, ArrayList<ImageItem> selectImageList,
                               ArrayList<ImageItem> allSetImageList, MultiGridAdapter adapter) {
        if (selectImageList == null || adapter == null) {
            return;
        }
        tip(context, "我是自定义的图片点击事件");
        adapter.preformCheckItem(imageItem);
    }
}
