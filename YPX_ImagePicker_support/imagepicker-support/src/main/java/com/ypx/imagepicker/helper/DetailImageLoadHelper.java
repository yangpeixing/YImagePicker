package com.ypx.imagepicker.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PBitmapUtils;

public class DetailImageLoadHelper {

    public static void displayDetailImage(final Activity activity, final ImageView imageView,
                                          final IPickerPresenter presenter, final ImageItem imageItem) {
        if (imageItem.width == 0 || imageItem.height == 0 ||
                imageItem.isLongImage()) {
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
                                imageItem.width = finalBitmap.getWidth();
                                imageItem.height = finalBitmap.getHeight();
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
