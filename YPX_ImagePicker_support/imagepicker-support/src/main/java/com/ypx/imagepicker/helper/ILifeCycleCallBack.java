package com.ypx.imagepicker.helper;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

public interface ILifeCycleCallBack extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();
}
