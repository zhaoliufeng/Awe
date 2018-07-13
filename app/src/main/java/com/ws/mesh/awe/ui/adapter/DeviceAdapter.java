package com.ws.mesh.awe.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telink.bluetooth.light.ConnectionStatus;
import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Device;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceAdapter extends RecyclerView.Adapter {

    private SparseArray<Device> mDatas;

    public DeviceAdapter(SparseArray<Device> deviceSparseArray) {
        mDatas = new SparseArray<>();
        for (int i = 0; i < deviceSparseArray.size(); i++) {
            if (deviceSparseArray.valueAt(i).mConnectionStatus != null &&
                    deviceSparseArray.valueAt(i).mConnectionStatus != ConnectionStatus.OFFLINE) {
                mDatas.append(deviceSparseArray.valueAt(i).mDevMeshId, deviceSparseArray.valueAt(i));
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;
        Device device = mDatas.valueAt(position);
        deviceViewHolder.tvDeviceName.setText(device.mDevName);
        deviceViewHolder.imgDeviceStatus.setImageResource(
                device.mConnectionStatus == ConnectionStatus.ON ?
                        R.drawable.icon_light_on : R.drawable.icon_light_off);
        deviceViewHolder.swcOnOff.setChecked(device.mConnectionStatus == ConnectionStatus.ON);
        deviceViewHolder.swcOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onDeviceSelectListener != null)
                    onDeviceSelectListener.onSwitch(position, isChecked);
            }
        });

        deviceViewHolder.imgDeviceSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeviceSelectListener != null)
                    onDeviceSelectListener.onSet(position);
            }
        });

        deviceViewHolder.rlFrame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onDeviceSelectListener != null)
                    onDeviceSelectListener.onEdit(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void refreshDevice(Device device) {
        if (device.mConnectionStatus != null && device.mConnectionStatus != ConnectionStatus.OFFLINE) {
            mDatas.append(device.mDevMeshId, device);
            int index = mDatas.indexOfKey(device.mDevMeshId);
            notifyItemChanged(index);
        } else {
            if (mDatas.get(device.mDevMeshId) != null) {
                int index = mDatas.indexOfKey(device.mDevMeshId);
                mDatas.remove(device.mDevMeshId);
                notifyItemRemoved(index);
            }
        }

    }

    private OnDeviceSelectListener onDeviceSelectListener;

    public void setOnDeviceSelectListener(OnDeviceSelectListener onDeviceSelectListener) {
        this.onDeviceSelectListener = onDeviceSelectListener;
    }

    public interface OnDeviceSelectListener {
        void onSet(int position);

        void onSwitch(int position, boolean isOn);

        void onEdit(int position);
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_device_status)
        ImageView imgDeviceStatus;
        @BindView(R.id.tv_device_name)
        TextView tvDeviceName;
        @BindView(R.id.swc_on_off)
        SwitchCompat swcOnOff;
        @BindView(R.id.img_device_setting)
        ImageView imgDeviceSet;
        @BindView(R.id.rl_frame)
        RelativeLayout rlFrame;

        DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
