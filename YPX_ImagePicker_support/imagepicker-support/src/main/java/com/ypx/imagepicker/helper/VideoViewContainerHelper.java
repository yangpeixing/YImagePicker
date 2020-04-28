package com.ypx.imagepicker.helper;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.presenter.IPickerPresenter;

/**
 * Time: 2019/9/30 9:45
 * Author:ypx
 * Description: 视频播放
 */
public class VideoViewContainerHelper {
    private VideoView videoView;
    private ImageView previewImg;
    private ImageView pauseImg;

    public void loadVideoView(ViewGroup parent, ImageItem imageItem, IPickerPresenter presenter, PickerUiConfig uiConfig) {
        Context context = parent.getContext();

        if (videoView == null) {
            videoView = new VideoView(context);
            videoView.setBackgroundColor(Color.TRANSPARENT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            videoView.setLayoutParams(params);

            previewImg = new ImageView(context);
            previewImg.setLayoutParams(params);
            previewImg.setScaleType(ImageView.ScaleType.FIT_CENTER);

            pauseImg = new ImageView(context);
            pauseImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            pauseImg.setImageDrawable(context.getResources().getDrawable(uiConfig.getVideoPauseIconID()));
            FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.gravity = Gravity.CENTER;
            pauseImg.setLayoutParams(params2);
        }
        pauseImg.setVisibility(View.GONE);
        parent.removeAllViews();
        parent.addView(videoView);
        parent.addView(previewImg);
        parent.addView(pauseImg);
        previewImg.setVisibility(View.VISIBLE);
        presenter.displayImage(previewImg, imageItem, 0, false);
        videoView.setVideoPath(imageItem.path);
        videoView.start();
        //监听视频播放完的代码
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mPlayer) {
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    onPause();
                } else {
                    onResume();
                }
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        videoView.start();
                    }
                });
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            // video 视屏播放的时候把背景设置为透明
                            videoView.setBackgroundColor(Color.TRANSPARENT);
                            previewImg.setVisibility(View.GONE);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    public void onResume() {
        if (videoView != null && pauseImg != null) {
            videoView.start();
            videoView.seekTo(videoView.getCurrentPosition());
            pauseImg.setVisibility(View.GONE);
        }
    }

    public void onPause() {
        if (videoView != null && pauseImg != null) {
            videoView.pause();
            pauseImg.setVisibility(View.VISIBLE);
        }
    }

    public void onDestroy() {
        if (videoView != null) {
            videoView.suspend();//将VideoView所占用的资源释放掉
        }
    }
}
