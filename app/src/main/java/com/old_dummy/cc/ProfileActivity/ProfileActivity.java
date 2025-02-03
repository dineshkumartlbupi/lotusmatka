package com.old_dummy.cc.ProfileActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.BaseActivity;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.MainActivity.MainActivity;
import com.old_dummy.cc.Models.LoginModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {
    TextInputEditText inputPersonName, inputMobileNumber, inputEmail;
    MaterialButton submitButton;
    InputMethodManager imm;
    ProgressBar progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    ProfileContract.Presenter presenter;
    TextInputLayout nameInputLayout;
    Boolean editProfile = false;
    LinearLayout editRequestLayout;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        intIDs();
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextInputEditText inputPersonName = findViewById(R.id.inputPersonName);
        TextView profileInitial = findViewById(R.id.profileInitial);

        String userName = inputPersonName.getText().toString();

        if (userName != null && !userName.isEmpty()) {
            String firstLetter = String.valueOf(userName.charAt(0)).toUpperCase();
            profileInitial.setText(firstLetter);
        }
//        String userName = inputPersonName.getText().toString();
        nameTextView.setText(userName);
submitButton.setVisibility(View.VISIBLE);
editRequestLayout.setVisibility(View.GONE);
        editRequestLayout.setOnClickListener(view -> {
            submitEditProfile(view);
        });
        inputPersonName = findViewById(R.id.inputPersonName);
        inputEmail = findViewById(R.id.inputEmail);
        inputMobileNumber = findViewById(R.id.inputMobNumber);

        // Set hint text color using android:textColorHint attribute (recommended)
        inputPersonName.setTextColor(getResources().getColor(R.color.main_color));
        inputEmail.setTextColor(getResources().getColor(R.color.main_color));
        inputMobileNumber.setTextColor(getResources().getColor(R.color.main_color));


        TextInputEditText hint = findViewById(R.id.inputPersonName);
        hint.setHintTextColor(ContextCompat.getColor(this, R.color.main_color));

    }

    private void intIDs() {
        inputPersonName = findViewById(R.id.inputPersonName);
        inputEmail = findViewById(R.id.inputEmail);
        inputMobileNumber = findViewById(R.id.inputMobNumber);
        submitButton = findViewById(R.id.submitButton);
        editRequestLayout = findViewById(R.id.editRequestLayout);
        progressBar = findViewById(R.id.progressBar);
        presenter = new ProfilePresenter(this);
        dataConText = findViewById(R.id.dataConText);
//        nameInputLayout = findViewById(R.id.labelTextView);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        MaterialToolbar toolbar = findViewById(R.id.appbarLayout).findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Profile");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputPersonName.setText(SharPrefHelper.getSignUpData(this, SharPrefHelper.KEY_PERSON_NAME));
        inputMobileNumber.setText(SharPrefHelper.getSignUpData(this, SharPrefHelper.KEY_MOBILE_NUMBER));
        inputEmail.setText(SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_USER_EMAIL));

        inputPersonName.setSelection(inputPersonName.getText().length());
    }

    public void submitEditProfile(View view) {
        if (editProfile) {
            imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if (TextUtils.isEmpty(inputPersonName.getText().toString())) {
                Snackbar.make(view, getString(R.string.please_enter_your_name), 2000).show();
                return;
            }
            if (TextUtils.isEmpty(inputEmail.getText().toString())) {
                Snackbar.make(view, getString(R.string.please_enter_your_email), 2000).show();
                return;
            }
            if (!isEmailValid(inputEmail.getText())) {
                Snackbar.make(view, getString(R.string.please_enter_valid_email), 2000).show();
                return;
            }
            if (YourService.isOnline(this)) {
                presenter.api(SharPrefHelper.getLogInToken(this), inputEmail.getText().toString(),
                        inputPersonName.getText().toString());
                editRequestLayout.setVisibility(View.GONE);
                submitButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
            }
        } else {
            submitButton.setVisibility(View.GONE);
            editRequestLayout.setVisibility(View.VISIBLE);
            inputPersonName.setEnabled(true);
            inputEmail.setEnabled(true);
            inputPersonName.requestFocus();

//            // Shift button to the bottom
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) submitButton.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1); // Align to the bottom of the parent
//            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0); // Remove alignment from the top
//            params.setMargins(16, 10, 16, 16); // Optional: Set margin for spacing
//            submitButton.setLayoutParams(params);

            editProfile = true;
        }
    }


//    public void submitEditProfile(View view) {
//        if (editProfile){
//            imm = (InputMethodManager)getSystemService(
//                    Activity.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            if (TextUtils.isEmpty(inputPersonName.getText().toString())
//            )
//            {
//                Snackbar.make(
//                        view, getString(
//                                R.string.please_enter_your_name
//                        ), 2000).show();
//                return;
//            }
//            if (TextUtils.isEmpty(inputEmail.getText().toString())){
//                Snackbar.make(view, getString(R.string.please_enter_your_email), 2000).show();
//                return;
//            }
//            if (!isEmailValid(inputEmail.getText())){
//                Snackbar.make(view, getString(R.string.please_enter_valid_email), 2000).show();
//                return;
//            }
//            if (YourService.isOnline(this))
//                presenter.api(SharPrefHelper.getLogInToken(this), inputEmail.getText().toString(), inputPersonName.getText().toString());
//            else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
//        }
//        else {
//            submitButton.setText("Submit Request");
//            inputPersonName.setEnabled(true);
//            inputEmail.setEnabled(true);
//            inputPersonName.requestFocus();
//            editProfile = true;
//        }
//    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
    public void apiResponse(LoginModel.Data data) {
        inputPersonName.setEnabled(false);
        inputEmail.setEnabled(false);
        editProfile = false;
        MainActivity.personName.setText(inputPersonName.getText().toString());
        SharPrefHelper.setSignUpData(this, SharPrefHelper.KEY_PERSON_NAME, data.getUsername());
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_USER_EMAIL, data.getEmail());
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}