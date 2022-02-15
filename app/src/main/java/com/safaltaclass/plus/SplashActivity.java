package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.AppVersionResponse;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.AppConstants;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;
import com.scottyab.rootbeer.RootBeer;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private ApiInterface apiInterface;
    private boolean isVersionUpdate = false;
    private Call<AppVersionResponse> appVersionCall;
    private String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_splash);

        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                try {
                    RootBeer rootBeer = new RootBeer(SplashActivity.this);
                    boolean isRooted = rootBeer.isRooted() || rootBeer.isRootedWithoutBusyBoxCheck();
                    String androidId = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    String deviceBrand = android.os.Build.MANUFACTURER;
                    String deviceModel = android.os.Build.MODEL;
                    String osVersion = android.os.Build.VERSION.RELEASE;
                    SafaltaPlusPreferences.getInstance().saveDeviceId(androidId, deviceBrand, deviceModel, osVersion, isRooted + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (SafaltaPlusUtility.getInstance().isConnected(SplashActivity.this)) {
                    getAppVersion();
                } else {
                    DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.continues), getString(R.string.cancel), R.drawable.ic_error, false, SplashActivity.this);
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.permission_required), getString(R.string.denied_permission), getString(R.string.quit), getString(R.string.cancel), R.drawable.ic_error, true, SplashActivity.this);
            }
        });
    }

    private void getAppVersion() {
        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        appVersionCall = apiInterface.getVersion();
        appVersionCall.enqueue(new Callback<AppVersionResponse>() {
            @Override
            public void onResponse(@NonNull Call<AppVersionResponse> call, @NonNull Response<AppVersionResponse> response) {

                if (!response.isSuccessful()) {
                    appVersionCall = call.clone();
                    appVersionCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;


                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success))) {
                    goToHomePage();
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    isVersionUpdate = false;
                    DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.message), response.body().getMessage(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_success, false, SplashActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_update))) {
                    isVersionUpdate = true;
                    DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.update), response.body().getMessage(), getString(R.string.update), getString(R.string.cancel), R.drawable.ic_update, false, SplashActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getMessage() != null && response.body().getMessage().isEmpty()) {
                        DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.abort), response.body().getMessage(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SplashActivity.this);
                    } else {
                        DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SplashActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                DialogsUtil.openAlertDialog(SplashActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SplashActivity.this);
            }
        });
    }

    private void goToHomePage() {
        if (SafaltaPlusPreferences.getInstance().getKeyUser().equals("")) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void onPositiveButtonClicked() {
        if (isVersionUpdate) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(AppConstants.playStoreUrl)));
            } catch (ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(AppConstants.playStoreUrl)));
            }
            finish();
        } else {
            goToHomePage();
        }
    }

    @Override
    public void onNegativeButtonClicked() {
        if (isVersionUpdate) {
            finish();
        } else {
            goToHomePage();
        }
    }

    @Override
    public void onErrorButtonClicked() {
        finish();
    }
}

