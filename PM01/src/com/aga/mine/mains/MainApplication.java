package com.aga.mine.mains;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class MainApplication extends Application {
    private Context mContext;
    private MainActivity mActivity;
    
    private static MainApplication mApplication;
    
    public static MainApplication getInstance(){
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        mContext = this.getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public void setActivity(MainActivity activity) {
        mActivity = activity;
    }
    
    public MainActivity getActivity() {
        return mActivity;
    }
    
    public Handler mHandler = new Handler() {
        
    };
}
