package com.ws.mesh.awe.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.we_smart.permissions.PermissionsListener;
import com.we_smart.permissions.PermissionsManager;
import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.constant.AppLifeStatusConstant;
import com.ws.mesh.awe.ui.impl.ILauncherView;
import com.ws.mesh.awe.ui.presenter.LauncherPresenter;
import com.ws.mesh.awe.utils.CoreData;

public class LauncherActivity extends BaseActivity implements ILauncherView{

    private Class<? extends BaseActivity> clazz;
    private PermissionsManager permissionsManager;
    private String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private PermissionsListener permissionsListener = new PermissionsListener() {
        @Override
        public void getAllPermissions() {
            pushActivity(clazz);
            finish();
        }

        @Override
        public void PermissionsDenied(String... deniedPermissions) {
            permissionsManager.startRequestPermission(deniedPermissions);
        }

        @Override
        public void cancelPermissionRequest() {
            pushActivity(clazz);
            finish();
        }
    };

    @Override
    protected int getLayoutId() {
        CoreData.core().setCurrAppStatus(AppLifeStatusConstant.NORMAL_START);
        return R.layout.activity_launcher;
    }

    @Override
    protected void initData() {
        //检查位置权限
        permissionsManager = new PermissionsManager(this, permissions);
        new LauncherPresenter(this);
    }

    @Override
    public void enterMain() {
        clazz = MainActivity.class;
        permissionsManager.checkPermissions(permissionsListener);
    }

    @Override
    public void enterLogin() {
        clazz = LoginActivity.class;
        permissionsManager.checkPermissions(permissionsListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int deniedCount = 0;
        if (requestCode == PermissionsManager.REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedCount++;
                    }
                }
                if (deniedCount != 0){
                    permissionsManager.showDialogTipUserGoToAppSettting(getString(R.string.no_locate_permission), getString(R.string.tip_no_locate_permission));
                }else {
                    pushActivity(clazz);
                    finish();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionsManager.REQUEST_SETTING_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    permissionsManager.showDialogTipUserGoToAppSettting(getString(R.string.no_locate_permission), getString(R.string.tip_no_locate_permission));
                } else {
                    pushActivity(clazz);
                    finish();
                }
            }
        }
    }
}
