package com.ypx.imagepicker.helper.launcher;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import java.util.Random;

/**
 * 把OnActivityResult方式转换为Callback方式的空Fragment（标准SDK）
 *
 * Created by XiaoFeng on 2018/9/5.
 */
public class PRouter extends Fragment {

    private SparseArray<PLauncher.Callback> mCallbacks = new SparseArray<>();
    private Random mCodeGenerator = new Random();

    public PRouter() {
        // Required empty public constructor
    }

    public static PRouter newInstance() {
        return new PRouter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startActivityForResult(Intent intent, PLauncher.Callback callback) {
        int requestCode = makeRequestCode();
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 随机生成唯一的requestCode，最多尝试10次
     *
     * @return
     */
    private int makeRequestCode() {
        int requestCode;
        int tryCount = 0;
        do {
            requestCode = mCodeGenerator.nextInt(0x0000FFFF);
            tryCount++;
        } while (mCallbacks.indexOfKey(requestCode) >= 0 && tryCount < 10);
        return requestCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PLauncher.Callback callback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(resultCode, data);
        }
    }
}
