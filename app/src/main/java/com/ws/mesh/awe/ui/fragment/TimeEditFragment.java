package com.ws.mesh.awe.ui.fragment;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;

import butterknife.BindView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeEditFragment extends BaseFragment {

    @BindView(R.id.tp_time)
    TimePicker tpTime;
    @BindView(R.id.rl_never_repeat)
    RelativeLayout rlNeverRepeat;
    @BindView(R.id.rl_every_day)
    RelativeLayout rlEveryDay;
    @BindView(R.id.rl_work_day)
    RelativeLayout rlWorkDay;
    @BindView(R.id.rl_custom)
    RelativeLayout rlCustom;
    @BindView(R.id.iv_never_repeat)
    ImageView ivNeverRepeat;
    @BindView(R.id.iv_every_day)
    ImageView ivEveryDay;
    @BindView(R.id.iv_work_day)
    ImageView ivWorkDay;
    @BindView(R.id.iv_custom)
    ImageView ivCustom;
    @BindView(R.id.rl_choose_events)
    RelativeLayout rlChooseEvents;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_time_edit;
    }

    @Override
    protected void initData(View view) {

    }
}
