package com.ws.mesh.awe.ui.impl;

import android.util.SparseArray;

import com.ws.mesh.awe.bean.Device;

public interface IMainView {
    //找到设备
    void onFindDevice();

    //登陆成功
    void onLoginSuccess();

    //设备下线
    void offline(Device device);

    //设备上线
    void online(SparseArray<Device> sparseArray);

    //状态更新
    void statusUpdate(Device device);

    //断开连接
    void onLoginOut();

    //更新设备
    void updateDevice(SparseArray<Device> deviceSparseArray);
}
