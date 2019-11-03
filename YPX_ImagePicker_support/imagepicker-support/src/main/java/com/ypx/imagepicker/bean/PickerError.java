package com.ypx.imagepicker.bean;

/**
 * Time: 2019/10/18 9:18
 * Author:ypx
 * Description: 选择器调用失败的回调
 */
public enum PickerError {

    CANCEL(-26883, "pick cancel"),
    MEDIA_NOT_FOUND(-26884, "not found media files"),
    PRESENTER_NOT_FOUND(-26885, "not found presenter,you must be implements IMultiPickerBindPresenter or ICropPickerBindPresenter"),
    UI_CONFIG_NOT_FOUND(-26886, "presenter not found uiConfig,please check IMultiPickerBindPresenter or ICropPickerBindPresenter's getUiConfig() method realize"),
    SELECT_CONFIG_NOT_FOUND(-26887, "not found selectConfig or cropConfig"),
    CROP_URL_NOT_FOUND(-26888, "not found imagePath to crop"),
    CROP_EXCEPTION(-26889, "crop exception"),
    TAKE_PHOTO_FAILED(-268890, "takePhoto failed"),
    MIMETYPES_EMPTY(-268892, "mimeTypes size is 0"),
    OTHER(-26891, "other error");


    private int mCode = 0;
    private String mMessage = "";

    PickerError(int code, String msg) {
        mCode = code;
        mMessage = msg;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public static PickerError valueOf(int code) {
        if (code == CANCEL.getCode()) {
            return CANCEL;
        } else if (code == PRESENTER_NOT_FOUND.getCode()) {
            return PRESENTER_NOT_FOUND;
        } else if (code == UI_CONFIG_NOT_FOUND.getCode()) {
            return UI_CONFIG_NOT_FOUND;
        } else if (code == SELECT_CONFIG_NOT_FOUND.getCode()) {
            return SELECT_CONFIG_NOT_FOUND;
        } else if (code == MEDIA_NOT_FOUND.getCode()) {
            return MEDIA_NOT_FOUND;
        }
        return OTHER;
    }

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
