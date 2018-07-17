package com.ws.mesh.awe.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telink.bluetooth.light.ConnectionStatus;
import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.db.RoomDAO;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;
import com.ws.mesh.awe.views.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupEditDevActivity extends BaseActivity {

    @BindView(R.id.rl_edit_current_device)
    RecyclerView mRlCurrentDevice;
    @BindView(R.id.rl_edit_device_not_added)
    RecyclerView mRlNotAddedDevice;

    private int mMeshAddress;   //操作群组的mesh地址
    private int mAddLocation = -1,
            mDelLocation = -1;     //添加 和 删除当前选中位置
    private Room mRoom;   //操作群组
    private List<Device> mAddedGroupList; //已添加列表
    private List<Device> mNoAddedGroupList;   //未添加列表

    private DeviceNotAddedAdapter mNotAddedAdapter;  //未添加列表适配器
    private DeviceAddedAdapter mAddedAdapter;    //已添加列表适配器

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_edit_dev;
    }

    @Override
    protected void initData() {
        mMeshAddress = getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);

        mRoom = CoreData.core().mRoomSparseArray.get(mMeshAddress);
        mAddedGroupList = new ArrayList<>();
        mNoAddedGroupList = new ArrayList<>();

        //获取群组中已添加的列表
        for (int i = 0; i < mRoom.mDeviceIds.size(); i++) {
            Device device = CoreData.core().mDeviceSparseArray.get(mRoom.mDeviceIds.valueAt(i));
            if (device != null && device.mConnectionStatus != ConnectionStatus.OFFLINE) {
                mAddedGroupList.add(CoreData.core().mDeviceSparseArray.get(mRoom.mDeviceIds.valueAt(i)));
            }
        }

        //将设备列表中的设备全部加入到未添加列表
        for (int i = 0; i < CoreData.core().mDeviceSparseArray.size(); i++) {
            Device device = CoreData.core().mDeviceSparseArray.get(CoreData.core().mDeviceSparseArray.keyAt(i));
            if (device.mDevType == AppConstant.OTHER_DEVICE_GATEWAY) continue;
            if (device.mConnectionStatus != null && device.mConnectionStatus != ConnectionStatus.OFFLINE)
                mNoAddedGroupList.add(device);
        }
        //从未添加列表中剔除已添加列表中的设备
        for (int i = 0; i < mAddedGroupList.size(); i++) {
            int meshAddress = mAddedGroupList.get(i).mDevMeshId;
            for (int j = mNoAddedGroupList.size() - 1; j >= 0; j--) {
                //找出有没有相同mesh地址
                if (meshAddress == mNoAddedGroupList.get(j).mDevMeshId) {
                    mNoAddedGroupList.remove(mNoAddedGroupList.get(j));
                    break;
                }
            }
        }

        mNotAddedAdapter = new DeviceNotAddedAdapter(this);
        mAddedAdapter = new DeviceAddedAdapter(this);

        mRlCurrentDevice.setLayoutManager(new GridLayoutManager(GroupEditDevActivity.this, 4));
        mRlCurrentDevice.addItemDecoration(new DividerGridItemDecoration(
                GroupEditDevActivity.this, 8, Color.parseColor("#00FFFFFF")));
        mRlCurrentDevice.setAdapter(mAddedAdapter);
        mRlNotAddedDevice.setLayoutManager(new GridLayoutManager(GroupEditDevActivity.this, 4));
        mRlNotAddedDevice.addItemDecoration(new DividerGridItemDecoration(
                GroupEditDevActivity.this, 8, Color.parseColor("#00FFFFFF")));
        mRlNotAddedDevice.setAdapter(mNotAddedAdapter);
    }

    @OnClick(R.id.img_back)
    public void onBack() {
        finish();
    }

    @OnClick(R.id.tv_confirm)
    public void onConfirm(){
        finish();
    }

    private class DeviceAddedAdapter extends RecyclerView.Adapter {
        private Context mContext;

        DeviceAddedAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_device_edit_item, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final DeviceViewHolder viewHolder = (DeviceViewHolder) holder;
            final Device device = mAddedGroupList.get(position);
            viewHolder.tvName.setText(device.mDevName);
            viewHolder.ivMarkIcon.setImageResource(R.drawable.group_icon_delete_dev);
            viewHolder.tvMarkTitle.setText(getString(R.string.del));
            if (mAddLocation == position) {
                viewHolder.llayoutMark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llayoutMark.setVisibility(View.GONE);
            }
            viewHolder.llayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //第一次点击
                    if (viewHolder.llayoutMark.getVisibility() == View.GONE) {
                        //定位设备
                        SendMsg.locationDevice(device.mDevMeshId);
                        mAddLocation = position;
                        notifyDataSetChanged();
                        //等待两秒将mark清除
                        CoreData.mHandler.removeCallbacks(runnable);
                        CoreData.mHandler.postDelayed(runnable, 3000);
                    } else {
                        groupAddDev();
                    }
                }

                private void groupAddDev() {
                    CoreData.mHandler.removeCallbacks(runnable);
                    viewHolder.llayoutMark.setVisibility(View.GONE);
                    mAddLocation = -1;
                    //移动设备到添加列表
                    mNoAddedGroupList.add(mAddedGroupList.get(position));
                    mAddedGroupList.remove(position);

                    mRoom.mDeviceIds.remove(device.mDevMeshId);
                    allocDeviceGroup(device, true);
                    updateGroupList(mAddedGroupList);
                    mAddedAdapter.notifyDataSetChanged();
                    mNotAddedAdapter.notifyDataSetChanged();
                }

                private Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.llayoutMark.setVisibility(View.GONE);
                        mAddLocation = -1;
                        notifyDataSetChanged();
                    }
                };
            });
        }

        @Override
        public int getItemCount() {
            if (mRoom == null || mRoom.mDeviceIds == null)
                return 0;
            return mAddedGroupList.size();
        }
    }

    private class DeviceNotAddedAdapter extends RecyclerView.Adapter {
        private Context mContext;

        DeviceNotAddedAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.view_device_edit_item, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final DeviceViewHolder viewHolder = (DeviceViewHolder) holder;
            final Device device = mNoAddedGroupList.get(position);
            viewHolder.tvName.setText(device.mDevName);
            viewHolder.ivMarkIcon.setImageResource(R.drawable.group_icon_add_dev);
            viewHolder.tvMarkTitle.setText(getString(R.string.add));
            if (mDelLocation == position) {
                viewHolder.llayoutMark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llayoutMark.setVisibility(View.GONE);
            }
            viewHolder.llayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //第一次点击
                    if (viewHolder.llayoutMark.getVisibility() == View.GONE) {
                        //定位设备
                        SendMsg.locationDevice(device.mDevMeshId);
                        mDelLocation = position;
                        notifyDataSetChanged();
                        //等待两秒将mark清除
                        CoreData.mHandler.postDelayed(runnable, 3000);
                    } else {
                        groupRemoveDev();
                    }
                }

                private void groupRemoveDev() {
                    CoreData.mHandler.removeCallbacks(runnable);
                    viewHolder.llayoutMark.setVisibility(View.GONE);
                    mDelLocation = -1;
                    //移动设备到添加列表
                    mAddedGroupList.add(mNoAddedGroupList.get(position));
                    mNoAddedGroupList.remove(position);

                    mRoom.mDeviceIds.put(device.mDevMeshId, device.mDevMeshId);
                    allocDeviceGroup(device, false);

                    updateGroupList(mAddedGroupList);
                    mAddedAdapter.notifyDataSetChanged();
                    mNotAddedAdapter.notifyDataSetChanged();
                }

                private Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.llayoutMark.setVisibility(View.GONE);
                        mDelLocation = -1;
                        notifyDataSetChanged();
                    }
                };
            });
        }

        @Override
        public int getItemCount() {
            if (mRoom == null || mRoom.mDeviceIds == null)
                return 0;
            return mNoAddedGroupList.size();
        }
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.llayout_item)
        LinearLayout llayoutItem;
        @BindView(R.id.tv_device_edit_name)
        TextView tvName;
        @BindView(R.id.llayout_mark)
        LinearLayout llayoutMark;
        @BindView(R.id.iv_device_mark_img)
        ImageView ivMarkIcon;
        @BindView(R.id.tv_device_mark_name)
        TextView tvMarkTitle;

        DeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void allocDeviceGroup(Device device, boolean isAdded) {
        int groupAddress = mMeshAddress;
        int dstAddress = device.mDevMeshId;
        if (isAdded) {
            SendMsg.cancelAllocationGroup(dstAddress, groupAddress);
        } else {
            SendMsg.allocationGroup(dstAddress, groupAddress);
        }
    }

    private void updateGroupList(List<Device> deviceList) {
        SparseArray<Integer> mDevId = new SparseArray<>();
        for (int i = 0; i < deviceList.size(); i++) {
            mDevId.put(deviceList.get(i).mDevMeshId, deviceList.get(i).mDevMeshId);
        }
        RoomDAO.getInstance().updateRoom(mRoom);
    }
}
