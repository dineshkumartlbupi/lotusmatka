package com.old_dummy.cc.SecurityPinActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.old_dummy.cc.Api.ApiClient;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.MainActivity.MainActivity;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.Models.LoginModel;
import com.old_dummy.cc.OtpActivity.OTPActivity;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.lang.reflect.Type;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecurityPinActivity extends AppCompatActivity implements SecurityPinContract.View {
    EditText pinBox1, pinBox2, pinBox3, pinBox4;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    LinearLayout checkBoxLay;
    MaterialTextView resetPin;
    Vibrator vibrator;
    Animation shake;
    FrameLayout progressBar;
    String mobileNumber = null;
    String from = "";
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    SecurityPinContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_security_pin);
        intIDs();

        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    private void intIDs() {
        pinBox1 = findViewById(R.id.pinBox1);
        pinBox2 = findViewById(R.id.pinBox2);
        pinBox3 = findViewById(R.id.pinBox3);
        pinBox4 = findViewById(R.id.pinBox4);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBoxLay = findViewById(R.id.pinCodeLayout);
        progressBar = findViewById(R.id.progressBar);
        resetPin = findViewById(R.id.resetPin);
        presenter = new SecurityPinPresenter(this);
        dataConText = findViewById(R.id.dataConText);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mobileNumber = getIntent().getStringExtra(getString(R.string.mobile_number));
        from = getIntent().getStringExtra("from");

        addPinWatcher(pinBox1, checkBox1);
        addPinWatcher(pinBox2, checkBox2);
        addPinWatcher(pinBox3, checkBox3);
        addPinWatcher(pinBox4, checkBox4);

        presenter.doAppDetailsApi("");
        MaterialTextView topTitle = findViewById(R.id.topDesign).findViewById(R.id.topText);
        topTitle.setText("Enter Pin code");
        if (Objects.equals(from, "login")) {
//            findViewById(R.id.backButton).setVisibility(View.VISIBLE);
//            findViewById(R.id.backButton).setOnClickListener(v -> {
//                onBackPressed();
//            });
        } else {
//            findViewById(R.id.backButton).setVisibility(View.GONE);
        }
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

    private void addPinWatcher(EditText pinBox, CheckBox checkBox) {
        pinBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Not needed for this logic
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
//                    pinBox.setVisibility(View.GONE);  // Hide the EditText once the user enters something
//                    checkBox.setVisibility(View.VISIBLE);  // Show the CheckBox
//                    checkBox.setChecked(true);
                    if (pinBox == pinBox1 && charSequence.length() == 1) {
                        pinBox2.requestFocus();
                    }
                    if (pinBox == pinBox2 && charSequence.length() == 1) {
                        pinBox3.requestFocus();
                    }
                    if (pinBox == pinBox3 && charSequence.length() == 1) {
                        pinBox4.requestFocus();
                    }
                    Log.e(charSequence.toString(), "");
                    Log.e("", pinBox4.getText().toString());
                    if (!pinBox4.getText().toString().isEmpty()) {
                        checkBoxFun(charSequence.toString());
                    }
                    // Mark the CheckBox as checked
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for this logic
            }
        });
    }

    public void button1(View view) {
        checkBoxFun("1");
    }

    public void button2(View view) {
        checkBoxFun("2");
    }

    public void button3(View view) {
        checkBoxFun("3");
    }

    public void button4(View view) {
        checkBoxFun("4");
    }

    public void button5(View view) {
        checkBoxFun("5");
    }

    public void button6(View view) {
        checkBoxFun("6");
    }

    public void button7(View view) {
        checkBoxFun("7");
    }

    public void button8(View view) {
        checkBoxFun("8");

    }

    public void button9(View view) {
        checkBoxFun("9");
    }

    public void buttonClr(View view) {
        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);
    }

    public void button0(View view) {
        checkBoxFun("0");
    }

    public void buttonDelete(View view) {
        if (checkBox4.isChecked()) {
            checkBox4.setChecked(false);
            checkBox4.setVisibility(View.GONE); // Hide CheckBox when unchecked
            pinBox4.setVisibility(View.VISIBLE); // Show EditText again
        } else if (checkBox3.isChecked()) {
            checkBox3.setChecked(false);
            checkBox3.setVisibility(View.GONE); // Hide CheckBox when unchecked
            pinBox3.setVisibility(View.VISIBLE); // Show EditText again
        } else if (checkBox2.isChecked()) {
            checkBox2.setChecked(false);
            checkBox2.setVisibility(View.GONE); // Hide CheckBox when unchecked
            pinBox2.setVisibility(View.VISIBLE); // Show EditText again
        } else if (checkBox1.isChecked()) {
            checkBox1.setChecked(false);
            checkBox1.setVisibility(View.GONE); // Hide CheckBox when unchecked
            pinBox1.setVisibility(View.VISIBLE); // Show EditText again
        }
    }

//    String pin1, pin2, pin3, pin4;

    public void checkBoxFun(String pin) {
        presenter.api(SharPrefHelper.getLogInToken(this), pinBox1.getText().toString() + pinBox2.getText().toString() + pinBox3.getText().toString() + pinBox4.getText().toString()); // Call API with pin
        // Pin Box 1
//        if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked() && !checkBox4.isChecked()) {
//            pin1 = pin;
//            checkBox1.setVisibility(View.VISIBLE); // Show CheckBox
//            checkBox1.setChecked(true); // Check the box
//            pinBox1.setVisibility(View.GONE); // Hide EditText
//        } else if (checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked() && !checkBox4.isChecked()) {
//            pin2 = pin;
//            checkBox2.setVisibility(View.VISIBLE); // Show CheckBox
//            checkBox2.setChecked(true); // Check the box
//            pinBox2.setVisibility(View.GONE); // Hide EditText
//        } else if (checkBox1.isChecked() && checkBox2.isChecked() && !checkBox3.isChecked() && !checkBox4.isChecked()) {
//            pin3 = pin;
//            checkBox3.setVisibility(View.VISIBLE); // Show CheckBox
//            checkBox3.setChecked(true); // Check the box
//            pinBox3.setVisibility(View.GONE); // Hide EditText
//        } else if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked() && !checkBox4.isChecked()) {
//            pin4 = pin;
//            checkBox4.setVisibility(View.VISIBLE); // Show CheckBox
//            checkBox4.setChecked(true); // Check the box
//            pinBox4.setVisibility(View.GONE); // Hide EditText
//
//            presenter.api(SharPrefHelper.getLogInToken(this), pin1 + pin2 + pin3 + pin4); // Call API with pin
//        }
    }

    public void whatsapp(View view) {
        String url = "";
        if (SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_WHATSAPP_NUMBER) != null)
            url = "https://api.whatsapp.com/send?phone=" + SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_WHATSAPP_NUMBER);
        else url = "1234567890";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
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
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void ApiResponse(LoginModel loginModel) {
        if (loginModel.getStatus().equals(getString(R.string.success))) {
            SharPrefHelper.setLoginToken(this, loginModel.getData().getToken());
            presenter.doUserDetailsApi(loginModel.getData().getToken());
        } else {
            checkBox1.setChecked(false);
            checkBox2.setChecked(false);
            checkBox3.setChecked(false);
            checkBox4.setChecked(false);
            vibrator.vibrate(500);
            shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            checkBoxLay.startAnimation(shake);
        }
    }

    @Override
    public void onForgotPinApiResponse(String number) {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra(getString(R.string.mobile_number), number);
        intent.putExtra(getString(R.string.verification), 400);
        startActivity(intent);
    }

    @Override
    public void onAppDetailsApiResponse(AppDetailsModel.Data data) {
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_MARQUEE_TEXT, data.getBanner_marquee());
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_WELCOME_MSG, data.getWelcome_message());
        SharPrefHelper.setContactDetails(this, SharPrefHelper.KEY_CONTACT_NUMBER1, "+91" + data.getContact_details().getMobile_no_1());
        SharPrefHelper.setContactDetails(this, SharPrefHelper.KEY_CONTACT_NUMBER2, "+91" + data.getContact_details().getMobile_no_2());
        SharPrefHelper.setContactDetails(this, SharPrefHelper.KEY_WHATSAPP_NUMBER, "+91" + data.getContact_details().getWhatsapp_no());
        SharPrefHelper.setContactDetails(this, SharPrefHelper.KEY_WITHDRAW_PROOF, data.getContact_details().getWithdraw_proof());
        SharPrefHelper.setContactDetails(this, SharPrefHelper.KEY_CONTACT_EMAIL, data.getContact_details().getEmail_1());
        SharPrefHelper.setBannerImages(this, SharPrefHelper.KEY_BANNER_IMAGES1, data.getBanner_image().getBanner_img_1());
        SharPrefHelper.setBannerImages(this, SharPrefHelper.KEY_BANNER_IMAGES2, data.getBanner_image().getBanner_img_2());
        SharPrefHelper.setBannerImages(this, SharPrefHelper.KEY_BANNER_IMAGES3, data.getBanner_image().getBanner_img_3());
        SharPrefHelper.setBooleanData(this, SharPrefHelper.KEY_MAIN_MARKET_STATUS, data.getProject_status().getMain_market().equals("On"));
        SharPrefHelper.setBooleanData(this, SharPrefHelper.KEY_STARLINE_MARKET_STATUS, data.getProject_status().getStarline_market().equals("On"));
        SharPrefHelper.setBooleanData(this, SharPrefHelper.KEY_GALIDESAWAR_MARKET_STATUS, data.getProject_status().getGalidesawar_market().equals("On"));
        SharPrefHelper.setBooleanData(this, SharPrefHelper.KEY_BANNER_STATUS, data.getProject_status().getBanner_status().equals("On"));
        SharPrefHelper.setBooleanData(this, SharPrefHelper.KEY_MARQUEE_STATUS, data.getProject_status().getMarquee_status().equals("On"));
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_BANNER_LIST, new Gson().toJson(data.getBannerList()));
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_App_Details, new Gson().toJson(data));

    }

    @Override
    public void onUserDetailsApiResponse(LoginModel.Data data) {
        SharPrefHelper.setSignUpData(this, SharPrefHelper.KEY_PERSON_NAME, data.getUsername());
        SharPrefHelper.setLoginSuccess(this, true);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("from", "pin");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    public void resetPin(View view) {
        if (mobileNumber == null) {
            presenter.resetPinBtn(SharPrefHelper.getSignUpData(this, SharPrefHelper.KEY_MOBILE_NUMBER));
        } else {
            presenter.resetPinBtn(mobileNumber);
        }
    }


    public void backToLogin(View view) {
        SharPrefHelper.setClearData(this);
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}