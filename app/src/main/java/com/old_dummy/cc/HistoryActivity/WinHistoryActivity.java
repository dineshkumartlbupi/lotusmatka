package com.old_dummy.cc.HistoryActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.old_dummy.cc.Adapters.BidHistoryAdapter;
import com.old_dummy.cc.Adapters.GalidesawarBidHistoryAdapter;
import com.old_dummy.cc.Adapters.GalidesawarWinHistoryAdapter;
import com.old_dummy.cc.Adapters.StarlineBidHistoryAdapter;
import com.old_dummy.cc.Adapters.StarlineWinHistoryAdapter;
import com.old_dummy.cc.Adapters.WinHistoryAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.Models.GalidesawarWinModel;
import com.old_dummy.cc.Models.StarLineWinModel;
import com.old_dummy.cc.Models.WinModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class WinHistoryActivity extends AppCompatActivity implements HistoryContract.View {

    MaterialToolbar toolbar;
    MaterialTextView fromDate, toDate;
    String fromDatestr;
    String toDatestr;
    Date fDate,tDate;
    SimpleDateFormat userSF = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    SimpleDateFormat serverSF = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
    final Calendar fromCal = Calendar.getInstance();
    final Calendar toCal = Calendar.getInstance();
    final Calendar todayCal = Calendar.getInstance();
    ShapeableImageView emptyIcon;

    int history=0;
    String title="History";
    RecyclerView recyclerView;
    WinHistoryAdapter winHistoryAdapter;
    BidHistoryAdapter bidHistoryAdapter;
    StarlineBidHistoryAdapter starlineBidHistoryAdapter;
    StarlineWinHistoryAdapter starlineWinHistoryAdapter;
    GalidesawarWinHistoryAdapter galidesawarWinHistoryAdapter;
    GalidesawarBidHistoryAdapter galidesawarBidHistoryAdapter;
    List<WinModel.Data> winModelArrayList = new ArrayList<>();
    List<StarLineWinModel.Data> starlineWinModelList = new ArrayList<>();
    List<GalidesawarWinModel.Data> galidesawarWinModelList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    Call<WinModel> call;
    Call<StarLineWinModel> starLineCall;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    HistoryContract.Presenter presenter;

    private Calendar calendar;
    private int year, month, day;

    private String ffDate = "";
    private String ttoDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_win_history);
        intIDs();
        configureToolbar();



        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set button click listeners
        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create DatePickerDialog for From Date
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        WinHistoryActivity.this,
                        R.style.CustomDatePickerDialog,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Update From Date
                            fromDatestr = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
                            fromDate.setText(fromDatestr);
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If From Date is selected, show the DatePicker for To Date
                // Create DatePickerDialog for To Date with minDate set to From Date
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        WinHistoryActivity.this,
                        R.style.CustomDatePickerDialog,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            // Ensure the selected To Date is after the From Date
                            toDatestr = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
                            toDate.setText(toDatestr);
                        },year,month,day);  // Set From Date as the min date
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });
    }


    private void intIDs() {
        toolbar = findViewById(R.id.toolbar);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        recyclerView = findViewById(R.id.recyclerView);
        emptyIcon = findViewById(R.id.emptyIcon);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        presenter = new HistoryPresenter(this);

        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);

        fDate = Calendar.getInstance().getTime();
        tDate = Calendar.getInstance().getTime();
        fromDate.setText(userSF.format(fDate));
        toDate.setText(userSF.format(tDate));
        history = getIntent().getIntExtra(getString(R.string.history), 0);
        title = getIntent().getStringExtra("from");


        if (history==100 || history ==200){
            historyMethod( fDate, tDate);
        }

        if (history==300 || history==400){
            winHistoryHistoryMethod( fDate, tDate);
        }

        if (history==500 || history==600){
            galidesawarHistoryMethod( fDate, tDate);
        }

    }

    private void galidesawarHistoryMethod(Date fDate, Date tDate) {
        String fromDate = serverSF.format(fDate) + " 00:00:00";
        String toDate = serverSF.format(tDate) + " 23:59:59";

        presenter.galidesawarHistoryApi(SharPrefHelper.getLogInToken(this),fromDate, toDate, history);
    }

    DatePickerDialog.OnDateSetListener fromDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            fromCal.set(Calendar.YEAR, year);
            fromCal.set(Calendar.MONTH, monthOfYear);
            fromCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            fDate = fromCal.getTime();
            fromDate.setText(userSF.format(fDate));
        }
    };

    DatePickerDialog.OnDateSetListener toDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            toCal.set(Calendar.YEAR, year);
            toCal.set(Calendar.MONTH, monthOfYear);
            toCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            if(toCal.getTimeInMillis()<fromCal.getTimeInMillis()){
                Toast.makeText(WinHistoryActivity.this, "To Date can't be smaller then From Date", Toast.LENGTH_SHORT).show();
                return;
            }
            tDate = toCal.getTime();
            toDate.setText(userSF.format(tDate));
        }
    };
    private void configureToolbar() {
       toolbar.setTitle(title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (history==100 || history ==200){
                    historyMethod( fDate, tDate);
                }
                if (history==300 || history==400){
                    winHistoryHistoryMethod( fDate, tDate);
                }
                if (history==500 || history==600){
                    galidesawarHistoryMethod( fDate, tDate);
                }

            }
        });
    }
    public void fromDate(View view) {
        
        DatePickerDialog datePickerDialog=  new DatePickerDialog(this,android.R.style.Theme_Holo_Light_Panel, fromDatePicker, fromCal
                .get(Calendar.YEAR), fromCal.get(Calendar.MONTH), fromCal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        long maxDate = todayCal.getTime().getTime() ;
        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        datePickerDialog.setCanceledOnTouchOutside(true);
        datePickerDialog.getDatePicker().setBackgroundColor(getResources().getColor(R.color.white));
        datePickerDialog.show();
    }

    public void toDate(View view) {
        DatePickerDialog datePickerDialog=  new DatePickerDialog(this,android.R.style.Theme_Holo_Light_Panel, toDatePicker, toCal
                .get(Calendar.YEAR), toCal.get(Calendar.MONTH), toCal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();


        long maxDate = todayCal.getTime().getTime() ;
        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        datePickerDialog.setCanceledOnTouchOutside(true);
        datePickerDialog.getDatePicker().setBackgroundColor(getResources().getColor(R.color.white));
        datePickerDialog.show();
    }



    public void submitWinHistory(View view) {

        if (history==100 || history ==200){
            historyMethod( fDate, tDate);
        }
        if (history==300 || history==400){
            winHistoryHistoryMethod(fDate, tDate);
        }
        if (history==500 || history==600){
            galidesawarHistoryMethod( fDate, tDate);
        }

    }

    private void historyMethod(Date fDate, Date tDate) {
        String fromDate = serverSF.format(fDate) + " 00:00:00";
        String toDate = serverSF.format(tDate) + " 23:59:59";
        presenter.mainHistoryApi(SharPrefHelper.getLogInToken(this),fromDate,toDate,history);
    }

    private void winHistoryHistoryMethod( Date fDate, Date tDate) {
        String fromDate = serverSF.format(fDate) + " 00:00:00";
        String toDate = serverSF.format(tDate) + " 23:59:59";

        presenter.starLineHistoryApi(SharPrefHelper.getLogInToken(this),fromDate, toDate, history);

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
    public void mainHistoryApiResponse(WinModel winModel) {
        if (winModel.getStatus().equalsIgnoreCase(getString(R.string.success))){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            winModelArrayList = winModel.getData();
            recyclerView.setLayoutManager(layoutManager);
            switch (history){
                case 100:
                    winHistoryAdapter = new WinHistoryAdapter(this, winModelArrayList);
                    recyclerView.setAdapter(winHistoryAdapter);
                    break;
                case 200:
                    bidHistoryAdapter = new BidHistoryAdapter(this, winModelArrayList);
                    recyclerView.setAdapter(bidHistoryAdapter);
                    break;
            }
        }
        if (winModel.getMessage().equalsIgnoreCase("No Record Found")){
            emptyIcon.setVisibility(View.VISIBLE);
        }else emptyIcon.setVisibility(View.GONE);

    }

    @Override
    public void starLineHistoryApiResponse(StarLineWinModel starLineWinModel) {
        if (starLineWinModel.getStatus().equalsIgnoreCase(getString(R.string.success))){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            starlineWinModelList = starLineWinModel.getData();
            recyclerView.setLayoutManager(layoutManager);
            switch (history){
                case 300:
                    starlineWinHistoryAdapter = new StarlineWinHistoryAdapter(this, starlineWinModelList);
                    recyclerView.setAdapter(starlineWinHistoryAdapter);
                    break;
                case 400:
                    starlineBidHistoryAdapter = new StarlineBidHistoryAdapter(this, starlineWinModelList);
                    recyclerView.setAdapter(starlineBidHistoryAdapter);
                    break;
            }
        }
        if (starLineWinModel.getMessage().equalsIgnoreCase("No Record Found")){
            emptyIcon.setVisibility(View.VISIBLE);
        }else emptyIcon.setVisibility(View.GONE);
    }
    @Override
    public void galidesawarHistoryApiResponse(GalidesawarWinModel starLineWinModel) {
        if (starLineWinModel.getStatus().equalsIgnoreCase(getString(R.string.success))){
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            galidesawarWinModelList = starLineWinModel.getData();
            recyclerView.setLayoutManager(layoutManager);
            switch (history){
                case 500:
                    galidesawarWinHistoryAdapter = new GalidesawarWinHistoryAdapter(this, galidesawarWinModelList);
                    recyclerView.setAdapter(galidesawarWinHistoryAdapter);
                    break;
                case 600:
                    galidesawarBidHistoryAdapter = new GalidesawarBidHistoryAdapter(this, galidesawarWinModelList);
                    recyclerView.setAdapter(galidesawarBidHistoryAdapter);
                    break;
            }
        }
        if (starLineWinModel.getMessage().equalsIgnoreCase("No Record Found")){
            emptyIcon.setVisibility(View.VISIBLE);
        }else emptyIcon.setVisibility(View.GONE);
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