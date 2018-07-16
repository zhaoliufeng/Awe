package com.ws.mesh.awe.ui.presenter;

import android.text.TextUtils;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.db.RoomDAO;
import com.ws.mesh.awe.ui.impl.IRoomFragmentView;
import com.ws.mesh.awe.utils.CoreData;

public class RoomPresenter {

    private IRoomFragmentView view;

    public RoomPresenter(IRoomFragmentView view) {
        this.view = view;
    }

    public void updateRoomName(Room room, String name) {
        if (TextUtils.isEmpty(name)) {
            view.onUpdateRoomNameError(R.string.tip_name_can_not_null);
            return;
        }

        room.mRoomName = name;
        //修改并保存名称
        if (saveName(room)) {
            view.onUpdateRoomNameSuccess(room);
        } else {
            view.onUpdateRoomNameError(R.string.tip_edit_failed);
        }
    }

    private boolean saveName(Room room) {
        return RoomDAO.getInstance().updateRoom(room);
    }

    public void deleteRoom(Room room) {
        if (RoomDAO.getInstance().deleteRoom(room)){
            CoreData.core().mRoomSparseArray.remove(room.mRoomId);
            view.onDeleteRoom(room,true);
        }else {
            view.onDeleteRoom(room,false);
        }
    }
}
