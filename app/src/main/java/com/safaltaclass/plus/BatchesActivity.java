package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.BatchesAdapter;
import com.safaltaclass.plus.model.BatchData;
import com.safaltaclass.plus.model.BatchList;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BatchesActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private RecyclerView rvBatch;
    private ApiInterface apiInterface;
    private BatchesAdapter adapter;
    private List<BatchData> batchList;
    private Toolbar toolbar;
    private Call<BatchList> batchListCall;
    private ProgressBar pbBatches;
    // private DialogsUtil dialogsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_batches);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.batches));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);
        batchList = new ArrayList<>();

        pbBatches = (ProgressBar) findViewById(R.id.progress_bar);
        rvBatch = (RecyclerView) findViewById(R.id.rv_batch_list);
        rvBatch.setHasFixedSize(true);
        rvBatch.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BatchesAdapter(this, batchList);
        rvBatch.setAdapter(adapter);

        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            pbBatches.setVisibility(View.VISIBLE);
            getBatchList();
        } else {
            DialogsUtil.openAlertDialog(BatchesActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, this);
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

    private void getBatchList() {

        batchListCall = apiInterface.getBatches();
        batchListCall.enqueue(new Callback<BatchList>() {

            @Override
            public void onResponse(@NonNull Call<BatchList> call, @NonNull Response<BatchList> response) {

                if (!response.isSuccessful()) {
                    batchListCall = call.clone();
                    batchListCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                pbBatches.setVisibility(View.GONE);
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success))) {
                    for (BatchData list : response.body().getData()) {
                        batchList.add(list);
                    }
                    adapter.notifyDataSetChanged();
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error))) {
                    DialogsUtil.openAlertDialog(BatchesActivity.this, getString(R.string.error), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, BatchesActivity.this);
                } else if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort))) {
                    if (response.body().getInternalMsg() != null && !response.body().getInternalMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(BatchesActivity.this, getString(R.string.abort), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, BatchesActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(BatchesActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, BatchesActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbBatches.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(BatchesActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, BatchesActivity.this);
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
