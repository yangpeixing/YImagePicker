package com.example.ypxredbookpicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ypxredbookpicker.ImageLoaderProvider;
import com.example.ypxredbookpicker.R;
import com.example.ypxredbookpicker.SelectPicAndCropActivity;
import com.example.ypxredbookpicker.bean.ImageItem;
import com.example.ypxredbookpicker.utils.ViewSizeUtils;

import java.util.List;

/**
 * Description: TODO
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class SelectPicAdapter extends RecyclerView.Adapter<SelectPicAdapter.ViewHolder> {
    private List<ImageItem> datas;
    private Context context;
    private ImageLoaderProvider imageLoader;

    public SelectPicAdapter(Context context, List<ImageItem> data, ImageLoaderProvider imageLoader) {
        this.context = context;
        this.datas = data;
        this.imageLoader = imageLoader;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_selectpic, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (i == 0) {
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon_item_photo));
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            viewHolder.v_mask.setVisibility(View.GONE);
            viewHolder.mTvIndex.setVisibility(View.GONE);
        } else {
            if(datas.get(i-1).isSelect()){
                viewHolder.v_mask.setVisibility(View.VISIBLE);
                viewHolder.mTvIndex.setVisibility(View.VISIBLE);
            }else {
                viewHolder.v_mask.setVisibility(View.GONE);
                viewHolder.mTvIndex.setVisibility(View.GONE);
            }
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (null != imageLoader) {
                imageLoader.displayListImage(viewHolder.imageView, datas.get(i - 1).path, 0);
            }
        }
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof SelectPicAndCropActivity){
                    ((SelectPicAndCropActivity)context).selectImage(viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView imageView;
        private RelativeLayout rootView;
        private View v_mask;
        private TextView mTvIndex;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvIndex= itemView.findViewById(R.id.mTvIndex);
            v_mask= itemView.findViewById(R.id.v_mask);
            imageView = itemView.findViewById(R.id.iv_image);
            rootView= itemView.findViewById(R.id.rootView);
            context = imageView.getContext();
            ViewSizeUtils.setViewSize(imageView, (ViewSizeUtils.getScreenWidth(context) - ViewSizeUtils.dp(context, 10)) / 4, 1.0f);
            ViewSizeUtils.setViewSize(v_mask, (ViewSizeUtils.getScreenWidth(context) - ViewSizeUtils.dp(context, 6)) / 4, 1.0f);
        }
    }
}
