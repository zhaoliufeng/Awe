package com.ws.mesh.awe.db;

import android.util.SparseArray;

import com.we_smart.sqldao.BaseDAO;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.utils.CoreData;

import java.util.List;

public class RoomDAO extends BaseDAO<Room> {

    private RoomDAO() {
        super(Room.class);
    }

    private static RoomDAO roomDAO;

    public static RoomDAO getInstance() {
        if (roomDAO == null) {
            synchronized (RoomDAO.class) {
                if (roomDAO == null) {
                    roomDAO = new RoomDAO();
                }
            }
        }
        return roomDAO;
    }

    public boolean insertRoom(Room room) {
        room.mRoomMeshName = CoreData.core().getCurrMesh().mMeshName;
        room.mDeviceIdsStr = Room.devIdToString(room.mDeviceIds);
        return insert(room);
    }

    public boolean deleteRoom(Room room) {
        return delete(room, "mRoomMeshName", "mRoomId");
    }

    public boolean updateRoom(Room room) {
        room.mDeviceIdsStr = Room.devIdToString(room.mDeviceIds);
        return update(room,"mRoomMeshName", "mRoomId");
    }

    public SparseArray<Room> queryRoom() {
        return queryRoom(new String[]{CoreData.core().getCurrMesh().mMeshName}, "mRoomMeshName");
    }

    private SparseArray<Room> queryRoom(String[] whereValue, String... whereKey) {
        List<Room> rooms = query(whereValue, whereKey);
        SparseArray<Room> roomSparseArray = new SparseArray<>();
        for (Room room : rooms) {
            room.mDeviceIds = Room.stringToDevId(room.mDeviceIdsStr);
            roomSparseArray.put(room.mRoomId, room);
        }
        return roomSparseArray;
    }
}
