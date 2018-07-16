package com.ws.mesh.awe.ui.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Device;

/**
 * Created by we_smart on 2017/12/27.
 */

public class ScanAddDeviceAdapter extends BaseAdapter {

    private SparseArray<Device> mDeviceSparseArray;

    private Context mContext;

    public void setSparseArray(SparseArray<Device> deviceSparseArray) {
        this.mDeviceSparseArray = deviceSparseArray;
    }

    public ScanAddDeviceAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mDeviceSparseArray == null) {
            return 0;
        }
        return mDeviceSparseArray.size();
    }

    @Override
    public Device getItem(int position) {
        return mDeviceSparseArray.valueAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceItemHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_grid_device, null);
            holder = new DeviceItemHolder();
            holder.txtName = convertView
                    .findViewById(R.id.txt_name);
            convertView.setTag(holder);
        } else {
            holder = (DeviceItemHolder) convertView.getTag();
        }

        Device device = this.getItem(position);
        holder.txtName.setText(device.mDevName);

        return convertView;
    }

    private static class DeviceItemHolder {
        TextView txtName;
    }
}
