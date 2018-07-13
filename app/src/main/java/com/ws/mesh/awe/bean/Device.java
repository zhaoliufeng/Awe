package com.ws.mesh.awe.bean;

import com.telink.bluetooth.light.ConnectionStatus;
import com.we_smart.sqldao.Annotation.DBFiled;

/**
 * Created by we_smart on 2017/11/15.
 */

public class Device {

    //所属网络名称
    @DBFiled
    public String mParentMeshName;

    //设备名称
    @DBFiled
    public String mDevName;

    //设备的MAC地址
    @DBFiled
    public String mDevMacAddress;

    //设备的meshId
    @DBFiled
    public int mDevMeshId;

    //设备类型
    @DBFiled
    public int mDevType;

    //设备通道1
    @DBFiled
    public String mChannelOne;

    //设备通道2
    @DBFiled
    public String mChannelTwo;

    //设备通道3
    @DBFiled
    public String mChannelThree;

    //设备状态
    public ConnectionStatus mConnectionStatus;

    //固件版本
    @DBFiled
    public String mFirmwareVersion;


    @Override
    public String toString() {
        return "Device{" +
                "mDevName='" + mDevName + '\'' +
                ", mDevMacAddress='" + mDevMacAddress + '\'' +
                ", mDevMeshId=" + mDevMeshId +
                ", mChannelOne='" + mChannelOne + '\'' +
                ", mChannelTwo='" + mChannelTwo + '\'' +
                ", mChannelThree='" + mChannelThree + '\''+
                '}';
    }
}
