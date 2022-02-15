package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.FileDownLoadListener;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.CourseDataAdapter;
import com.safaltaclass.plus.model.DynamicVideoData;
import com.safaltaclass.plus.model.TopicData;
import com.safaltaclass.plus.model.TopicRequestBody;
import com.safaltaclass.plus.model.TopicResponseBody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.DownloadAndEncryptFileTask;
import com.safaltaclass.plus.utility.DownloadFile;
import com.safaltaclass.plus.utility.DownloadVideoFile;
import com.safaltaclass.plus.utility.PaginationScrollListener;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.io.File;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private String TAG = "SubjectActivity";
    private CourseDataAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView rvCourse;


    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 50;
    private int currentPage = PAGE_START;

    private Call<TopicResponseBody> courseDataCall;
    private ApiInterface apiInterface;

    private String courseId;
    private String courseTitle;
    private ProgressBar pbSubject;
    private Toolbar toolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long byteRange = 0L;
    private RelativeLayout rl_subject;
    private Call<DynamicVideoData> videoDataCall;
    private DownloadVideoFile downloadVideoFile;
    private int positionRow;
    private String fileId;
    private boolean isNetOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_subject);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);

        Intent receivedIntent = getIntent();
        courseId = receivedIntent.getStringExtra("courseId");
        courseTitle = receivedIntent.getStringExtra("courseTitle");

        rl_subject = (RelativeLayout) findViewById(R.id.rl_subject);

        pbSubject = (ProgressBar) findViewById(R.id.progress_bar);

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.safalata_status_bar_color));


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(courseTitle);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        rvCourse = (RecyclerView) findViewById(R.id.rv_subject_datalist);
        adapter = new CourseDataAdapter(this, new CourseDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TopicData item, int position, boolean value) {
                if (value) {
                    positionRow = position;
                    fileId = item.getUid();
                    DialogsUtil.openAlertDialog(SubjectActivity.this,"Delete File","Are you sure to delete this downloaded Video?","Delete","Cancel",R.drawable.ic_delete,false,SubjectActivity.this);
                } else {
                    if (SafaltaPlusPreferences.getInstance().isDownloading().isEmpty() || SafaltaPlusPreferences.getInstance().isDownloading().equals(item.getUid())) {
                        if (SafaltaPlusPreferences.getInstance().isStatus().isEmpty()) {
                            downloadVideoFile = new DownloadVideoFile();
                            File file = ContextCompat.getExternalFilesDirs(getApplicationContext(),
                                    null)[0];
                            File encrypted = new File(file.getAbsolutePath(), item.getUid());
                            if (encrypted.exists()) {
                                byteRange = encrypted.length();
                            }
                            getDynamicLink(item, position, byteRange, encrypted);
                            Snackbar.make(rl_subject, "Download Starting....", Snackbar.LENGTH_LONG)
                                    .show();
                        } else {
                            adapter.notifyItemChanged(position);
                            Snackbar.make(rl_subject, "Already Video Downloading Or Already Video Downloading...", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        adapter.notifyItemChanged(position);
                        Snackbar.make(rl_subject, "Download Resume Video First Or Already Video Downloading...", Snackbar.LENGTH_LONG)
                                .show();

                    }
                }
            }
        });

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCourse.setLayoutManager(linearLayoutManager);
        rvCourse.setItemAnimator(new DefaultItemAnimator());
        rvCourse.setAdapter(adapter);
        rvCourse.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                if (SafaltaPlusUtility.getInstance().isConnected(SubjectActivity.this)) {
                    isLoading = true;
                    currentPage += 1;
                    // mocking network delay for API call
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loadNextPage();
                        }
                    }, 1000);
                } else {
                    // adapter.removeLoadingFooter();
                    isNetOff = true;
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                }

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            pbSubject.setVisibility(View.VISIBLE);
            loadFirstPage();
        } else {
            isNetOff = true;
            DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SafaltaPlusUtility.getInstance().isConnected(SubjectActivity.this)) {
                    if (courseDataCall != null && courseDataCall.isExecuted()) {
                        courseDataCall.cancel();
                    }
                    currentPage = PAGE_START;
                    isLastPage = false;
                    loadFirstPage();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    isNetOff = true;
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                }
            }
        });
    }


    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        TopicRequestBody topicRequestBody = new TopicRequestBody(courseId, "", "", "", "", "", "No", String.valueOf(currentPage));
        courseDataCall = apiInterface.getData(topicRequestBody);
        courseDataCall.enqueue(new Callback<TopicResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<TopicResponseBody> call, @NonNull Response<TopicResponseBody> response) {
                if (!response.isSuccessful()) {
                    courseDataCall = call.clone();
                    courseDataCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbSubject.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);

                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size() > 0) {
                    List<TopicData> results = fetchResults(response);
                    adapter.clear();
                    adapter.addAll(results);
                    adapter.addLoadingFooter();
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getInternalMsg() != null) {
                    isLastPage = true;
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getInternalMsg() != null) {
                    isLastPage = true;
                    if (response.body().getInternalMsg() != null && !response.body().getInternalMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.abort), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SubjectActivity.this);
                    } else {
                        DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SubjectActivity.this);
                    }
                } else {
                    isLastPage = true;
                    isNetOff=true;
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                isNetOff = true;
                pbSubject.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
            }
        });
    }


    private List<TopicData> fetchResults(Response<TopicResponseBody> response) {
        TopicResponseBody courseData = response.body();
        return courseData.getData();
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        TopicRequestBody topicRequestBody = new TopicRequestBody(courseId, "", "", "", "", "", "No", String.valueOf(currentPage));
        courseDataCall = apiInterface.getData(topicRequestBody);
        courseDataCall.enqueue(new Callback<TopicResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<TopicResponseBody> call, @NonNull Response<TopicResponseBody> response) {
                if (!response.isSuccessful()) {
                    courseDataCall = call.clone();
                    courseDataCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                adapter.removeLoadingFooter();
                isLoading = false;

                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size() > 0) {
                    List<TopicData> results = fetchResults(response);
                    adapter.addAll(results);
                    adapter.addLoadingFooter();
                } else {
                    isLastPage = true;

                    //  DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
              //  Log.e(TAG, t.getMessage());
                isNetOff = true;
                DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
            }
        });
    }

    private void getDynamicLink(TopicData item, int position, long byteRange, File encryptedFile) {
        videoDataCall = apiInterface.getVideo(item.getContent());
        videoDataCall.enqueue(new Callback<DynamicVideoData>() {

            @Override
            public void onResponse(@NonNull Call<DynamicVideoData> call, @NonNull Response<DynamicVideoData> response) {

                if (!response.isSuccessful()) {
                    videoDataCall = call.clone();
                    videoDataCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size() > 0 && response.body().getData().get(0).getSource() != null) {
                    SafaltaPlusPreferences.getInstance().addDownloading(item.getUid());
                    showNotificationBar(response.body().getData().get(0).getSource(), position, item, byteRange, encryptedFile);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getUserMsg() != null) {
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getUserMsg() != null) {
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SubjectActivity.this);
                } else {
                    DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                isNetOff = true;
                DialogsUtil.openAlertDialog(SubjectActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubjectActivity.this);
            }
        });
    }

    private void showNotificationBar(String videoUrl, int position, TopicData item, long byteRange, File mEncryptedFile) {

        NotificationCompat.Builder builder;
        String ChannelName = "Safalta Plus";
        String ChannelId = "1";
        int importance = NotificationManager.IMPORTANCE_LOW;
        Random random = new Random();
        int notificationBuilderId = random.nextInt(9999 - 1000) + 1000;


        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        ChannelId, ChannelName, importance);
                channel.enableVibration(false);
                channel.enableLights(false);
                notifyManager.createNotificationChannel(channel);
                builder = new NotificationCompat.Builder(SubjectActivity.this, ChannelId)
                        .setContentTitle("Video Downloading")
                        .setVibrate(new long[]{0L})
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.ic_safalata_logo);
            } else {
                builder = new NotificationCompat.Builder(SubjectActivity.this, ChannelId);
                builder.setContentTitle("Video Downloading")
                        .setSmallIcon(R.mipmap.ic_safalata_logo)
                        .setVibrate(new long[]{0L})
                        .setAutoCancel(true)
                        .setChannelId(ChannelId).build();

            }

            downloadVideoFile = new DownloadVideoFile(getApplicationContext(), videoUrl, item.getUid(), SafaltaPlusUtility.encrypt(BuildConfig.API_KEY, "salt", byteRange), position, byteRange, mEncryptedFile, new FileDownLoadListener() {
                @Override
                public void onDownloadComplete(Boolean download, int pos) {
                    if (download) {
                        builder.setProgress(0, 0, false);
                        builder.setContentText("Download Complete");
                        notifyManager.notify(notificationBuilderId, builder.build());
                        adapter.notifyItemChanged(position);
                    } else {
                        builder.setProgress(0, 0, false);
                        builder.setContentText("Download Failed");
                        notifyManager.notify(notificationBuilderId, builder.build());
                        adapter.notifyItemChanged(position);
                    }
                }

                @Override
                public void onDownloadProgress(int status) {
                    builder.setProgress(100, status, false);
                    notifyManager.notify(notificationBuilderId, builder.build());
                }
            });
            downloadVideoFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } catch (Exception e) {
            e.printStackTrace();
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
        if(!isNetOff) {
            if (fileId != null) {
                File file = ContextCompat.getExternalFilesDirs(getApplicationContext(),
                        null)[0];

                File mEncryptedFile = new File(file.getAbsolutePath(), fileId);
                if (mEncryptedFile.exists()) {
                    mEncryptedFile.delete();
                }
                if (downloadVideoFile != null && downloadVideoFile.getStatus() == AsyncTask.Status.RUNNING) {
                    downloadVideoFile.cancel(true);
                    downloadVideoFile = null;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SafaltaPlusPreferences.getInstance().removeDownloaded();
                        if (SafaltaPlusPreferences.getInstance().isDownloading().equals(fileId))
                            SafaltaPlusPreferences.getInstance().removeDownloading();
                        if (SafaltaPlusPreferences.getInstance().isStatus().equals(fileId))
                            SafaltaPlusPreferences.getInstance().removeVideoDownloadStaus();
                    }
                }).start();


                adapter.notifyItemChanged(positionRow);
            }
        }
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
