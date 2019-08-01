package com.ypx.imagepicker.adapter.multi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePickerActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.MultiUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.bean.MultiSelectConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.widget.CheckImageView;
import com.ypx.imagepicker.widget.ShowTypeImageView;

import java.util.List;


/**
 * 作者：yangpeixing on 2018/4/6 10:32
 * 功能：gridview适配器
 * 产权：南京婚尚信息技术
 */
public class MultiGridAdapter extends BaseAdapter {
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

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (selectConfig.isShowCamera()) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }


    @Override
    public int getCount() {
        return selectConfig.isShowCamera() ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (selectConfig.isShowCamera()) {
            if (position == 0) {
                return null;
            }
            return images.get(position - 1);
        } else {
            return images.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ypx_grid_item_camera, parent, false);
            TextView tv_camera = convertView.findViewById(R.id.tv_camera);
            tv_camera.setCompoundDrawablesWithIntrinsicBounds(null, mContext.getResources().getDrawable(multiUiConfig.getCameraIconID()), null, null);
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof MultiImagePickerActivity) {
                        ((MultiImagePickerActivity) mContext).takePhoto();
                    }
                }
            });
            ViewGroup.LayoutParams params = convertView.getLayoutParams();
            params.width = params.height = (getScreenWidth() - dp_2() * 2) / selectConfig.getColumnCount();
            convertView.setLayoutParams(params);
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ypx_image_grid_item, null);
                holder = new ViewHolder();
                holder.ivPic = convertView.findViewById(R.id.iv_thumb);
                holder.cbSelected = convertView.findViewById(R.id.iv_thumb_check);
                holder.cbPanel = convertView.findViewById(R.id.thumb_check_panel);
                holder.v_masker = convertView.findViewById(R.id.v_masker);
                holder.mVideoLayout = convertView.findViewById(R.id.mVideoLayout);
                holder.mVideoTime = convertView.findViewById(R.id.mVideoTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (selectConfig.getSelectMode() == ImageSelectMode.MODE_MULTI) {
                holder.cbSelected.setVisibility(View.VISIBLE);
            } else {
                holder.cbSelected.setVisibility(View.GONE);
            }

            if (multiUiConfig.getImageItemBackgroundColor() != 0) {
                holder.ivPic.setBackgroundColor(multiUiConfig.getImageItemBackgroundColor());
            }
            holder.cbSelected.setSelectIconId(multiUiConfig.getSelectedIconID());
            holder.cbSelected.setUnSelectIconId(multiUiConfig.getUnSelectIconID());
            final ImageItem item = getItem(position);
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

            ViewGroup.LayoutParams params = holder.ivPic.getLayoutParams();
            params.width = params.height = (getScreenWidth() - dp_2() * 2) / selectConfig.getColumnCount();
            holder.v_masker.setLayoutParams(params);

            holder.ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectConfig.isShieldItem(item)) {
                        presenter.tip(mContext, mContext.getResources().getString(R.string.str_shield));
                        return;
                    }
                    if (mContext instanceof MultiImagePickerActivity) {
                        ((MultiImagePickerActivity) mContext).onImageClickListener(item, selectConfig.isShowCamera() ? position - 1 : position);
                    }
                }
            });

            if (presenter != null) {
                presenter.displayListImage(holder.ivPic, item.path, params.width);
            }
        }
        return convertView;
    }

    public void refreshData(List<ImageItem> items) {
        if (items != null && items.size() > 0) {
            images = items;
        }
        notifyDataSetChanged();
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private int dp_2() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 2, mContext.getResources().getDisplayMetrics());
    }

    class ViewHolder {
        ShowTypeImageView ivPic;
        CheckImageView cbSelected;
        View cbPanel, v_masker;
        LinearLayout mVideoLayout;
        TextView mVideoTime;
    }
}
