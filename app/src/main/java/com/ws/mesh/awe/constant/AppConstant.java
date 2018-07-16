package com.ws.mesh.awe.constant;

public class AppConstant {
    public static final String SQL_NAME = "breath_sql";

    //初始网络
    public static final String MESH_DEFAULT_NAME = "Fulife";
    public static final String MESH_DEFAULT_PASSWORD = "2846";
    public static final String DEVICE_DEFAULT_NAME = "Device";

    public static final int ALARM_TYPE_DEVICE = 1;
    public static final int ALARM_TYPE_GROUP = 2;
    public static final String ID = "_id";
    public static final int DEFAULT_TYPE = 0xA0FF;

    public static final long DAY_TIME = 1000L * 24 * 60 * 60;

    public static final byte SCENE_ALARM = (byte) 0x92;
    //关闭闹钟
    public static final byte OFF_ALARM = (byte) 0x90;
    //开启闹钟
    public static final byte ON_ALARM = (byte) 0x91;
    //日模式的場景鬧鐘
    public static final byte DAY_SCENE_ALARM = (byte) 0x82;
    //日模式的关闭鬧鐘
    public static final byte DAY_OFF_ALARM = (byte) 0x80;
    //日模式的打开鬧鐘
    public static final byte DAY_ON_ALARM = (byte) 0x81;

    //起始房间id
    public static final int ROOM_START_ID = 0x8011;
    //末尾房间id
    public static final int ROOM_LAST_ID = 0x80D9;

    //网关设备
    public static final int OTHER_DEVICE_GATEWAY = 0xC100;

    public static final long MIN_SEND_GAP = 120;
    public static final int MIN_BRIGHTNESS = 5;
}
