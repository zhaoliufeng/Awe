package com.ws.mesh.awe.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.ui.adapter.ViewPagerAdapter;
import com.ws.mesh.awe.ui.fragment.DeviceFragment;
import com.ws.mesh.awe.ui.fragment.RoomsFragment;
import com.ws.mesh.awe.ui.impl.IMainView;
import com.ws.mesh.awe.ui.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements IMainView {

    @BindView(R.id.drawer_menu)
    DrawerLayout mMenu;
    @BindView(R.id.vp_list)
    ViewPager viewPager;
    @BindView(R.id.tab_title)
    TabLayout tabLayout;

    MainPresenter presenter;

    private DeviceFragment deviceFragment;
    private RoomsFragment roomsFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        presenter = new MainPresenter(this, this);
        List<BaseFragment> mFragmentList = new ArrayList<>();
        List<String> mTitleList = new ArrayList<>();

        deviceFragment = new DeviceFragment();
        roomsFragment = new RoomsFragment();
        mFragmentList.add(deviceFragment);
        mFragmentList.add(roomsFragment);

        mTitleList.add(getString(R.string.devices));
        mTitleList.add(getString(R.string.rooms_zones));

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), mFragmentList, mTitleList);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.checkBle();
    }

    @OnClick(R.id.img_menu)
    public void OnMenuOpen() {
        mMenu.openDrawer(Gravity.START, true);
    }

    @OnClick(R.id.img_add)
    public void OnAdd(){
        if (viewPager.getCurrentItem() == 1){
            //添加默认房间
            presenter.addDefaultRoom();
        }
    }

    @Override
    public void onFindDevice() {
        toast("正在连接设备");
    }

    @Override
    public void onLoginSuccess() {
        toast("设备连接成功");
    }

    @Override
    public void offline(Device device) {

    }

    @Override
    public void online(SparseArray<Device> sparseArray) {

    }

    @Override
    public void statusUpdate(final Device device) {
        //刷新设备状态
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceFragment.refreshDevice(device);
            }
        });
    }

    @Override
    public void onLoginOut() {

    }

    @Override
    public void updateDevice(SparseArray<Device> deviceSparseArray) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    @Override
    public void addRoom(boolean success) {
        if (success){
            roomsFragment.refreshRoomList();
        }else {
            toast(R.string.add_failed);
        }
    }
}
