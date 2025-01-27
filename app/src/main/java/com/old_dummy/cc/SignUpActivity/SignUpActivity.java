package com.old_dummy.cc.SignUpActivity;

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
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.old_dummy.cc.Api.ApiUrl;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.OtpActivity.OTPActivity;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import java.lang.reflect.Type;

public class SignUpActivity extends AppCompatActivity implements SignUpContract.View {

    TextInputEditText inputPersonName, inputMobNumber, inputPassword, inputPinCode;
    InputMethodManager imm;
    FrameLayout progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    SignUpContract.Presenter presenter;
    ShapeableImageView passToggleEye, pinToggleEye;
    MaterialCheckBox privacyCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_sign_up);
        intIDs();

    }

    private void intIDs() {
        inputPersonName = findViewById(R.id.inputPersonName);
        inputMobNumber = findViewById(R.id.inputMobNumber);
        inputPassword = findViewById(R.id.inputPassword);
        inputPinCode = findViewById(R.id.inputPinCode);
        progressBar = findViewById(R.id.progressBar);
        dataConText = findViewById(R.id.dataConText);
        passToggleEye = findViewById(R.id.visibleEyePass);
        pinToggleEye = findViewById(R.id.visibleEyePin);
        privacyCheck = findViewById(R.id.tncCheck);
        presenter = new SignUpPresenter(this);

        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);


        MaterialTextView topTitle = findViewById(R.id.topDesign).findViewById(R.id.topText);
        topTitle.setText("SignUp");
//        findViewById(R.id.backButton).setVisibility(View.GONE);
        findViewById(R.id.privacyText).setOnClickListener(v -> {
            String url = ApiUrl.BASE_URL + "terms_and_conditions";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        findViewById(R.id.adminButton).setOnClickListener(v -> {
            AppDetailsModel.Data data = null;
            Gson gson = new Gson();
            Type type = new TypeToken<AppDetailsModel.Data>() {
            }.getType();
            try {
                data = gson.fromJson(SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_App_Details), type);
            } catch (Exception e) {
                System.out.println("json conversion failed");
            }
            String adminMsg = data.getAdmin_message();

            String url = "https://api.whatsapp.com/send?phone=" + SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_WHATSAPP_NUMBER) + "&text=" + adminMsg;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

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

    public void LoginNow(View view) {
        presenter.login(this);
    }

    public void createNewAccount(View view) {
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(inputPersonName.getText().toString())) {
            Snackbar.make(view, getString(R.string.please_enter_your_name), 2000).show();
            return;
        }
        if (TextUtils.isEmpty(inputMobNumber.getText().toString())) {
            Snackbar.make(view, getString(R.string.please_enter_mobile_number), 2000).show();
            return;
        }
        if (inputMobNumber.getText().toString().length() < 10) {
            Snackbar.make(view, getString(R.string.please_enter_valid_mobile_number), 2000).show();
            return;
        }
        if (TextUtils.isEmpty(inputPassword.getText().toString())) {
            Snackbar.make(view, getString(R.string.please_enter_password), 2000).show();
            return;
        }
        if (inputPassword.getText().toString().length() < 4) {
            Snackbar.make(view, getString(R.string.please_enter_min_4_digits_password), 2000).show();
            return;
        }
        if (TextUtils.isEmpty(inputPinCode.getText().toString())) {
            Snackbar.make(view, "Please Enter Security Pin", 2000).show();
            return;
        }
        if (inputPinCode.getText().toString().length() < 4) {
            Snackbar.make(view, getString(R.string.please_enter_min_4_digit_pin), 2000).show();
            return;
        }
        /*if (!privacyCheck.isChecked()){
            Snackbar.make(view, "Please check the terms and conditions",2000).show();
            return;
        }*/
        if (YourService.isOnline(this))
            presenter.api(inputPersonName.getText().toString().trim(), inputMobNumber.getText().toString().trim(), inputPassword.getText().toString().trim(), inputPinCode.getText().toString());
        else
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
    }

    public void passToggleEye(View view) {
        if (inputPassword.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
            inputPassword.setTransformationMethod(new SingleLineTransformationMethod());
            passToggleEye.setImageResource(R.drawable.ic_baseline_visibility_24);
        } else {
            inputPassword.setTransformationMethod(new PasswordTransformationMethod());
            passToggleEye.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }

        inputPassword.setSelection(inputPassword.getText().length());
    }

    public void pinToggleEye(View view) {
        if (inputPinCode.getTransformationMethod().getClass().getSimpleName().equals("PasswordTransformationMethod")) {
            inputPinCode.setTransformationMethod(new SingleLineTransformationMethod());
            pinToggleEye.setImageResource(R.drawable.ic_baseline_visibility_24);
        } else {
            inputPinCode.setTransformationMethod(new PasswordTransformationMethod());
            pinToggleEye.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }

        inputPinCode.setSelection(inputPinCode.getText().length());
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
        Intent intent = new Intent(SignUpActivity.this, OTPActivity.class);
        SharPrefHelper.setSignUpData(SignUpActivity.this,
                SharPrefHelper.KEY_PERSON_NAME, inputPersonName.getText().toString().trim());
        SharPrefHelper.setSignUpData(SignUpActivity.this,
                SharPrefHelper.KEY_MOBILE_NUMBER, inputMobNumber.getText().toString().trim());
        SharPrefHelper.setSignUpData(SignUpActivity.this,
                SharPrefHelper.KEY_USER_PASSWORD, inputPassword.getText().toString().trim());
        intent.putExtra(getString(R.string.verification), 200);
        intent.putExtra(getString(R.string.mobile_number), inputMobNumber.getText().toString().trim());
        startActivity(intent);
    }

    @Override
    public void message(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}