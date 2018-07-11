package com.ws.mesh.awe;

import com.telink.TelinkApplication;
import com.we_smart.sqldao.DBHelper;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.db.DBOpenHelper;
import com.ws.mesh.awe.service.TelinkLightService;
import com.ws.mesh.awe.utils.CoreData;

public class MeshApplication extends TelinkApplication {

    private static final String TAG = "BreathApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        DBHelper.getInstance().initDBHelper(new DBOpenHelper(this, AppConstant.SQL_NAME));
        CoreData.core().initMeshMap();
    }

    @Override
    public void doInit() {
        super.doInit();
        startLightService(TelinkLightService.class);
    }

    //获取到app对象
    private static MeshApplication mApplication;

    public static MeshApplication getInstance() {
        return mApplication;
    }
}
