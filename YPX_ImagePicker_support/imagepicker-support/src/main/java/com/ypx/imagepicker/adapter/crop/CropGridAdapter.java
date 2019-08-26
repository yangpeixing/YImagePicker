package com.ypx.imagepicker.adapter.crop;

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

import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PViewSizeUtils;

import java.util.List;


/**
 * Description: 图片适配器
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class CropGridAdapter extends RecyclerView.Adapter<CropGridAdapter.ViewHolder> {
    private List<ImageItem> datas;
    private List<ImageItem> selectList;
    private Context context;
    private boolean isShowCamera;
    private boolean isVideoShowMask;
    private ICropPickerBindPresenter bindingProvider;
    private OnSelectImageListener mOnSelectImage;
    private OnPressImageListener mOnPressImageListener;

    public interface OnSelectImageListener {
        /**
         * @param position
         */
        void onSelectImage(int position);
    }

    public interface OnPressImageListener {
        /**
         * @param position
         * @param isShowTransit
         */
        void onPressImage(final int position, boolean isShowTransit);
    }

    public CropGridAdapter(Context context, boolean isShowCamera, boolean isVideoShowMask, List<ImageItem> data,
                           List<ImageItem> selectList, ICropPickerBindPresenter imageLoader) {
        this.context = context;
        this.isShowCamera = isShowCamera;
        this.isVideoShowMask = isVideoShowMask;
        this.datas = data;
        this.selectList = selectList;
        this.bindingProvider = imageLoader;
    }

    public void setOnSelectImageSet(OnSelectImageListener mOnSelectImageSet) {
        this.mOnSelectImage = mOnSelectImageSet;
    }

    public void setOnPressImageListener(OnPressImageListener mOnPressImageListener) {
        this.mOnPressImageListener = mOnPressImageListener;
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
        if (i == 0 && isShowCamera) {
            viewHolder.showCamera();
        } else {
            final ImageItem imageItem = datas.get(isShowCamera ? i - 1 : i);
            viewHolder.loadImage(imageItem);
            if (imageItem.isVideo()) {
                viewHolder.showVideo(imageItem, selectList.size() > 0 || isVideoShowMask ||
                        imageItem.duration > ImagePicker.MAX_VIDEO_DURATION);
            } else {
                viewHolder.mTvIndex.setVisibility(View.VISIBLE);
                viewHolder.iv_camera.setVisibility(View.GONE);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                viewHolder.mTvDuration.setVisibility(View.GONE);
                viewHolder.v_select.setVisibility(View.VISIBLE);
                if (imageItem.isPress()) {
                    PViewSizeUtils.setViewMargin(viewHolder.imageView, dp(2));
                    viewHolder.rootView.setBackgroundColor(context.getResources().getColor(R.color.picker_theme_color));
                    viewHolder.v_mask.setVisibility(View.VISIBLE);
                    viewHolder.v_mask.setBackgroundColor(context.getResources().getColor(R.color.picker_theme_color));
                } else {
                    PViewSizeUtils.setViewMargin(viewHolder.imageView, dp(1));
                    viewHolder.rootView.setBackgroundColor(Color.parseColor("#F0F0F0"));
                    viewHolder.v_mask.setVisibility(View.GONE);
                }

                viewHolder.v_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != mOnSelectImage) {
                            mOnSelectImage.onSelectImage(viewHolder.getAdapterPosition());
                        }
                    }
                });

                //如果当前item在选中列表里
                if (selectList != null && selectList.size() > 0) {
                    for (int t = 0; t < selectList.size(); t++) {
                        if (imageItem.equals(selectList.get(t))) {
                            imageItem.setSelectIndex(t);
                            viewHolder.mTvIndex.setBackground(PCornerUtils.cornerDrawableAndStroke(
                                    context.getResources().getColor(R.color.picker_theme_color), dp(12), dp(1), Color.WHITE));
                            viewHolder.mTvIndex.setText(String.format("%d", imageItem.getSelectIndex() + 1));
                            return;
                        }
                    }
                }

                viewHolder.mTvIndex.setText("");
                viewHolder.mTvIndex.setBackground(context.getResources().getDrawable(R.mipmap.picker_icon_unselect));
            }
        }

        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnPressImageListener) {
                    mOnPressImageListener.onPressImage(viewHolder.getAdapterPosition(), true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return isShowCamera ? datas.size() + 1 : datas.size();
    }

    public int dp(float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView imageView;
        private ImageView iv_camera;
        private RelativeLayout rootView;
        private View v_mask, v_select;
        private TextView mTvIndex;
        private TextView mTvDuration;

        ViewHolder(View itemView) {
            super(itemView);
            mTvIndex = itemView.findViewById(R.id.mTvIndex);
            v_mask = itemView.findViewById(R.id.v_mask);
            v_select = itemView.findViewById(R.id.v_select);
            imageView = itemView.findViewById(R.id.iv_image);
            iv_camera = itemView.findViewById(R.id.iv_camera);
            rootView = itemView.findViewById(R.id.rootView);
            mTvDuration = itemView.findViewById(R.id.mTvDuration);
            context = imageView.getContext();
            PViewSizeUtils.setViewSize(rootView, (PViewSizeUtils.getScreenWidth(context) - PViewSizeUtils.dp(context, 10)) / 4, 1.0f);
            PViewSizeUtils.setViewSize(v_mask, (PViewSizeUtils.getScreenWidth(context) - PViewSizeUtils.dp(context, 6)) / 4, 1.0f);
        }

        public void showCamera() {
            iv_camera.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            v_mask.setVisibility(View.GONE);
            mTvIndex.setVisibility(View.GONE);
            rootView.setBackgroundColor(Color.parseColor("#F0F0F0"));
            itemView.setTag(null);
        }

        public void showVideo(ImageItem imageItem, boolean isShowMask) {
            mTvDuration.setVisibility(View.VISIBLE);
            mTvDuration.setText(imageItem.getDurationFormat());
            v_select.setVisibility(View.GONE);
            mTvIndex.setVisibility(View.GONE);

            iv_camera.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            if (isShowMask) {
                v_mask.setVisibility(View.VISIBLE);
                v_mask.setBackgroundColor(Color.WHITE);
            } else {
                v_mask.setVisibility(View.GONE);
            }
            loadImage(imageItem);

//            if (imageItem.videoImageUri == null || imageItem.videoImageUri.length() == 0) {
//                String thumbPath = "";
//                MediaStore.Video.Thumbnails.getThumbnail(itemView.getContext().getContentResolver(), imageItem.getId(), MediaStore.Video.Thumbnails.MICRO_KIND, null);
//                String[] projection = {MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA};
//                Cursor cursor = itemView.getContext().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
//                        , projection
//                        , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
//                        , new String[]{imageItem.getId() + ""}
//                        , null);
//
//                while (cursor.moveToNext()) {
//                    thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
//                    imageItem.videoImageUri = thumbPath;
//                    loadImage(imageItem);
//                }
//                cursor.close();
//            } else {
//                loadImage(imageItem);
//            }
        }

        public void loadImage(ImageItem imageItem) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String path = imageItem.path;
            if (null != bindingProvider) {
                bindingProvider.displayListImage(imageView, imageItem);
            }
        }
    }
}
