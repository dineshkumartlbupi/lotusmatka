package com.old_dummy.cc.ContactUsActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.old_dummy.cc.Extras.SharPrefHelper;
import com.old_dummy.cc.Models.AppDetailsModel;
import com.old_dummy.cc.R;

import java.lang.reflect.Type;

public class ContactUsPresenter implements ContactUsContract.Presenter{
    ContactUsContract.View view;

    public ContactUsPresenter(ContactUsContract.View view) {
        this.view = view;
    }

    public void call(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CALL_PHONE}, 100);
        }else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+ SharPrefHelper.getContactDetails(activity, SharPrefHelper.KEY_CONTACT_NUMBER1)));
            activity.startActivity(callIntent);
        }
    }

    @Override
    public void whatsapp(Activity activity) {
        AppDetailsModel.Data data = null;
        Gson gson = new Gson();
        Type type = new TypeToken<AppDetailsModel.Data>() {
        }.getType();
        try {
            data = gson.fromJson(SharPrefHelper.getPreferenceData(activity,SharPrefHelper.KEY_App_Details), type);
        } catch (Exception e) {
            System.out.println("json conversion failed");
        }
        String adminMsg = data.getAdmin_message();

        if(adminMsg.equals("")){
            adminMsg = "Hello Sir, My Name is "+SharPrefHelper.getSignUpData(activity, SharPrefHelper.KEY_PERSON_NAME)+"\nMobile Number :"+SharPrefHelper.getSignUpData(activity,SharPrefHelper.KEY_MOBILE_NUMBER);
        }
        String url = "https://api.whatsapp.com/send?phone="+SharPrefHelper.getContactDetails(activity, SharPrefHelper.KEY_WHATSAPP_NUMBER)+"&text="+adminMsg;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    @Override
    public void mail(Activity activity) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{SharPrefHelper.getContactDetails(activity, SharPrefHelper.KEY_CONTACT_EMAIL)});
        email.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.mail_subject));
        email.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.mail_message));
        email.setPackage("com.google.android.gm");
        email.setType("message/rfc822");
        activity.startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void telegram(Activity activity) {
        String telegramLink = SharPrefHelper.getContactDetails(activity, SharPrefHelper.KEY_TELEGRAM);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(telegramLink));

        // Check if Telegram is installed
        intent.setPackage("org.telegram.messenger");

        // If the Telegram app is installed, this will open it
        // If not, it will open the link in a browser
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            // If the Telegram app isn't installed, open the link in a browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telegramLink));
            activity.startActivity(browserIntent);
        }
    }

    @Override
    public void withdrawProof(ContactUsActivity contactUsActivity) {
        AppDetailsModel.Data data = null;
        Gson gson = new Gson();
        Type type = new TypeToken<AppDetailsModel.Data>() {
        }.getType();
        try {
            data = gson.fromJson(SharPrefHelper.getPreferenceData(contactUsActivity, SharPrefHelper.KEY_App_Details), type);
        } catch (Exception e) {
            showErrorPopup(contactUsActivity, "Failed to load data.");
            return;
        }

        if (data == null || data.getContact_details() == null ||
                TextUtils.isEmpty(data.getContact_details().getWithdraw_proof())) {
            showErrorPopup(contactUsActivity, "Withdraw proof URL is missing.");
            return;
        }

        String url = data.getContact_details().getWithdraw_proof();
        if (!url.startsWith("https://chat.whatsapp.com/DAtghHfoAb71zysbzS37D") && !url.startsWith("https://chat.whatsapp.com/DAtghHfoAb71zysbzS37D")) {
            url = "https://chat.whatsapp.com/DAtghHfoAb71zysbzS37D" + url;
        }

        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            contactUsActivity.startActivity(intent);
        } catch (Exception e) {
            showErrorPopup(contactUsActivity, "No application found to open the URL.");
        }
    }

    private void showErrorPopup(ContactUsActivity context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
