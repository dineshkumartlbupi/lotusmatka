package com.old_dummy.cc.NewPasswordActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SecurityPinActivity.SecurityPinActivity;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.lang.reflect.Type;

public class NewPasswordActivity extends AppCompatActivity implements NewPasswordContract.View{

    private TextInputEditText mINewPass, mIConfPass,mINewPin, mIConfPin;
    private ShapeableImageView mPassToggle, mPassToggleConf,mPinToggle, mPinToggleConf;
    private FrameLayout mProgressBar;
    private IntentFilter mIntentFilter;
    private String mMNumber = null;
    Utility utility;
    private int code = 200;
    LinearLayout passLayout,pinLayout;
    NewPasswordContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_new_password);
        intVariables();
        loadData();
    }

    private void loadData() {
        MaterialTextView mDataConText = findViewById(R.id.dataConText);
        utility = new Utility(mDataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
    }
    private void intVariables() {
        mINewPass = findViewById(R.id.inputNewPass);
        mPassToggle = findViewById(R.id.passToggleEye);
        mIConfPass = findViewById(R.id.inputConformPass);
        mPassToggleConf = findViewById(R.id.passToggleEyeConf);
        mINewPin = findViewById(R.id.inputNewPin);
        mPinToggle = findViewById(R.id.pinToggleEye);
        mIConfPin = findViewById(R.id.inputConformPin);
        mPinToggleConf = findViewById(R.id.pinToggleEyeConf);
        mProgressBar = findViewById(R.id.progressBar);
        passLayout = findViewById(R.id.passLayout);
        pinLayout = findViewById(R.id.pinLayout);
        mMNumber = getIntent().getStringExtra(getString(R.string.mobile_number));
        code = getIntent().getIntExtra(getString(R.string.verification), 200);
        presenter = new NewPasswordPresenter(this);

        MaterialTextView topTitle = findViewById(R.id.topDesign).findViewById(R.id.topText);
//        findViewById(R.id.backButton).setOnClickListener(v -> {
//            onBackPressed();
//        });
        if (code==300){
            topTitle.setText("Reset Password");
            passLayout.setVisibility(View.VISIBLE);
        }else {
            topTitle.setText("Change Pin");
            pinLayout.setVisibility(View.VISIBLE);
        }

    }

    public void passToggleEye(View view) {
        if (mINewPass.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            mINewPass.setTransformationMethod(new SingleLineTransformationMethod());
            mPassToggle.setImageResource(R.drawable.ic_baseline_visibility_24);
        }
        else {
            mINewPass.setTransformationMethod(new PasswordTransformationMethod());
            mPassToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }

        mINewPass.setSelection(mINewPass.getText().length());

    }

    public void GoChangePass(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(mINewPass.getText().toString())){
            Snackbar.make(view,getString( R.string.please_enter_your_new_password), 2000).show();
            return;
        }
        if (mINewPass.getText().toString().length()<4){
            Snackbar.make(view, getString(R.string.new_password_must_be_of_at_least_4_characters_length), 2000).show();
            return;
        }
        if (TextUtils.isEmpty(mIConfPass.getText().toString())){
            Snackbar.make(view, getString(R.string.please_enter_conform_password), 2000).show();
            return;
        }
        if (!mIConfPass.getText().toString().trim().equals(mINewPass.getText().toString().trim())){
            Snackbar.make(view, getString(R.string.password_not_matchin), 2000).show();
            return;
        }
        if (YourService.isOnline(this))
            presenter.api(SharPrefHelper.getLogInToken(this),mMNumber, mIConfPass.getText().toString());
        else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
    }
    public void passToggleEyeConf(View view) {
        if (mIConfPass.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            mIConfPass.setTransformationMethod(new SingleLineTransformationMethod());
            mPassToggleConf.setImageResource(R.drawable.ic_baseline_visibility_24);
        }
        else {
            mIConfPass.setTransformationMethod(new PasswordTransformationMethod());
            mPassToggleConf.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
        mIConfPass.setSelection(mIConfPass.getText().length());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(myReceiver, mIntentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, mIntentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void apiResponse(String token) {
        if (code==300) {
            SharPrefHelper.setLoginToken(NewPasswordActivity.this, token);
            Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else {
            SharPrefHelper.setLoginToken(NewPasswordActivity.this, token);
            Intent intent = new Intent(NewPasswordActivity.this, SecurityPinActivity.class);
            intent.putExtra("from","newPin");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void message(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void destroy(String msg) {
        SharPrefHelper.setClearData(this);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    public void pinToggleEye(View view) {
        if (mINewPin.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            mINewPin.setTransformationMethod(new SingleLineTransformationMethod());
            mPinToggle.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
        else {
            mINewPin.setTransformationMethod(new PasswordTransformationMethod());
            mPinToggle.setImageResource(R.drawable.ic_baseline_visibility_24);
        }

        mINewPin.setSelection(mINewPin.getText().length());

    }

    public void pinToggleEyeConf(View view) {
        if (mIConfPin.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            mIConfPin.setTransformationMethod(new SingleLineTransformationMethod());
            mPinToggleConf.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
        else {
            mIConfPin.setTransformationMethod(new PasswordTransformationMethod());
            mPinToggleConf.setImageResource(R.drawable.ic_baseline_visibility_24);
        }
        mIConfPin.setSelection(mIConfPin.getText().length());
    }
    public void GoChangePin(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(mINewPin.getText().toString())){
            Snackbar.make(view,"Please Enter Your New Pin", 2000).show();
            return;
        }
        if (mINewPin.getText().toString().length()<4){
            Snackbar.make(view, "Please Enter 4 Digit Pin", 2000).show();
            return;
        }
        if (TextUtils.isEmpty(mIConfPin.getText().toString())){
            Snackbar.make(view, "Please Enter Confirm Pin", 2000).show();
            return;
        }
        if (!mIConfPin.getText().toString().trim().equals(mINewPin.getText().toString().trim())){
            Snackbar.make(view, "Pin Not Match", 2000).show();
            return;
        }
        if (YourService.isOnline(this))
            presenter.apiChangePin(SharPrefHelper.getLogInToken(this),mMNumber, mIConfPin.getText().toString());
        else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
    }
}