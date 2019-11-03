package com.ypx.imagepicker.adapter.multi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PConstantsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 多选adapter
 * <p>
 * Author: yangpeixing on 2018/4/6 10:32
 * Date: 2019/2/21
 */
public class MultiGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_TYPE_CAMERA = 0;
    private static final int ITEM_TYPE_NORMAL = 1;
    private List<ImageItem> images;
    //选中图片列表
    private ArrayList<ImageItem> selectList;
    private MultiSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private PickerUiConfig pickerUiConfig;
    private Context context;

    public MultiGridAdapter(Context ctx, ArrayList<ImageItem> selectList, List<ImageItem> images, MultiSelectConfig selectConfig, IMultiPickerBindPresenter presenter) {
        this.context = ctx;
        this.images = images;
        this.selectList = selectList;
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        pickerUiConfig = presenter.getUiConfig(ctx);
    }

    /**
     * 模拟执行选中（取消选中）操作
     *
     * @param imageItem 当前item
     */
    public void preformCheckItem(ImageItem imageItem) {
        if (onActionResult != null) {
            onActionResult.onCheckItem(imageItem);
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
            onActionResult.onClickItem(imageItem, position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.picker_grid_item_camera, parent, false),
                    selectConfig, pickerUiConfig);
        } else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.picker_image_grid_item_root, parent, false),
                    selectConfig, presenter);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        int itemViewType = getItemViewType(position);
        final ImageItem item = getItem(position);
        if (itemViewType == ITEM_TYPE_CAMERA || item == null) {
            CameraViewHolder viewHolder1 = (CameraViewHolder) viewHolder;
            viewHolder1.tv_camera.setText(selectConfig.isOnlyShowVideo() ?
                    PConstantsUtil.getString(context, presenter).picker_str_take_video :
                    PConstantsUtil.getString(context, presenter).picker_str_take_photo);
            return;
        }
        int index = selectConfig.isShowCamera() ? position - 1 : position;
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.getBaseItemView().bindData(item, this, index, selectList, onActionResult);
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

    class CameraViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_camera;

        CameraViewHolder(@NonNull View itemView, MultiSelectConfig selectConfig, PickerUiConfig uiConfig) {
            super(itemView);
            Context context = itemView.getContext();
            tv_camera = itemView.findViewById(R.id.tv_camera);
            tv_camera.setCompoundDrawablesWithIntrinsicBounds(null, itemView.getContext().getResources().getDrawable(uiConfig.getCameraIconID()),
                    null, null);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            params.leftMargin = dp(context, 1);
            params.topMargin = dp(context, 1);
            params.rightMargin = dp(context, 1);
            params.bottomMargin = dp(context, 1);
            params.height = getScreenWidth(context) / selectConfig.getColumnCount() - dp(context, 2);
            itemView.setLayoutParams(params);
            if (uiConfig.getCameraBackgroundColor() != 0) {
                itemView.setBackgroundColor(uiConfig.getCameraBackgroundColor());
            }
            tv_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionResult != null) {
                        onActionResult.onClickItem(null, -1);
                    }
                }
            });
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private BaseItemView baseItemView;

        ItemViewHolder(@NonNull View itemView, MultiSelectConfig selectConfig, IMultiPickerBindPresenter presenter) {
            super(itemView);
            context = itemView.getContext();
            RelativeLayout layout = itemView.findViewById(R.id.mRoot);
            PickerUiConfig uiConfig = presenter.getUiConfig(context);
            if (uiConfig != null && uiConfig.getPickerItemView() != null) {
                baseItemView = uiConfig.getPickerItemView();
            } else {
                baseItemView = new WXItemView(context);
            }
            baseItemView.initData(selectConfig, presenter, uiConfig);
            layout.addView(baseItemView);
        }

        BaseItemView getBaseItemView() {
            return baseItemView;
        }
    }


    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private static int dp(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) dp, context.getResources().getDisplayMetrics());
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
        void onClickItem(ImageItem imageItem, int position);

        /**
         * 执行选中（取消选中）操作
         *
         * @param imageItem 当前item
         */
        void onCheckItem(ImageItem imageItem);
    }
}
