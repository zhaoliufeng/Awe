package com.ws.mesh.awe.ui.activity;

import android.widget.TextView;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.constant.IntentConstant;

import butterknife.BindView;
import butterknife.OnClick;

public class InfoActivity extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView tvContent;

    private final int ABOUT_AWE = 0;
    private final int ABOUT_US = 1;
    private final int SUPPORT = 2;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_info;
    }

    @Override
    protected void initData() {
        int typeId = getIntent().getIntExtra(IntentConstant.NEED_ID, 0);
        switch (typeId){
            case ABOUT_AWE:
                tvContent.setText(R.string.about_awe_content);
                break;
            case ABOUT_US:
                tvContent.setText(R.string.about_us_content);
                break;
            case SUPPORT:
                tvContent.setText(R.string.support_content);
                break;
        }
    }

    @OnClick(R.id.img_back)
    public void OnBack(){
        finish();
    }
}
