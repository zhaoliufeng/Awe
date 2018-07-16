package com.ws.mesh.awe.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import com.ws.mesh.awe.R;
import com.ws.mesh.awe.base.BaseActivity;
import com.ws.mesh.awe.bean.Mesh;
import com.ws.mesh.awe.constant.AppConstant;
import com.ws.mesh.awe.constant.IntentConstant;
import com.ws.mesh.awe.db.MeshDAO;
import com.ws.mesh.awe.ui.impl.ILoginView;
import com.ws.mesh.awe.ui.presenter.LoginPresenter;
import com.ws.mesh.awe.utils.CoreData;
import com.ws.mesh.awe.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements ILoginView{

    @BindView(R.id.user_account)
    EditText mEdtAccount;
    @BindView(R.id.user_password)
    EditText mEdtPassword;

    private LoginPresenter presenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {
        presenter = new LoginPresenter(this);
    }

    @OnClick(R.id.btn_login)
    public void onLogin(){
        String account = mEdtAccount.getText().toString().trim();
        String password = mEdtPassword.getText().toString().trim();
        presenter.addMesh(account, password);
    }

    @Override
    public void addMesh(boolean success) {
        if (success){
            startActivity(new Intent(this, MainActivity.class).putExtra(IntentConstant.NEED_ID, true));
            finish();
        }else {
            toast(R.string.login_failed);
        }
    }

    @Override
    public void onError(int errMsg) {
        toast(errMsg);
    }
}
