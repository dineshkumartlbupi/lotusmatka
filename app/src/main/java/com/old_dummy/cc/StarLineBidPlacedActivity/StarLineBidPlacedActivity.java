package com.old_dummy.cc.StarLineBidPlacedActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.old_dummy.cc.Adapters.SelectDigitAdapter;
import com.old_dummy.cc.Adapters.StarLineSubmitGameAdapter;
import com.old_dummy.cc.Adapters.StarlineBidAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.StarlineBidModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StarLineBidPlacedActivity extends AppCompatActivity implements StarLineBidPlacedContract.View {

    int gameProceed = 0;
    int totalPoints = 0;
    int currentPoints = 0;
    String gameID;
    String points = "";
    String digitOrPanna = "";
    boolean isOpen = false;
    MaterialToolbar toolbar;
    MaterialTextView chooseDate, mtv_totalPoints;
    MaterialAutoCompleteTextView inputDigit;
    LinearLayout ll_bid_bottom;
    TextInputEditText inputPoints;
    MaterialButton btn_proceed;
    MaterialButton proceedConform;
    ArrayList<String> numbers;
    ArrayAdapter<String> adapter;
    RecyclerView recyclerView;
    List<StarlineBidModel> starlineBidModelList = new ArrayList<>();
    ArrayList<String> selectedList;
    StarlineBidAdapter starlineBidAdapter;
    StarLineSubmitGameAdapter starLineSubmitGameAdapter;
    ProgressBar progressBar;
    MaterialTextView dataConText, walletAmount;
    IntentFilter mIntentFilter;
    Utility utility;
    String gamesName = "";
    StarLineBidPlacedContract.Presenter presenter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_star_line_bid_placed);
        intIDs();
        configureToolbar();
        configureRecycler();

        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
    }

    private void configureRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        starlineBidAdapter = new StarlineBidAdapter(this, starlineBidModelList, new StarlineBidAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                int index = position;
                int size = starlineBidModelList.size();
                if (position - size >= 0) {
                    index = size - 1;
                }
                int bid_points = Integer.parseInt(starlineBidModelList.get(index).getBid_points());
                totalPoints = totalPoints - bid_points;
                mtv_totalPoints.setText("Total Points\n" + totalPoints);
                starlineBidModelList.remove(index);

                if (starlineBidModelList.isEmpty()) ll_bid_bottom.setVisibility(View.GONE);
                starlineBidAdapter.notifyItemRemoved(position);
                walletAmount.setText(String.valueOf(currentPoints - totalPoints));
                starlineBidAdapter.notifyDataSetChanged();


            }
        });
        recyclerView.setAdapter(starlineBidAdapter);
    }

    private void intIDs() {
        toolbar = findViewById(R.id.toolbar);
        chooseDate = findViewById(R.id.chooseDate);
        inputDigit = findViewById(R.id.inputDigit);
        inputPoints = findViewById(R.id.inputPoints);
        mtv_totalPoints = findViewById(R.id.mtv_totalPoints);
        proceedConform = findViewById(R.id.proceedConform);
        btn_proceed = findViewById(R.id.btn_proceed);
        recyclerView = findViewById(R.id.recyclerView);
        ll_bid_bottom = findViewById(R.id.ll_bid_bottom);
        progressBar = findViewById(R.id.progressBar);
        dataConText = findViewById(R.id.dataConText);
//        walletAmount = findViewById(R.id.walletAmount);
        presenter = new StarLineBidPlacedPresenter(this);
        gameProceed = getIntent().getIntExtra(getString(R.string.game_name), 8);
        gamesName = getIntent().getStringExtra("gamesName");
        gameID = getIntent().getStringExtra("games");
//        walletAmount.setText(SharPrefHelper.getUserPoints(StarLineBidPlacedActivity.this));
        currentPoints = Integer.parseInt(SharPrefHelper.getUserPoints(StarLineBidPlacedActivity.this));
        numbers = new ArrayList<String>();
        selectedList = new ArrayList<String>();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEE dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        chooseDate.setText(formattedDate);
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed(v);
            }
        });

        inputPoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               /* points = s.toString();
                setRecycleData(gameProceed);*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void selectDigit() {
        View view = getLayoutInflater().inflate(R.layout.select_digit_layout, null);
        MaterialButton doneBtn = view.findViewById(R.id.doneBtn);
        MaterialTextView selectDigitText = view.findViewById(R.id.selectDigitText);
        selectDigitText.setText("Select " + digitOrPanna);
        SearchView searchView = view.findViewById(R.id.search);
        RecyclerView recyclerView = view.findViewById(R.id.digitList);
        SelectDigitAdapter adapter = new SelectDigitAdapter(this, numbers, gameProceed, selectedList, position -> {

        });
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (String item : numbers) {
                    if (item.toUpperCase(Locale.ROOT).contains(s.toUpperCase(Locale.ROOT))) {
                        arrayList.add(item);
                    }
                }
                if (arrayList.isEmpty()) {
                    Toast.makeText(StarLineBidPlacedActivity.this, "No Digit Found", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.filterList(arrayList);
                }
                return false;
            }
        });

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        doneBtn.setOnClickListener(view1 -> {
            //viewModel.addSingleDigitBid()
            setRecycleData(gameProceed);
            popupWindow.dismiss();
        });
    }


    private void configureToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        switch (gameProceed) {
            case 8:
                digitOrPanna = "Digit";
                actionbar.setTitle(getString(R.string.single_digit) + " (" + gamesName + ")");
                inputDigit.setHint("Enter Single Digit");
                for (int i = 0; i <= 9; i++) {
                    numbers.add(String.valueOf(i));
                }
                break;
            case 9:
                digitOrPanna = "Panna";
                actionbar.setTitle(getString(R.string.single_pana) + " (" + gamesName + ")");
                inputDigit.setHint("Enter Single Panna");
                for (int a = 1; a <= 9; a++) {
                    for (int b = 1; b <= 9; b++) {
                        for (int c = 0; c <= 9; c++) {
                            if (a != b && a != c && b != c) {
                                if (a < b && b < c || c == 0 && a < b) {
                                    numbers.add(String.valueOf(a) + String.valueOf(b) + String.valueOf(c));
                                }
                            }
                        }
                    }
                }
                break;
            case 10:
                digitOrPanna = "Panna";
                actionbar.setTitle(getString(R.string.double_pana) + " (" + gamesName + ")");
                inputDigit.setHint("Enter Double Panna");
                for (int a = 1; a <= 9; a++) {
                    for (int b = 0; b <= 9; b++) {
                        for (int c = 0; c <= 9; c++) {
                            if (a == b && b < c || b == 0 && c == 0 || a == b && c == 0 || a < b && b == c) {
                                numbers.add(String.valueOf(a) + String.valueOf(b) + String.valueOf(c));
                            }
                        }
                    }
                }
                break;
            case 11:
                digitOrPanna = "Panna";
                actionbar.setTitle(getString(R.string.triple_pana) + " (" + gamesName + ")");
                inputDigit.setHint("Enter Triple Panna");
                for (int a = 0; a <= 9; a++) {
                    for (int b = 0; b <= 9; b++) {
                        for (int c = 0; c <= 9; c++) {
                            if (a == b && b == c) {
                                numbers.add(String.valueOf(a) + String.valueOf(b) + String.valueOf(c));
                            }
                        }
                    }
                }
                break;
        }

        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, numbers);

        inputDigit.setThreshold(1);//will start working from first character
        inputDigit.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        int maxLength = numbers.get(0).length();
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        inputDigit.setFilters(fArray);

    }

    public void proceed(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(inputDigit.getText().toString())) {
            snackbar(getString(R.string.please_enter_digits), view);
            return;
        }
        if (!numbers.contains(inputDigit.getText().toString())) {
            snackbar(getString(R.string.please_enter_valid_digits), view);
            return;
        }
        if (TextUtils.isEmpty(inputPoints.getText().toString().trim())) {
            snackbar(getString(R.string.please_enter_points), view);
            return;
        }
        if (Integer.parseInt(inputPoints.getText().toString().trim()) < Integer.parseInt(SharPrefHelper.getMinMaxData(this, SharPrefHelper.KEY_MIN_BID_AMOUNT))
                || Integer.parseInt(inputPoints.getText().toString().trim()) > Integer.parseInt(SharPrefHelper.getMinMaxData(this, SharPrefHelper.KEY_MAX_BID_AMOUNT))) {
            snackbar("Minimum Bid Points " + SharPrefHelper.getMinMaxData(this, SharPrefHelper.KEY_MIN_BID_AMOUNT) + " and Maximum Bid Points " + SharPrefHelper.getMinMaxData(this, SharPrefHelper.KEY_MAX_BID_AMOUNT), view);
            return;
        }
        setRecycleData(gameProceed);

    }

    private void setRecycleData(int gameProceed) {
        String openNum = inputDigit.getText().toString();
        String points = inputPoints.getText().toString();
        totalPoints += Integer.parseInt(points);
        if (totalPoints > currentPoints) {
            message("Insufficient Points");
            totalPoints = totalPoints - Integer.parseInt(points);
            return;
        }
        String userPoints = String.valueOf(currentPoints - totalPoints);
        walletAmount.setText(userPoints);
        switch (gameProceed) {
            case 8:
                starlineBidModelList.add(new StarlineBidModel(gameID, "single_digit", points, openNum, ""));
                break;
            case 9:
                starlineBidModelList.add(new StarlineBidModel(gameID, "single_panna", points, "", openNum));
                break;
            case 10:
                starlineBidModelList.add(new StarlineBidModel(gameID, "double_panna", points, "", openNum));
                break;
            case 11:
                starlineBidModelList.add(new StarlineBidModel(gameID, "triple_panna", points, "", openNum));
                break;
        }
        inputDigit.setText("");
        inputPoints.setText("");
        inputDigit.requestFocus();
        recyclerView.setVisibility(View.VISIBLE);
        ll_bid_bottom.setVisibility(View.VISIBLE);
        mtv_totalPoints.setText("Total Points\n" + totalPoints);
        starlineBidAdapter.notifyDataSetChanged();

    }

    private void snackbar(String messaage, View view) {
        Snackbar.make(view, messaage, 2000).show();
    }

    public void proceedConform(View view) {

        String gsonData = new Gson().toJson(starlineBidModelList);
        String serverData = getString(R.string.bids_api_open) + gsonData + getString(R.string.bids_api_close);
        if (YourService.isOnline(this))
            presenter.api(SharPrefHelper.getLogInToken(this), serverData, view);
        else
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.submit_game_dialog_layout, null);
        builder.setView(dialogView);
        MaterialTextView gameName = dialogView.findViewById(R.id.gameName);
        MaterialTextView totalBid = dialogView.findViewById(R.id.totalBid);
        MaterialTextView totalBidAmount = dialogView.findViewById(R.id.tb_amount);
        MaterialTextView wb_deduction = dialogView.findViewById(R.id.wb_deduction);
        MaterialTextView wa_deduction = dialogView.findViewById(R.id.wa_deduction);
        AppCompatButton cancel_btn = dialogView.findViewById(R.id.cancel_button);
        AppCompatButton submit_btn = dialogView.findViewById(R.id.submitButton);
        RecyclerView recyclerView1 = dialogView.findViewById(R.id.recyclerView);
        gameName.setText(gamesName);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager);
        starLineSubmitGameAdapter = new StarLineSubmitGameAdapter(this, starlineBidModelList);
        recyclerView1.setAdapter(starLineSubmitGameAdapter);
        totalBid.setText(String.valueOf(starlineBidModelList.size()));
        totalBidAmount.setText(String.valueOf(totalPoints));
        wb_deduction.setText(String.valueOf(SharPrefHelper.getUserPoints(this)));
        wa_deduction.setText(walletAmount.getText().toString());
        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        cancel_btn.setOnClickListener(v -> {
            dialog.dismiss();
        });
        submit_btn.setOnClickListener(v -> {
                     String gsonData = new Gson().toJson(starlineBidModelList);
                     String serverData = getString(R.string.bids_api_open)+gsonData+getString(R.string.bids_api_close);
            if (YourService.isOnline(this))
                presenter.api(SharPrefHelper.getLogInToken(this),serverData,v);
            else Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });*/
    }


    public void playAgain(View view) {
        dialog.dismiss();
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
    public void apiResponse(View view) {
        SharPrefHelper.setUserPoints(StarLineBidPlacedActivity.this, walletAmount.getText().toString());
        starlineBidModelList.clear();
        selectedList.clear();
        inputPoints.setText("");
        mtv_totalPoints.setText("Total Points");
        starlineBidAdapter.notifyDataSetChanged();
        ll_bid_bottom.setVisibility(View.GONE);
        snackbar("Bid Successfully Submitted", view);
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