# HeaderBidding AdManager implementation

* [Home](../../../..)
* [Useful links](#useful-links)
* [Banner implementation](#banner-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [RewardedVideo implementation](#rewardedvideo-implementation)

## Useful links
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

    // Request BidMachine Ads without load it
    bannerRequest.request(this);
}

private void loadAdManagerBanner() {
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
                    loadBidMachineBanner();
                }
            });
        }
    });
    adManagerAdView.loadAd(adRequestBuilder.build());
}

private void loadBidMachineBanner() {
    // Create BannerView for load with previously loaded BannerRequest
    bidMachineBannerView = new BannerView(this);
    bidMachineBannerView.setListener(new BidMachineBannerListener());
    bidMachineBannerView.load(bannerRequest);
}

private void showBanner() {
    // Checking for can show before showing ads
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

    // Request BidMachine Ads without load it
    interstitialRequest.request(this);
}

private void loadAdManagerInterstitial() {
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
                                 new AdManagerInterstitialAdLoadCallback(){
                                     @Override
                                     public void onAdLoaded(@NonNull AdManagerInterstitialAd adManagerInterstitialAd) {
                                         // Checking whether it is BidMachine or not
                                         BidMachineUtils.isBidMachineInterstitial(adManagerInterstitialAd, isSuccess -> {
                                             if (isSuccess) {
                                                 loadBidMachineInterstitial();
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
    // Checking for can show before showing ads
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

    // Request BidMachine Ads without load it
    rewardedRequest.request(this);
}

private void loadAdManagerRewarded() {
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
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            // Checking whether it is BidMachine or not
                            if (BidMachineUtils.isBidMachineRewarded(rewardedAd)) {
                                loadBidMachineRewarded();
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
    // Checking for can show before showing ads
    if (bidMachineRewardedAd != null && bidMachineRewardedAd.canShow()) {
        bidMachineRewardedAd.show();
    }
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdManagerActivity.java#L364)