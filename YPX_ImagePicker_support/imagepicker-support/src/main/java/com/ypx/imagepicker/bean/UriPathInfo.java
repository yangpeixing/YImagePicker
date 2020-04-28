package com.ypx.imagepicker.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class UriPathInfo implements Serializable, Parcelable {
    public Uri uri;
    public String absolutePath;

    public UriPathInfo(Uri uri, String absolutePath) {
        this.uri = uri;
        this.absolutePath = absolutePath;
    }

    protected UriPathInfo(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        absolutePath = in.readString();
    }

    public static final Creator<UriPathInfo> CREATOR = new Creator<UriPathInfo>() {
        @Override
        public UriPathInfo createFromParcel(Parcel in) {
            return new UriPathInfo(in);
        }

        @Override
        public UriPathInfo[] newArray(int size) {
            return new UriPathInfo[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(uri, flags);
        dest.writeString(absolutePath);
    }
}
