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
import com.safaltaclass.plus.model.ForgetRequestBody;
import com.safaltaclass.plus.model.ForgetResponsebody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity implements OnDialogButtonClickListener {

    private Toolbar toolbar;
    private Button btnSubmit;
    private EditText etUserName;
    private TextInputLayout tilUser;
    private ApiInterface apiInterface;
    private ProgressBar pbSubmit;
    private Call<ForgetResponsebody> forgetCall;
    private boolean isBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_forget_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pbSubmit = (ProgressBar) findViewById(R.id.progress_bar);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        etUserName = (EditText) findViewById(R.id.et_username);
        tilUser = (TextInputLayout) findViewById(R.id.textInputEmail);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (SafaltaPlusUtility.getInstance().isConnected(ForgetPassword.this)) {
                        isBack = false;
                        pbSubmit.setVisibility(View.VISIBLE);
                        sendInfo();
                    } else {
                        DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, ForgetPassword.this);
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

    private void sendInfo(){

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        ForgetRequestBody forgetRequestBody = new ForgetRequestBody("forgot", etUserName.getText().toString());
        forgetCall = apiInterface.getForget(forgetRequestBody);
        forgetCall.enqueue(new Callback<ForgetResponsebody>() {

            @Override
            public void onResponse(@NonNull Call<ForgetResponsebody> call, @NonNull Response<ForgetResponsebody> response) {
                if (!response.isSuccessful()) {
                    forgetCall = call.clone();
                    forgetCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbSubmit.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success))) {
                    if (response.body().getStatus() != null && response.body().getStatus() == true && response.body().getUserMsg() != null) {
                        isBack = true;
                        DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.suceess), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_success, false, ForgetPassword.this);
                    } else {
                        DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, ForgetPassword.this);
                    }
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, ForgetPassword.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, ForgetPassword.this);
                    }else{
                        DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, ForgetPassword.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbSubmit.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(ForgetPassword.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, ForgetPassword.this);
            }
        });

    }

    private boolean validation() {
        boolean isvalid = true;
        tilUser.setError("");
        if (etUserName.getText().toString().isEmpty()) {
            tilUser.setError(getString(R.string.email_forget_error));
            isvalid = false;
        }
        return isvalid;
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
