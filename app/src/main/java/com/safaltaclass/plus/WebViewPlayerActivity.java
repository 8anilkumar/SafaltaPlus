package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.DynamicVideoData;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;
import com.safaltaclass.plus.utility.VideoEnabledWebChromeClient;
import com.safaltaclass.plus.utility.VideoEnabledWebView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebViewPlayerActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private WebView webView;
    private Call<DynamicVideoData> videoDataCall;
    private ApiInterface apiInterface;

    private String content;
    private String contentFormat;
    private String title;
    private String description;
    private String source;
    private VideoEnabledWebView videowebView;
    private VideoEnabledWebChromeClient webChromeClient;
    private String TAG = "WebViewPlayerActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_web_view_player);

        webView = (WebView) findViewById(R.id.webView);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);

        Intent receivedIntent = getIntent();
        content = receivedIntent.getStringExtra("content");
        contentFormat = receivedIntent.getStringExtra("contentformat");
        title = receivedIntent.getStringExtra("title");
        description = receivedIntent.getStringExtra("description");
        source = receivedIntent.getStringExtra("source");

        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            initWebview();
        } else {
            DialogsUtil.openAlertDialog(WebViewPlayerActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, WebViewPlayerActivity.this);
        }
    }

    private void initWebview() {
        if(source != null && source.equals("dynamic")) {
            getDynamicLink(content);
        }else{
            Player(content,contentFormat);
        }
    }

    private void getDynamicLink(String content) {
        videoDataCall = apiInterface.getVideo(content);
        videoDataCall.enqueue(new Callback<DynamicVideoData>() {

            @Override
            public void onResponse(@NonNull Call<DynamicVideoData> call, @NonNull Response<DynamicVideoData> response) {

                if (!response.isSuccessful()) {
                    videoDataCall = call.clone();
                    videoDataCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData()!=null && response.body().getData().size()> 0 && response.body().getData().get(0).getSource() != null) {
                    Player(response.body().getData().get(0).getSource(),contentFormat);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getUserMsg() != null){
                    DialogsUtil.openAlertDialog(WebViewPlayerActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, WebViewPlayerActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getUserMsg() != null){
                    DialogsUtil.openAlertDialog(WebViewPlayerActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, WebViewPlayerActivity.this);
                }else{
                    DialogsUtil.openAlertDialog(WebViewPlayerActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, WebViewPlayerActivity.this);
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
                DialogsUtil.openAlertDialog(WebViewPlayerActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, WebViewPlayerActivity.this);
            }
        });
    }

    private class InsideWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private void Player(String url,String playerType){
        webView = (VideoEnabledWebView) findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments
        videoLayout.setVisibility(View.VISIBLE);
        nonVideoLayout.setVisibility(View.VISIBLE);
        //noinspection all
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, videowebView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            @Override
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        // Call private class InsideWebViewClient
        webView.setWebViewClient(new InsideWebViewClient());

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        webView.loadUrl(url);
    }

    @Override
    public void onPositiveButtonClicked() {

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onErrorButtonClicked() {

    }
}
