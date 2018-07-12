package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.ui.adapter.IconTitleGridAdapter;

import butterknife.BindView;

public class ColourFragment extends BaseFragment {

    private static final String TAG = "ColourFragment";
    @BindView(R.id.recl_colour)
    RecyclerView colourList;

    private int[] colourTitle = new int[]{R.string.red, R.string.yellow, R.string.green,
            R.string.cyan, R.string.blue, R.string.purple};

    private int[] colourIcon = new int[]{R.drawable.icon_colour_red, R.drawable.icon_colour_yellow, R.drawable.icon_colour_green,
            R.drawable.icon_colour_cyan, R.drawable.icon_colour_blue, R.drawable.icon_colour_purple};

    private IconTitleGridAdapter colourGridAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_colour;
    }

    @Override
    protected void initData(View view) {
        colourGridAdapter = new IconTitleGridAdapter(colourTitle, colourIcon);
        colourGridAdapter.setOnItemSelectedListener(new IconTitleGridAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                Log.i(TAG, "OnItemSelected: position -> " + position);
            }
        });
        colourList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        colourList.setAdapter(colourGridAdapter);
    }
}
