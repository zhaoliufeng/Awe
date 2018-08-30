package com.ws.mesh.awe.ui.activity;

import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.adapter.ScanAddDeviceAdapter;
import com.ws.mesh.awe.ui.impl.IScanDeviceView;
import com.ws.mesh.awe.ui.presenter.ScanDevicePresenter;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.views.MultiScrollNumber;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanDeviceActivity extends BaseActivity implements IScanDeviceView {

    @BindView(R.id.current_status)
    TextView mCurrStatus;

    @BindView(R.id.add_device_success_num)
    MultiScrollNumber mMultiScrollNumber;

    @BindView(R.id.list_devices)
    GridView mGridView;

    private ScanAddDeviceAdapter mDeviceAdapter;
    private ScanDevicePresenter presenter;
    private boolean formLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_device;
    }

    @Override
    protected void initData() {
        formLogin = getIntent().getBooleanExtra(IntentConstant.NEED_ID, false);
        presenter = new ScanDevicePresenter(this);
        mDeviceAdapter = new ScanAddDeviceAdapter(this);
        mGridView.setAdapter(mDeviceAdapter);
        CoreData.mAddDeviceMode = true;
        presenter.checkBle(this);
    }

    @OnClick(R.id.img_back)
    void OnBack(){
        if (formLogin){
            pushActivity(MainActivity.class);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        OnBack();
    }

    @Override
    public void addDeviceSuccess(SparseArray<Device> sparseArray) {
        mMultiScrollNumber.setNumber(sparseArray.size());
        mDeviceAdapter.setSparseArray(sparseArray);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void addDeviceFinish(int num) {
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog).create();
        if (!dialog.isShowing()) dialog.show();
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setContentView(R.layout.dialog_custom_view);
        Button mPositiveButton = window.findViewById(R.id.add_new_device_ok);
        Button mNegativeButton = window.findViewById(R.id.add_new_device_cancel);
        TextView mTipMessage = window.findViewById(R.id.custom_dialog_tip_message);
        mTipMessage.setText(getResources().getString(R.string.continue_add));
        dialog.setCanceledOnTouchOutside(false);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.startScan(500);
                dialog.dismiss();
            }
        });
        mPositiveButton.setText(getString(R.string.no));
        mNegativeButton.setText(getString(R.string.yes));
    }

    @Override
    public void addDeviceStatus(int status) {
        mCurrStatus.setText(status);
    }

    @Override
    public void onBLEError() {

    }

    @Override
    public void onStopScan() {
        mCurrStatus.setText(getString(R.string.ble_close));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoreData.mAddDeviceMode = false;
        presenter.destroy();
    }
}
