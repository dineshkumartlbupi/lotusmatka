package com.old_dummy.cc.FundsActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

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
import com.old_dummy.cc.Adapters.WalletAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.WalletStatementModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;
import com.old_dummy.cc.WalletActivity.WalletActivity;

import java.util.ArrayList;
import java.util.List;

public class FundsActivity extends AppCompatActivity implements FundsContract.View {

    RecyclerView recyclerViewWallet;
    WalletAdapter walletAdapter;
    List<WalletStatementModel.Data.Statement> modelWalletArrayList = new ArrayList<>();
    ShapeableImageView emptyIcon;
    MaterialTextView amount;
    SwipeRefreshLayout swipeRefreshLayout;
    Vibrator vibe;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    FundsContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_funds);
        intIDs();
        MaterialToolbar toolbar = findViewById(R.id.appbarLayout).findViewById(R.id.toolbar);
        toolbar.setTitle("Wallet");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.api(SharPrefHelper.getLogInToken(FundsActivity.this));
            }
        });
    }
    private void intIDs() {
        recyclerViewWallet = findViewById(R.id.recyclerViewWallet);
        emptyIcon = findViewById(R.id.emptyIcon);
        amount = findViewById(R.id.amount);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
        amount.setText(SharPrefHelper.getUserPoints(this));
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
        presenter = new FundsPresenter(this);
        presenter.api(SharPrefHelper.getLogInToken(FundsActivity.this));
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
    public void apiResponse(WalletStatementModel walletStatementModel) {
        SharPrefHelper.setUserPoints(this, walletStatementModel.getData().getAvailablePoints());
        amount.setText(walletStatementModel.getData().getAvailablePoints());

        modelWalletArrayList = walletStatementModel.getData().getStatement();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewWallet.setLayoutManager(layoutManager);
        walletAdapter = new WalletAdapter(this, modelWalletArrayList);
        recyclerViewWallet.setAdapter(walletAdapter);

        if (!modelWalletArrayList.isEmpty()){
            emptyIcon.setVisibility(View.GONE);
        }else {
            emptyIcon.setVisibility(View.VISIBLE);
        }
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
    public void fundItemClick(View view) {
        switch (view.getTag().toString()){
            case "addFund":
                presenter.addFund(this);
                break;
            case "withdraw":
                presenter.withdrawFund(this);
                break;
            case "transfer":
                if(SharPrefHelper.getTransferPoint(this)){
                    presenter.transferFund(this);
                }else {
                    Toast.makeText(this, "Transfer is not enable in your account", Toast.LENGTH_SHORT).show();
                }
                break;
            case "paymentMethods":
                presenter.paymentMethods(this);
                break;
            case "phonePe":
                presenter.upi(this, 3);
                break;
            case "paytm":
                presenter.upi(this, 1);
                break;
            case "gPay":
                presenter.upi(this, 2);
                break;
            case "wallet":
                presenter.wallet(this);
                break;
        }
    }
}