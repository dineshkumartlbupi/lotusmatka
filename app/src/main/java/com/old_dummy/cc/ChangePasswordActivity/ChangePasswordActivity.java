package com.old_dummy.cc.ChangePasswordActivity;

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
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

public class ChangePasswordActivity extends AppCompatActivity implements ChangePasswordContract.View {

    TextInputEditText inputNewPass,inputConformPass;
    String mobileNumber = "";
    InputMethodManager imm;
    ShapeableImageView passToggleEye,passToggleEyeConf;
    FrameLayout progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    ChangePasswordContract.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_change_password);
        intIDs();
    }

    private void intIDs() {
        inputNewPass = findViewById(R.id.inputNewPass);
        passToggleEye = findViewById(R.id.passToggleEye);
        inputConformPass = findViewById(R.id.inputConformPass);
        passToggleEyeConf = findViewById(R.id.passToggleEyeConf);
        progressBar = findViewById(R.id.progressBar);
        mobileNumber = SharPrefHelper.getSignUpData(this, SharPrefHelper.KEY_MOBILE_NUMBER);
        presenter = new ChangePasswordPresenter(this);

        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
//        ImageButton backButton = findViewById(R.id.backButton);
        MaterialTextView topText = findViewById(R.id.topText);
        topText.setText("Change Password");
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
    }

    public void GoChangePass(View view) {
        imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(inputNewPass.getText().toString())){
            Snackbar.make(view,getString( R.string.please_enter_your_new_password), 2000).show();
            return;
        }
        if (inputNewPass.getText().toString().length()<4){
            Snackbar.make(view, getString(R.string.new_password_must_be_of_at_least_4_characters_length), 2000).show();
            return;
        }
        if (TextUtils.isEmpty(inputConformPass.getText().toString())){
            Snackbar.make(view, getString(R.string.please_enter_conform_password), 2000).show();
            return;
        }
        if (!inputConformPass.getText().toString().trim().equals(inputNewPass.getText().toString().trim())){
            Snackbar.make(view, getString(R.string.password_not_matchin), 2000).show();
            return;
        }
        if (YourService.isOnline(this)) presenter.api(SharPrefHelper.getLogInToken(this),mobileNumber,"mobile_token", inputConformPass.getText().toString());
        else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
    }


    public void passToggleEye(View view) {
        if (inputNewPass.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            inputNewPass.setTransformationMethod(new SingleLineTransformationMethod());
            passToggleEye.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
        else {
            inputNewPass.setTransformationMethod(new PasswordTransformationMethod());
            passToggleEye.setImageResource(R.drawable.ic_baseline_visibility_24);
        }

        inputNewPass.setSelection(inputNewPass.getText().length());
    }
    public void passToggleEyeConf(View view) {
        if (inputConformPass.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
            inputConformPass.setTransformationMethod(new SingleLineTransformationMethod());
            passToggleEyeConf.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
        else {
            inputConformPass.setTransformationMethod(new PasswordTransformationMethod());
            passToggleEyeConf.setImageResource(R.drawable.ic_baseline_visibility_24);
        }
        inputConformPass.setSelection(inputConformPass.getText().length());
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
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void apiResponse(String token) {
        onBackPressed();
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

}