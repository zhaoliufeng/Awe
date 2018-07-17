package com.ws.mesh.awe.ui.presenter;

import android.text.TextUtils;
import android.util.SparseArray;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.db.DeviceDAO;
import com.ws.mesh.awe.db.RoomDAO;
import com.ws.mesh.awe.db.TimingDAO;
import com.ws.mesh.awe.ui.impl.IDeviceFragmentView;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;

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

    public void kickOutDevice(Device device){
        if (DeviceDAO.getInstance().deleteDevice(device)){
            CoreData.core().mDeviceSparseArray.remove(device.mDevMeshId);
            SendMsg.kickOut(device.mDevMeshId);

            //删除有关设备的房间的设备id
            for (int i = 0; i < CoreData.core().mRoomSparseArray.size(); i++){
                Room room = CoreData.core().mRoomSparseArray.valueAt(i);
                if (room.mDeviceIds.get(device.mDevMeshId) != null){
                    room.mDeviceIds.remove(device.mDevMeshId);
                    RoomDAO.getInstance().updateRoom(room);
                }
            }

            //删除有关设备的定时
            SparseArray<Timing> timingSparseArray = TimingDAO.getInstance().queryTiming(device.mDevMeshId);
            for (int i = 0; i < timingSparseArray.size(); i++){
                TimingDAO.getInstance().deleteTiming(timingSparseArray.valueAt(i));
            }

            view.onRemoveDevice(device);
        }else {
            view.onError(R.string.remove_failed);
        }

    }
}
