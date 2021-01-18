# BidMachine Android AdMobAdapter

[<img src="https://img.shields.io/badge/SDK%20Version-1.6.3-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.6.3.9-brightgreen">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.admob/1.6.3.9/)
[<img src="https://img.shields.io/badge/AdMob%20Version-19.5.0-blue">](https://developers.google.com/admob/android/quick-start)

* [Useful links](#useful-links)
* [Integration](#integration)
* [Classic implementation](#classic-implementation)
  * [Banner implementation](#banner-implementation)
  * [Interstitial implementation](#interstitial-implementation)
  * [RewardedVideo implementation](#rewardedvideo-implementation)
  * [Native implementation](#native-implementation)
* [HeaderBidding implementation](#headerbidding-implementation)
  * [Banner implementation](#banner-implementation-1)
  * [Interstitial implementation](#interstitial-implementation-1)
  * [RewardedVideo implementation](#rewardedvideo-implementation-1)
  * [Native implementation](#native-implementation-1)
  * [Work with price](#work-with-price)
* [What's new in last version](whats-new-in-last-version)

## Useful links
* [BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)
* [BidMachine AdMob custom network integration guide](https://wiki.appodeal.com/display/BID/BidMachine+AdMob+custom+network+integration+guide)

## Integration
```gradle
repositories {
    //Add BidMachine maven repository
    maven {
        name 'BidMachine Ads maven repository'
        url 'https://artifactory.bidmachine.io/bidmachine'
    }
}

dependencies {
    //Add BidMachine SDK dependency
    implementation 'io.bidmachine:ads:1.6.3'
    //Add BidMachine SDK AdMob Adapter dependency
    implementation 'io.bidmachine:ads.adapters.admob:1.6.3.9'
    //Add AdMob SDK dependency
    implementation 'com.google.android.gms:play-services-ads:19.5.0'
    ...
}
```

## Classic implementation
### Banner implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .build();

//Set bundle to custom event banner
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventBanner.class, bundle)
        .build();

//Create new AdView instance and load
AdView adView = new AdView(this);
adView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
adView.setAdUnitId(BANNER_ID);
adView.setAdSize(AdSize.BANNER);
adView.setAdListener(new BannerViewListener());
adView.loadAd(adRequest);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L98)

### Interstitial implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "ad_content_type": "All",
    "user_id": "YOUR_USER_ID",
    "gender": "M",
    "yob": "1990",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .build();

//Set bundle to custom event interstitial
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class, bundle)
        .build();

//Create new InterstitialAd instance and load
InterstitialAd interstitialAd = new InterstitialAd(this);
interstitialAd.setAdUnitId(INTERSTITIAL_ID);
interstitialAd.setAdListener(new InterstitialListener());
interstitialAd.loadAd(adRequest);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L163)

### RewardedVideo implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .build();

//Set bundle to mediation rewarded video ad adapter
AdRequest adRequest = new AdRequest.Builder()
        .addNetworkExtrasBundle(BidMachineMediationRewardedAdAdapter.class, bundle)
        .build();

//Create new RewardedVideoAd instance and load
RewardedVideoAd rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoListener());
rewardedVideoAd.loadAd(REWARDED_ID, adRequest);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L220)

### Native implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ]
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .build();

//Set bundle to mediation native ad adapter
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventNative.class, bundle)
        .build();

//Create new AdLoader instance and load
AdLoader adLoader = new AdLoader.Builder(this, NATIVE_ID)
        .forUnifiedNativeAd(nativeListener)
        .withAdListener(nativeListener)
        .build();
adLoader.loadAd(adRequest);
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L277)


## HeaderBidding implementation
### Banner implementation
```java
private void loadBanner() {
    //Create new BidMachine request
    BannerRequest bannerRequest = new BannerRequest.Builder()
            .setSize(...)
            .setTargetingParams(...)
            .setPriceFloorParams(...)
            .setListener(new BannerRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull BannerRequest bannerRequest,
                                             @NonNull AuctionResult auctionResult) {
                    // If you want, change the price rounding logic before BidMachineFetcher.fetch
                    // more info in "Work with price"

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
    AdView adView = new AdView(this);
    adView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    adView.setAdUnitId(BANNER_ID);
    adView.setAdSize(AdSize.BANNER);
    adView.setAdListener(new BannerViewListener());

    //Load AdMob Ads
    adView.loadAd(adRequest);
}
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L133)

### Interstitial implementation
```java
private void loadInterstitial() {
    //Create new BidMachine request
    InterstitialRequest interstitialRequest = new InterstitialRequest.Builder()
            .setListener(new InterstitialRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull InterstitialRequest interstitialRequest,
                                             @NonNull AuctionResult auctionResult) {
                    // If you want, change the price rounding logic before BidMachineFetcher.fetch
                    // more info in "Work with price"

                    // Fetch BidMachine Ads
                    Map<String, String> fetchParams = BidMachineFetcher.fetch(interstitialRequest);
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
                public void onRequestFailed(@NonNull InterstitialRequest interstitialRequest,
                                            @NonNull BMError bmError) {
                    runOnUiThread(() -> Toast.makeText(
                            BidMachineAdMobFetchActivity.this,
                            "InterstitialFetchFailed",
                            Toast.LENGTH_SHORT).show());
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
    InterstitialAd interstitialAd = new InterstitialAd(this);
    interstitialAd.setAdUnitId(INTERSTITIAL_ID);
    interstitialAd.setAdListener(new InterstitialListener());

    //Load AdMob Ads
    interstitialAd.loadAd(adRequest);
}
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L237)

### RewardedVideo implementation
```java
private void loadRewardedVideo() {
    //Create new BidMachine request
    RewardedRequest rewardedRequest = new RewardedRequest.Builder()
            .setListener(new RewardedRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull RewardedRequest rewardedRequest,
                                             @NonNull AuctionResult auctionResult) {
                    // If you want, change the price rounding logic before BidMachineFetcher.fetch
                    // more info in "Work with price"

                    // Fetch BidMachine Ads
                    Map<String, String> fetchParams = BidMachineFetcher.fetch(rewardedRequest);
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
                public void onRequestFailed(@NonNull RewardedRequest rewardedRequest,
                                            @NonNull BMError bmError) {
                    runOnUiThread(() -> Toast.makeText(
                            BidMachineAdMobFetchActivity.this,
                            "RewardedFetchFailed",
                            Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onRequestExpired(@NonNull RewardedRequest rewardedRequest) {
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
    RewardedVideoAd rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
    rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoListener());

    //Load AdMob Ads
    rewardedVideoAd.loadAd(REWARDED_ID, adRequest);
}
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L332)

### Native implementation
```java
private void loadNative() {
    //Create new BidMachine request
    NativeRequest nativeRequest = new NativeRequest.Builder()
            .setListener(new NativeRequest.AdRequestListener() {
                @Override
                public void onRequestSuccess(@NonNull NativeRequest nativeRequest,
                                             @NonNull AuctionResult auctionResult) {
                    // If you want, change the price rounding logic before BidMachineFetcher.fetch
                    // more info in "Work with price"

                    // Fetch BidMachine Ads
                    Map<String, String> fetchParams = BidMachineFetcher.fetch(nativeRequest);
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
                public void onRequestFailed(@NonNull NativeRequest nativeRequest,
                                            @NonNull BMError bmError) {
                    runOnUiThread(() -> Toast.makeText(
                            BidMachineAdMobFetchActivity.this,
                            "NativeFetchFailed",
                            Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onRequestExpired(@NonNull NativeRequest nativeRequest) {
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
```
[*Example*](example/src/main/java/io/bidmachine/examples/BidMachineAdMobFetchActivity.java#L427)

### Work with price
When **BidMachineFetcher.fetch(...)** is called, price rounding occurs. By default, RoundingMode is **RoundingMode.CEILING**, but if you want specific RoundingMode, you can change it with help **BidMachineFetcher.setPriceRounding(...)**. You can try your rounding configuration via call **BidMachineFetcher.roundPrice(...)**. More info about RoundingMode [here](https://developer.android.com/reference/java/math/RoundingMode).

**Attention**:  RoundingMode.UNNECESSARY is not supported.

Price rounding examples:

| Round mode | Result |
| ---------- | ------ |
| BidMachineFetcher.setPriceRounding(0.01) | 0.01 -> 0.01 <br> 0.99 -> 0.99 <br> 1.212323 -> 1.22 <br> 1.34538483 -> 1.35 <br> 1.4 -> 1.40 <br> 1.58538483 -> 1.59 |
| BidMachineFetcher.setPriceRounding(0.1) | 0.01 -> 0.1 <br> 0.99 -> 1.0 <br> 1.212323 -> 1.3 <br> 1.34538483 -> 1.4 <br> 1.4 -> 1.4 <br> 1.58538483 -> 1.6 |
| BidMachineFetcher.setPriceRounding(0.01, RoundingMode.FLOOR) | 0.01 -> 0.01 <br> 0.99 -> 0.99 <br> 1.212323 -> 1.21 <br> 1.34538483 -> 1.34 <br> 1.4 -> 1.40 <br> 1.58538483 -> 1.58 |

## What's new in this version

Please view the [changelog](CHANGELOG.md) for details.