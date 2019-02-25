/*******************************************************************************
 * Copyright 2016 oginotihiro
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.ypxredbookpicker.widget.cropview;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;


import com.example.ypxredbookpicker.widget.cropview.gestures.OnGestureListener;
import com.example.ypxredbookpicker.widget.cropview.gestures.VersionedGestureDetector;
import com.example.ypxredbookpicker.widget.cropview.scrollerproxy.ScrollerProxy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CropView extends android.support.v7.widget.AppCompatImageView implements ViewTreeObserver.OnGlobalLayoutListener, OnGestureListener {
    private static final float DEFAULT_MAX_SCALE = 6.0f;
    private static final float DEFAULT_MID_SCALE = 3.0f;
    private static final float DEFAULT_MIN_SCALE = 1.0f;
    private static final long DEFAULT_ZOOM_DURATION = 200;
    private static final float OUTLINE_DP = 2f;

    private float mMinScale = DEFAULT_MIN_SCALE;
    private float mMidScale = DEFAULT_MID_SCALE;
    private float mMaxScale = DEFAULT_MAX_SCALE;
    private long mZoomDuration = DEFAULT_ZOOM_DURATION;

    private com.example.ypxredbookpicker.widget.cropview.gestures.GestureDetector mDragScaleDetector;
    private GestureDetector mGestureDetector;

    private int mIvTop, mIvRight, mIvBottom, mIvLeft;

    private final Matrix mBaseMatrix = new Matrix();
    private final Matrix mSuppMatrix = new Matrix();
    private final Matrix mDrawMatrix = new Matrix();
    private final RectF mDisplayRect = new RectF();

    private final float[] mMatrixValues = new float[9];

    private FlingRunnable mCurrentFlingRunnable;
    private Interpolator sInterpolator = new AccelerateDecelerateInterpolator();

    private RotateBitmap mBitmapDisplayed = new RotateBitmap(null, 0);

    private RectF mCropRect;

    private final Paint outlinePaint = new Paint();
    private final Paint outsidePaint = new Paint();

    private int highlightColor = Color.WHITE;

    private Path path = new Path();
    private Rect viewDrawingRect = new Rect();

    private Uri mSource;
    private int mAspectX = 1;
    private int mAspectY = 1;
    private int mOutputX;
    private int mOutputY;

    private int mSampleSize;

    public CropView of(Uri source) {
        mSource = source;
        return this;
    }

    public CropView asSquare() {
        mAspectX = 1;
        mAspectY = 1;
        return this;
    }

    public CropView withAspect(int x, int y) {
        mAspectX = x;
        mAspectY = y;
        return this;
    }

    public CropView withOutputSize(int width, int height) {
        mOutputX = width;
        mOutputY = height;
        return this;
    }

    public void initialize(Context context) {
        if (mSource != null) {
            File imageFile = CropUtil.getFromMediaUri(context, mSource);

            InputStream is = null;
            try {
                mSampleSize = CropUtil.calculateBitmapSampleSize(context, mSource);

                is = context.getContentResolver().openInputStream(mSource);
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = mSampleSize;
                RotateBitmap rotateBitmap = new RotateBitmap(BitmapFactory.decodeStream(is, null, option), CropUtil.getExifRotation(imageFile));

                if (rotateBitmap != null) {
                    setImageRotateBitmap(rotateBitmap);
                }
            } catch (IOException e) {
            } catch (OutOfMemoryError e) {
            } finally {
                CropUtil.closeSilently(is);
            }
        }
    }

    public Bitmap getOutput() {
        if (getDrawable() == null || mCropRect == null) {
            return null;
        }

        // 图片当前显示区域
        final Matrix drawMatrix = getDrawMatrix();
        final RectF displayRect = getDisplayRect(drawMatrix);

        final float leftOffset = mCropRect.left - displayRect.left;
        final float topOffset = mCropRect.top - displayRect.top;

        // image scale
        final float scale = (float) Math.sqrt((float) Math.pow(getValue(drawMatrix, Matrix.MSCALE_X), 2)
                + (float) Math.pow(getValue(drawMatrix, Matrix.MSKEW_Y), 2));

        Rect cropRect = new Rect(
                (int) (leftOffset / scale * mSampleSize),
                (int) (topOffset / scale * mSampleSize),
                (int) ((leftOffset + mCropRect.width()) / scale * mSampleSize),
                (int) ((topOffset + mCropRect.height()) / scale * mSampleSize)
        );

        return CropUtil.decodeRegionCrop(getContext(), mSource, cropRect, mOutputX, mOutputY, mBitmapDisplayed.getRotation());
    }

    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.MATRIX);

        mDragScaleDetector = VersionedGestureDetector.newInstance(context, this);

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(new DefaultOnDoubleTapListener());

        outlinePaint.setAntiAlias(true);
        outlinePaint.setColor(highlightColor);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(dpToPx(OUTLINE_DP));
        outsidePaint.setARGB(125, 50, 50, 50);

        ViewTreeObserver observer = getViewTreeObserver();
        if (null != observer) {
            observer.addOnGlobalLayoutListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cleanup();
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("deprecation")
    private void cleanup() {
        cancelFling();

        if (null != mGestureDetector) {
            mGestureDetector.setOnDoubleTapListener(null);
        }

        ViewTreeObserver observer = getViewTreeObserver();
        if (null != observer && observer.isAlive()) {
            observer.removeGlobalOnLayoutListener(this);
        }

        setImageBitmap(null);

        mBitmapDisplayed.recycle();
    }

    private int getCropViewWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getCropViewHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void onGlobalLayout() {
        final int top = getTop();
        final int bottom = getBottom();
        final int left = getLeft();
        final int right = getRight();

        if (top != mIvTop || bottom != mIvBottom || left != mIvLeft || right != mIvRight) {
            updateBaseMatrix();

            mIvTop = top;
            mIvBottom = bottom;
            mIvLeft = left;
            mIvRight = right;
        }
    }

    private void updateBaseMatrix() {
        if (mBitmapDisplayed.getBitmap() == null) {
            return;
        }

        float viewWidth = getCropViewWidth();
        float viewHeight = getCropViewHeight();
        float w = mBitmapDisplayed.getWidth();
        float h = mBitmapDisplayed.getHeight();

        mBaseMatrix.reset();

        // We limit up-scaling to 3x otherwise the result may look bad if it's a small icon
        float widthScale = Math.min(viewWidth / w, 3.0f);
        float heightScale = Math.min(viewHeight / h, 3.0f);
        float scale = Math.min(widthScale, heightScale);

        // Make the default size about 4/5 of the width or height
        float cropWidth = Math.min(w, h) * 4 / 5 * scale;
        float cropHeight = cropWidth;

        if (mAspectX != 0 && mAspectY != 0) {
            if (mAspectX > mAspectY) {
                cropHeight = cropWidth * mAspectY / mAspectX;
            } else {
                cropWidth = cropHeight * mAspectX / mAspectY;
            }
        }

        // 处理特殊图片，例如全景图
        float z1 = viewWidth / cropWidth * .6F;
        float z2 = viewHeight / cropHeight * .6F;
        float zoom = Math.min(z1, z2);

        if (zoom > 1F) { // 继续放大
            scale = scale * zoom;
            cropWidth = cropWidth * zoom;
            cropHeight = cropHeight * zoom;
        }

        float x = (viewWidth - cropWidth) / 2F;
        float y = (viewHeight - cropHeight) / 2F;

         mCropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
      //  mCropRect = new RectF(0, 0, getWidth(), getHeight());

        mBaseMatrix.postConcat(mBitmapDisplayed.getRotateMatrix());  // 旋转
        mBaseMatrix.postScale(scale, scale);
        mBaseMatrix.postTranslate((viewWidth - w * scale) / 2F, (viewHeight - h * scale) / 2F);

        mSuppMatrix.reset();

        setImageMatrix(getDrawMatrix());

        // calculate min scale 初始化图片显示Rect
        RectF displayRect = getDisplayRect(mBaseMatrix);

        float wScale = mCropRect.width() / displayRect.width();
        float hScale = mCropRect.height() / displayRect.height();

        mMinScale = Math.max(wScale, hScale);
    }

    private void setImageRotateBitmap(RotateBitmap bitmap) {
        Bitmap old = mBitmapDisplayed.getBitmap();

        mBitmapDisplayed = bitmap;
        setImageBitmap(bitmap.getBitmap());

        if (old != null) {
            old.recycle();
        }

        updateBaseMatrix();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;

        if (getDrawable() != null && mCropRect != null) {
            switch (ev.getAction()) {
                case ACTION_DOWN:
                    cancelFling();
                    break;
                case ACTION_CANCEL:
                case ACTION_UP:
                    break;
            }

            if (null != mDragScaleDetector) {
                handled = mDragScaleDetector.onTouchEvent(ev);
            }

            if (null != mGestureDetector && mGestureDetector.onTouchEvent(ev)) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (mDragScaleDetector.isScaling()) {
            return;
        }

        mSuppMatrix.postTranslate(dx, dy);
        checkAndDisplayMatrix();
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        final float scale = getScale();

        if (scaleFactor > 1) {
            float maxScaleFactor = mMaxScale / scale;
            if (scaleFactor >= maxScaleFactor) {
                scaleFactor = maxScaleFactor;
            }
        } else if (scaleFactor < 1) {
            float minScaleFactor = mMinScale / scale;
            if (scaleFactor <= minScaleFactor) {
                scaleFactor = minScaleFactor;
            }
        }

        mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        checkAndDisplayMatrix();
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        mCurrentFlingRunnable = new FlingRunnable(getContext());
        mCurrentFlingRunnable.fling((int) velocityX, (int) velocityY);
        post(mCurrentFlingRunnable);
    }

    private void cancelFling() {
        if (null != mCurrentFlingRunnable) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    private void checkAndDisplayMatrix() {
        if (checkMatrixBounds()) {
            setImageMatrix(getDrawMatrix());
        }
    }

    private boolean checkMatrixBounds() {
        final RectF rect = getDisplayRect(getDrawMatrix());
        if (null == rect) {
            return false;
        }

        float deltaX = 0, deltaY = 0;

        if (rect.top >= mCropRect.top) {
            deltaY = -rect.top + mCropRect.top;
        } else if (rect.bottom <= mCropRect.bottom) {
            deltaY = mCropRect.bottom - rect.bottom;
        }

        if (rect.left >= mCropRect.left) {
            deltaX = -rect.left + mCropRect.left;
        } else if (rect.right <= mCropRect.right) {
            deltaX = mCropRect.right - rect.right;
        }

        mSuppMatrix.postTranslate(deltaX, deltaY);
        return true;
    }

    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = getDrawable();
        if (null != d) {
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private Matrix getDrawMatrix() {
        mDrawMatrix.set(mBaseMatrix);
        mDrawMatrix.postConcat(mSuppMatrix);
        return mDrawMatrix;
    }

    private float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X), 2)
                + (float) Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y), 2));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private class FlingRunnable implements Runnable {
        private final ScrollerProxy mScroller;
        private int mCurrentX, mCurrentY;

        public FlingRunnable(Context context) {
            mScroller = ScrollerProxy.getScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        public void fling(int velocityX, int velocityY) {
            final RectF rect = getDisplayRect(getDrawMatrix());
            if (rect == null) return;

            final int startX = Math.round((mCropRect.left - rect.left));
            final int startY = Math.round((mCropRect.top - rect.top));

            final int minX = 0;
            final int minY = 0;

            final int maxX = Math.round(rect.width() - mCropRect.width());
            final int maxY = Math.round(rect.height() - mCropRect.height());

            mCurrentX = startX;
            mCurrentY = startY;

            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return;
            }

            if (mScroller.computeScrollOffset()) {
                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                mSuppMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);

                setImageMatrix(getDrawMatrix());

                mCurrentX = newX;
                mCurrentY = newY;

                Compat.postOnAnimation(CropView.this, this);
            }
        }
    }

    private class DefaultOnDoubleTapListener implements GestureDetector.OnDoubleTapListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent ev) {
            try {
                float scale = getScale();
                float x = ev.getX();
                float y = ev.getY();

                if (scale < mMidScale) {
                    setScale(mMidScale, x, y, true);
                } else if (scale >= mMidScale && scale < mMaxScale) {
                    setScale(mMaxScale, x, y, true);
                } else {
                    setScale(mMinScale, x, y, true);
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // Can sometimes happen when getX() and getY() is called
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    private void setScale(float scale, float focalX, float focalY, boolean animate) {
        if (scale < mMinScale || scale > mMaxScale) {
            return;
        }

        if (animate) {
            post(new AnimatedZoomRunnable(getScale(), scale, focalX, focalY));
        } else {
            mSuppMatrix.setScale(scale, scale, focalX, focalY);
            checkAndDisplayMatrix();
        }
    }

    private class AnimatedZoomRunnable implements Runnable {
        private final float mZoomStart, mZoomEnd;
        private final float mFocalX, mFocalY;
        private final long mStartTime;

        public AnimatedZoomRunnable(final float currentZoom, final float targetZoom, final float focalX, final float focalY) {
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
            mFocalX = focalX;
            mFocalY = focalY;
            mStartTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            float t = interpolate();
            float scale = mZoomStart + t * (mZoomEnd - mZoomStart);
            float deltaScale = scale / getScale();

            onScale(deltaScale, mFocalX, mFocalY);

            if (t < 1f) {
                Compat.postOnAnimation(CropView.this, this);
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = sInterpolator.getInterpolation(t);
            return t;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCropRect == null) return;

        path.reset();
        path.addRect(mCropRect.left, mCropRect.top, mCropRect.right, mCropRect.bottom, Path.Direction.CW);

        if (isClipPathSupported(canvas)) {
            getDrawingRect(viewDrawingRect);
            canvas.clipPath(path, android.graphics.Region.Op.DIFFERENCE);
            canvas.drawRect(viewDrawingRect, outsidePaint);
        } else {
            drawOutsideFallback(canvas);
        }

        canvas.drawPath(path, outlinePaint);
    }

    private boolean isClipPathSupported(Canvas canvas) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        } else if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                || Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            return true;
        } else {
            return !canvas.isHardwareAccelerated();
        }
    }

    private void drawOutsideFallback(Canvas canvas) {
        canvas.drawRect(0, 0, canvas.getWidth(), mCropRect.top, outsidePaint);
        canvas.drawRect(0, mCropRect.bottom, canvas.getWidth(), canvas.getHeight(), outsidePaint);
        canvas.drawRect(0, mCropRect.top, mCropRect.left, mCropRect.bottom, outsidePaint);
        canvas.drawRect(mCropRect.right, mCropRect.top, canvas.getWidth(), mCropRect.bottom, outsidePaint);
    }

    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}