package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.CourseAdapter;
import com.safaltaclass.plus.model.CourseListData;
import com.safaltaclass.plus.model.CourseResponseBody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private RecyclerView rvCourse;
    private ApiInterface apiInterface;
    private CourseAdapter adapter;
    private List<CourseListData> courseList;
    private Toolbar toolbar;
    private Call<CourseResponseBody> courseCall;
    private ProgressBar pbCourse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //private DialogsUtil dialogsUtil;
    private String TAG = "CourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_course);

        pbCourse = (ProgressBar) findViewById(R.id.progress_bar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.my_courses));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        courseList = new ArrayList<>();


        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.safalata_status_bar_color));
        rvCourse = (RecyclerView) findViewById(R.id.rv_course_list);

        rvCourse.setHasFixedSize(true);
        rvCourse.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(this, courseList);
        rvCourse.setAdapter(adapter);


        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            pbCourse.setVisibility(View.VISIBLE);
            getCourseList();
        } else {
            DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CourseActivity.this);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SafaltaPlusUtility.getInstance().isConnected(CourseActivity.this)) {
                    if (courseCall != null && courseCall.isExecuted())
                        courseCall.cancel();
                    getCourseList();
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CourseActivity.this);
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

    private void getCourseList() {

        courseCall = apiInterface.getCourse();
        courseCall.enqueue(new Callback<CourseResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<CourseResponseBody> call, @NonNull Response<CourseResponseBody> response) {

                if (!response.isSuccessful()) {
                    courseCall = call.clone();
                    courseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbCourse.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size() > 0) {
                    courseList.clear();
                    for (CourseListData list : response.body().getData()) {
                        courseList.add(list);
                    }
                    adapter.notifyDataSetChanged();
                } else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getUserMsg() != null){
                    DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CourseActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getUserMsg() != null){
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, CourseActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, CourseActivity.this);
                    }
                }else{
                    DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CourseActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbCourse.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                DialogsUtil.openAlertDialog(CourseActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CourseActivity.this);
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
