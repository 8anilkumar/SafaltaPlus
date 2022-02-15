package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.StateAdapter;
import com.safaltaclass.plus.model.CentreData;
import com.safaltaclass.plus.model.CentreResponse;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CentreActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private ExpandableListView expandableListView;
    private StateAdapter expandableListViewAdapter;
    private List<CentreData> listDataGroup;
    private ApiInterface apiInterface;
    private Toolbar toolbar;
    private Call<CentreResponse> centreListCall;
    //private DialogsUtil dialogsUtil;
    private ProgressBar pbCentre;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_centre);

        pbCentre = (ProgressBar) findViewById(R.id.progress_bar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.centres));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        expandableListView = findViewById(R.id.expandableListView);
        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        listDataGroup = new ArrayList<>();
        // initializing the list of child


        expandableListViewAdapter = new StateAdapter(this, listDataGroup,listDataGroup);
        expandableListView.setAdapter(expandableListViewAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        // ExpandableListView Group expanded listener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        // ExpandableListView Group collapsed listener
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            pbCentre.setVisibility(View.VISIBLE);
            getCentreData();
        } else {
            DialogsUtil.openAlertDialog(CentreActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, this);
        }
    }

    private void getCentreData() {

        centreListCall = apiInterface.getCentres();
        centreListCall.enqueue(new Callback<CentreResponse>() {

            @Override
            public void onResponse(@NonNull Call<CentreResponse> call, @NonNull Response<CentreResponse> response) {

                if (!response.isSuccessful()) {
                    centreListCall = call.clone();
                    centreListCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbCentre.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null) {
                    for (CentreData list : response.body().getData()) {
                        listDataGroup.add(list);
                    }

                    expandableListViewAdapter.notifyDataSetChanged();

                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    DialogsUtil.openAlertDialog(CentreActivity.this, getString(R.string.error), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CentreActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getInternalMsg() != null && !response.body().getInternalMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(CentreActivity.this, getString(R.string.abort), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, CentreActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(CentreActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, CentreActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbCentre.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(CentreActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, CentreActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" +
                getResources().getString(R.string.search_center) + "</font>"));


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                expandableListViewAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                expandableListViewAdapter.getFilter().filter(query);
                //expandableListViewAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
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
