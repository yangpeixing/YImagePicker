package com.ypx.imagepicker.widget.cropimage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.Scroller;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.utils.PBitmapUtils;

/**
 * Description: 剪裁ImageView
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
@SuppressLint("AppCompatCustomView")
public class CropImageView extends ImageView {
    private final static int MIN_ROTATE = 35;
    private final static int ANIM_DURING = 340;
    private final static float MAX_SCALE = 2.5f;
    private static int loadMaxSize = 0;

    private int mMinRotate;
    private int mAnimDuring;
    private float mMaxScale;

    private int MAX_FLING_OVER_SCROLL = 0;
    private int MAX_OVER_RESISTANCE = 0;

    private Matrix mBaseMatrix = new Matrix();
    private Matrix mAnimMatrix = new Matrix();
    private Matrix mSynthesisMatrix = new Matrix();
    private Matrix mTmpMatrix = new Matrix();

    private RotateGestureDetector mRotateDetector;
    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleDetector;
    private OnClickListener mClickListener;

    private ScaleType mScaleType = ScaleType.CENTER_INSIDE;

    private boolean hasMultiTouch;
    private boolean hasDrawable;
    private boolean isKnowSize;
    private boolean hasOverTranslate;
    private boolean isEnable = false;
    private boolean isRotateEnable = false;
    // 当前是否处于放大状态
    private boolean isZoomUp;
    private boolean canRotate;

    private boolean imgLargeWidth;
    private boolean imgLargeHeight;

    private float mRotateFlag;
    private float mDegrees;
    private float mScale = 1.0f;
    private int mTranslateX;
    private int mTranslateY;

    private RectF mCropRect = new RectF();
    private RectF mBaseRect = new RectF();
    private RectF mImgRect = new RectF();
    private RectF mTmpRect = new RectF();
    private RectF mCommonRect = new RectF();

    private PointF mScreenCenter = new PointF();
    private PointF mScaleCenter = new PointF();
    private PointF mRotateCenter = new PointF();

    private Paint linePaint;

    private Transform mTranslate = new Transform();

    private RectF mClip;
    private Runnable mCompleteCallBack;

    private OnLongClickListener mLongClick;

    private boolean isShowCropRect = true;

    public CropImageView(Context context) {
        super(context);
        init();
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setScaleType(ScaleType.MATRIX);
        if (mScaleType == null) mScaleType = ScaleType.CENTER_CROP;
        mRotateDetector = new RotateGestureDetector(mRotateListener);
        mDetector = new GestureDetector(getContext(), mGestureListener);
        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        float density = getResources().getDisplayMetrics().density;
        MAX_FLING_OVER_SCROLL = (int) (density * 30);
        MAX_OVER_RESISTANCE = (int) (density * 140);

        mMinRotate = MIN_ROTATE;
        mAnimDuring = ANIM_DURING;
        mMaxScale = MAX_SCALE;

        initCropLineRect();
        initCropRect();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mClickListener = l;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType == ScaleType.MATRIX) return;

        if (scaleType != mScaleType) {
            mScaleType = scaleType;
            initBase();
        }
    }

    public ScaleType getNewScaleType() {
        return mScaleType;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        mLongClick = l;
    }


    /**
     * 设置最大可以缩放的倍数
     */
    public void setMaxScale(float maxScale) {
        mMaxScale = maxScale;
    }

    /**
     * 启用缩放功能
     */
    public void enable() {
        isEnable = true;
    }

    @Override
    public void setImageResource(int resId) {
        Drawable drawable = null;
        try {
            drawable = getResources().getDrawable(resId);
        } catch (Exception ignored) {
        }

        setImageDrawable(drawable);
    }

    private Bitmap originalBitmap;

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        if (bm == null || bm.getWidth() == 0 || bm.getHeight() == 0) {
            return;
        }

        originalBitmap = bm;

        if (loadMaxSize == 0) {
            loadMaxSize = Math.max(bm.getWidth(), bm.getHeight());
        }

        float ratio = bm.getWidth() * 1.00f / bm.getHeight() * 1.00f;
        if (bm.getWidth() > loadMaxSize) {
            bm = Bitmap.createScaledBitmap(bm, loadMaxSize, (int) (loadMaxSize / ratio), false);
        }

        if (bm.getHeight() > loadMaxSize) {
            bm = Bitmap.createScaledBitmap(bm, (int) (loadMaxSize * ratio), loadMaxSize, false);
        }

        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if (drawable == null) {
            hasDrawable = false;
            return;
        }
        if (!hasSize(drawable)) {
            return;
        }

        hasDrawable = true;
        if (originalBitmap == null) {
            if (drawable instanceof BitmapDrawable) {
                originalBitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof AnimationDrawable) {
                AnimationDrawable drawable1 = (AnimationDrawable) drawable;
                Drawable drawable2 = drawable1.getFrame(0);
                if (drawable2 instanceof BitmapDrawable) {
                    originalBitmap = ((BitmapDrawable) drawable2).getBitmap();
                }
            }
        }

        if (onImageLoadListener != null) {
            onImageLoadListener.onImageLoaded(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            onImageLoadListener = null;
        }

        if (restoreInfo != null) {
            mScaleType = restoreInfo.getScaleType();
            mCropRect = restoreInfo.mWidgetRect;
            aspectX = (int) restoreInfo.mCropX;
            aspectY = (int) restoreInfo.mCropY;
            initBase();
            post(new Runnable() {
                @Override
                public void run() {
                    restoreCrop();
                }
            });
        } else {
            initBase();
        }
    }

    private Info restoreInfo;

    public void setRestoreInfo(Info restoreInfo) {
        this.restoreInfo = restoreInfo;
    }

    /**
     * 恢复状态
     */
    private void restoreCrop() {
        Info info = restoreInfo;

        mTranslateX = 0;
        mTranslateY = 0;

        if (info == null || info.mImgRect == null) {
            return;
        }

        float tcx = info.mImgRect.left + info.mImgRect.width() / 2;
        float tcy = info.mImgRect.top + info.mImgRect.height() / 2;

        mScaleCenter.set(mImgRect.left + mImgRect.width() / 2, mImgRect.top + mImgRect.height() / 2);
        mRotateCenter.set(mScaleCenter);

        // 将图片旋转回正常位置，用以计算
        mAnimMatrix.postRotate(-mDegrees, mScaleCenter.x, mScaleCenter.y);
        mAnimMatrix.mapRect(mImgRect, mBaseRect);

        // 缩放
        float scaleX = info.mImgRect.width() / mBaseRect.width();
        float scaleY = info.mImgRect.height() / mBaseRect.height();
        float scale = scaleX > scaleY ? scaleX : scaleY;

        mAnimMatrix.postRotate(mDegrees, mScaleCenter.x, mScaleCenter.y);
        mAnimMatrix.mapRect(mImgRect, mBaseRect);

        mDegrees = mDegrees % 360;

        mTranslate.withTranslate(0, 0, (int) (tcx - mScaleCenter.x), (int) (tcy - mScaleCenter.y));
        mTranslate.withScale(mScale, scale);
        mTranslate.withRotate((int) mDegrees, (int) info.mDegrees, mAnimDuring * 2 / 3);
        mTranslate.start();

        restoreInfo = null;
    }

    onImageLoadListener onImageLoadListener;

    public interface onImageLoadListener {
        void onImageLoaded(float w, float h);
    }

    public void setOnImageLoadListener(onImageLoadListener onImageLoadListener) {
        this.onImageLoadListener = onImageLoadListener;
    }

    private boolean hasSize(Drawable d) {
        return (d.getIntrinsicHeight() > 0 && d.getIntrinsicWidth() > 0)
                || (d.getMinimumWidth() > 0 && d.getMinimumHeight() > 0)
                || (d.getBounds().width() > 0 && d.getBounds().height() > 0);
    }

    private static int getDrawableWidth(Drawable d) {
        int width = d.getIntrinsicWidth();
        if (width <= 0) width = d.getMinimumWidth();
        if (width <= 0) width = d.getBounds().width();
        return width;
    }

    private static int getDrawableHeight(Drawable d) {
        int height = d.getIntrinsicHeight();
        if (height <= 0) height = d.getMinimumHeight();
        if (height <= 0) height = d.getBounds().height();
        return height;
    }

    float baseScale;

    public void initBase() {
        if (!hasDrawable) return;
        if (!isKnowSize) return;

        mBaseMatrix.reset();
        mAnimMatrix.reset();
        isZoomUp = false;

        Drawable img = getDrawable();

        int w = getWidth();
        int h = getHeight();
        int drawableWidth = getDrawableWidth(img);
        int drawableHeight = getDrawableHeight(img);

        mBaseRect.set(0, 0, drawableWidth, drawableHeight);

        // 以图片中心点居中位移
        int tx = (w - drawableWidth) / 2;
        int ty = (h - drawableHeight) / 2;

        float sx = 1;
        float sy = 1;

        // 缩放，默认不超过屏幕大小
        if (drawableWidth > w) {
            sx = (float) w / drawableWidth;
        }

        if (drawableHeight > h) {
            sy = (float) h / drawableHeight;
        }

        baseScale = Math.min(sx, sy);

        mBaseMatrix.reset();
        mBaseMatrix.postTranslate(tx, ty);
        mBaseMatrix.postScale(baseScale, baseScale, mScreenCenter.x, mScreenCenter.y);
        mBaseMatrix.mapRect(mBaseRect);

        mScaleCenter.set(mScreenCenter);
        mRotateCenter.set(mScaleCenter);

        executeTranslate();

        switch (mScaleType) {
            case CENTER:
                initCenter();
                break;
            case CENTER_CROP:
                initCenterCrop();
                break;
            case CENTER_INSIDE:
                initCenterInside();
                break;
            case FIT_CENTER:
                initFitCenter();
                break;
            case FIT_START:
                initFitStart();
                break;
            case FIT_END:
                initFitEnd();
                break;
            case FIT_XY:
                initFitXY();
                break;
        }
    }

    private void initCenter() {
        mAnimMatrix.postScale(1, 1, mScreenCenter.x, mScreenCenter.y);
        executeTranslate();
        resetBase();
    }

    private void initCenterCrop() {
        float widthScale = mCropRect.width() / mImgRect.width();
        float heightScale = mCropRect.height() / mImgRect.height();
        mScale = Math.max(widthScale, heightScale);
        mAnimMatrix.postScale(mScale, mScale, mScreenCenter.x, mScreenCenter.y);
        executeTranslate();
        resetBase();
    }

    private void initCenterInside() {
        //控件大于图片，即可完全显示图片，相当于Center，反之，相当于FitCenter
        if (mCropRect.width() > mImgRect.width()) {
            initCenter();
        } else {
            initFitCenter();
        }
        float widthScale = mCropRect.width() / mImgRect.width();
        if (widthScale > mMaxScale) {
            mMaxScale = widthScale;
        }
    }

    private void initFitCenter() {
        float widthScale = mCropRect.width() / mImgRect.width();
        float heightScale = mCropRect.height() / mImgRect.height();
        mScale = Math.min(widthScale, heightScale);
        mAnimMatrix.postScale(mScale, mScale, mScreenCenter.x, mScreenCenter.y);
        executeTranslate();
        resetBase();

        if (widthScale > mMaxScale) {
            mMaxScale = widthScale;
        }
    }

    private void initFitStart() {
        initFitCenter();
        float ty = -mImgRect.top;
        mAnimMatrix.postTranslate(0, ty);
        executeTranslate();
        resetBase();
        mTranslateY += ty;
    }

    private void initFitEnd() {
        initFitCenter();
        float ty = (mCropRect.bottom - mImgRect.bottom);
        mTranslateY += ty;
        mAnimMatrix.postTranslate(0, ty);
        executeTranslate();
        resetBase();
    }

    private void initFitXY() {
        float widthScale = mCropRect.width() / mImgRect.width();
        float heightScale = mCropRect.height() / mImgRect.height();
        mAnimMatrix.postScale(widthScale, heightScale, mScreenCenter.x, mScreenCenter.y);
        executeTranslate();
        resetBase();
    }

    private void resetBase() {
        Drawable img = getDrawable();
        mBaseRect.set(0, 0, getDrawableWidth(img), getDrawableHeight(img));
        mBaseMatrix.set(mSynthesisMatrix);
        mBaseMatrix.mapRect(mBaseRect);
        mScale = 1;
        mTranslateX = 0;
        mTranslateY = 0;
        mAnimMatrix.reset();
    }

    private void executeTranslate() {
        mSynthesisMatrix.set(mBaseMatrix);
        mSynthesisMatrix.postConcat(mAnimMatrix);
        setImageMatrix(mSynthesisMatrix);
        mAnimMatrix.mapRect(mImgRect, mBaseRect);
        imgLargeWidth = mImgRect.width() >= mCropRect.width();
        imgLargeHeight = mImgRect.height() >= mCropRect.height();
    }

    private int aspectX = -1, aspectY = -1;
    private int cropMargin = 0;

    public void setCropRatio(int aspectX, int aspectY) {
        this.aspectX = aspectX;
        this.aspectY = aspectY;
        if (cropAnim != null && cropAnim.isRunning()) {
            cropAnim.cancel();
        }
        if (aspectX <= 0 || aspectY <= 0) {
            mCropRect.set(0, 0, getWidth(), getHeight());
            mScaleType = ScaleType.CENTER_INSIDE;
            initBase();
            invalidate();
            return;
        }

        mScaleType = ScaleType.CENTER_CROP;
        resetCropSize(getWidth(), getHeight());
    }

    public boolean isEditing() {
        return isShowLine;
    }

    public void setCropMargin(int cropMargin) {
        this.cropMargin = cropMargin;
    }

    public int getCropWidth() {
        return (int) mCropRect.width();
    }

    public int getCropHeight() {
        return (int) mCropRect.height();
    }

    private void resetCropSize(int w, int h) {
        float left = 0, top = 0, right = w, bottom = h;
        if (aspectY != -1 && aspectX != -1) {
            float cropRatio = aspectX * 1.00f / aspectY;
            float viewRatio = w * 1.00f / h;
            if (h > w) {//view的高>宽
                float top1 = (h - (w - cropMargin * 2) * 1.00f / cropRatio) * 1.00f / 2;
                if (cropRatio >= 1) {//宽比例剪裁
                    left = cropMargin;
                    right = w - left;
                    top = top1;
                    bottom = h - top;
                } else if (cropRatio < 1) {//高比例剪裁
                    if (cropRatio > viewRatio) {//剪裁比例大于view宽高比，说明以宽充满，剪裁的高肯定不会超出view的高
                        left = cropMargin;
                        right = w - left;
                        top = top1;
                        bottom = h - top;
                    } else {//剪裁比例小于view宽高比,说明以高充满，宽度肯定不会超过view的宽度
                        top = cropMargin;
                        bottom = h - top;
                        left = (w - (h - cropMargin * 2) * cropRatio) / 2;
                        right = w - left;

                    }
                }
            }
            anim(left, top, right, bottom);
        } else {
            mCropRect.set(left, top, right, bottom);
            initBase();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isKnowSize = true;
        mScreenCenter.set(w / 2.0f, h / 2.0f);
        resetCropSize(w, h);
        setImageDrawable(getDrawable());
    }

    public void setRotateEnable(boolean rotateEnable) {
        isRotateEnable = rotateEnable;
    }

    private boolean isShowLine = false;
    private Paint cropRectPaint;
    private Paint maskPaint;
    private Paint cropStrokePaint;

    private void initCropRect() {
        cropRectPaint = new Paint();
        cropRectPaint.setStrokeWidth(dp(2f));
        cropRectPaint.setColor(Color.WHITE);
        cropRectPaint.setAntiAlias(true);
        cropRectPaint.setStyle(Paint.Style.STROKE);
        cropRectPaint.setDither(true);
        initMaskPaint();
    }

    private void initCropLineRect() {
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(dp(0.5f));
        linePaint.setStyle(Paint.Style.FILL);

        cropStrokePaint = new Paint();
        cropStrokePaint.setColor(Color.WHITE);
        cropStrokePaint.setAntiAlias(true);
        cropStrokePaint.setStrokeCap(Paint.Cap.ROUND);
        cropStrokePaint.setStrokeWidth(dp(4));
        cropStrokePaint.setStyle(Paint.Style.STROKE);
    }

    private void initMaskPaint() {
        maskPaint = new Paint();
        maskPaint.setColor(Color.parseColor("#a0000000"));
        maskPaint.setAntiAlias(true);
        maskPaint.setStyle(Paint.Style.FILL);
    }

    private Rect viewDrawingRect = new Rect();
    private Path path = new Path();

    private void drawStrokeLine(Canvas canvas) {
        int lineWidth = dp(30);
        float x = mCropRect.left;
        float y = mCropRect.top + dp(1);
        float w = mCropRect.width();
        float h = mCropRect.height() - dp(2);
        canvas.drawLine(x, y, lineWidth + x, y, cropStrokePaint);
        canvas.drawLine(x, y, x, y + lineWidth, cropStrokePaint);
        canvas.drawLine(x, y + h, x, y + h - lineWidth, cropStrokePaint);
        canvas.drawLine(x, y + h, x + lineWidth, y + h, cropStrokePaint);
        canvas.drawLine(x + w, y, x + w - lineWidth, y, cropStrokePaint);
        canvas.drawLine(x + w, y, x + w, y + lineWidth, cropStrokePaint);
        canvas.drawLine(x + w, y + h, x + w - lineWidth, y + h, cropStrokePaint);
        canvas.drawLine(x + w, y + h, x + w, y + h - lineWidth, cropStrokePaint);
    }

    private boolean isShowImageRectLine = false;
    private boolean canShowTouchLine = true;
    private boolean isCircle = false;

    public void setCircle(boolean circle) {
        isCircle = circle;
        invalidate();
    }

    public void setShowImageRectLine(boolean showImageRectLine) {
        isShowImageRectLine = showImageRectLine;
        invalidate();
    }

    public void setCanShowTouchLine(boolean canShowTouchLine) {
        this.canShowTouchLine = canShowTouchLine;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception ignored) {
            loadMaxSize = (int) (loadMaxSize * 0.8);
            setImageBitmap(originalBitmap);
            return;
        }

        if (isShowLine && canShowTouchLine && !isCircle) {
            int left, top, right, bottom, w, h;
            if (isShowImageRectLine) {
                left = mImgRect.left > mCropRect.left ? (int) mImgRect.left : (int) mCropRect.left;
                top = (int) mImgRect.top > mCropRect.top ? (int) mImgRect.top : (int) mCropRect.top;
                right = mImgRect.right < mCropRect.right ? (int) mImgRect.right : (int) mCropRect.right;
                bottom = mImgRect.bottom < mCropRect.bottom ? (int) mImgRect.bottom : (int) mCropRect.bottom;
                w = right - left;
                h = bottom - top;
            } else {
                w = (int) mCropRect.width();
                h = (int) mCropRect.height();
                left = (int) mCropRect.left;
                top = (int) mCropRect.top;
            }
            canvas.drawLine(left + w / 3.0f, top, left + w / 3.0f, h + top, linePaint);
            canvas.drawLine(left + w * 2 / 3.0f, top, left + w * 2 / 3.0f, h + top, linePaint);
            canvas.drawLine(left, top + h / 3.0f, left + w, top + h / 3.0f, linePaint);
            canvas.drawLine(left, top + h * 2 / 3.0f, left + w, top + h * 2 / 3.0f, linePaint);
        }

        if (!isShowCropRect || aspectY <= 0 || aspectX <= 0) {
            return;
        }

        getDrawingRect(viewDrawingRect);
        path.reset();
        if (isCircle) {
            path.addCircle(mCropRect.left + mCropRect.width() / 2, mCropRect.top + mCropRect.height() / 2, mCropRect.width() / 2, Path.Direction.CW);
        } else {
            drawStrokeLine(canvas);
            path.addRect(mCropRect.left, mCropRect.top, mCropRect.right, mCropRect.bottom, Path.Direction.CW);
        }
        canvas.clipPath(path, android.graphics.Region.Op.DIFFERENCE);
        canvas.drawRect(viewDrawingRect, maskPaint);
        canvas.drawPath(path, cropRectPaint);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mClip != null) {
            canvas.clipRect(mClip);
            mClip = null;
        }
        super.draw(canvas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnable) {
            final int Action = event.getActionMasked();
            if (event.getPointerCount() >= 2) hasMultiTouch = true;

            mDetector.onTouchEvent(event);
            if (isRotateEnable) {
                mRotateDetector.onTouchEvent(event);
            }
            mScaleDetector.onTouchEvent(event);
            if (Action == MotionEvent.ACTION_DOWN) {
                isShowLine = true;
                invalidate();
            } else if (Action == MotionEvent.ACTION_UP || Action == MotionEvent.ACTION_CANCEL) {
                onUp();
                isShowLine = false;
                invalidate();
            }

            return true;
        } else {
            return super.dispatchTouchEvent(event);
        }
    }

    private void onUp() {
        if (mTranslate.isRunning)
            return;

        if (canRotate || mDegrees % 90 != 0) {
            float toDegrees = (int) (mDegrees / 90) * 90;
            float remainder = mDegrees % 90;

            if (remainder > 45)
                toDegrees += 90;
            else if (remainder < -45)
                toDegrees -= 90;

            mTranslate.withRotate((int) mDegrees, (int) toDegrees);

            mDegrees = toDegrees;
        }

        if (!isBounceEnable) {
            return;
        }

        float cx = mImgRect.left * 1.00f + mImgRect.width() / 2;
        float cy = mImgRect.top * 1.00f + mImgRect.height() / 2;

        mRotateCenter.set(cx, cy);

        if (mScale < 1) {
            mTranslate.withScale(mScale, 1);
            mScale = 1;
        } else if (mScale > mMaxScale) {
            mTranslate.withScale(mScale, mMaxScale);
            mScale = mMaxScale;
        }

        mScaleCenter.set(cx, cy);
        mTranslateX = 0;
        mTranslateY = 0;

        mTmpMatrix.reset();
        mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top);
        mTmpMatrix.postTranslate(cx - mBaseRect.width() / 2, cy - mBaseRect.height() / 2);
        mTmpMatrix.postScale(mScale, mScale, mScaleCenter.x, mScaleCenter.y);
        mTmpMatrix.postRotate(mDegrees, cx, cy);
        mTmpMatrix.mapRect(mTmpRect, mBaseRect);

        doTranslateReset(mTmpRect);
        mTranslate.start();
    }

    private boolean isBounceEnable = true;

    public void setBounceEnable(boolean isBounceEnable) {
        this.isBounceEnable = isBounceEnable;
    }

    private void doTranslateReset(RectF imgRect) {
        int tx = 0;
        int ty = 0;

        int width = (int) (mCropRect.width());
        int height = (int) (mCropRect.height());

        if (imgRect.width() <= width) {
            if (!isImageCenterWidth(imgRect)) {
                if (aspectX > 0 && aspectY > 0) {
                    tx = (int) (imgRect.left - mCropRect.left);
                } else {
                    tx = -(int) ((mCropRect.width() - imgRect.width()) / 2 - imgRect.left);
                }
            }
        } else {
            if (imgRect.left > mCropRect.left) {
                tx = (int) (imgRect.left - mCropRect.left);
            } else if (imgRect.right < mCropRect.right) {
                tx = (int) (imgRect.right - mCropRect.right);
            }
        }

        if (imgRect.height() <= height) {
            if (!isImageCenterHeight(imgRect))
                if (aspectX > 0 && aspectY > 0) {
                    ty = (int) (imgRect.top - mCropRect.top);
                } else {
                    ty = -(int) ((mCropRect.height() - imgRect.height()) / 2 - imgRect.top);
                }
        } else {
            if (imgRect.top > mCropRect.top) {
                ty = (int) (imgRect.top - mCropRect.top);
            } else if (imgRect.bottom < mCropRect.bottom) {
                ty = (int) (imgRect.bottom - mCropRect.bottom);
            }
        }

        if (tx != 0 || ty != 0) {
            if (!mTranslate.mFlingScroller.isFinished()) mTranslate.mFlingScroller.abortAnimation();
            mTranslate.withTranslate(mTranslateX, mTranslateY, -tx, -ty);
        }
    }

    private boolean isImageCenterHeight(RectF rect) {
        return Math.abs(Math.round(rect.top) - (mCropRect.height() - rect.height()) / 2) < 1;
    }

    private boolean isImageCenterWidth(RectF rect) {
        return Math.abs(Math.round(rect.left) - (mCropRect.width() - rect.width()) / 2) < 1;
    }

    private RotateGestureDetector.OnRotateListener mRotateListener = new RotateGestureDetector.OnRotateListener() {
        @Override
        public void onRotate(float degrees, float focusX, float focusY) {
            mRotateFlag += degrees;
            if (canRotate) {
                mDegrees += degrees;
                mAnimMatrix.postRotate(degrees, focusX, focusY);
            } else {
                if (Math.abs(mRotateFlag) >= mMinRotate) {
                    canRotate = true;
                    mRotateFlag = 0;
                }
            }
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();

            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                return false;

            if (mScale > mMaxScale) {
                return true;
            }

            mScale *= scaleFactor;
            mScaleCenter.set(detector.getFocusX(), detector.getFocusY());
            mAnimMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            executeTranslate();
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    private float resistanceScrollByX(float overScroll, float detalX) {
        return detalX * (Math.abs(Math.abs(overScroll) - MAX_OVER_RESISTANCE) / (float) MAX_OVER_RESISTANCE);
    }

    private float resistanceScrollByY(float overScroll, float detalY) {
        return detalY * (Math.abs(Math.abs(overScroll) - MAX_OVER_RESISTANCE) / (float) MAX_OVER_RESISTANCE);
    }

    /**
     * 匹配两个Rect的共同部分输出到out，若无共同部分则输出0，0，0，0
     */
    private void mapRect(RectF r1, RectF r2, RectF out) {
        float l, r, t, b;

        l = r1.left > r2.left ? r1.left : r2.left;
        r = r1.right < r2.right ? r1.right : r2.right;

        if (l > r) {
            out.set(0, 0, 0, 0);
            return;
        }

        t = r1.top > r2.top ? r1.top : r2.top;
        b = r1.bottom < r2.bottom ? r1.bottom : r2.bottom;

        if (t > b) {
            out.set(0, 0, 0, 0);
            return;
        }

        out.set(l, t, r, b);
    }

    private void checkRect() {
        if (!hasOverTranslate) {
            mapRect(mCropRect, mImgRect, mCommonRect);
        }
    }

    private Runnable mClickRunnable = new Runnable() {
        @Override
        public void run() {
            if (mClickListener != null) {
                mClickListener.onClick(CropImageView.this);
            }
        }
    };

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
            if (mLongClick != null) {
                mLongClick.onLongClick(CropImageView.this);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            hasOverTranslate = false;
            hasMultiTouch = false;
            canRotate = false;
            removeCallbacks(mClickRunnable);
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (hasMultiTouch) return false;
            if (!imgLargeWidth && !imgLargeHeight) return false;
            if (mTranslate.isRunning) return false;

            float vx = velocityX;
            float vy = velocityY;

            if (Math.round(mImgRect.left) >= mCropRect.left || Math.round(mImgRect.right) <= mCropRect.right) {
                vx = 0;
            }

            if (Math.round(mImgRect.top) >= mCropRect.top || Math.round(mImgRect.bottom) <= mCropRect.bottom) {
                vy = 0;
            }

            if (canRotate || mDegrees % 90 != 0) {
                float toDegrees = (int) (mDegrees / 90) * 90;
                float remainder = mDegrees % 90;

                if (remainder > 45)
                    toDegrees += 90;
                else if (remainder < -45)
                    toDegrees -= 90;

                mTranslate.withRotate((int) mDegrees, (int) toDegrees);
                mDegrees = toDegrees;
            }
            mTranslate.withFling(vx, vy);
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mTranslate.isRunning) {
                mTranslate.stop();
            }
            if (canScrollHorizontallySelf(distanceX)) {
                if (distanceX < 0 && mImgRect.left - distanceX > mCropRect.left)
                    distanceX = mImgRect.left;
                if (distanceX > 0 && mImgRect.right - distanceX < mCropRect.right)
                    distanceX = mImgRect.right - mCropRect.right;

                mAnimMatrix.postTranslate(-distanceX, 0);
                mTranslateX -= distanceX;
            } else if (imgLargeWidth || hasMultiTouch || hasOverTranslate || !isBounceEnable) {
                checkRect();
                if (!hasMultiTouch || !isBounceEnable) {
                    if (distanceX < 0 && mImgRect.left - distanceX > mCommonRect.left)
                        distanceX = resistanceScrollByX(mImgRect.left - mCommonRect.left, distanceX);
                    if (distanceX > 0 && mImgRect.right - distanceX < mCommonRect.right)
                        distanceX = resistanceScrollByX(mImgRect.right - mCommonRect.right, distanceX);
                }

                mTranslateX -= distanceX;
                mAnimMatrix.postTranslate(-distanceX, 0);
                hasOverTranslate = true;
            }

            if (canScrollVerticallySelf(distanceY)) {
                if (distanceY < 0 && mImgRect.top - distanceY > mCropRect.top)
                    distanceY = mImgRect.top;
                if (distanceY > 0 && mImgRect.bottom - distanceY < mCropRect.bottom)
                    distanceY = mImgRect.bottom - mCropRect.bottom;

                mAnimMatrix.postTranslate(0, -distanceY);
                mTranslateY -= distanceY;
            } else if (imgLargeHeight || hasOverTranslate || hasMultiTouch || !isBounceEnable) {
                checkRect();
                if (!hasMultiTouch || !isBounceEnable) {
                    if (distanceY < 0 && mImgRect.top - distanceY > mCommonRect.top)
                        distanceY = resistanceScrollByY(mImgRect.top - mCommonRect.top, distanceY);
                    if (distanceY > 0 && mImgRect.bottom - distanceY < mCommonRect.bottom)
                        distanceY = resistanceScrollByY(mImgRect.bottom - mCommonRect.bottom, distanceY);
                }

                mAnimMatrix.postTranslate(0, -distanceY);
                mTranslateY -= distanceY;
                hasOverTranslate = true;
            }

            executeTranslate();
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            postDelayed(mClickRunnable, 250);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mTranslate.stop();

            float from;
            float to;

            float imageCenterX = mImgRect.left + mImgRect.width() / 2;
            float imageCenterY = mImgRect.top + mImgRect.height() / 2;

            mScaleCenter.set(imageCenterX, imageCenterY);
            mRotateCenter.set(imageCenterX, imageCenterY);
            mTranslateX = 0;
            mTranslateY = 0;

            if (mScale > 1) {
                from = mScale;
                to = 1;
            } else {
                from = mScale;
                to = mMaxScale;
                mScaleCenter.set(e.getX(), e.getY());
            }

            mTmpMatrix.reset();
            mTmpMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top);
            mTmpMatrix.postTranslate(mRotateCenter.x, mRotateCenter.y);
            mTmpMatrix.postTranslate(-mBaseRect.width() / 2, -mBaseRect.height() / 2);
            mTmpMatrix.postRotate(mDegrees, mRotateCenter.x, mRotateCenter.y);
            mTmpMatrix.postScale(to, to, mScaleCenter.x, mScaleCenter.y);
            mTmpMatrix.postTranslate(mTranslateX, mTranslateY);
            mTmpMatrix.mapRect(mTmpRect, mBaseRect);
            doTranslateReset(mTmpRect);

            isZoomUp = !isZoomUp;
            mTranslate.withScale(from, to);
            mTranslate.start();

            return false;
        }
    };

    public boolean canScrollHorizontallySelf(float direction) {
        if (mImgRect.width() <= mCropRect.width()) return false;
        if (direction < 0 && Math.round(mImgRect.left) - direction >= mCropRect.left)
            return false;
        return !(direction > 0) || !(Math.round(mImgRect.right) - direction <= mCropRect.right);
    }

    public boolean canScrollVerticallySelf(float direction) {
        if (mImgRect.height() <= mCropRect.height()) return false;
        if (direction < 0 && Math.round(mImgRect.top) - direction >= mCropRect.top)
            return false;
        return !(direction > 0) || !(Math.round(mImgRect.bottom) - direction <= mCropRect.bottom);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (!isEnable) {
            return super.canScrollHorizontally(direction);
        }
        if (hasMultiTouch) return true;
        return canScrollHorizontallySelf(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        if (!isEnable) {
            return super.canScrollVertically(direction);
        }
        if (hasMultiTouch) return true;
        return canScrollVerticallySelf(direction);
    }

    private class InterpolatorProxy implements Interpolator {

        private Interpolator mTarget;

        private InterpolatorProxy() {
            mTarget = new DecelerateInterpolator();
        }

        void setTargetInterpolator(Interpolator interpolator) {
            mTarget = interpolator;
        }

        @Override
        public float getInterpolation(float input) {
            if (mTarget != null) {
                return mTarget.getInterpolation(input);
            }
            return input;
        }
    }

    private class Transform implements Runnable {

        boolean isRunning;

        OverScroller mTranslateScroller;
        OverScroller mFlingScroller;
        Scroller mScaleScroller;
        Scroller mClipScroller;
        Scroller mRotateScroller;

        ClipCalculate C;

        int mLastFlingX;
        int mLastFlingY;

        int mLastTranslateX;
        int mLastTranslateY;

        RectF mClipRect = new RectF();

        InterpolatorProxy mInterpolatorProxy = new InterpolatorProxy();

        Transform() {
            Context ctx = getContext();
            mTranslateScroller = new OverScroller(ctx, mInterpolatorProxy);
            mScaleScroller = new Scroller(ctx, mInterpolatorProxy);
            mFlingScroller = new OverScroller(ctx, mInterpolatorProxy);
            mClipScroller = new Scroller(ctx, mInterpolatorProxy);
            mRotateScroller = new Scroller(ctx, mInterpolatorProxy);
        }

        public void setInterpolator(Interpolator interpolator) {
            mInterpolatorProxy.setTargetInterpolator(interpolator);
        }

        void withTranslate(int startX, int startY, int deltaX, int deltaY) {
            mLastTranslateX = 0;
            mLastTranslateY = 0;
            mTranslateScroller.startScroll(startX, startY, deltaX, deltaY, mAnimDuring);
        }

        void withScale(float form, float to) {
            mScaleScroller.startScroll((int) (form * 10000), 0, (int) ((to - form) * 10000), 0, mAnimDuring);
        }

        void withRotate(int fromDegrees, int toDegrees) {
            mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, mAnimDuring);
        }

        void withRotate(int fromDegrees, int toDegrees, int during) {
            mRotateScroller.startScroll(fromDegrees, 0, toDegrees - fromDegrees, 0, during);
        }

        void withFling(float velocityX, float velocityY) {
            mLastFlingX = velocityX < 0 ? Integer.MAX_VALUE : 0;
            int distanceX = (int) (velocityX > 0 ? Math.abs(mImgRect.left) : mImgRect.right - mCropRect.right);
            distanceX = velocityX < 0 ? Integer.MAX_VALUE - distanceX : distanceX;
            int minX = velocityX < 0 ? distanceX : 0;
            int maxX = velocityX < 0 ? Integer.MAX_VALUE : distanceX;
            int overX = velocityX < 0 ? Integer.MAX_VALUE - minX : distanceX;

            mLastFlingY = velocityY < 0 ? Integer.MAX_VALUE : 0;
            int distanceY = (int) (velocityY > 0 ? Math.abs(mImgRect.top - mCropRect.top) : mImgRect.bottom - mCropRect.bottom);
            distanceY = velocityY < 0 ? Integer.MAX_VALUE - distanceY : distanceY;
            int minY = velocityY < 0 ? distanceY : 0;
            int maxY = velocityY < 0 ? Integer.MAX_VALUE : distanceY;
            int overY = velocityY < 0 ? Integer.MAX_VALUE - minY : distanceY;

            if (velocityX == 0) {
                maxX = 0;
                minX = 0;
            }

            if (velocityY == 0) {
                maxY = 0;
                minY = 0;
            }

            mFlingScroller.fling(mLastFlingX, mLastFlingY, (int) velocityX, (int) velocityY, minX, maxX, minY, maxY,
                    Math.abs(overX) < MAX_FLING_OVER_SCROLL * 2 ? 0 : MAX_FLING_OVER_SCROLL,
                    Math.abs(overY) < MAX_FLING_OVER_SCROLL * 2 ? 0 : MAX_FLING_OVER_SCROLL);
        }

        void start() {
            isRunning = true;
            postExecute();
        }

        void stop() {
            removeCallbacks(this);
            mTranslateScroller.abortAnimation();
            mScaleScroller.abortAnimation();
            mFlingScroller.abortAnimation();
            mRotateScroller.abortAnimation();
            isRunning = false;
        }

        @Override
        public void run() {
            if (!isRunning) return;

            boolean endAnim = true;

            if (mScaleScroller.computeScrollOffset()) {
                mScale = mScaleScroller.getCurrX() / 10000f;
                endAnim = false;
            }

            if (mTranslateScroller.computeScrollOffset()) {
                int tx = mTranslateScroller.getCurrX() - mLastTranslateX;
                int ty = mTranslateScroller.getCurrY() - mLastTranslateY;
                mTranslateX += tx;
                mTranslateY += ty;
                mLastTranslateX = mTranslateScroller.getCurrX();
                mLastTranslateY = mTranslateScroller.getCurrY();
                endAnim = false;
            }

            if (mFlingScroller.computeScrollOffset()) {
                int x = mFlingScroller.getCurrX() - mLastFlingX;
                int y = mFlingScroller.getCurrY() - mLastFlingY;

                mLastFlingX = mFlingScroller.getCurrX();
                mLastFlingY = mFlingScroller.getCurrY();

                mTranslateX += x;
                mTranslateY += y;
                endAnim = false;
            }

            if (mRotateScroller.computeScrollOffset()) {
                mDegrees = mRotateScroller.getCurrX();
                endAnim = false;
            }

            if (mClipScroller.computeScrollOffset() || mClip != null) {
                float sx = mClipScroller.getCurrX() / 10000f;
                float sy = mClipScroller.getCurrY() / 10000f;
                mTmpMatrix.setScale(sx, sy, (mImgRect.left + mImgRect.right) / 2, C.calculateTop());
                mTmpMatrix.mapRect(mClipRect, mImgRect);

                if (sx == 1) {
                    mClipRect.left = mCropRect.left;
                    mClipRect.right = mCropRect.right;
                }

                if (sy == 1) {
                    mClipRect.top = mCropRect.top;
                    mClipRect.bottom = mCropRect.bottom;
                }

                mClip = mClipRect;
            }
            if (!endAnim) {
                applyAnim();
                postExecute();
            } else {
                isRunning = false;
                if (aspectX > 0 && aspectY > 0) {
                    return;
                }
                // 修复动画结束后边距有些空隙，
                boolean needFix = false;
                if (imgLargeWidth) {
                    if (mImgRect.left > 0) {
                        mTranslateX -= mCropRect.left;
                    } else if (mImgRect.right < mCropRect.width()) {
                        mTranslateX -= (int) (mCropRect.width() - mImgRect.right);
                    }
                    needFix = true;
                }

                if (imgLargeHeight) {
                    if (mImgRect.top > 0) {
                        mTranslateY -= mCropRect.top;
                    } else if (mImgRect.bottom < mCropRect.height()) {
                        mTranslateY -= (int) (mCropRect.height() - mImgRect.bottom);
                    }
                    needFix = true;
                }

                if (needFix) {
                    applyAnim();
                }

                invalidate();
            }
            if (mCompleteCallBack != null) {
                mCompleteCallBack.run();
                mCompleteCallBack = null;
            }
        }

        private void applyAnim() {
            mAnimMatrix.reset();
            mAnimMatrix.postTranslate(-mBaseRect.left, -mBaseRect.top);
            mAnimMatrix.postTranslate(mRotateCenter.x, mRotateCenter.y);
            mAnimMatrix.postTranslate(-mBaseRect.width() / 2, -mBaseRect.height() / 2);
            mAnimMatrix.postRotate(mDegrees, mRotateCenter.x, mRotateCenter.y);
            mAnimMatrix.postScale(mScale, mScale, mScaleCenter.x, mScaleCenter.y);
            mAnimMatrix.postTranslate(mTranslateX, mTranslateY);
            executeTranslate();
        }

        private void postExecute() {
            if (isRunning) post(this);
        }
    }

    public Info getInfo() {
        return new Info(mImgRect, mCropRect, mDegrees, mScaleType.name(), aspectX, aspectY, getTranslateX(), getTranslateY(), getScale());
    }

    public interface ClipCalculate {
        float calculateTop();
    }

    public void rotate(float degrees) {
        mDegrees += degrees;
        int centerX = (int) (mCropRect.left + mCropRect.width() / 2);
        int centerY = (int) (mCropRect.top + mCropRect.height() / 2);

        mAnimMatrix.postRotate(degrees, centerX, centerY);
        executeTranslate();
    }

    public Bitmap generateCropBitmapFromView(final int backgroundColor) {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setShowImageRectLine(false);
                isShowCropRect = false;
                invalidate();
            }
        });

        Bitmap bitmap = PBitmapUtils.getViewBitmap(CropImageView.this);
        try {
            bitmap = Bitmap.createBitmap(bitmap, (int) mCropRect.left, (int) mCropRect.top,
                    (int) mCropRect.width(), (int) mCropRect.height());
            if (isCircle) {
                bitmap = createCircleBitmap(bitmap, backgroundColor);
            }
        } catch (Exception ignored) {
        }
        return bitmap;
    }

    /**
     * 生成剪裁图片
     *
     * @return bitmap
     */
    public Bitmap generateCropBitmap() {
        if (originalBitmap == null) {
            return null;
        }
        //水平平移像素点
        float x = Math.abs(getTranslateX());
        //垂直平移像素点
        float y = Math.abs(getTranslateY());
        //缩放比例
        float scale = mScale;
        //原图宽度(Glide压缩过的，Glide默认加载会减小大图的宽高)
        int bw = originalBitmap.getWidth();
        //原图高度(Glide压缩过的)
        int bh = originalBitmap.getHeight();
        //图片宽高比
        float bRatio = bw * 1.00f / (bh * 1.00f);

        float endW;
        float endH;
        float endX;
        float endY;

        float cropWidth = mCropRect.width();
        float cropHeight = mCropRect.height();
        float cropRatio = (cropWidth * 1.00f / (cropHeight * 1.00f));

        //图片比例小于剪裁比例，以宽填满，高自适应，计算高
        if (bRatio < cropRatio) {
            endW = bw / scale;
            endH = endW / cropRatio;
            endX = bw * x / (cropWidth * scale * 1.00f);
            endY = bw * y / (cropWidth * scale * 1.00f);
        } else {
            endH = bh / scale;
            endW = cropRatio * endH;
            endX = bh * x / (cropHeight * scale * 1.00f);
            endY = bh * y / (cropHeight * scale * 1.00f);
        }

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

        Bitmap bitmap1;
        try {
            bitmap1 = Bitmap.createBitmap(originalBitmap, (int) endX, (int) endY, (int) endW, (int) endH);
            if (isCircle) {
                bitmap1 = createCircleBitmap(bitmap1, Color.TRANSPARENT);
            }
        } catch (Exception ignored) {
            bitmap1 = generateCropBitmapFromView(Color.BLACK);
        }
        return bitmap1;
    }

    public void setShowCropRect(boolean showCropRect) {
        isShowCropRect = showCropRect;
        invalidate();
    }

    private Bitmap createCircleBitmap(Bitmap resource, int backgroundColor) {
        int width = resource.getWidth();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap circleBitmap = Bitmap.createBitmap(resource.getWidth(), resource.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);
        if (backgroundColor != Color.TRANSPARENT) {
            paint.setColor(backgroundColor);
        }
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);
        //设置画笔为取交集模式
        if (backgroundColor == Color.TRANSPARENT) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        } else {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        }

        //裁剪图片
        canvas.drawBitmap(resource, 0, 0, paint);
        return circleBitmap;
    }


    public float getTranslateX() {
        return mImgRect.left - mCropRect.left;
    }

    public float getTranslateY() {
        return mImgRect.top - mCropRect.top;
    }

    public float getScale() {
        if (mScale <= 1) {
            return 1;
        }
        return mScale;
    }

    public int dp(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }


    private ValueAnimator cropAnim;

    private void anim(float left, float top, float right, float bottom) {
        final float oldLeft = mCropRect.left;
        final float oldTop = mCropRect.top;
        final float oldRight = mCropRect.right;
        final float oldBottom = mCropRect.bottom;
        final float finalLeft = left;
        final float finalTop = top;
        final float finalRight = right;
        final float finalBottom = bottom;

        if ((oldRight == 0 || oldBottom == 0) || (oldLeft == left && oldBottom == bottom
                && oldRight == right && oldTop == top)) {
            mCropRect.set(finalLeft, finalTop, finalRight, finalBottom);
            initBase();
            invalidate();
            return;
        }

        if (cropAnim == null) {
            cropAnim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(400);
            cropAnim.setInterpolator(new DecelerateInterpolator());
        }
        cropAnim.removeAllUpdateListeners();
        cropAnim.removeAllListeners();
        cropAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mCropRect.left = (finalLeft - oldLeft) * value + oldLeft;
                mCropRect.top = (finalTop - oldTop) * value + oldTop;
                mCropRect.right = (finalRight - oldRight) * value + oldRight;
                mCropRect.bottom = (finalBottom - oldBottom) * value + oldBottom;
                isShowLine = value < 1.0f;
                initBase();
                invalidate();
            }
        });
        cropAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                initBase();
                invalidate();
            }
        });
        cropAnim.start();
    }

    public void changeSize(boolean isAnim, final int endWidth, final int endHeight) {
        if (isAnim) {
            final int startWidth = getWidth();
            final int startHeight = getHeight();
            ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(200);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float ratio = (Float) animation.getAnimatedValue();
                    ViewGroup.LayoutParams params = getLayoutParams();
                    params.width = (int) ((endWidth - startWidth) * ratio + startWidth);
                    params.height = (int) ((endHeight - startHeight) * ratio + startHeight);
                    setLayoutParams(params);
                    setImageDrawable(getDrawable());
                }
            });
            anim.start();
        } else {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = endWidth;
            params.height = endHeight;
            setLayoutParams(params);
        }
    }
}