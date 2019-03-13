package com.ypx.imagepicker.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypx.imagepicker.ImageLoaderProvider;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.ImagePickAndCropActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.utils.CornerUtils;
import com.ypx.imagepicker.utils.ViewSizeUtils;

import java.util.List;


/**
 * Description: 图片适配器
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ViewHolder> {
    private List<ImageItem> datas;
    private List<ImageItem> selectList;
    private Context context;
    private ImageLoaderProvider imageLoader;

    public ImageGridAdapter(Context context, List<ImageItem> data, List<ImageItem> selectList, ImageLoaderProvider imageLoader) {
        this.context = context;
        this.datas = data;
        this.selectList = selectList;
        this.imageLoader = imageLoader;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.picker_item_imagegrid, viewGroup, false));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (i == 0) {
            viewHolder.iv_camera.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.v_mask.setVisibility(View.GONE);
            viewHolder.mTvIndex.setVisibility(View.GONE);
            viewHolder.rootView.setBackgroundColor(Color.parseColor("#F0F0F0"));
            viewHolder.itemView.setTag(null);
        } else {
            viewHolder.mTvIndex.setVisibility(View.VISIBLE);
            viewHolder.iv_camera.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            final ImageItem imageItem = datas.get(i - 1);
            if (imageItem.isPress()) {
                ViewSizeUtils.setViewMargin(viewHolder.imageView, dp(2));
                viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.picker_theme_color));
                viewHolder.v_mask.setVisibility(View.VISIBLE);
            } else {
                ViewSizeUtils.setViewMargin(viewHolder.imageView, dp(1));
                viewHolder.rootView.setBackgroundColor(Color.parseColor("#F0F0F0"));
                viewHolder.v_mask.setVisibility(View.GONE);
            }

            if (imageItem.isSelect()) {
                viewHolder.mTvIndex.setBackground(CornerUtils.cornerDrawableAndStroke(
                        context.getResources().getColor(R.color.picker_theme_color), dp(12), dp(1), Color.WHITE));
                for (int t = 0; t < selectList.size(); t++) {
                    if (imageItem.path.equals(selectList.get(t).path)) {
                        imageItem.setSelectIndex(t + 1);
                    }
                }
                viewHolder.mTvIndex.setText(String.format("%d", imageItem.getSelectIndex()));
            } else {
                viewHolder.mTvIndex.setText("");
                viewHolder.mTvIndex.setBackground(context.getResources().getDrawable(R.mipmap.picker_icon_unselect));
            }
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (null != imageLoader) {
                if (viewHolder.itemView.getTag() instanceof String) {
                    if (!viewHolder.itemView.getTag().equals(imageItem.path)) {
                        imageLoader.displayListImage(viewHolder.imageView, imageItem.path);
                    }
                } else {
                    imageLoader.displayListImage(viewHolder.imageView, imageItem.path);
                }
                viewHolder.itemView.setTag(imageItem.path);
            }

            viewHolder.v_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof ImagePickAndCropActivity) {
                        ((ImagePickAndCropActivity) context).selectImage(viewHolder.getAdapterPosition());
                    }
                }
            });
        }
        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ImagePickAndCropActivity) {
                    ((ImagePickAndCropActivity) context).pressImage(viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    public int dp(int dp) {
        return ViewSizeUtils.dp(context, dp);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView imageView;
        private ImageView iv_camera;
        private RelativeLayout rootView;
        private View v_mask,v_select;
        private TextView mTvIndex;

        ViewHolder(View itemView) {
            super(itemView);
            mTvIndex = itemView.findViewById(R.id.mTvIndex);
            v_mask = itemView.findViewById(R.id.v_mask);
            v_select = itemView.findViewById(R.id.v_select);
            imageView = itemView.findViewById(R.id.iv_image);
            iv_camera = itemView.findViewById(R.id.iv_camera);
            rootView = itemView.findViewById(R.id.rootView);
            context = imageView.getContext();
            ViewSizeUtils.setViewSize(rootView, (ViewSizeUtils.getScreenWidth(context) - ViewSizeUtils.dp(context, 10)) / 4, 1.0f);
            ViewSizeUtils.setViewSize(v_mask, (ViewSizeUtils.getScreenWidth(context) - ViewSizeUtils.dp(context, 6)) / 4, 1.0f);
        }
    }
}
