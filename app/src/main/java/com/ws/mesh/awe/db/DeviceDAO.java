package com.ws.mesh.awe.db;

import android.util.SparseArray;

import com.we_smart.sqldao.BaseDAO;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.utils.CoreData;

import java.util.List;

public class DeviceDAO extends BaseDAO<Device>{
    private DeviceDAO() {
        super(Device.class);
    }

    private static DeviceDAO dao;
    public static DeviceDAO getInstance(){
        if (dao == null){
            synchronized (DeviceDAO.class){
                if (dao == null){
                    dao = new DeviceDAO();
                }
            }
        }
        return dao;
    }

    public boolean insertDevice(Device device){
        device.mParentMeshName = CoreData.core().getCurrMesh().mMeshName;
        return insert(device);
    }

    public boolean deleteDevice(Device device){
        return delete(device, "mParentMeshName", "mDevMeshId");
    }

    public SparseArray<Device> queryDevice(){
        List<Device> devices = query(new String[]{CoreData.core().getCurrMesh().mMeshName}, "mParentMeshName");
        SparseArray<Device> deviceSparseArray = new SparseArray<>();
        for (Device device : devices){
            deviceSparseArray.append(device.mDevMeshId, device);
        }
        return deviceSparseArray;
    }

    public boolean updateDevice(Device device){
        return update(device, "mParentMeshName", "mDevMeshId");
    }
}
