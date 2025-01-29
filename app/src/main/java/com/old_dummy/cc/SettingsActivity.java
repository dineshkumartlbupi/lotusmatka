package com.old_dummy.cc;

import static com.old_dummy.cc.Extras.Utility.BroadCastStringForAction;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.old_dummy.cc.Adapters.MenuAdapter;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Extras.Utility;
import com.old_dummy.cc.GalidesawarActivity.GalidesawarActivity;
import com.old_dummy.cc.MainActivity.MainActivity;
import com.old_dummy.cc.MainActivity.MainContract;
import com.old_dummy.cc.MainActivity.MainPresenter;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.Models.GameListModel;
import com.old_dummy.cc.Models.LoginModel;
import com.old_dummy.cc.Models.MenuItemModel;
import com.old_dummy.cc.Models.UserStatusModel;
import com.old_dummy.cc.NoticeActivity.NoticeActivity;
import com.old_dummy.cc.SplashActivity.SplashActivity;
import com.old_dummy.cc.WithdrawActivity.WithdrawActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingsActivity extends BaseActivity implements
        MainContract.View,  MenuAdapter.OnMenuItemClickListener {
    TextInputEditText inputPersonName, inputMobileNumber, inputEmail;
    ProgressBar progressBar;
    MaterialTextView dataConText;
    IntentFilter mIntentFilter;
    Utility utility;
    RecyclerView navigationRecyclerView;
    List<MenuItemModel> menuItems;
    MainContract.Presenter presenter;
    @Override
    protected int getLayoutResourceId() {

        return R.layout.fragment_setting;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(
                getResources().getColor(
                        R.color.main_color));
        intIDs();


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


        if (internetIsConnected()){
            presenter.appDetailsApi(SharPrefHelper.getLogInToken(this)==null?"":SharPrefHelper.getLogInToken(this));
            presenter.userStatusApi(SharPrefHelper.getLogInToken(SettingsActivity.this));
            presenter.gameListApi(SharPrefHelper.getLogInToken(SettingsActivity.this));

            presenter.userDetailsApi( SharPrefHelper.getLogInToken(SettingsActivity.this));
        }
        else Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
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
        menuItems = new ArrayList<>();
        navigationRecyclerView = findViewById(R.id.navigation_recycler_view);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);
        dataConText = findViewById(R.id.dataConText);
        utility = new Utility(dataConText);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BroadCastStringForAction);
        presenter= new MainPresenter(this);
        updateUserStatus("2");

    }

    private void updateUserStatus(String accountStatus) {
        if (accountStatus.equals("2")){
            menuItems.clear();
//            menuItems.add(new MenuItemModel("Home", R.drawable.outline_home_24));
//            menuItems.add(new MenuItemModel("Profile", R.drawable.baseline_perm_identity_24));
            menuItems.add(new MenuItemModel("Contact Us", R.drawable.outline_contact_phone_24));
            menuItems.add(new MenuItemModel("Share With Friends", R.drawable.baseline_share_24));
//            menuItems.add(new MenuItemModel("Privacy Policy", R.drawable.outline_gpp_maybe_24));
            menuItems.add(new MenuItemModel("Rate App", R.drawable.outline_star_border_24));
            menuItems.add(new MenuItemModel("Change Password", R.drawable.baseline_password_24));
            menuItems.add(new MenuItemModel("Logout", R.drawable.ic_logout));
            setNavigationMenu(menuItems);

        }else{
            menuItems.clear();
//            menuItems.add(new MenuItemModel("Home", R.drawable.outline_home_24));
//            menuItems.add(new MenuItemModel("Profile", R.drawable.baseline_perm_identity_24));
//            menuItems.add(new MenuItemModel("Wallet", R.drawable.outline_add_business_24));
//            menuItems.add(new MenuItemModel("My History", R.drawable.baseline_history_24));
            menuItems.add(new MenuItemModel("Game Rates", R.drawable.baseline_currency_bitcoin_24));
            menuItems.add(new MenuItemModel("How To Play", R.drawable.baseline_play_circle_outline_24));
            menuItems.add(new MenuItemModel("Contact Us", R.drawable.outline_contact_phone_24));
            menuItems.add(new MenuItemModel("Share With Friends", R.drawable.baseline_share_24));
//            menuItems.add(new MenuItemModel("Privacy Policy", R.drawable.outline_gpp_maybe_24));
            menuItems.add(new MenuItemModel("Rate App", R.drawable.outline_star_border_24));
            menuItems.add(new MenuItemModel("Change Password", R.drawable.baseline_password_24));
            menuItems.add(new MenuItemModel("Logout", R.drawable.ic_logout));
            setNavigationMenu(menuItems);
        }
    }

    void setNavigationMenu(List<MenuItemModel> menuItemList){
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MenuAdapter menuAdapter = new MenuAdapter(menuItemList, this);
        navigationRecyclerView.setAdapter(menuAdapter);
    }





    @Override
    protected void onPause() {
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @Override
    public void onMenuItemClick(String menuItem) {
        // Handle the menu item clicks here
        // For example, you can replace fragments, start new activities, etc.
        switch (menuItem) {
//            case "Profile":
//                presenter.profile(SettingsActivity.this);
//                break;
//            case "Wallet":
//                presenter.funds(SettingsActivity.this);
//                break;
//            case "My History":
//                presenter.history(SettingsActivity.this,200);
//                break;
            case "Game Rates":
                presenter.gameRates(SettingsActivity.this,1);
                break;
            case "How To Play":
                presenter.gameRates(SettingsActivity.this,2);
                break;
            case "Contact Us":
                presenter.contactUs(SettingsActivity.this);
                break;
            case "Share With Friends":
                presenter.shareWithFriends(SettingsActivity.this);
                break;
//            case "Privacy Policy":
//                presenter.privacyPolicy(this);
//                break;
            case "Rate App":
                presenter.rateApp(SettingsActivity.this);
                break;
            case "Change Password":
                presenter.changePassword(SettingsActivity.this);
                break;
            case "Logout":
                presenter.logout(SettingsActivity.this);
                break;        }
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSwipeProgressBar() {

//        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void hideSwipeProgressBar() {
//        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void gameListApiResponse(GameListModel gameListModel) {
//        dataList = gameListModel.getData();
//        configureRecyclerView();
    }

    @Override
    public void userStatusApiResponse(UserStatusModel.Data userStatusData) {
        SharPrefHelper.setUserPoints(SettingsActivity.this,userStatusData.getAvailablePoints());
        SharPrefHelper.setTransferPoints(SettingsActivity.this, userStatusData.getTransferPoint().equalsIgnoreCase("1"));
        SharPrefHelper.setAddFundUPI(SettingsActivity.this,SharPrefHelper.KEY_ADD_FUND_UPI_ID ,userStatusData.getUpiPaymentId());
        SharPrefHelper.setAddFundUPI(SettingsActivity.this,SharPrefHelper.KEY_ADD_FUND_UPI_NAME ,userStatusData.getUpiName());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MAX_ADD_FUND_POINTS, userStatusData.getMaximumDeposit());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MIN_ADD_FUND_POINTS, userStatusData.getMinimumDeposit());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MAX_WITHDRAW_POINTS, userStatusData.getMaximumWithdraw());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MIN_WITHDRAW_POINTS, userStatusData.getMinimumWithdraw());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MAX_BID_AMOUNT, userStatusData.getMaximumBidAmount());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MIN_BID_AMOUNT, userStatusData.getMinimumBidAmount());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MAX_TRANSFER_POINTS, userStatusData.getMaximumTransfer());
        SharPrefHelper.setMinMaxData(SettingsActivity.this,SharPrefHelper.KEY_MIN_TRANSFER_POINTS, userStatusData.getMinimumTransfer());
        SharPrefHelper.setActiveUser(SettingsActivity.this, userStatusData.getAccountStatus());
        SharPrefHelper.setVipStatus(SettingsActivity.this, userStatusData.getVipStatus());
        updateUserStatus(userStatusData.getAccountStatus());
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

        // Check if Telegram is installed
        intent.setPackage("org.telegram.messenger");

        // If the Telegram app is installed, this will open it
        // If not, it will open the link in a browser
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If the Telegram app isn't installed, open the link in a browser
//            startActivity(browserIntent);

        }

    }
}