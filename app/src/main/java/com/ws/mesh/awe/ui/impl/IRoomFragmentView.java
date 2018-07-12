package com.ws.mesh.awe.ui.impl;

import com.ws.mesh.awe.bean.Room;

public interface IRoomFragmentView {
    void onSaveRoomNameSuccess(Room room);
    void onSaveRoomNameError(int errMsgId);
}
