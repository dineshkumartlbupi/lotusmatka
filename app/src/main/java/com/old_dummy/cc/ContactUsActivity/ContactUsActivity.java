package com.old_dummy.cc.ContactUsActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textview.MaterialTextView;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

public class ContactUsActivity extends AppCompatActivity implements ContactUsContract.View {

    MaterialTextView mobileNumber1, whatsAppNumber,mobileNumber2,mail,withdrawProof,telegram;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    ContactUsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_contact_us);
        presenter = new ContactUsPresenter(this);
        intIDs();
        MaterialToolbar toolbar = findViewById(R.id.appbarLayout).findViewById(R.id.toolbar);
        toolbar.setTitle("Contact Us");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void intIDs() {
        mobileNumber1 = findViewById(R.id.mobileNumber1);
        mobileNumber2 = findViewById(R.id.mobileNumber2);
        whatsAppNumber = findViewById(R.id.whatsAppNumber);
        mail = findViewById(R.id.mail);
        telegram = findViewById(R.id.telegram);
        withdrawProof = findViewById(R.id.withdrawProof);
        LottieAnimationView playIcon = findViewById(R.id.callanima);
        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);



        mobileNumber1.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_CONTACT_NUMBER1));
        mobileNumber2.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_CONTACT_NUMBER2));
        whatsAppNumber.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_WHATSAPP_NUMBER));
        withdrawProof.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_WITHDRAW_PROOF));
        telegram.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_TELEGRAM));
        mail.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_CONTACT_EMAIL));
    }
    public void mobileNumber1(View view) {
       presenter.call(this);
    }
    public void mobileNumber2(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 100);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
             callIntent.setData(Uri.parse("tel:" + mobileNumber2.getText()));
            startActivity(callIntent);
        }
    }
    public void whatsAppNumber(View view) {
        presenter.whatsapp(this);
    }
    public void mail(View view) {
        presenter.mail(this);
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
    public void message(String msg) {

    }

    public void withdrawProof(View view) {
        presenter.withdrawProof(this);
    }

    public void telegram(View view) {
        presenter.telegram(this);
    }
}