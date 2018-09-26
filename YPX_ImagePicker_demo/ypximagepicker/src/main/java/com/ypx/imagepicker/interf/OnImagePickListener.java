package com.ypx.imagepicker.interf;

import com.ypx.imagepicker.bean.ImageItem;

import java.util.List;

/**
 * 作者：yangpeixing on 2018/6/21 14:27
 * 功能：
 * 产权：南京婚尚信息技术
 */
public class OnImagePickListener implements OnImagePickCompleteListener {
    private static final long serialVersionUID = 12544653L;
    private static ResultCallBack resultCallBack;

    public OnImagePickListener(ResultCallBack resultCallBack) {
        OnImagePickListener.resultCallBack = resultCallBack;
    }
    
    @Override
    public void onImagePickComplete(List<ImageItem> items) {
        if (resultCallBack != null) {
            resultCallBack.onPickResult(items);
        }
        // Log.e("OnImagePickListener", "onImagePickComplete: " + items);
    }

    public interface ResultCallBack {
        void onPickResult(List<ImageItem> list);
    }
}
