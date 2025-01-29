package com.old_dummy.cc.GameProceedActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.annotation.SuppressLint;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.old_dummy.cc.Adapters.GameProceedAdapter;
import com.old_dummy.cc.Adapters.SelectDigitAdapter;
import com.old_dummy.cc.Adapters.SubmitGameAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.LoginActivity.LoginActivity;
import com.old_dummy.cc.Models.GameProceedModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GameProceedActivity extends AppCompatActivity implements GameProceedContract.View {

    int gameProceed=0;
    int totalPoints = 0;
    int currentPoints = 0;
    String gameID;
    String points="";
    String digitOrPanna="Digit";
    String digitOrPanna2="Digit";
    boolean isOpen = true;
    boolean bidOpen = false;
    boolean bidClose = false;
    MaterialToolbar toolbar;
    MaterialTextView chooseDate,mtv_totalPoints,walletAmount;
    MaterialAutoCompleteTextView inputDigit,inputCloseDigit;
    TextInputEditText inputPoints;
    MaterialRadioButton openLlout, closeLlout;
    MaterialButton btn_proceed;
    ArrayList<String> numbers, numbers2;
    ArrayAdapter<String> adapter,adapter2;
    RecyclerView recyclerView;
    List<GameProceedModel> gameProceedModelList = new ArrayList<>();
    ArrayList<String> selectedList;
    ArrayList<String> selectedList2;
    GameProceedAdapter gameProceedAdapter;
    SubmitGameAdapter submitGameAdapter;
    FrameLayout progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    String gamesName="";
    MaterialButton proceedConform;
    AlertDialog dialog;
    PopupWindow popupWindow;
    String sangam = "";
    String sangam2 = "";
    LinearLayout ll_bid_bottom;

    GameProceedContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_game_proceed);
        intIDs();
        configureToolbar();
        configureRecycler();
        openLlout.setOnClickListener(view -> {
            bidOpen=true;
        });
        closeLlout.setOnClickListener( view -> {
            bidClose=true;
        });
    }

    private void configureRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        gameProceedAdapter = new GameProceedAdapter(this, gameProceedModelList, new GameProceedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                int index = position;
                int size = gameProceedModelList.size();
                if(position-size>=0){
                    index = size-1;
                }
                int bid_points = Integer.parseInt(gameProceedModelList.get(index).getBid_points());
                totalPoints = totalPoints - bid_points;
                mtv_totalPoints.setText("Total Points\n"+totalPoints);
                gameProceedModelList.remove(index);

                if(gameProceedModelList.isEmpty()) ll_bid_bottom.setVisibility(View.GONE);
                gameProceedAdapter.notifyItemRemoved(position);
                walletAmount.setText(String.valueOf(currentPoints - totalPoints));
                gameProceedAdapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(gameProceedAdapter);


    }

    private void intIDs() {
        toolbar = findViewById(R.id.toolbar);
        chooseDate = findViewById(R.id.chooseDate);
        inputDigit = findViewById(R.id.inputDigit);
        inputPoints = findViewById(R.id.inputPoints);
        inputCloseDigit = findViewById(R.id.inputCloseDigit);
        mtv_totalPoints = findViewById(R.id.mtv_totalPoints);
        openLlout = findViewById(R.id.open);
        closeLlout = findViewById(R.id.close);
        proceedConform = findViewById(R.id.proceedConform);
        btn_proceed = findViewById(R.id.btn_proceed);
        recyclerView = findViewById(R.id.recyclerView);
        ll_bid_bottom = findViewById(R.id.ll_bid_bottom);
        walletAmount = findViewById(R.id.walletAmount);
        progressBar = findViewById(R.id.progressBar);
        presenter = new GameProceedPresenter(this);
        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);


        gameProceed = getIntent().getIntExtra(getString(R.string.game_name), 0);
        gameID = getIntent().getStringExtra("games");
        gamesName = getIntent().getStringExtra("gamesName");
        isOpen = getIntent().getBooleanExtra("open",false);
        //System.out.println("game "+isOpen);
        currentPoints = Integer.parseInt(SharPrefHelper.getUserPoints(GameProceedActivity.this));
        numbers = new ArrayList<String>();
        numbers2 = new ArrayList<String>();
        selectedList = new ArrayList<String>();
        selectedList2 = new ArrayList<String>();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("EEE dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        chooseDate.setText(formattedDate);
//        open.setEnabled(isOpen);
//        open.setChecked(isOpen);
//        close.setChecked(!isOpen);
        walletAmount.setText(String.valueOf(currentPoints));
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed(v);
            }
        });

        if (gameProceed==6){
//            open.setChecked(true);
            inputCloseDigit.setVisibility(View.VISIBLE);
        }
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (gameProceed!=2)
//                    configureToolbar();
//            }
//        });

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

        proceedConform.setOnClickListener(v -> {
            //proceedConform(v);
            String gsonData = new Gson().toJson(gameProceedModelList);
            String serverData = getString(R.string.bids_api_open)+gsonData+getString(R.string.bids_api_close);
            if (YourService.isOnline(GameProceedActivity.this))
                presenter.api(SharPrefHelper.getLogInToken(this),serverData,v);
            else Toast.makeText(GameProceedActivity.this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
        });


    }

    private void selectDigit() {
        View view = getLayoutInflater().inflate(R.layout.select_digit_layout, null);
        MaterialButton doneBtn = view.findViewById(R.id.doneBtn);
        MaterialTextView selectDigitText = view.findViewById(R.id.selectDigitText);
        selectDigitText.setText("Select "+digitOrPanna);
        SearchView searchView = view.findViewById(R.id.search);
        RecyclerView recyclerView = view.findViewById(R.id.digitList);
        if (gameProceed==6 || gameProceed ==7){
            doneBtn.setVisibility(View.INVISIBLE);
        }
        SelectDigitAdapter adapter = new SelectDigitAdapter(this, numbers,gameProceed,selectedList , position -> {
            sangam = position;
            inputDigit.setText(position);
            setRecycleData(gameProceed);
            popupWindow.dismiss();
        });
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<String> arrayList =new ArrayList<>();
                for (String item : numbers) {
                    if (item.toUpperCase(Locale.ROOT).contains(s.toUpperCase(Locale.ROOT))){
                        arrayList.add(item);
                    }
                }
                if (arrayList.isEmpty()){
                    Toast.makeText(GameProceedActivity.this, "No Digit Found", Toast.LENGTH_SHORT).show();
                }else {
                    adapter.filterList(arrayList);
                }
                return false;
            }
        });

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it

        popupWindow = new PopupWindow(view, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        doneBtn.setOnClickListener(view1 ->  {
            //viewModel.addSingleDigitBid()
            setRecycleData(gameProceed);
            popupWindow.dismiss();
        });
    }

    private void selectDigit2() {
        View view = getLayoutInflater().inflate(R.layout.select_digit_layout, null);
        MaterialButton doneBtn = view.findViewById(R.id.doneBtn);
        MaterialTextView selectDigitText = view.findViewById(R.id.selectDigitText);
        selectDigitText.setText("Select "+digitOrPanna2);
        SearchView searchView = view.findViewById(R.id.search);
        RecyclerView recyclerView = view.findViewById(R.id.digitList);
        if (gameProceed==6 || gameProceed ==7){
            doneBtn.setVisibility(View.INVISIBLE);
        }
        SelectDigitAdapter adapter = new SelectDigitAdapter(this, numbers2,gameProceed,selectedList2 , position -> {
            sangam2 = position;
            inputCloseDigit.setText(position);
            setRecycleData(gameProceed);
            popupWindow.dismiss();
        });
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<String> arrayList =new ArrayList<>();
                for (String item : numbers2) {
                    if (item.toUpperCase(Locale.ROOT).contains(s.toUpperCase(Locale.ROOT))){
                        arrayList.add(item);
                    }
                }
                if (arrayList.isEmpty()){
                    Toast.makeText(GameProceedActivity.this, "No Digit Found", Toast.LENGTH_SHORT).show();
                }else {
                    adapter.filterList(arrayList);
                }
                return false;
            }
        });

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(view, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        doneBtn.setOnClickListener(view1 ->  {
            //viewModel.addSingleDigitBid()
            setRecycleData(gameProceed);
            popupWindow.dismiss();
        });
    }


    private void configureToolbar() {
        numbers.clear();
        numbers2.clear();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        switch (gameProceed){
            case 1:
                inputDigit.setHint("Enter Single Digit");
                digitOrPanna = "Digit";
                toolbar.setTitle(getString(R.string.single_digit));
                for (int i = 0; i <= 9; i++) {
                    numbers.add(String.valueOf(i));
                }
                break;
            case 2:
                inputDigit.setHint("Enter Jodi Digit");
                digitOrPanna = "Digit";
                toolbar.setTitle(getString(R.string.jodi_digit));
//                radioGroup.setVisibility(View.GONE);
                for (int i = 0; i <= 9; i++) {
                    for (int j = 0; j<=9; j++){
                        numbers.add(String.valueOf(i)+String.valueOf(j));
                    }
                }
                break;
            case 3:
                inputDigit.setHint("Enter Single Panna");
                digitOrPanna = "Panna";
                toolbar.setTitle(getString(R.string.single_pana));
                for (int a =1 ; a<=9; a++){
                    for (int b = 1;b<=9;b++){
                        for (int c = 0;c<=9;c++){
                            if(a!=b && a!=c && b!=c){
                                if (a < b && b<c||c==0&& a<b){
                                    numbers.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                                }
                            }
                        }
                    }
                }
                break;
            case 4:
                inputDigit.setHint("Enter Double Panna");
                digitOrPanna = "Panna";
                toolbar.setTitle(getString(R.string.double_pana));
                for (int a =1 ; a<=9; a++){
                    for (int b = 0;b<=9;b++){
                        for (int c = 0;c<=9;c++){
                            if(a == b && b < c || b == 0 && c == 0 || a == b && c == 0||a<b && b==c){
                                numbers.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                            }
                        }
                    }
                }
                break;
            case 5:
                inputDigit.setHint("Enter Triple Panna");
                digitOrPanna = "Panna";
                toolbar.setTitle(getString(R.string.triple_pana));
                for (int a =0 ; a<=9; a++){
                    for (int b = 0;b<=9;b++){
                        for (int c = 0;c<=9;c++){
                            if(a == b && b == c ){
                                numbers.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                            }
                        }
                    }
                }
                break;
            case 6:
                toolbar.setTitle(getString(R.string.half_sangam));
                if (bidOpen){
                    inputDigit.setHint("Enter Open Digit");
                    inputCloseDigit.setHint("Enter Close Panna");
                    digitOrPanna = "Digit";
                    digitOrPanna2 = "Panna";
                    for (int a =0 ; a<=9; a++){
                        numbers.add(String.valueOf(a));
                    }
                    for (int a =0 ; a<=9; a++){
                        for (int b = 0;b<=9;b++){
                            for (int c = 0;c<=9;c++){
                                if(a > 0 && a <= b && b <= c || b == 0 && c == 0 || c == 0 && a <= b &&a!=0){
                                    numbers2.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                                }
                            }
                        }
                    }
                }else if (bidClose){
                    inputDigit.setHint("Enter Open Panna");
                    inputCloseDigit.setHint("Enter Close Digit");
                    digitOrPanna = "Panna";
                    digitOrPanna2 = "Digit";
                    for (int a =0 ; a<=9; a++){
                        for (int b = 0;b<=9;b++){
                            for (int c = 0;c<=9;c++){
                                if(a > 0 && a <= b && b <= c || b == 0 && c == 0 || c == 0 && a <= b &&a!=0){
                                    numbers.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                                }
                            }
                        }
                    }
                    for (int a =0 ; a<=9; a++){
                        numbers2.add(String.valueOf(a));
                    }
                }
                break;
            case 7:
                inputDigit.setHint("Enter Open Panna");
                inputCloseDigit.setHint("Enter Close Panna");
                digitOrPanna = "Panna";
                digitOrPanna2 = "Panna";
//                radioGroup.setVisibility(View.GONE);
                inputCloseDigit.setVisibility(View.VISIBLE);
                toolbar.setTitle(getString(R.string.full_sangam));
                for (int a =0 ; a<=9; a++){
                    for (int b = 0;b<=9;b++){
                        for (int c = 0;c<=9;c++){
                            if(a > 0 && a <= b && b <= c || b == 0 && c == 0 || c == 0 && a <= b &&a!=0){
                                numbers.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                                numbers2.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
                            }
                        }
                    }
                }

                break;
        }

        adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,numbers);
        adapter2 = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,numbers2);

        inputDigit.setThreshold(1);//will start working from first character
        inputDigit.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView

        inputCloseDigit.setThreshold(1);
        inputCloseDigit.setAdapter(adapter2);
        if (gameProceed!=6){
            int maxLength = numbers.get(0).length() ;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            inputDigit.setFilters(fArray);

            int maxLength2 = 3 ;
            InputFilter[] fArray2 = new InputFilter[1];
            fArray2[0] = new InputFilter.LengthFilter(maxLength2);
            inputCloseDigit.setFilters(fArray2);
        }else {
            int maxLength = 3 ;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            inputDigit.setFilters(fArray);
            inputCloseDigit.setFilters(fArray);
        }
    }
    public void proceed(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (TextUtils.isEmpty(inputDigit.getText().toString())){
            snackbar(getString(R.string.please_enter_digits),view);
            return;
        }
        switch (gameProceed){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                if(!bidOpen && !bidClose){
                    snackbar("Please select session", view);
                    return;
                }
                if (!numbers.contains(inputDigit.getText().toString())){
                    snackbar(getString(R.string.please_enter_valid_digits), view);
                    return;
                }
                if (TextUtils.isEmpty(inputPoints.getText().toString().trim())){
                    snackbar(getString(R.string.please_enter_points),view);
                    return;
                }
                if (Integer.parseInt(inputPoints.getText().toString().trim())<Integer.parseInt(SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MIN_BID_AMOUNT))
                        ||Integer.parseInt(inputPoints.getText().toString().trim())>Integer.parseInt(SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MAX_BID_AMOUNT))){
                    snackbar("Minimum Bid Points "+SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MIN_BID_AMOUNT)+" and Maximum Bid Points "+SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MAX_BID_AMOUNT),view);
                    return;
                }
                setRecycleData(gameProceed);
                break;
            case 6:
                if(!bidOpen && !bidClose){
                    snackbar("Please select session", view);
                    return;
                }
                if (bidOpen){
                    if (!numbers.contains(inputDigit.getText().toString())){
                        snackbar(getString(R.string.please_enter_valid_open_digits), view);
                        return;
                    }
                    if (TextUtils.isEmpty(inputCloseDigit.getText().toString().trim())){
                        snackbar(getString(R.string.please_enter_close_pana), view);
                        return;
                    }
                    if (!numbers2.contains(inputCloseDigit.getText().toString())){
                        snackbar(getString(R.string.please_enter_valid_close_pana), view);
                        return;
                    }
                }else {
                    if (!numbers.contains(inputDigit.getText().toString())){
                        snackbar(getString(R.string.please_enter_valid_open_pana), view);
                        return;
                    }
                    if (TextUtils.isEmpty(inputCloseDigit.getText().toString().trim())){
                        snackbar(getString(R.string.please_enter_close_digits), view);
                        return;
                    }
                    if (!numbers2.contains(inputCloseDigit.getText().toString())){
                        snackbar(getString(R.string.please_enter_valid_close_digits), view);
                        return;
                    }
                }
                if (TextUtils.isEmpty(inputPoints.getText().toString().trim())){
                    snackbar(getString(R.string.please_enter_points),view);
                    return;
                }
                if (Integer.parseInt(inputPoints.getText().toString().trim())<Integer.parseInt(SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MIN_BID_AMOUNT))
                        ||Integer.parseInt(inputPoints.getText().toString().trim())>Integer.parseInt(SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MAX_BID_AMOUNT))){
                    snackbar("Minimum Bid Points "+SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MIN_BID_AMOUNT)+" and Maximum Bid Points "+SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MAX_BID_AMOUNT),view);
                    return;
                }
                setRecycleData(gameProceed);
                break;
            case 7:
                if (!numbers.contains(inputDigit.getText().toString())){
                    snackbar(getString(R.string.please_enter_valid_open_pana), view);
                    return;
                }
                if (TextUtils.isEmpty(inputCloseDigit.getText().toString().trim())){
                    snackbar(getString(R.string.please_enter_close_pana), view);
                    return;
                }
                if (!numbers2.contains(inputCloseDigit.getText().toString())){
                    snackbar(getString(R.string.please_enter_valid_close_pana), view);
                    return;
                }
                if (TextUtils.isEmpty(inputPoints.getText().toString().trim())){
                    snackbar(getString(R.string.please_enter_points),view);
                    return;
                }
                if (Integer.parseInt(inputPoints.getText().toString().trim())<Integer.parseInt(SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MIN_BID_AMOUNT))
                        ||Integer.parseInt(inputPoints.getText().toString().trim())>Integer.parseInt(SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MAX_BID_AMOUNT))){
                    snackbar("Minimum Bid Points "+SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MIN_BID_AMOUNT)+" and Maximum Bid Points "+SharPrefHelper.getMinMaxData(this,SharPrefHelper.KEY_MAX_BID_AMOUNT),view);
                    return;
                }
                setRecycleData(gameProceed);
                break;
        }

    }

    @SuppressLint("SetTextI18n")
    private void setRecycleData(int gameProceed) {
        String openNum = inputDigit.getText().toString();
        String closeNum = inputCloseDigit.getText().toString();
        String points = inputPoints.getText().toString();
        totalPoints += Integer.parseInt(points);
        if(totalPoints>currentPoints){
            message("Insufficient Points");
            totalPoints = totalPoints - Integer.parseInt(points);
            return;
        }
        String userPoints = String.valueOf(currentPoints-totalPoints);
        walletAmount.setText(userPoints);
        switch (gameProceed){
            case 1:
                if(bidOpen){
                    gameProceedModelList.add(new GameProceedModel(gameID,"single_digit","Open",points,openNum,"","",""));
                }
                else{
                    gameProceedModelList.add(new GameProceedModel(gameID,"single_digit","Close",points,"",openNum,"",""));
                }

                break;
            case 2:
                String open_digit = openNum.substring(0,1);
                String close_digit = openNum.substring(1,2);
                gameProceedModelList.add(new GameProceedModel(gameID,"jodi_digit","Open",points,open_digit,close_digit,"",""));
                break;
            case 3:
                if(bidOpen){
                    gameProceedModelList.add(new GameProceedModel(gameID,"single_panna","Open",points,"","",openNum,""));
                }
                else{
                    gameProceedModelList.add(new GameProceedModel(gameID,"single_panna","Close",points,"","","",openNum));
                }
                break;
            case 4:
                if(bidOpen){
                    gameProceedModelList.add(new GameProceedModel(gameID,"double_panna","Open",points,"","",openNum,""));
                }
                else{
                    gameProceedModelList.add(new GameProceedModel(gameID,"double_panna","Close",points,"","","",openNum));
                }
                break;
            case 5:
                if(bidOpen){
                    gameProceedModelList.add(new GameProceedModel(gameID,"triple_panna","Open",points,"","",openNum,""));
                }
                else{
                    gameProceedModelList.add(new GameProceedModel(gameID,"triple_panna","Close",points,"","","",openNum));
                }
                break;
            case 6:
                if(bidOpen){
                    gameProceedModelList.add(new GameProceedModel(gameID,"half_sangam","Open",points,openNum,"","",closeNum));
                }
                else{
                    gameProceedModelList.add(new GameProceedModel(gameID,"half_sangam","Close",points,"",closeNum,openNum,""));
                }
                break;
            case 7:
                gameProceedModelList.add(new GameProceedModel(gameID,"full_sangam","Open",points,"","",openNum,closeNum));
                break;
        }
        inputCloseDigit.setText("");
        inputDigit.setText("");
        inputPoints.setText("");
        inputDigit.requestFocus();
        if (isOpen)
//            radioGroup.clearCheck();

        recyclerView.setVisibility(View.VISIBLE);
        ll_bid_bottom.setVisibility(View.VISIBLE);
        mtv_totalPoints.setText("Total Points\n"+totalPoints);
        gameProceedAdapter.notifyDataSetChanged();
        if(gameProceed==2){
            bidOpen=true;
        }
        if (gameProceed ==6){
            bidOpen=true;
            inputCloseDigit.setVisibility(View.VISIBLE);
            inputDigit.setHint("Enter Open Digit");
            inputCloseDigit.setHint("Enter Close Panna");
        }
    }
    private void snackbar(String messaage, View view) {
        Snackbar.make(view,messaage, 2000).show();
    }

    public void proceedConform(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameProceedActivity.this);
        LayoutInflater inflater = LayoutInflater.from(GameProceedActivity.this);
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
        submitGameAdapter = new SubmitGameAdapter(this, gameProceedModelList);
        recyclerView1.setAdapter(submitGameAdapter);
        totalBid.setText(String.valueOf(gameProceedModelList.size()));
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
            String gsonData = new Gson().toJson(gameProceedModelList);
            String serverData = getString(R.string.bids_api_open)+gsonData+getString(R.string.bids_api_close);
            if (YourService.isOnline(GameProceedActivity.this))
                presenter.api(SharPrefHelper.getLogInToken(this),serverData,v);
            else Toast.makeText(GameProceedActivity.this, getString(R.string.check_your_internet_connection), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

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
        SharPrefHelper.setUserPoints(GameProceedActivity.this, walletAmount.getText().toString());
        gameProceedModelList.clear();
        selectedList.clear();
        selectedList2.clear();
        inputPoints.setText("");
        inputDigit.setText("");
        inputCloseDigit.setText("");
        sangam = "";
        sangam2 = "";
        gameProceedAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.GONE);
        ll_bid_bottom.setVisibility(View.GONE);
        snackbar("Bid Successfully Submitted",view);
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