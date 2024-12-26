package com.old_dummy.cc.GameProceedActivity;

public interface GameProceedContract {
    interface View{
        void showProgressBar();
        void hideProgressBar();
        void apiResponse(android.view.View view);
        void message(String msg);
        void destroy(String msg);
    }
    interface ViewModel{
        interface OnFinishedListener{
            void finished(android.view.View view);
            void message(String msg);
            void destroy(String msg);
            void failure(Throwable t);
        }
        void callApi(OnFinishedListener onFinishedListener, String token, String serverData, android.view.View view);
    }
    interface Presenter{
        void api(String token,String serverData, android.view.View view);
    }
}