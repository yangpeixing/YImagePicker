package com.ypx.imagepicker.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.YPXImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.imp.ImageSelectMode;
import com.ypx.imagepicker.ui.activity.ImagesGridActivity;
import com.ypx.imagepicker.widget.ShowTypeImageView;
import com.ypx.imagepicker.widget.SuperCheckBox;

import java.util.List;

/**
 * 作者：yangpeixing on 2018/4/6 10:32
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class ImageGridAdapter extends BaseAdapter {
    private static final int ITEM_TYPE_CAMERA = 0;
    private static final int ITEM_TYPE_NORMAL = 1;
    private List<ImageItem> images;
    private Context mContext;
    private YPXImagePicker imagePicker;

    public ImageGridAdapter(Context ctx, List<ImageItem> images, YPXImagePicker imagePicker) {
        this.images = images;
        this.mContext = ctx;
        this.imagePicker = imagePicker;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (imagePicker.isShouldShowCamera()) {
            return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        }
        return ITEM_TYPE_NORMAL;
    }


    @Override
    public int getCount() {
        return imagePicker.isShouldShowCamera() ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (imagePicker.isShouldShowCamera()) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ipk_grid_item_camera, parent, false);
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof ImagesGridActivity) {
                        ((ImagesGridActivity) mContext).takePhoto();
                    }
                }
            });
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ipk_image_grid_item, null);
                holder = new ViewHolder();
                holder.ivPic = (ShowTypeImageView) convertView.findViewById(R.id.iv_thumb);
                holder.cbSelected = (SuperCheckBox) convertView.findViewById(R.id.iv_thumb_check);
                holder.cbPanel = convertView.findViewById(R.id.thumb_check_panel);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (YPXImagePicker.selectMode == ImageSelectMode.MODE_MULTI) {
                holder.cbSelected.setVisibility(View.VISIBLE);
            } else {
                holder.cbSelected.setVisibility(View.GONE);
            }

            final ImageItem item = getItem(position);

            holder.cbSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagePicker.getSelectImageCount() > imagePicker.getSelectLimit()) {
                        if (holder.cbSelected.isChecked()) {
                            //had better use ImageView instead of CheckBox
                            holder.cbSelected.toggle();//do this because CheckBox will auto toggle when clicking,must inverse
                            @SuppressLint("StringFormatMatches")
                            String toast = mContext.getResources().getString(R.string.you_have_a_select_limit, imagePicker.getSelectLimit());
                            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            holder.cbSelected.setOnCheckedChangeListener(null);
            if (imagePicker.isSelect(position, item)) {
                holder.cbSelected.setChecked(true);
                holder.ivPic.setSelected(true);
            } else {
                holder.cbSelected.setChecked(false);
            }

            ViewGroup.LayoutParams params = holder.ivPic.getLayoutParams();
            params.width = params.height = (getScreenWidth() - dp_2() * 2) / 3;

            holder.ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext instanceof ImagesGridActivity) {
                        ((ImagesGridActivity) mContext).onImageClickListener(item, position);
                    }
                }
            });

            holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        imagePicker.addSelectedImageItem(position, item);
                    } else {
                        imagePicker.deleteSelectedImageItem(position, item);
                    }
                }

            });
            holder.ivPic.setTypeWithUrlAndSize(item.path, item.width, item.height);
            imagePicker.getImgLoader().onPresentImage(holder.ivPic, getItem(position).path, params.width);
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
        SuperCheckBox cbSelected;
        View cbPanel;
    }
}
