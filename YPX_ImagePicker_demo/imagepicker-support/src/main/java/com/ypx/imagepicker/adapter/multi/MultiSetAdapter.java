package com.ypx.imagepicker.adapter.multi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Time: 2018/4/6 10:47
 * Author:yangpeixing
 * Description: 文件夹adapter
 */
public class MultiSetAdapter extends BaseAdapter {
    private int lastSelected = 0;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ImageSet> mImageSets = new ArrayList<>();
    private IMultiPickerBindPresenter uiConfig;

    public MultiSetAdapter(Context context, IMultiPickerBindPresenter uiConfig) {
        this.mContext = context;
        this.uiConfig = uiConfig;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refreshData(List<ImageSet> folders) {
        if (folders != null && folders.size() > 0) {
            mImageSets = folders;
        } else {
            mImageSets.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mImageSets.size();
    }

    @Override
    public ImageSet getItem(int i) {
        return mImageSets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.picker_list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.bindData(getItem(i));

        if (lastSelected == i) {
            holder.indicator.setVisibility(View.VISIBLE);
        } else {
            holder.indicator.setVisibility(View.INVISIBLE);
        }

        holder.indicator.setColorFilter(uiConfig.getUiConfig(mContext).getThemeColor());

        return view;
    }

    public int getSelectIndex() {
        return lastSelected;
    }

    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
        notifyDataSetChanged();
    }

    private int dp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) dp, mContext.getResources().getDisplayMetrics());
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            cover = (ImageView) view.findViewById(R.id.cover);
            name = (TextView) view.findViewById(R.id.name);
            size = (TextView) view.findViewById(R.id.size);
            indicator = (ImageView) view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        @SuppressLint("DefaultLocale")
        void bindData(ImageSet data) {
            name.setText(data.name);
            size.setText(String.format("%d%s", data.imageItems.size(), mContext.getResources().getString(R.string.piece)));
            if (uiConfig != null) {
                uiConfig.displayListImage(cover, data.cover, (getScreenWidth() - dp(2) * 2) / 3);
            }
        }
    }
}
