/*
 * Copyright (C) 2015 The Telink Bluetooth Light Project
 *
 */
package com.telink.bluetooth.light;

/**
 * 连接状态
 */
public enum ConnectionStatus {
    OFF(0), ON(1), OFFLINE(2), ERROR(3);

    private final int value;

    ConnectionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
