package com.old_dummy.cc.StarLineActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Adapters.StarlineGameListAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.StarlineGameListModel;
import com.old_dummy.cc.MyHistoryActivity.MyHistoryActivity;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;
import com.old_dummy.cc.StarLineBidPlacedActivity.StarLineBidPlacedActivity;
import com.old_dummy.cc.StarlineGameActivity.StarLineGameActivity;

import java.util.ArrayList;
import java.util.List;

public class StarLineActivity extends AppCompatActivity implements StarLineContract.View{

    MaterialToolbar toolbar;
    RecyclerView recyclerStarLine;
    StarlineGameListAdapter starlineGameListAdapter;
    MaterialTextView singleDigitValue,singlePanaValue,doublePanaValue,triplePanaValue;
    MaterialTextView singleDigitEarningAmount,singlePanaEarningAmount,doublePanaEarningAmount,triplePanaEarningAmount;
    String chartURL = "";
    List<MaterialTextView> digitValue = new ArrayList<>();
    List<MaterialTextView> earningDigitValue = new ArrayList<>();
    List<StarlineGameListModel.Data.StarlineGame> starlineGameList = new ArrayList<>();
    Vibrator vibe;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    StarLineContract.Presenter presenter;
    ArrayList<String> nameList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_star_line);
        intIDs();

        digitValue.add(singleDigitValue);
        digitValue.add(singlePanaValue);
        digitValue.add(doublePanaValue);
        digitValue.add(triplePanaValue);


        earningDigitValue.add(singleDigitValue);
        earningDigitValue.add(singlePanaValue);
        earningDigitValue.add(doublePanaValue);
        earningDigitValue.add(triplePanaValue);

        configureRecycler();
        toolbar.setTitle("Starline Game");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.api(SharPrefHelper.getLogInToken(StarLineActivity.this));
            }
        });

    }

    private void configureRecycler() {
        starlineGameListAdapter = new StarlineGameListAdapter(this, starlineGameList, new StarlineGameListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(StarlineGameListModel.Data.StarlineGame starlineGame, View itemView) {
                if (!starlineGame.isPlay()){
                    ObjectAnimator
                            .ofFloat(itemView, "translationX", 0, 25, -25, 25, -25,15, -15, 6, -6, 0)
                            .setDuration(700)
                            .start();
                    vibe.vibrate(500);
                }else {
                    Intent intent = new Intent(StarLineActivity.this, StarLineGameActivity.class);
                    intent.putExtra(getString(R.string.game), starlineGame.getId());
                    intent.putExtra(getString(R.string.game_name), starlineGame.getName());
                    startActivity(intent);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerStarLine.setLayoutManager(layoutManager);
        recyclerStarLine.setAdapter(starlineGameListAdapter);
    }

    private void intIDs() {
        toolbar = findViewById(R.id.toolbar);
        recyclerStarLine = findViewById(R.id.recyclerStarLine);

        singleDigitValue = findViewById(R.id.singleCoastAmount);
        singlePanaValue = findViewById(R.id.singlePannaCoastAmount);
        doublePanaValue = findViewById(R.id.doublePannaCoastAmount);
        triplePanaValue = findViewById(R.id.tripalePannaCoastAmount);

        singleDigitEarningAmount = findViewById(R.id.singleEarningAmount);
        singlePanaEarningAmount = findViewById(R.id.singlePannaEarningAmount);
        doublePanaEarningAmount= findViewById(R.id.doublePannaEarningAmount);
        triplePanaEarningAmount = findViewById(R.id.tripalePannaEarningAmount);


        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        presenter =new StarLinePresenter(this);
        dataConText = findViewById(R.id.dataConText);
        nameList = new ArrayList<>();
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        presenter.api(SharPrefHelper.getLogInToken(this));
    }

    public void chart(View view) {
        presenter.chart(this, nameList);
    }

    public void winHistory(View view) {
        presenter.History(this, 300);
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
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void apiResponse(StarlineGameListModel.Data data) {
        chartURL = data.getStarlineChart();
        List<StarlineGameListModel.Data.StarlineGame> gameList = data.getStarlineGame();
        for (int i=0; i <gameList.size(); i++){
            nameList.add(gameList.get(i).getName());
        }
        List<StarlineGameListModel.Data.StarlineRates> starlineRatesList = data.getStarlineRates();
        for (int i = 0; i < starlineRatesList.size(); i++) {
            String value = starlineRatesList.get(i).getCost_amount();
            String earning = starlineRatesList.get(i).getEarning_amount();
            digitValue.get(i).setText(value);
            earningDigitValue.get(i).setText(earning);
        }
        starlineGameList = data.getStarlineGame();
        configureRecycler();
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

    public void history(View view) {
        startActivity(new Intent(this, StarLineBidPlacedActivity.class));
    }
}