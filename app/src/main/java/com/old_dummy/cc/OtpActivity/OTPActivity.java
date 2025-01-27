package com.old_dummy.cc.OtpActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.MainActivity.MainActivity;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.NewPasswordActivity.NewPasswordActivity;
import com.old_dummy.cc.R;
import com.google.android.material.button.MaterialButton;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OTPActivity extends AppCompatActivity implements OtpContract.View {
    private EditText otpBox1, otpBox2, otpBox3, otpBox4;
    private FrameLayout progressBar;
    private MaterialTextView dataConText, resendOtp;
    private IntentFilter mIntentFilter;
    private int code = 200;
    private String mobileNumber = "";
    private Utility utility;
    private OtpContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.background_bg));
        setContentView(R.layout.activity_otpactivity);

        // Initialize views and variables
        initializeViews();
        setupOtpListeners();
        LinearLayout verifyButton = findViewById(R.id.btnVerifyOtp);
        verifyButton.setOnClickListener(v -> verifyOtp(v));

        loadData();
        presenter.countdown(resendOtp);
    }

    private void initializeViews() {
        // EditText Boxes
        otpBox1 = findViewById(R.id.otpBox1);
        otpBox2 = findViewById(R.id.otpBox2);
        otpBox3 = findViewById(R.id.otpBox3);
        otpBox4 = findViewById(R.id.otpBox4);

        // Other Views
        progressBar = findViewById(R.id.progressBar);
        dataConText = findViewById(R.id.dataConText);
        resendOtp = findViewById(R.id.resendOtp);

        // Presenter initialization
        presenter = new OtpPresenter(this);
        code = getIntent().getIntExtra(getString(R.string.verification), 200);
        mobileNumber = getIntent().getStringExtra(getString(R.string.mobile_number));
    }

    private void setupOtpListeners() {
        TextWatcher otpWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    if (otpBox1.isFocused()) otpBox2.requestFocus();
                    else if (otpBox2.isFocused()) otpBox3.requestFocus();
                    else if (otpBox3.isFocused()) otpBox4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        otpBox1.addTextChangedListener(otpWatcher);
        otpBox2.addTextChangedListener(otpWatcher);
        otpBox3.addTextChangedListener(otpWatcher);
        otpBox4.addTextChangedListener(otpWatcher);
    }

    private void loadData() {
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
    }

    public void verifyOtp(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Get the text from all 4 OTP boxes
        String otp = otpBox1.getText().toString() + otpBox2.getText().toString() +
                otpBox3.getText().toString() + otpBox4.getText().toString();

        // Check if the OTP is empty or if it has less than 4 characters
        if (TextUtils.isEmpty(otp)) {
            Snackbar.make(view, "Please Enter OTP", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (otp.length() < 4) {
            Snackbar.make(view, "Please Enter a valid OTP", Snackbar.LENGTH_LONG).show();
            return;
        }

        // Proceed with OTP verification if there's no error
        if (YourService.isOnline(this)) {
            switch (code) {
                case 200:
                    presenter.verifyUserMethodApi(mobileNumber, otp);
                    break;
                case 300:
                case 400:
                    presenter.verifyOtpApi(mobileNumber, otp);
                    break;
            }
        } else {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
        }
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

    public void resendOtp(View view) {
        presenter.resendOtpApi(mobileNumber);
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
    public void verifyOtpApiResponse(String token) {
        SharPrefHelper.setLoginToken(this, token);
        Intent intent = new Intent(this, NewPasswordActivity.class);
        intent.putExtra(getString(R.string.verification), code);
        intent.putExtra(getString(R.string.mobile_number), mobileNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void verifyUserMethodApiResponse(String token) {
        SharPrefHelper.setLoginSuccess(this, true);
        SharPrefHelper.setLoginToken(this, token);
        Intent intentMain = new Intent(this, MainActivity.class);
        intentMain.putExtra("from", "signup");
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentMain);
        finish();
    }

    @Override
    public void resendOtpApiResponse() {
        presenter.countdown(resendOtp);
    }

    @Override
    public void message(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
