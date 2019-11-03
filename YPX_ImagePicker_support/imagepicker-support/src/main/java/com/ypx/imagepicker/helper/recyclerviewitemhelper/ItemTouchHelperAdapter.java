package com.ypx.imagepicker.helper.recyclerviewitemhelper;

public interface ItemTouchHelperAdapter {

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    boolean isItemViewSwipeEnabled();
}
