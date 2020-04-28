package com.ypx.imagepicker.bean;



import android.support.v4.util.ArraySet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
public enum MimeType {

    // ============== images ==============
    JPEG("image/jpeg", arraySetOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", arraySetOf(
            "png"
    )),
    GIF("image/gif", arraySetOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", arraySetOf(
            "bmp", "x-ms-bmp"
    )),
    WEBP("image/webp", arraySetOf(
            "webp"
    )),

    // ============== videos ==============
    MPEG("video/mpeg", arraySetOf(
            "mpeg",
            "mpg"
    )),
    MP4("video/mp4", arraySetOf(
            "mp4"
    )),
    QUICKTIME("video/quicktime", arraySetOf(
            "mov","quicktime"
    )),
    THREEGPP("video/3gpp", arraySetOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("video/3gpp2", arraySetOf(
            "3g2",
            "3gpp2"
    )),
    MKV("video/x-matroska", arraySetOf(
            "mkv","x-matroska"
    )),
    WEBM("video/webm", arraySetOf(
            "webm"
    )),
    TS("video/mp2ts", arraySetOf(
            "ts","mp2ts"
    )),
    AVI("video/avi", arraySetOf(
            "avi"
    ));

    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public Set<String> getExtensions() {
        return mExtensions;
    }

    public String getSuffix() {
        return new ArrayList<>(mExtensions).get(0);
    }

    public static Set<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static Set<MimeType> of(MimeType type, MimeType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<MimeType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<MimeType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }

    public static boolean isImage(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("image");
    }

    public static boolean isVideo(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("video");
    }

    public static boolean isGif(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.equals(MimeType.GIF.toString());
    }

    private static Set<String> arraySetOf(String... suffixes) {
        return new ArraySet<>(Arrays.asList(suffixes));
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public static ArrayList<String> getMimeTypeList(Set<MimeType> mimeTypes) {
        if (mimeTypes == null) {
            return new ArrayList<>();
        }
        ArrayList<String> mimeList = new ArrayList<>();
        for (MimeType mimeType : mimeTypes) {
            if (mimeType.mExtensions != null) {
                for (String s : mimeType.mExtensions) {
                    if (MimeType.isImage(String.valueOf(mimeType))) {
                        mimeList.add("image/" + s);
                    } else if (MimeType.isVideo(String.valueOf(mimeType))) {
                        mimeList.add("video/" + s);
                    }
                }
            }
        }
        return mimeList;
    }
}
