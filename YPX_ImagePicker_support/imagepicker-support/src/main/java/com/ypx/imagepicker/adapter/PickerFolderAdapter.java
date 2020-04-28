package com.ypx.imagepicker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.views.wx.WXFolderItemView;
import com.ypx.imagepicker.views.base.PickerFolderItemView;

import java.util.ArrayList;
import java.util.List;


/**
 * Time: 2018/4/6 10:47
 * Author:yangpeixing
 * Description: 文件夹adapter
 */
public class PickerFolderAdapter extends RecyclerView.Adapter<PickerFolderAdapter.ViewHolder> {
    private List<ImageSet> mImageSets = new ArrayList<>();
    private IPickerPresenter presenter;
    private PickerUiConfig uiConfig;

    public PickerFolderAdapter(IPickerPresenter presenter, PickerUiConfig uiConfig) {
        this.presenter = presenter;
        this.uiConfig = uiConfig;
    }

    public void refreshData(List<ImageSet> folders) {
        mImageSets.clear();
        mImageSets.addAll(folders);
        notifyDataSetChanged();
    }

    private ImageSet getItem(int i) {
        return mImageSets.get(i);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picker_item_root, parent, false);
        return new ViewHolder(view, uiConfig);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ImageSet imageSet = getItem(position);
        PickerFolderItemView pickerFolderItemView = holder.pickerFolderItemView;
        pickerFolderItemView.displayCoverImage(imageSet, presenter);
        pickerFolderItemView.loadItem(imageSet);
        pickerFolderItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderSelectResult != null) {
                    folderSelectResult.folderSelected(getItem(position), position);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private PickerFolderItemView pickerFolderItemView;

        ViewHolder(View view, PickerUiConfig uiConfig) {
            super(view);
            pickerFolderItemView = uiConfig.getPickerUiProvider().getFolderItemView(view.getContext());
            if (pickerFolderItemView == null) {
                pickerFolderItemView = new WXFolderItemView(view.getContext());
            }
            FrameLayout layout = itemView.findViewById(R.id.mRoot);
            int height = pickerFolderItemView.getItemHeight();
            layout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    height > 0 ? height : ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.removeAllViews();
            layout.addView(pickerFolderItemView);
        }
    }

    private FolderSelectResult folderSelectResult;

    public void setFolderSelectResult(FolderSelectResult folderSelectResult) {
        this.folderSelectResult = folderSelectResult;
    }

    public interface FolderSelectResult {
        void folderSelected(ImageSet set, int pos);
    }
}
