package com.hhtv.eventqa_admin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hhtv.eventqa_admin.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nienb on 10/3/16.
 */
public class TestFragment extends Fragment {

    String text;
    @Bind(R.id.textView)
    TextView mTextView;

    public static TestFragment newInstance(String text) {
        TestFragment f = new TestFragment();
        f.text = text;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test_main, container, false);
        ButterKnife.bind(this, v);
        mTextView.setText(text);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
