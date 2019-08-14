package com.ypx.imagepicker.adapter.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSet;

import java.util.List;

/**
 * Description: 文件夹列表
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class CropSetAdapter extends RecyclerView.Adapter<CropSetAdapter.ViewHolder> {
    private List<ImageSet> datas;
    private Context context;
    private ICropPickerBindPresenter imageLoader;
    private OnSelectImageSetListener mOnSelectImageSetListener;

    public interface OnSelectImageSetListener {
        /**
         * @param position
         */
        void onSelectImageSet(int position);
    }

    public CropSetAdapter(Context context, List<ImageSet> data, ICropPickerBindPresenter imageLoader) {
        this.context = context;
        this.datas = data;
        this.imageLoader = imageLoader;
    }

    public void setOnSelectImageSetListener(OnSelectImageSetListener mOnSelectImageSetListener) {
        this.mOnSelectImageSetListener = mOnSelectImageSetListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.picker_item_iamgeset, viewGroup, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        ImageSet imageSet = datas.get(i);
        if (imageSet.isSelected) {
            viewHolder.itemView.setBackgroundColor(context.getResources().getColor(R.color.press));
        } else {
            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.picker_selector_list_item_bg));
        }
        viewHolder.mTvCount.setText(imageSet.imageItems.size() + "");
        viewHolder.mTvSetName.setText(imageSet.name);
        if (imageLoader != null) {
            ImageItem imageItem = imageSet.imageItems.get(0);
            imageLoader.displayListImage(viewHolder.imageView, imageItem);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnSelectImageSetListener) {
                    mOnSelectImageSetListener.onSelectImageSet(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView imageView;
        private TextView mTvSetName;
        private TextView mTvCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvSetName = itemView.findViewById(R.id.mTvSetName);
            mTvCount = itemView.findViewById(R.id.mTvCount);
            imageView = itemView.findViewById(R.id.iv_image);
            context = imageView.getContext();
        }
    }
}
