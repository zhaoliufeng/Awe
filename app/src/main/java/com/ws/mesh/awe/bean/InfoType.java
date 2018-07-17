package com.ws.mesh.awe.bean;

public enum  InfoType {
    ABOUT_WE(0), ABOUT_US(1), SUPPORT(2);

    private final int value;
    InfoType(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
