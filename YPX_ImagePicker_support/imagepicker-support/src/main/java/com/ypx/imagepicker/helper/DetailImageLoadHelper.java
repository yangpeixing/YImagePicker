package com.ypx.imagepicker.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

public class DetailImageLoadHelper {

    public static void displayDetailImage(final Activity activity, final CropImageView imageView,
                                          final IPickerPresenter presenter, final ImageItem imageItem) {
        if (imageItem.isLongImage()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    if (imageItem.isUriPath()) {
                        bitmap = PBitmapUtils.getBitmapFromUri(activity, imageItem.getUri());
                    } else {
                        bitmap = BitmapFactory.decodeFile(imageItem.path);
                    }
                    final Bitmap finalBitmap = bitmap;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (finalBitmap == null) {
                                if (presenter != null) {
                                    presenter.displayImage(imageView, imageItem, imageView.getWidth(), false);
                                }
                            } else {
                                imageView.setImageBitmap(finalBitmap);
                            }
                        }
                    });
                }
            }).start();
        } else {
            if (presenter != null) {
                presenter.displayImage(imageView, imageItem, imageView.getWidth(), false);
            }
        }
    }
}
