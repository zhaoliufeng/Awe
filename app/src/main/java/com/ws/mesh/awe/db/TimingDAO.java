package com.ws.mesh.awe.db;

import android.util.SparseArray;

import com.we_smart.sqldao.BaseDAO;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.utils.CoreData;

import java.util.List;

public class TimingDAO extends BaseDAO<Timing> {

    private TimingDAO() {
        super(Timing.class);
    }

    private static TimingDAO timingDAO;

    public static TimingDAO getInstance(){
        if (timingDAO == null){
            synchronized (TimingDAO.class){
                if (timingDAO == null){
                    timingDAO = new TimingDAO();
                }
            }
        }
        return timingDAO;
    }


    public boolean insetTiming(Timing timing) {
        timing.mAlarmMeshName = CoreData.core().getCurrMesh().mMeshName;
        return insert(timing);
    }

    public boolean deleteTiming(Timing timing) {
        return delete(timing, "mParentId", "mAlarmMeshName", "mAlarmType" ,"mAId");
    }

    //根据mesh地址查询定时
    public SparseArray<Timing> queryTiming(int meshAddress) {
        int alarmType = meshAddress > 0x8000 ? AppConstant.ALARM_TYPE_GROUP : AppConstant.ALARM_TYPE_DEVICE;
        if (meshAddress < 0x8000) {
            //如果是单个设备的定时 则需要增加一条所属id定位字段
            return queryTiming(
                    new String[]{"mParentId", "mAlarmType", "mAlarmMeshName"},
                    String.valueOf(meshAddress), String.valueOf(alarmType), CoreData.core().getCurrMesh().mMeshName);
        } else {
            return queryTiming(
                    new String[]{"mAlarmType", "mAlarmMeshName"},
                    String.valueOf(alarmType), CoreData.core().getCurrMesh().mMeshName);
        }
    }

    private SparseArray<Timing> queryTiming(String[] whereKey, String... whereValue) {
        List<Timing> alarms = query(whereValue, whereKey);
        SparseArray<Timing> alarmSparseArray = new SparseArray<>();
        for (Timing alarm : alarms) {
            alarmSparseArray.put(alarm.mAId, alarm);
        }
        return alarmSparseArray;
    }

    public boolean updateTiming(Timing timing) {
        return update(timing, "mParentId", "mAlarmMeshName", "mAlarmType" ,"mAId");
    }
}
