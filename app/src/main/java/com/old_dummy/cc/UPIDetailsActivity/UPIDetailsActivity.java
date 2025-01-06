package com.old_dummy.cc.UPIDetailsActivity;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

public class UPIDetailsActivity extends AppCompatActivity implements UpiDetailsContract.View {

    TextInputEditText inputUPI;
    MaterialToolbar toolbar;
    int upiActivity = 0;
    InputMethodManager imm;
    ProgressBar progressBar;
    MaterialTextView dataConText,titleText;
    IntentFilter mIntentFilter;
    Utility utility;
    ShapeableImageView upiIcon;
    UpiDetailsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_u_p_i_details);
        intIDs();
        configureToolbar();
    }
    private void intIDs() {
        inputUPI = findViewById(R.id.inputUPI);
        titleText = findViewById(R.id.titleText);
        toolbar = findViewById(R.id.appbarLayout).findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        upiIcon = findViewById(R.id.upiIcon);
        presenter = new UpiDetailsPresenter(this);

        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        upiActivity = getIntent().getIntExtra(getString(R.string.upi),0);

        switch (upiActivity){
            case 1:
                toolbar.setTitle("Paytm");
                titleText.setText("Paytm Number");
                upiIcon.setImageResource(R.drawable.paytm);
                if (SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_PAYTM_UPI)!=null){
                    inputUPI.setText(SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_PAYTM_UPI));
                }
                break;
            case 2:
                toolbar.setTitle("Google Pay");
                titleText.setText("Google Pay Number");
                upiIcon.setImageResource(R.drawable.gogpay);
                if (SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_GOOGLEPAY_UPI)!=null)
                {
                    inputUPI.setText(SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_GOOGLEPAY_UPI));
                }
                break;
            case 3:
                toolbar.setTitle("PhonePe");
                titleText.setText("PhonePe Number");
                upiIcon.setImageResource(R.drawable.phonepe_a);
                if (SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_PHONEPE_UPI)!=null)
                {
                    inputUPI.setText(SharPrefHelper.getPreferenceData(this, SharPrefHelper.KEY_PHONEPE_UPI));
                }
                break;
        }

    }

    private void configureToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void submitUpiID(View view) {
        imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(inputUPI.getText().toString())){
            Snackbar.make(view, getString(R.string.enter_valid_number), 2000).show();
            return;
        }
        if (inputUPI.getText().toString().length()<10){
            Snackbar.make(view, getString(R.string.enter_valid_number), 2000).show();
            return;
        }
        if (YourService.isOnline(this))
        presenter.api(SharPrefHelper.getLogInToken(this), inputUPI.getText().toString(),upiActivity);
        else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();

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
    public void apiResponse() {
        switch (upiActivity){
            case 1:
                SharPrefHelper.setPreferenceData(UPIDetailsActivity.this,SharPrefHelper.KEY_PAYTM_UPI ,inputUPI.getText().toString());
                break;
            case 2:
                SharPrefHelper.setPreferenceData(UPIDetailsActivity.this,SharPrefHelper.KEY_GOOGLEPAY_UPI ,inputUPI.getText().toString());
                break;
            case 3:
                SharPrefHelper.setPreferenceData(UPIDetailsActivity.this,SharPrefHelper.KEY_PHONEPE_UPI ,inputUPI.getText().toString());
                break;
        }
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