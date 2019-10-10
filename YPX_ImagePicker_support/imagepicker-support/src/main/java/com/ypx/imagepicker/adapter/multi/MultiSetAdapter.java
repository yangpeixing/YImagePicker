package com.ypx.imagepicker.adapter.multi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Time: 2018/4/6 10:47
 * Author:yangpeixing
 * Description: 文件夹adapter
 */
public class MultiSetAdapter extends RecyclerView.Adapter<MultiSetAdapter.ViewHolder> {
    private int lastSelected = 0;
    private Context mContext;
    private List<ImageSet> mImageSets = new ArrayList<>();
    private IMultiPickerBindPresenter uiConfig;

    public MultiSetAdapter(Context context, IMultiPickerBindPresenter uiConfig) {
        this.mContext = context;
        this.uiConfig = uiConfig;
    }

    public void refreshData(List<ImageSet> folders) {
        if (folders != null && folders.size() > 0) {
            mImageSets = folders;
        } else {
            mImageSets.clear();
        }
        notifyDataSetChanged();
    }

    private ImageSet getItem(int i) {
        return mImageSets.get(i);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_list_item_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bindData(getItem(position));

        if (lastSelected == position) {
            holder.indicator.setVisibility(View.VISIBLE);
        } else {
            holder.indicator.setVisibility(View.INVISIBLE);
        }

        holder.indicator.setColorFilter(uiConfig.getUiConfig(mContext).getThemeColor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setSelectCallBack != null) {
                    setSelectCallBack.selectImageSet(getItem(position), position);
                }
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return mImageSets.size();
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView name;
        TextView size;
        ImageView indicator;

        ViewHolder(View view) {
            super(view);
            cover = view.findViewById(R.id.cover);
            name = view.findViewById(R.id.name);
            size = view.findViewById(R.id.size);
            indicator = view.findViewById(R.id.indicator);
            view.setTag(this);
        }

        @SuppressLint("DefaultLocale")
        void bindData(ImageSet data) {
            name.setText(data.name);
            size.setText(String.format("%d%s", data.count, mContext.getResources().getString(R.string.piece)));
            if (uiConfig != null) {
                ImageItem imageItem = new ImageItem();
                imageItem.path = data.coverPath;
                uiConfig.displayListImage(cover, imageItem, (getScreenWidth() - dp(2) * 2) / 3);
            }
        }
    }

    private SetSelectCallBack setSelectCallBack;

    public void setSetSelectCallBack(SetSelectCallBack setSelectCallBack) {
        this.setSelectCallBack = setSelectCallBack;
    }

    public interface SetSelectCallBack {
        void selectImageSet(ImageSet set, int pos);
    }
}
