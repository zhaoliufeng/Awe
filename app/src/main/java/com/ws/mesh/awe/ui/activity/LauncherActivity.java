package com.ws.mesh.awe.ui.activity;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.constant.AppLifeStatusConstant;
import com.ws.mesh.awe.ui.impl.ILauncherView;
import com.ws.mesh.awe.ui.presenter.LauncherPresenter;
import com.ws.mesh.awe.utils.CoreData;

public class LauncherActivity extends BaseActivity implements ILauncherView{
    @Override
    protected int getLayoutId() {
        CoreData.core().setCurrAppStatus(AppLifeStatusConstant.NORMAL_START);
        return R.layout.activity_launcher;
    }

    @Override
    protected void initData() {
        new LauncherPresenter(this);
    }

    @Override
    public void enterMain() {
        pushActivity(MainActivity.class);
        finish();
    }

    @Override
    public void enterLogin() {
        pushActivity(LoginActivity.class);
        finish();
    }
}
