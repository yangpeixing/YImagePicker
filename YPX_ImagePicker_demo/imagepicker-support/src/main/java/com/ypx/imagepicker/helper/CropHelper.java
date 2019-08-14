package com.ypx.imagepicker.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.ImageView;

import com.ypx.imagepicker.widget.browseimage.PicBrowseImageView;

public class CropHelper {

    public static Bitmap cropViewToBitmap(PicBrowseImageView imageView, int cropWidth, int cropHeight) {
        Bitmap bitmap = imageView.getOriginalBitmap();
        if (bitmap == null) {
            return null;
        }

        //水平平移像素点
        float x = Math.abs(imageView.getmTranslateX());
        //垂直平移像素点
        float y = Math.abs(imageView.getmTranslateY());
        //缩放比例
        float scale = imageView.getmScale();
        //原图宽度(Glide压缩过的，Glide默认加载会减小大图的宽高)
        int bw = bitmap.getWidth();
        //原图高度(Glide压缩过的)
        int bh = bitmap.getHeight();

        float bitmapShowWidth = bw;//图片默认显示的宽度
        float bitmapShowHeight = bh;//图片默认显示的高度

        float endW = 0;
        float endH = 0;
        float endX = 0;
        float endY = 0;
        float cropRatio = (cropWidth * 1.00f / (cropHeight * 1.00f));

        if (imageView.getmScaleType() == ImageView.ScaleType.CENTER_INSIDE) {//留白
            //TODO 留白

            


            // int bitmapShowWidth=  imageView.getImgRect().height();

        } else if (imageView.getmScaleType() == ImageView.ScaleType.FIT_CENTER) {//充满
            if (bw > bh) {//宽图，高填充剪裁区域（屏幕宽），宽度自适应
                endH = bh / scale;
                endW = cropRatio * endH;
                endX = bh * x / (cropHeight * scale * 1.00f);
                endY = bh * y / (cropHeight * scale * 1.00f);
            } else {//高图或者方形图，宽填充剪裁区域，高度自适应
                endW = bw / scale;
                endH = endW / cropRatio;
                endX = bw * x / (cropWidth * scale * 1.00f);
                endY = bw * y / (cropWidth * scale * 1.00f);
            }
        }

        Log.e("CropHelper", "cropViewToBitmap: cropWidth:" + cropWidth + " cropHeight:" + cropHeight + " x:" + x + " y:" + y + " scale:" + scale
                + " bw:" + bw + " bh:" + bh + "  endX:" + endX + " endY:" + endY + " endW:" + endW + " endH:" + endH);

        if (endX + endW > bw) {
            endX = bw - endW;
            if (endX < 0) {
                endX = 0;
            }
        }

        if (endY + endH > bh) {
            endY = bh - endH;
            if (endY < 0) {
                endY = 0;
            }
        }

        Bitmap bitmap1 = null;
        try {
            bitmap1 = Bitmap.createBitmap(bitmap, (int) endX, (int) endY, (int) endW, (int) endH);
        } catch (Exception ignored) {
        }

        return bitmap1;
    }


    /**
     * 合并两张bitmap为一张
     */
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2.00f,
                (bgHeight - fgHeight) / 2.00f, null);
      //  canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newmap;
    }

}
