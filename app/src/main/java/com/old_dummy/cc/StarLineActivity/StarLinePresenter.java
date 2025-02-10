package com.old_dummy.cc.StarLineActivity;

import android.app.Activity;
import android.content.Intent;

import com.old_dummy.cc.HistoryActivity.WinHistoryActivity;
import com.old_dummy.cc.Models.StarlineGameListModel;
import com.old_dummy.cc.R;
import com.old_dummy.cc.StarlineChart.StarlineChartActivity;

import java.util.ArrayList;

public class StarLinePresenter implements StarLineContract.ViewModel.OnFinishedListener, StarLineContract.Presenter{

    StarLineContract.View view;
    StarLineContract.ViewModel viewModel;

    public StarLinePresenter(StarLineContract.View view) {
        this.view = view;
        viewModel = new StarLineViewModel();
    }

    @Override
    public void finished(StarlineGameListModel.Data data) {
        if (view!=null){
            view.hideProgressBar();
            view.apiResponse(data);
        }
    }


    @Override
    public void message(String msg) {
        if (view!=null){
            view.hideProgressBar();
            view.message(msg);
        }
    }

    @Override
    public void destroy(String msg) {
        if (view!=null){
            view.hideProgressBar();
            view.message(msg);
        }
    }

    @Override
    public void failure(Throwable t) {
        if (view!=null){
            view.hideProgressBar();
        }
    }
    @Override
    public void api(String token) {
        if (view!=null){
            view.showProgressBar();
        }
        viewModel.callApi(this, token);
    }

    @Override
    public void chart(Activity activity, ArrayList<String> name) {
        Intent intent = new Intent(activity, StarlineChartActivity.class);
        intent.putExtra("gameNames",name);
        activity.startActivity(intent);
    }

    @Override
    public void History(Activity activity, int history, String mainWinHistory) {
        Intent bidHistory = new Intent(activity, WinHistoryActivity.class);
        bidHistory.putExtra(activity.getString(R.string.history), history);
        activity.startActivity(bidHistory);
    }
}
