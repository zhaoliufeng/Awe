package com.ws.mesh.awe.constant;

/**
 * Created by zhaol on 2017/11/21.
 */

public class DeviceChannel {
    /*
       * A类功能性通道
       * */
    //RGBWC五路
    public final static int LIGHT_FIVE_RGBWC_CHANNEL = 0xA0;
    //RGBW
    public final static int LIGHT_FOUR_RGBW_CHANNEL = 0xA1;
    //RGBC
    public final static int LIGHT_FOUR_RGBC_CHANNEL = 0xA2;
    //RGB
    public final static int LIGHT_THREE_RGB_CHANNEL = 0xA3;
    //WC两路设备
    public final static int LIGHT_WC_TWO_CHANNEL = 0xA4;
    //W
    public final static int LIGHT_ONE_W_CHANNEL = 0xA5;
    //C
    public final static int LIGHT_ONE_C_CHANNEL = 0xA6;

    //强电开关
    public final static int DEVICE_SWITCH = 0xA7;
    //强电插排
    public final static int DEVICE_SOCKET = 0xA8;
    //一路强电开关
    public final static int CONTROL_SWITCH = 0xA701;
    //二路强电开关
    public final static int CONTROL_TWO_SWITCH = 0xA702;
    //三路强电开关
    public final static int CONTROL_THREE_SWITCH = 0xA703;
    //一路强电插排
    public final static int CONTROL_SOCKET = 0xA801;
    //二路强电插排
    public final static int CONTROL_TWO_SOCKET = 0xA802;
    //三路强电插排
    public final static int CONTROL_THREE_SOCKET = 0xA803;
    /*
     * B类传感器通道
     * */
    //B类传感器设备
    public final static int SENSOR_DEVICE = 0xB0;

    /*
     * C类非功能性设备
     * */
    //遥控器
    public final static int DEVICE_REMOTE_CONTROL = 0xC0;
    //网关
    public final static int DEVICE_MESH_GATEWAY = 0XC1;
}
