package com.ws.mesh.awe.constant;

/**
 * Created by zhaol on 2017/11/21.
 */

public class DeviceForm {
    //灯带
    public static final int FORM_LIGHT_LIGHTSWITH = 0x00;
    //吊灯FORM_
    public static final int FORM_LIGHT_DROPLIGHT = 0x01;
    //轨道灯FORM_
    public static final int FORM_LIGHT_TRACK_LIGHTING = 0x02;
    //落地灯FORM_
    public static final int FORM_LIGHT_FLOOR_LAMP = 0x03;
    //面板灯FORM_
    public static final int FORM_LIGHT_PANEL_LIGHT = 0x04;
    //台灯FORM_
    public static final int FORM_LIGHT_TABLE_LAMP = 0x05;
    //天花灯FORM_
    public static final int FORM_LIGHT_CEILING_LAMP = 0x06;
    //吸顶灯FORM_
    public static final int FORM_LIGHT_VLZ_B = 0x07;
    //A型球泡灯FORM_
    public static final int FORM_LIGHT_A_BULB = 0x08;
    //B型蜡烛灯FORM_
    public static final int FORM_LIGHT_B_CANDLE_LIGHT = 0x09;
    //BR-灯FORM_
    public static final int FORM_LIGHT_BR_LIGHT = 0x0A;
    //G型球泡灯FORM_
    public static final int FORM_LIGHT_G_BULB = 0x0B;
    //G10射灯FORM_
    public static final int FORM_LIGHT_GU10_LED_SPOT_LIGHT = 0x0C;
    //MR16射灯FORM_
    public static final int FORM_LIGHT_MR16_LED_SPOT_LIGHT = 0x0D;
    //情景灯FORM_
    public static final int FORM_LIGHT_SCENE_LAMP = 0x10;
    //PAR灯FORM_
    public static final int FORM_LIGHT_PAR_LIGHT = 0x0E;
    //T5-T8灯管FORM_
    public static final int FORM_LIGHT_T5_T8_TUBE = 0x20;
    //T5-T8一体灯FORM_
    public static final int FORM_LIGHT_T5_T8_INTEGRATION = 0x21;
    //筒灯FORM_
    public static final int FORM_LIGHT_CANISTER_LAMP = 0x22;
    //两路筒灯FORM_
    public static final int FORM_LIGHT_TWO_CANISTER_LIGHT = 0x22;
    //风雨传感器
    public static final int FORM_SENSOR_WIND_AND_RAIN = 0x00;
    //煤气传感器FORM_
    public static final int FORM_SENSOR_GAS = 0x01;
    //门磁传感器FORM_
    public static final int FORM_SENSOR_DOOR_MAGNETIC = 0x02;
    //人体红外线传感器FORM_
    public static final int FORM_SENSOR_INFRARED_BODY = 0x03;
    //水浸传感器FORM_
    public static final int FORM_SENSOR_WATER_OUT = 0x04;
    //温度传感器FORM_
    public static final int FORM_SENSOR_TEMPERATURE = 0x05;
    //烟雾传感器FORM_
    public static final int FORM_SENSOR_SMOKE = 0x06;
    //照度传感器FORM_
    public static final int FORM_SENSOR_ILLUMINATION = 0x07;
    //斗胆灯
    public static final int FORM_GIMBAL_SPOT_LIGHT = 0x11;
    //异型灯
    public static final int LIGHT_SPECIAL_LAMP = 0x12;

    /*
   * 遥控器
   * */
    //手持遥控器
    public static final int HAND_CONTROL = 0xC000;

    /*
    * 开关
    * */
    //面板开关
    public static final int SWITCH_PANEL = 0xC001;

    /*
    * 钥匙扣开关
    * */
    public static final int KEY_SWITCH = 0xC002;

    //网关设备
    public static final int OTHER_DEVICE_GATEWAY = 0xC100;

    public static final int FORM_DEFAULTTYPE = 0xFF;
}
