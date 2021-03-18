package io.bidmachine.examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.ads.mediation.bidmachine.BidMachineUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import io.bidmachine.BidMachine;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.nativead.NativeRequest;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineAdMobFetchActivity extends Activity {

    private static final String TAG = BidMachineAdMobFetchActivity.class.getSimpleName();
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String BANNER_ID = "YOUR_BANNER_ID";
    private static final String MREC_ID = "YOUR_MREC_ID";
    private static final String INTERSTITIAL_ID = "YOUR_INTERSTITIAL_ID";
    private static final String REWARDED_ID = "YOUR_REWARDED_ID";
    private static final String NATIVE_ID = "YOUR_NATIVE_ID";

    private Button bInitialize;
    private Button bLoadBanner;
    private Button bShowBanner;
    private Button bLoadMrec;
    private Button bShowMrec;
    private Button bLoadInterstitial;
    private Button bShowInterstitial;
    private Button bLoadRewarded;
    private Button bShowRewarded;
    private Button bLoadNative;
    private Button bShowNative;
    private FrameLayout adContainer;

    private AdView bannerAdView;
    private AdView mrecAdView;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private NativeAd nativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob_fetch);

        bInitialize = findViewById(R.id.bInitialize);
        bInitialize.setOnClickListener(v -> initialize());
        bLoadBanner = findViewById(R.id.bLoadBanner);
        bLoadBanner.setOnClickListener(v -> loadBanner());
        bShowBanner = findViewById(R.id.bShowBanner);
        bShowBanner.setOnClickListener(v -> showBanner());
        bLoadMrec = findViewById(R.id.bLoadMrec);
        bLoadMrec.setOnClickListener(v -> loadMrec());
        bShowMrec = findViewById(R.id.bShowMrec);
        bShowMrec.setOnClickListener(v -> showMrec());
        bLoadInterstitial = findViewById(R.id.bLoadInterstitial);
        bLoadInterstitial.setOnClickListener(v -> loadInterstitial());
        bShowInterstitial = findViewById(R.id.bShowInterstitial);
        bShowInterstitial.setOnClickListener(v -> showInterstitial());
        bLoadRewarded = findViewById(R.id.bLoadRewarded);
        bLoadRewarded.setOnClickListener(v -> loadRewarded());
        bShowRewarded = findViewById(R.id.bShowRewarded);
        bShowRewarded.setOnClickListener(v -> showRewarded());
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
        destroyMrec();
        destroyInterstitial();
        destroyRewarded();
        destroyNative();
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
        bLoadMrec.setEnabled(true);
        bLoadInterstitial.setEnabled(true);
        bLoadRewarded.setEnabled(true);
        bLoadNative.setEnabled(true);
    }

    private void addAdView(View view) {
        adContainer.removeAllViews();
        adContainer.addView(view);
    }

    /**
     * Method for load BannerRequest
     */
    private void loadBanner() {
        bShowBanner.setEnabled(false);

        // Destroy previous AdView
        destroyBanner();

        // Create new BidMachine request
        BannerRequest bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_320x50)
                .setListener(new BannerRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdMobBanner(bannerRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "BannerRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdMobFetchActivity.this,
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

        // Request an ad from BidMachine without loading it
        bannerRequest.request(this);

        Log.d(TAG, "loadBanner");
    }

    /**
     * Method for load AdView
     */
    private void loadAdMobBanner(@NonNull BannerRequest bannerRequest) {
        Log.d(TAG, "loadAdMobBanner");

        // Create AdRequest
        AdRequest adRequest = BidMachineUtils.createAdRequest(bannerRequest);

        // Create new AdView instance and load it
        bannerAdView = new AdView(this);
        bannerAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                ViewGroup.LayoutParams.MATCH_PARENT));
        bannerAdView.setAdUnitId(BANNER_ID);
        bannerAdView.setAdSize(AdSize.BANNER);
        bannerAdView.setAdListener(new BannerViewListener());
        bannerAdView.loadAd(adRequest);
    }

    /**
     * Method for show AdView
     */
    private void showBanner() {
        Log.d(TAG, "showBanner");

        bShowBanner.setEnabled(false);

        if (bannerAdView != null && bannerAdView.getParent() == null) {
            addAdView(bannerAdView);
        } else {
            Log.d(TAG, "show error - banner object is null");
        }
    }

    /**
     * Method for destroy AdView
     */
    private void destroyBanner() {
        Log.d(TAG, "destroyBanner");

        adContainer.removeAllViews();
        if (bannerAdView != null) {
            bannerAdView.setAdListener(null);
            bannerAdView.destroy();
            bannerAdView = null;
        }
    }

    /**
     * Method for load BannerRequest
     */
    private void loadMrec() {
        bShowMrec.setEnabled(false);

        // Destroy previous AdView
        destroyMrec();

        // Create new BidMachine request
        BannerRequest bannerRequest = new BannerRequest.Builder()
                .setSize(BannerSize.Size_300x250)
                .setListener(new BannerRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdMobMrec(bannerRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "BannerRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdMobFetchActivity.this,
                                           "MrecFetchFailed",
                                           Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull BannerRequest bannerRequest) {
                        //ignore
                    }
                })
                .build();

        // Request an ad from BidMachine without loading it
        bannerRequest.request(this);

        Log.d(TAG, "loadMrec");
    }

    /**
     * Method for load AdView
     */
    private void loadAdMobMrec(@NonNull BannerRequest bannerRequest) {
        Log.d(TAG, "loadAdMobMrec");

        // Create AdRequest
        AdRequest adRequest = BidMachineUtils.createAdRequest(bannerRequest);

        // Create new AdView instance and load it
        mrecAdView = new AdView(this);
        mrecAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                              ViewGroup.LayoutParams.MATCH_PARENT));
        mrecAdView.setAdUnitId(MREC_ID);
        mrecAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        mrecAdView.setAdListener(new MrecViewListener());
        mrecAdView.loadAd(adRequest);
    }

    /**
     * Method for show AdView
     */
    private void showMrec() {
        Log.d(TAG, "showMrec");

        bShowMrec.setEnabled(false);

        if (mrecAdView != null && mrecAdView.getParent() == null) {
            addAdView(mrecAdView);
        } else {
            Log.d(TAG, "show error - mrec object is null");
        }
    }

    /**
     * Method for destroy AdView
     */
    private void destroyMrec() {
        Log.d(TAG, "destroyMrec");

        adContainer.removeAllViews();
        if (mrecAdView != null) {
            mrecAdView.setAdListener(null);
            mrecAdView.destroy();
            mrecAdView = null;
        }
    }

    /**
     * Method for load InterstitialRequest
     */
    private void loadInterstitial() {
        bShowInterstitial.setEnabled(false);

        // Destroy previous InterstitialAd
        destroyInterstitial();

        // Create new BidMachine request
        InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
                .setListener(new InterstitialRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdMobInterstitial(interstitialRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull InterstitialRequest interstitialRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "InterstitialRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdMobFetchActivity.this,
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

        // Request an ad from BidMachine without loading it
        interstitialRequest.request(this);

        Log.d(TAG, "loadInterstitial");
    }

    /**
     * Method for load InterstitialAd
     */
    private void loadAdMobInterstitial(@NonNull InterstitialRequest interstitialRequest) {
        Log.d(TAG, "loadAdMobInterstitial");

        // Create AdRequest
        AdRequest adRequest = BidMachineUtils.createAdRequest(interstitialRequest);

        // Load InterstitialAd
        InterstitialAd.load(this, INTERSTITIAL_ID, adRequest, new InterstitialLoadListener());
    }

    /**
     * Method for show InterstitialAd
     */
    private void showInterstitial() {
        Log.d(TAG, "showInterstitial");

        bShowInterstitial.setEnabled(false);

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new InterstitialShowListener());
            interstitialAd.show(this);
        } else {
            Log.d(TAG, "show error - interstitial object not loaded");
        }
    }

    /**
     * Method for destroy InterstitialAd
     */
    private void destroyInterstitial() {
        Log.d(TAG, "destroyInterstitial");

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(null);
            interstitialAd = null;
        }
    }

    /**
     * Method for load RewardedRequest
     */
    private void loadRewarded() {
        bShowRewarded.setEnabled(false);

        // Destroy previous RewardedAd
        destroyRewarded();

        // Create new BidMachine request
        RewardedRequest rewardedRequest = new RewardedRequest.Builder()
                .setListener(new RewardedRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdMobRewarded(rewardedRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull RewardedRequest rewardedRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "RewardedRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdMobFetchActivity.this,
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

        // Request an ad from BidMachine without loading it
        rewardedRequest.request(this);

        Log.d(TAG, "loadRewarded");
    }

    /**
     * Method for load RewardedAd
     */
    private void loadAdMobRewarded(@NonNull RewardedRequest rewardedRequest) {
        Log.d(TAG, "loadAdMobRewarded");

        // Create AdRequest
        AdRequest adRequest = BidMachineUtils.createAdRequest(rewardedRequest);

        // Load RewardedAd
        RewardedAd.load(this, REWARDED_ID, adRequest, new RewardedLoadListener());
    }

    /**
     * Method for show RewardedAd
     */
    private void showRewarded() {
        Log.d(TAG, "showRewarded");

        bShowRewarded.setEnabled(false);

        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new RewardedShowListener());
            rewardedAd.show(this, new RewardedEarnedListener());
        } else {
            Log.d(TAG, "show error - rewarded object not loaded");
        }
    }

    /**
     * Method for destroy RewardedAd
     */
    private void destroyRewarded() {
        Log.d(TAG, "destroyRewarded");

        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(null);
            rewardedAd = null;
        }
    }

    /**
     * Method for load NativeRequest
     */
    private void loadNative() {
        bShowNative.setEnabled(false);

        // Destroy previous NativeAd
        destroyNative();

        // Create new BidMachine request
        NativeRequest nativeRequest = new NativeRequest.Builder()
                .setListener(new NativeRequest.AdRequestListener() {
                    @Override
                    public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        runOnUiThread(() -> loadAdMobNative(nativeRequest));
                    }

                    @Override
                    public void onRequestFailed(@NonNull NativeRequest nativeRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(() -> {
                            Log.d(TAG, "NativeRequestListener - onRequestFailed");
                            Toast.makeText(BidMachineAdMobFetchActivity.this,
                                           "NativeFetchFailed",
                                           Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull NativeRequest nativeRequest) {
                        //ignore
                    }
                })
                .build();

        // Request an ad from BidMachine without loading it
        nativeRequest.request(this);

        Log.d(TAG, "loadNative");
    }

    /**
     * Method for load NativeAd
     */
    private void loadAdMobNative(@NonNull NativeRequest nativeRequest) {
        Log.d(TAG, "loadAdMobNative");

        // Create AdRequest
        AdRequest adRequest = BidMachineUtils.createAdRequest(nativeRequest);

        // Create new AdLoader instance and load
        NativeListener nativeListener = new NativeListener();
        AdLoader adLoader = new AdLoader.Builder(this, NATIVE_ID)
                .forNativeAd(nativeListener)
                .withAdListener(nativeListener)
                .build();
        adLoader.loadAd(adRequest);
    }

    /**
     * Method for show NativeAd
     */
    private void showNative() {
        Log.d(TAG, "showNative");

        bShowNative.setEnabled(false);

        if (nativeAd == null) {
            Log.d(TAG, "show error - native object not loaded");
            return;
        }

        NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(this)
                .inflate(R.layout.native_ad, adContainer, false);
        fillNative(nativeAdView, nativeAd);
        addAdView(nativeAdView);
    }

    /**
     * Method sets the text, images and the native ad, etc into the ad view
     *
     * @param nativeAdView container what will be filled by assets from NativeAd
     * @param nativeAd     data storage which contains title, description, etc
     */
    private void fillNative(NativeAdView nativeAdView, NativeAd nativeAd) {
        Log.d(TAG, "fillNative");

        TextView titleView = nativeAdView.findViewById(R.id.txtTitle);
        titleView.setText(nativeAd.getHeadline());

        TextView descriptionView = nativeAdView.findViewById(R.id.txtDescription);
        descriptionView.setText(nativeAd.getBody());

        float rating = nativeAd.getStarRating() != null
                ? nativeAd.getStarRating().floatValue()
                : 0;
        RatingBar ratingBar = nativeAdView.findViewById(R.id.ratingBar);
        ratingBar.setRating(rating);

        Button ctaView = nativeAdView.findViewById(R.id.btnCta);
        ctaView.setText(nativeAd.getCallToAction());

        ImageView iconView = nativeAdView.findViewById(R.id.icon);
        nativeAdView.setIconView(iconView);

        MediaView mediaView = nativeAdView.findViewById(R.id.mediaView);
        nativeAdView.setMediaView(mediaView);

        nativeAdView.setNativeAd(nativeAd);
    }

    /**
     * Method for destroy NativeAd
     */
    private void destroyNative() {
        Log.d(TAG, "destroyNative");

        if (nativeAd != null) {
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

            Log.d(TAG, "BannerViewListener - onAdLoaded");
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "BannerLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            Log.d(TAG, "BannerViewListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "BannerFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "BannerViewListener - onAdOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "BannerViewListener - onAdImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "BannerViewListener - onAdClicked");
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "BannerViewListener - onAdClosed");
        }

    }

    /**
     * Class for definition behavior AdView
     */
    private class MrecViewListener extends AdListener {

        @Override
        public void onAdLoaded() {
            bShowMrec.setEnabled(true);

            Log.d(TAG, "MrecViewListener - onAdLoaded");
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "MrecLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            Log.d(TAG, "MrecViewListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "MrecFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "MrecViewListener - onAdOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "MrecViewListener - onAdImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "MrecViewListener - onAdClicked");
        }

        @Override
        public void onAdClosed() {
            Log.d(TAG, "MrecViewListener - onAdClosed");
        }

    }

    /**
     * Class for definition behavior InterstitialAd
     */
    private class InterstitialLoadListener extends InterstitialAdLoadCallback {

        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            BidMachineAdMobFetchActivity.this.interstitialAd = interstitialAd;

            bShowInterstitial.setEnabled(true);

            Log.d(TAG, "InterstitialLoadListener - onAdLoaded");
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "InterstitialLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.d(TAG, "InterstitialLoadListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "InterstitialFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

    }

    private static class InterstitialShowListener extends FullScreenContentCallback {

        @Override
        public void onAdShowedFullScreenContent() {
            Log.d(TAG, "InterstitialShowListener - onAdShowedFullScreenContent");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            Log.d(TAG, "InterstitialShowListener - onAdFailedToShowFullScreenContent");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "InterstitialShowListener - onAdImpression");
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            Log.d(TAG, "InterstitialShowListener - onAdDismissedFullScreenContent");
        }

    }

    /**
     * Class for definition behavior RewardedAd
     */
    private class RewardedLoadListener extends RewardedAdLoadCallback {

        @Override
        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
            BidMachineAdMobFetchActivity.this.rewardedAd = rewardedAd;

            bShowRewarded.setEnabled(true);

            Log.d(TAG, "RewardedLoadListener - onAdLoaded");
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "RewardedLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.d(TAG, "RewardedLoadListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "RewardedFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

    }

    private static class RewardedShowListener extends FullScreenContentCallback {

        @Override
        public void onAdShowedFullScreenContent() {
            Log.d(TAG, "RewardedShowListener - onAdShowedFullScreenContent");
        }

        @Override
        public void onAdFailedToShowFullScreenContent(AdError adError) {
            Log.d(TAG, "RewardedShowListener - onAdFailedToShowFullScreenContent");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "RewardedShowListener - onAdImpression");
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            Log.d(TAG, "RewardedShowListener - onAdDismissedFullScreenContent");
        }

    }

    private static class RewardedEarnedListener implements OnUserEarnedRewardListener {

        @Override
        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            Log.d(TAG, "RewardedEarnedListener - onUserEarnedReward");
        }

    }

    /**
     * Class for definition behavior UnifiedNativeAd
     */
    private class NativeListener extends AdListener implements NativeAd.OnNativeAdLoadedListener {

        @Override
        public void onNativeAdLoaded(NativeAd nativeAd) {
            BidMachineAdMobFetchActivity.this.nativeAd = nativeAd;

            bShowNative.setEnabled(true);

            Log.d(TAG, "NativeListener - onNativeAdLoaded");
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "NativeLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            Log.d(TAG, "NativeListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobFetchActivity.this,
                           "NativeFailedToLoad",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdOpened() {
            Log.d(TAG, "NativeListener - onAdOpened");
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "NativeListener - onAdImpression");
        }

        @Override
        public void onAdClicked() {
            Log.d(TAG, "NativeListener - onAdClicked");
        }

    }

}