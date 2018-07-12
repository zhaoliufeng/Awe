package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.ui.adapter.IconTitleGridAdapter;
import com.ws.mesh.awe.views.ColorPickView;
import com.ws.mesh.awe.views.CustomSeekBar;

import butterknife.BindView;

public class ModesFragment extends BaseFragment {

    private static final String TAG = "ModesFragment";
    @BindView(R.id.cp_warm_cold)
    ColorPickView cpWarmCold;
    @BindView(R.id.sb_brightness)
    CustomSeekBar sbBrightness;
    @BindView(R.id.recl_modes)
    RecyclerView modesList;

    private int mColdValues;

    private IconTitleGridAdapter modesGridAdapter;

    private int[] modeTitle = new int[]{R.string.relax, R.string.active, R.string.reading_mode,
            R.string.concentration, R.string.alert};

    private int[] modeIcon = new int[]{R.drawable.icon_mode_relax, R.drawable.icon_mode_active, R.drawable.icon_mode_reading_mode,
            R.drawable.icon_mode_concentration, R.drawable.icon_mode_alert};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modes;
    }

    @Override
    protected void initData(View view) {
        modesGridAdapter = new IconTitleGridAdapter(modeTitle, modeIcon);
        modesGridAdapter.setOnItemSelectedListener(new IconTitleGridAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                Log.i(TAG, "OnItemSelected: position -> " + position);
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
                Log.i(TAG, "onColorChange: coldValues -> " + mColdValues);
            }
        });
    }
}
