package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.LoginRequestBody;
import com.safaltaclass.plus.model.LoginResponseBody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private Button btnLogin;
    private EditText etUserName;
    private EditText etPassword;
    private TextInputLayout tilUser;
    private TextInputLayout tilPassword;
    private ApiInterface apiInterface;
    private ProgressBar pbLogin;
    private Toolbar toolbar;
    private Call<LoginResponseBody> loginCall;
    private boolean isLogin = false;
    private TextView tvRegister;
    private TextView tvForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnLogin = (Button) findViewById(R.id.btn_login);
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tilUser = (TextInputLayout) findViewById(R.id.textInputEmail);
        tilPassword = (TextInputLayout) findViewById(R.id.textInputPassword);
        pbLogin = (ProgressBar) findViewById(R.id.progress_bar);
        tvRegister = (TextView)findViewById(R.id.tv_register);
        tvForgetPassword = (TextView)findViewById(R.id.tv_forget_password);

        /*KatexView ketex = (KatexView)findViewById(R.id.katex_text);
        ketex.setText("$$ c = \\pm\\sqrt{a^2 + b^2} $$");*/


        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPassword.class);
                startActivity(intent);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (SafaltaPlusUtility.getInstance().isConnected(LoginActivity.this)) {
                        isLogin =false;
                        pbLogin.setVisibility(View.VISIBLE);
                        checkLogin();
                    } else {
                        DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, LoginActivity.this);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkLogin() {
        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        LoginRequestBody loginRequestBody = new LoginRequestBody(etUserName.getText().toString(), etPassword.getText().toString());
        loginCall = apiInterface.getLogin(loginRequestBody);
        loginCall.enqueue(new Callback<LoginResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<LoginResponseBody> call, @NonNull Response<LoginResponseBody> response) {
                if (!response.isSuccessful()) {
                    loginCall = call.clone();
                    loginCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbLogin.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success))) {
                    if (response.body().getStatus() != null && response.body().getStatus() && response.body().getUser() != null && response.body().getUser().size() > 0) {
                        if (SafaltaPlusPreferences.getInstance().saveLoginData(response.body().getUser().get(0).getKey(), response.body().getUser().get(0).getType(), response.body().getUser().get(0).getName(), response.body().getUser().get(0).getEmail(), response.body().getUser().get(0).getMobile())) {
                            isLogin = true;
                            DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.suceess), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_success, false, LoginActivity.this);
                        }
                    } else {
                        isLogin = false;
                        DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, LoginActivity.this);
                    }
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    isLogin = false;
                    DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, LoginActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, LoginActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, LoginActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbLogin.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(LoginActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, LoginActivity.this);
            }
        });
    }

    private boolean validation() {
        boolean isvalid = true;
        tilUser.setError("");
        tilPassword.setError("");
        if (etUserName.getText().toString().isEmpty()) {
            tilUser.setError(getString(R.string.user_login_error));
            isvalid = false;
        }
        if (etPassword.getText().toString().isEmpty()) {
            tilPassword.setError(getString(R.string.password_login_error));
            isvalid = false;
        }
        return isvalid;
    }

    @Override
    public void onPositiveButtonClicked() {
        if (isLogin) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onNegativeButtonClicked() {
        if (isLogin) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onErrorButtonClicked() {
        finishAffinity();
        System.exit(0);
    }

}
