package io.bidmachine.examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ads.mediation.bidmachine.BidMachineBundleBuilder;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventBanner;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventInterstitial;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventNative;
import com.google.ads.mediation.bidmachine.BidMachineMediationRewardedAdAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Map;

import io.bidmachine.BidMachine;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.nativead.NativeRequest;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineAdMobFetchActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String BANNER_ID = "YOUR_BANNER_ID";
    private static final String INTERSTITIAL_ID = "YOUR_INTERSTITIAL_ID";
    private static final String REWARDED_ID = "YOUR_REWARDED_ID";
    private static final String NATIVE_ID = "YOUR_NATIVE_ID";

    private Button bLoadBanner;
    private Button bShowBanner;
    private Button bLoadInterstitial;
    private Button bShowInterstitial;
    private Button bLoadRewardedVideo;
    private Button bShowRewardedVideo;
    private Button bLoadNative;
    private Button bShowNative;
    private FrameLayout adContainer;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;
    private UnifiedNativeAd nativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);

        Button bInitialize = findViewById(R.id.bInitialize);
        bInitialize.setOnClickListener(v -> initialize());
        bLoadBanner = findViewById(R.id.bLoadBanner);
        bLoadBanner.setOnClickListener(v -> loadBanner());
        bShowBanner = findViewById(R.id.bShowBanner);
        bShowBanner.setOnClickListener(v -> showBanner());
        bLoadInterstitial = findViewById(R.id.bLoadInterstitial);
        bLoadInterstitial.setOnClickListener(v -> loadInterstitial());
        bShowInterstitial = findViewById(R.id.bShowInterstitial);
        bShowInterstitial.setOnClickListener(v -> showInterstitial());
        bLoadRewardedVideo = findViewById(R.id.bLoadRewarded);
        bLoadRewardedVideo.setOnClickListener(v -> loadRewardedVideo());
        bShowRewardedVideo = findViewById(R.id.bShowRewarded);
        bShowRewardedVideo.setOnClickListener(v -> showRewardedVideo());
        bLoadNative = findViewById(R.id.bLoadNative);
        bLoadNative.setOnClickListener(v -> loadNative());
        bShowNative = findViewById(R.id.bShowNative);
        bShowNative.setOnClickListener(v -> showNative());
        adContainer = findViewById(R.id.adContainer);

        if (BidMachine.isInitialized()) {
            bInitialize.setEnabled(false);
            enableButton();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyBanner();
        destroyInterstitial();
        destroyRewardedVideo();
        destroyNative();
    }

    private void initialize() {
        //Initialize BidMachine SDK first
        BidMachine.setTestMode(true);
        BidMachine.setLoggingEnabled(true);
        BidMachine.initialize(this, "5");

        enableButton();
    }

    private void enableButton() {
        bLoadBanner.setEnabled(true);
        bLoadInterstitial.setEnabled(true);
        bLoadRewardedVideo.setEnabled(true);
        bLoadNative.setEnabled(true);
    }

    /**
     * Method for load banner from AdMob
     */
    private void loadBanner() {
        bShowBanner.setEnabled(false);

        //Destroy previous AdView
        destroyBanner();

        Log.d(TAG, "AdMob loadBanner");

        BannerRequest bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_320x50)
                .setListener(new BannerRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        // Fetch BidMachine Ads
                        Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
                        if (fetchParams != null) {
                            //Request callbacks run in background thread, but you should call AdMob load methods on UI thread
                            runOnUiThread(() -> loadAdMobBanner(fetchParams));
                        } else {
                            runOnUiThread(() -> Toast.makeText(
                                    BidMachineAdMobFetchActivity.this,
                                    "BannerFetchFailed",
                                    Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(
                                BidMachineAdMobFetchActivity.this,
                                "BannerFetchFailed",
                                Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull BannerRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        //Request BidMachine Ads without load it
        bannerRequest.request(this);
    }

    private void loadAdMobBanner(@NonNull Map<String, String> fetchParams) {
        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                //Set fetching parameters
                .setFetchParams(fetchParams)
                .build();

        //Set bundle to custom event banner
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventBanner.class, bundle)
                .build();

        //Create new AdView instance and load
        adView = new AdView(this);
        adView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adView.setAdUnitId(BANNER_ID);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdListener(new BannerViewListener());

        //Load AdMob Ads
        adView.loadAd(adRequest);
    }

    /**
     * Method for show banner from AdMob
     */
    private void showBanner() {
        if (adView != null && adView.getParent() == null) {
            Log.d(TAG, "AdView showBanner");

            //Add AdView for show
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            Log.d(TAG, "AdView null, load banner first");
        }
    }

    /**
     * Method for destroy AdView
     */
    private void destroyBanner() {
        if (adView != null) {
            Log.d(TAG, "AdView destroyBanner");

            adContainer.removeAllViews();
            adView.setAdListener(null);
            adView.destroy();
            adView = null;
        }
    }

    /**
     * Method for load interstitial from AdMob
     */
    private void loadInterstitial() {
        bShowInterstitial.setEnabled(false);

        //Destroy previous InterstitialAd
        destroyInterstitial();

        Log.d(TAG, "InterstitialAd loadInterstitial");

        InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
                .setListener(new InterstitialRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull InterstitialRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        // Fetch BidMachine Ads
                        Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
                        if (fetchParams != null) {
                            //Request callbacks run in background thread, but you should call AdMob load methods on UI thread
                            runOnUiThread(() -> loadAdMobInterstitial(fetchParams));
                        } else {
                            runOnUiThread(() -> Toast.makeText(
                                    BidMachineAdMobFetchActivity.this,
                                    "InterstitialFetchFailed",
                                    Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull InterstitialRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(
                                BidMachineAdMobFetchActivity.this,
                                "InterstitialFetchFailed",
                                Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull InterstitialRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        //Request BidMachine Ads without load it
        interstitialRequest.request(this);
    }

    private void loadAdMobInterstitial(@NonNull Map<String, String> fetchParams) {
        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                //Set fetching parameters
                .setFetchParams(fetchParams)
                .build();

        //Set bundle to custom event interstitial
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class, bundle)
                .build();

        //Create new InterstitialAd instance and load
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_ID);
        interstitialAd.setAdListener(new InterstitialListener());

        //Load AdMob Ads
        interstitialAd.loadAd(adRequest);
    }

    /**
     * Method for show interstitial from AdMob
     */
    private void showInterstitial() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            Log.d(TAG, "InterstitialAd showInterstitial");

            interstitialAd.show();
        } else {
            Log.d(TAG, "InterstitialAd null, load interstitial first");
        }
    }

    /**
     * Method for destroy InterstitialAd
     */
    private void destroyInterstitial() {
        if (interstitialAd != null) {
            Log.d(TAG, "InterstitialAd destroyInterstitial");

            interstitialAd.setAdListener(null);
            interstitialAd = null;
        }
    }

    /**
     * Method for load rewarded video from AdMob
     */
    private void loadRewardedVideo() {
        bShowRewardedVideo.setEnabled(false);

        //Destroy previous RewardedVideoAd
        destroyRewardedVideo();

        Log.d(TAG, "RewardedVideoAd loadRewardedVideo");

        RewardedRequest rewardedRequest = new RewardedRequest.Builder()
                .setListener(new RewardedRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull RewardedRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        // Fetch BidMachine Ads
                        Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
                        if (fetchParams != null) {
                            //Request callbacks run in background thread, but you should call AdMob load methods on UI thread
                            runOnUiThread(() -> loadAdMobRewardedVideo(fetchParams));
                        } else {
                            runOnUiThread(() -> Toast.makeText(
                                    BidMachineAdMobFetchActivity.this,
                                    "RewardedFetchFailed",
                                    Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull RewardedRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(
                                BidMachineAdMobFetchActivity.this,
                                "RewardedFetchFailed",
                                Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull RewardedRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        //Request BidMachine Ads without load it
        rewardedRequest.request(this);
    }

    private void loadAdMobRewardedVideo(@NonNull Map<String, String> fetchParams) {
        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                //Set fetching parameters
                .setFetchParams(fetchParams)
                .build();

        //Set bundle to mediation rewarded video ad adapter
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(BidMachineMediationRewardedAdAdapter.class, bundle)
                .build();

        //Create new RewardedVideoAd instance and load
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoListener());

        //Load AdMob Ads
        rewardedVideoAd.loadAd(REWARDED_ID, adRequest);
    }

    /**
     * Method for show rewarded video from AdMob
     */
    private void showRewardedVideo() {
        if (rewardedVideoAd != null && rewardedVideoAd.isLoaded()) {
            Log.d(TAG, "RewardedVideoAd showRewardedVideo");

            rewardedVideoAd.show();
        } else {
            Log.d(TAG, "RewardedVideo not loaded");
        }
    }

    /**
     * Method for destroy RewardedAd
     */
    private void destroyRewardedVideo() {
        if (rewardedVideoAd != null) {
            Log.d(TAG, "RewardedVideoAd destroyRewardedVideo");

            rewardedVideoAd.setRewardedVideoAdListener(null);
            rewardedVideoAd.destroy(this);
            rewardedVideoAd = null;
        }
    }

    /**
     * Method for load native from AdMob
     */
    private void loadNative() {
        bShowNative.setEnabled(false);

        //Destroy previous NativeAd
        destroyNative();

        Log.d(TAG, "UnifiedNativeAd loadNative");

        NativeRequest nativeRequest = new NativeRequest.Builder()
                .setListener(new NativeRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull NativeRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        // Fetch BidMachine Ads
                        Map<String, String> fetchParams = BidMachineFetcher.fetch(bannerRequest);
                        if (fetchParams != null) {
                            //Request callbacks run in background thread, but you should call AdMob load methods on UI thread
                            runOnUiThread(() -> loadAdMobNative(fetchParams));
                        } else {
                            runOnUiThread(() -> Toast.makeText(
                                    BidMachineAdMobFetchActivity.this,
                                    "NativeFetchFailed",
                                    Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull NativeRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> Toast.makeText(
                                BidMachineAdMobFetchActivity.this,
                                "NativeFetchFailed",
                                Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onRequestExpired(@NonNull NativeRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        //Request BidMachine Ads without load it
        nativeRequest.request(this);
    }

    private void loadAdMobNative(@NonNull Map<String, String> fetchParams) {
        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                //Set fetching parameters
                .setFetchParams(fetchParams)
                .build();

        //Set bundle to mediation native ad adapter
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventNative.class, bundle)
                .build();

        //Create new AdLoader instance and load
        NativeListener nativeListener = new NativeListener();
        AdLoader adLoader = new AdLoader.Builder(this, NATIVE_ID)
                .forUnifiedNativeAd(nativeListener)
                .withAdListener(nativeListener)
                .build();
        adLoader.loadAd(adRequest);
    }

    /**
     * Method for show native from AdMob
     */
    private void showNative() {
        if (nativeAd == null) {
            Log.d(TAG, "UnifiedNativeAd not loaded");
            return;
        }
        Log.d(TAG, "UnifiedNativeAd showNative");

        UnifiedNativeAdView unifiedNativeAdView = (UnifiedNativeAdView) LayoutInflater.from(this)
                .inflate(R.layout.native_ad, adContainer, false);
        fillNative(unifiedNativeAdView, nativeAd);
        unifiedNativeAdView.setNativeAd(nativeAd);
        adContainer.removeAllViews();
        adContainer.addView(unifiedNativeAdView);
    }

    /**
     * Method sets the text, images and the native ad, etc into the ad view
     *
     * @param unifiedNativeAdView container what will be filled by assets from UnifiedNativeAd
     * @param unifiedNativeAd     data storage which contains title, description, etc
     */
    private void fillNative(UnifiedNativeAdView unifiedNativeAdView,
                            UnifiedNativeAd unifiedNativeAd) {
        TextView titleView = unifiedNativeAdView.findViewById(R.id.txtTitle);
        titleView.setText(unifiedNativeAd.getHeadline());

        TextView descriptionView = unifiedNativeAdView.findViewById(R.id.txtDescription);
        descriptionView.setText(unifiedNativeAd.getBody());

        float rating = unifiedNativeAd.getStarRating() != null
                ? unifiedNativeAd.getStarRating().floatValue()
                : 0;
        RatingBar ratingBar = unifiedNativeAdView.findViewById(R.id.ratingBar);
        ratingBar.setRating(rating);

        Button ctaView = unifiedNativeAdView.findViewById(R.id.btnCta);
        ctaView.setText(unifiedNativeAd.getCallToAction());

        ImageView iconView = unifiedNativeAdView.findViewById(R.id.icon);
        unifiedNativeAdView.setIconView(iconView);

        MediaView mediaView = unifiedNativeAdView.findViewById(R.id.mediaView);
        unifiedNativeAdView.setMediaView(mediaView);
    }

    /**
     * Method for destroy UnifiedNativeAd
     */
    private void destroyNative() {
        if (nativeAd != null) {
            Log.d(TAG, "InterstitialAd destroyInterstitial");

            nativeAd.destroy();
            nativeAd = null;
        }
    }

    /**
     * Class for definition behavior AdView
     */
    private class BannerViewListener extends AdListener {

        @Override
        public void onAdLoaded() {
            bShowBanner.setEnabled(true);

            Log.d(TAG, "AdView onBannerLoaded");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "BannerLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "AdView onBannerFailedToLoad with errorCode - " + i + ")");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "BannerFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "AdView onBannerOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "AdView onBannerImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "AdView onBannerClicked");
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "AdView onBannerClosed");
        }

        @Override
        public void onAdLeftApplication() {
            Log.d(TAG, "AdView onBannerLeftApplication");
        }

    }

    /**
     * Class for definition behavior InterstitialAd
     */
    private class InterstitialListener extends AdListener {

        @Override
        public void onAdLoaded() {
            bShowInterstitial.setEnabled(true);

            Log.d(TAG, "InterstitialAd onInterstitialLoaded");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "InterstitialLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "InterstitialAd onInterstitialFailedToLoad with errorCode - " + i + ")");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "InterstitialFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "InterstitialAd onInterstitialOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "InterstitialAd onInterstitialImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "InterstitialAd onInterstitialClicked");
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "InterstitialAd onInterstitialClosed");
        }

        @Override
        public void onAdLeftApplication() {
            Log.d(TAG, "InterstitialAd onInterstitialLeftApplication");
        }

    }

    /**
     * Class for definition behavior RewardedVideoAd
     */
    private class RewardedVideoListener implements RewardedVideoAdListener {

        @Override
        public void onRewardedVideoAdLoaded() {
            bShowRewardedVideo.setEnabled(true);

            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdLoaded");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "RewardedVideoAdLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdFailedToLoad with errorCode - " + i + ")");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "RewardedVideoAdFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdOpened() {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdOpened");
        }

        @Override
        public void onRewardedVideoStarted() {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoStarted");
        }

        @Override
        public void onRewardedVideoCompleted() {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoCompleted");
        }

        @Override
        public void onRewardedVideoAdClosed() {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdClosed");
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            Log.d(TAG, "RewardedVideoAd onRewarded");
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdLeftApplication");
        }
    }

    /**
     * Class for definition behavior UnifiedNativeAd
     */
    private class NativeListener extends AdListener implements UnifiedNativeAd.OnUnifiedNativeAdLoadedListener {

        @Override
        public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
            bShowNative.setEnabled(true);

            nativeAd = unifiedNativeAd;
            Log.d(TAG, "NativeAd onNativeAdLoaded");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "NativeAdLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            Log.d(TAG, "NativeAd onNativeAdFailedToLoad with errorCode - " + errorCode + ")");
            Toast.makeText(
                    BidMachineAdMobFetchActivity.this,
                    "NativeAdFailedToLoad",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "NativeAd onNativeAdOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "NativeAd onNativeAdImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "NativeAd onNativeAdClicked");
        }
    }

}