package com.ws.mesh.awe.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.adapter.ViewPagerAdapter;
import com.ws.mesh.awe.ui.fragment.ColourFragment;
import com.ws.mesh.awe.ui.fragment.DeviceFragment;
import com.ws.mesh.awe.ui.fragment.ModesFragment;
import com.ws.mesh.awe.ui.fragment.RoomsFragment;
import com.ws.mesh.awe.ui.fragment.SceneFragment;
import com.ws.mesh.awe.ui.fragment.SchedularFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceContentActivity extends BaseActivity {

    @BindView(R.id.vp_list)
    ViewPager viewPager;
    @BindView(R.id.tab_title)
    TabLayout tabLayout;

    private SceneFragment sceneFragment;
    private ModesFragment modesFragment;
    private ColourFragment colourFragment;
    private SchedularFragment schedularFragment;

    private int meshAddress;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_content;
    }

    @Override
    protected void initData() {
        meshAddress = getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);
        List<BaseFragment> mFragmentList = new ArrayList<>();
        List<String> mTitleList = new ArrayList<>();

        sceneFragment = new SceneFragment();
        modesFragment = new ModesFragment();
        colourFragment = new ColourFragment();
        schedularFragment = new SchedularFragment();

        mFragmentList.add(sceneFragment);
        mFragmentList.add(modesFragment);
        mFragmentList.add(colourFragment);
        mFragmentList.add(schedularFragment);

        mTitleList.add(getString(R.string.scenes));
        mTitleList.add(getString(R.string.modes));
        mTitleList.add(getString(R.string.colour));
        mTitleList.add(getString(R.string.schedular));

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), mFragmentList, mTitleList);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.img_back)
    public void onBack(){
        finish();
    }
}
