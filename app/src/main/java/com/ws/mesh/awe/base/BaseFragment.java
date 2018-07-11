package com.ws.mesh.awe.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ws.mesh.awe.MeshApplication;

import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    /**
     * 获取当前绑定的视图id
     **/
    protected abstract int getLayoutId();

    /**
     * 初始化数据
     **/
    protected abstract void initData(View view);
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), null);
        unbinder = ButterKnife.bind(this, view);
        initData(view);
        return view;
    }

    public void pushActivity(Class<?> clazz) {
        startActivity(new Intent(getActivity(), clazz));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void toast(final String s) {
        if (isAdded()) {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MeshApplication.getInstance(), s, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void toast(int sId){
        toast(getString(sId));
    }
}
