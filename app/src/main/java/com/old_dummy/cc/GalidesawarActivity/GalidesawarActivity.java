package com.old_dummy.cc.GalidesawarActivity;

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
import com.old_dummy.cc.Adapters.GalidesawarGameListAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.GalidesawarGameActivity.GalidesawarGameActivity;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.GalidesawarGameListModel;
import com.old_dummy.cc.MyHistoryActivity.MyHistoryActivity;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.util.ArrayList;
import java.util.List;

public class GalidesawarActivity extends AppCompatActivity implements GalidesawarContract.View{

    MaterialToolbar toolbar;
    RecyclerView recyclerGalidesawar;
    GalidesawarGameListAdapter galidesawarGameListAdapter;
    MaterialTextView leftDigitValue, rightDigitValue, jodiDigitValue;
    MaterialTextView leftDigitEarning, rightDigitEarning, jodiDigitEarning;
    String chartURL = "";
    List<MaterialTextView> digitValue = new ArrayList<>();
    List<MaterialTextView> digitEarning = new ArrayList<>();
    List<GalidesawarGameListModel.Data.GalidesawarGame> galidesawarGameList = new ArrayList<>();
    Vibrator vibe;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    GalidesawarContract.Presenter presenter;
    ArrayList<String> nameList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_galidesawar);
        intIDs();

        digitValue.add(leftDigitValue);
        digitValue.add(rightDigitValue);
        digitValue.add(jodiDigitValue);

        digitEarning.add(leftDigitEarning);
        digitEarning.add(rightDigitEarning);
        digitEarning.add(jodiDigitEarning);

        configureRecycler();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle("Galidesawar Game");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.api(SharPrefHelper.getLogInToken(GalidesawarActivity.this));
            }
        });

    }

    private void configureRecycler() {
        galidesawarGameListAdapter = new GalidesawarGameListAdapter(this, galidesawarGameList, new GalidesawarGameListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GalidesawarGameListModel.Data.GalidesawarGame galidesawarGame, View itemView) {
                if (!galidesawarGame.isPlay()){
                    ObjectAnimator
                            .ofFloat(itemView, "translationX", 0, 25, -25, 25, -25,15, -15, 6, -6, 0)
                            .setDuration(700)
                            .start();
                    vibe.vibrate(500);
                }else {
                    Intent intent = new Intent(GalidesawarActivity.this, GalidesawarGameActivity.class);
                    intent.putExtra(getString(R.string.game), galidesawarGame.getId());
                    intent.putExtra(getString(R.string.game_name), galidesawarGame.getName());
                    startActivity(intent);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerGalidesawar.setLayoutManager(layoutManager);
        recyclerGalidesawar.setAdapter(galidesawarGameListAdapter);
    }

    private void intIDs() {
        toolbar = findViewById(R.id.toolbar);
        recyclerGalidesawar = findViewById(R.id.recyclerGalidesawar);

        leftDigitValue = findViewById(R.id.leftCoastAmount);
        rightDigitValue = findViewById(R.id.rightCoastAmount);
        jodiDigitValue = findViewById(R.id.jodiCoastAmount);

        leftDigitEarning = findViewById(R.id.leftEarningAmount);
        rightDigitEarning = findViewById(R.id.rightEarningAmount);
        jodiDigitEarning = findViewById(R.id.jodiEarningAmount);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        presenter =new GalidesawarPresenter(this);
        nameList = new ArrayList<>();
        dataConText = findViewById(R.id.dataConText);
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
        presenter.History(this, 500,"Galidesawar Win History");
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
    public void apiResponse(GalidesawarGameListModel.Data data) {
        chartURL = data.getGalidesawrChart();
        List<GalidesawarGameListModel.Data.GalidesawarGame> gameList = data.getGalidesawrGame();
        for (int i=0; i <gameList.size(); i++){
            nameList.add(gameList.get(i).getName());
        }
        List<GalidesawarGameListModel.Data.GalidesawarRates> galidesawarRatesList = data.getGalidesawrRates();
        for (int i = 0; i < galidesawarRatesList.size(); i++) {
            String value = galidesawarRatesList.get(i).getCost_amount();
            String earning =  galidesawarRatesList.get(i).getEarning_amount();
            digitValue.get(i).setText(value);
            digitEarning.get(i).setText(earning);
        }
        galidesawarGameList = data.getGalidesawrGame();
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
        startActivity(new Intent(this, MyHistoryActivity.class));
    }
}