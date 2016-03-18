package com.hhtv.eventqa_admin.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by nienb on 10/3/16.
 */
public class APIService {
    static final String APIENDPOINT = "http://event.hhtv.vn";
    static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create();
    static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(APIENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public static APIEndpoint build() {
        return retrofit.create(APIEndpoint.class);
    }

    public static APIEndpoint fakebuild(){
        Gson mgson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        Retrofit mretrofit = new Retrofit.Builder()
                .baseUrl("http://demo3846615.mockable.io/")
                .addConverterFactory(GsonConverterFactory.create(mgson))
                .build();
        return mretrofit.create(APIEndpoint.class);
    }
}
