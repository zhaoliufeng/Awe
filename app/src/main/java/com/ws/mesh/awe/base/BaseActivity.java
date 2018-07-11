package com.ws.mesh.awe.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ws.mesh.awe.MeshApplication;
import com.ws.mesh.awe.R;
import com.ws.mesh.awe.constant.AppLifeStatusConstant;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.activity.LauncherActivity;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.StatusBarUpper;

import butterknife.ButterKnife;

public abstract class BaseActivity extends FragmentActivity {

    protected abstract int getLayoutId();

    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (CoreData.core().getCurrAppStatus() == AppLifeStatusConstant.KILL_PROGRESS) {
            //初始化状态
            Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        ButterKnife.bind(this);
        initData();
        CoreData.addActivity(this, getClass().getSimpleName());

        //设置状态栏高度
        if (findViewById(R.id.view_status_bar) != null) {
            setStatusBar(findViewById(R.id.view_status_bar));
        }
    }

    public void pushActivity(Class<? extends BaseActivity> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    public void pushActivity(Class<? extends BaseActivity> activityClass, int intExtra) {
        startActivity(new Intent(this, activityClass)
                .putExtra(IntentConstant.NEED_ID, intExtra));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreData.removeActivity(getClass().getSimpleName());
    }

    protected void toast(final String context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MeshApplication.getInstance(), context, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 设置状态栏 高度
     */
    protected void setStatusBar(View statusBar) {
        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.height = StatusBarUpper.getStatusBarHeight(this);
        statusBar.setLayoutParams(layoutParams);
    }
}
