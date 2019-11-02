package com.ypx.imagepicker.adapter.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypx.imagepicker.bean.CropSelectConfig;
import com.ypx.imagepicker.bean.CropUiConfig;
import com.ypx.imagepicker.presenter.ICropPickerBindPresenter;
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
    private CropSelectConfig selectConfig;
    private ICropPickerBindPresenter presenter;
    private OnSelectImageListener mOnSelectImage;
    private OnPressImageListener mOnPressImageListener;
    private Drawable maskDrawable;
    private Drawable stokeDrawable;
    private CropUiConfig uiConfig;

    public interface OnSelectImageListener {
        /**
         * @param position item索引
         */
        void onSelectImage(int position);
    }

    public interface OnPressImageListener {
        /**
         * @param position      item索引
         * @param isShowTransit 是否需要完全显示剪裁view
         */
        void onPressImage(final int position, boolean isShowTransit);
    }

    public CropGridAdapter(Context context, CropSelectConfig selectConfig, List<ImageItem> data,
                           List<ImageItem> selectList, ICropPickerBindPresenter presenter, CropUiConfig uiConfig) {
        this.context = context;
        this.selectConfig = selectConfig;
        this.datas = data;
        this.selectList = selectList;
        this.presenter = presenter;
        this.uiConfig = uiConfig;

        int themeColor = uiConfig.getThemeColor();
        int halfColor = Color.argb(100, Color.red(themeColor), Color.green(themeColor), Color.blue(themeColor));
        maskDrawable = PCornerUtils.cornerDrawableAndStroke(halfColor, 0, dp(2), themeColor);
        stokeDrawable = PCornerUtils.cornerDrawableAndStroke(themeColor, dp(12), dp(1), Color.WHITE);
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
        if (i == 0 && selectConfig.isShowCamera()) {
            viewHolder.iv_camera.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.mVSelect.setVisibility(View.GONE);
            viewHolder.mTvIndex.setVisibility(View.GONE);
            viewHolder.mVMask.setVisibility(View.GONE);
            viewHolder.itemView.setTag(null);
            viewHolder.bindListener();
        } else {
            viewHolder.iv_camera.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            ImageItem imageItem = datas.get(selectConfig.isShowCamera() ? i - 1 : i);
            viewHolder.loadImage(imageItem);
            viewHolder.bindListener();
            viewHolder.bindData(imageItem);
        }
    }

    @Override
    public int getItemCount() {
        return selectConfig.isShowCamera() ? datas.size() + 1 : datas.size();
    }

    public int dp(float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView iv_camera;
        private RelativeLayout rootView;
        private View mVMask, mVSelect;
        private TextView mTvIndex;
        private TextView mTvDuration;

        ViewHolder(View itemView) {
            super(itemView);
            mTvIndex = itemView.findViewById(R.id.mTvIndex);
            mVMask = itemView.findViewById(R.id.v_mask);
            mVSelect = itemView.findViewById(R.id.v_select);
            imageView = itemView.findViewById(R.id.iv_image);
            iv_camera = itemView.findViewById(R.id.iv_camera);
            rootView = itemView.findViewById(R.id.rootView);
            mTvDuration = itemView.findViewById(R.id.mTvDuration);
            context = imageView.getContext();
            int width = (PViewSizeUtils.getScreenWidth(context) - (PViewSizeUtils.dp(context, 2) *
                    (selectConfig.getColumnCount() + 1))) / selectConfig.getColumnCount();
            PViewSizeUtils.setViewSize(rootView, width, 1.0f);
            PViewSizeUtils.setViewSize(mVMask, width, 1.0f);
            iv_camera.setImageDrawable(itemView.getContext().getResources().getDrawable(uiConfig.getCameraIconID()));
            iv_camera.setBackgroundColor(uiConfig.getCameraBackgroundColor());
        }

        void loadImage(ImageItem imageItem) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (null != presenter) {
                presenter.displayListImage(imageView, imageItem, rootView.getMeasuredWidth());
            }
        }

        void bindListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mOnPressImageListener) {
                        mOnPressImageListener.onPressImage(getAdapterPosition(), true);
                    }
                }
            });

            mVSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mOnSelectImage) {
                        mOnSelectImage.onSelectImage(getAdapterPosition());
                    }
                }
            });
        }

        @SuppressLint("DefaultLocale")
        void bindData(ImageItem imageItem) {
            mVMask.setVisibility(View.GONE);
            if (imageItem.isVideo()) {
                mTvDuration.setVisibility(View.VISIBLE);
                mTvDuration.setText(imageItem.getDurationFormat());
                //如果当前选中列表的第一个item不是视频,或者该视频超过最大时长选择，则需要置灰该视频item
                if (selectConfig.hasFirstImageItem()
                        || (selectList != null && selectList.size() > 0 && !selectList.get(0).isVideo())
                        || imageItem.duration > selectConfig.getMaxVideoDuration()
                        || imageItem.duration < selectConfig.getMinVideoDuration()) {
                    mVMask.setVisibility(View.VISIBLE);
                    mVMask.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                    hideCheckBox();
                    return;
                } else {
                    if (selectConfig.isVideoSinglePick()) {
                        hideCheckBox();
                    } else {
                        showCheckBox();
                    }
                }
            } else {
                mTvDuration.setVisibility(View.GONE);
                //如果当前选中列表的第一个item是视频，则需要置灰该图片item
                if (selectConfig.hasFirstVideoItem()
                        || (selectList != null && selectList.size() > 0 && selectList.get(0).isVideo())) {
                    mVMask.setVisibility(View.VISIBLE);
                    mVMask.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                    hideCheckBox();
                    return;
                } else {
                    showCheckBox();
                }
            }

            //选中item时添加边框和蒙层
            if (imageItem.isPress()) {
                //如果视频单选，
                if (imageItem.isVideo() && selectConfig.isVideoSinglePick()) {
                    mVMask.setVisibility(View.GONE);
                } else {
                    mVMask.setVisibility(View.VISIBLE);
                    mVMask.setBackground(maskDrawable);
                }
            }

            //如果当前item在选中列表里
            if (selectList != null) {
                int index = selectList.indexOf(imageItem);
                if (index >= 0) {
                    mTvIndex.setText(String.format("%d", index + 1));
                    mTvIndex.setBackground(stokeDrawable);
                } else {
                    mTvIndex.setBackground(itemView.getContext().getResources().
                            getDrawable(uiConfig.getUnSelectIconID()));
                    mTvIndex.setText("");
                }
            }
        }

        void showCheckBox() {
            mTvIndex.setVisibility(View.VISIBLE);
            mVSelect.setVisibility(View.VISIBLE);
        }

        void hideCheckBox() {
            mTvIndex.setVisibility(View.GONE);
            mVSelect.setVisibility(View.GONE);
        }
    }
}
