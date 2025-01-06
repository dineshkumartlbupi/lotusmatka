package com.old_dummy.cc.GameRateActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.GameRateModel;
import com.old_dummy.cc.Models.HowToPlayModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameRatesActivity extends AppCompatActivity implements GameRatesContract.View{

    MaterialTextView singleDigitValue,jodiDigitValue,singlePanaValue,doubleDigitValue,tripleDigitValue,halfSangamValue,fullSangamValue;
    MaterialTextView singleDigitEarning,jodiDigitEarning,singlePanaEarning,doubleDigitEarning,tripleDigitEarning,halfSangamEarning,fullSangamEarning;
    int activity=0;
    MaterialTextView howToPlayText;
    View view1,view2;
    ProgressBar progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    String videoLink= "";
    LinearLayout gameRateLyt,howToPlayLyt;
    MaterialToolbar toolbar;

    GameRatesContract.Presenter presenter;

    List<MaterialTextView> digitValue = new ArrayList<>();
    List<MaterialTextView> digitEarning = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_game_rates);
        intIDs();
        LoadData();
        digitValue.add(singleDigitValue);
        digitValue.add(jodiDigitValue);
        digitValue.add(singlePanaValue);
        digitValue.add(doubleDigitValue);
        digitValue.add(tripleDigitValue);
        digitValue.add(halfSangamValue);
        digitValue.add(fullSangamValue);

        digitEarning.add(singleDigitEarning);
        digitEarning.add(jodiDigitEarning);
        digitEarning.add(singlePanaEarning);
        digitEarning.add(doubleDigitEarning);
        digitEarning.add(tripleDigitEarning);
        digitEarning.add(halfSangamEarning);
        digitEarning.add(fullSangamEarning);


    }

    private void intIDs() {
        gameRateLyt = findViewById(R.id.gameRateLyt);

//        Coast
        singleDigitValue = findViewById(R.id.singleCoastAmount);
        jodiDigitValue = findViewById(R.id.jodiCoastAmount);
        singlePanaValue = findViewById(R.id.singlePannaCoastAmount);
        doubleDigitValue = findViewById(R.id.doublePannaCoastAmount);
        tripleDigitValue = findViewById(R.id.tripalePannaCoastAmount);
        halfSangamValue = findViewById(R.id.halfSangamCoastAmount);
        fullSangamValue = findViewById(R.id.fullSangamCoastAmount);

//earning
        singleDigitEarning = findViewById(R.id.singleEarningAmount);
        jodiDigitEarning = findViewById(R.id.jodiEarningAmount);
        singlePanaEarning = findViewById(R.id.singlePannaEarningAmount);
        doubleDigitEarning= findViewById(R.id.doublePannaEarningAmount);
        tripleDigitEarning= findViewById(R.id.triplePannaEarningAmount);
        halfSangamEarning = findViewById(R.id.halfSangamEarningAmount);
        fullSangamEarning = findViewById(R.id.fullSangamEarningAmount);


        howToPlayText = findViewById(R.id.howToPlayText);
        howToPlayLyt = findViewById(R.id.howToPlay);
        progressBar = findViewById(R.id.progressBar);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);

        presenter = new GameRatesPresenter(this);

        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        activity = getIntent().getIntExtra(getString(R.string.main_activity), 0);

        toolbar = findViewById(R.id.appbarLayout).findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void LoadData() {
        if (activity==1){
            toolbar.setTitle(getString(R.string.game_rates));
            gameRateLyt.setVisibility(View.VISIBLE);
            presenter.gameRatesApi(SharPrefHelper.getLogInToken(this));
        }else if (activity==2){
            toolbar.setTitle(getString(R.string.how_to_play));
            howToPlayLyt.setVisibility(View.VISIBLE);
            presenter.howToPlayApi(SharPrefHelper.getLogInToken(this));

        }
    }



    public void WatchItOnYoutube(View view) {
        if (!Objects.equals(videoLink, "")){
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(videoLink));
            try {
                startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                startActivity(webIntent);
            }
        }else Toast.makeText(this, getString(R.string.youtube_video_not_added_yet), Toast.LENGTH_SHORT).show();
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

    String[] gameNames = {"Single Digit","Jodi Digit","Single Panna","Double Panna","Triple Panna","Half Sanagam","Full Sangam",};
    @Override
    public void gameRatesApiResponse(GameRateModel gameRateModel) {
        List<GameRateModel.Data> gameRateData = gameRateModel.getData();
        for (int i = 0; i < gameRateData.size(); i++) {
            String value  =gameRateData.get(i).getCost_amount();
            String earning = gameRateData.get(i).getEarning_amount();
            digitValue.get(i).setText(value);
            Log.e("Testing :: ",earning.toString());
            try {
                digitEarning.get(i).setText(earning);


            }catch (Exception e){
                Log.e("Testing :: ",e.toString());
            }
        }
    }

    @Override
    public void howToPlayApiResponse(HowToPlayModel.Data data) {
        howToPlayText.setText(data.getHowToPlay());
        videoLink = data.getVideoLink();
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