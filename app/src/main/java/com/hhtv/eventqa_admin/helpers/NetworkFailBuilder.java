package com.hhtv.eventqa_admin.helpers;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by nienb on 18/3/16.
 */
public class NetworkFailBuilder extends MaterialDialog.Builder {
    public NetworkFailBuilder(Context context) {
        super(context);
        this.title("Error");
        this.content("There was problem with your network connection, please check and try again !");
        this.negativeText("Dismiss");
        this.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.dismiss();
            }
        });
    }

}
