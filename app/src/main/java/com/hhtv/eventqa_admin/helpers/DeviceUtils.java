package com.hhtv.eventqa_admin.helpers;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by nienb on 11/3/16.
 */
public class DeviceUtils {
    public static String readAsset(Context context, String path) {
        StringBuilder buf=new StringBuilder();
        try{
            InputStream json=context.getAssets().open("fakedapi/" + path);
            BufferedReader in=
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return buf.toString();
    }
}
