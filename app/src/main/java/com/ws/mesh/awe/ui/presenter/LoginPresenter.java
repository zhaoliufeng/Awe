package com.ws.mesh.awe.ui.presenter;

import android.text.TextUtils;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Mesh;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.db.MeshDAO;
import com.ws.mesh.awe.ui.impl.ILoginView;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SPUtils;

public class LoginPresenter {
    private ILoginView view;
    public LoginPresenter(ILoginView view) {
        this.view = view;
    }

    public void addMesh(String name, String pwd){
        if (name.isEmpty() || pwd.isEmpty()){
            view.onError(R.string.pls_input_account_pwd);
            return;
        }
        if (AppConstant.MESH_DEFAULT_NAME.equals(name)) {
            view.onError(R.string.cant_create_network);
            return;
        }

        if (TextUtils.isEmpty(name)) {
            view.onError(R.string.name_can_not_null);
            return;
        }

        Mesh mesh = new Mesh();
        mesh.mMeshName = name;
        mesh.mMeshPassword = pwd;
        mesh.mMeshFactoryName = AppConstant.MESH_DEFAULT_NAME;
        mesh.mMeshFactoryPassword = AppConstant.MESH_DEFAULT_PASSWORD;
        if (MeshDAO.getInstance().insertMesh(mesh)){
            SPUtils.setLatelyMesh(mesh.mMeshName);
            CoreData.core().switchMesh(mesh);
            view.addMesh(true);
        }else {
            view.addMesh(false);
        }
    }
}
