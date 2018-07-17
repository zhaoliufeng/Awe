package com.ws.mesh.awe.ui.fragment;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.bean.Room;
import com.ws.mesh.awe.ui.activity.DeviceContentActivity;
import com.ws.mesh.awe.ui.activity.GroupEditDevActivity;
import com.ws.mesh.awe.ui.adapter.RoomAdapter;
import com.ws.mesh.awe.ui.impl.IRoomFragmentView;
import com.ws.mesh.awe.ui.presenter.RoomPresenter;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;

import butterknife.BindView;

public class RoomsFragment extends BaseFragment implements IRoomFragmentView{

    @BindView(R.id.rl_rooms_list)
    RecyclerView mRlRoomsList;

    private RoomAdapter roomAdapter;
    private RoomPresenter presenter;

    private AlertDialog renameDialog;
    private PopupWindow pop;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rooms;
    }

    @Override
    protected void initData(View view) {
        presenter = new RoomPresenter(this);
        roomAdapter = new RoomAdapter(CoreData.core().mRoomSparseArray);

        roomAdapter.setOnRoomSelectListener(onRoomSelectListener);
        mRlRoomsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRlRoomsList.setAdapter(roomAdapter);
    }

    public void refreshRoomList(){
        roomAdapter.notifyDataSetChanged();
    }

    private RoomAdapter.OnRoomSelectListener onRoomSelectListener =
            new RoomAdapter.OnRoomSelectListener() {
                @Override
                public void onSet(int position) {
                    popWindow(getView(), position);
                }

                @Override
                public void onSwitch(int position, boolean isOn) {
                    Room room = CoreData.core().mRoomSparseArray.valueAt(position);
                    SendMsg.switchDevice(room.mRoomId, isOn);
                }

                @Override
                public void onEdit(int position) {
                    Room room = CoreData.core().mRoomSparseArray.valueAt(position);
                    pushActivity(DeviceContentActivity.class, room.mRoomId);
                }
            };

    //弹窗
    private void popWindow(View v, final int position) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_room_layout, null);
        //设置屏幕的高度和宽度
        pop = new PopupWindow(view, getScreenWidth(getActivity()),
                (int) (getScreenHeight(getActivity()) / 3.6f));
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.pop_background));
        pop.setOutsideTouchable(true);
        pop.setAnimationStyle(R.style.PopupWindow_anim_style);
        pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        final Room room = CoreData.core().mRoomSparseArray.valueAt(position);
        TextView tvRename, tvDelete, tvSetColor,tvEditRoom;
        tvRename = view.findViewById(R.id.tv_rename);
        tvDelete = view.findViewById(R.id.tv_delete);
        tvSetColor = view.findViewById(R.id.tv_set_color);
        tvEditRoom = view.findViewById(R.id.tv_edit_room);
        tvRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popRenameDialog(room);
                pop.dismiss();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.deleteRoom(room);
                pop.dismiss();
            }
        });

        tvSetColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushActivity(DeviceContentActivity.class, room.mRoomId);
                pop.dismiss();
            }
        });

        tvEditRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到编辑房间界面
                pushActivity(GroupEditDevActivity.class, room.mRoomId);
                pop.dismiss();
            }
        });
    }

    private static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    private static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    private void popRenameDialog(final Room room) {
        renameDialog = new AlertDialog.Builder(getActivity(), R.style.CustomDialog).create();
        renameDialog.show();
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_rename);
            renameDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            renameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            //获取控件绑定 并初始化值
            final EditText edtNewDeviceName = window.findViewById(R.id.edt_edit_input);
            TextView tvTitle = window.findViewById(R.id.tv_edit_title);
            tvTitle.setText(R.string.rename_room);
            if (room != null)
                edtNewDeviceName.setHint(room.mRoomName);
            edtNewDeviceName.setFocusable(true);
            edtNewDeviceName.setFocusableInTouchMode(true);

            CoreData.mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    edtNewDeviceName.requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }, 100);

            Button confirm = window.findViewById(R.id.btn_confirm);
            Button cancel = window.findViewById(R.id.btn_cancel);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String name = edtNewDeviceName.getText().toString().trim();
                    presenter.updateRoomName(room, name);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renameDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onUpdateRoomNameSuccess(Room room) {
        toast(R.string.tip_edit_success);
        roomAdapter.refreshRoom(room);
        renameDialog.dismiss();
    }

    @Override
    public void onUpdateRoomNameError(int errMsgId) {
        toast(errMsgId);
    }

    @Override
    public void onDeleteRoom(Room room, boolean success) {
        if (success){
            toast(R.string.delete_success);
            roomAdapter.notifyDataSetChanged();
        }else {
            toast(R.string.delete_failed);
        }
    }
}
