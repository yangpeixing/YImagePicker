package com.ypx.imagepicker.bean;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


import com.ypx.imagepicker.utils.PBitmapUtils;
import com.ypx.imagepicker.widget.cropimage.Info;

import java.io.Serializable;


/**
 * Description: 图片信息
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class ImageItem implements Serializable, Parcelable {
    private static final long serialVersionUID = 3429291195776736078L;
    //媒体文件ID,可通过此id查询此媒体文件的所有信息
    public long id;
    //媒体文件宽
    public int width;
    //高
    public int height;
    //生成或者更新时间
    public long time;
    //时常（仅针对视频）
    public long duration;
    //文件类型
    public String mimeType;
    //更新时间格式化 例如：2019年12月  本周内 等
    public String timeFormat;
    //时常格式化  00：00：00
    public String durationFormat;
    //是否是视频文件
    private boolean isVideo = false;
    //是否是原图
    public boolean isOriginalImage = true;
    //文件名
    public String displayName;

    //视频缩略图地址，默认是null，并没有扫描视频缩略图，这里提供此变量便于使用者自己塞入使用
    private String videoImageUri;
    // 加入滤镜后的原图图片地址,如果无滤镜返回原图地址，这里提供此变量便于使用者自己app塞入地址使用
    private String imageFilterPath = "";

    //androidQ上废弃了DATA绝对路径，需要手动拼凑Uri，这里为了兼容大部分项目还没有适配androidQ的情况
    //默认path还是先取绝对路径，取不到或者异常才去取Uri路径
    public String path;
    //直接拿到Uri路径，在媒体库里，一定会有Uri路径
    private String uriPath;
    // 剪裁后的图片绝对地址（从imageFilterPath 计算出来，已经带了滤镜）
    private String cropUrl;


    //以下是UI上用到的临时变量
    private boolean isSelect = false;
    private boolean isPress = false;
    private int selectIndex = -1;
    private int cropMode = ImageCropMode.ImageScale_FILL;

    private Info cropRestoreInfo;

    public ImageItem() {
    }

    public static ImageItem withPath(Context context, String path) {
        ImageItem imageItem = new ImageItem();
        imageItem.path = path;
        if (imageItem.isUriPath()) {
            Uri uri = Uri.parse(path);
            imageItem.setUriPath(uri.toString());
            imageItem.mimeType = PBitmapUtils.getMimeTypeFromUri((Activity) context, uri);
            if (imageItem.mimeType != null && imageItem.isImage()) {
                imageItem.setVideo(MimeType.isVideo(imageItem.mimeType));
                if(imageItem.isImage()) {
                    int[] size = PBitmapUtils.getImageWidthHeight(context, uri);
                    imageItem.width = size[0];
                    imageItem.height = size[1];
                }
            }
        } else {
            imageItem.mimeType = PBitmapUtils.getMimeTypeFromPath(imageItem.path);
            if (imageItem.mimeType != null) {
                imageItem.setVideo(MimeType.isVideo(imageItem.mimeType));
                Uri uri;
                if (imageItem.isImage()) {
                    uri = PBitmapUtils.getImageContentUri(context, path);
                    int[] size = PBitmapUtils.getImageWidthHeight(path);
                    imageItem.width = size[0];
                    imageItem.height = size[1];
                } else {
                    uri = PBitmapUtils.getVideoContentUri(context, path);
                    imageItem.duration = PBitmapUtils.getLocalVideoDuration(path);
                }
                if (uri != null) {
                    imageItem.setUriPath(uri.toString());
                }
            }
        }

        return imageItem;
    }


    protected ImageItem(Parcel in) {
        id = in.readLong();
        width = in.readInt();
        height = in.readInt();
        time = in.readLong();
        duration = in.readLong();
        mimeType = in.readString();
        timeFormat = in.readString();
        durationFormat = in.readString();
        isVideo = in.readByte() != 0;
        videoImageUri = in.readString();
        imageFilterPath = in.readString();
        path = in.readString();
        uriPath = in.readString();
        cropUrl = in.readString();
        isSelect = in.readByte() != 0;
        isPress = in.readByte() != 0;
        selectIndex = in.readInt();
        cropMode = in.readInt();
        cropRestoreInfo = in.readParcelable(Info.class.getClassLoader());
        isOriginalImage = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(time);
        dest.writeLong(duration);
        dest.writeString(mimeType);
        dest.writeString(timeFormat);
        dest.writeString(durationFormat);
        dest.writeByte((byte) (isVideo ? 1 : 0));
        dest.writeString(videoImageUri);
        dest.writeString(imageFilterPath);
        dest.writeString(path);
        dest.writeString(uriPath);
        dest.writeString(cropUrl);
        dest.writeByte((byte) (isSelect ? 1 : 0));
        dest.writeByte((byte) (isPress ? 1 : 0));
        dest.writeInt(selectIndex);
        dest.writeInt(cropMode);
        dest.writeParcelable(cropRestoreInfo, flags);
        dest.writeByte((byte) (isOriginalImage ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageItem> CREATOR = new Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel in) {
            return new ImageItem(in);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public Info getCropRestoreInfo() {
        return cropRestoreInfo;
    }

    public void setCropRestoreInfo(Info cropRestoreInfo) {
        this.cropRestoreInfo = cropRestoreInfo;
    }

    public String getVideoImageUri() {
        if (videoImageUri == null || videoImageUri.length() == 0) {
            return path;
        }
        return videoImageUri;
    }

    public void setVideoImageUri(String videoImageUri) {
        this.videoImageUri = videoImageUri;
    }

    public String getImageFilterPath() {
        if (imageFilterPath == null || imageFilterPath.length() == 0) {
            return path;
        }
        return imageFilterPath;
    }

    public void setImageFilterPath(String imageFilterPath) {
        this.imageFilterPath = imageFilterPath;
    }

    public boolean isOriginalImage() {
        return isOriginalImage;
    }

    public void setOriginalImage(boolean originalImage) {
        isOriginalImage = originalImage;
    }

    public String getLastImageFilterPath() {
        return imageFilterPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDurationFormat() {
        return durationFormat;
    }

    public void setDurationFormat(String durationFormat) {
        this.durationFormat = durationFormat;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isGif() {
        return MimeType.isGif(mimeType);
    }

    public boolean isLongImage() {
        return getWidthHeightRatio() > 5 || getWidthHeightRatio() < 0.2;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public boolean isImage() {
        return !isVideo;
    }

    public int getCropMode() {
        return cropMode;
    }

    public void setCropMode(int cropMode) {
        this.cropMode = cropMode;
    }

    public String getCropUrl() {
        return cropUrl;
    }

    public void setCropUrl(String cropUrl) {
        this.cropUrl = cropUrl;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public boolean isPress() {
        return isPress;
    }

    public void setPress(boolean press) {
        isPress = press;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isUriPath() {
        return path != null && path.contains("content://");
    }

    public Uri getUri() {
        if (uriPath != null && uriPath.length() > 0) {
            return Uri.parse(uriPath);
        }

        if (isUriPath()) {
            return Uri.parse(path);
        }

        return PBitmapUtils.getContentUri(mimeType, id);
    }


    public float getWidthHeightRatio() {
        if (height == 0) {
            return 1;
        }
        return width * 1.00f / (height * 1.00f);
    }

    /**
     * 获取图片宽高类型，误差0.1
     *
     * @return 1：宽图  -1：高图  0：方图
     */
    public int getWidthHeightType() {
        if (getWidthHeightRatio() > 1.02f) {
            return 1;
        }

        if (getWidthHeightRatio() < 0.98f) {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (path == null) {
            return false;
        }
        try {
            ImageItem other = (ImageItem) o;
            if (other.path == null) {
                return false;
            }
            return this.path.equalsIgnoreCase(other.path);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public ImageItem copy() {
        ImageItem newItem = new ImageItem();
        newItem.path = this.path;
        newItem.isVideo = this.isVideo;
        newItem.duration = this.duration;
        newItem.height = this.height;
        newItem.width = this.width;
        newItem.cropMode = this.cropMode;
        newItem.cropUrl = this.cropUrl;
        newItem.durationFormat = this.durationFormat;
        newItem.id = this.id;
        newItem.isPress = false;
        newItem.isSelect = false;
        newItem.cropRestoreInfo = cropRestoreInfo;
        newItem.isOriginalImage = isOriginalImage;
        return newItem;
    }

    public boolean isOver2KImage() {
        return width > 3000 || height > 3000;
    }

    public boolean isEmpty() {
        return (path == null || path.length() == 0)
                && (uriPath == null || uriPath.length() == 0);
    }

}
