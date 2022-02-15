package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.CourseDataAdapter;
import com.safaltaclass.plus.model.SearchRequestBody;
import com.safaltaclass.plus.model.TopicData;
import com.safaltaclass.plus.model.TopicResponseBody;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.PaginationScrollListener;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
public class SearchActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private String TAG = "SearchActivity";
    CourseDataAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rvCourse;


    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int currentPage = PAGE_START;

    private Call<TopicResponseBody> courseDataCall;
    private ApiInterface apiInterface;

    private String searchText;
    private String courseId;
    private ProgressBar pbSubject;
    private Toolbar toolbar;
    private DialogsUtil dialogsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_search);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);

        Intent receivedIntent = getIntent();
        searchText = receivedIntent.getStringExtra("searchText");
        courseId = receivedIntent.getStringExtra("courseId");

        pbSubject = (ProgressBar) findViewById(R.id.progress_bar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(searchText);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        rvCourse = (RecyclerView) findViewById(R.id.rv_subject_datalist);
        adapter = new CourseDataAdapter(this, new CourseDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TopicData item,int position) {

            }
        });
        dialogsUtil = new DialogsUtil(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCourse.setLayoutManager(linearLayoutManager);
        rvCourse.setItemAnimator(new DefaultItemAnimator());
        rvCourse.setAdapter(adapter);
        rvCourse.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                if (SafaltaPlusUtility.isConnected(SearchActivity.this)) {
                    isLoading = true;
                    currentPage += 1;

                    // mocking network delay for API call
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //   loadNextPage();
                        }
                    }, 1000);
                }else{
                    // adapter.removeLoadingFooter();
                    dialogsUtil.openAlertDialog(SearchActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SearchActivity.this);
                }

            }

            @Override
            public int getTotalPageCount() {
                return 100;
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

        if (SafaltaPlusUtility.isConnected(this)) {
            pbSubject.setVisibility(View.VISIBLE);
            loadFirstPage();
        } else {
            dialogsUtil.openAlertDialog(SearchActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SearchActivity.this);
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

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        SearchRequestBody searchRequestBody = new SearchRequestBody(courseId, searchText,"",String.valueOf(currentPage));
        courseDataCall = apiInterface.getData(searchRequestBody);
        courseDataCall.enqueue(new Callback<TopicResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<TopicResponseBody> call, @NonNull Response<TopicResponseBody> response) {
                if (!response.isSuccessful()) {
                    courseDataCall = call.clone();
                    courseDataCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                List<TopicData> results = fetchResults(response);
                pbSubject.setVisibility(View.GONE);

                //   adapter.addAll(results);

                if(results != null && results.size() > 0 ) {
                    //   adapter.clearAll();
                    adapter.clear();
                    adapter.addAll(results);
                    adapter.addLoadingFooter();
                }else{
                    dialogsUtil.openAlertDialog(SearchActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SearchActivity.this);
                    isLastPage = true;
                }


            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                Log.e(TAG,t.getMessage());
                pbSubject.setVisibility(View.GONE);
                dialogsUtil.openAlertDialog(SearchActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, SearchActivity.this);
            }
        });
    }

    private List<TopicData> fetchResults(Response<TopicResponseBody> response) {
        TopicResponseBody courseData = response.body();
        return courseData.getData();
    }
}
*/
