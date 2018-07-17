package com.ws.mesh.awe.ui.presenter;

public interface ITimingView {
    //打开定时
    void openAlarm(int id, boolean success);

    //关闭定时
    void closeAlarm(boolean success);

    //删除定时
    void deleteAlarm(boolean success);
}
