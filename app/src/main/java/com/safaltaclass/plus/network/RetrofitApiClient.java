package com.safaltaclass.plus.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.safaltaclass.plus.BuildConfig;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiClient {


    private static Retrofit retrofit = null;


    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public RetrofitApiClient() {
    } // So that nobody can create an object with constructor

    /* OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
         @Override
         public Response intercept(@NonNull Chain chain) throws IOException {
             Request request = chain.request()
                     .newBuilder()
                     .addHeader("app_id", "")
                     .addHeader("app_key", "")
                     .build();
             return chain.proceed(request);
         }
     }).build();*/
    private static OkHttpClient getOkHttpClient(Context context) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      //  interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new RequestInterceptor(context)) // This is used to add ApplicationInterceptor.
                .addInterceptor(interceptor)
                .build();
        return okHttpClient;
    }


    public static synchronized Retrofit getClient(Context context) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkHttpClient(context))
                    .baseUrl("https://api.aldine.edu.in/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
