package com.ypx.imagepicker.views.wx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.MultiPreviewAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.PickerItemDisableCode;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.bean.selectconfig.MultiSelectConfig;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.helper.recyclerviewitemhelper.SimpleItemTouchHelperCallback;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.utils.PCornerUtils;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepicker.views.base.PickerControllerView;
import com.ypx.imagepicker.views.base.PreviewControllerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class WXPreviewControllerView extends PreviewControllerView {
    private RecyclerView mPreviewRecyclerView;
    private RelativeLayout mBottomBar;
    private CheckBox mSelectCheckBox;
    private CheckBox mOriginalCheckBox;
    private MultiPreviewAdapter previewAdapter;
    private IPickerPresenter presenter;
    private BaseSelectConfig selectConfig;
    private PickerUiConfig uiConfig;
    private ArrayList<ImageItem> selectedList;
    private FrameLayout mTitleContainer;
    private boolean isShowOriginal = false;

    public WXPreviewControllerView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.picker_wx_preview;
    }


    @Override
    protected void initView(View view) {
        mPreviewRecyclerView = view.findViewById(R.id.mPreviewRecyclerView);
        mBottomBar = view.findViewById(R.id.bottom_bar);
        mSelectCheckBox = view.findViewById(R.id.mSelectCheckBox);
        mOriginalCheckBox = view.findViewById(R.id.mOriginalCheckBox);
        mTitleContainer = view.findViewById(R.id.mTitleContainer);
        mBottomBar.setClickable(true);
        setOriginalCheckBoxDrawable(R.mipmap.picker_wechat_unselect, R.mipmap.picker_wechat_select);
        setSelectCheckBoxDrawable(R.mipmap.picker_wechat_unselect, R.mipmap.picker_wechat_select);
    }

    @Override
    public void setStatusBar() {
        setTitleBarColor(getResources().getColor(R.color.white_F5));
        setBottomBarColor(Color.parseColor("#f0303030"));
    }

    @Override
    public void initData(BaseSelectConfig selectConfig, IPickerPresenter presenter, PickerUiConfig uiConfig, ArrayList<ImageItem> selectedList) {
        this.selectConfig = selectConfig;
        this.presenter = presenter;
        this.selectedList = selectedList;
        this.uiConfig = uiConfig;
        isShowOriginal = (selectConfig instanceof MultiSelectConfig && ((MultiSelectConfig) selectConfig).isShowOriginalCheckBox());
        initUI();
        initPreviewList();
    }

    private void initUI() {
        titleBar = uiConfig.getPickerUiProvider().getTitleBar(getContext());
        if (titleBar == null) {
            titleBar = new WXTitleBar(getContext());
        }
        mTitleContainer.addView(titleBar, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mSelectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mSelectCheckBox.setChecked(false);
                    selectedList.remove(currentImageItem);
                } else {
                    int disableCode = PickerItemDisableCode.getItemDisableCode(currentImageItem, selectConfig, selectedList,
                            selectedList.contains(currentImageItem));

                    if (disableCode != PickerItemDisableCode.NORMAL) {
                        String message = PickerItemDisableCode.getMessageFormCode(getContext(), disableCode, presenter, selectConfig);
                        if (message.length() > 0) {
                            presenter.tip(new WeakReference<>(getContext()).get(), message);
                        }
                        mSelectCheckBox.setChecked(false);
                        return;
                    }
                    if (!selectedList.contains(currentImageItem)) {
                        selectedList.add(currentImageItem);
                    }
                    mSelectCheckBox.setChecked(true);
                }

                titleBar.refreshCompleteViewState(selectedList, selectConfig);
                notifyPreviewList(currentImageItem);
            }
        });

        mOriginalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSelectCheckBox.setChecked(true);
                }
                ImagePicker.isOriginalImage = isChecked;
            }
        });
    }

    private void initPreviewList() {
        mPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        previewAdapter = new MultiPreviewAdapter(selectedList, presenter);
        mPreviewRecyclerView.setAdapter(previewAdapter);
        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(previewAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mPreviewRecyclerView);
    }

    /**
     * 刷新预览编辑列表
     *
     * @param imageItem 当前预览的图片
     */
    private void notifyPreviewList(ImageItem imageItem) {
        previewAdapter.setPreviewImageItem(imageItem);
        if (selectedList.contains(imageItem)) {
            mPreviewRecyclerView.smoothScrollToPosition(selectedList.indexOf(imageItem));
        }
    }

    private PickerControllerView titleBar;

    @Override
    public View getCompleteView() {
        return titleBar.getCanClickToCompleteView();
    }

    @Override
    public void singleTap() {
        if (mTitleContainer.getVisibility() == View.VISIBLE) {
            mTitleContainer.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_top_out));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_fade_out));
            mPreviewRecyclerView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_fade_out));
            mTitleContainer.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
            mPreviewRecyclerView.setVisibility(View.GONE);
        } else {
            mTitleContainer.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_top_in));
            mBottomBar.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_fade_in));
            mPreviewRecyclerView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.picker_fade_in));
            mTitleContainer.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
            mPreviewRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private ImageItem currentImageItem;

    @SuppressLint("DefaultLocale")
    @Override
    public void onPageSelected(int position, ImageItem imageItem, int totalPreviewCount) {
        this.currentImageItem = imageItem;
        titleBar.setTitle(String.format("%d/%d", position + 1, totalPreviewCount));
        mSelectCheckBox.setChecked(selectedList.contains(imageItem));
        notifyPreviewList(imageItem);
        titleBar.refreshCompleteViewState(selectedList, selectConfig);

        if (!imageItem.isVideo() && isShowOriginal) {
            mOriginalCheckBox.setVisibility(VISIBLE);
            if (ImagePicker.isOriginalImage) {
                mOriginalCheckBox.setChecked(true);
            }
        } else {
            mOriginalCheckBox.setVisibility(GONE);
        }
    }

    public void setOriginalCheckBoxDrawable(int unCheckDrawableID, int checkedDrawableID) {
        PCornerUtils.setCheckBoxDrawable(mOriginalCheckBox, checkedDrawableID, unCheckDrawableID);
    }

    public void setSelectCheckBoxDrawable(int unCheckDrawableID, int checkedDrawableID) {
        PCornerUtils.setCheckBoxDrawable(mSelectCheckBox, checkedDrawableID, unCheckDrawableID);
    }

    public void setTitleBarColor(int titleBarColor) {
        mTitleContainer.setBackgroundColor(titleBarColor);
        mTitleContainer.setPadding(0, PStatusBarUtil.getStatusBarHeight(getContext()), 0, 0);
        PStatusBarUtil.setStatusBar((Activity) getContext(), Color.TRANSPARENT, true,
                PStatusBarUtil.isDarkColor(titleBarColor));
    }

    public void setBottomBarColor(int bottomBarColor) {
        mBottomBar.setBackgroundColor(bottomBarColor);
        mPreviewRecyclerView.setBackgroundColor(bottomBarColor);
    }
}
