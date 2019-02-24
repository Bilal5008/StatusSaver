package com.craftingapps.status.saver.managers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.craftingapps.status.saver.BuildConfig;
import com.craftingapps.status.saver.R;
import com.craftingapps.status.saver.helper.AppConstants;
import com.craftingapps.status.saver.helper.SharedPreferenceHelper;
import com.craftingapps.status.saver.utils.Utils;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;


import java.util.List;

import static com.craftingapps.status.saver.helper.AppConstants.IS_ADS_DISABLED;

public class AdsManager {
    static Context context;

    private static AdsManager manager;
    SharedPreferenceHelper appPreference;
    private InterstitialAd interstitialAd;
    //    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    private String TAG = AdsManager.class.getName();
//    private com.facebook.ads.NativeAd nativeAd;

    private AdsManager() {
        appPreference = SharedPreferenceHelper.getInstance();
        if (!appPreference.getBoolean(IS_ADS_DISABLED)) {
            interstitialAd = new InterstitialAd(AppConstants.CONTEXT);

            interstitialAd.setAdUnitId(AppConstants.CONTEXT.getString(R.string.admob_interstitial_ad_unit));// load the ads and cache them for later use
            loadInterstitialAd();
        }
    }

    public static AdsManager getInstance() {
        if (manager == null) {
            manager = new AdsManager();

        }
        return manager;
    }

    public void loadInterstitialAd() {
        if (Utils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            if (BuildConfig.DEBUG) {
                interstitialAd.loadAd(appendUserConsent());
            } else {
                interstitialAd.loadAd(appendUserConsent());
            }
            interstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Log.e(TAG, "onAdClosed");
                    // reload it and cache it for next time
                    loadInterstitialAd();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e(TAG, "onAdFailedToLoad");
                    // if failed to load then reload it again
                    loadInterstitialAd();
                }
            });
        }
    }

    public void showInterstitialAd(String tag) {
        try {
            if (interstitialAd.isLoaded() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
                 // AnalyticsUtils.sendAnalytics(tag, "admob_interstitial");
                interstitialAd.show();
            } else {
                loadInterstitialAd();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private AdRequest appendUserConsent() {
        AdRequest adRequest = null;
        if (appPreference.getBoolean(AppConstants.CONTEXT.getString(R.string.npa))) {
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.CONTEXT.getString(R.string.npa), "1");
            Log.d(TAG, "consent status: npa");
            adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, bundle)./*addTestDevice("A46E6715F55869399FFDAA853912934B").*/build();
        } else {
            Log.d(TAG, "consent status: pa");
            adRequest = new AdRequest.Builder()./*addTestDevice("A46E6715F55869399FFDAA853912934B").*/build();
        }
        return adRequest;
    }

    public void showAdMobLargeBanner(final AdView adView) {
        if (Utils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            if (adView != null) {
                AdRequest adRequest = null;
                if (BuildConfig.DEBUG) {
                    adRequest = appendUserConsent();
                } else {
                    adRequest = appendUserConsent();
                }
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d("AdMobBanner", "onAdFailedToLoad");
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d("AdMobBanner", "onAdLoaded");
                        if (adView.getVisibility() == View.GONE) {
                            adView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }


    public void loadNativeAppInstall(final FrameLayout nativeAppInstall) {
        if (Utils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            AdLoader adLoader = new AdLoader.Builder(AppConstants.CONTEXT, AppConstants.CONTEXT.getString(R.string.admob_native_ad_unit))
                    .forUnifiedNativeAd(unifiedNativeAd -> {
                        Log.e(TAG, "onNativeAppInstallAdLoaded");
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(AppConstants.CONTEXT).inflate(R.layout.ad_app_install, null);
                        populateNativeAppInstallAdView(unifiedNativeAd, adView);
                        nativeAppInstall.removeAllViews();
                        nativeAppInstall.addView(adView);
                    })
                    .withAdListener(new AdListener() {

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            Log.e(TAG, "onNativeAppInstallAdFailedToLoad");
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (nativeAppInstall.getVisibility() == View.GONE) {
                                nativeAppInstall.setVisibility(View.VISIBLE);
                            }
                            Log.e(TAG, "onNativeAppInstallAdLoaded");
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Log.e(TAG, "onNativeAppInstallAdClosed");
                        }
                    }).build();
            adLoader.loadAd(appendUserConsent());
        }
    }

    private void populateNativeAppInstallAdView(UnifiedNativeAd nativeAppInstallAd, UnifiedNativeAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        try {
            if (nativeAppInstallAd != null) {
                VideoController vc = nativeAppInstallAd.getVideoController();

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before refreshing
                        // or replacing them with another ad in the same UI location.
                        super.onVideoEnd();
                    }
                });

                adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
                adView.setBodyView(adView.findViewById(R.id.appinstall_body));
                adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
                adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
                adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));

                // Some assets are guaranteed to be in every NativeAppInstallAd.
                ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
                ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
                ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
                // ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());

                Picasso.with(AppConstants.CONTEXT)
                        .load(nativeAppInstallAd.getIcon().getUri())
                        .into(((ImageView) adView.getIconView()));


                MediaView mediaView = adView.findViewById(R.id.appinstall_media);
                ImageView mainImageView = adView.findViewById(R.id.appinstall_image);

                // Apps can check the VideoController's hasVideoContent property to determine if the
                // NativeAppInstallAd has a video asset.
                if (vc.hasVideoContent()) {
                    adView.setMediaView(mediaView);
                    mainImageView.setVisibility(View.GONE);
                } else {

                    adView.setImageView(mainImageView);
                    mediaView.setVisibility(View.GONE);

                    // At least one image is guaranteed.
                    List<NativeAd.Image> images = nativeAppInstallAd.getImages();
                    if (images.size() > 0) {
                        Picasso.with(AppConstants.CONTEXT)
                                .load(images.get(0).getUri())
                                .into(mainImageView);
                    }
                    // mainImageView.setImageDrawable(images.get(0).getUri());
                }

                // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
                // check before trying to display them.
                if (nativeAppInstallAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.INVISIBLE);
                } else {
                    ((RatingBar) adView.getStarRatingView())
                            .setRating(nativeAppInstallAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }

                // Assign native ad object to the native view.
                adView.setNativeAd(nativeAppInstallAd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // START EXERCISE LIST ACTIVITY Native install app without image/mediaview
    public void loadNativeBannerAppInstall(final Context context,
                                           final FrameLayout nativeAppInstall) {
        if (Utils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad_unit))
                    .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                            Log.e(TAG, "onNativeBannerAppInstallAdLoaded");
                            NativeAppInstallAdView adView = (NativeAppInstallAdView)
                                    LayoutInflater.from(context).inflate(R.layout.banner_ad_app_install, null);
                            populateNativeBannerAppInstallAdView(nativeAppInstallAd, adView);
                            nativeAppInstall.removeAllViews();
                            nativeAppInstall.addView(adView);
                        }
                    })
                    .withAdListener(new AdListener() {

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            Log.e(TAG, "onNativeBannerAppInstallAdFailedToLoad");
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (nativeAppInstall.getVisibility() == View.GONE) {
                                nativeAppInstall.setVisibility(View.VISIBLE);
                            }
                            Log.e(TAG, "onNativeBannerAppInstallAdLoaded");
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Log.e(TAG, "onNativeBannerAppInstallAdClosed");
                        }
                    }).build();
            adLoader.loadAd(appendUserConsent());
        }
    }

    public void createAndShowBanner(final AdView adView) {
        if (!SharedPreferenceHelper.getInstance().getBooleanValue(AppConstants.IS_ADS_DISABLED, false)) {
            if (BuildConfig.DEBUG) {
                adView.loadAd(new AdRequest.Builder().build());
            } else {
                adView.loadAd(appendUserConsent());
            }
//            AdRequest adRequest = appendUserConsent();
//            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adView.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    adView.setVisibility(View.GONE);
                    super.onAdFailedToLoad(i);
                }
            });

        }
    }

    private void populateNativeBannerAppInstallAdView(NativeAppInstallAd
                                                              nativeAppInstallAd, NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
      /*  ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAppInstallAd.getIcon().getDrawable());*/
        Picasso.with(AppConstants.CONTEXT)
                .load(nativeAppInstallAd.getIcon().getUri())
                .into(((ImageView) adView.getIconView()));

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }



}