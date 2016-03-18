package com.hhtv.eventqa_admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hhtv.eventqa_admin.R;
import com.hhtv.eventqa_admin.api.APIEndpoint;
import com.hhtv.eventqa_admin.api.APIService;
import com.hhtv.eventqa_admin.helpers.NetworkFailBuilder;
import com.hhtv.eventqa_admin.helpers.UserUtils;
import com.hhtv.eventqa_admin.models.user.GetUserResponse;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by nienb on 10/3/16.
 */
public class HomeActivity extends AppCompatActivity {


    @Bind(R.id.home_input_email)
    EditText mHomeInputEmail;
    @Bind(R.id.home_input_password)
    EditText mHomeInputPassword;
    @Bind(R.id.home_btn_login)
    AppCompatButton mHomeBtnLogin;


    MaterialDialog loadingDialog, signinFailDialog;
    NetworkFailBuilder b;
    APIEndpoint api = APIService.build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        loadingDialog = new MaterialDialog.Builder(this)
                .title("Signin")
                .content("Please wait...")
                .progress(true, 0)
                .cancelable(false)
                .build();
        signinFailDialog = new MaterialDialog.Builder(this)
                .title("Signin fail")
                .content("Please check your credential !")
                .negativeText("Dismiss")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                    }
                }).build();
        b = new NetworkFailBuilder(this);
    }

    @OnClick({R.id.home_btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_btn_login:
                login();
                break;

        }
    }

    public void login() {
        loadingDialog.show();
        mHomeBtnLogin.setEnabled(false);

        String email = mHomeInputEmail.getText().toString();
        String password = mHomeInputPassword.getText().toString();
        Call<GetUserResponse> call = api.getUser(email, password);
        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Response<GetUserResponse> response, Retrofit retrofit) {
                loadingDialog.dismiss();
                if (response.isSuccess()){
                    if (response.body().isSuccess()){
                        UserUtils.login(HomeActivity.this, response.body());
                        Intent i = new Intent(HomeActivity.this, MainActivity.class);
                        i.putExtra("user", Parcels.wrap(response.body()));
                        startActivity(i);
                    }else{
                        signinFailDialog.show();
                    }
                }else{
                    b.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                loadingDialog.dismiss();
                b.show();
            }
        });



    }

    public void onLoginSuccess() {
        mHomeBtnLogin.setEnabled(true);

    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mHomeBtnLogin.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;
        String email = mHomeInputEmail.getText().toString();
        String password = mHomeInputPassword.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mHomeInputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            mHomeInputEmail.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mHomeInputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mHomeInputPassword.setError(null);
        }
        return valid;
    }
}
