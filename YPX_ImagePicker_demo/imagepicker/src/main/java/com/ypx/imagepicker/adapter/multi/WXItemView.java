package com.ypx.imagepicker.adapter.multi;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.widget.CheckImageView;
import com.ypx.imagepicker.widget.ShowTypeImageView;

import java.util.ArrayList;

/**
 * Time: 2019/8/8 15:45
 * Author:ypx
 * Description: 微信样式item
 */
public class WXItemView extends BaseItemView {
    private PickerSelectConfig selectConfig;
    private IMultiPickerBindPresenter presenter;
    private ShowTypeImageView mIvThumb;
    private View mVMasker;
    private CheckImageView mIvThumbCheck;
    private TextView mVideoTime;
    private LinearLayout mVideoLayout;

    protected WXItemView(Context context) {
        super(context);
    }

    @Override
    public void initData(PickerSelectConfig selectConfig, IMultiPickerBindPresenter presenter, PickerUiConfig uiConfig) {
        super.initData(selectConfig, presenter, uiConfig);
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        if (uiConfig.getPickerItemBackgroundColor() != 0) {
            mIvThumb.setBackgroundColor(uiConfig.getPickerItemBackgroundColor());
        }
        mIvThumbCheck.setSelectIconId(uiConfig.getSelectedIconID());
        mIvThumbCheck.setUnSelectIconId(uiConfig.getUnSelectIconID());

    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_image_grid_item;
    }

    @Override
    protected void initView(View view) {
        mIvThumb = view.findViewById(R.id.iv_thumb);
        mVMasker = view.findViewById(R.id.v_masker);
        mIvThumbCheck = view.findViewById(R.id.iv_thumb_check);
        mVideoTime = view.findViewById(R.id.mVideoTime);
        mVideoLayout = view.findViewById(R.id.mVideoLayout);
    }

    @Override
    protected void bindData(final ImageItem item, final RecyclerView.Adapter adapter,
                            final int position,
                            final ArrayList<ImageItem> selectImageList,
                            final MultiGridAdapter.OnActionResult onActionResult) {

        if (presenter != null) {
            presenter.displayListImage(mIvThumb, item, getLayoutParams().height);
        }

        //如果是视频
        if (item.isVideo()) {
            mVideoLayout.setVisibility(View.VISIBLE);
            mVideoTime.setText(item.getDurationFormat());
            mIvThumb.setType(ShowTypeImageView.TYPE_NONE);
        } else {
            mVideoLayout.setVisibility(View.GONE);
            mIvThumb.setTypeWithUrlAndSize(item);
        }

        //如果只能选择图片或视频的一种，当选中一种类型时，把另一种置灰
        if (selectConfig.isSinglePickImageOrVideoType() && selectImageList != null && selectImageList.size() > 0) {
            //如果当前类型和选中的第一个item的类型不一样，则置灰
            if (selectImageList.get(0).isVideo() != item.isVideo()) {
                mIvThumbCheck.setVisibility(View.GONE);
                mVMasker.setVisibility(View.VISIBLE);
                mVMasker.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                mIvThumb.setOnClickListener(null);
                return;
            }
        }

        //屏蔽列表
        if (selectConfig.isShieldItem(item)) {
            mIvThumbCheck.setVisibility(View.GONE);
            mVMasker.setVisibility(View.VISIBLE);
            mVMasker.setBackgroundColor(Color.parseColor("#80FFFFFF"));
        } else {
            if ((selectConfig.isVideoSinglePick() && item.isVideo())
                    || selectConfig.getMaxCount() <= 1) {
                mIvThumbCheck.setVisibility(View.GONE);
            } else {
                mIvThumbCheck.setVisibility(View.VISIBLE);
            }

            //是否选中
            if (selectImageList != null && selectImageList.contains(item)) {
                mIvThumbCheck.setChecked(true);
                mVMasker.setVisibility(View.VISIBLE);
                mVMasker.setBackgroundColor(Color.parseColor("#80000000"));
            } else {
                mIvThumbCheck.setChecked(false);
                mVMasker.setVisibility(View.GONE);
            }
        }

        mIvThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onActionResult != null) {
                    onActionResult.onClickItem(item, position);
                }
            }
        });


        mIvThumbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIvThumbCheck.toggle();
                if (onActionResult != null) {
                    onActionResult.onCheckItem(item, mIvThumbCheck.isChecked());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}
