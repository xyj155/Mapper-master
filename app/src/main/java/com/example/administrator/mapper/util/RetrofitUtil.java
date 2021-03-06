package com.example.administrator.mapper.util;

import com.example.administrator.mapper.api.ApiService;
import com.example.administrator.mapper.entity.Usage;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Administrator on 2018/6/25.
 */

public class RetrofitUtil {
    private static final String BASE_URL = "http://182.254.147.87/";
    private Retrofit retrofit;
    private static RetrofitUtil sInstance;
    public RetrofitUtil() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
    public static RetrofitUtil getInstance() {
        synchronized (RetrofitUtil.class) {
            if (sInstance == null) {
                sInstance = new RetrofitUtil();
            }
        }
        return sInstance;
    }
    public ApiService getServerices() {
        return retrofit.create(ApiService.class);
    }
}