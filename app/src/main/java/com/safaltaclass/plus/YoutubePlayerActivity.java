package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.FullScreenHelper;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

public class YoutubePlayerActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private YouTubePlayerView youTubePlayerView;
    private FullScreenHelper fullScreenHelper;
    private String content;
    private String contentFormat;
   // private String title;
   // private String description;
    private String source;
    private TextView tvTitle;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_youtube_player);

        Intent receivedIntent = getIntent();
        content = receivedIntent.getStringExtra("content");
        contentFormat = receivedIntent.getStringExtra("contentformat");
     //   title = receivedIntent.getStringExtra("title");
     //   description = receivedIntent.getStringExtra("description");
        source = receivedIntent.getStringExtra("source");

      //  tvTitle = (TextView) findViewById(R.id.tv_title);
      //  tvDescription = (TextView) findViewById(R.id.tv_description);

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        fullScreenHelper = new FullScreenHelper(this);
        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            initYouTubePlayerView();
        }else {
            DialogsUtil.openAlertDialog(YoutubePlayerActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, YoutubePlayerActivity.this);
        }
    }

    private void initYouTubePlayerView() {
        youTubePlayerView.setVisibility(View.VISIBLE);
    //    tvTitle.setText(title);
    //    tvDescription.setText(description);

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer,
                        getLifecycle(),
                        content,
                        0f
                );
                addFullScreenListenerToPlayer();
            }
        });
    }

    private void addFullScreenListenerToPlayer() {
        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fullScreenHelper.enterFullScreen();
               /* tvTitle.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);*/
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fullScreenHelper.exitFullScreen();
             /*   tvTitle.setVisibility(View.VISIBLE);
                tvDescription.setVisibility(View.VISIBLE);*/
            }
        });

        youTubePlayerView.enterFullScreen();
    }

    @Override
    public void onBackPressed() {
        if (youTubePlayerView.isFullScreen()){
            youTubePlayerView.exitFullScreen();
        } else {
            super.onBackPressed();
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

    }
}

