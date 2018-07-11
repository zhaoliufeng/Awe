package com.ws.mesh.awe.constant;

/**
 * Created by we_smart on 2017/11/20.
 */

public class AppLifeStatusConstant {

    //正常启动
    public static final int NORMAL_START = 1;

    //异常杀死
    public static final int KILL_PROGRESS = -1;

    //杀死重启
    public static final int RESTART_APP = 0;

    //应该开始启动
    public static final int APP_START = 2;

    //直接返回
    public static final int APP_MAIN_BACK = 3;
}
