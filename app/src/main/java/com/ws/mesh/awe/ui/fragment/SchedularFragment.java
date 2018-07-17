package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.bean.Timing;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.db.TimingDAO;
import com.ws.mesh.awe.ui.activity.TimingEditActivity;
import com.ws.mesh.awe.ui.adapter.TimingAdapter;
import com.ws.mesh.awe.ui.presenter.ITimingView;
import com.ws.mesh.awe.ui.presenter.SchedularPresenter;

import butterknife.BindView;
import butterknife.OnClick;

public class SchedularFragment extends BaseFragment implements ITimingView {

    @BindView(R.id.recl_timing)
    RecyclerView timingList;
    private SparseArray<Timing> mAlarmBeanList;

    private TimingAdapter timingAdapter;

    private int meshAddress;

    private SchedularPresenter presenter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_schedular;
    }

    @Override
    protected void initData(View view) {
        presenter = new SchedularPresenter(this);
        if (getActivity() != null) {
            meshAddress = getActivity().getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);
        }
        presenter.init(meshAddress);
        mAlarmBeanList = TimingDAO.getInstance().queryTiming(meshAddress);
        timingAdapter = new TimingAdapter(mAlarmBeanList);
        timingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        timingList.setAdapter(timingAdapter);
        timingAdapter.setOnTimingSelectListener(new TimingAdapter.OnTimingSelectListener() {
            @Override
            public void onSwitchTiming(boolean isOpen, int position) {
                int alarmId = mAlarmBeanList.valueAt(position).mAId;
                //开关定时
                if (isOpen) {
                    presenter.openAlarm(alarmId);
                } else {
                    presenter.closeAlarm(alarmId);
                }
            }

            @Override
            public void onEdit(int position) {
                int alarmId = mAlarmBeanList.valueAt(position).mAId;
                pushActivity(TimingEditActivity.class, meshAddress, alarmId);
            }

            @Override
            public void onDelete(int position) {
                int alarmId = mAlarmBeanList.valueAt(position).mAId;
                presenter.deleteAlarm(alarmId);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //每次resume 都从数据库查询
        presenter.getAlarmList();
        SparseArray<Timing> temp = TimingDAO.getInstance().queryTiming(meshAddress);
        mAlarmBeanList.clear();
        for (int i = 0; i < temp.size(); i++){
            mAlarmBeanList.append(temp.valueAt(i).mAId, temp.valueAt(i));
        }
        timingAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_add_timing)
    public void addTiming() {
        pushActivity(TimingEditActivity.class, meshAddress);
    }

    @Override
    public void openAlarm(int id, boolean success) {
        if (success) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timingAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void closeAlarm(boolean success) {
        if (success) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timingAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void deleteAlarm(boolean success) {
        if (success){
            SparseArray<Timing> temp = TimingDAO.getInstance().queryTiming(meshAddress);
            mAlarmBeanList.clear();
            for (int i = 0; i < temp.size(); i++){
                mAlarmBeanList.append(temp.valueAt(i).mAId, temp.valueAt(i));
            }
            timingAdapter.notifyDataSetChanged();
            toast(R.string.delete_success);
        }else {
            toast(R.string.delete_failed);
        }
    }
}
