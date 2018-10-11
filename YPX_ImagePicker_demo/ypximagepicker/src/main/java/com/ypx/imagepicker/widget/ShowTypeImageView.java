package com.ypx.imagepicker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ypx.imagepicker.R;


/**
 * 作者：yangpeixing on 2017/12/7 16:40
 * 功能：可以根据宽高和类型动态显示长图和gif图标签
 * 产权：南京婚尚信息技术
 */
public class ShowTypeImageView extends ImageView {
    public static final int TYPE_GIF = 1;//gif图片
    public static final int TYPE_LONG = 2;//长图
    public static final int TYPE_NONE = 3;//正常图
    public static final int TYPE_IMAGECOUNT = 4;//正常图

    protected int imageType = TYPE_NONE;

    private String imageCountTip = "";

    public ShowTypeImageView(Context context) {
        super(context);
    }

    public ShowTypeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowTypeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public void setType(int type) {
        this.imageType = type;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (imageType == TYPE_NONE) {
            return;
        }
        int width = getWidth();
        int height = getHeight();
        Paint mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.parseColor("#ffffff"));


        Paint mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#90000000"));
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.normal_textsize12));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        switch (imageType) {
            case TYPE_GIF:
                mCirclePaint.setAlpha(200);
                canvas.drawCircle(width / 2, height / 2, width * 0.18f, mCirclePaint);
                canvas.drawText("GIF", width / 2 - dp(10), height / 2 + dp(5), mTextPaint);
                break;

            case TYPE_LONG:
                mCirclePaint.setAlpha(200);
                RectF rectF = new RectF(width - dp(30), height - dp(20), width + dp(3), height);
                canvas.drawRoundRect(rectF, dp(3), dp(3), mCirclePaint);
                canvas.drawText("长图", width - dp(27), height - dp(6), mTextPaint);
                break;

            case TYPE_IMAGECOUNT:
                mCirclePaint.setAlpha(200);
                RectF rectF2 = new RectF(width - dp(30), height - dp(20), width + dp(3), height);
                canvas.drawRoundRect(rectF2, dp(3), dp(3), mCirclePaint);
                canvas.drawText(imageCountTip, width - dp(27), height - dp(6), mTextPaint);
                break;
        }
    }

    public void setImageCountTip(String imageCountTip) {
        this.imageCountTip = imageCountTip;
        this.imageType = TYPE_IMAGECOUNT;
        invalidate();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (bm.getHeight() > bm.getWidth() * 3 && bm.getHeight() > getScreenHeight() * 1.5f) {
            setType(TYPE_LONG);
        }
    }

    public void setTypeWithUrlAndSize(String url, int width, int height) {
        if (imageType == TYPE_IMAGECOUNT) {
            return;
        }
        if (url != null && url.contains(".gif")) {
            setType(TYPE_GIF);
        } else if (height > width * 3 && height > getScreenHeight() * 1.5f) {
            setType(TYPE_LONG);
        } else {
            setType(TYPE_NONE);
        }
    }

    /**
     * 获得屏幕高度
     */
    public int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public int dp(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, this.getResources().getDisplayMetrics());
    }
}
