package com.safaltaclass.plus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

public class WebViewActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private Toolbar toolbar;
    private WebView wvlinks;
    private ProgressBar pbWebview;
    private String linkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_web_view);

        Intent receivedIntent = getIntent();
        linkUrl = receivedIntent.getStringExtra("url");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(linkUrl);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pbWebview = (ProgressBar) findViewById(R.id.progress_bar);
        wvlinks = (WebView) findViewById(R.id.wv_link);

        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            String keyUser = SafaltaPlusPreferences.getInstance().getKeyUser();
            wvlinks.getSettings().setSupportZoom(true);
            wvlinks.getSettings().setJavaScriptEnabled(true);
            wvlinks.getSettings().setAllowFileAccess(true);
            wvlinks.setWebViewClient(new WebViewClient() {

                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }


                public void onLoadResource(WebView view, String url) {
                    pbWebview.setVisibility(View.VISIBLE);
                }

                public void onPageFinished(WebView view, String url) {
                    pbWebview.setVisibility(View.GONE);
                    wvlinks.setVisibility(View.VISIBLE);
                }

            });

            if (linkUrl.contains("user_key")) {
                wvlinks.loadUrl(linkUrl + keyUser);
            } else {
                wvlinks.loadUrl(linkUrl);
            }

        } else {
            DialogsUtil.openAlertDialog(WebViewActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, WebViewActivity.this);
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
