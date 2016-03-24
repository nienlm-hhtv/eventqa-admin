package com.hhtv.eventqa_admin.helpers;

import android.content.Context;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by nienb on 24/3/16.
 */
public class ServerInaccessDialogBuilder  extends MaterialDialog.Builder {
    public ServerInaccessDialogBuilder(Context context) {
        super(context);
        this.title("Error");
        this.content("Cannot access to server at the moment, please try later !");
        this.negativeText("Dismiss");
        this.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                dialog.dismiss();
            }
        });
    }

}

