package com.ws.mesh.awe.ui.fragment;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.ui.activity.DeviceContentActivity;
import com.ws.mesh.awe.ui.adapter.DeviceAdapter;
import com.ws.mesh.awe.ui.impl.IDeviceFragmentView;
import com.ws.mesh.awe.ui.presenter.DevicePresenter;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;

import butterknife.BindView;

public class DeviceFragment extends BaseFragment implements IDeviceFragmentView{

    @BindView(R.id.rl_devices_list)
    RecyclerView mRlDeviceList;

    private DeviceAdapter deviceAdapter;

    private DevicePresenter presenter;

    private AlertDialog renameDialog;
    private PopupWindow pop;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_devices;
    }

    @Override
    protected void initData(View view) {
        presenter = new DevicePresenter(this);
        deviceAdapter = new DeviceAdapter(CoreData.core().mDeviceSparseArray);

        deviceAdapter.setOnDeviceSelectListener(onDeviceSelectListener);
        mRlDeviceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRlDeviceList.setAdapter(deviceAdapter);
    }

    public void refreshDevice(Device device) {
        deviceAdapter.refreshDevice(device);
    }

    private DeviceAdapter.OnDeviceSelectListener onDeviceSelectListener =
            new DeviceAdapter.OnDeviceSelectListener() {
                @Override
                public void onSet(int position) {
                    popWindow(getView(), position);
                }

                @Override
                public void onSwitch(int position, boolean isOn) {
                    Device device = CoreData.core().mDeviceSparseArray.valueAt(position);
                    SendMsg.switchDevice(device.mDevMeshId, isOn);
                }

                @Override
                public void onEdit(int position) {
                    Device device = CoreData.core().mDeviceSparseArray.valueAt(position);
                    pushActivity(DeviceContentActivity.class, device.mDevMeshId);
                }
            };

    //弹窗
    private void popWindow(View v, final int position) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_device_layout, null);
        //设置屏幕的高度和宽度
        pop = new PopupWindow(view, getScreenWidth(getActivity()),
                (getScreenHeight(getActivity()) / 4));
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_background));
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.style.PopupWindow_anim_style);
        pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        final Device device = CoreData.core().mDeviceSparseArray.valueAt(position);
        TextView tvRename, tvDelete, tvSetColor;
        tvRename = view.findViewById(R.id.tv_rename);
        tvDelete = view.findViewById(R.id.tv_delete);
        tvSetColor = view.findViewById(R.id.tv_set_color);
        tvRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popRenameDialog(device);
                pop.dismiss();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.kickOutDevice(device);
                pop.dismiss();
            }
        });

        tvSetColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushActivity(DeviceContentActivity.class, device.mDevMeshId);
                pop.dismiss();
            }
        });
    }

    private static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    private static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    private void popRenameDialog(final Device device) {
        renameDialog = new AlertDialog.Builder(getActivity(), R.style.CustomDialog).create();
        renameDialog.show();
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_rename);
            renameDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            renameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            //获取控件绑定 并初始化值
            final EditText edtNewDeviceName = window.findViewById(R.id.edt_edit_input);
            if (device != null)
                edtNewDeviceName.setHint(device.mDevName);
            edtNewDeviceName.setFocusable(true);
            edtNewDeviceName.setFocusableInTouchMode(true);

            CoreData.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    edtNewDeviceName.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }, 100);

            Button confirm = window.findViewById(R.id.btn_confirm);
            Button cancel = window.findViewById(R.id.btn_cancel);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String name = edtNewDeviceName.getText().toString().trim();
                    presenter.saveDeviceName(device, name);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onSaveDeviceNameSuccess(Device device) {
        toast(R.string.tip_edit_success);
        deviceAdapter.notifyItemChanged(CoreData.core().mDeviceSparseArray.indexOfKey(device.mDevMeshId));
        renameDialog.dismiss();
        pop.dismiss();
    }

    @Override
    public void onSaveDeviceNameError(int errMsgId) {
        toast(errMsgId);
    }

    @Override
    public void onRemoveDevice(Device device) {
        toast(R.string.remove_success);
        deviceAdapter.removeDevice(device);
        pop.dismiss();
    }

    @Override
    public void onError(int errMsg) {
        toast(errMsg);
    }
}
