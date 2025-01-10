package com.old_dummy.cc.MainActivity;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;
import static com.old_dummy.cc.Extras.Utility.myReceiver;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.old_dummy.cc.Adapters.GameListAdapter;
import com.old_dummy.cc.Adapters.MenuAdapter;
//import com.old_dummy.cc.Adapters.ViewPagerAdapter;
import com.old_dummy.cc.BaseActivity;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.Extras.YourService;
import com.old_dummy.cc.GalidesawarActivity.GalidesawarActivity;
import com.old_dummy.cc.GameActivity.GameActivity;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.Models.GameListModel;
import com.old_dummy.cc.Models.LoginModel;
import com.old_dummy.cc.Models.MenuItemModel;
import com.old_dummy.cc.Models.UserStatusModel;
import com.old_dummy.cc.NoticeActivity.NoticeActivity;
import com.old_dummy.cc.R;
import com.old_dummy.cc.SplashActivity.SplashActivity;
import com.old_dummy.cc.WithdrawActivity.WithdrawActivity;
//import com.old_dummy.cc.Adapters.ViewPagerAdapter;
//import com.smarteist.autoimageslider.SliderView;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements MainContract.View {
    TextView txtMarquee;
    MaterialToolbar toolbar;
    ImageView noGameIv;
    List<AppDetailsModel.Data.Banner> bannerList;
    FrameLayout stripLayout;
    RecyclerView recyclerView,navigationRecyclerView;
    GameListAdapter gameListAdapter;
    List<GameListModel.Data> dataList = new ArrayList<>();
    MaterialTextView whatsAppNumber, mobileNumber,walletAmount,pendingNoti,callNumber;
    public static MaterialTextView personName;
    ProgressBar progressBar;
    Vibrator vibe;
    SwipeRefreshLayout swipeRefreshLayout;
    MaterialCardView addFundLyt,playStarLineLty, galidesawarCard;
    LinearLayout openBottomSheet;
    SwitchMaterial notiSwitchBtn;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    LinearLayout fundLayout,naviagationMenu;
    MainContract.Presenter presenter;
    FrameLayout sivNotice;
    String welcomeMessage, from, pendingNotification,telegramLink;
    Boolean vipStatus;
    ShapeableImageView vipBadge;
    ConstraintLayout navigationHeader;
    List<MenuItemModel> menuItems;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.main_color));
        intIDs();
        configureToolbar();


        String string1 = "20:11:13";
        Date time1 = null;
        try {
            time1 = new SimpleDateFormat("HH:mm:ss").parse(string1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar1 = Calendar.getInstance();
        assert time1 != null;
        calendar1.setTime(time1);
        calendar1.add(Calendar.DATE, 1);



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (internetIsConnected()){
                    presenter.userStatusApi(SharPrefHelper.getLogInToken(MainActivity.this));
                    presenter.gameListApi(SharPrefHelper.getLogInToken(MainActivity.this));
                    presenter.appDetailsApi(SharPrefHelper.getLogInToken(MainActivity.this)==null?"":SharPrefHelper.getLogInToken(MainActivity.this));
                    presenter.userDetailsApi( SharPrefHelper.getLogInToken(MainActivity.this));
                }
                else Toast.makeText(MainActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        if (internetIsConnected()){
            presenter.appDetailsApi(SharPrefHelper.getLogInToken(this)==null?"":SharPrefHelper.getLogInToken(this));
           // presenter.userStatusApi(SharPrefHelper.getLogInToken(MainActivity.this));
            presenter.gameListApi(SharPrefHelper.getLogInToken(MainActivity.this));

            presenter.userDetailsApi( SharPrefHelper.getLogInToken(MainActivity.this));
        }
        else Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();


        openBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create BottomSheetDialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);

                // Inflate custom layout
                View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
                bottomSheetDialog.setContentView(bottomSheetView);

                // Close button action
                LinearLayout playStarLine = bottomSheetView.findViewById(R.id.playStarLine);
                LinearLayout galidesawar = bottomSheetView.findViewById(R.id.galidesawar);
                playStarLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        presenter.playStarLine(MainActivity.this);
                    }
                });

                galidesawar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, GalidesawarActivity.class);
                        startActivity(intent);
                    }
                });

                // Show BottomSheetDialog
                bottomSheetDialog.show();
            }
        });
    }




    private void updateProjectStatus() {
//        if(!SharPrefHelper.getBooleanData(this,SharPrefHelper.KEY_BANNER_STATUS,false)) viewPager.setVisibility(View.GONE);
//        if(!SharPrefHelper.getBooleanData(this,SharPrefHelper.KEY_MARQUEE_STATUS,false)) stripLayout.setVisibility(View.GONE);
        if(!SharPrefHelper.getBooleanData(this,SharPrefHelper.KEY_MAIN_MARKET_STATUS,false)){
            recyclerView.setVisibility(View.GONE);
            noGameIv.setVisibility(View.VISIBLE);
        }

    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }
    private void intIDs() {
//        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationRecyclerView = findViewById(R.id.navigation_recycler_view);
//        viewPager = findViewById(R.id.slider);
        recyclerView = findViewById(R.id.recyclerView);
        whatsAppNumber = findViewById(R.id.whatsAppNumber);
        callNumber = findViewById(R.id.callNumber);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        stripLayout = findViewById(R.id.stripLayout);
        txtMarquee =findViewById(R.id.marqueeText);
        txtMarquee.setSelected(true);
        // Now we will call setSelected() method
        // and pass boolean value as true

//        addFundLyt = findViewById(R.id.addFund_mcv);
//        playStarLineLty = findViewById(R.id.playStarLineLty);
//
//        galidesawarCard = findViewById(R.id.galidesawar_card);
//        fundLayout = findViewById(R.id.fundLayout);

        openBottomSheet = findViewById(R.id.openBottomSheet);
        progressBar = findViewById(R.id.progressBar);
        walletAmount = findViewById(R.id.walletAmount);
        noGameIv = findViewById(R.id.nogame_iv);
        sivNotice = findViewById(R.id.siv_notice);
        pendingNoti = findViewById(R.id.mtv_pending_noti);
        vipBadge = findViewById(R.id.vipBadge);
        navigationHeader = findViewById(R.id.drawerHeader);
        naviagationMenu = findViewById(R.id.naviagationMenu);
        presenter= new MainPresenter(this);
        menuItems = new ArrayList<>();
        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, YourService.class);
        startService(serviceIntent);
        from = getIntent().getStringExtra("from");
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        personName = navigationHeader.findViewById(R.id.personName);
        mobileNumber = navigationHeader.findViewById(R.id.mobileNumber);
        notiSwitchBtn = navigationHeader.findViewById(R.id.notiSwitchBtn);

        personName.setText(SharPrefHelper.getSignUpData(this, SharPrefHelper.KEY_PERSON_NAME));
        mobileNumber.setText(SharPrefHelper.getSignUpData(this, SharPrefHelper.KEY_MOBILE_NUMBER));
        welcomeMessage=SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_WELCOME_MSG);
//        textStripFirst.setText(SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_MARQUEE_TEXT));
//        textStripFirst.setEllipsize(TextUtils.TruncateAt.MARQUEE);
//        textStripFirst.setSelected(true);
//        textStripFirst.setSingleLine(true);
//        textStripFirst.setMarqueeRepeatLimit(-1);
//        whatsAppNumber.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_WHATSAPP_NUMBER));
//        callNumber.setText(SharPrefHelper.getContactDetails(this, SharPrefHelper.KEY_CONTACT_NUMBER1));

        pendingNoti.setText(SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_PENDING_NOTICE));
        if(SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_PENDING_NOTICE)!=null){
            if(!SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_PENDING_NOTICE).equals("0"))pendingNoti.setVisibility(View.VISIBLE);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<AppDetailsModel.Data.Banner>>() {
        }.getType();
        try {
            bannerList = gson.fromJson(SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_BANNER_LIST), type);
        } catch (Exception e) {
            System.out.println("json conversion failed");
        }
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
//        configureViewPager();
    }

    private void updateUserStatus(String accountStatus) {
        if (accountStatus.equals("2")){
            walletAmount.setVisibility(View.GONE);
//            addFundLyt.setVisibility(View.GONE);
//            playStarLineLty.setVisibility(View.GONE);
//            galidesawarCard.setVisibility(View.GONE);
//            fundLayout.setVisibility(View.GONE);
            sivNotice.setVisibility(View.GONE);
//            menuItems.clear();
//            menuItems.add(new MenuItemModel("Home", R.drawable.outline_home_24));
//            menuItems.add(new MenuItemModel("Profile", R.drawable.baseline_perm_identity_24));
//            menuItems.add(new MenuItemModel("Contact Us", R.drawable.outline_contact_phone_24));
//            menuItems.add(new MenuItemModel("Share With Friends", R.drawable.baseline_share_24));
//            menuItems.add(new MenuItemModel("Privacy Policy", R.drawable.outline_gpp_maybe_24));
//            menuItems.add(new MenuItemModel("Rate App", R.drawable.outline_star_border_24));
//            menuItems.add(new MenuItemModel("Change Password", R.drawable.baseline_password_24));
//            menuItems.add(new MenuItemModel("Logout", R.drawable.baseline_logout_24));
//            setNavigationMenu(menuItems);

        }else{
            walletAmount.setVisibility(View.VISIBLE);
//            addFundLyt.setVisibility(View.VISIBLE);
//            if(SharPrefHelper.getBooleanData(this,SharPrefHelper.KEY_STARLINE_MARKET_STATUS,false)) playStarLineLty.setVisibility(View.VISIBLE);
//            else  playStarLineLty.setVisibility(View.GONE);
//            if(SharPrefHelper.getBooleanData(this,SharPrefHelper.KEY_GALIDESAWAR_MARKET_STATUS,false)) galidesawarCard.setVisibility(View.VISIBLE);
//            else galidesawarCard.setVisibility(View.GONE);
//            fundLayout.setVisibility(View.VISIBLE);
            sivNotice.setVisibility(View.VISIBLE);
            updateProjectStatus();
            menuItems.clear();
            menuItems.add(new MenuItemModel("Home", R.drawable.outline_home_24));
            menuItems.add(new MenuItemModel("Profile", R.drawable.baseline_perm_identity_24));
            menuItems.add(new MenuItemModel("Wallet", R.drawable.outline_add_business_24));
            menuItems.add(new MenuItemModel("My History", R.drawable.baseline_history_24));
            menuItems.add(new MenuItemModel("Game Rates", R.drawable.baseline_currency_bitcoin_24));
            menuItems.add(new MenuItemModel("How To Play", R.drawable.baseline_play_circle_outline_24));
            menuItems.add(new MenuItemModel("Contact Us", R.drawable.outline_contact_phone_24));
            menuItems.add(new MenuItemModel("Share With Friends", R.drawable.baseline_share_24));
            menuItems.add(new MenuItemModel("Privacy Policy", R.drawable.outline_gpp_maybe_24));
            menuItems.add(new MenuItemModel("Rate App", R.drawable.outline_star_border_24));
            menuItems.add(new MenuItemModel("Change Password", R.drawable.baseline_password_24));
            menuItems.add(new MenuItemModel("Logout", R.drawable.baseline_logout_24));
//            setNavigationMenu(menuItems);
        }
    }

//    void setNavigationMenu(List<MenuItemModel> menuItemList){
//        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        MenuAdapter menuAdapter = new MenuAdapter(menuItemList, this);
//        navigationRecyclerView.setAdapter(menuAdapter);
//    }

    void welcomePopUp() {
        // Create the custom layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_welcome, null);

        // Initialize the views in the layout
        TextView welcomeTitle = dialogView.findViewById(R.id.welcomeTitle);
        TextView welcometv = dialogView.findViewById(R.id.welcomeMessage);
        Button btnDone = dialogView.findViewById(R.id.btnDone);

        // Set the title and message dynamically
        welcomeTitle.setText("Welcome to " + getString(R.string.app_name) + "!");
        welcometv.setText(welcomeMessage); // Assuming welcomeMessage is a class-level variable holding your message

        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Customize the dialog animations
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Set button click listener
        btnDone.setOnClickListener(v -> alertDialog.dismiss());

        // Show the dialog
        alertDialog.show();

        // Customize the dialog appearance
        alertDialog.getWindow().setBackgroundDrawable(
                ContextCompat.getDrawable(this, R.drawable.rounded_corner_white));
        alertDialog.getWindow().setLayout(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        if(SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_PENDING_NOTICE)!=null){
            if(SharPrefHelper.getPreferenceData(this,SharPrefHelper.KEY_PENDING_NOTICE).equals("0")){
                pendingNoti.setVisibility(View.GONE);
            }else {
                pendingNoti.setVisibility(View.VISIBLE);
            }
        }
        presenter.userDetailsApi(SharPrefHelper.getLogInToken(this));
        presenter.userStatusApi(SharPrefHelper.getLogInToken(this));
        registerReceiver(myReceiver, mIntentFilter, Context.RECEIVER_NOT_EXPORTED);
    }

//    @Override
//    public void onMenuItemClick(String menuItem) {
//        // Handle the menu item clicks here
//        // For example, you can replace fragments, start new activities, etc.
//        switch (menuItem) {
//            case "Profile":
//                presenter.profile(MainActivity.this);
//                break;
//            case "Wallet":
//                presenter.funds(MainActivity.this);
//                break;
//            case "My History":
//                presenter.history(MainActivity.this,200);
//                break;
//            case "Game Rates":
//                presenter.gameRates(MainActivity.this,1);
//                break;
//            case "How To Play":
//                presenter.gameRates(MainActivity.this,2);
//                break;
//            case "Contact Us":
//                presenter.contactUs(MainActivity.this);
//                break;
//            case "Share With Friends":
//                presenter.shareWithFriends(MainActivity.this);
//                break;
//            case "Privacy Policy":
//                presenter.privacyPolicy(this);
//                break;
//            case "Rate App":
//                presenter.rateApp(MainActivity.this);
//                break;
//            case "Change Password":
//                presenter.changePassword(MainActivity.this);
//                break;
//            case "Logout":
//                presenter.logout(MainActivity.this);
//                break;        }
//        drawerLayout.closeDrawers();
//    }
    private void configureToolbar() {
//        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        walletAmount.setOnClickListener(v -> presenter.funds(this));
    }

//    private void configureViewPager() {
//        viewPagerAdapter = new ViewPagerAdapter(this, bannerList);
//        viewPager.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
//        viewPager.setSliderAdapter(viewPagerAdapter);
//        viewPager.setScrollTimeInSec(3);
//        viewPager.setAutoCycle(true);
//        viewPager.startAutoCycle();
//    }
    private void configureRecyclerView() {
        gameListAdapter = new GameListAdapter(this, (ArrayList<GameListModel.Data>)
                dataList, new GameListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GameListModel.Data data, View itemView) {
                if (!data.isPlay()){
                    ObjectAnimator
                            .ofFloat(itemView, "translationX",
                                    0, 25, -25, 25, -25,15, -15, 6, -6, 0)
                            .setDuration(700)
                            .start();
                    vibe.vibrate(500);
                }else {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.putExtra(getString(R.string.game), data.getId());
                    intent.putExtra(getString(R.string.game_name), data.getName());
                    intent.putExtra("Running",data.isOpen());
                    startActivity(intent);
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(gameListAdapter);
    }


    public void whatsAppNumber(View view) {
        presenter.whatsApp(this);
    }

    public void callNumber(View view) {
        presenter.call(this);
    }

    public void playStarLine(View view) {
        presenter.playStarLine(this);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
//        if (drawerLayout.isDrawerOpen(naviagationMenu)){
//            drawerLayout.closeDrawers();
//        }else {
//            finishAffinity();
//        }
    }

    public void addFund(View view) {
        presenter.addFund(this);
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


    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSwipeProgressBar() {
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void hideSwipeProgressBar() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void gameListApiResponse(GameListModel gameListModel) {
        dataList = gameListModel.getData();
        configureRecyclerView();
    }

    @Override
    public void userStatusApiResponse(UserStatusModel.Data userStatusData) {
        SharPrefHelper.setUserPoints(MainActivity.this,userStatusData.getAvailablePoints());
        SharPrefHelper.setTransferPoints(MainActivity.this, userStatusData.getTransferPoint().equalsIgnoreCase("1"));
        SharPrefHelper.setAddFundUPI(MainActivity.this,SharPrefHelper.KEY_ADD_FUND_UPI_ID ,userStatusData.getUpiPaymentId());
        SharPrefHelper.setAddFundUPI(MainActivity.this,SharPrefHelper.KEY_ADD_FUND_UPI_NAME ,userStatusData.getUpiName());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MAX_ADD_FUND_POINTS, userStatusData.getMaximumDeposit());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MIN_ADD_FUND_POINTS, userStatusData.getMinimumDeposit());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MAX_WITHDRAW_POINTS, userStatusData.getMaximumWithdraw());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MIN_WITHDRAW_POINTS, userStatusData.getMinimumWithdraw());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MAX_BID_AMOUNT, userStatusData.getMaximumBidAmount());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MIN_BID_AMOUNT, userStatusData.getMinimumBidAmount());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MAX_TRANSFER_POINTS, userStatusData.getMaximumTransfer());
        SharPrefHelper.setMinMaxData(MainActivity.this,SharPrefHelper.KEY_MIN_TRANSFER_POINTS, userStatusData.getMinimumTransfer());
        walletAmount.setText(userStatusData.getAvailablePoints());
        vipStatus = userStatusData.getVipStatus();
        SharPrefHelper.setActiveUser(MainActivity.this, userStatusData.getAccountStatus());
        SharPrefHelper.setVipStatus(MainActivity.this, userStatusData.getVipStatus());
        updateUserStatus(userStatusData.getAccountStatus());
        if(vipStatus){
            vipBadge.setVisibility(View.VISIBLE);
        }else {
            vipBadge.setVisibility(View.VISIBLE);
        }
        if(userStatusData.getAccountStatus().equals("1")){
           try {
               if(from.equals("pin")){
                   from = "";
                   welcomePopUp();
               }
           }catch (Exception e){

           }

        }
    }

    @Override
    public void appDetailsApiResponse(AppDetailsModel appDetailsModel) {
        AppDetailsModel.Data data = appDetailsModel.getData();
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_MARQUEE_TEXT,appDetailsModel.getData().getBanner_marquee());
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_WELCOME_MSG,appDetailsModel.getData().getWelcome_message());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_CONTACT_NUMBER1,"+91"+appDetailsModel.getData().getContact_details().getMobile_no_1());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_CONTACT_NUMBER2,"+91"+appDetailsModel.getData().getContact_details().getMobile_no_2());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_WHATSAPP_NUMBER,"+91"+appDetailsModel.getData().getContact_details().getWhatsapp_no());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_WITHDRAW_PROOF,appDetailsModel.getData().getContact_details().getWithdraw_proof());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_CONTACT_EMAIL,appDetailsModel.getData().getContact_details().getEmail_1());
        SharPrefHelper.setContactDetails(this,SharPrefHelper.KEY_TELEGRAM,appDetailsModel.getData().getContact_details().getTelegram_no());
        SharPrefHelper.setBannerImages(this,SharPrefHelper.KEY_BANNER_IMAGES1, appDetailsModel.getData().getBanner_image().getBanner_img_1());
        SharPrefHelper.setBannerImages(this,SharPrefHelper.KEY_BANNER_IMAGES2, appDetailsModel.getData().getBanner_image().getBanner_img_2());
        SharPrefHelper.setBannerImages(this,SharPrefHelper.KEY_BANNER_IMAGES3, appDetailsModel.getData().getBanner_image().getBanner_img_3());
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_MAIN_MARKET_STATUS, data.getProject_status().getMain_market().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_STARLINE_MARKET_STATUS, data.getProject_status().getStarline_market().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_GALIDESAWAR_MARKET_STATUS, data.getProject_status().getGalidesawar_market().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_BANNER_STATUS, data.getProject_status().getBanner_status().equals("On"));
        SharPrefHelper.setBooleanData(this,SharPrefHelper.KEY_MARQUEE_STATUS, data.getProject_status().getMarquee_status().equals("On"));
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_BANNER_LIST, new Gson().toJson(data.getBannerList()));
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_App_Details, new Gson().toJson(data));
//        whatsAppNumber.setText(appDetailsModel.getData().getContact_details().getWhatsapp_no());
//        callNumber.setText(appDetailsModel.getData().getContact_details().getMobile_no_1());
        telegramLink = data.getContact_details().getTelegram_no();
        welcomeMessage = data.getWelcome_message();
        bannerList = data.getBannerList();
//        viewPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void userDetailsApiResponse(LoginModel loginModel) {
        SharPrefHelper.setSignUpData(this,SharPrefHelper.KEY_PERSON_NAME ,loginModel.getData().getUsername());
        SharPrefHelper.setSignUpData(this, SharPrefHelper.KEY_MOBILE_NUMBER, loginModel.getData().getMobile());
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_USER_EMAIL,loginModel.getData().getEmail());
        SharPrefHelper.setBankDetails(this, SharPrefHelper.KEY_BANK_HOLDER_NAME, loginModel.getData().getAccount_holder_name());
        SharPrefHelper.setBankDetails(this, SharPrefHelper.KEY_BANK_AC_NUMBER, loginModel.getData().getBank_account_no());
        SharPrefHelper.setBankDetails(this, SharPrefHelper.KEY_BANK_IFSC_CODE, loginModel.getData().getIfsc_code());
        SharPrefHelper.setBankDetails(this, SharPrefHelper.KEY_BANK_NAME, loginModel.getData().getBank_name());
        SharPrefHelper.setBankDetails(this, SharPrefHelper.KEY_BANK_ADDRESS, loginModel.getData().getBranch_address());
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_PAYTM_UPI,loginModel.getData().getPaytm_mobile_no());
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_PHONEPE_UPI,loginModel.getData().getPhonepe_mobile_no());
        SharPrefHelper.setPreferenceData(this, SharPrefHelper.KEY_GOOGLEPAY_UPI,loginModel.getData().getGpay_mobile_no());
        SharPrefHelper.setPreferenceData(this,SharPrefHelper.KEY_PENDING_NOTICE,loginModel.getData().getPending_noti());
        personName.setText(loginModel.getData().getUsername());
        pendingNoti.setText(loginModel.getData().getPending_noti());
        pendingNotification=loginModel.getData().getPending_noti();
        if(pendingNotification.equals("0"))pendingNoti.setVisibility(View.GONE);
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

    public void galidesawar(View view) {
        Intent intent = new Intent(this, GalidesawarActivity.class);
        startActivity(intent);
    }

    public void withdraw(View view) {
        Intent intent = new Intent(this, WithdrawActivity.class);
        startActivity(intent);
    }


    public void callNotice(View view) {
        Intent intent = new Intent(this, NoticeActivity.class);
        startActivity(intent);
    }

    public void telegram(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(telegramLink));

        // Check if Telegram is installed
        intent.setPackage("org.telegram.messenger");

        // If the Telegram app is installed, this will open it
        // If not, it will open the link in a browser
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If the Telegram app isn't installed, open the link in a browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telegramLink));
            startActivity(browserIntent);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}