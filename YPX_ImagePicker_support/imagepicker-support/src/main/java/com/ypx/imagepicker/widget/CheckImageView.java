package com.ypx.imagepicker.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class CheckImageView extends ImageView {
    private int unSelectIconId;
    private int selectIconId;
    private boolean isChecked;

    public CheckImageView(Context context) {
        this(context, null);
    }

    public CheckImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.FIT_XY);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isCheck) {
        isChecked = isCheck;
        if (selectIconId != 0 && unSelectIconId != 0) {
            setImageDrawable(getResources().getDrawable(isCheck ? selectIconId : unSelectIconId));
        }
        if (!isCheck) {
            setColorFilter(Color.WHITE);
        } else {
            setColorFilter(0);
        }
    }

    public void toggle() {
        setChecked(!isChecked());
    }

    public int getUnSelectIconId() {
        return unSelectIconId;
    }

    public void setUnSelectIconId(int unSelectIconId) {
        this.unSelectIconId = unSelectIconId;
    }

    public int getSelectIconId() {
        return selectIconId;
    }

    public void setSelectIconId(int selectIconId) {
        this.selectIconId = selectIconId;
    }
}
