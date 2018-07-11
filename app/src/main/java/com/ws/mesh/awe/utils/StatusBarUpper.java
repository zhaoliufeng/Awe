package com.ws.mesh.awe.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ws.mesh.awe.base.BaseActivity;


/**
 * Created by zhaol on 2017/11/17.
 */

public class StatusBarUpper {

    public static void setStatusBarUpper(BaseActivity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            setStatusBarUpperAPI21(activity);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            setStatusBarUpperAPI19(activity);
        }
    }

    private static void setStatusBarUpperAPI21(BaseActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)return;

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
        }
    }

    private static void setStatusBarUpperAPI19(BaseActivity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        View statusBarView = mContentView.getChildAt(0);
        if (statusBarView != null && statusBarView.getLayoutParams() != null &&
                statusBarView.getLayoutParams().height == getStatusBarHeight(activity)) {
            mContentView.removeView(statusBarView);
        }
        //不预留空间
        if (mContentView.getChildAt(0) != null) {
            ViewCompat.setFitsSystemWindows(mContentView.getChildAt(0), false);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
