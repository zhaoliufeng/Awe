package com.ws.mesh.awe.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.utils.Utils;
import com.ws.mesh.awe.views.SwipeMenuLayout;

/**
 * Created by zhaol on 2017/11/18.
 */

public class TimingAdapter extends RecyclerView.Adapter {
    private SparseArray<Timing> mDatas;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private OnTimingSelectListener mOnTimingSelectListener = new OnTimingSelectListener() {
        @Override
        public void onSwitchTiming(boolean isOpen, int position) {

        }

        @Override
        public void onEdit(int position) {

        }

        @Override
        public void onDelete(int position) {

        }
    };

    public TimingAdapter(SparseArray<Timing> datas) {
        mDatas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mRecyclerView = (RecyclerView) parent;
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_timing, parent, false);
        return new TimingViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TimingViewHolder viewHolder = (TimingViewHolder) holder;
        final Timing alarm = mDatas.valueAt(position);
        String[] timeFormat = Utils.getAlarmShow(alarm.mHours, alarm.mMins);
        viewHolder.mTvTime.setText(timeFormat[0]);
        viewHolder.mTvAmPm.setText(timeFormat[1]);
        //在setCheck之前取消onCheck的监听
        viewHolder.mSwitchExecute.setOnCheckedChangeListener(null);
        viewHolder.mSwitchExecute.setChecked(alarm.mIsOpen);

        String timingEvent = getExecuteEvent(alarm.mAlarmEvent);
        viewHolder.mTvExecuteWeek.setText(String.format("%s,%s", getExecuteInfo(alarm.mWeekNum), timingEvent));

        viewHolder.mSwitchExecute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (viewHolder.mSwitchExecute.isChecked()) {
                    alarm.mIsOpen = true;
                    mOnTimingSelectListener.onSwitchTiming(true, position);
                } else {
                    alarm.mIsOpen = false;
                    mOnTimingSelectListener.onSwitchTiming(false, position);
                }
                if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE
                        && !mRecyclerView.isComputingLayout())
                    notifyDataSetChanged();
            }
        });

        viewHolder.mTvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mSwipeMenu.smoothClose();
                mOnTimingSelectListener.onEdit(position);
            }
        });

        viewHolder.mTvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mSwipeMenu.smoothClose();
                mOnTimingSelectListener.onDelete(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.size();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private class TimingViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTime;
        private TextView mTvExecuteWeek;
        private TextView mTvAmPm;
        private TextView mTvEdit, mTvDelete;
        private SwitchCompat mSwitchExecute;
        private SwipeMenuLayout mSwipeMenu;

        TimingViewHolder(View itemView) {
            super(itemView);
            mTvTime = itemView.findViewById(R.id.tv_execute_time);
            mTvExecuteWeek = itemView.findViewById(R.id.tv_timing_info);
            mTvAmPm = itemView.findViewById(R.id.tv_am_pm);
            mSwitchExecute = itemView.findViewById(R.id.switch_timing);
            mTvEdit = itemView.findViewById(R.id.tv_edit);
            mTvDelete = itemView.findViewById(R.id.tv_delete);
            mSwipeMenu = itemView.findViewById(R.id.swipeMenu);
        }
    }

    //获取执行的时间
    private String getExecuteInfo(int weekNum) {
        if (weekNum == 0)
            return mContext.getString(R.string.never_repeat);
        if (weekNum == 127)
            return mContext.getString(R.string.every_day);
        if (weekNum == 62)
            return mContext.getString(R.string.work_day);

        byte[] weeks = Utils.reverseBytes(Utils.weekNumToBinaryByteArray(weekNum));
        String[] weekString = mContext.getResources().getStringArray(R.array.custom_week_data);
        StringBuilder showString = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if (weeks[i] == 1) {
                showString.append(weekString[i]).append(",");
            }
        }
        return showString.substring(0, showString.length() - 1);
    }

    //获取执行的动作
    private String getExecuteEvent(int eventId) {
        return mContext.getResources().getStringArray(R.array.timing_events)[eventId];
    }

    public interface OnTimingSelectListener {
        void onSwitchTiming(boolean isOpen, int position);

        void onEdit(int position);

        void onDelete(int position);
    }

    public void setOnTimingSelectListener(OnTimingSelectListener listener) {
        this.mOnTimingSelectListener = listener;
    }
}
