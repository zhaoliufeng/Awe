package com.ws.mesh.awe.ui.fragment;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.constant.DeviceChannel;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.adapter.IconTitleGridAdapter;
import com.ws.mesh.awe.ui.impl.IColourView;
import com.ws.mesh.awe.ui.presenter.ColourPresenter;
import com.ws.mesh.awe.utils.DevTypeUtils;
import com.ws.mesh.awe.utils.SendMsg;
import com.ws.mesh.awe.utils.Utils;
import com.ws.mesh.awe.views.ColorPickView;
import com.ws.mesh.awe.views.CustomSeekBar;

import java.util.Objects;

import butterknife.BindView;

public class ColourFragment extends BaseFragment implements IColourView{

    private static final String TAG = "ColourFragment";
    @BindView(R.id.recl_colour)
    RecyclerView colourList;
    @BindView(R.id.cp_colour)
    ColorPickView colorPickView;
    @BindView(R.id.sb_brightness)
    CustomSeekBar sbBrightness;

    private int color;

    private int[] colourTitle = new int[]{R.string.magenta, R.string.yellow, R.string.mauve,
            R.string.teal, R.string.cyan, R.string.blue};

    private int[] colourIcon = new int[]{R.drawable.icon_colour_magenta, R.drawable.icon_colour_yellow, R.drawable.icon_colour_mauve,
            R.drawable.icon_colour_teal, R.drawable.icon_colour_cyan, R.drawable.icon_colour_blue};

    private IconTitleGridAdapter colourGridAdapter;
    private int meshAddress;
    private ColourPresenter presenter;
    private String[] colorSet = {"#EC008C", "#D7DF23", "#8E4D9E", "#2BB673", "#00A79D", "#1B75BC"};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_colour;
    }

    @Override
    protected void initData(View view) {
        presenter = new ColourPresenter(this);
        meshAddress = Objects.requireNonNull(getActivity()).getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);
        presenter.init(meshAddress);

        colourGridAdapter = new IconTitleGridAdapter(colourTitle, colourIcon);
        colourGridAdapter.setOnItemSelectedListener(new IconTitleGridAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                Log.i(TAG, "OnItemSelected: position -> " + position);
                int color = Color.parseColor(colorSet[position]);
                presenter.controlColor(color, true, true);
                colorPickView.setPoint(Utils.rgb2hsb(Color.red(color), Color.green(color), Color.blue(color)));
            }
        });
        colourList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        colourList.setAdapter(colourGridAdapter);
        colorPickView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(float[] hsb, boolean reqUpdate) {
                color = new Utils.UIColor(hsb[ 0 ], hsb[ 1 ], hsb[ 2 ]).ARGB();
                presenter.controlColor(color, reqUpdate, true);
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
