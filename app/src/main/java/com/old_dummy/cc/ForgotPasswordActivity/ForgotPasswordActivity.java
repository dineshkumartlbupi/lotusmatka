package com.old_dummy.cc.ForgotPasswordActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.OtpActivity.OTPActivity;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.R;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

public class ForgotPasswordActivity extends AppCompatActivity implements ForgotPasswordContract.View {

    FrameLayout progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    TextInputEditText inputMobNumber;
    Utility utility;
    ForgotPasswordContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_forgot_password);
        intIDs();
    }

    private void intIDs() {
        progressBar = findViewById(R.id.progressBar);
        dataConText = findViewById(R.id.dataConText);
        inputMobNumber = findViewById(R.id.inputMobNumber);
        presenter = new ForgotPasswordPresenter(this);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        MaterialTextView topTitle = findViewById(R.id.topDesign).findViewById(R.id.topText);
        topTitle.setText("Forgot Password");
//        findViewById(R.id.backButton).setOnClickListener(v -> {
//            onBackPressed();
//        });
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

    public void getOtp(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(inputMobNumber.getText().toString())){
            Snackbar.make(view, "Please Enter Mobile Number",2000).show();
            return;
        }
        if (inputMobNumber.getText().toString().length()<10){
            Snackbar.make(view, "Please Enter a valid Mobile Number",2000).show();
            return;
        }
        if (YourService.isOnline(this)){
            presenter.api( inputMobNumber.getText().toString().trim());
        }
        else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void apiResponse() {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra(getString(R.string.verification), 300);
        intent.putExtra(getString(R.string.mobile_number),inputMobNumber.getText().toString().trim());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void message(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}