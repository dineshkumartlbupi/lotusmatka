package com.old_dummy.cc.ForgotPasswordActivity;

import com.old_dummy.cc.Api.ApiClient;
import com.old_dummy.cc.Models.CommonModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel implements ForgotPasswordContract.ViewModel{

    @Override
    public void callApi(OnFinishedListener onFinishedListener, String number) {
        Call<CommonModel> call = ApiClient.getClient().forgotPassword(number);
        call.enqueue(new Callback<CommonModel>() {
            @Override
            public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                if (response.isSuccessful()) {
                    CommonModel data = response.body();
                    if (data.getStatus().equals("success")) {
                        onFinishedListener.finished();
                    }
                    onFinishedListener.message(data.getMessage());
                } else onFinishedListener.message("Network Error");
            }

            @Override
            public void onFailure(Call<CommonModel> call, Throwable t) {
                System.out.println("forgotPassword " + t);
                onFinishedListener.failure(t);
                onFinishedListener.message("Server Error");
            }
        });
    }
}
