package com.ws.mesh.awe.ui.activity;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.constant.DeviceChannel;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.impl.ITimingEditView;
import com.ws.mesh.awe.ui.presenter.TimingEditPresenter;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.Utils;
import com.ws.mesh.awe.utils.ViewUtils;
import com.ws.mesh.awe.views.DividerGridItemDecoration;

import butterknife.BindView;
import butterknife.OnClick;

public class TimingEditActivity extends BaseActivity implements ITimingEditView {

    @BindView(R.id.tp_time)
    TimePicker tpTime;
    @BindView(R.id.iv_never_repeat)
    ImageView ivNeverRepeat;
    @BindView(R.id.iv_every_day)
    ImageView ivEveryDay;
    @BindView(R.id.iv_work_day)
    ImageView ivWorkDay;
    @BindView(R.id.iv_custom)
    ImageView ivCustom;
    @BindView(R.id.tv_custom_detail)
    TextView customDetail;
    @BindView(R.id.tv_events)
    TextView tvEvents;
    TimingEditPresenter presenter;

    private int hour, minutes;
    //执行周期
    private int weekNum;
    private byte weekBytes[];
    private String[] customListWeekData;
    private String[] listEventsData;
    private int alarmId;
    private int meshAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_time_edit;
    }

    @Override
    protected void initData() {
        presenter = new TimingEditPresenter(this, this);

        alarmId = getIntent().getIntExtra(IntentConstant.ALARM_ID, -1);
        meshAddress = getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);
        presenter.init(meshAddress);

        if (alarmId != -1){
            //编辑定时
            Timing timing = presenter.mAlarmSparseArray.get(alarmId);
            weekNum = timing.mWeekNum;
            hour = timing.mHours;
            minutes = timing.mMins;
            eventId = timing.mAlarmEvent;
            position = eventId;

            tpTime.setCurrentHour(hour);
            tpTime.setCurrentMinute(minutes);
            tvEvents.setText(presenter.getExecuteEvent(eventId));
        }else {
            hour = tpTime.getCurrentHour();
            minutes = tpTime.getCurrentMinute();
        }

        tpTime.setOnTimeChangedListener(onTimeChangedListener);
        if (weekNum == 0) {
            ivNeverRepeat.setImageResource(R.drawable.icon_single_selected);
        } else if (weekNum == 127) {
            ivEveryDay.setImageResource(R.drawable.icon_single_selected);
        } else if (weekNum == 62) {
            ivWorkDay.setImageResource(R.drawable.icon_single_selected);
        } else {
            ivCustom.setImageResource(R.drawable.icon_single_selected);
        }
    }

    @OnClick(R.id.tv_confirm)
    void OnConfirm(){
        //确定添加定时
        presenter.addAlarm(hour, minutes, eventId, weekNum, meshAddress, alarmId);
    }

    @OnClick(R.id.img_back)
    void OnBack(){
        finish();
    }

    @OnClick({R.id.rl_never_repeat, R.id.rl_every_day, R.id.rl_work_day, R.id.rl_custom})
    void OnRepeatSelected(View vew) {
        clearSelectedState();
        switch (vew.getId()) {
            case R.id.rl_never_repeat:
                ivNeverRepeat.setImageResource(R.drawable.icon_single_selected);
                weekNum = 0;
                break;
            case R.id.rl_every_day:
                ivEveryDay.setImageResource(R.drawable.icon_single_selected);
                weekNum = 127;
                break;
            case R.id.rl_work_day:
                ivWorkDay.setImageResource(R.drawable.icon_single_selected);
                weekNum = 62;
                break;
            case R.id.rl_custom:
                ivCustom.setImageResource(R.drawable.icon_single_selected);
                showCustomWeekDialog();
                break;
        }
    }

    @OnClick(R.id.rl_choose_events)
    void OnChooseEvents(){
        showEventsDialog();
    }

    //清空所有重复选项的选中状态
    private void clearSelectedState() {
        ivNeverRepeat.setImageResource(R.drawable.icon_single_unselected);
        ivEveryDay.setImageResource(R.drawable.icon_single_unselected);
        ivWorkDay.setImageResource(R.drawable.icon_single_unselected);
        ivCustom.setImageResource(R.drawable.icon_single_unselected);
    }

    //时间选择监听
    TimePicker.OnTimeChangedListener onTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            minutes = minute;
        }
    };

    private void showCustomWeekDialog() {
        weekBytes = Utils.reverseBytes(Utils.weekNumToBinaryByteArray(weekNum));
        final AlertDialog mAlertDialog = new AlertDialog.Builder(this, R.style.CustomDialog).create();
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        if (customListWeekData == null)
            customListWeekData = getResources().getStringArray(R.array.custom_week_data);
        if (window != null) {
            window.setContentView(R.layout.dialog_list_style);
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            RecyclerView listContentView = window.findViewById(R.id.list_content);
            AlarmCustomWeekAdapter alarmWeekAdapter = new AlarmCustomWeekAdapter();
            listContentView.setLayoutManager(new LinearLayoutManager(this));
            listContentView.addItemDecoration(new DividerGridItemDecoration(this, (int) ViewUtils.dp2px(this, 0.5), 0x99999999));
            listContentView.setAdapter(alarmWeekAdapter);
            ((TextView) window.findViewById(R.id.dialog_custom_title)).setText(R.string.custom_week);
            Button confirm = window.findViewById(R.id.btn_confirm);
            Button cancel = window.findViewById(R.id.btn_cancel);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weekNum = Utils.byteArrayToWeekNum(Utils.reverseBytes(weekBytes));
                    customDetail.setText(presenter.getExecuteInfo(weekNum));
                    mAlertDialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void addAlarm(boolean success) {
        if (success){
            toast(R.string.add_timing_success);
            finish();
        }else {
            toast(R.string.add_timing_failed);
        }
    }

    @Override
    public void maximumNumber() {
        toast(R.string.cannot_add_alarm_anymore);
    }

    class AlarmCustomWeekAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_timing_event, null);
            return new AlarmEventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            AlarmEventViewHolder alarmEventViewHolder = (AlarmEventViewHolder) holder;
            alarmEventViewHolder.mAlarmEventName.setText(customListWeekData[position]);
            if (weekBytes[position] == 1) {
                alarmEventViewHolder.mAlarmEventSelectedStatus.setImageResource(R.drawable.icon_single_selected);
            } else {
                alarmEventViewHolder.mAlarmEventSelectedStatus.setImageResource(R.drawable.icon_single_unselected);
            }
            alarmEventViewHolder.mAlarmEventSelectedStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weekBytes[position] = (weekBytes[position] == 1 ? (byte) 0 : (byte) 1);
                    notifyDataSetChanged();
                }
            });

            alarmEventViewHolder.mWeekFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weekBytes[position] = (weekBytes[position] == 1 ? (byte) 0 : (byte) 1);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            if (customListWeekData == null) return 0;
            return customListWeekData.length;
        }

    }

    class AlarmEventViewHolder extends RecyclerView.ViewHolder {
        TextView mAlarmEventName;
        ImageView mAlarmEventSelectedStatus;
        RelativeLayout mWeekFrame;

        AlarmEventViewHolder(View itemView) {
            super(itemView);
            mAlarmEventName = itemView.findViewById(R.id.tv_timing_event_name);
            mAlarmEventSelectedStatus = itemView.findViewById(R.id.img_timing_event_selected_status);
            mWeekFrame = itemView.findViewById(R.id.rl_week_frame);
        }


    }

    private int choosePosition = 0;
    private int position;
    private int eventId = 0;

    private void showEventsDialog() {
        final AlertDialog mAlertDialog = new AlertDialog.Builder(this, R.style.CustomDialog).create();
        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        choosePosition = position;
        if (listEventsData == null) {
            if (meshAddress < 0x8000) {
                Device device = CoreData.core().mDeviceSparseArray.get(meshAddress);
                if ((device.mDevType >> 8) == DeviceChannel.LIGHT_WC_TWO_CHANNEL ||
                        (device.mDevType >> 8) == DeviceChannel.LIGHT_ONE_W_CHANNEL ||
                        (device.mDevType >> 8) == DeviceChannel.LIGHT_ONE_C_CHANNEL) {
                    listEventsData = getResources().getStringArray(R.array.alarm_warm_event_data);
                } else {
                    listEventsData = getResources().getStringArray(R.array.timing_events);
                }
            } else {
                listEventsData = getResources().getStringArray(R.array.timing_events);
            }
        }

        if (window != null) {
            window.setContentView(R.layout.dialog_fixed_height);
            mAlertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            RecyclerView listContentView = window.findViewById(R.id.list_content);
            final AlarmEventAdapter alarmEventAdapter = new AlarmEventAdapter();
            listContentView.setLayoutManager(new LinearLayoutManager(this));
            listContentView.addItemDecoration(new DividerGridItemDecoration(this, (int) ViewUtils.dp2px(this, 0.5), 0x99999999));
            listContentView.setAdapter(alarmEventAdapter);
            Button confirm = window.findViewById(R.id.btn_confirm);
            Button cancel = window.findViewById(R.id.btn_cancel);
            TextView tvTitle = window.findViewById(R.id.dialog_custom_title);
            tvTitle.setText(R.string.choose_event);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = choosePosition;
                    setEventName();
                    eventId = position;
                    mAlertDialog.dismiss();
                }

                private void setEventName() {
                    if (position == -1) {
                        tvEvents.setText("");
                    } else {
                        tvEvents.setText(listEventsData[position]);
                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAlertDialog.dismiss();
                }
            });
        }
    }

    private class AlarmEventAdapter extends RecyclerView.Adapter<AlarmEventAdapter.AlarmEventViewHolder> {

        @Override
        public AlarmEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_timing_event, null);
            return new AlarmEventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AlarmEventViewHolder holder, final int position) {
            holder.mAlarmEventSelectedStatus.setImageResource(
                    choosePosition == position ? R.drawable.icon_single_selected : R.drawable.icon_single_unselected);
            holder.mAlarmEventName.setText(listEventsData[position]);

            holder.mAlarmEventSelectedStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choosePosition = position;
                    notifyDataSetChanged();
                }
            });
            holder.mWeekFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choosePosition = position;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            if (listEventsData == null) return 0;
            return listEventsData.length;
        }

        class AlarmEventViewHolder extends RecyclerView.ViewHolder {
            TextView mAlarmEventName;
            ImageView mAlarmEventSelectedStatus;
            RelativeLayout mWeekFrame;

            AlarmEventViewHolder(View itemView) {
                super(itemView);
                mAlarmEventName = itemView.findViewById(R.id.tv_timing_event_name);
                mAlarmEventSelectedStatus = itemView.findViewById(R.id.img_timing_event_selected_status);
                mWeekFrame = itemView.findViewById(R.id.rl_week_frame);
            }
        }
    }
}
