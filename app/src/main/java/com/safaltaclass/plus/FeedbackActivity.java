package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.EnquiryRequest;
import com.safaltaclass.plus.model.EnquiryResponse;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private Toolbar toolbar;
    private EditText etName;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etmessage;
    private Spinner spinner;
    private Button btnSubmit;
    private ProgressBar pbFeedback;
    private ApiInterface apiInterface;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilMesaage;
    //private DialogsUtil dialogsUtil;
    private Call<EnquiryResponse> supportCall;
    private List<String> errorKeyList = new ArrayList<>();
    private boolean isBack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_feedback);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.support));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);


        pbFeedback = (ProgressBar) findViewById(R.id.progress_bar);
        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etmessage = (EditText) findViewById(R.id.et_message);
        spinner = (Spinner) findViewById(R.id.spinner_feedback_type);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        tilName = (TextInputLayout) findViewById(R.id.til_name);
        tilEmail = (TextInputLayout) findViewById(R.id.til_email);
        tilPhone = (TextInputLayout) findViewById(R.id.til_phone);
        tilMesaage = (TextInputLayout) findViewById(R.id.til_message);

        if (!SafaltaPlusPreferences.getInstance().getName().isEmpty()) {
            etName.setEnabled(false);
            etName.setText(SafaltaPlusPreferences.getInstance().getName());
        }

        if (!SafaltaPlusPreferences.getInstance().getEmail().isEmpty()) {
            etEmail.setEnabled(false);
            etEmail.setText(SafaltaPlusPreferences.getInstance().getEmail());
        }

        if (!SafaltaPlusPreferences.getInstance().getMobile().isEmpty()) {
            etPhone.setText(SafaltaPlusPreferences.getInstance().getMobile());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validation()) {
                    if (etmessage.length() > 15) {
                        if (SafaltaPlusUtility.getInstance().isConnected(FeedbackActivity.this)) {
                            isBack = false;
                            pbFeedback.setVisibility(View.VISIBLE);
                            sendFeedbackData();
                        } else {
                            DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, FeedbackActivity.this);
                        }
                    } else {
                        DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.warning), getString(R.string.message_length), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, FeedbackActivity.this);
                    }
                }
            }
        });


        errorKeyList = feedbackErrorKeys();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void sendFeedbackData() {
        EnquiryRequest enquiryRequest = new EnquiryRequest(etName.getText().toString(), etEmail.getText().toString(), etPhone.getText().toString(), errorKeyList.get(spinner.getSelectedItemPosition()), etmessage.getText().toString());
        supportCall = apiInterface.getEnquiry(enquiryRequest);
        supportCall.enqueue(new Callback<EnquiryResponse>() {

            @Override
            public void onResponse(@NonNull Call<EnquiryResponse> call, @NonNull Response<EnquiryResponse> response) {
                if (!response.isSuccessful()) {
                    supportCall = call.clone();
                    supportCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;


                pbFeedback.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success))) {
                    if (response.body().getStatus() != null && response.body().getStatus() == true && response.body().getData() != null) {
                        isBack = true;
                        DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.suceess), response.body().getData().getTicketNo(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_success, false, FeedbackActivity.this);
                    } else {
                        DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, FeedbackActivity.this);
                    }
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, FeedbackActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, FeedbackActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, FeedbackActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbFeedback.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(FeedbackActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, FeedbackActivity.this);
            }
        });
    }

    private boolean validation() {
        boolean isvalid = true;
        tilName.setError("");
        tilEmail.setError("");
        tilPhone.setError("");
        tilMesaage.setError("");
        if (etName.getText().toString().isEmpty()) {
            tilName.setError(getString(R.string.name_error));
            isvalid = false;
        }
        if (etEmail.getText().toString().isEmpty()) {
            tilEmail.setError(getString(R.string.email_error));
            isvalid = false;
        } else if ((!isValidMail(etEmail.getText().toString()))) {
            tilEmail.setError(getString(R.string.invalid_email));
            isvalid = false;
        }
        if (etPhone.getText().toString().isEmpty()) {
            tilPhone.setError(getString(R.string.phone_error));
            isvalid = false;
        } else if ((!isValidMobile(etPhone.getText().toString()))) {
            tilPhone.setError(getString(R.string.invalid_phone));
            isvalid = false;
        }
        if (etmessage.getText().toString().isEmpty()) {
            tilMesaage.setError(getString(R.string.message_error));
            isvalid = false;
        }
        return isvalid;
    }

    private List<String> feedbackErrorKeys() {
        List<String> list = new ArrayList<>();
        list.add("LMS");
        list.add("AVA");
        list.add("APP");
        list.add("INCORRECT-DETAILS");
        list.add("FEEDBACK-CENTER");
        list.add("FEEDBACK-FACULTY");
        list.add("FEEDBACK-APP");
        list.add("FEEDBACK-WEBSITE");
        list.add("OTHER");
        return list;
    }

    @Override
    public void onPositiveButtonClicked() {
        if (isBack)
            finish();
    }

    @Override
    public void onNegativeButtonClicked() {
        if (isBack) finish();
    }

    @Override
    public void onErrorButtonClicked() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}