package com.old_dummy.cc.GameActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.R;

public class GameActivity extends AppCompatActivity implements GameActivityContract.View {

    MaterialToolbar toolbar;
    String games="";
    String gameName="";
    Boolean open = false;
    ShapeableImageView singleDigit,jodiDigit,singlePana,doublePana,triplePana,halfSangam,fullSangam;
    ProgressBar progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    GameActivityContract.Presenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        intIDs();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
    }


    private void intIDs() {
        toolbar = findViewById(R.id.toolbar);
        singleDigit = findViewById(R.id.singleDigit);
        jodiDigit = findViewById(R.id.jodiDigit);
        singlePana = findViewById(R.id.singlePana);
        doublePana = findViewById(R.id.doublePana);
        triplePana = findViewById(R.id.triplePana);
        halfSangam = findViewById(R.id.halfSangam);
        fullSangam = findViewById(R.id.fullSangam);
        progressBar = findViewById(R.id.progressBar);
        dataConText = findViewById(R.id.dataConText);

        presenter= new GameActivityPresenter(this);
        games = getIntent().getStringExtra(getString(R.string.game));
        gameName= getIntent().getStringExtra(getString(R.string.game_name));
        if (gameName!=null){
            toolbar.setTitle(gameName.toUpperCase());
        }
        open = getIntent().getBooleanExtra("open",false);

        if(!open){
            singleDigit.setVisibility(View.VISIBLE);
            jodiDigit.setVisibility(View.VISIBLE);
            singlePana.setVisibility(View.VISIBLE);
            doublePana.setVisibility(View.VISIBLE);
            triplePana.setVisibility(View.VISIBLE);
            halfSangam.setVisibility(View.VISIBLE);
            fullSangam.setVisibility(View.VISIBLE);
        }
        if(SharPrefHelper.getVipStatus(this)){
            halfSangam.setVisibility(View.GONE);
            fullSangam.setVisibility(View.GONE);
        }
    }

    public void singleDigit(View view) {
        presenter.gameClick(this,open,games,gameName,1);
    }

    public void jodiDigit(View view) {
        presenter.gameClick(this,open,games,gameName,2);
    }

    public void singlePana(View view) {
        presenter.gameClick(this,open,games,gameName,3);
    }

    public void doublePana(View view) {
        presenter.gameClick(this,open,games,gameName,4);
    }

    public void triplePana(View view) {
        presenter.gameClick(this,open,games,gameName,5);
    }

    public void halfSangam(View view) {
        presenter.gameClick(this,open,games,gameName,6);
    }

    public void fullSangam(View view) {
        presenter.gameClick(this,open,games,gameName,7);
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
}