package com.ws.mesh.awe.utils;


import com.ws.mesh.awe.constant.AppConstant;

/**
 * Created by we_smart on 2017/11/23.
 */

public class Gap {

    private long mCurrentTime;

    public synchronized boolean isPassNext() {
        if (System.currentTimeMillis() - mCurrentTime >= AppConstant.MIN_SEND_GAP) {
            mCurrentTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
}
