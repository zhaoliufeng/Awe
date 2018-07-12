package com.ws.mesh.awe.bean;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.SparseArray;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.we_smart.sqldao.Annotation.DBFiled;
import com.ws.mesh.awe.constant.AppConstant;

/**
 * 群组
 * Created by we_smart on 2017/11/15.
 */

public class Room {

    public Room() {
    }

    @DBFiled
    public int mRoomId;

    //群组所属网络名
    @DBFiled
    public String mRoomMeshName;

    //群组的名称
    @DBFiled
    public String mRoomName;

    //群组的图标
    @DBFiled
    public String mRoomIcon;

    //群组中的设备id列表字符串 用于存储在数据库中
    @DBFiled(isText = true)
    public String mDeviceIdsStr;

    //群组中的设备id列表集合 用于操作增删
    public SparseArray<Integer> mDeviceIds;

    public static String devIdToString(SparseArray<Integer> mDevSparseArray) {
        if (mDevSparseArray == null || mDevSparseArray.size() == 0) return "";
        JSONArray jsonArray = new JSONArray();
        for (int x = 0; x < mDevSparseArray.size(); x++) {
            int id = mDevSparseArray.valueAt(x);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(AppConstant.ID, id);
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    @SuppressLint("UseSparseArrays")
    public static SparseArray<Integer> stringToDevId(String devIdText) {
        SparseArray<Integer> mDevIdSparseArray = new SparseArray<>();
        if (TextUtils.isEmpty(devIdText)) return mDevIdSparseArray;
        JSONArray jsonArray = JSON.parseArray(devIdText);
        for (int x = 0; x < jsonArray.size(); x++) {
            JSONObject jsonObject = jsonArray.getJSONObject(x);
            int id = jsonObject.getIntValue(AppConstant.ID);
            mDevIdSparseArray.put(id, id);
        }
        return mDevIdSparseArray;
    }
}
