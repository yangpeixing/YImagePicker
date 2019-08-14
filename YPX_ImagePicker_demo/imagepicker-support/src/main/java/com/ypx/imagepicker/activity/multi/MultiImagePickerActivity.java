package com.ypx.imagepicker.activity.multi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.R;
import com.ypx.imagepicker.adapter.multi.MultiGridAdapter;
import com.ypx.imagepicker.adapter.multi.MultiSetAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.ImageSelectMode;
import com.ypx.imagepicker.bean.ImageSet;
import com.ypx.imagepicker.bean.PickerSelectConfig;
import com.ypx.imagepicker.bean.PickerUiConfig;
import com.ypx.imagepicker.data.MultiPickerData;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.data.OnImagesLoadedListener;
import com.ypx.imagepicker.data.impl.MediaDataSource;
import com.ypx.imagepicker.helper.launcher.ActivityLauncher;
import com.ypx.imagepicker.presenter.IMultiPickerBindPresenter;
import com.ypx.imagepicker.utils.PermissionUtils;
import com.ypx.imagepicker.utils.StatusBarUtil;
import com.ypx.imagepicker.utils.TakePhotoUtil;
import com.ypx.imagepicker.utils.ViewSizeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.ypx.imagepicker.activity.crop.ImagePickAndCropActivity.REQ_STORAGE;

/**
 * Description: 多选页
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class MultiImagePickerActivity extends FragmentActivity {

    public static final String INTENT_KEY_SELECT_CONFIG = "PickerSelectConfig";
    public static final String INTENT_KEY_UI_CONFIG = "IMultiPickerBindPresenter";
    public static final String INTENT_KEY_CURRENT_INDEX = "currentIndex";
    public static final String INTENT_KEY_CURRENT_IMAGE = "currentImage";
    public static final int REQ_CAMERA = 1431;

    public static void intent(Activity activity,
                              PickerSelectConfig selectConfig,
                              IMultiPickerBindPresenter presenter,
                              final OnImagePickCompleteListener listener) {
        Intent intent = new Intent(activity, MultiImagePickerActivity.class);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_SELECT_CONFIG, selectConfig);
        intent.putExtra(MultiImagePickerActivity.INTENT_KEY_UI_CONFIG, presenter);
        ActivityLauncher.init(activity).startActivityForResult(intent, new ActivityLauncher.Callback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                if (resultCode == ImagePicker.REQ_PICKER_RESULT_CODE &&
                        data.hasExtra(ImagePicker.INTENT_KEY_PICKERRESULT) && listener != null) {
                    ArrayList list = (ArrayList) data.getSerializableExtra(ImagePicker.INTENT_KEY_PICKERRESULT);
                    listener.onImagePickComplete(list);
                    MultiPickerData.instance.clear();
                }
            }
        });
    }

    private MultiImagePickerFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_activity_fragment_wrapper);
        if (getIntent() == null || !getIntent().hasExtra(INTENT_KEY_SELECT_CONFIG)
                || !getIntent().hasExtra(INTENT_KEY_UI_CONFIG)) {
            finish();
            return;
        }
        PickerSelectConfig selectConfig = (PickerSelectConfig) getIntent().getSerializableExtra(INTENT_KEY_SELECT_CONFIG);
        IMultiPickerBindPresenter presenter = (IMultiPickerBindPresenter) getIntent().getSerializableExtra(INTENT_KEY_UI_CONFIG);
        if (selectConfig == null || presenter == null) {
            finish();
            return;
        }
        fragment = ImagePicker.withMultiFragment(presenter)
                .setPickerSelectConfig(selectConfig)
                .pickWithFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.isImageSetShow()) {
            return;
        }
        super.onBackPressed();
        MultiPickerData.instance.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onTakePhotoResult(requestCode, resultCode);
        }
    }
}
