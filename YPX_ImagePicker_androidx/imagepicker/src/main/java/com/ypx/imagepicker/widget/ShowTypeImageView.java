package com.ypx.imagepicker.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.ImageView;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;

/**
 * 可以根据宽高和类型动态显示长图和gif图标签
 * <p>
 * yangpeixing on 2017/12/7 16:40
 */
@SuppressLint("AppCompatCustomView")
public class ShowTypeImageView extends ImageView {
    public static final int TYPE_GIF = 1;//gif图片
    public static final int TYPE_LONG = 2;//长图
    public static final int TYPE_NONE = 3;//正常图
    public static final int TYPE_VIDEO = 5;//视频
    public static final int TYPE_IMAGECOUNT = 4;//数量

    protected int imageType = TYPE_NONE;

    private String imageCountTip = "";

    private boolean isSelect = false;

    public ShowTypeImageView(Context context) {
        super(context);
        init();
    }

    public ShowTypeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShowTypeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setType(int type) {
        this.imageType = type;
        invalidate();
    }

    public void setSelect(boolean isSelect, int selectColor) {
        this.isSelect = isSelect;
        mSelectPaint.setColor(selectColor);
        invalidate();
    }

    private Paint mCirclePaint;
    private Paint mMaskPaint;
    private Paint mBitmapPaint;
    private Paint mTextPaint;
    private RectF rectF;
    private Paint mSelectPaint;
    private Bitmap videoBitmap;

    private void init() {
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.parseColor("#ffffff"));
        mCirclePaint.setAlpha(200);

        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setColor(Color.parseColor("#40000000"));

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#90000000"));
        mTextPaint.setTextSize(sp(12));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        rectF = new RectF();

        mSelectPaint = new Paint();
        mSelectPaint.setAntiAlias(true);
        mSelectPaint.setStrokeWidth(dp(4));
        mSelectPaint.setStyle(Paint.Style.STROKE);

        try {
            videoBitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.picker_item_video)).getBitmap();
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelect) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mSelectPaint);
        }

        if (imageType == TYPE_NONE) {
            return;
        }
        int width = getWidth();
        int height = getHeight();

        switch (imageType) {
            case TYPE_VIDEO:
                if (videoBitmap != null) {
                    canvas.drawRect(0, 0, width, height, mMaskPaint);
                    canvas.drawBitmap(videoBitmap, (width - videoBitmap.getWidth()) >> 1,
                            (height - videoBitmap.getHeight()) >> 1, mBitmapPaint);
                }

                break;
            case TYPE_GIF:
                canvas.drawCircle(width >> 1, height >> 1, width * 0.18f, mCirclePaint);
                canvas.drawText("GIF", (width >> 1) - dp(10), (height >> 1) + dp(5), mTextPaint);
                break;

            case TYPE_LONG:
                rectF.left = width - dp(30);
                rectF.top = height - dp(20);
                rectF.right = width + dp(3);
                rectF.bottom = height;
                canvas.drawRoundRect(rectF, dp(3), dp(3), mCirclePaint);
                canvas.drawText("长图", width - dp(27), height - dp(6), mTextPaint);
                break;

            case TYPE_IMAGECOUNT:
                rectF.left = width - dp(30);
                rectF.top = height - dp(20);
                rectF.right = width + dp(3);
                rectF.bottom = height;
                canvas.drawRoundRect(rectF, dp(3), dp(3), mCirclePaint);
                canvas.drawText(imageCountTip, width - dp(27), height - dp(6), mTextPaint);
                break;
        }


    }

    public int sp(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public void setImageCountTip(String imageCountTip) {
        this.imageCountTip = imageCountTip;
        this.imageType = TYPE_IMAGECOUNT;
        invalidate();
    }


    public void setTypeFromImage(ImageItem imageItem) {
        if (imageType == TYPE_IMAGECOUNT) {
            return;
        }
        if (imageItem.isVideo()) {
            setType(TYPE_VIDEO);
        } else if (imageItem.isGif()) {
            setType(TYPE_GIF);
        } else if (imageItem.isLongImage()) {
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
