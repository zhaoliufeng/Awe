package com.ws.mesh.awe.ui.presenter;

import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.constant.DeviceChannel;
import com.ws.mesh.awe.ui.impl.IColourView;
import com.ws.mesh.awe.ui.impl.ISceneView;
import com.ws.mesh.awe.utils.DevTypeUtils;
import com.ws.mesh.awe.utils.Gap;
import com.ws.mesh.awe.utils.SendMsg;

public class ScenePresenter {
    private ISceneView view;
    //设备的MeshId
    private int meshAddress;
    private int mChannelType;

    private Gap mGap = new Gap();
    public ScenePresenter(ISceneView view) {
        this.view = view;
    }

    public void init(int meshAddress) {
        this.meshAddress = meshAddress;
        setControlType();
    }

    public void controlColor(int color, boolean isUp, boolean isChangeMode) {
        byte validBit = (byte) 0x1F;
        switch (mChannelType) {
            case DeviceChannel.LIGHT_FIVE_RGBWC_CHANNEL:
                validBit = (byte) 0x1F;
                break;
            case DeviceChannel.LIGHT_FOUR_RGBW_CHANNEL:
                validBit = (byte) 0x0F;
                break;
            case DeviceChannel.LIGHT_FOUR_RGBC_CHANNEL:
                validBit = (byte) 0x17;
                break;
            case DeviceChannel.LIGHT_THREE_RGB_CHANNEL:
                validBit = (byte) 0x07;
                break;
            case DeviceChannel.LIGHT_WC_TWO_CHANNEL:
                validBit = (byte) 0x18;
                break;
            case DeviceChannel.LIGHT_ONE_W_CHANNEL:
                validBit = (byte) 0x08;
                break;
            case DeviceChannel.LIGHT_ONE_C_CHANNEL:
                validBit = (byte) 0x10;
                break;
        }

        if (isUp) {
            SendMsg.setDevColor(meshAddress, color, 0, 0, isChangeMode, validBit);
        } else {
            if (mGap.isPassNext()) {
                SendMsg.setDevColor(meshAddress, color, 0, 0, isChangeMode, validBit);
            }
        }
    }

    public void controlBrightness(boolean isUp, int values) {
        //最小亮度为5%
        int brightness = values >= AppConstant.MIN_BRIGHTNESS ? values : values;
        if (isUp) {
            SendMsg.setDevBrightness(meshAddress, brightness);
        } else {
            if (mGap.isPassNext()) {
                if (brightnessChange(brightness)) {
                    SendMsg.setDevBrightness(meshAddress, brightness);
                }
            }
        }
    }

    private boolean brightnessChange(int values) {
        if (Math.abs(values - brightness[0]) > 3 || Math.abs(values - brightness[1]) > 3
                || Math.abs(values - brightness[2]) > 3) {
            brightness[0] = brightness[1];
            brightness[1] = brightness[2];
            brightness[2] = values;
            return true;
        } else {
            brightness[0] = brightness[1];
            brightness[1] = brightness[2];
            brightness[2] = values;
            return false;
        }
    }

    private void setControlType() {
        mChannelType = 0xA0;
        if (isDevice()) {
            mChannelType = DevTypeUtils.getCtrlDevType(meshAddress);
        } else if (isGroup()) {
            mChannelType = DevTypeUtils.getCtrlGroupType(meshAddress);
        }
    }

    private boolean isDevice() {
        return meshAddress < 0x8000;
    }

    private boolean isGroup() {
        return meshAddress >= 0x8000;
    }

    private int brightness[] = new int[]{0x00, 0x00, 0x00};
}
