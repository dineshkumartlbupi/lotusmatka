package com.old_dummy.cc.StarLineActivity;

import android.app.Activity;

import com.old_dummy.cc.Models.StarlineGameListModel;

import java.util.ArrayList;

public interface StarLineContract {
    interface View{
        void showProgressBar();
        void hideProgressBar();
        void apiResponse(StarlineGameListModel.Data data);
        void message(String msg);
        void destroy(String msg);
    }

    interface ViewModel{
        interface OnFinishedListener{
            void finished(StarlineGameListModel.Data data);
            void message(String msg);
            void destroy(String msg);
            void failure(Throwable t);
        }
        void callApi(OnFinishedListener onFinishedListener, String token);

    }

    interface Presenter{
        void api(String token);
        void chart(Activity activity, ArrayList<String> name);
        void History(Activity activity, int history, String mainWinHistory);

    }
}
