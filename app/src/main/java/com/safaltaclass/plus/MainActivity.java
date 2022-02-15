package com.safaltaclass.plus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.safaltaclass.plus.Interface.ApiInterface;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.adapter.CourseAdapter;
import com.safaltaclass.plus.adapter.GridAdapter;
import com.safaltaclass.plus.adapter.PosterAdapter;
import com.safaltaclass.plus.model.CourseListData;
import com.safaltaclass.plus.model.CourseResponseBody;
import com.safaltaclass.plus.model.GridItemMenu;
import com.safaltaclass.plus.model.PosterImage;
import com.safaltaclass.plus.model.PosterImageResponse;
import com.safaltaclass.plus.network.RetrofitApiClient;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnDialogButtonClickListener {

    private DrawerLayout drawerLayout;
    private Button btnLogout;
    private Button btnLogin;
    private TextView tvName;
    private TextView tvVersionCode;
    private ImageView ivUser;
    private ProgressBar pbCourse;
    private RecyclerView rvPoster;
    private ApiInterface apiInterface;
    private RecyclerView rvCourse;
    private CourseAdapter courseAdapter;
    private List<CourseListData> courseList;
    private Call<CourseResponseBody> courseCall;
    private PosterAdapter adapter;
    private Call<PosterImageResponse> posterCall;
    private List<PosterImage> posterList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FusedLocationProviderClient mFusedLocationClient;
    private String tag = "MainScreen";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        posterList = new ArrayList<>();
        courseList = new ArrayList<>();
        apiInterface = RetrofitApiClient.getClient(this).create(ApiInterface.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        pbCourse = (ProgressBar)findViewById(R.id.progress_bar);
        rvCourse = (RecyclerView) findViewById(R.id.rv_course_list);
        rvCourse.setHasFixedSize(true);
        rvCourse.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(this, courseList);
        rvCourse.setAdapter(courseAdapter);

        rvPoster = (RecyclerView) findViewById(R.id.rv_poster_list);
        rvPoster.setHasFixedSize(true);
        rvPoster.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PosterAdapter(this, posterList);
        rvPoster.setAdapter(adapter);

        tvVersionCode = findViewById(R.id.tv_version_value);
        tvVersionCode.setText(String.valueOf(BuildConfig.VERSION_CODE));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        navigationView.setItemIconTintList(null);

        tvName = header.findViewById(R.id.tv_name);
        ivUser = header.findViewById(R.id.iv_user);
        btnLogout = header.findViewById(R.id.btnlogout);
        btnLogin = header.findViewById(R.id.btnlogin);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (!SafaltaPlusPreferences.getInstance().getKeyUser().equals("")) {
                    tvName.setText(SafaltaPlusPreferences.getInstance().getName());
                    tvName.setVisibility(View.VISIBLE);
                    ivUser.setVisibility(View.VISIBLE);
                    btnLogout.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.GONE);
                } else {
                    btnLogin.setVisibility(View.VISIBLE);
                    tvName.setVisibility(View.GONE);
                    ivUser.setVisibility(View.GONE);
                    btnLogout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        actionBarDrawerToggle.syncState();


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                SafaltaPlusPreferences.getInstance().removeData();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


        if (SafaltaPlusUtility.getInstance().isConnected(MainActivity.this)) {
            pbCourse.setVisibility(View.VISIBLE);
            getCourseList();
            getPoster();
        } else {
            DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, this);
        }

        SafaltaPlusPreferences.getInstance().removeAllPendingVideoStatus();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_batches:
                startActivity(new Intent(MainActivity.this, BatchesActivity.class));
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_centre:
                startActivity(new Intent(MainActivity.this, CentreActivity.class));
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_about_us:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                drawerLayout.closeDrawers();
                return true;
            case R.id.nav_feedback:
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                drawerLayout.closeDrawers();
                return true;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SafaltaPlusUtility.getInstance().isConnected(MainActivity.this)) {
            getLastLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2

    }


    private void getPoster() {

        posterCall = apiInterface.getPoster();
        posterCall.enqueue(new Callback<PosterImageResponse>() {

            @Override
            public void onResponse(@NonNull Call<PosterImageResponse> call, @NonNull Response<PosterImageResponse> response) {

                if (!response.isSuccessful()) {
                    posterCall = call.clone();
                    posterCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                if (response.body().getCode() != null  && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size() > 0) {
                    for (PosterImage list : response.body().getData()) {
                        posterList.add(list);
                    }
                    adapter.notifyDataSetChanged();
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getInternalMsg() != null){
                    DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.error), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, MainActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getInternalMsg() != null){
                    if (response.body().getInternalMsg() != null && !response.body().getInternalMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.abort), response.body().getInternalMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, MainActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, MainActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
               // Log.e(tag, t.getMessage());
              //  DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, MainActivity.this);
            }
        });
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
                if (response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_success)) && response.body().getData() != null && response.body().getData().size() > 0) {
                    courseList.clear();
                    for (CourseListData list : response.body().getData()) {
                        courseList.add(list);
                    }
                    courseAdapter.notifyDataSetChanged();
                } else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_error)) && response.body().getUserMsg() != null){
                    DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.error), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, MainActivity.this);
                }else if(response.body().getCode() != null && response.body().getCode().equals(getString(R.string.response_code_abort)) && response.body().getUserMsg() != null){
                    if (response.body().getUserMsg() != null && !response.body().getUserMsg().isEmpty()) {
                        DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.abort), response.body().getUserMsg(), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, MainActivity.this);
                    }else{
                        DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.abort), getString(R.string.content_unavailable), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, true, MainActivity.this);
                    }
                }else{
                    DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, MainActivity.this);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                pbCourse.setVisibility(View.GONE);
                DialogsUtil.openAlertDialog(MainActivity.this, getString(R.string.error), getString(R.string.slow_internet), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, MainActivity.this);
            }
        });
    }

    private void getLastLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (mFusedLocationClient != null) {
            mFusedLocationClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                setAddress(location.getLatitude(),location.getLongitude());
                            }
                        }
                    }
            );
        } else {
            requestNewLocationData();
        }
    }
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            setAddress(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        }
    };

    private void setAddress(Double latitude, Double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size() > 0) {
                SafaltaPlusPreferences.getInstance().saveLocation(latitude+ "", longitude+ "",addresses.get(0).getLocality(),addresses.get(0).getAdminArea(),
                        addresses.get(0).getCountryName(),addresses.get(0).getCountryCode(),addresses.get(0).getPostalCode(),addresses.get(0).getSubAdminArea());
            }
        } catch (Exception e) {
            e.printStackTrace();
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


}
