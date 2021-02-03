# HeaderBidding AdManager implementation

> AdManager implementation support starts from version 19.7.0

* [Useful links](#useful-links)
* [Banner implementation](#banner-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [RewardedVideo implementation](#rewardedvideo-implementation)
* [Utils](#utils)

## Useful links
* [AdManager documentation](https://developers.google.com/ad-manager/mobile-ads-sdk/android/quick-start)
* [AdManager integration](https://wiki.appodeal.com/display/BID/BidMachine+SDK+Google+AdManager+integration)

## Banner implementation
```java
private void loadBanner() {
    // Create new BidMachine request
    BannerRequest bannerRequest = new BannerRequest.Builder()
            .setSize(...)
            .setListener(new BannerRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadAdManagerBanner());
                }
            })
            .build();

    // Request an ad from BidMachine without loading it
    bannerRequest.request(this);
}

private void loadAdManagerBanner() {
    // Create AdManagerAdRequest builder
    AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();

    // Append BidMachine BannerRequest to AdManagerAdRequest
    BidMachineUtils.appendRequest(adRequestBuilder, bannerRequest);

    // Create new AdView instance and load it
    AdManagerAdView adManagerAdView = new AdManagerAdView(this);
    adManagerAdView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                               ViewGroup.LayoutParams.MATCH_PARENT));
    adManagerAdView.setAdUnitId(BANNER_ID);
    adManagerAdView.setAdSizes(AdSize.BANNER);
    adManagerAdView.setAdListener(new AdListener() {
        @Override
        public void onAdLoaded() {
            // Checking whether it is BidMachine or not
            BidMachineUtils.isBidMachineBanner(adManagerAdView, isSuccess -> {
                if (isSuccess) {
                    // If isSuccess is true, then BidMachine has won the mediation.
                    // Load BidMachine ad object, before show BidMachine ad
                    loadBidMachineBanner();
                } else {
                    // If isSuccess is false, then BidMachine has lost the mediation.
                    // No need to load BidMachine ad object.
                    // Process the OnAdLoaded callback in standard mode
                }
            });
        }
    });
    adManagerAdView.loadAd(adRequestBuilder.build());
}

private void loadBidMachineBanner() {
    // Create BannerView to load an ad from loaded BidMachine BannerRequest
    bidMachineBannerView = new BannerView(this);
    bidMachineBannerView.setListener(new BidMachineBannerListener());
    bidMachineBannerView.load(bannerRequest);
}

private void showBanner() {
    // Check if an ad can be shown before actual impression
    if (bidMachineBannerView != null && bidMachineBannerView.canShow()) {
        adContainer.removeAllViews();
        adContainer.addView(bidMachineBannerView);
    }
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdManagerActivity.java#L117)

## Interstitial implementation
```java
private void loadInterstitial() {
    // Create new BidMachine request
    InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
            .setAdContentType(...)
            .setListener(new InterstitialRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadAdManagerInterstitial());
                }
            })
            .build();

    // Request an ad from BidMachine without loading it
    interstitialRequest.request(this);
}

private void loadAdManagerInterstitial() {
    // Create AdManagerAdRequest builder
    AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();

    // Append BidMachine InterstitialRequest to AdManagerAdRequest
    BidMachineUtils.appendRequest(adRequestBuilder, interstitialRequest);

    // Load InterstitialAd
    AdManagerInterstitialAd.load(this,
                                 INTERSTITIAL_ID,
                                 adRequestBuilder.build(),
                                 new AdManagerInterstitialAdLoadCallback() {
                                     @Override
                                     public void onAdLoaded(@NonNull AdManagerInterstitialAd adManagerInterstitialAd) {
                                         // Checking whether it is BidMachine or not
                                         BidMachineUtils.isBidMachineInterstitial(adManagerInterstitialAd, isSuccess -> {
                                             if (isSuccess) {
                                                 // If isSuccess is true, then BidMachine has won the mediation.
                                                 // Load BidMachine ad object, before show BidMachine ad
                                                 loadBidMachineInterstitial();
                                             } else {
                                                 // If isSuccess is false, then BidMachine has lost the mediation.
                                                 // No need to load BidMachine ad object.
                                                 // Process the OnAdLoaded callback in standard mode
                                             }
                                         });
                                     }
                                 });
}

private void loadBidMachineInterstitial() {
    // Create InterstitialAd for load with previously loaded InterstitialRequest
    bidMachineInterstitialAd = new InterstitialAd(this);
    bidMachineInterstitialAd.setListener(new BidMachineInterstitialListener());
    bidMachineInterstitialAd.load(interstitialRequest);
}

private void showInterstitial() {
    // Check if an ad can be shown before actual impression
    if (bidMachineInterstitialAd != null && bidMachineInterstitialAd.canShow()) {
        bidMachineInterstitialAd.show();
    }
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdManagerActivity.java#L247)

## RewardedVideo implementation
```java
private void loadRewardedVideo() {
    // Create new BidMachine request
    RewardedRequest rewardedRequest = new RewardedRequest.Builder()
            .setListener(new RewardedRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadAdManagerRewarded());
                }
            })
            .build();

    // Request an ad from BidMachine without loading it
    rewardedRequest.request(this);
}

private void loadAdManagerRewarded() {
    // Create AdManagerAdRequest builder
    AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();

    // Append BidMachine RewardedRequest to AdManagerAdRequest
    BidMachineUtils.appendRequest(adRequestBuilder, rewardedRequest);

    // Load RewardedAd
    RewardedAd.load(this,
                    REWARDED_ID,
                    adRequestBuilder.build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            // Checking whether it is BidMachine or not
                            if (BidMachineUtils.isBidMachineRewarded(rewardedAd)) {
                                // If isSuccess is true, then BidMachine has won the mediation.
                                // Load BidMachine ad object, before show BidMachine ad
                                loadBidMachineRewarded();
                            } else {
                                // If isSuccess is false, then BidMachine has lost the mediation.
                                // No need to load BidMachine ad object.
                                // Process the OnAdLoaded callback in standard mode
                            }
                        }
                    });
}

private void loadBidMachineRewarded() {
    // Create RewardedAd for load with previously loaded RewardedRequest
    bidMachineRewardedAd = new io.bidmachine.rewarded.RewardedAd(this);
    bidMachineRewardedAd.setListener(new BidMachineRewardedListener());
    bidMachineRewardedAd.load(rewardedRequest);
}

private void showRewarded() {
    // Check if an ad can be shown before actual impression
    if (bidMachineRewardedAd != null && bidMachineRewardedAd.canShow()) {
        bidMachineRewardedAd.show();
    }
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdManagerActivity.java#L364)

## Utils
Ways to set up AdManagerAdRequest by BidMachine AdRequest:
1. Create new AdManagerAdRequest instance
```java
    AdManagerAdRequest adRequest = BidMachineUtils.createAdManagerRequest(bidMachineAdRequest);
```
2. Create new AdManagerAdRequest.Builder instance
```java
    AdManagerAdRequest.Builder adRequestBuilder = BidMachineUtils.createAdManagerRequestBuilder(bidMachineAdRequest);
```
3. Fill existing AdManagerAdRequest.Builder by BidMachine AdRequest
```java
    BidMachineUtils.appendRequest(adRequestBuilder, bidMachineAdRequest);
```