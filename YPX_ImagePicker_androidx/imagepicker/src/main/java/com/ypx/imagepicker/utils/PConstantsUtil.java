package com.ypx.imagepicker.utils;

import android.content.Context;

import com.ypx.imagepicker.bean.PickConstants;
import com.ypx.imagepicker.presenter.BasePresenter;

/**
 * Time: 2019/11/2 13:07
 * Author:ypx
 * Description:
 */
public class PConstantsUtil {
    public static PickConstants getString(Context context, BasePresenter presenter) {
        if (presenter == null) {
            return new PickConstants(context);
        }
        PickConstants pickConstants = presenter.getPickConstants(context);
        if (pickConstants != null) {
            return pickConstants;
        }
        return new PickConstants(context);
    }
}
