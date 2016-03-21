package com.hhtv.eventqa_admin.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hhtv.eventqa_admin.models.user.GetUserResponse;

import java.util.Map;

/**
 * Created by nienb on 10/3/16.
 */
public class UserUtils {
    public static int getUserId(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return spfr.getInt("1",-1);
    }

    public static void setUserId(Context mContext, int id) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putInt("1", id);
        editor.commit();
    }

    public static void login(Context mContext, GetUserResponse user){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE).edit();
        editor.putInt("1", Integer.parseInt(user.getCode()));
        editor.putString("2", user.getUsername());
        editor.putString("3", user.getEmail());
        editor.apply();
    }

    public static void logout(Context mContext){
        SharedPreferences.Editor editor = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE).edit();
        editor.putInt("1", -1);
        editor.putString("2", "guest");
        editor.putString("3", "");
        editor.apply();
    }

    public static  GetUserResponse getUser(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return new GetUserResponse("ok", true, spfr.getString("1","-1"), spfr.getString("2","guest")
        , spfr.getString("3",""));
    }

    public static String getUserName(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return spfr.getString("2", "");
    }

    public static void setUserName(Context mContext, String name) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putString("2", name);
        editor.commit();
    }

    public static String getUserEmail(Context mContext){
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        return spfr.getString("3","");
    }

    public static void setUserEmail(Context mContext, String email) {
        SharedPreferences spfr = mContext.getSharedPreferences("userdata",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spfr.edit();
        editor.putString("3", email);
        editor.commit();
    }

    public static void getAllSharePref(Context context, String filename) {
        SharedPreferences prefs = context.getSharedPreferences(filename,
                Context.MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("MYTAG: ", "SPRF: " + entry.getKey() + " : "
                    + entry.getValue().toString());
        }
    }
}
