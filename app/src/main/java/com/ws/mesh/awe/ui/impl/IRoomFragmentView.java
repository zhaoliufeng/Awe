package com.ws.mesh.awe.ui.impl;

import com.ws.mesh.awe.bean.Room;

public interface IRoomFragmentView {
    void onUpdateRoomNameSuccess(Room room);

    void onUpdateRoomNameError(int errMsgId);

    void onDeleteRoom(Room room, boolean success);
}
