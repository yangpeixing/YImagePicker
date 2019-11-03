package com.ypx.imagepicker.adapter.multi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

import java.util.ArrayList;

/**
 * Time: 2019/8/8 15:42
 * Author:ypx
 * Description:自定义item基类
 */
public abstract class BaseItemView extends LinearLayout {
    protected View view;

    /**
     * @return item布局id
     */
    protected abstract int getLayoutId();

    /**
     * @param view 初始化view
     */
    protected abstract void initView(View view);

    /**
     * 绑定数据
     *
     * @param imageItem       当前要加载的imageitem
     * @param adapter         当前adapter
     * @param position        position
     * @param selectImageList 选中列表
     * @param result          操作回调
     */
    protected abstract void bindData(ImageItem imageItem,
                                     RecyclerView.Adapter adapter,
                                     int position,
                                     ArrayList<ImageItem> selectImageList,
                                     MultiGridAdapter.OnActionResult result);


    protected BaseItemView(Context context) {
        super(context);
        init();
    }

    private void init() {
        view = LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        initView(view);
    }

    public void initData(MultiSelectConfig selectConfig, IMultiPickerBindPresenter presenter, PickerUiConfig uiConfig) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = getScreenWidth() / selectConfig.getColumnCount() - dp(2);
        setLayoutParams(params);
        view.setLayoutParams(params);
    }


    protected void setLayoutParams(RelativeLayout.LayoutParams params) {
        params.leftMargin = dp(1);
        params.topMargin = dp(1);
        params.rightMargin = dp(1);
        params.bottomMargin = dp(1);
    }

    protected int dp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getContext().getResources().getDisplayMetrics());
    }

    protected int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
