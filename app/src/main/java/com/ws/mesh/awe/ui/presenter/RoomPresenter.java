package com.ws.mesh.awe.ui.presenter;

import android.text.TextUtils;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.db.RoomDAO;
import com.ws.mesh.awe.ui.impl.IRoomFragmentView;

public class RoomPresenter {

    private IRoomFragmentView view;

    public RoomPresenter(IRoomFragmentView view) {
        this.view = view;
    }

    public void saveRoomName(Room room, String name) {
        if (TextUtils.isEmpty(name)) {
            view.onSaveRoomNameError(R.string.tip_name_can_not_null);
            return;
        }

        room.mRoomName = name;
        //修改并保存名称
        if (saveName(room)) {
            view.onSaveRoomNameSuccess(room);
        } else {
            view.onSaveRoomNameError(R.string.tip_edit_failed);
        }
    }

    private boolean saveName(Room room) {
        return RoomDAO.getInstance().updateRoom(room);
    }
}
