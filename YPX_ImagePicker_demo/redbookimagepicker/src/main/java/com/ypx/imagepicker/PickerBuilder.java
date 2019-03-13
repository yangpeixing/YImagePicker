package com.ypx.imagepicker;

import android.app.Activity;
import android.content.Intent;

import com.ypx.imagepicker.activity.ImagePickAndCropActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.helper.launcher.ActivityLauncher;
import com.ypx.imagepicker.utils.FileUtil;

import java.util.ArrayList;

/**
 * Description: 选择器构造类
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/28
 */
public class PickerBuilder {
    private int maxCount = 9;
    private ImageItem firstImageItem = null;
    private boolean isShowBottomView = false;
    private boolean isShowDraft = false;
    private ImageLoaderProvider imageLoaderProvider;

    PickerBuilder(ImageLoaderProvider imageLoaderProvider) {
        this.imageLoaderProvider = imageLoaderProvider;
    }

    public PickerBuilder setFirstImageItem(ImageItem firstImageItem) {
        this.firstImageItem = firstImageItem;
        return this;
    }

    /**
     * 在没有指定setFirstImageItem时，使用这个方法传入当前的第一张剪裁图片url,
     * 会生成一个新的FirstImageItem，其剪裁模式根据图片宽高决定，如果已经指定了FirstImageItem，则该方法无效
     *
     * @param firstImageUrl 第一张建材后的图片
     */
    public PickerBuilder setFirstImageUrl(String firstImageUrl) {
        if (firstImageUrl == null || firstImageUrl.length() == 0 || firstImageItem != null) {
            return this;
        }
        this.firstImageItem = new ImageItem();
        firstImageItem.setCropUrl(firstImageUrl);
        int[] imageSize = FileUtil.getImageWidthHeight(firstImageUrl);
        firstImageItem.width = imageSize[0];
        firstImageItem.height = imageSize[1];

        if (firstImageItem.getWidthHeightType() == 0) {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FILL);
        } else {
            firstImageItem.setCropMode(ImageCropMode.CropViewScale_FIT);
        }
        return this;
    }

    public PickerBuilder setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        return this;
    }

    public PickerBuilder showBottomView(boolean isShowBottomView) {
        this.isShowBottomView = isShowBottomView;
        return this;
    }

    public PickerBuilder showDraftDialog(boolean isShowDraft) {
        this.isShowDraft = isShowDraft;
        return this;
    }

    public PickerBuilder setCropPicSaveFilePath(String cropPicSaveFilePath) {
        MarsImagePicker.cropPicSaveFilePath = cropPicSaveFilePath;
        return this;
    }

    private Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, ImagePickAndCropActivity.class);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_IMAGELOADER, imageLoaderProvider);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_MAXSELECTEDCOUNT, maxCount);
        if (firstImageItem != null) {
            intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_FIRSTIMAGEITEM, firstImageItem);
        }
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_CROPPICSAVEFILEPATH, MarsImagePicker.cropPicSaveFilePath);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SHOWBOTTOMVIEW, isShowBottomView);
        intent.putExtra(ImagePickAndCropActivity.INTENT_KEY_SHOWDRAFTDIALOG, isShowDraft);
        return intent;

    }

    public void pick(Activity activity, final OnImagePickCompleteListener listener) {
        ActivityLauncher.init(activity).startActivityForResult(getIntent(activity), new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (data != null && data.hasExtra(MarsImagePicker.INTENT_KEY_PICKERRESULT)
                        && resultCode == MarsImagePicker.REQ_PICKER_RESULT_CODE && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(MarsImagePicker.INTENT_KEY_PICKERRESULT);
                    listener.onImagePickComplete(list);
                }
            }
        });
    }

    public void pick(Activity activity) {
        activity.startActivityForResult(getIntent(activity),
                MarsImagePicker.REQ_PICKER_RESULT_CODE);
    }
}
