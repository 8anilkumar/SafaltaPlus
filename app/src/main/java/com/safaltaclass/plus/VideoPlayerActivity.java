package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.model.DynamicVideoData;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.player.LocalExoplayerView;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.EncryptedFileDataSourceFactory;
import com.safaltaclass.plus.utility.FullScreenHelper;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;
import com.safaltaclass.plus.utility.VideoEnabledWebChromeClient;
import com.safaltaclass.plus.utility.VideoEnabledWebView;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayerActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private String content;
    private String contentFormat;
    private String title;
    private String description;
    private String source;
    private String uid;
    private LocalExoplayerView localExoPlayerView;
    private Call<DynamicVideoData> videoDataCall;
    private ApiInterface apiInterface;
    private TextView tvTitle;
    private TextView tvDescription;
    private String TAG = "VideoPlayerActivity";

    private File mEncryptedFile;
    DataSource.Factory dataSourceFactory;
    ExtractorsFactory extractorsFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_player);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);


        Intent receivedIntent = getIntent();
        content = receivedIntent.getStringExtra("content");
        contentFormat = receivedIntent.getStringExtra("contentformat");
        title = receivedIntent.getStringExtra("title");
        description = receivedIntent.getStringExtra("description");
        source = receivedIntent.getStringExtra("source");
        uid = receivedIntent.getStringExtra("uid");

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDescription = (TextView) findViewById(R.id.tv_description);

        localExoPlayerView = findViewById(R.id.localExoPlayerView);

        File file = ContextCompat.getExternalFilesDirs(getApplicationContext(),
                null)[0];
        mEncryptedFile = new File(file.getAbsolutePath(), uid);
        if(mEncryptedFile.exists() && SafaltaPlusPreferences.getInstance().isDownloaded().equals(uid)) {
           /* DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);*/
            Pair<Cipher, Pair<SecretKeySpec, IvParameterSpec>> values = SafaltaPlusUtility.decrypt(BuildConfig.API_KEY, "salt");
            dataSourceFactory = new EncryptedFileDataSourceFactory(values.first, values.second.first, values.second.second);
            extractorsFactory = new DefaultExtractorsFactory();

            try {
                Uri uri = Uri.fromFile(mEncryptedFile);
                MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
              /*  tvTitle.setText(title);
                tvDescription.setText(description);
                tvTitle.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);*/


                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        localExoPlayerView.setSource(videoSource);
                        localExoPlayerView.setVisibility(View.VISIBLE);

                    }
                }, 2000);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            if (SafaltaPlusUtility.getInstance().isConnected(this)) {
                initVideoView();
            } else {
                DialogsUtil.openAlertDialog(VideoPlayerActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, VideoPlayerActivity.this);
            }
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

    /*protected void onPause(){
        super.onPause();
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
    }

    protected void onResume(){
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }*/



    private void initVideoView() {
        if(source != null && source.equals("dynamic")){
            getDynamicLink(content);
        }else {
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
                    DialogsUtil.openAlertDialog(VideoPlayerActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, VideoPlayerActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getUserMsg() != null){
                    DialogsUtil.openAlertDialog(VideoPlayerActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, VideoPlayerActivity.this);
                }else{
                    DialogsUtil.openAlertDialog(VideoPlayerActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, VideoPlayerActivity.this);
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.e(TAG, t.getMessage());
                DialogsUtil.openAlertDialog(VideoPlayerActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, VideoPlayerActivity.this);
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        if(localExoPlayerView != null) {
            localExoPlayerView.getPlayer().release();
            localExoPlayerView = null;
        }

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


    private void Player(String url,String playerType){

        localExoPlayerView.setVisibility(View.VISIBLE);
        localExoPlayerView.setSource(url);
       /* tvTitle.setText(title);
        tvDescription.setText(description);
       tvTitle.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);*/
    }


}