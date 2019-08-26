package com.ypx.imagepicker.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * <b>Desc your Class</b><br/>
 * Created by Eason.Lai on 2015/11/1 10:42 <br/>
 * contact：easonline7@gmail.com <br/>
 */
public class SuperCheckBox extends CompoundButton {

    private boolean canChecked = true;

    public SuperCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SuperCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperCheckBox(Context context) {
        super(context);
    }

    public CharSequence getAccessibilityClassName() {
        return CheckBox.class.getName();
    }

    public boolean isCanChecked() {
        return canChecked;
    }

    public void setCanChecked(boolean canChecked) {
        this.canChecked = canChecked;
    }


    @Override
    public void toggle() {
        if (canChecked) {
            super.toggle();
        }
    }

    @Override
    public boolean performClick() {

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    public void setRightDrawable(Drawable selectedDrawable, Drawable unSelectedDrawable) {
        StateListDrawable checkBoxDrawable = new StateListDrawable();
        checkBoxDrawable.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
        checkBoxDrawable.addState(new int[]{android.R.attr.state_checked}, selectedDrawable);
        checkBoxDrawable.addState(new int[]{}, unSelectedDrawable);
        setCompoundDrawablesWithIntrinsicBounds(null, null, checkBoxDrawable, null);
    }

    public void setLeftDrawable(Drawable selectedDrawable, Drawable unSelectedDrawable) {
        StateListDrawable checkBoxDrawable = new StateListDrawable();
        checkBoxDrawable.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
        checkBoxDrawable.addState(new int[]{android.R.attr.state_checked}, selectedDrawable);
        checkBoxDrawable.addState(new int[]{}, unSelectedDrawable);
        setCompoundDrawablesWithIntrinsicBounds(checkBoxDrawable, null, null, null);
    }


}
