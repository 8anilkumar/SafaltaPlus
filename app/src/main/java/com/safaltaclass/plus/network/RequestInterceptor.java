package com.safaltaclass.plus.network;

import android.content.Context;

import com.safaltaclass.plus.BuildConfig;
import com.safaltaclass.plus.R;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {

    private Context context;


    public RequestInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        /*
        chain.request() returns original request that you can work with(modify, rewrite)
        */

        Request originalRequest = chain.request();
        Headers headers = new Headers.Builder()
                .add("Content-Type", context.getString(R.string.content_type))
                .add("api-key", BuildConfig.API_KEY)
                .add("app-version", BuildConfig.VERSION_NAME)
                .add("user-key", SafaltaPlusPreferences.getInstance().getKeyUser())
                .add("instance-id", SafaltaPlusPreferences.getInstance().getKeyDeviceId())
                .add("device", SafaltaPlusPreferences.getInstance().getDeviceBrand() + "," + SafaltaPlusPreferences.getInstance().getDeviceModel() + "," + SafaltaPlusPreferences.getInstance().getOsVersion())
                .add("gps", SafaltaPlusPreferences.getInstance().getLatitude() + "," + SafaltaPlusPreferences.getInstance().getLongitude())
                .add("loc", SafaltaPlusPreferences.getInstance().getCity() + "," + SafaltaPlusPreferences.getInstance().getState() + "," +
                        SafaltaPlusPreferences.getInstance().getCountry() + "," + SafaltaPlusPreferences.getInstance().getCountrycode() + "," +
                        SafaltaPlusPreferences.getInstance().getPostalcode() + "," + SafaltaPlusPreferences.getInstance().getSubAdminArea())
                .add("rooted", SafaltaPlusPreferences.getInstance().getIsrooted())
                .build();

        Request newRequest = originalRequest.newBuilder()
                .cacheControl(CacheControl.FORCE_NETWORK) // Sets this request's Cache-Control header, replacing any cache control headers already present.
                .headers(headers) //Removes all headers on this builder and adds headers.
                // .method(originalRequest.method(), null) // Adds request method and request body// Removes all the headers with this name
                .build();
        /*
        chain.proceed(request) is the call which will initiate the HTTP work. This call invokes the
        request and returns the response as per the request.
        */
        Response response = chain.proceed(newRequest);
        return response;

    }
}
