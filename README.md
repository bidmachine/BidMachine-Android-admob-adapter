# BidMachine Android AdMobAdapter
BidMachine Android adapter for AdMob mediation

[BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)

## Integration:
[<img src="https://img.shields.io/badge/SDK%20Version-1.5.1-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.5.1.5-brightgreen">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.admob/1.5.1.5/)
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
    implementation 'io.bidmachine:ads:1.5.1'
    //Add BidMachine SDK AdMob Adapter dependency
    implementation 'io.bidmachine:ads.adapters.admob:1.5.1.5'
    //Add AdMob SDK dependency
    implementation 'com.google.android.gms:play-services-ads:19.2.0'
    ...
}
```

## Examples:

#### Load Banner: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L246)
#### Load Interstitial: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L310)
#### Load Rewarded Video: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L366)
#### Load Native: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L422)


Parameter list for server configuretion:

| Key              | Definition | Value type |
|:---------------- |:-------------- |:---------- |
| seller_id        | Your unique seller id. To get your Seller Id or for more info please visit https://bidmachine.io/ | String |
| mediation_config | Your mediation config | JSONArray in String |
| coppa            | Flag indicating if COPPA regulations can be applied. The Children's Online Privacy Protection Act (COPPA) was established by the U.S. Federal Trade Commission. | String |
| logging_enabled  | Enable logs if required | String |
| test_mode        | Enable test mode | String |
| subject_to_gdpr  | Flag indicating if GDPR regulations can be applied. The General Data Protection Regulation (GDPR) is a regulation of the European Union. | String |
| has_consent      | User has given consent to the processing of personal data relating to him or her. https://www.eugdpr.org/ | String |
| consent_string   | GDPR consent string if applicable, complying with the comply with the IAB standard <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework/blob/master/Consent%20string%20and%20vendor%20list%20formats%20v1.1%20Final.md">Consent String Format</a> in the <a href="https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework">Transparency and Consent Framework</a> technical specifications | String |
| endpoint         | Your custom endpoint | String |
| ad_content_type  | Content type for interstitial ad, one of following: "All", "Static", "Video"   | String              |
| user_id          | Vendor-specific ID for the user                                                | String              |
| gender           | Gender, one of following: "F", "M", "O"                                        | String              |
| yob              | Year of birth as a 4-digit integer (e.g - 1990)                                | String              |
| keywords         | List of keywords, interests, or intents (separated by comma)                   | String              |
| country          | Country of the user's home base (i.e., not necessarily their current location) | String              |
| city             | City of the user's home base (i.e., not necessarily their current location)    | String              |
| zip              | Zip of the user's home base (i.e., not necessarily their current location)     | String              |
| sturl            | App store URL for an installed app; for IQG 2.1 compliance                     | String              |
| paid             | Determines, if it is a free or paid version of the app                         | String              |
| bcat             | Block list of content categories using IDs (separated by comma)                | String              |
| badv             | Block list of advertisers by their domains (separated by comma)                | String              |
| bapps            | Block list of apps where ads are disallowed (separated by comma)               | String              |
| price_floors     | List of price floor                                                            | JSONArray in String |

Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG", //JSONArray in String
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

Local SDK configuration sample:
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
        .setMediationConfig("YOUR_MEDIATION_CONFIG") //JSONArray in String
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setAdContentType(AdContentType.All)
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
```

Local Banner configuration sample:
```java
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
```

Local Interstitial configuration sample:
```java
//Set bundle to custom event interstitial
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class, bundle)
        .build();

//Create new InterstitialAd instance and load
interstitialAd = new InterstitialAd(this);
interstitialAd.setAdUnitId(INTERSTITIAL_ID);
interstitialAd.setAdListener(new InterstitialListener());
interstitialAd.loadAd(adRequest);
```

Local RewardedVideo configuration sample:
```java
//Set bundle to mediation rewarded video ad adapter
AdRequest adRequest = new AdRequest.Builder()
        .addNetworkExtrasBundle(BidMachineMediationRewardedAdAdapter.class, bundle)
        .build();

//Create new RewardedVideoAd instance and load
rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoListener());
rewardedVideoAd.loadAd(REWARDED_ID, adRequest);
```

Local Native configuration sample:
```java
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

## What's new in this version

Please view the [changelog](CHANGELOG.md) for details.