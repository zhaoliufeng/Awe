package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.ui.adapter.IconTitleGridAdapter;

import butterknife.BindView;

public class SceneFragment extends BaseFragment {

    private static final String TAG = "SceneFragment";
    @BindView(R.id.rcl_scene_grid)
    RecyclerView sceneList;
    private IconTitleGridAdapter sceneGridAdapter;

    private int[] sceneTitle = new int[]{R.string.scene_sleep, R.string.scene_celebration, R.string.scene_party,
            R.string.scene_movie_time, R.string.scene_candle_light, R.string.scene_date_night,
            R.string.scene_meditation, R.string.scene_nature, R.string.scene_sunset,
            R.string.scene_moonlight, R.string.scene_beach, R.string.scene_visual_alarm};

    private int[] sceneIcon = new int[]{R.drawable.icon_scene_sleep, R.drawable.icon_scene_celebration, R.drawable.icon_scene_party,
            R.drawable.icon_scene_moive_time, R.drawable.icon_scene_candle_light, R.drawable.icon_scene_date_night,
            R.drawable.icon_scene_meditation, R.drawable.icon_scene_nature, R.drawable.icon_scene_sunset,
            R.drawable.icon_scene_moonlight, R.drawable.icon_scene_beach, R.drawable.icon_scene_visual_alarm};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene;
    }

    @Override
    protected void initData(View view) {
        sceneGridAdapter = new IconTitleGridAdapter(sceneTitle, sceneIcon);
        sceneGridAdapter.setOnItemSelectedListener(new IconTitleGridAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                Log.i(TAG, "OnItemSelected: position -> " + position);
            }
        });
        sceneList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        sceneList.setAdapter(sceneGridAdapter);
    }
}
