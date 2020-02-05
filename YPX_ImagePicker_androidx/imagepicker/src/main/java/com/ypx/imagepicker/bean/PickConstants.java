package com.ypx.imagepicker.bean;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ypx.imagepicker.ImagePicker;

import java.lang.reflect.Field;

/**
 * Time: 2019/11/2 12:55
 * Author:ypx
 * Description: 选择器文案修改类，可在presenter中修改
 */
public class PickConstants {
    //---------- 选择页面标题栏 ------------
    //图片和视频
    public String picker_str_title_all;
    //视频选择
    public String picker_str_title_video;
    //图片选择
    public String picker_str_title_image;
    //完成
    public String picker_str_title_right;

    //---------- 单图剪裁页面标题栏------------
    //图片剪裁
    public String picker_str_title_crop;
    //确定
    public String picker_str_title_crop_right;

    //---------- 小红书剪裁状态文字------------
    //充满
    public String picker_str_redBook_full;
    //留白
    public String picker_str_redBook_gap;

    //---------- 预览页面底部栏------------
    //预览
    public String picker_str_bottom_preview;
    //原图
    public String picker_str_bottom_original;
    //选择
    public String picker_str_bottom_choose;

    //---------- 文件夹名称------------
    //图片和视频
    public String picker_str_folder_item_video;
    //所有视频
    public String picker_str_folder_item_all;
    //所有图片
    public String picker_str_folder_item_image;
    //张
    public String picker_str_folder_image_unit;

    //---------- 拍照item文本------------
    //拍摄视频
    public String picker_str_item_take_video;
    //拍摄照片
    public String picker_str_item_take_photo;

    //---------- 提示相关------------
    //拍照权限话术
    public String picker_str_camera_permission;
    //存储权限话术
    public String picker_str_storage_permission;

    //图片未加载完成，请稍候!
    public String picker_str_tip_shield;
    //暂未发现媒体文件
    public String picker_str_tip_media_empty;
    //只能选择图片!
    public String picker_str_tip_only_select_image;
    //只能选择视频!
    public String picker_str_tip_only_select_video;
    //视频时长不得超过
    public String picker_str_str_video_over_max_duration;
    //视频时长不得少于
    public String picker_str_tip_video_less_min_duration;
    //只能选择一个视频!
    public String picker_str_tip_only_select_one_video;
    //不支持预览视频！
    public String picker_str_tip_cant_preview_video;
    //请至少选择一种文件加载类型!
    public String picker_str_tip_mimeTypes_empty;
    //请勿操作过快!
    public String picker_str_tip_action_frequently;
    //剪裁异常，已为您重置图片，请重试！
    public String picker_str_tip_singleCrop_error;

    //---------- 时间相关------------
    public String picker_str_today;
    public String picker_str_this_week;
    public String picker_str_this_months;
    public String picker_str_time_format;
    public String picker_str_day;
    public String picker_str_hour;
    public String picker_str_minute;
    public String picker_str_second;
    public String picker_str_milli;

    public PickConstants(Context context) {
        Field[] field = getClass().getFields();
        String value;
        for (Field fi : field) {
            int stringID = context.getResources().getIdentifier(fi.getName(),
                    "string", context.getPackageName());
            try {
                value = context.getResources().getString(stringID);
            } catch (Resources.NotFoundException e) {
                value = "";
                Log.e("ImagePicker", "PickConstants: " + fi.getName() +
                        " string id not found,please add it in strings.xml");
            }
            try {
                fi.setAccessible(true);
                fi.set(this, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static PickConstants getConstants(Context context) {
        if (ImagePicker.pickConstants == null) {
            ImagePicker.pickConstants = new PickConstants(context.getApplicationContext());
        }
        return ImagePicker.pickConstants;
    }
}
