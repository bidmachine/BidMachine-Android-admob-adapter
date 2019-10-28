package io.bidmachine.examples.admanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.ads.mediation.bidmachine.BidMachineBundleBuilder;
import com.google.ads.mediation.bidmachine.BidMachineUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import io.bidmachine.AdContentType;
import io.bidmachine.AdRequest;
import io.bidmachine.AdsType;
import io.bidmachine.BidMachine;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.InitializationCallback;
import io.bidmachine.PriceFloorParams;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineAdMobActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static final String BANNER_ID = "/6499/example/banner";
    private static final String INTERSTITIAL_ID = "/6499/example/interstitial";
    private static final String REWARDED_ID = "/6499/example/rewarded-video";

    private FrameLayout bannerContainer;

    private PublisherAdView adView;
    private PublisherInterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;
    private JSONArray mediationConfig;

    private Button btnLoadBanner;
    private Button btnShowBanner;
    private Button btnLoadInterstitial;
    private Button btnShowInterstitial;
    private Button btnLoadRewardedVideo;
    private Button btnShowRewardedVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerContainer = findViewById(R.id.banner_container);
        findViewById(R.id.initialize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initialize();
            }
        });

        btnLoadBanner = findViewById(R.id.load_banner);
        btnLoadBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBanner();
            }
        });
        btnShowBanner = findViewById(R.id.show_banner);
        btnShowBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBanner();
            }
        });
        btnLoadInterstitial = findViewById(R.id.load_interstitial);
        btnLoadInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInterstitial();
            }
        });
        btnShowInterstitial = findViewById(R.id.show_interstitial);
        btnShowInterstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitial();
            }
        });
        btnLoadRewardedVideo = findViewById(R.id.load_rvideo);
        btnLoadRewardedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardedVideo();
            }
        });
        btnShowRewardedVideo = findViewById(R.id.show_rvideo);
        btnShowRewardedVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedVideo();
            }
        });

        setButtonsEnabled(BidMachine.isInitialized());

        try {
            JSONObject myTargetJSON = new JSONObject(""
                                                             +
                                                             "{"
                                                             +
                                                             "  \"network\": \"my_target\","
                                                             +
                                                             "  \"ad_units\": [{"
                                                             +
                                                             "    \"format\": \"banner\","
                                                             +
                                                             "    \"slot_id\": \"437933\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"banner_320x50\","
                                                             +
                                                             "    \"slot_id\": \"437933\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"banner_300x250\","
                                                             +
                                                             "    \"slot_id\": \"64526\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"banner_728x90\","
                                                             +
                                                             "    \"slot_id\": \"81620\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"interstitial_static\","
                                                             +
                                                             "    \"slot_id\": \"365991\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"rewarded_video\","
                                                             +
                                                             "    \"slot_id\": \"482205\""
                                                             +
                                                             "  }]"
                                                             +
                                                             "}");
            JSONObject adColonyJSON = new JSONObject(""
                                                             +
                                                             "{"
                                                             +
                                                             "  \"network\": \"adcolony\","
                                                             +
                                                             "  \"network_config\": {"
                                                             +
                                                             "    \"app_id\": \"app185a7e71e1714831a49ec7\""
                                                             +
                                                             "  },"
                                                             +
                                                             "  \"ad_units\": [{"
                                                             +
                                                             "    \"format\": \"interstitial_video\","
                                                             +
                                                             "    \"app_id\": \"app185a7e71e1714831a49ec7\","
                                                             +
                                                             "    \"zone_id\": \"vz06e8c32a037749699e7050\","
                                                             +
                                                             "    \"store_id\": \"google\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"rewarded_video\","
                                                             +
                                                             "    \"app_id\": \"app185a7e71e1714831a49ec7\","
                                                             +
                                                             "    \"zone_id\": \"vz1fd5a8b2bf6841a0a4b826\","
                                                             +
                                                             "    \"store_id\": \"google\""
                                                             +
                                                             "  }]"
                                                             +
                                                             "}");
            JSONObject facebookJSON = new JSONObject(""
                                                             +
                                                             "{"
                                                             +
                                                             "  \"network\": \"facebook\","
                                                             +
                                                             "  \"app_id\": \"1525692904128549\","
                                                             +
                                                             "  \"ad_units\": [{"
                                                             +
                                                             "    \"format\": \"banner\","
                                                             +
                                                             "    \"facebook_key\": \"1525692904128549_2386746951356469\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"banner_320x50\","
                                                             +
                                                             "    \"facebook_key\": \"1525692904128549_2386746951356469\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"banner_300x250\","
                                                             +
                                                             "    \"facebook_key\": \"1525692904128549_2386746951356469\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"interstitial_static\","
                                                             +
                                                             "    \"facebook_key\": \"1525692904128549_2386743441356820\""
                                                             +
                                                             "  }, {"
                                                             +
                                                             "    \"format\": \"rewarded_video\","
                                                             +
                                                             "    \"facebook_key\": \"1525692904128549_2386753464689151\""
                                                             +
                                                             "  }]"
                                                             +
                                                             "}");
            JSONObject tapjoyJSON = new JSONObject(""
                                                           +
                                                           "{"
                                                           +
                                                           "  \"network\": \"tapjoy\","
                                                           +
                                                           "  \"sdk_key\": \"tmyN5ZcXTMyjeJNJmUD5ggECAbnEGtJREmLDd0fvqKBXcIr7e1dvboNKZI4y\","
                                                           +
                                                           "  \"ad_units\": [{"
                                                           +
                                                           "    \"format\": \"interstitial_video\","
                                                           +
                                                           "    \"sdk_key\": \"tmyN5ZcXTMyjeJNJmUD5ggECAbnEGtJREmLDd0fvqKBXcIr7e1dvboNKZI4y\","
                                                           +
                                                           "    \"placement_name\": \"video_without_cap_pb\""
                                                           +
                                                           "  }, {"
                                                           +
                                                           "    \"format\": \"rewarded_video\","
                                                           +
                                                           "    \"sdk_key\": \"tmyN5ZcXTMyjeJNJmUD5ggECAbnEGtJREmLDd0fvqKBXcIr7e1dvboNKZI4y\","
                                                           +
                                                           "    \"placement_name\": \"rewarded_video_without_cap_pb\""
                                                           +
                                                           "  }]"
                                                           +
                                                           "}");
            mediationConfig = new JSONArray();
            mediationConfig.put(myTargetJSON);
            mediationConfig.put(adColonyJSON);
            mediationConfig.put(facebookJSON);
            mediationConfig.put(tapjoyJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyBanner();
        destroyInterstitial();
    }

    private void initialize() {
        //Initialize BidMachine SDK first
        BidMachine.setTestMode(true);
        BidMachine.setLoggingEnabled(true);
//        BidMachine.registerNetworks(mediationConfig.toString());
        BidMachine.initialize(this, "1", new InitializationCallback() {
            @Override
            public void onInitialized() {
                setButtonsEnabled(true);
            }
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnLoadBanner.setEnabled(enabled);
        btnShowBanner.setEnabled(enabled);
        btnLoadInterstitial.setEnabled(enabled);
        btnShowInterstitial.setEnabled(enabled);
        btnLoadRewardedVideo.setEnabled(enabled);
        btnShowRewardedVideo.setEnabled(enabled);
    }

    /**
     * Method for load banner from AdMob
     */
    private void loadBanner() {
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
                        final Map<String, String> fetchParams =
                                BidMachineFetcher.fetch(bannerRequest);
                        if (fetchParams != null) {
                            //Request callbacks run in background thread, but you should call AdMob load methods on UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadAdManagerBanner(fetchParams);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            BidMachineAdMobActivity.this,
                                            "BannerFetchFailed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull BannerRequest bannerRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        BidMachineAdMobActivity.this,
                                        "BannerFetchFailed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void loadAdManagerBanner(@NonNull Map<String, String> fetchParams) {
        //Prepare bundle for set to AdRequest
        BidMachineBundleBuilder bundle = new BidMachineBundleBuilder()
                .setSellerId("1")
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .setMediationConfig(mediationConfig)
                //Set fetching parameters
                .setFetchParams(fetchParams);

        PublisherAdRequest adRequest = BidMachineUtils
                .createPublisherAdRequest(AdsType.Banner, bundle)
                .build();

        //Create new AdView instance and load
        adView = new PublisherAdView(this);
        adView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        adView.setAdUnitId(BANNER_ID);
        adView.setAdSizes(AdSize.BANNER);
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

        InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
                .setListener(new AdRequest.AdRequestListener<InterstitialRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        // Fetch BidMachine Ads
                        final Map<String, String> fetchParams =
                                BidMachineFetcher.fetch(interstitialRequest);
                        if (fetchParams != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadAdManagerInterstitial(fetchParams);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            BidMachineAdMobActivity.this,
                                            "InterstitialFetchFailed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull InterstitialRequest interstitialRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        BidMachineAdMobActivity.this,
                                        "InterstitialFetchFailed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull InterstitialRequest interstitialRequest) {
                        //ignore
                    }
                })
                .build();

        //Request BidMachine Ads without load it
        interstitialRequest.request(this);
    }

    private void loadAdManagerInterstitial(@NonNull Map<String, String> fetchParams) {
        //Prepare bundle for set to AdRequest
        BidMachineBundleBuilder bundle = new BidMachineBundleBuilder()
                .setSellerId("1")
                .setCoppa(true)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .setAdContentType(AdContentType.All)
                .setMediationConfig(mediationConfig)
                .setFetchParams(fetchParams);

        //Set bundle to custom event interstitial
        PublisherAdRequest adRequest = BidMachineUtils
                .createPublisherAdRequest(AdsType.Interstitial, bundle)
                .build();

        //Create new InterstitialAd instance and load
        interstitialAd = new PublisherInterstitialAd(this);
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

        RewardedRequest request = new RewardedRequest.Builder()
                .setListener(new AdRequest.AdRequestListener<RewardedRequest>() {
                    @Override
                    public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                                 @NonNull AuctionResult auctionResult) {
                        //Fetch BidMachine Ads
                        final Map<String, String> fetchParams = BidMachineFetcher.fetch(
                                rewardedRequest);
                        if (fetchParams != null) {
                            //Request callbacks run in background thread, but you should call MoPub load methods on UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadAdManagerRewardedVideo(fetchParams);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(
                                            BidMachineAdMobActivity.this,
                                            "RewardedFetchFailed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onRequestFailed(@NonNull RewardedRequest rewardedRequest,
                                                @NonNull BMError bmError) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        BidMachineAdMobActivity.this,
                                        "RewardedFetchFailed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onRequestExpired(@NonNull RewardedRequest rewardedRequest) {
                        //ignore
                    }
                })
                .build();

        //Request BidMachine Ads without load it
        request.request(this);
    }

    private void loadAdManagerRewardedVideo(@NonNull Map<String, ?> fetchParams) {
        //Prepare bundle for set to AdRequest
        BidMachineBundleBuilder bundle = new BidMachineBundleBuilder()
                .setSellerId("1")
                .setCoppa(false)
                .setLoggingEnabled(true)
                .setTestMode(true)
                .setMediationConfig(mediationConfig)
                .setFetchParams(fetchParams);

        //Set bundle to mediation rewarded video ad adapter
        PublisherAdRequest adRequest = BidMachineUtils
                .createPublisherAdRequest(AdsType.Rewarded, bundle)
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