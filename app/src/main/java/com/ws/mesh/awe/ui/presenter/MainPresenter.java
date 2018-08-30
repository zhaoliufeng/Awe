package com.ws.mesh.awe.ui.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.util.SparseArray;

import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.NotificationInfo;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.util.Event;
import com.telink.util.EventListener;
import com.ws.mesh.awe.MeshApplication;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.bean.Mesh;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.db.DeviceDAO;
import com.ws.mesh.awe.db.RoomDAO;
import com.ws.mesh.awe.service.TelinkLightService;
import com.ws.mesh.awe.ui.impl.IMainView;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;
import com.ws.mesh.awe.utils.TaskPool;

import java.util.List;

import static com.telink.bluetooth.light.LightAdapter.MODE_AUTO_CONNECT_MESH;

public class MainPresenter implements EventListener<String>{

    private IMainView mIPrimaryView;
    private Activity mActivity;
    private Mesh mCurrMesh;
    private MeshApplication mApplication;

    public MainPresenter(IMainView IMainView, Activity activity) {
        this.mIPrimaryView = IMainView;
        mActivity = activity;
        mApplication = (MeshApplication) mActivity.getApplication();
        mCurrMesh = CoreData.core().getCurrMesh();
        addListener();
        registerReceiver();
    }

    private void addListener() {
        MeshApplication.getInstance().addEventListener(NotificationEvent.GET_DEVICE_TYPE, this);
        MeshApplication.getInstance().addEventListener(LeScanEvent.LE_SCAN, this);
        MeshApplication.getInstance().addEventListener(DeviceEvent.STATUS_CHANGED, this);
        MeshApplication.getInstance().addEventListener(NotificationEvent.ONLINE_STATUS, this);
        MeshApplication.getInstance().addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        MeshApplication.getInstance().addEventListener(MeshEvent.OFFLINE, this);
        MeshApplication.getInstance().addEventListener(MeshEvent.ERROR, this);
        MeshApplication.getInstance().addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
    }


    //广播监听
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        autoConnect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        mIPrimaryView.bleClose();
                        mIPrimaryView.onLoginOut();
                        break;
                }
            }
        }
    };

    //监听广播
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        MeshApplication.getInstance().registerReceiver(mReceiver, filter);
    }


    //请求蓝牙开启
    public void checkBle() {
        if (!CoreData.core().getBLERequestStatus()) {
            BluetoothAdapter mBluetoothAdapter =
                    LeBluetooth.getInstance().getAdapter(mActivity);
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, 1);
                CoreData.core().setBLERequest(true);
            }
        }
        TaskPool.DefRandTaskPool().PushTask(new Runnable() {
            @Override
            public void run() {
                if (TelinkLightService.Instance() != null) {
                    if (TelinkLightService.Instance().getMode() != MODE_AUTO_CONNECT_MESH) {
                        //自动重连参数
                        LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                        connectParams.setMeshName(CoreData.core().getCurrMesh().mMeshName);
                        connectParams.setPassword(CoreData.core().getCurrMesh().mMeshPassword);
                        connectParams.setTimeoutSeconds(15);
                        connectParams.autoEnableNotification(true);
                        TelinkLightService.Instance().autoConnect(connectParams);
                    }
                }
            }
        }, 2000);
    }

    public void autoConnect() {
        if (!TelinkLightService.Instance().isLogin()) {
            onMeshOffline();
        }

        if (TelinkLightService.Instance() != null) {
            if (TelinkLightService.Instance().getMode() != MODE_AUTO_CONNECT_MESH) {
                Mesh mesh = CoreData.core().getCurrMesh();
                //自动重连参数
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(mesh.mMeshName);
                connectParams.setPassword(mesh.mMeshPassword);
                connectParams.setTimeoutSeconds(15);
                connectParams.autoEnableNotification(true);
                TelinkLightService.Instance().autoConnect(connectParams);
            }
            ReFreshNotify();
        }
    }

    public void ReFreshNotify() {
        //刷新Notify参数
        LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
        refreshNotifyParams.setRefreshRepeatCount(2);
        refreshNotifyParams.setRefreshInterval(2000);
        //开启自动刷新Notify
        TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
    }

    //切换网络
    public void switchNetwork() {
        if (!mCurrMesh.mMeshName.equals(CoreData.core().getCurrMesh().mMeshName)) {
            //更新设备信息
            mIPrimaryView.updateDevice(CoreData.core().mDeviceSparseArray);

            mCurrMesh = CoreData.core().getCurrMesh();
        }
    }

    public void onDestroy() {
        MeshApplication.getInstance().removeEventListener(this);
        MeshApplication.getInstance().unregisterReceiver(mReceiver);
        mIPrimaryView = null;
        mActivity = null;
        mReceiver = null;
        mCurrMesh = null;
    }

    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                onMeshOffline();
                break;
            case MeshEvent.ERROR:
                onMeshError((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                onServiceDisconnected((ServiceEvent) event);
            case NotificationEvent.GET_DEVICE_TYPE:
                onDeviceType((NotificationEvent) event);
                break;
            default:
                break;
        }
    }

    /**
     * 设备的状态变化
     */
    private void onDeviceStatusChanged(DeviceEvent event) {
        DeviceInfo deviceInfo = event.getArgs();
        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                if (CoreData.mAddDeviceMode) return;
//                SendMsg.updateDeviceTime();
                CoreData.core().mDirectDeviceMeshAddress = mApplication.getConnectDevice().meshAddress;
                mIPrimaryView.onLoginSuccess();
                break;
            case LightAdapter.STATUS_CONNECTING:
                if (CoreData.mAddDeviceMode) return;
                mIPrimaryView.onFindDevice();
                break;
            case LightAdapter.STATUS_LOGOUT:
                mIPrimaryView.onLoginOut();
                if (CoreData.mAddDeviceMode) return;
                CoreData.core().clear();
                switchNetwork();
                break;
            default:
                break;
        }
    }

    /**
     * 蓝牙状态数据上报
     */
    @SuppressWarnings("unchecked")
    private synchronized void onOnlineStatusNotify(NotificationEvent event) {
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> mNotificationInfoList =
                (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();
        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : mNotificationInfoList) {
            int meshAddress = notificationInfo.meshAddress;
            int status = notificationInfo.status;
            ConnectionStatus connectionStatus = notificationInfo.connectStatus;
            if (CoreData.core().isDevDeleted(meshAddress)) {
                CoreData.core().mDeviceSparseArray.remove(meshAddress);
            } else {
                Device device = CoreData.core().mDeviceSparseArray.get(meshAddress);
                if (device != null) {
                    //已经获取过的设备
                    if (status != 0) {
                        device.mConnectionStatus = connectionStatus;
                        device.mDevMeshId = meshAddress;
                        mIPrimaryView.statusUpdate(device);
                    } else {
                        device.mConnectionStatus = ConnectionStatus.OFFLINE;
                        mIPrimaryView.offline(device);
                    }
                } else {
                    if (status != 0) {
                        device = new Device();
                        device.mDevName = AppConstant.DEVICE_DEFAULT_NAME + "-" + meshAddress;
                        device.mConnectionStatus = connectionStatus;
                        device.mDevMeshId = meshAddress;
                        CoreData.core().mDeviceSparseArray.put(device.mDevMeshId, device);
                        DeviceDAO.getInstance().insertDevice(device);
                        mIPrimaryView.updateDevice(CoreData.core().mDeviceSparseArray);
                    } else {
                        if (CoreData.core().isDevDeleted(meshAddress)) {
                            CoreData.core().mDeviceSparseArray.remove(meshAddress);
                            CoreData.core().deleteData(meshAddress);
                        }
                    }
                }
            }
        }
    }

    /**
     * 蓝牙连接服务连接
     */
    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    /**
     * 蓝牙连接服务断开
     */
    private void onServiceDisconnected(ServiceEvent event) {

    }

    private void onMeshError(MeshEvent event) {
//        showToast(getResources().getString(R.string.faq_text_list), true);
    }

    /**
     * Mesh网络离线
     */
    private void onMeshOffline() {
        for (int x = 0; x < CoreData.core().mDeviceSparseArray.size(); x++) {
            Device device = CoreData.core().mDeviceSparseArray.valueAt(x);
            device.mConnectionStatus = ConnectionStatus.OFFLINE;
        }
        mIPrimaryView.onLoginOut();
    }

    /**
     * 设备类型解析
     */
    private synchronized void onDeviceType(NotificationEvent event) {
        synchronized (MainPresenter.this) {
            NotificationInfo info = event.getArgs();
            int srcAddress = info.src & 0xFF;
            byte[] params = info.params;
            Device device = CoreData.core().mDeviceSparseArray.get(srcAddress);
            int type;
            if (device != null && params[0] == 0x01) {
                int one = ((int) params[1] & 0xFF) << 8;
                int two = params[2] & 0xFF;
                type = one + two;
                if (device.mDevType != type) {
                    device.mDevType = type;
                    CoreData.core().mDeviceSparseArray.put(device.mDevMeshId, device);
                    if (DeviceDAO.getInstance().updateDevice(device)) {
                        mIPrimaryView.statusUpdate(device);
                    }
                }
            }
            //判断是否全部获取到了设备类型
            int notTypeDeviceAddress = hasAllType();
            if (notTypeDeviceAddress != -1) {
                SendMsg.getDeviceType(notTypeDeviceAddress);
            }
        }

    }

    private int hasAllType() {
        for (int i = 0; i < CoreData.core().mDeviceSparseArray.size(); i++) {
            if (CoreData.core().mDeviceSparseArray.valueAt(i).mDevType == AppConstant.DEFAULT_TYPE) {
                return CoreData.core().mDeviceSparseArray.valueAt(i).mDevMeshId;
            }
        }
        return -1;
    }

    //添加默认房间
    @SuppressLint("UseSparseArrays")
    public void addDefaultRoom(){
        Room room = new Room();
        room.mRoomId = getRoomId();
        room.mRoomName = "Room-" + (room.mRoomId - AppConstant.ROOM_START_ID + 1);
        room.mDeviceIds = new SparseArray<>();
        CoreData.core().mRoomSparseArray.append(room.mRoomId, room);
        if (RoomDAO.getInstance().insertRoom(room)){
            mIPrimaryView.addRoom(true);
        }else {
            mIPrimaryView.addRoom(false);
        }
    }

    //获取可用的RoomId
    private int getRoomId() {
        for (int id = AppConstant.ROOM_START_ID; id < AppConstant.ROOM_LAST_ID; id++) {
            if (CoreData.core().mRoomSparseArray.get(id) == null) {
                return id;
            }
        }
        return -1;
    }

    //开启蓝牙
    public void openBluetooth(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null){

        }else {
            if (!adapter.isEnabled()){
                adapter.enable();
            }
        }
    }
}
