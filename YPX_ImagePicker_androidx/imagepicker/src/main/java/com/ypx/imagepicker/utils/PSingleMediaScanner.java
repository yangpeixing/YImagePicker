package com.ypx.imagepicker.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * @author yangpeixing
 * 媒体扫描刷新类
 */
public class PSingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mediaScannerConnection;
    private String mPath;
    private ScanListener mListener;

    public interface ScanListener {
        void onScanFinish();
    }

    public PSingleMediaScanner(Context context, String mPath, ScanListener mListener) {
        this.mPath = mPath;
        this.mListener = mListener;
        this.mediaScannerConnection = new MediaScannerConnection(context, this);
        this.mediaScannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mediaScannerConnection.scanFile(mPath, null);
    }

    @Override
    public void onScanCompleted(String mPath, Uri mUri) {
        mediaScannerConnection.disconnect();
        if (mListener != null) {
            mListener.onScanFinish();
        }
    }

    public static void refresh(Context context, String path, PSingleMediaScanner.ScanListener scanListener) {
        new PSingleMediaScanner(context.getApplicationContext(), path, scanListener);
    }
}
