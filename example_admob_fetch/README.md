# HeaderBidding AdMob implementation

* [Useful links](#useful-links)
* [Banner implementation](#banner-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [RewardedVideo implementation](#rewardedvideo-implementation)
* [Native implementation](#native-implementation)

## Useful links
* [AdMob documentation](https://developers.google.com/admob/android/quick-start)
* [AdMob integration](https://wiki.appodeal.com/display/BID/BidMachine+AdMob+custom+network+integration+guide)

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
                    runOnUiThread(() -> loadAdMobBanner(bannerRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    bannerRequest.request(this);
}

private void loadAdMobBanner(@NonNull BannerRequest bannerRequest) {
    // Create AdRequest
    AdRequest adRequest = BidMachineUtils.createAdRequest(bannerRequest);

    // Create new AdView instance and load
    AdView adView = new AdView(this);
    adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                      ViewGroup.LayoutParams.MATCH_PARENT));
    adView.setAdUnitId(BANNER_ID);
    adView.setAdSize(AdSize.BANNER);
    adView.setAdListener(new BannerViewListener());
    adView.loadAd(adRequest);
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L138)

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
                    runOnUiThread(() -> loadAdMobInterstitial(interstitialRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    interstitialRequest.request(this);
}

private void loadAdMobInterstitial(@NonNull InterstitialRequest interstitialRequest) {
    // Create AdRequest
    AdRequest adRequest = BidMachineUtils.createAdRequest(interstitialRequest);

    // Load InterstitialAd
    InterstitialAd.load(this, INTERSTITIAL_ID, adRequest, new InterstitialLoadListener());
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L241)

## RewardedVideo implementation
```java
private void loadRewardedVideo() {
    // Create new BidMachine request
    RewardedRequest rewardedRequest = new RewardedRequest.Builder()
            .setListener(new RewardedRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadAdMobRewarded(rewardedRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    rewardedRequest.request(this);
}

private void loadAdMobRewarded(@NonNull RewardedRequest rewardedRequest) {
    // Create AdRequest
    AdRequest adRequest = BidMachineUtils.createAdRequest(rewardedRequest);

    // Load RewardedAd
    RewardedAd.load(this, REWARDED_ID, adRequest, new RewardedLoadListener());
}
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L336)

## Native implementation
```java
private void loadNative() {
    // Create new BidMachine request
    NativeRequest nativeRequest = new NativeRequest.Builder()
            .setListener(new NativeRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                             @NonNull AuctionResult auctionResult) {
                    runOnUiThread(() -> loadAdMobNative(nativeRequest));
                }
            })
            .build();

    // Request BidMachine Ads without load it
    nativeRequest.request(this);
}

private void loadAdMobNative(@NonNull NativeRequest nativeRequest) {
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
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L431)