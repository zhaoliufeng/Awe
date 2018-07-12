package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;

import butterknife.BindView;

public class SchedularFragment extends BaseFragment {

    @BindView(R.id.recl_timing)
    RecyclerView timingList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_schedular;
    }

    @Override
    protected void initData(View view) {

    }
}
