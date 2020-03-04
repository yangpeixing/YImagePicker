package com.ypx.imagepicker.bean;

import android.net.Uri;

public class UriPathInfo {
    public Uri uri;
    public String absolutePath;

    public UriPathInfo(Uri uri, String absolutePath) {
        this.uri = uri;
        this.absolutePath = absolutePath;
    }
}
