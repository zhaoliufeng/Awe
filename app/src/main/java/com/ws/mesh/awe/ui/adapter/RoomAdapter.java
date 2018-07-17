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

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Room;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RoomAdapter extends RecyclerView.Adapter {

    private SparseArray<Room> mDatas;

    public RoomAdapter(SparseArray<Room> roomSparseArray) {
        mDatas = roomSparseArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        RoomViewHolder deviceViewHolder = (RoomViewHolder) holder;
        Room room = mDatas.valueAt(position);
        deviceViewHolder.tvDeviceName.setText(room.mRoomName);
        //房间默认打开
        deviceViewHolder.swcOnOff.setChecked(true);
        deviceViewHolder.swcOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onRoomSelectListener != null)
                    onRoomSelectListener.onSwitch(position, isChecked);
            }
        });

        deviceViewHolder.imgDeviceSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRoomSelectListener != null)
                    onRoomSelectListener.onSet(position);
            }
        });

        deviceViewHolder.rlFrame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRoomSelectListener != null)
                    onRoomSelectListener.onEdit(position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void refreshRoom(Room room) {
        mDatas.append(room.mRoomId, room);
        int index = mDatas.indexOfKey(room.mRoomId);
        notifyItemChanged(index);
    }

    private OnRoomSelectListener onRoomSelectListener;

    public void setOnRoomSelectListener(OnRoomSelectListener onRoomSelectListener) {
        this.onRoomSelectListener = onRoomSelectListener;
    }

    public interface OnRoomSelectListener {
        void onSet(int position);

        void onSwitch(int position, boolean isOn);

        void onEdit(int position);
    }

    class RoomViewHolder extends RecyclerView.ViewHolder {
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

        RoomViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
