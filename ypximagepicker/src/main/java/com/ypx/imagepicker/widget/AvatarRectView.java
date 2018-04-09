package com.ypx.imagepicker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.ypx.imagepicker.R;


/**
 * <b>image crop mask View for avatar</b><br/>
 * Created by Eason.Lai on 2015/11/1 10:42 <br/>
 * contact：easonline7@gmail.com <br/>
 */
public class AvatarRectView extends View {
	private static final String TAG = AvatarRectView.class.getSimpleName();

	final Paint mPaint = new Paint();
	final Rect mRect = new Rect();
	private int mAvatarSize;

	Rect[] rectArray;//Rect masks to draw
	Rect centerRect;//transparent Rect in center
	Bitmap centerBitmap;//center transparent area draw by a bitmap

	public AvatarRectView(Context context, int avatarSize) {
		super(context);
		this.mAvatarSize = avatarSize;

		//init the mask Rectangles
		rectArray = new Rect[8];
		for(int i = 0 ; i < rectArray.length ; i++){
			rectArray[i] = new Rect();
		}

		centerBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.head_photo_preview_circle_mask);
		centerRect = new Rect(0, 0, centerBitmap.getWidth(), centerBitmap.getHeight());

	}

	@Override
	protected final void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);//去掉锯齿
		this.mRect.left = ((getWidth() - this.mAvatarSize) / 2);
		this.mRect.right = ((getWidth() + this.mAvatarSize) / 2);
		this.mRect.top = ((getHeight() - this.mAvatarSize) / 2);
		this.mRect.bottom = ((getHeight() + this.mAvatarSize) / 2);

		rectArray[0].set(0, 0,mRect.left,mRect.top);
		rectArray[1].set(mRect.left, 0,mRect.right,this.mRect.top);
		rectArray[2].set(this.mRect.right, 0, getWidth(), this.mRect.top);
		rectArray[3].set(0,mRect.top,mRect.left,this.mRect.bottom);
		rectArray[4].set(mRect.right,mRect.top, getWidth(),this.mRect.bottom);
		rectArray[5].set(0,mRect.bottom, this.mRect.left,getHeight());
		rectArray[6].set(mRect.left,mRect.bottom,this.mRect.right, getHeight());
		rectArray[7].set(mRect.right, mRect.bottom, getWidth(), getHeight());

		this.mPaint.setColor(0x7f000000);
		this.mPaint.setStyle(Paint.Style.FILL);
		for (Rect rect : rectArray){
			canvas.drawRect(rect, this.mPaint);
		}

		mPaint.reset();
		if(!centerBitmap.isRecycled()) {
			canvas.drawBitmap(centerBitmap, centerRect, mRect, mPaint);
		}else{
			Log.i(TAG,"bitmap recycle");
		}
		//centerBitmap.recycle();

	}

	/**
	 * get the Rect of this View
	 * @return mRect the center Rect of avatar area
	 */
	public Rect getCropRect(){
		return mRect;
	}


}
