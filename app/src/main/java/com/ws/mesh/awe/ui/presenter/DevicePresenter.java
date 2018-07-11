package com.ws.mesh.awe.ui.presenter;

import android.text.TextUtils;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.db.DeviceDAO;
import com.ws.mesh.awe.ui.impl.IDeviceFragmentView;

public class DevicePresenter{

    private IDeviceFragmentView view;

    public DevicePresenter(IDeviceFragmentView view) {
        this.view = view;
    }

    public void saveDeviceName(Device device, String name){
        if (TextUtils.isEmpty(name)) {
            view.onSaveDeviceNameError(R.string.tip_name_can_not_null);
            return;
        }

        device.mDevName = name;
        //修改并保存名称
        if (saveName(device)) {
            view.onSaveDeviceNameSuccess(device);
        } else {
            view.onSaveDeviceNameError(R.string.tip_edit_failed);
        }
    }

    private boolean saveName(Device device) {
        return DeviceDAO.getInstance().updateDevice(device);
    }
}
