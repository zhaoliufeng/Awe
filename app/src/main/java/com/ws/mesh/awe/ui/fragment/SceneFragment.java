package com.ws.mesh.awe.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseFragment;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.ui.adapter.IconTitleGridAdapter;
import com.ws.mesh.awe.ui.impl.ISceneView;
import com.ws.mesh.awe.ui.presenter.ScenePresenter;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SendMsg;

import butterknife.BindView;

public class SceneFragment extends BaseFragment implements ISceneView{

    private static final String TAG = "SceneFragment";
    @BindView(R.id.rcl_scene_grid)
    RecyclerView sceneList;
    private IconTitleGridAdapter sceneGridAdapter;
    private ScenePresenter presenter;
    private int meshAddress;

    private int[] sceneTitle = new int[]{R.string.scene_sleep, R.string.scene_celebration, R.string.scene_party,
            R.string.scene_movie_time, R.string.scene_candle_light, R.string.scene_date_night,
            R.string.scene_meditation, R.string.scene_nature, R.string.scene_sunset,
            R.string.scene_moonlight, R.string.scene_beach, R.string.scene_visual_alarm};

    private int[] sceneIcon = new int[]{R.drawable.icon_scene_sleep, R.drawable.icon_scene_celebration, R.drawable.icon_scene_party,
            R.drawable.icon_scene_moive_time, R.drawable.icon_scene_candle_light, R.drawable.icon_scene_date_night,
            R.drawable.icon_scene_meditation, R.drawable.icon_scene_nature, R.drawable.icon_scene_sunset,
            R.drawable.icon_scene_moonlight, R.drawable.icon_scene_beach, R.drawable.icon_scene_visual_alarm};

    private int[][] scenes = {{0xFF8000, 0x000000, 0x000000, 0x0000FF,
            0xFFFF00, 0x7E00FE, 0xFF8000, 0x00FF00,
            0x000000, 0xFFFF00, 0x00FFFF, 0x000000},
            {50, 0, 1, 100,
                    100, 100, 100, 100,
                    2, 100, 100, 3}};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene;
    }

    @Override
    protected void initData(View view) {
        presenter = new ScenePresenter(this);
        meshAddress = getActivity().getIntent().getIntExtra(IntentConstant.MESH_ADDRESS, -1);
        presenter.init(meshAddress);
        sceneGridAdapter = new IconTitleGridAdapter(sceneTitle, sceneIcon);
        sceneGridAdapter.setOnItemSelectedListener(new IconTitleGridAdapter.OnItemSelectedListener() {
            @Override
            public void OnItemSelected(final int position) {
                Log.i(TAG, "OnItemSelected: position -> " + position);
                if (scenes[0][position] == 0x000000){
                    //呼吸场景
                    SendMsg.loadBreath(meshAddress, scenes[1][position]);
                }else {
                    //颜色场景
                    presenter.controlColor(scenes[0][position], true, true);
                    CoreData.mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.controlBrightness(true, scenes[1][position]);
                        }
                    }, 300);
                }
            }
        });
        sceneList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        sceneList.setAdapter(sceneGridAdapter);
    }
}
