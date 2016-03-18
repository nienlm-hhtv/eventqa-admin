package com.hhtv.eventqa_admin.helpers;

import android.content.Context;
import android.widget.Toast;

import com.hhtv.eventqa_admin.R;

import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 10/3/16.
 */
public  class MyCallBack implements retrofit.Callback {

    IOnDataReceived i;
    Context c;
    Response mResponse;
    Retrofit mRetrofit;
    public MyCallBack(Context c, IOnDataReceived i) {
        this.i = i;
        this.c = c;
    }

    @Override
    public void onResponse(Response response, Retrofit retrofit) {
        if (response.isSuccess()){
            i.onReceived(response, retrofit);
        }else{
            Toast.makeText(c, c.getResources().getString(R.string.server_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Toast.makeText(c, c.getResources().getString(R.string.network_error),
                Toast.LENGTH_SHORT).show();
        i.onError(t);
    }

    public interface IOnDataReceived{
        void onReceived(Response response, Retrofit retrofit);
        void onError(Throwable t);
    }
}
