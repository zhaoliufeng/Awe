package com.ws.mesh.awe.utils;

import com.telink.bluetooth.light.Opcode;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.service.TelinkLightService;

import java.util.Calendar;


/**
 * 消息发送
 */

public class SendMsg {

    private static void sendCommonMsg(int meshAddress, byte opCode, byte[] params) {
        if (TelinkLightService.Instance() != null && TelinkLightService.Instance().isLogin()) {
            TelinkLightService.Instance().sendCommandNoResponse(opCode, meshAddress, params);
        }
    }

    public static void updateDeviceTime() {
        Calendar calendar = Calendar.getInstance();
        byte[] params = new byte[]{(byte) ((calendar.get(Calendar.YEAR) >> 0) & 0xFF),
                (byte) ((calendar.get(Calendar.YEAR) >> 8) & 0xFF), (byte) (calendar.get(Calendar.MONTH) + 1),
                (byte) calendar.get(Calendar.DAY_OF_MONTH), (byte) calendar.get(Calendar.HOUR_OF_DAY),
                (byte) calendar.get(Calendar.MINUTE), (byte) calendar.get(Calendar.SECOND)};
        sendCommonMsg(0xFFFF, Opcode.BLE_GATT_OP_CTRL_E4.getValue(), params);
    }


    //获取设备类型
    public static void getDeviceType(int meshAddress) {
        sendCommonMsg(meshAddress, Opcode.BLE_GATT_OP_CTRL_EA.getValue(), new byte[]{0x10});
    }

    //设备定位
    public static void locationDevice(int meshAddress) {
        sendCommonMsg(meshAddress, Opcode.BLE_GATT_OP_CTRL_D0.getValue(), new byte[]{0x03, 0x00, 0x00});
    }

    //开关灯
    public static void switchDevice(int meshAddress, boolean isOpen) {
        byte switchParams[] = new byte[]{(byte) (isOpen ? 0x01 : 0x00), 0x00, 0x00};
        sendCommonMsg(meshAddress, Opcode.BLE_GATT_OP_CTRL_D0.getValue(), switchParams);
    }

    public static void sendBreath(int meshAddress, byte opcode, byte[] params) {
        TelinkLightService.Instance().sendCommandNoResponse(opcode, meshAddress, params);
    }

    public static void sendCustomBreath(final int meshAddress, byte[] params) {
        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xFD, meshAddress, params);
    }

    public static void addAlarm(int meshAddress, Timing alarmBean) {
        if (null == alarmBean) return;
        int mode = alarmBean.mAlarmEvent > 1 ? 2 : alarmBean.mAlarmEvent;
        byte byte13;
        if (alarmBean.mWeekNum == 0) {
            if (mode == 0) {
                byte13 = AppConstant.DAY_OFF_ALARM;
            } else if (mode == 1) {
                byte13 = AppConstant.DAY_ON_ALARM;
            } else {
                byte13 = AppConstant.DAY_SCENE_ALARM;
            }
        } else {
            if (mode == 0) {
                byte13 = AppConstant.OFF_ALARM;
            } else if (mode == 1) {
                byte13 = AppConstant.ON_ALARM;
            } else {
                byte13 = AppConstant.SCENE_ALARM;
            }
        }
        int months = 0, day = 0;
        if (alarmBean.mWeekNum == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(alarmBean.mUtcTime);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            months = calendar.get(Calendar.MONTH) + 1;
        }
        sendCommonMsg(meshAddress, Opcode.BLE_GATT_OP_CTRL_E5.getValue(), new byte[]{0x02,
                (byte) alarmBean.mAId, (byte) (byte13 & 0xFF), (byte) (alarmBean.mWeekNum == 0 ? months : 0),
                (byte) (alarmBean.mWeekNum == 0 ? day : alarmBean.mWeekNum), (byte) alarmBean.mHours,
                (byte) alarmBean.mMins, (byte) 0, (byte) (mode == 2 ? (0xF0 + alarmBean.mAlarmEvent - 1) : 0), 0
        });
    }

}


