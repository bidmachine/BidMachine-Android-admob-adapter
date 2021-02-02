package io.bidmachine.examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ads.mediation.bidmachine.BidMachineUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import io.bidmachine.BidMachine;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.banner.BannerView;
import io.bidmachine.interstitial.InterstitialAd;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineAdManagerActivity extends Activity {

    private static final String TAG = BidMachineAdManagerActivity.class.getSimpleName();
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String BANNER_ID = "YOUR_BANNER_ID";
    private static final String INTERSTITIAL_ID = "YOUR_INTERSTITIAL_ID";
    private static final String REWARDED_ID = "YOUR_REWARDED_ID";

    private Button bInitialize;
    private Button bLoadBanner;
    private Button bShowBanner;
    private Button bLoadInterstitial;
    private Button bShowInterstitial;
    private Button bLoadRewarded;
    private Button bShowRewarded;
    private FrameLayout adContainer;

    private BannerRequest bannerRequest;
    private AdManagerAdView adManagerAdView;
    private BannerView bidMachineBannerView;

    private InterstitialRequest interstitialRequest;
    private InterstitialAd bidMachineInterstitialAd;

    private RewardedRequest rewardedRequest;
    private io.bidmachine.rewarded.RewardedAd bidMachineRewardedAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_manager);

        bInitialize = findViewById(R.id.bInitialize);
        bInitialize.setOnClickListener(v -> initialize());
        bLoadBanner = findViewById(R.id.bLoadBanner);
        bLoadBanner.setOnClickListener(v -> loadBanner());
        bShowBanner = findViewById(R.id.bShowBanner);
        bShowBanner.setOnClickListener(v -> showBanner());
        bLoadInterstitial = findViewById(R.id.bLoadInterstitial);
        bLoadInterstitial.setOnClickListener(v -> loadInterstitial());
        bShowInterstitial = findViewById(R.id.bShowInterstitial);
        bShowInterstitial.setOnClickListener(v -> showInterstitial());
        bLoadRewarded = findViewById(R.id.bLoadRewarded);
        bLoadRewarded.setOnClickListener(v -> loadRewarded());
        bShowRewarded = findViewById(R.id.bShowRewarded);
        bShowRewarded.setOnClickListener(v -> showRewarded());
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
        destroyRewarded();
    }

    private void initialize() {
        // Initialize BidMachine SDK first
        BidMachine.setTestMode(true);
        BidMachine.setLoggingEnabled(true);
        BidMachine.initialize(this, BID_MACHINE_SELLER_ID);

        bInitialize.setEnabled(false);
        enableButton();
    }

    private void enableButton() {
        bLoadBanner.setEnabled(true);
        bLoadInterstitial.setEnabled(true);
        bLoadRewarded.setEnabled(true);
    }

    /**
     * Method for load BannerRequest
     */
    private void loadBanner() {
        bShowBanner.setEnabled(false);

        // Destroy previous ad
        destroyBanner();

        // Create new BidMachine request
        bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_320x50)
                .setListener(new BannerRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdManagerBanner());
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "BannerRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdManagerActivity.this,
                                           "BannerFetchFailed",
                                           Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull BannerRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        bannerRequest.request(this);

        Log.d(TAG, "loadBanner");
    }

    /**
     * Method for load AdManagerAdView
     */
    private void loadAdManagerBanner() {
        Log.d(TAG, "loadAdManagerBanner");

        /*
        You need to set up price rounding with method BidMachineFetcher.setPriceRounding
        before creating AdRequest object
        For example:
        double price = bannerRequest.getAuctionResult().getPrice();
        if (price <= 1) {
            BidMachineFetcher.setPriceRounding(0.2);
        } else {
            BidMachineFetcher.setPriceRounding(1);
        }
        */

        // Create AdManagerAdRequest builder
        AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();

        // Append BidMachine BannerRequest to AdManagerAdRequest
        BidMachineUtils.appendRequest(adRequestBuilder, bannerRequest);

        // Create new AdView instance and load
        adManagerAdView = new AdManagerAdView(this);
        adManagerAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                   ViewGroup.LayoutParams.MATCH_PARENT));
        adManagerAdView.setAdUnitId(BANNER_ID);
        adManagerAdView.setAdSizes(AdSize.BANNER);
        adManagerAdView.setAdListener(new BannerListener());
        adManagerAdView.loadAd(adRequestBuilder.build());
    }

    /**
     * Method for load BannerView
     */
    private void loadBidMachineBanner() {
        Log.d(TAG, "loadBidMachineBanner");

        // Create BannerView for load with previously loaded BannerRequest
        bidMachineBannerView = new BannerView(this);
        bidMachineBannerView.setListener(new BidMachineBannerListener());
        bidMachineBannerView.load(bannerRequest);
    }

    /**
     * Method for show BidMachine BannerView
     */
    private void showBanner() {
        Log.d(TAG, "showBanner");

        bShowBanner.setEnabled(false);

        // Checking for can show before showing ads
        if (bidMachineBannerView != null
                && bidMachineBannerView.canShow()
                && bidMachineBannerView.getParent() == null) {
            adContainer.removeAllViews();
            adContainer.addView(bidMachineBannerView);
        } else {
            Log.d(TAG, "show error - banner object is null");
        }
    }

    /**
     * Method for destroy banner
     */
    private void destroyBanner() {
        Log.d(TAG, "destroyBanner");

        adContainer.removeAllViews();
        if (bidMachineBannerView != null) {
            bidMachineBannerView.setListener(null);
            bidMachineBannerView.destroy();
            bidMachineBannerView = null;
        }
        if (adManagerAdView != null) {
            adManagerAdView.setAdListener(null);
            adManagerAdView.destroy();
            adManagerAdView = null;
        }
        if (bannerRequest != null) {
            bannerRequest = null;
        }
    }

    /**
     * Method for load InterstitialRequest
     */
    private void loadInterstitial() {
        bShowInterstitial.setEnabled(false);

        // Destroy previous ad
        destroyInterstitial();

        // Create new BidMachine request
        interstitialRequest = new InterstitialRequest.Builder()
                .setListener(new InterstitialRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdManagerInterstitial());
                    }

                    @Override
                    public void onRequestFailed(@NonNull InterstitialRequest interstitialRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "InterstitialRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdManagerActivity.this,
                                           "InterstitialFetchFailed",
                                           Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull InterstitialRequest interstitialRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        interstitialRequest.request(this);

        Log.d(TAG, "loadInterstitial");
    }

    /**
     * Method for load AdManagerInterstitialAd
     */
    private void loadAdManagerInterstitial() {
        Log.d(TAG, "loadAdManagerInterstitial");

        /*
        You need to set up price rounding with method BidMachineFetcher.setPriceRounding
        before creating AdRequest object
        For example:
        double price = bannerRequest.getAuctionResult().getPrice();
        if (price <= 1) {
            BidMachineFetcher.setPriceRounding(0.2);
        } else {
            BidMachineFetcher.setPriceRounding(1);
        }
        */

        // Create AdManagerAdRequest builder
        AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();

        // Append BidMachine InterstitialRequest to AdManagerAdRequest
        BidMachineUtils.appendRequest(adRequestBuilder, interstitialRequest);

        // Load InterstitialAd
        AdManagerInterstitialAd.load(this,
                                     INTERSTITIAL_ID,
                                     adRequestBuilder.build(),
                                     new InterstitialLoadListener());
    }

    /**
     * Method for load BidMachine InterstitialAd
     */
    private void loadBidMachineInterstitial() {
        Log.d(TAG, "loadBidMachineInterstitial");

        // Create InterstitialAd for load with previously loaded InterstitialRequest
        bidMachineInterstitialAd = new InterstitialAd(this);
        bidMachineInterstitialAd.setListener(new BidMachineInterstitialListener());
        bidMachineInterstitialAd.load(interstitialRequest);
    }

    /**
     * Method for show BidMachine InterstitialAd
     */
    private void showInterstitial() {
        Log.d(TAG, "showInterstitial");

        bShowInterstitial.setEnabled(false);

        // Checking for can show before showing ads
        if (bidMachineInterstitialAd != null && bidMachineInterstitialAd.canShow()) {
            bidMachineInterstitialAd.show();
        } else {
            Log.d(TAG, "show error - interstitial object not loaded");
        }
    }

    /**
     * Method for destroy interstitial
     */
    private void destroyInterstitial() {
        Log.d(TAG, "destroyInterstitial");

        if (bidMachineInterstitialAd != null) {
            bidMachineInterstitialAd.setListener(null);
            bidMachineInterstitialAd.destroy();
            bidMachineInterstitialAd = null;
        }
        if (interstitialRequest != null) {
            interstitialRequest = null;
        }
    }

    /**
     * Method for load RewardedRequest
     */
    private void loadRewarded() {
        bShowRewarded.setEnabled(false);

        // Destroy previous ad
        destroyRewarded();

        // Create new BidMachine request
        rewardedRequest = new RewardedRequest.Builder()
                .setListener(new RewardedRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdManagerRewarded());
                    }

                    @Override
                    public void onRequestFailed(@NonNull RewardedRequest rewardedRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "RewardedRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdManagerActivity.this,
                                           "RewardedFetchFailed",
                                           Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull RewardedRequest rewardedRequest) {
                        //ignore
                    }
                })
                .build();

        // Request BidMachine Ads without load it
        rewardedRequest.request(this);

        Log.d(TAG, "loadRewarded");
    }

    /**
     * Method for load AdManager RewardedAd
     */
    private void loadAdManagerRewarded() {
        Log.d(TAG, "loadAdManagerRewarded");

        /*
        You need to set up price rounding with method BidMachineFetcher.setPriceRounding
        before creating AdRequest object
        For example:
        double price = bannerRequest.getAuctionResult().getPrice();
        if (price <= 1) {
            BidMachineFetcher.setPriceRounding(0.2);
        } else {
            BidMachineFetcher.setPriceRounding(1);
        }
        */

        // Create AdManagerAdRequest builder
        AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();

        // Append BidMachine RewardedRequest to AdManagerAdRequest
        BidMachineUtils.appendRequest(adRequestBuilder, rewardedRequest);

        // Load RewardedAd
        RewardedAd.load(this,
                        REWARDED_ID,
                        adRequestBuilder.build(),
                        new RewardedAdLoadListener());
    }

    /**
     * Method for load BidMachine RewardedAd
     */
    private void loadBidMachineRewarded() {
        Log.d(TAG, "loadBidMachineRewarded");

        // Create RewardedAd for load with previously loaded RewardedRequest
        bidMachineRewardedAd = new io.bidmachine.rewarded.RewardedAd(this);
        bidMachineRewardedAd.setListener(new BidMachineRewardedListener());
        bidMachineRewardedAd.load(rewardedRequest);
    }

    /**
     * Method for show BidMachine RewardedAd
     */
    private void showRewarded() {
        Log.d(TAG, "showRewarded");

        bShowRewarded.setEnabled(false);

        // Checking for can show before showing ads
        if (bidMachineRewardedAd != null && bidMachineRewardedAd.canShow()) {
            bidMachineRewardedAd.show();
        } else {
            Log.d(TAG, "show error - rewarded object not loaded");
        }
    }

    /**
     * Method for destroy rewarded ad
     */
    private void destroyRewarded() {
        Log.d(TAG, "destroyRewarded");

        if (bidMachineRewardedAd != null) {
            bidMachineRewardedAd.setListener(null);
            bidMachineRewardedAd.destroy();
            bidMachineRewardedAd = null;
        }
        if (rewardedRequest != null) {
            rewardedRequest = null;
        }
    }


    /**
     * Class for definition behavior AdManagerAdView
     */
    private class BannerListener extends AdListener {

        @Override
        public void onAdLoaded() {
            Log.d(TAG, "BannerListener - onAdLoaded");

            // Checking whether it is BidMachine or not
            BidMachineUtils.isBidMachineBanner(adManagerAdView, isSuccess -> {
                if (isSuccess) {
                    loadBidMachineBanner();
                } else {
                    onError("Invalid key");
                }
            });
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            onError(loadAdError.getMessage());
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "BannerListener - onAdOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "BannerListener - onAdImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "BannerListener - onAdClicked");
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "BannerListener - onAdClosed");
        }

        private void onError(@NonNull String message) {
            Log.d(TAG, "BannerListener - onAdFailedToLoad with message: " + message);
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BannerFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Class for definition behavior BidMachine BannerView
     */
    private class BidMachineBannerListener implements io.bidmachine.banner.BannerListener {

        @Override
        public void onAdLoaded(@NonNull BannerView bannerView) {
            bShowBanner.setEnabled(true);

            Log.d(TAG, "BidMachineBannerListener - onAdLoaded");
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BannerLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdLoadFailed(@NonNull BannerView bannerView, @NonNull BMError bmError) {
            Log.d(TAG, "BidMachineBannerListener - onAdLoadFailed with message: "
                    + bmError.getMessage());
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BannerFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdShown(@NonNull BannerView bannerView) {
            Log.d(TAG, "BidMachineBannerListener - onAdShown");
        }

        @Override
        public void onAdImpression(@NonNull BannerView bannerView) {
            Log.d(TAG, "BidMachineBannerListener - onAdImpression");
        }

        @Override
        public void onAdClicked(@NonNull BannerView bannerView) {
            Log.d(TAG, "BidMachineBannerListener - onAdClicked");
        }

        @Override
        public void onAdExpired(@NonNull BannerView bannerView) {
            Log.d(TAG, "BidMachineBannerListener - onAdExpired");
        }

    }

    /**
     * Class for definition behavior InterstitialAd
     */
    private class InterstitialLoadListener extends AdManagerInterstitialAdLoadCallback {

        @Override
        public void onAdLoaded(@NonNull AdManagerInterstitialAd adManagerInterstitialAd) {
            Log.d(TAG, "InterstitialLoadListener - onAdLoaded");

            // Checking whether it is BidMachine or not
            BidMachineUtils.isBidMachineInterstitial(adManagerInterstitialAd, isSuccess -> {
                if (isSuccess) {
                    loadBidMachineInterstitial();
                } else {
                    onError("Invalid key");
                }
            });
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            onError(loadAdError.getMessage());
        }

        private void onError(@NonNull String message) {
            Log.d(TAG, "InterstitialLoadListener - onError with message: " + message);
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "InterstitialFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Class for definition behavior BidMachine InterstitialAd
     */
    private class BidMachineInterstitialListener implements io.bidmachine.interstitial.InterstitialListener {

        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            bShowInterstitial.setEnabled(true);

            Log.d(TAG, "BidMachineInterstitialListener - onAdLoaded");
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BidMachineInterstitialLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdLoadFailed(@NonNull InterstitialAd interstitialAd,
                                   @NonNull BMError bmError) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdLoadFailed with message: "
                    + bmError.getMessage());
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BidMachineInterstitialFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdShown(@NonNull InterstitialAd interstitialAd) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdShown");
        }

        @Override
        public void onAdShowFailed(@NonNull InterstitialAd interstitialAd,
                                   @NonNull BMError bmError) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdShowFailed");
        }

        @Override
        public void onAdImpression(@NonNull InterstitialAd interstitialAd) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdImpression");
        }

        @Override
        public void onAdClicked(@NonNull InterstitialAd interstitialAd) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdClicked");
        }

        @Override
        public void onAdClosed(@NonNull InterstitialAd interstitialAd, boolean b) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdClosed");
        }

        @Override
        public void onAdExpired(@NonNull InterstitialAd interstitialAd) {
            Log.d(TAG, "BidMachineInterstitialListener - onAdExpired");
        }

    }

    /**
     * Class for definition behavior RewardedAdLoadCallback
     */
    private class RewardedAdLoadListener extends RewardedAdLoadCallback {

        @Override
        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
            // Checking whether it is BidMachine or not
            if (BidMachineUtils.isBidMachineRewarded(rewardedAd)) {
                loadBidMachineRewarded();
            } else {
                onError("Invalid key");
            }
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            onError(loadAdError.getMessage());
        }

        private void onError(@NonNull String message) {
            Log.d(TAG, "RewardedAdLoadListener - onError with message: " + message);
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "RewardedAdFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Class for definition behavior BidMachine RewardedAd
     */
    private class BidMachineRewardedListener implements io.bidmachine.rewarded.RewardedListener {

        @Override
        public void onAdLoaded(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd) {
            bShowRewarded.setEnabled(true);

            Log.d(TAG, "BidMachineRewardedListener - onAdLoaded");
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BidMachineRewardedLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdLoadFailed(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd,
                                   @NonNull BMError bmError) {
            Log.d(TAG, "BidMachineRewardedListener - onAdLoadFailed with message: "
                    + bmError.getMessage());
            Toast.makeText(BidMachineAdManagerActivity.this,
                           "BidMachineRewardedFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdShown(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd) {
            Log.d(TAG, "BidMachineRewardedListener - onAdShown");
        }

        @Override
        public void onAdShowFailed(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd,
                                   @NonNull BMError bmError) {
            Log.d(TAG, "BidMachineRewardedListener - onAdShowFailed");
        }

        @Override
        public void onAdImpression(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd) {
            Log.d(TAG, "BidMachineRewardedListener - onAdImpression");
        }

        @Override
        public void onAdClicked(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd) {
            Log.d(TAG, "BidMachineRewardedListener - onAdClicked");
        }

        @Override
        public void onAdClosed(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd, boolean b) {
            Log.d(TAG, "BidMachineRewardedListener - onAdClosed");
        }

        @Override
        public void onAdRewarded(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd) {
            Log.d(TAG, "BidMachineRewardedListener - onAdRewarded");
        }

        @Override
        public void onAdExpired(@NonNull io.bidmachine.rewarded.RewardedAd rewardedAd) {
            Log.d(TAG, "BidMachineRewardedListener - onAdExpired");
        }

    }

}