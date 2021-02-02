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

import com.google.ads.mediation.bidmachine.BidMachineAdapter;
import com.google.ads.mediation.bidmachine.BidMachineBundleBuilder;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventBanner;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventInterstitial;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventNative;
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

import io.bidmachine.AdContentType;

public class BidMachineAdMobActivity extends Activity {

    private static final String TAG = BidMachineAdMobActivity.class.getSimpleName();
    private static final String BID_MACHINE_SELLER_ID = "5";
    private static final String BANNER_ID = "YOUR_BANNER_ID";
    private static final String INTERSTITIAL_ID = "YOUR_INTERSTITIAL_ID";
    private static final String REWARDED_ID = "YOUR_REWARDED_ID";
    private static final String NATIVE_ID = "YOUR_NATIVE_ID";

    private Button bShowBanner;
    private Button bShowInterstitial;
    private Button bShowRewarded;
    private Button bShowNative;
    private FrameLayout adContainer;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private NativeAd nativeAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob);

        findViewById(R.id.bLoadBanner).setOnClickListener(v -> loadBanner());
        bShowBanner = findViewById(R.id.bShowBanner);
        bShowBanner.setOnClickListener(v -> showBanner());

        findViewById(R.id.bLoadInterstitial).setOnClickListener(v -> loadInterstitial());
        bShowInterstitial = findViewById(R.id.bShowInterstitial);
        bShowInterstitial.setOnClickListener(v -> showInterstitial());

        findViewById(R.id.bLoadRewarded).setOnClickListener(v -> loadRewarded());
        bShowRewarded = findViewById(R.id.bShowRewarded);
        bShowRewarded.setOnClickListener(v -> showRewarded());

        findViewById(R.id.bLoadNative).setOnClickListener(v -> loadNative());
        bShowNative = findViewById(R.id.bShowNative);
        bShowNative.setOnClickListener(v -> showNative());

        adContainer = findViewById(R.id.adContainer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyBanner();
        destroyInterstitial();
        destroyRewarded();
        destroyNative();
    }

    private void addAdView(View view) {
        adContainer.removeAllViews();
        adContainer.addView(view);
    }

    /**
     * Method for load AdView
     */
    private void loadBanner() {
        bShowBanner.setEnabled(false);

        // Destroy previous AdView
        destroyBanner();

        // Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId(BID_MACHINE_SELLER_ID)
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .build();

        // Set bundle to custom event banner
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventBanner.class, bundle)
                .build();

        // Create new AdView instance and load
        adView = new AdView(this);
        adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                          ViewGroup.LayoutParams.MATCH_PARENT));
        adView.setAdUnitId(BANNER_ID);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdListener(new BannerViewListener());
        adView.loadAd(adRequest);

        Log.d(TAG, "loadBanner");
    }

    /**
     * Method for show AdView
     */
    private void showBanner() {
        Log.d(TAG, "showBanner");

        bShowBanner.setEnabled(false);

        if (adView != null && adView.getParent() == null) {
            addAdView(adView);
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
        if (adView != null) {
            adView.setAdListener(null);
            adView.destroy();
            adView = null;
        }
    }

    /**
     * Method for load InterstitialAd
     */
    private void loadInterstitial() {
        bShowInterstitial.setEnabled(false);

        // Destroy previous InterstitialAd
        destroyInterstitial();

        // Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId(BID_MACHINE_SELLER_ID)
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .setAdContentType(AdContentType.All)
                .build();

        // Set bundle to custom event interstitial
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class, bundle)
                .build();

        // Load InterstitialAd
        InterstitialAd.load(this, INTERSTITIAL_ID, adRequest, new InterstitialLoadListener());

        Log.d(TAG, "loadInterstitial");
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
     * Method for load RewardedAd
     */
    private void loadRewarded() {
        bShowRewarded.setEnabled(false);

        // Destroy previous RewardedAd
        destroyRewarded();

        // Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId(BID_MACHINE_SELLER_ID)
                .setCoppa(false)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .build();

        // Set bundle to mediation rewarded ad adapter
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(BidMachineAdapter.class, bundle)
                .build();

        // Load RewardedAd
        RewardedAd.load(this, REWARDED_ID, adRequest, new RewardedLoadListener());

        Log.d(TAG, "loadRewarded");
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
     * Method for load NativeAd
     */
    private void loadNative() {
        bShowNative.setEnabled(false);

        // Destroy previous NativeAd
        destroyNative();

        // Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId(BID_MACHINE_SELLER_ID)
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .build();

        // Set bundle to mediation native ad adapter
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventNative.class, bundle)
                .build();

        // Create new AdLoader instance and load
        NativeListener nativeListener = new NativeListener();
        AdLoader adLoader = new AdLoader.Builder(this, NATIVE_ID)
                .forNativeAd(nativeListener)
                .withAdListener(nativeListener)
                .build();
        adLoader.loadAd(adRequest);

        Log.d(TAG, "loadNative");
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
            Toast.makeText(BidMachineAdMobActivity.this,
                           "BannerLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            Log.d(TAG, "BannerViewListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobActivity.this,
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
     * Class for definition behavior InterstitialAd
     */
    private class InterstitialLoadListener extends InterstitialAdLoadCallback {

        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            BidMachineAdMobActivity.this.interstitialAd = interstitialAd;

            bShowInterstitial.setEnabled(true);

            Log.d(TAG, "InterstitialLoadListener - onAdLoaded");
            Toast.makeText(BidMachineAdMobActivity.this,
                           "InterstitialLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.d(TAG, "InterstitialLoadListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobActivity.this,
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
            BidMachineAdMobActivity.this.rewardedAd = rewardedAd;

            bShowRewarded.setEnabled(true);

            Log.d(TAG, "RewardedLoadListener - onAdLoaded");
            Toast.makeText(BidMachineAdMobActivity.this,
                           "RewardedLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.d(TAG, "RewardedLoadListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobActivity.this,
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
            BidMachineAdMobActivity.this.nativeAd = nativeAd;

            bShowNative.setEnabled(true);

            Log.d(TAG, "NativeListener - onNativeAdLoaded");
            Toast.makeText(BidMachineAdMobActivity.this,
                           "NativeLoaded",
                           Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            Log.d(TAG, "NativeListener - onAdFailedToLoad with message: "
                    + loadAdError.getMessage());
            Toast.makeText(BidMachineAdMobActivity.this,
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