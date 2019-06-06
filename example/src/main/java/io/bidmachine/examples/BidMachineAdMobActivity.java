package io.bidmachine.examples;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.ads.mediation.bidmachine.BidMachineBundleBuilder;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventBanner;
import com.google.ads.mediation.bidmachine.BidMachineCustomEventInterstitial;
import com.google.ads.mediation.bidmachine.BidMachineMediationRewardedAdAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import io.bidmachine.AdContentType;

public class BidMachineAdMobActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String BANNER_ID = "ca-app-pub-1405929557079197/8614249475";
    private static final String INTERSTITIAL_ID = "ca-app-pub-1405929557079197/1600418448";
    private static final String REWARDED_ID = "ca-app-pub-1405929557079197/6263418305";

    private FrameLayout bannerContainer;

    private AdView adView;
    private InterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerContainer = findViewById(R.id.banner_container);
        findViewById(R.id.load_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBanner();
            }
        });
        findViewById(R.id.show_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBanner();
            }
        });
        findViewById(R.id.load_interstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitial();
            }
        });
        findViewById(R.id.show_interstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });
        findViewById(R.id.load_rvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardedVideo();
            }
        });
        findViewById(R.id.show_rvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedVideo();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyBanner();
        destroyInterstitial();
    }

    /**
     * Method for load banner from AdMob
     */
    private void loadBanner() {
        //Destroy previous AdView
        destroyBanner();

        Log.d(TAG, "AdMob loadBanner");

        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId("1")
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
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
        adView.loadAd(adRequest);
    }

    /**
     * Method for show banner from AdMob
     */
    private void showBanner() {
        if (adView != null && adView.getParent() == null) {
            Log.d(TAG, "AdView showBanner");

            //Add AdView for show
            bannerContainer.addView(adView);
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

            bannerContainer.removeAllViews();
            adView.setAdListener(null);
            adView.destroy();
            adView = null;
        }
    }

    /**
     * Method for load interstitial from AdMob
     */
    private void loadInterstitial() {
        //Destroy previous InterstitialAd
        destroyInterstitial();

        Log.d(TAG, "InterstitialAd loadInterstitial");

        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId("1")
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .setAdContentType(AdContentType.All)
                .build();

        //Set bundle to custom event interstitial
        AdRequest adRequest = new AdRequest.Builder()
                .addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class, bundle)
                .build();

        //Create new InterstitialAd instance and load
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_ID);
        interstitialAd.setAdListener(new InterstitialListener());
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
        //Destroy previous RewardedVideoAd
        destroyRewardedVideo();

        Log.d(TAG, "RewardedVideoAd loadRewardedVideo");

        //Prepare bundle for set to AdRequest
        Bundle bundle = new BidMachineBundleBuilder()
                .setSellerId("1")
                .setCoppa(false)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .build();

        //Set bundle to mediation rewarded video ad adapter
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(BidMachineMediationRewardedAdAdapter.class, bundle)
                .build();

        //Create new RewardedVideoAd instance and load
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoListener());
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
     * Method for destroy InterstitialAd
     */
    private void destroyRewardedVideo() {
        if (rewardedVideoAd != null) {
            Log.d(TAG, "InterstitialAd destroyInterstitial");

            rewardedVideoAd.setRewardedVideoAdListener(null);
            rewardedVideoAd.destroy(this);
            rewardedVideoAd = null;
        }
    }

    /**
     * Class for definition behavior AdView
     */
    private class BannerViewListener extends AdListener {

        @Override
        public void onAdLoaded() {
            Log.d(TAG, "AdView onBannerLoaded");
            Toast.makeText(
                    BidMachineAdMobActivity.this,
                    "BannerLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "AdView onBannerFailedToLoad with errorCode - " + i + ")");
            Toast.makeText(
                    BidMachineAdMobActivity.this,
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
            Log.d(TAG, "InterstitialAd onInterstitialLoaded");
            Toast.makeText(
                    BidMachineAdMobActivity.this,
                    "InterstitialLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAdFailedToLoad(int i) {
            Log.d(TAG, "InterstitialAd onInterstitialFailedToLoad with errorCode - " + i + ")");
            Toast.makeText(
                    BidMachineAdMobActivity.this,
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
            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdLoaded");
            Toast.makeText(
                    BidMachineAdMobActivity.this,
                    "RewardedVideoAdLoaded",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Log.d(TAG, "RewardedVideoAd onRewardedVideoAdFailedToLoad with errorCode - " + i + ")");
            Toast.makeText(
                    BidMachineAdMobActivity.this,
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

}