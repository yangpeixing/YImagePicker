package com.ypx.imagepickerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.ypx.imagepicker.ImagePicker;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.MimeType;
import com.ypx.imagepicker.bean.SelectMode;
import com.ypx.imagepicker.data.OnImagePickCompleteListener;
import com.ypx.imagepicker.utils.PStatusBarUtil;
import com.ypx.imagepickerdemo.style.RedBookPresenter;
import com.ypx.imagepickerdemo.style.WeChatPresenter;

import java.util.ArrayList;

/**
 * Time: 2019/11/6 18:24
 * Author:ypx
 * Description:
 */
public class SecondActivity extends Activity {
    ArrayList<ImageItem> imageItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PStatusBarUtil.fullScreen(this);
        setContentView(R.layout.activity_second);
        imageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.INTENT_KEY_PICKER_RESULT);
        ImagesViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setImageViewList(imageItems);


        ImagePicker.withMulti(new WeChatPresenter())//指定presenter
                .setMaxCount(9)//设置选择的最大数
                .setColumnCount(4)//设置列数
                .mimeTypes(MimeType.ofAll())//设置要加载的文件类型，可指定单一类型
                .filterMimeTypes(MimeType.GIF)//设置需要过滤掉加载的文件类型
                .showCamera(true)//显示拍照
                .setPreview(true)//开启预览
                .setPreviewVideo(true) //大图预览时是否支持预览视频
                .setVideoSinglePick(true)//设置视频单选
                .setSinglePickImageOrVideoType(true)//设置图片和视频单一类型选择
                .setSinglePickWithAutoComplete(false)//当单选或者视频单选时，点击item直接回调，无需点击完成按钮
                .setOriginal(true)  //显示原图
                .setSelectMode(SelectMode.MODE_SINGLE)   //设置单选模，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
                .setMaxVideoDuration(2000L)//设置视频可选取的最大时长
                .setMinVideoDuration(60000L)//设置视频可选取的最小时长
                .setLastImageList(null)//设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
                .setShieldList(null)//设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
                .pick(this, new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //图片选择回调，主线程
                    }
                });

        ImagePicker.preview(this,new WeChatPresenter(),);
    }

    public void click(View view) {
        ImagePicker.closePickerWithCallback(imageItems);
        finish();
    }
}
