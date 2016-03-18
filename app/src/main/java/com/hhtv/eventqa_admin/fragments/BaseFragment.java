package com.hhtv.eventqa_admin.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by nienb on 10/3/16.
 */
public class BaseFragment extends Fragment {
    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public Context getContext() {
        return super.getContext() == null ? mContext : super.getContext();
    }
}
