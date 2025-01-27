package com.old_dummy.cc.SplashActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.old_dummy.cc.ChartActivity.ChartActivity;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.MainActivity.MainActivity;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SecurityPinActivity.SecurityPinActivity;
import com.old_dummy.cc.ServerActivity.ServerActivity;
import com.old_dummy.cc.SignUpActivity.SignUpActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    SplashContract.Presenter presenter;
    FrameLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_splash);
        presenter = new SplashPresenter(this);
        presenter.api("");
    }

    @Override
    public void apiResponse(AppDetailsModel.Data data) {
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_MARQUEE_TEXT,data.getBanner_marquee());
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_WELCOME_MSG,data.getWelcome_message());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_CONTACT_NUMBER1,"+91"+data.getContact_details().getMobile_no_1());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_CONTACT_NUMBER2,"+91"+data.getContact_details().getMobile_no_2());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_WHATSAPP_NUMBER,"+91"+data.getContact_details().getWhatsapp_no());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_WITHDRAW_PROOF,data.getContact_details().getWithdraw_proof());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_CONTACT_EMAIL,data.getContact_details().getEmail_1());
        SharPrefHelper.setBannerImages(this,SharPrefHelper.KEY_BANNER_IMAGES1, data.getBanner_image().getBanner_img_1());
        SharPrefHelper.setBannerImages(this,SharPrefHelper.KEY_BANNER_IMAGES2, data.getBanner_image().getBanner_img_2());
        SharPrefHelper.setBannerImages(this,SharPrefHelper.KEY_BANNER_IMAGES3, data.getBanner_image().getBanner_img_3());
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_MAIN_MARKET_STATUS, data.getProject_status().getMain_market().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_STARLINE_MARKET_STATUS, data.getProject_status().getStarline_market().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_GALIDESAWAR_MARKET_STATUS, data.getProject_status().getGalidesawar_market().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_BANNER_STATUS, data.getProject_status().getBanner_status().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_MARQUEE_STATUS, data.getProject_status().getMarquee_status().equals("On"));
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_BANNER_LIST, new Gson().toJson(data.getBannerList()));
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_App_Details, new Gson().toJson(data));

        switch (data.getApp_status()) {
            case "live":
                callApp();
                break;
            case "development":
                callDevApp(data);
                break;
            case "server_down":
                callServerDown();
        }

    }

    @Override
    public void apiPinResponse(String token) {
        progressBar.setVisibility(View.GONE);
        SharPrefHelper.setLoginToken(this, token);
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void callServerDown() {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
        finish();
    }

    private void callDevApp(AppDetailsModel.Data data) {
        Intent intent = new Intent(this, ChartActivity.class);
        intent.putExtra(getString(R.string.chart), data.getApp_link());
        intent.putExtra(getString(R.string.game_name), "Under Development");
        startActivity(intent);
        finish();
    }

    private void callApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SharPrefHelper.getLoginSuccess(SplashActivity.this)) {
                    Intent i = new Intent(SplashActivity.this, SecurityPinActivity.class);
                    i.putExtra(getString(R.string.mobile_number),SharPrefHelper.getSignUpData(SplashActivity.this, SharPrefHelper.KEY_MOBILE_NUMBER));
                    i.putExtra("from","splash");
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  );
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } else {
//                    progressBar.setVisibility(View.GONE);
                    Intent i = new Intent(SplashActivity.this, SignUpActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  );
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            }
        }, 500);
    }

    @Override
    public void message(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void destroy() {
        SharPrefHelper.setLoginSuccess(this, false);
        Intent logOut= new Intent(this, SignUpActivity.class);
        logOut.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logOut);
    }

}