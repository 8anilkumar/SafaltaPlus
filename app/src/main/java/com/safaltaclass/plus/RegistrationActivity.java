package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.material.textfield.TextInputLayout;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.RegisterRequestBody;
import com.safaltaclass.plus.model.RegisterResponseBody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private Toolbar toolbar;
    private Button btnRegister;
    private EditText etUserName;
    private EditText etEmail;
    private EditText etPhone;
    private TextInputLayout tilUser;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private ApiInterface apiInterface;
    private ProgressBar pbRegister;
    private Call<RegisterResponseBody> registerCall;
    private boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_registration);

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pbRegister = (ProgressBar) findViewById(R.id.progress_bar);
        btnRegister = (Button) findViewById(R.id.btn_register);
        etUserName = (EditText) findViewById(R.id.et_username);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPhone = (EditText) findViewById(R.id.et_phone);
        tilUser = (TextInputLayout) findViewById(R.id.textInputUsername);
        tilEmail = (TextInputLayout) findViewById(R.id.textInputEmail);
        tilPhone = (TextInputLayout) findViewById(R.id.textInputPhone);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (SafaltaPlusUtility.getInstance().isConnected(RegistrationActivity.this)) {
                        isBack = false;
                        pbRegister.setVisibility(View.VISIBLE);
                        userRegister();
                    } else {
                        DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, RegistrationActivity.this);
                    }
                }
            }
        });
    }

    private void userRegister(){

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        RegisterRequestBody registerRequestBody = new RegisterRequestBody(etUserName.getText().toString(),etEmail.getText().toString(),etPhone.getText().toString());
        registerCall = apiInterface.getRegister(registerRequestBody);
        registerCall.enqueue(new Callback<RegisterResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<RegisterResponseBody> call, @NonNull Response<RegisterResponseBody> response) {
                if (!response.isSuccessful()) {
                    registerCall = call.clone();
                    registerCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbRegister.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success))) {
                    if (response.body().getStatus() != null && response.body().getStatus() == true && response.body().getUserMsg() != null) {
                        isBack = true;
                        DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.suceess), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_success, false, RegistrationActivity.this);
                    } else {
                        DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, RegistrationActivity.this);
                    }
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, RegistrationActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, RegistrationActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, RegistrationActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbRegister.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(RegistrationActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, RegistrationActivity.this);
            }
        });

    }

    private boolean validation() {
        boolean isvalid = true;
        tilUser.setError("");
        tilEmail.setError("");
        tilPhone.setError("");
        if (etUserName.getText().toString().isEmpty()) {
            tilUser.setError(getString(R.string.user_login_error));
            isvalid = false;
        }
        if (etEmail.getText().toString().isEmpty()) {
            tilEmail.setError(getString(R.string.email_error));
            isvalid = false;
        }
        if (etPhone.getText().toString().isEmpty()) {
            tilPhone.setError(getString(R.string.phone_error));
            isvalid = false;
        }
        return isvalid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveButtonClicked() {
        if (isBack)
            finish();
    }

    @Override
    public void onNegativeButtonClicked() {
        if (isBack)
            finish();
    }

    @Override
    public void onErrorButtonClicked() {
        finishAffinity();
        System.exit(0);
    }
}
