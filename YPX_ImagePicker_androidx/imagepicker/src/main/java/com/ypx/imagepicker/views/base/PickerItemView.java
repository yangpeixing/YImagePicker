package com.ypx.imagepicker.views.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.presenter.IPickerPresenter;

/**
 * Time: 2019/8/8 15:42
 * Author:ypx
 * Description:自定义item基类
 * <p>
 * 执行流程：
 * initItem——> enableItem ——> disableItem
 * </p>
 */
public abstract class PickerItemView extends PBaseLayout {
    /**
     * 获取拍照item样式
     *
     * @param selectConfig 选择配置类
     * @param presenter    implements of {@link com.ypx.imagepicker.presenter.IPickerPresenter}
     * @return 拍照
     */
    public abstract View getCameraView(BaseSelectConfig selectConfig, IPickerPresenter presenter);

    /**
     * @return 返回用于点击选中item的view
     */
    public abstract View getCheckBoxView();

    /**
     * 初始化item
     *
     * @param imageItem    当前图片
     * @param presenter    presenter
     * @param selectConfig 选择器配置项
     */
    public abstract void initItem(ImageItem imageItem, IPickerPresenter presenter, BaseSelectConfig selectConfig);

    /**
     * 当检测到此item不能被选中时，执行此方法
     *
     * @param imageItem   当前图片
     * @param disableCode 不能选中的原因 {@link com.ypx.imagepicker.bean.PickerItemDisableCode}
     */
    public abstract void disableItem(ImageItem imageItem, int disableCode);

    /**
     * 在disableItem之前调用，用于正常加载每个item
     *
     * @param imageItem           当前图片
     * @param isChecked           是否已经被选中
     * @param indexOfSelectedList 在已选中列表里的索引
     */
    public abstract void enableItem(ImageItem imageItem, boolean isChecked, int indexOfSelectedList);

    private RecyclerView.Adapter adapter;
    private int position;

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public int getPosition() {
        return position;
    }

    public PickerItemView(Context context) {
        super(context);
    }

    public PickerItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PickerItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
