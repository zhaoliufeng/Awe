package com.ws.mesh.awe.ui.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.activity.TimingEditActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SchedularFragment extends BaseFragment {

    @BindView(R.id.recl_timing)
    RecyclerView timingList;
    @BindView(R.id.btn_add_timing)
    FloatingActionButton btnAddTiming;

    private int meshAddress;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_schedular;
    }

    @Override
    protected void initData(View view) {
        if (getActivity() != null){
            meshAddress = getActivity().getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, 0x00);
        }
    }

    @OnClick(R.id.btn_add_timing)
    public void addTiming(){
        pushActivityWithMeshAddress(TimingEditActivity.class, meshAddress);
    }
}
