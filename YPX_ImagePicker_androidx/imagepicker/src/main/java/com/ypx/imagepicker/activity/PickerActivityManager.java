package com.ypx.imagepicker.activity;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Time: 2019/11/6 17:09
 * Author:ypx
 * Description: 自定义activity栈
 */
public class PickerActivityManager {

    private static List<WeakReference<Activity>> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(activityWeakReference);
    }

    public static void removeActivity(Activity activity) {
        if (activities == null || activities.size() == 0) {
            return;
        }
        WeakReference<Activity> activityWeakReference = null;
        for (WeakReference<Activity> activityWeakReference1 : activities) {
            if (activityWeakReference1 != null
                    && activityWeakReference1.get() != null
                    && activityWeakReference1.get() == activity) {
                activityWeakReference = activityWeakReference1;
                break;
            }
        }
        if (activityWeakReference != null) {
            activities.remove(activityWeakReference);
        }
    }

    public static Activity getLastActivity() {
        if (activities != null && activities.size() > 0) {
            WeakReference<Activity> activityWeakReference = activities.get(activities.size() - 1);
            if (activityWeakReference != null) {
                return activityWeakReference.get();
            }
        }
        return null;
    }

    public static void clear() {
        if (activities != null && activities.size() > 0) {
            for (WeakReference<Activity> activityWeakReference : activities) {
                if (activityWeakReference.get() != null && !activityWeakReference.get().isDestroyed()) {
                    activityWeakReference.get().finish();
                }
            }
            activities.clear();
            activities = null;
        }
    }
}
