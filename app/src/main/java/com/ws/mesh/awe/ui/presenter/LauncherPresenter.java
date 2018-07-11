package com.ws.mesh.awe.ui.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ws.mesh.awe.MeshApplication;
import com.ws.mesh.awe.bean.Mesh;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.db.MeshDAO;
import com.ws.mesh.awe.service.TelinkLightService;
import com.ws.mesh.awe.ui.impl.ILauncherView;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SPUtils;

public class LauncherPresenter {

    private ILauncherView view;

    public LauncherPresenter(ILauncherView view) {
        this.view = view;
        if (TelinkLightService.Instance() == null) {
            MeshApplication.getInstance().doInit();
        }
        initData();
    }

    private void initData() {
        if (CoreData.core().getCurrMesh() != null) {
            if (!TextUtils.isEmpty(SPUtils.getLatelyMesh())) {
                view.enterMain();
            } else {
                initMeshData();
            }
        } else {
            initMeshData();
        }
    }

    //创建mesh网络 fulife
    private void initMeshData() {
        Mesh mesh;
        if (CoreData.core().mMeshMap == null
                || CoreData.core().mMeshMap.size() == 0) {
            mesh = new Mesh();

            mesh.mMeshName = AppConstant.MESH_DEFAULT_NAME;
            mesh.mMeshPassword = AppConstant.MESH_DEFAULT_PASSWORD;
            mesh.mMeshFactoryName = AppConstant.MESH_DEFAULT_NAME;
            mesh.mMeshFactoryPassword = AppConstant.MESH_DEFAULT_PASSWORD;

            MeshDAO.getInstance().insertMesh(mesh);
            SPUtils.setLatelyMesh(mesh.mMeshName);
        }else {
            String meshName = SPUtils.getLatelyMesh();
            mesh = CoreData.core().mMeshMap.get(meshName);
        }
        CoreData.core().switchMesh(mesh);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                view.enterMain();
            }
        }, 1500);
    }
}
