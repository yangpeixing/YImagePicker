package com.example.imagepicker_support.style.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ypx.imagepicker.adapter.MultiPreviewAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.helper.recyclerviewitemhelper.SimpleItemTouchHelperCallback;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.views.base.PreviewControllerView;
import com.example.imagepicker_support.R;

import java.util.ArrayList;

public class CustomPreviewControllerView extends PreviewControllerView {
    private RelativeLayout mTitleBar;
    private ImageView mBackImg;
    private TextView mTvNext;
    private TextView mTvIndex;
    private RecyclerView mPreviewRecyclerView;

    private MultiPreviewAdapter previewAdapter;
    private IPickerPresenter presenter;
    private BaseSelectConfig selectConfig;
    private ArrayList<ImageItem> selectedList;

    private ImageItem currentImageItem;
    private int mCurrentItemPosition;

    public CustomPreviewControllerView(Context context) {
        super(context);
    }

    /**
     * @return item布局id
     */
    @Override
    protected int getLayoutId() {
        return R.layout.layout_custom_preview;
    }

    /**
     * @param view 初始化view
     */
    @Override
    protected void initView(View view) {
        mTitleBar = view.findViewById(R.id.mTitleBar);
        mBackImg = view.findViewById(R.id.mBackImg);
        mTvNext = view.findViewById(R.id.mTvNext);
        mTvIndex = view.findViewById(R.id.mTvIndex);
        mPreviewRecyclerView = view.findViewById(R.id.mPreviewRecyclerView);
    }

    /**
     * 设置状态栏
     */
    @Override
    public void setStatusBar() {
        PStatusBarUtil.fullScreenWithCheckNotch((Activity) getContext(), Color.BLACK);
    }

    /**
     * 初始化数据
     *
     * @param selectConfig 选择配置项
     * @param presenter    presenter
     * @param uiConfig     ui配置类
     * @param selectedList 已选中列表
     */
    @Override
    public void initData(BaseSelectConfig selectConfig, IPickerPresenter presenter,
                         PickerUiConfig uiConfig, ArrayList<ImageItem> selectedList) {
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        this.selectedList = selectedList;
        initPreviewList();
        initListener();
    }

    private void initPreviewList() {
        mPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        previewAdapter = new MultiPreviewAdapter(selectedList, presenter);
        mPreviewRecyclerView.setAdapter(previewAdapter);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(previewAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mPreviewRecyclerView);
    }

    @SuppressLint("DefaultLocale")
    private void initListener() {
        mTvIndex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedList.contains(currentImageItem)) {
                    selectedList.remove(currentImageItem);
                } else {
                    if (selectedList.size() >= selectConfig.getMaxCount()) {
                        presenter.overMaxCountTip(getContext(), selectConfig.getMaxCount());
                        return;
                    } else {
                        if (!selectedList.contains(currentImageItem)) {
                            selectedList.add(currentImageItem);
                            currentImageItem.setSelectIndex(mCurrentItemPosition);
                            if (mTitleBar.getVisibility() == View.GONE) {
                                singleTap();
                            }
                        }
                    }
                }
                refreshNextBtn();
            }
        });

        mBackImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * @return 获取可以点击完成的View
     */
    @Override
    public View getCompleteView() {
        return mTvNext;
    }

    /**
     * 单击图片
     */
    @Override
    public void singleTap() {
        if (mTitleBar.getVisibility() == View.VISIBLE) {
            mTitleBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_top_out));
            mTvIndex.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_top_out));
            mPreviewRecyclerView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_fade_out));
            mTitleBar.setVisibility(View.GONE);
            mPreviewRecyclerView.setVisibility(View.GONE);
            mPreviewRecyclerView.setVisibility(View.GONE);
            mTvIndex.setVisibility(View.GONE);
        } else {
            mTitleBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_top_in));
            mTvIndex.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_top_in));
            mPreviewRecyclerView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_fade_in));
            mTitleBar.setVisibility(View.VISIBLE);
            mTvIndex.setVisibility(View.VISIBLE);
            mPreviewRecyclerView.setVisibility(View.VISIBLE);
            mPreviewRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 图片切换回调
     *
     * @param position          当前图片索引
     * @param imageItem         当前图片信息
     * @param totalPreviewCount 总预览数
     */
    @Override
    public void onPageSelected(int position, ImageItem imageItem, int totalPreviewCount) {
        mCurrentItemPosition = position;
        currentImageItem = imageItem;
        notifyPreviewList();
        refreshNextBtn();
    }


    /**
     * 刷新预览编辑列表
     */
    private void notifyPreviewList() {
        previewAdapter.setPreviewImageItem(currentImageItem);
        if (selectedList.contains(currentImageItem)) {
            mPreviewRecyclerView.smoothScrollToPosition(selectedList.indexOf(currentImageItem));
        }
    }


    @SuppressLint("DefaultLocale")
    private void refreshNextBtn() {
        int index = selectedList.indexOf(currentImageItem);
        if (index >= 0) {
            mTvIndex.setText(String.format("%d", index + 1));
            Drawable drawable = PCornerUtils.cornerDrawableAndStroke(Color.parseColor("#859D7B"), dp(30), dp(1), Color.WHITE);
            mTvIndex.setBackground(drawable);
            mPreviewRecyclerView.scrollToPosition(index);
        } else {
            mTvIndex.setText("");
            mTvIndex.setBackground(getResources().getDrawable(com.ypx.imagepicker.R.mipmap.picker_icon_unselect));
        }

        if (selectedList == null || selectedList.size() == 0) {
            mTvNext.setText("下一步");
            mTvNext.setTextColor(Color.parseColor("#999999"));
        } else {
            String text = String.format("%s(%d/%d)", "下一步", selectedList.size(), selectConfig.getMaxCount());
            mTvNext.setText(text);
            mTvNext.setTextColor(Color.parseColor("#859D7B"));
        }
    }
}