package com.ws.mesh.awe.ui.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.SparseArray;

import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.WeSmartLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;
import com.ws.mesh.awe.MeshApplication;
import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.constant.DeviceForm;
import com.ws.mesh.awe.db.DeviceDAO;
import com.ws.mesh.awe.service.TelinkLightService;
import com.ws.mesh.awe.ui.impl.IScanDeviceView;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;
import com.ws.mesh.awe.utils.TaskPool;
import com.ws.mesh.awe.utils.Utils;

import java.util.HashMap;

/**
 * Created by we_smart on 2017/12/27.
 */

public class ScanDevicePresenter implements EventListener<String> {


    private SparseArray<Device> mDeviceSparseArray = new SparseArray<>();
    private HashMap<String, DeviceInfo> mAllDevice = new HashMap<>();

    //监听蓝牙数据
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        startScan(1000);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        //蓝牙关闭
                        view.onStopScan();
                        break;
                }
            }
        }
    };
    private volatile int mMeshAddress;

    private IScanDeviceView view;

    public ScanDevicePresenter(IScanDeviceView view) {
        this.view = view;
        addListener();
        registerReceiver();
    }

    public void checkBle(Activity activity) {
        BluetoothAdapter mBluetoothAdapter = LeBluetooth.getInstance().getAdapter(MeshApplication.getInstance());
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        } else {
            startScan(1000);
        }
    }

    private void addListener() {
        MeshApplication.getInstance().addEventListener(LeScanEvent.LE_SCAN, this);
        MeshApplication.getInstance().addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
        MeshApplication.getInstance().addEventListener(DeviceEvent.STATUS_CHANGED, this);
        MeshApplication.getInstance().addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        MeshApplication.getInstance().addEventListener(MeshEvent.ERROR, this);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        MeshApplication.getInstance().registerReceiver(mReceiver, filter);
    }

    /**
     * 事件处理方法
     *
     * @param event
     */
    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                this.onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                this.onLeScanTimeout((LeScanEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                this.onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.ERROR:
                this.onMeshEvent((MeshEvent) event);
                break;
        }
    }

    private void onLeScanTimeout(LeScanEvent event) {
        view.addDeviceStatus(R.string.done);
        view.addDeviceFinish(mDeviceSparseArray.size());
    }

    private void onMeshEvent(MeshEvent event) {
        view.addDeviceStatus(R.string.please_restart_ble);
        view.onBLEError();
    }

    private void onLeScan(LeScanEvent event) {
        view.addDeviceStatus(R.string.find_device);
        DeviceInfo deviceInfo = event.getArgs();
        if (null != deviceInfo) {
            if (!TextUtils.isEmpty(deviceInfo.macAddress))
                mAllDevice.put(deviceInfo.macAddress, deviceInfo);
        }
        if (deviceInfo != null) {
            if (deviceInfo.productUUID == 0x04) {
                switch (deviceInfo.meshUUID) {
                    case DeviceForm.HAND_CONTROL:
                    case DeviceForm.SWITCH_PANEL:
                        mMeshAddress = 255;
                        break;
                    default:
                        mMeshAddress = Utils.getVaildMeshAddress(CoreData.core().mDeviceSparseArray) & 0xFF;
                        break;
                }
            } else if (deviceInfo.productUUID == 0x20) {
                mMeshAddress = 255;
            }
        }
        if (mMeshAddress == -1) {
            view.addDeviceStatus(R.string.cannot_add_any_device);
            return;
        }

        //更新参数
        LeUpdateParameters params = Parameters.createUpdateParameters();
        params.setOldMeshName(CoreData.core().getCurrMesh().mMeshFactoryName);
        params.setOldPassword(CoreData.core().getCurrMesh().mMeshFactoryPassword);
        params.setNewMeshName(CoreData.core().getCurrMesh().mMeshName);
        params.setNewPassword(CoreData.core().getCurrMesh().mMeshPassword);
        if (deviceInfo != null) {
            deviceInfo.meshAddress = mMeshAddress & 0xFF;
        }
        params.setUpdateDeviceList(deviceInfo);
        TelinkLightService.Instance().idleMode(true);
        //加灯
        TelinkLightService.Instance().updateMesh(params);
    }

    @SuppressLint("DefaultLocale")
    private void onDeviceStatusChanged(DeviceEvent event) {
        WeSmartLog.i(event.getType());
        DeviceInfo deviceInfo = event.getArgs();
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:
                //加灯完成继续扫描,直到扫不到设备
                if (!TextUtils.isEmpty(deviceInfo.macAddress)) {
                    Device device = new Device();
                    device.mConnectionStatus = ConnectionStatus.OFFLINE;
                    device.mDevMeshId = deviceInfo.meshAddress;
                    if (mAllDevice.get(deviceInfo.macAddress) == null){
                        device.mDevType = AppConstant.DEFAULT_TYPE;
                    }else {
                        device.mDevType = mAllDevice.get(deviceInfo.macAddress).meshUUID;
                    }

                    device.mDevName = String.format("Device-%d", device.mDevMeshId);
                    device.mDevMacAddress = deviceInfo.macAddress;
                    DeviceDAO.getInstance().insertDevice(device);
                    CoreData.core().mDeviceSparseArray.put(device.mDevMeshId, device);
                    mDeviceSparseArray.put(device.mDevMeshId, device);
                    view.addDeviceSuccess(mDeviceSparseArray);
                    startScan(500);
                } else {
                    SendMsg.kickOut(deviceInfo.meshAddress);
                    startScan(500);
                    view.addDeviceStatus(R.string.add_dev_fail);
                }
                break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
                view.addDeviceStatus(R.string.add_dev_fail);
                //加灯失败继续扫描
                this.startScan(500);
                break;
        }
    }

    public void startScan(int delay) {
        view.addDeviceStatus(R.string.search);
        TaskPool.DefRandTaskPool().PushTask(new Runnable() {
            @Override
            public void run() {
                if (CoreData.core().isEmptyMesh()) return;
                //扫描参数
                LeScanParameters params = LeScanParameters.create();
                params.setMeshName(CoreData.core().getCurrMesh().mMeshFactoryName);
                params.setOutOfMeshName(CoreData.core().getCurrMesh().mMeshFactoryName);
                params.setTimeoutSeconds(15);
                params.setScanMode(true);
                TelinkLightService.Instance().startScan(params);
            }
        }, delay);
    }

    public void destroy() {
        MeshApplication.getInstance().removeEventListener(this);
        MeshApplication.getInstance().unregisterReceiver(mReceiver);
        mAllDevice = null;
        mReceiver = null;
    }
}
