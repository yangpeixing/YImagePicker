package com.ypx.imagepicker.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.views.base.PickerItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 多选adapter
 * <p>
 * Author: yangpeixing on 2018/4/6 10:32
 * Date: 2019/2/21
 */
public class PickerItemAdapter extends RecyclerView.Adapter<PickerItemAdapter.ItemViewHolder> {
    private static final int ITEM_TYPE_CAMERA = 0;
    private static final int ITEM_TYPE_NORMAL = 1;
    private List<ImageItem> images;
    //选中图片列表
    private ArrayList<ImageItem> selectList;
    private BaseSelectConfig selectConfig;
    private IPickerPresenter presenter;
    private PickerUiConfig uiConfig;
    private boolean isPreformClick = false;

    public PickerItemAdapter(ArrayList<ImageItem> selectList,
                             List<ImageItem> images,
                             BaseSelectConfig selectConfig,
                             IPickerPresenter presenter,
                             PickerUiConfig uiConfig) {
        this.images = images;
        this.selectList = selectList;
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        this.uiConfig = uiConfig;
    }

    /**
     * 模拟执行选中（取消选中）操作
     *
     * @param imageItem 当前item
     */
    public void preformCheckItem(ImageItem imageItem) {
        if (onActionResult != null) {
            isPreformClick = true;
            onActionResult.onCheckItem(imageItem, PickerItemDisableCode.NORMAL);
        }
    }

    /**
     * 模拟执行点击操作
     *
     * @param imageItem 当前item
     * @param position  当前item的position
     */
    public void preformClickItem(ImageItem imageItem, int position) {
        if (onActionResult != null) {
            isPreformClick = true;
            onActionResult.onClickItem(imageItem, position, PickerItemDisableCode.NORMAL);
        }
    }

    @NonNull
    @Override
    public PickerItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.picker_item_root, parent, false),
                viewType == ITEM_TYPE_CAMERA,
                selectConfig, presenter, uiConfig);
    }


    @Override
    public void onBindViewHolder(@NonNull final PickerItemAdapter.ItemViewHolder viewHolder, final int position) {
        int itemViewType = getItemViewType(position);
        final ImageItem imageItem = getItem(position);
        if (itemViewType == ITEM_TYPE_CAMERA || imageItem == null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preformClickItem(null, -1);
                }
            });
            return;
        }
        PickerItemView pickerItemView = viewHolder.pickerItemView;
        final int index = selectConfig.isShowCamera() ? position - 1 : position;
        pickerItemView.setPosition(index);
        pickerItemView.setAdapter(this);
        pickerItemView.initItem(imageItem, presenter, selectConfig);

        int indexOfSelectList = selectList.indexOf(imageItem);
        boolean isContainsThisItem = indexOfSelectList >= 0;
        final int finalDisableCode = PickerItemDisableCode.getItemDisableCode(imageItem, selectConfig,
                selectList, isContainsThisItem);
        if (pickerItemView.getCheckBoxView() != null) {
            pickerItemView.getCheckBoxView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onActionResult != null) {
                        isPreformClick = false;
                        onActionResult.onCheckItem(imageItem, finalDisableCode);
                    }
                }
            });
        }

        pickerItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onActionResult != null) {
                    isPreformClick = false;
                    onActionResult.onClickItem(imageItem, position, finalDisableCode);
                }
            }
        });

        pickerItemView.enableItem(imageItem, indexOfSelectList >= 0, indexOfSelectList);
        if (finalDisableCode != PickerItemDisableCode.NORMAL) {
            pickerItemView.disableItem(imageItem, finalDisableCode);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (selectConfig.isShowCamera()) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return selectConfig.isShowCamera() ? images.size() + 1 : images.size();
    }

    private ImageItem getItem(int position) {
        if (selectConfig.isShowCamera()) {
            if (position == 0) {
                return null;
            }
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    public void refreshData(List<ImageItem> items) {
        if (items != null && items.size() > 0) {
            images = items;
        }
        notifyDataSetChanged();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private PickerItemView pickerItemView;
        private Context context;

        ItemViewHolder(@NonNull View itemView, boolean isCamera, BaseSelectConfig selectConfig, IPickerPresenter presenter, PickerUiConfig uiConfig) {
            super(itemView);
            context = itemView.getContext();
            FrameLayout layout = itemView.findViewById(R.id.mRoot);
            int width = (getScreenWidth() - dp(2)) / selectConfig.getColumnCount();
            PViewSizeUtils.setViewSize(layout, width, 1.00f);

            pickerItemView = uiConfig.getPickerUiProvider().getItemView(context);
            layout.removeAllViews();
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.bottomMargin = dp(1);
            params.topMargin = dp(1);
            params.rightMargin = dp(1);
            params.leftMargin = dp(1);
            if (isCamera) {
                layout.addView(pickerItemView.getCameraView(selectConfig, presenter), params);
            } else {
                layout.addView(pickerItemView, params);
            }
        }

        int getScreenWidth() {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            assert wm != null;
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.widthPixels;
        }

        int dp(int dp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) dp, context.getResources().getDisplayMetrics());
        }
    }

    public boolean isPreformClick() {
        return isPreformClick;
    }

    private OnActionResult onActionResult;

    public void setOnActionResult(OnActionResult onActionResult) {
        this.onActionResult = onActionResult;
    }

    public interface OnActionResult {
        /**
         * 点击操作
         *
         * @param imageItem 当前item
         * @param position  当前item的position
         */
        void onClickItem(ImageItem imageItem, int position, int disableItemCode);

        /**
         * 执行选中（取消选中）操作
         *
         * @param imageItem 当前item
         */
        void onCheckItem(ImageItem imageItem, int disableItemCode);
    }
}
