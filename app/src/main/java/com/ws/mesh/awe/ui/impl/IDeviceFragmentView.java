package com.ws.mesh.awe.ui.impl;

import com.ws.mesh.awe.bean.Device;

public interface IDeviceFragmentView {
    void onSaveDeviceNameSuccess(Device device);
    void onSaveDeviceNameError(int errMsgId);
}
