package com.ypx.imagepicker.activity.preview;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ypx.imagepicker.R;
import com.ypx.imagepicker.activity.multi.MultiImagePreviewActivity;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.utils.PViewSizeUtils;
import com.ypx.imagepicker.utils.PickerFileProvider;
import com.ypx.imagepicker.widget.cropimage.CropImageView;

import java.io.File;
import java.util.Objects;

/**
 * Time: 2019/11/1 16:20
 * Author:ypx
 * Description:单图预览
 */
public class SinglePreviewFragment extends Fragment {
    public static final String KEY_URL = "key_url";
    private RelativeLayout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        final ImageItem imageItem = (ImageItem) bundle.getSerializable(KEY_URL);
        if (imageItem == null) {
            return;
        }
        String url = imageItem.path;
        layout = new RelativeLayout(getContext());
        CropImageView imageView = new CropImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundColor(0xff000000);
        // 启用图片缩放功能
        imageView.enable();
        imageView.setShowImageRectLine(false);
        imageView.setCanShowTouchLine(false);
        imageView.setMaxScale(7.0f);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        layout.setLayoutParams(params);
        layout.addView(imageView);

        ImageView mVideoImg = new ImageView(getContext());
        mVideoImg.setImageDrawable(getResources().getDrawable(R.mipmap.picker_icon_video));
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(PViewSizeUtils.dp(getContext(), 80), PViewSizeUtils.dp(getContext(), 80));
        mVideoImg.setLayoutParams(params1);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(mVideoImg, params1);

        if (imageItem.isVideo()) {
            mVideoImg.setVisibility(View.VISIBLE);
        } else {
            mVideoImg.setVisibility(View.GONE);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageItem.isVideo()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(imageItem.path);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = PickerFileProvider.getUriForFile(v.getContext(),
                                Objects.requireNonNull(getActivity())
                                        .getApplication().getPackageName() + ".picker.fileprovider", file);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } else {
                        uri = Uri.fromFile(file);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                    return;
                }
                if (getActivity() instanceof MediaPreviewActivity) {
                    ((MediaPreviewActivity) getActivity()).onImageSingleTap();
                } else if (getActivity() instanceof MultiImagePreviewActivity) {
                    ((MultiImagePreviewActivity) getActivity()).onImageSingleTap();
                }
            }
        });
        if (getActivity() instanceof MediaPreviewActivity) {
            ((MediaPreviewActivity) getActivity()).getImgLoader().displayPerViewImage(imageView, url);
        } else if (getActivity() instanceof MultiImagePreviewActivity) {
            ((MultiImagePreviewActivity) getActivity()).getImgLoader().displayPerViewImage(imageView, url);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return layout;
    }
}
