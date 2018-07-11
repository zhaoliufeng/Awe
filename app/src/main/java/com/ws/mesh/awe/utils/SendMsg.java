package com.ws.mesh.awe.utils;

import com.telink.bluetooth.light.Opcode;
import com.ws.mesh.awe.service.TelinkLightService;


/**
 * 消息发送
 */

public class SendMsg {

    private static void sendCommonMsg(int meshAddress, byte opCode, byte[] params) {
        if (TelinkLightService.Instance() != null && TelinkLightService.Instance().isLogin()) {
            TelinkLightService.Instance().sendCommandNoResponse(opCode, meshAddress, params);
        }
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

}


