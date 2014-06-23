package com.aga.mine.main;

import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class MainApplication extends Application {
    //private static final String APP_ID = "625994234086470";
//	private static final String APP_ID = "1387999011424838";
//	private static final String APP_NAMESPACE = "typing_game";
    //private static final String APP_ID = "709362365791816";
    //private static final String APP_NAMESPACE = "pumpkinmine;
	private static final String APP_ID = "709362365791816";
    private static final String APP_NAMESPACE = "pumpkinmine";
    
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
        
        // initialize facebook configuration
        Permission[] permissions = new Permission[] { Permission.BASIC_INFO,
                                                     Permission.USER_CHECKINS,
                                                     Permission.USER_EVENTS,
                                                     Permission.USER_GROUPS,
                                                     Permission.USER_LIKES,
                                                     Permission.USER_PHOTOS,
                                                     Permission.USER_VIDEOS,
                                                     Permission.FRIENDS_EVENTS,
                                                     Permission.FRIENDS_PHOTOS,
                                                     Permission.FRIENDS_ABOUT_ME,
                                                     Permission.PUBLISH_STREAM };

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder().setAppId(APP_ID)
                    .setNamespace(APP_NAMESPACE)
                    .setPermissions(permissions)
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setAskForAllPermissionsAtOnce(false)
                    .build();

        SimpleFacebook.setConfiguration(configuration);
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
    
    //이모티콘 구매 애니메이션
    private ShopEmoticon mShopEmoticon;
    public void setShopEmoticon(ShopEmoticon shopEmoticon) {
    	mShopEmoticon = shopEmoticon;
    }
    
    public void startAni(int gold, int price) {
    	mShopEmoticon.makeAPurchase(gold, price);
    }
}
