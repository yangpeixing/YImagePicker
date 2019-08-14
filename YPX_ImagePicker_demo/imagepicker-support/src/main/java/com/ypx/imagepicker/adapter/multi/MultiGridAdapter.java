package com.ypx.imagepicker.adapter.multi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.TakePhotoUtil;
import com.ypx.imagepicker.widget.CheckImageView;
import com.ypx.imagepicker.widget.ShowTypeImageView;

import java.util.List;

import static com.ypx.imagepicker.activity.multi.MultiImagePickerActivity.REQ_CAMERA;

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
    private PickerSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private PickerUiConfig pickerUiConfig;

    public MultiGridAdapter(Context ctx, List<ImageItem> images, PickerSelectConfig selectConfig, IMultiPickerBindPresenter presenter) {
        this.images = images;
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        pickerUiConfig = presenter.getUiConfig(ctx);
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
            return;
        }
        int index= selectConfig.isShowCamera() ? position - 1 : position;
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.getBaseItemView().bindData(item, this, index,
                MultiPickerData.instance.getSelectImageList(), onActionResult);
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

    static class CameraViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_camera;

        CameraViewHolder(@NonNull View itemView, PickerSelectConfig selectConfig, PickerUiConfig uiConfig) {
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
                    TakePhotoUtil.takePhoto((Activity) v.getContext(), REQ_CAMERA);
                }
            });
        }
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private BaseItemView baseItemView;

        ItemViewHolder(@NonNull View itemView, PickerSelectConfig selectConfig, IMultiPickerBindPresenter presenter) {
            super(itemView);
            context = itemView.getContext();
            RelativeLayout layout = itemView.findViewById(R.id.mRoot);
            PickerUiConfig uiConfig = presenter.getUiConfig(context);
            if (uiConfig != null && uiConfig.getPickerItemView() != null) {
                baseItemView = uiConfig.getPickerItemView();
            } else {
                baseItemView = new WXItemView(context);
            }
            baseItemView.initData(selectConfig, presenter,uiConfig);
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
        void onClickItem(ImageItem item, int position);

        void onCheckItem(ImageItem imageItem, boolean isChecked);
    }
}
