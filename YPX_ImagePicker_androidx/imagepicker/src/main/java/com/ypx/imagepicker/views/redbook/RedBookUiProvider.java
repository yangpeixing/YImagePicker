package com.ypx.imagepicker.views.redbook;

import android.content.Context;

import com.ypx.imagepicker.views.PickerUiProvider;
import com.ypx.imagepicker.views.base.PickerControllerView;
import com.ypx.imagepicker.views.base.PickerItemView;

public class RedBookUiProvider extends PickerUiProvider {
    @Override
    public PickerControllerView getBottomBar(Context context) {
        return null;
    }

    @Override
    public PickerControllerView getTitleBar(Context context) {
        return new RedBookTitleBar(context);
    }

    @Override
    public PickerItemView getItemView(Context context) {
        return new RedBookItemView(context);
    }
}
