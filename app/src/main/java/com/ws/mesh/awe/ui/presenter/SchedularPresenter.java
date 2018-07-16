package com.ws.mesh.awe.ui.presenter;

import android.util.SparseArray;

import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.db.TimingDAO;
import com.ws.mesh.awe.utils.SendMsg;

import java.util.Calendar;

public class SchedularPresenter {

    ITimingView view;
    public SparseArray<Timing> mAlarmSparseArray;
    private int mMeshAddress;

    public SchedularPresenter(ITimingView view) {
        this.view = view;
    }

    public void init(int meshAddress) {
        this.mMeshAddress = meshAddress;
        getAlarmList();
    }

    private void getAlarmList() {
        mAlarmSparseArray = TimingDAO.getInstance().queryTiming(mMeshAddress);
    }

    /**
     * 打开定时
     *
     * @param alarmId 定时id
     */
    public void openAlarm(int alarmId) {
        Timing alarm = mAlarmSparseArray.get(alarmId);
        if (alarm != null) {
            alarm.mIsOpen = true;
            if (alarm.mWeekNum == 0) {
                if (alarm.mUtcTime < System.currentTimeMillis()) {
                    alarm.mUtcTime = alarm.mUtcTime + (1000L * 60L * 60L * 24L);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(alarm.mUtcTime);
                    alarm.mMonths = calendar.get(Calendar.MONTH) + 1;
                    alarm.mDate = calendar.get(Calendar.DAY_OF_MONTH);
                    alarm.mHours = calendar.get(Calendar.HOUR_OF_DAY);
                    alarm.mMins = calendar.get(Calendar.MINUTE);
                }
            }
            setAlarm(alarm);
            if (TimingDAO.getInstance().updateTiming(alarm)){
                view.openAlarm(true);
            }else {
                view.openAlarm(false);
            }
        }
    }

    /**
     * 关闭定时
     *
     * @param alarmId 定时id
     */
    public void closeAlarm(int alarmId) {
        Timing alarm = mAlarmSparseArray.get(alarmId);
        if (alarm != null) {
            alarm.mIsOpen = false;
            if (TimingDAO.getInstance().updateTiming(alarm)) {
                SendMsg.deleteAlarm(mMeshAddress, alarmId);
                view.closeAlarm(true);
            } else {
                view.closeAlarm(false);
            }
        }
    }

    private void setAlarm(Timing timing) {
        if (timing.mAlarmEvent > 1){
            //开关
            SendMsg.openAlarm(mMeshAddress, timing.mAId);
        }else {
            //绑定默认场景
            SendMsg.addAlarmScene(mMeshAddress, timing, timing.mAlarmEvent - 1);
        }
    }

}
