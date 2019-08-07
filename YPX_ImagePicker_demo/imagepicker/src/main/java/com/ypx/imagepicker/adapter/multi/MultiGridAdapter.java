package com.ypx.imagepicker.adapter.multi;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.bean.MultiUiConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.widget.CheckImageView;
import com.ypx.imagepicker.widget.ShowTypeImageView;

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
    private Context mContext;
    private MultiSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private MultiUiConfig multiUiConfig;

    public MultiGridAdapter(Context ctx, List<ImageItem> images, MultiSelectConfig selectConfig, IMultiPickerBindPresenter presenter) {
        this.images = images;
        this.mContext = ctx;
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        multiUiConfig = presenter.getUiConfig(ctx);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_CAMERA) {
            return new CameraViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.picker_grid_item_camera, parent, false),
                    selectConfig, multiUiConfig);
        } else {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.picker_image_grid_item, parent, false),
                    selectConfig, multiUiConfig);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        int itemViewType = getItemViewType(position);
        final ImageItem item = getItem(position);
        if (itemViewType == ITEM_TYPE_CAMERA || item == null) {
            return;
        }
        final ItemViewHolder holder = (ItemViewHolder) viewHolder;
        if (selectConfig.getSelectMode() != ImageSelectMode.MODE_MULTI) {
            holder.cbSelected.setVisibility(View.GONE);
        } else {
            holder.cbSelected.setVisibility(View.VISIBLE);
        }
        if (item.isVideo()) {
            holder.mVideoLayout.setVisibility(View.VISIBLE);
            holder.mVideoTime.setText(item.getDurationFormat());
            holder.ivPic.setType(ShowTypeImageView.TYPE_NONE);
        } else {
            holder.mVideoLayout.setVisibility(View.GONE);
            holder.ivPic.setTypeWithUrlAndSize(item);
        }
        holder.cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MultiPickerData.instance.isOverLimit(selectConfig.getMaxCount() - 1) && !holder.cbSelected.isChecked()) {
                    String toast = mContext.getResources().getString(R.string.you_have_a_select_limit, selectConfig.getMaxCount() + "");
                    presenter.tip(mContext, toast);
                    return;
                }
                holder.cbSelected.toggle();
                if (mContext instanceof MultiImagePickerActivity) {
                    ((MultiImagePickerActivity) mContext).imageSelectChange(item, holder.cbSelected.isChecked());
                    notifyDataSetChanged();
                }
            }
        });

        //屏蔽列表
        if (selectConfig.isShieldItem(item)) {
            holder.cbSelected.setVisibility(View.GONE);
            holder.v_masker.setVisibility(View.VISIBLE);
            holder.v_masker.setBackgroundColor(Color.parseColor("#80FFFFFF"));
        } else {
            if (MultiPickerData.instance.hasItem(item)) {
                holder.cbSelected.setChecked(true);
                holder.v_masker.setVisibility(View.VISIBLE);
                holder.v_masker.setBackgroundColor(Color.parseColor("#80000000"));
            } else {
                holder.cbSelected.setChecked(false);
                holder.v_masker.setVisibility(View.GONE);
            }
        }

        holder.ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectConfig.isShieldItem(item)) {
                    presenter.tip(mContext, mContext.getResources().getString(R.string.str_shield));
                    return;
                }
                if (!selectConfig.isPreview()) {
                    holder.cbSelected.performClick();
                    return;
                }
                if (mContext instanceof MultiImagePickerActivity) {
                    ((MultiImagePickerActivity) mContext).onImageClickListener(item, selectConfig.isShowCamera() ? position - 1 : position);
                }
            }
        });

        if (presenter != null) {
            presenter.displayListImage(holder.ivPic, item.path, 0);
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

    static class CameraViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_camera;

        CameraViewHolder(@NonNull View itemView, MultiSelectConfig selectConfig, MultiUiConfig uiConfig) {
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
                    if (v.getContext() instanceof MultiImagePickerActivity) {
                        ((MultiImagePickerActivity) v.getContext()).takePhoto();
                    }
                }
            });
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ShowTypeImageView ivPic;
        private CheckImageView cbSelected;
        private View v_masker;
        private LinearLayout mVideoLayout;
        private TextView mVideoTime;
        private Context context;

        ItemViewHolder(@NonNull View itemView, MultiSelectConfig selectConfig, MultiUiConfig uiConfig) {
            super(itemView);
            context = itemView.getContext();
            ivPic = itemView.findViewById(R.id.iv_thumb);
            cbSelected = itemView.findViewById(R.id.iv_thumb_check);
            v_masker = itemView.findViewById(R.id.v_masker);
            mVideoLayout = itemView.findViewById(R.id.mVideoLayout);
            mVideoTime = itemView.findViewById(R.id.mVideoTime);

            if (uiConfig.getPickerItemBackgroundColor() != 0) {
                ivPic.setBackgroundColor(uiConfig.getPickerItemBackgroundColor());
            }
            cbSelected.setSelectIconId(uiConfig.getSelectedIconID());
            cbSelected.setUnSelectIconId(uiConfig.getUnSelectIconID());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
            params.leftMargin = dp(context, 1);
            params.topMargin = dp(context, 1);
            params.rightMargin = dp(context, 1);
            params.bottomMargin = dp(context, 1);
            params.height = getScreenWidth(context) / selectConfig.getColumnCount() - dp(context, 2);
            itemView.setLayoutParams(params);
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
}
