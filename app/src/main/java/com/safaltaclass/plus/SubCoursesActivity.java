package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.CourseAdapter;
import com.safaltaclass.plus.model.CourseListData;
import com.safaltaclass.plus.model.CourseResponseBody;
import com.safaltaclass.plus.model.SubCourseRequestBody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubCoursesActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private RecyclerView rvCourse;
    private ApiInterface apiInterface;
    private CourseAdapter adapter;
    private List<CourseListData> courseList;
    private Toolbar toolbar;
    private Call<CourseResponseBody> courseCall;
    private ProgressBar pbCourse;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String parentCode;
    private String nextPageTitle;
    private String TAG = "SubCoursesActivity";
    //   private SimpleSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_sub_courses);
        pbCourse = (ProgressBar) findViewById(R.id.progress_bar);

        parentCode = getIntent().getStringExtra("parentCode");
        nextPageTitle = getIntent().getStringExtra("nextPageTitle");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(nextPageTitle);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //   searchView = (SimpleSearchView)findViewById(R.id.searchView);
        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        courseList = new ArrayList<>();

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.safalata_status_bar_color));
        rvCourse = (RecyclerView) findViewById(R.id.rv_sub_course_list);

        rvCourse.setHasFixedSize(true);
        rvCourse.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter(this, courseList);
        rvCourse.setAdapter(adapter);


        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            pbCourse.setVisibility(View.VISIBLE);
            getCourseList();
        } else {
            DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubCoursesActivity.this);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (SafaltaPlusUtility.getInstance().isConnected(SubCoursesActivity.this)) {
                    if (courseCall!= null && courseCall.isExecuted())
                        courseCall.cancel();
                    getCourseList();
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubCoursesActivity.this);
                }
            }
        });

    /*    searchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (SafaltaUtility.getInstance().isConnected(SubCoursesActivity.this)) {
                    Intent intent = new Intent(SubCoursesActivity.this,SearchActivity.class);
                    intent.putExtra("searchText",query);
                    intent.putExtra("courseId",parentCode);
                    startActivity(intent);
                }else{
                    mSwipeRefreshLayout.setRefreshing(false);
                    DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SubCoursesActivity.this);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SimpleSearchView", "Text changed:" + newText);
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                Log.d("SimpleSearchView", "Text cleared");
                return false;
            }
        });*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCourseList() {
        SubCourseRequestBody subCourseRequestBody = new SubCourseRequestBody(parentCode);
        courseCall = apiInterface.getSubCourse(subCourseRequestBody);
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
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getUserMsg() != null){
                    DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubCoursesActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getUserMsg() != null){
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SubCoursesActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SubCoursesActivity.this);
                    }
                }else{
                    DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubCoursesActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
              //  Log.e(TAG,t.getMessage());
                if(mSwipeRefreshLayout !=null && pbCourse != null) {
                    pbCourse.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                DialogsUtil.openAlertDialog(SubCoursesActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, SubCoursesActivity.this);
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
