package com.ypx.imagepicker.helper.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * Activity跳转封装类，把OnActivityResult方式改为Callback方式
 * <p>
 * Created by XiaoFeng on 2018/9/5.
 */
public class PLauncher {

    private static final String TAG = "PLauncher";
    private Context mContext;
    /**
     * V4兼容包下的Fragment
     */
    private PRouterV4 mRouterFragmentV4;
    /**
     * 标准SDK下的Fragment
     */
    private PRouter mRouterFragment;

    public static PLauncher init(Fragment fragment) {
        return init(fragment.getActivity());
    }

    public static PLauncher init(FragmentActivity activity) {
        return new PLauncher(activity);
    }

    public static PLauncher init(Activity activity) {
        return new PLauncher(activity);
    }

    private PLauncher(FragmentActivity activity) {
        mContext = activity;
        mRouterFragmentV4 = getRouterFragmentV4(activity);
    }

    private PLauncher(Activity activity) {
        mContext = activity;
        mRouterFragment = getRouterFragment(activity);
    }

    private PRouterV4 getRouterFragmentV4(FragmentActivity activity) {
        PRouterV4 routerFragment = findRouterFragmentV4(activity);
        if (routerFragment == null) {
            routerFragment = PRouterV4.newInstance();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(routerFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return routerFragment;
    }

    private PRouterV4 findRouterFragmentV4(FragmentActivity activity) {
        return (PRouterV4) activity.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    private PRouter getRouterFragment(Activity activity) {
        PRouter routerFragment = findRouterFragment(activity);
        if (routerFragment == null) {
            routerFragment = PRouter.newInstance();
            android.app.FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(routerFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return routerFragment;
    }

    private PRouter findRouterFragment(Activity activity) {
        return (PRouter) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public void startActivityForResult(Class<?> clazz, Callback callback) {
        Intent intent = new Intent(mContext, clazz);
        startActivityForResult(intent, callback);
    }

    public void startActivityForResult(Intent intent, Callback callback) {
        if (mRouterFragmentV4 != null) {
            mRouterFragmentV4.startActivityForResult(intent, callback);
        } else if (mRouterFragment != null) {
            mRouterFragment.startActivityForResult(intent, callback);
        } else {
            throw new RuntimeException("please do init first!");
        }
    }

    public interface Callback {
        void onActivityResult(int resultCode, Intent data);
    }
}
