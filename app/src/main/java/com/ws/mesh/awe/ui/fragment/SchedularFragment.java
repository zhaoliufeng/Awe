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
        timingAdapter = new TimingAdapter(getActivity(), mAlarmBeanList);
        timingList.setLayoutManager(new LinearLayoutManager(getActivity()));
        timingList.setAdapter(timingAdapter);
        timingAdapter.setOnTimingSelectListener(new TimingAdapter.OnTimingSelectListener() {
            @Override
            public void onItemSelect(int position) {

            }

            @Override
            public void onSwitchTiming(boolean isOpen, int position) {
                int alarmId = presenter.mAlarmSparseArray.valueAt(position).mAId;
                //开关定时
                if (isOpen) {
                    presenter.openAlarm(alarmId);
                } else {
                    presenter.closeAlarm(alarmId);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SparseArray<Timing> temp = TimingDAO.getInstance().queryTiming(meshAddress);
        mAlarmBeanList.clear();
        for (int i = 0; i < temp.size(); i++){
            mAlarmBeanList.append(temp.valueAt(i).mAId, temp.valueAt(i));
        }
        timingAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_add_timing)
    public void addTiming() {
        pushActivityWithMeshAddress(TimingEditActivity.class, meshAddress);
    }

    @Override
    public void openAlarm(boolean success) {
        if (success) {
            timingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void closeAlarm(boolean success) {
        if (success) {
            timingAdapter.notifyDataSetChanged();
        }
    }
}
