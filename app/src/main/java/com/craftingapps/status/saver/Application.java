package com.craftingapps.status.saver;

import android.content.Context;
import com.craftingapps.status.saver.helper.AppConstants;
import com.craftingapps.status.saver.managers.AdsManager;
import com.crashlytics.android.Crashlytics;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;


public class Application extends android.app.Application {

    private static Context context;
    private final String TAG = Application.class.getSimpleName();

//    private static FirebaseAnalytics mFirebaseAnalytics;
//
//    public static FirebaseAnalytics getmFirebaseAnalytics() {
//        return mFirebaseAnalytics = FirebaseAnalytics.getInstance(AppConstant.CONTEXT);
//    }

    public static Context getContext()
    {
        return context;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        AppConstants.CONTEXT = context;
        //Firebase.setAndroidContext(this);
        AdsManager.getInstance();
     //   Fabric.with(context, new Crashlytics());

//        if(!FirebaseApp.getApps(this).isEmpty()) {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        }


        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        Picasso.setSingletonInstance(built);


    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }


}
