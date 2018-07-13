package com.ws.mesh.awe.utils;

import android.content.Context;

/**
 * 视图工具集合
 *  Created by zhaol on 2018/4/23.
 */

public class ViewUtils {
    public static double dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static double dp2px(Context context, double dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
