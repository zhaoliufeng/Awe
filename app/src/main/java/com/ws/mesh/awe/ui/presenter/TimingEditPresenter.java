package com.ws.mesh.awe.ui.presenter;

import android.content.Context;
import android.util.SparseArray;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.db.TimingDAO;
import com.ws.mesh.awe.ui.impl.ITimingEditView;
import com.ws.mesh.awe.utils.SendMsg;
import com.ws.mesh.awe.utils.Utils;

import java.util.Calendar;

public class TimingEditPresenter {

    private ITimingEditView mITimingEditView;
    public SparseArray<Timing> mAlarmSparseArray;
    private Context mContext;
    private int meshAddress;
    public TimingEditPresenter(ITimingEditView iTimingEditView, Context context) {
        mITimingEditView = iTimingEditView;
        mContext = context;
    }

    public void init(int meshAddress) {
        this.meshAddress = meshAddress;
        getAlarmList();
    }

    private SparseArray<Timing> getAlarmList() {
        mAlarmSparseArray = TimingDAO.getInstance().queryTiming(meshAddress);
        return mAlarmSparseArray;
    }

    public void addAlarm(int hours, int mins, int sceneId, int weekNums, int meshAddress, int alarmId) {
        SendMsg.updateDeviceTime();
        Timing timing = packAlarm(hours, mins, sceneId, weekNums, meshAddress, alarmId);
        if (timing != null) {
            if (TimingDAO.getInstance().insetTiming(timing)) {
                //发送指令
                SendMsg.addAlarm(meshAddress, timing);
                mITimingEditView.addAlarm(true);
            } else {
                mITimingEditView.addAlarm(false);
            }
        } else {
            mITimingEditView.addAlarm(false);
        }
    }

    private Timing packAlarm(int hours, int mins, int sceneId, int weeknums, int meshAddress, int alarmId) {
        Timing timing = new Timing();
        timing.mAId = alarmId == -1 ? getAlarmId() : alarmId;
        if (timing.mAId == -1) {
            //无可用Id
            mITimingEditView.maximumNumber();
            return null;
        }

        timing.mWeekNum = 0;
        timing.mAlarmType = meshAddress < 0x8000 ? 1 : 2;
        timing.mAlarmEvent = sceneId;
        timing.mHours = hours;
        timing.mMins = mins;
        timing.mIsOpen = true;
        timing.mMonths = 0;
        timing.mTotalTime = hours * 60 + mins;
        timing.mSec = 0;
        timing.mDesc = "";

        if (weeknums == 0) {
            //日月年模式
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, mins);
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                timing.mUtcTime = calendar.getTimeInMillis() + AppConstant.DAY_TIME;
                timing.mMonths = Calendar.getInstance().get(Calendar.MONTH) + 1;
                timing.mDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1;
            } else {
                timing.mUtcTime = calendar.getTimeInMillis();
                timing.mMonths = calendar.get(Calendar.MONTH) + 1;
                timing.mDate = calendar.get(Calendar.DAY_OF_MONTH);
            }
        } else {
            //周期模式
            timing.mWeekNum = weeknums;
        }

        timing.mParentId = meshAddress;
        return timing;
    }

    //获取当前可用闹钟id
    private int getAlarmId() {
        if (meshAddress < 0x8000) {
            //设备有2个定时 1 - 2
            for (int id = 1; id <= 2; id++) {
                if (mAlarmSparseArray.get(id) == null) {
                    return id;
                }
            }
        } else {
            //房间有4个定时 3 - 6
            for (int id = 3; id <= 6; id++) {
                if (mAlarmSparseArray.get(id) == null) {
                    return id;
                }
            }
        }
        return -1;
    }

    //获取执行的时间
    public String getExecuteInfo(int weekNum) {
        if (weekNum == 0)
            return mContext.getString(R.string.never_repeat);
        if (weekNum == 127)
            return mContext.getString(R.string.every_day);
        if (weekNum == 62)
            return mContext.getString(R.string.work_day);

        byte[] weeks = Utils.reverseBytes(Utils.weekNumToBinaryByteArray(weekNum));
        String[] weekString = mContext.getResources().getStringArray(R.array.custom_week_data);
        StringBuilder showString = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (weeks[i] == 1) {
                showString.append(weekString[i]).append(",");
            }
        }
        return showString.substring(0, showString.length() - 1);
    }

    //获取执行的动作
    public String getExecuteEvent(int eventId) {
        return mContext.getResources().getStringArray(R.array.timing_events)[eventId];
    }
}
