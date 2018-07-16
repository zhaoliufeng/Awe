package com.ws.mesh.awe.ui.impl;

import android.util.SparseArray;

import com.ws.mesh.awe.bean.Device;

public interface IScanDeviceView {
    void addDeviceSuccess(SparseArray<Device> sparseArray);

    void addDeviceFinish(int num);

    void addDeviceStatus(int status);

    void onBLEError();
}
