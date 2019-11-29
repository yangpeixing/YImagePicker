package com.ypx.imagepicker.bean;

import android.content.Context;

import com.ypx.imagepicker.R;

/**
 * Time: 2019/11/2 12:55
 * Author:ypx
 * Description: 选择器文案修改类，可在presenter中修改
 */
public class PickConstants {
    //拍照权限话术
    public String picker_str_camera_permission;
    //存储权限话术
    public String picker_str_storage_permission;
    //图片和视频
    public String picker_str_multi_title;
    //视频选择
    public String picker_str_multi_title_video;
    //图片选择
    public String picker_str_multi_title_image;
    //图片剪裁
    public String picker_str_crop_title;
    //充满
    public String picker_str_full;
    //留白
    public String picker_str_gap;
    //该文件已选过或无法选择
    public String picker_str_shield;
    //图片未加载完成，请稍候!
    public String picker_str_wait_for_load;
    //资源加载中,请稍后…
    public String picker_str_loading;
    //该视频文件路径无效或已损坏!
    public String picker_str_video_error;
    //暂未发现媒体文件
    public String picker_str_media_not_found;
    //只能选择图片!
    public String picker_str_only_select_image;
    //只能选择视频!
    public String picker_str_only_select_video;
    //视频时长不得超过
    public String picker_str_video_over_max_duration;
    //视频时长不得少于
    public String picker_str_video_less_min_duration;
    //拍摄视频
    public String picker_str_take_video;
    //拍摄照片
    public String picker_str_take_photo;

    public PickConstants(Context context) {
        picker_str_camera_permission = context.getResources().getString(R.string.picker_str_camera_permission);
        picker_str_storage_permission = context.getResources().getString(R.string.picker_str_storage_permission);
        picker_str_multi_title = context.getResources().getString(R.string.picker_str_multi_title);
        picker_str_multi_title_video = context.getResources().getString(R.string.picker_str_multi_title_video);
        picker_str_multi_title_image = context.getResources().getString(R.string.picker_str_multi_title_image);
        picker_str_crop_title = context.getResources().getString(R.string.picker_str_crop_title);
        picker_str_full = context.getResources().getString(R.string.picker_str_full);
        picker_str_gap = context.getResources().getString(R.string.picker_str_gap);
        picker_str_shield = context.getResources().getString(R.string.picker_str_shield);
        picker_str_wait_for_load = context.getResources().getString(R.string.picker_str_wait_for_load);
        picker_str_loading = context.getResources().getString(R.string.picker_str_loading);
        picker_str_video_error = context.getResources().getString(R.string.picker_str_video_error);
        picker_str_media_not_found = context.getResources().getString(R.string.picker_str_media_not_found);
        picker_str_only_select_image = context.getResources().getString(R.string.picker_str_only_select_image);
        picker_str_only_select_video = context.getResources().getString(R.string.picker_str_only_select_video);
        picker_str_video_over_max_duration = context.getResources().getString(R.string.picker_str_video_over_max_duration);
        picker_str_video_less_min_duration = context.getResources().getString(R.string.picker_str_video_less_min_duration);
        picker_str_take_video = context.getResources().getString(R.string.picker_str_take_video);
        picker_str_take_photo = context.getResources().getString(R.string.picker_str_take_photo);
    }
}
