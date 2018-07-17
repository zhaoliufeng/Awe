package com.ws.mesh.awe.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.bean.Device;
import com.ws.mesh.awe.constant.DeviceChannel;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.adapter.ViewPagerAdapter;
import com.ws.mesh.awe.ui.fragment.ColourFragment;
import com.ws.mesh.awe.ui.fragment.DeviceFragment;
import com.ws.mesh.awe.ui.fragment.ModesFragment;
import com.ws.mesh.awe.ui.fragment.RoomsFragment;
import com.ws.mesh.awe.ui.fragment.SceneFragment;
import com.ws.mesh.awe.ui.fragment.SchedularFragment;
import com.ws.mesh.awe.utils.CoreData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceContentActivity extends BaseActivity {

    @BindView(R.id.vp_list)
    ViewPager viewPager;
    @BindView(R.id.tab_title)
    TabLayout tabLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;

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
        if (meshAddress > 0x8000){
            tvTitle.setText(CoreData.core().mRoomSparseArray.get(meshAddress).mRoomName);
        }else {
            tvTitle.setText(CoreData.core().mDeviceSparseArray.get(meshAddress).mDevName);
        }

        getDeviceType();

        List<BaseFragment> mFragmentList = new ArrayList<>();
        List<String> mTitleList = new ArrayList<>();

        sceneFragment = new SceneFragment();
        modesFragment = new ModesFragment();
        colourFragment = new ColourFragment();
        schedularFragment = new SchedularFragment();

        mFragmentList.add(sceneFragment);
        mTitleList.add(getString(R.string.scenes));
        mFragmentList.add(schedularFragment);
        mTitleList.add(getString(R.string.schedular));

        if (deviceType == 1){
            mFragmentList.add(modesFragment);
            mTitleList.add(getString(R.string.modes));
        }else if (deviceType == 2){
            mFragmentList.add(colourFragment);
            mTitleList.add(getString(R.string.colour));
        }else {
            mFragmentList.add(modesFragment);
            mFragmentList.add(colourFragment);
            mTitleList.add(getString(R.string.modes));
            mTitleList.add(getString(R.string.colour));
        }


        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this.getSupportFragmentManager(), mFragmentList, mTitleList);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.img_back)
    public void onBack(){
        finish();
    }

    // 0 二路 1 三路 2五路
    private int deviceType = 2;
    private void getDeviceType(){
        if (meshAddress < 0x8000) {
            Device device = CoreData.core().mDeviceSparseArray.get(meshAddress);
            if ((device.mDevType >> 8) == DeviceChannel.LIGHT_WC_TWO_CHANNEL ||
                    (device.mDevType >> 8) == DeviceChannel.LIGHT_ONE_W_CHANNEL ||
                    (device.mDevType >> 8) == DeviceChannel.LIGHT_ONE_C_CHANNEL) {
                deviceType = 0;
            } else if (device.mDevType >> 8 == DeviceChannel.LIGHT_THREE_RGB_CHANNEL){
                deviceType = 1;
            }else {
                deviceType = 2;
            }
        }else {
            deviceType = 2;
        }
    }
}
