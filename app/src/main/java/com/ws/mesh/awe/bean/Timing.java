package com.ws.mesh.awe.bean;

import com.we_smart.sqldao.Annotation.DBFiled;

public class Timing {
    //键
    @DBFiled(isPrimary = true, isAutoIncrement = true)
    public int mId;

    //定时的Id
    @DBFiled
    public int mAId;

    //1是设备、2是群组
    @DBFiled
    public int mAlarmType;

    //群组所属网络名
    @DBFiled
    public String mAlarmMeshName;

    //定时所属目标ID
    @DBFiled
    public int mParentId;

    //定时的状态
    @DBFiled
    public boolean mIsOpen;

    //定时需要执行的事件 0 关 1开 2彩虹呼吸....
    @DBFiled
    public int mAlarmEvent;

    //UTC的时间戳
    @DBFiled
    public long mUtcTime;

    //定时的月
    @DBFiled
    public int mMonths;

    //定时的日
    @DBFiled
    public int mDate;

    //定时的小时
    @DBFiled
    public int mHours;

    //定时的分钟
    @DBFiled
    public int mMins;

    //定时的秒
    @DBFiled
    public int mSec;

    //总的时间
    @DBFiled
    public long mTotalTime;

    //定时的周期参数
    @DBFiled
    public int mWeekNum;

    //定时的描述
    @DBFiled
    public String mDesc;
}
