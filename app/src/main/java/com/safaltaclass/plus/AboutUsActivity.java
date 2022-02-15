package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.AboutUsData;
import com.safaltaclass.plus.model.AboutUsResponse;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutUsActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private ApiInterface apiInterface;
    private List<AboutUsData> aboutList;
    private Toolbar toolbar;
    private Call<AboutUsResponse> aboutUsCall;
    private WebView wvAboutUs;
    private ProgressBar pbAboutUs;
    //private DialogsUtil dialogsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_about_us);

        wvAboutUs = (WebView) findViewById(R.id.wv_about_us);
        pbAboutUs = (ProgressBar) findViewById(R.id.progress_bar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.about_us));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        aboutList = new ArrayList<>();


        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            pbAboutUs.setVisibility(View.VISIBLE);
            getAboutUsData();
        } else {
            DialogsUtil.openAlertDialog(AboutUsActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void getAboutUsData() {

        aboutUsCall = apiInterface.getAboutUs();
        aboutUsCall.enqueue(new Callback<AboutUsResponse>() {

            @Override
            public void onResponse(@NonNull Call<AboutUsResponse> call, @NonNull Response<AboutUsResponse> response) {

                if (!response.isSuccessful()) {
                    aboutUsCall = call.clone();
                    aboutUsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbAboutUs.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size()>0) {
                    for (AboutUsData list : response.body().getData()) {
                        aboutList.add(list);
                    }
                    if (aboutList.get(0).getContent() != null) {
                        wvAboutUs.getSettings().setJavaScriptEnabled(true);
                        wvAboutUs.loadDataWithBaseURL(null, aboutList.get(0).getContent(), "text/html", "UTF-8", null);
                        wvAboutUs.setVerticalScrollBarEnabled(false);
                    } else {
                        DialogsUtil.openAlertDialog(AboutUsActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, AboutUsActivity.this);
                    }
                } else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))){
                    DialogsUtil.openAlertDialog(AboutUsActivity.this, getString(R.string.error), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, AboutUsActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getInternalMsg() != null && !response.body().getInternalMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(AboutUsActivity.this, getString(R.string.abort), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, AboutUsActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(AboutUsActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, AboutUsActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbAboutUs.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(AboutUsActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, AboutUsActivity.this);
            }
        });
    }

    @Override
    public void onPositiveButtonClicked() {

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onErrorButtonClicked() {
        finishAffinity();
        System.exit(0);
    }
}
