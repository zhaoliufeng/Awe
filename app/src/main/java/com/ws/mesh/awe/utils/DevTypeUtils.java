package com.ws.mesh.awe.utils;

import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.bean.Room;


/**
 * Created by we_smart on 2018/1/31.
 */

public class DevTypeUtils {

    public static final int DEFAULT_DEV_CHANNEL = 0xA0;


    public static int getCtrlGroupType(int meshAddress) {
        if (meshAddress < 0x8000 || meshAddress > 0xFFFF) {
            return DEFAULT_DEV_CHANNEL;
        } else {
            if (meshAddress == 0xFFFF) {
                int devType = -1;
                if (CoreData.core().mDeviceSparseArray.size() <= 50) {
                    for (int x = 0; x < CoreData.core().mDeviceSparseArray.size(); x++) {
                        Device device = CoreData.core().mDeviceSparseArray.valueAt(x);
                        if (devType == -1) {
                            devType = device.mDevType;
                        } else {
                            if (device.mDevType != devType) {
                                devType = 0;
                                break;
                            }
                        }
                    }
                    if (devType == 0 || devType == -1) {
                        return DEFAULT_DEV_CHANNEL;
                    } else {
                        return (devType >> 8) & 0xFF;
                    }
                } else {
                    return DEFAULT_DEV_CHANNEL;
                }

            } else if (meshAddress > 0x8000) {
                Room room = CoreData.core().mRoomSparseArray.get(meshAddress);
                if (room != null) {
                    if (room.mDeviceIds != null && room.mDeviceIds.size() != 0) {
                        int devType = -1;
                        for (int x = 0; x < room.mDeviceIds.size(); x++) {
                            Device device = CoreData.core().mDeviceSparseArray.
                                    get(room.mDeviceIds.valueAt(x));
                            if (device != null) {
                                if (devType == -1) {
                                    devType = device.mDevType;
                                } else {
                                    if (device.mDevType != devType) {
                                        devType = 0;
                                        break;
                                    }
                                }
                            }
                        }

                        if (devType == 0 || devType == -1) {
                            return DEFAULT_DEV_CHANNEL;
                        } else {
                            return (devType >> 8) & 0xFF;
                        }
                    } else {
                        return DEFAULT_DEV_CHANNEL;
                    }
                } else {
                    return DEFAULT_DEV_CHANNEL;
                }
            } else {
                return DEFAULT_DEV_CHANNEL;
            }
        }
    }

    public static int getCtrlDevType(int meshAddress) {
        return ((CoreData.core().mDeviceSparseArray.get(meshAddress).mDevType >> 8) & 0xFF);
    }
}
