package com.ypx.imagepicker.bean;

import android.content.Context;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;

import java.util.ArrayList;

/**
 * Time: 2019/10/18 9:18
 * Author:ypx
 * Description: 选择器Item不可选中的原因码
 */
public class PickerItemDisableCode {
    public static final int NORMAL = 0;
    public static final int DISABLE_IN_SHIELD = 1;
    public static final int DISABLE_OVER_MAX_COUNT = 2;
    public static final int DISABLE_ONLY_SELECT_IMAGE = 3;
    public static final int DISABLE_ONLY_SELECT_VIDEO = 4;
    public static final int DISABLE_VIDEO_OVER_MAX_DURATION = 5;
    public static final int DISABLE_VIDEO_LESS_MIN_DURATION = 6;
    public static final int DISABLE_VIDEO_ONLY_SINGLE_PICK = 7;

    public static String getMessageFormCode(Context context, int code, IPickerPresenter presenter, BaseSelectConfig selectConfig) {
        String message = "";
        switch (code) {
            case DISABLE_IN_SHIELD:
                message = context.getString(R.string.picker_str_tip_shield);
                break;
            case DISABLE_OVER_MAX_COUNT:
                presenter.overMaxCountTip(context, selectConfig.getMaxCount());
                message = "";
                break;
            case DISABLE_ONLY_SELECT_IMAGE:
                message = context.getString(R.string.picker_str_tip_only_select_image);
                break;
            case DISABLE_ONLY_SELECT_VIDEO:
                message = context.getString(R.string.picker_str_tip_only_select_video);
                break;
            case DISABLE_VIDEO_OVER_MAX_DURATION:
                message = context.getString(R.string.picker_str_str_video_over_max_duration)
                        + selectConfig.getMaxVideoDurationFormat(context);
                break;
            case DISABLE_VIDEO_LESS_MIN_DURATION:
                message = context.getString(R.string.picker_str_tip_video_less_min_duration)
                        + selectConfig.getMinVideoDurationFormat(context);
                break;
            case DISABLE_VIDEO_ONLY_SINGLE_PICK:
                message = context.getString(R.string.picker_str_tip_only_select_one_video);
                break;
        }
        return message;
    }


    public static int getItemDisableCode(ImageItem imageItem, BaseSelectConfig selectConfig,
                                         ArrayList<ImageItem> selectList,
                                         boolean isContainsThisItem) {
        boolean isItemEnable = true;
        int disableCode = PickerItemDisableCode.NORMAL;

        //如果在屏蔽列表中，代表不可选择
        if (selectConfig.isShieldItem(imageItem)) {
            isItemEnable = false;
            disableCode = PickerItemDisableCode.DISABLE_IN_SHIELD;
        }

        //如果是视频item
        if (imageItem.isVideo()) {
            //如果只能选择图片和视频类型一种，并且当前已经选择了图片，则该视频不可以选中
            if (isItemEnable
                    && selectConfig.isSinglePickImageOrVideoType()
                    && selectedFirstItemIsImage(selectList)) {
                isItemEnable = false;
                disableCode = PickerItemDisableCode.DISABLE_ONLY_SELECT_IMAGE;
            }
            //视频时长不符合选择条件
            else if (isItemEnable
                    && imageItem.duration > selectConfig.getMaxVideoDuration()) {
                isItemEnable = false;
                disableCode = PickerItemDisableCode.DISABLE_VIDEO_OVER_MAX_DURATION;
            } else if (isItemEnable
                    && imageItem.duration < selectConfig.getMinVideoDuration()) {
                isItemEnable = false;
                disableCode = PickerItemDisableCode.DISABLE_VIDEO_LESS_MIN_DURATION;
            }
            //如果视频只能单选并且已经选过视频
            else if (isItemEnable
                    && selectConfig.isVideoSinglePick()
                    && isSelectedListContainsVideo(selectList)
                    && !isContainsThisItem) {
                isItemEnable = false;
                disableCode = PickerItemDisableCode.DISABLE_VIDEO_ONLY_SINGLE_PICK;
            }
        }
        //如果是图片item
        else {
            //如果只能选择图片和视频类型一种，并且当前已经选择了视频，则该图片不可以选中
            if (selectConfig.isSinglePickImageOrVideoType()
                    && selectedFirstItemIsVideo(selectList)) {
                isItemEnable = false;
                disableCode = PickerItemDisableCode.DISABLE_ONLY_SELECT_VIDEO;
            }
        }

        //已经超过最大选中数量
        if (isItemEnable && hasSelectedList(selectList) && selectList.size() >= selectConfig.getMaxCount()
                && !isContainsThisItem) {
            disableCode = PickerItemDisableCode.DISABLE_OVER_MAX_COUNT;
        }

        return disableCode;
    }

    private static boolean selectedFirstItemIsVideo(ArrayList<ImageItem> selectList) {
        return hasSelectedList(selectList) && selectList.get(0) != null && selectList.get(0).isVideo();
    }

    private static boolean selectedFirstItemIsImage(ArrayList<ImageItem> selectList) {
        return hasSelectedList(selectList) && selectList.get(0) != null && !selectList.get(0).isVideo();
    }

    private static boolean hasSelectedList(ArrayList<ImageItem> selectList) {
        return selectList != null && selectList.size() > 0;
    }


    private static boolean isSelectedListContainsVideo(ArrayList<ImageItem> selectList) {
        for (ImageItem imageItem : selectList) {
            if (imageItem.isVideo()) {
                return true;
            }
        }
        return false;
    }
}
