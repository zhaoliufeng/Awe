package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.adapter.IconTitleGridAdapter;
import com.ws.mesh.awe.ui.impl.IWarmColdView;
import com.ws.mesh.awe.ui.presenter.WarmColdPresenter;
import com.ws.mesh.awe.views.ColorPickView;
import com.ws.mesh.awe.views.CustomSeekBar;

import butterknife.BindView;

public class ModesFragment extends BaseFragment implements IWarmColdView {

    private static final String TAG = "ModesFragment";
    @BindView(R.id.cp_warm_cold)
    ColorPickView cpWarmCold;
    @BindView(R.id.sb_brightness)
    CustomSeekBar sbBrightness;
    @BindView(R.id.recl_modes)
    RecyclerView modesList;

    private int mColdValues;
    private int meshAddress;
    private IconTitleGridAdapter modesGridAdapter;
    private WarmColdPresenter presenter;

    private int[] modeTitle = new int[]{R.string.relax, R.string.reading_mode,
            R.string.concentration, R.string.alert};

    private int[] modeIcon = new int[]{R.drawable.icon_mode_relax, R.drawable.icon_mode_reading_mode,
            R.drawable.icon_mode_concentration, R.drawable.icon_mode_alert};

    private float[] modes = {0.0f, 0.1f, 0.2f, 0.625f};
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modes;
    }

    @Override
    protected void initData(View view) {
        presenter = new WarmColdPresenter(this);
        meshAddress = getActivity().getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);
        presenter.init(meshAddress);
        modesGridAdapter = new IconTitleGridAdapter(modeTitle, modeIcon);
        modesGridAdapter.setOnItemSelectedListener(new IconTitleGridAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                Log.i(TAG, "OnItemSelected: position -> " + position);
                presenter.controlWarmCold((int) (255f * (1 - modes[position])), true, true);
                float hsb[] = new float[]{ 90.0f, 255f * modes[position] / 255.0f, 1.0f };
                cpWarmCold.setPoint(hsb);
            }
        });

        modesList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        modesList.setAdapter(modesGridAdapter);

        cpWarmCold.setBackgroundImg(false);

        cpWarmCold.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(float[] hsb, boolean reqUpdate) {
                //暖白
                mColdValues = (int) (255 * hsb[ 1 ]);
                presenter.controlWarmCold(255 - mColdValues, reqUpdate, true);
                Log.i(TAG, "onColorChange: coldValues -> " + mColdValues);
            }
        });
        sbBrightness.setOnPositionChangedListener(new CustomSeekBar.OnPositionChangedListener() {
            @Override
            public void onNewPosition(float position, boolean isUp) {
                presenter.controlBrightness(isUp, (int) (position * 100));
            }
        });
    }
}
