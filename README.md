# BidMachine-Android-admob-adapter
BidMachine Android adapter for AdMob mediation

## Integration:
[<img src="https://appodeal-android.s3-us-west-1.amazonaws.com/publishing/bm_130_git_version_badge.svg">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://appodeal-android.s3-us-west-1.amazonaws.com/publishing/bm_mopub_1302_git_version_badge.svg">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads-mopub/1.3.0.2/)
```gradle
repositories {
    //Add BidMachine maven repository
    maven {
        url 'https://artifactory.bidmachine.io/bidmachine'
    }
}

dependencies {
    //Add BidMachine SDK dependency
    implementation 'io.bidmachine:ads:1.3.0'
    //Add BidMachine SDK AdMob Adapter dependency
    implementation 'io.bidmachine:ads-adapters-admob:1.3.0.2'
    //Add AdMob SDK dependency
    implementation 'com.google.android.gms:play-services-ads:17.2.0'
    ...
}
```

## Examples:

#### Load Banner: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L96)
#### Load Interstitial: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L157)
#### Load Rewarded Video: [Sample](example/src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L212)


Parameter list for server configuretion:

| Key             | Definition | Value type |
|:--------------- |:-------------- |:---------- |
| seller_id       | You unique seller id. To get your Seller Id or for more info please visit https://bidmachine.io/ | String |
| mediation_config | Your mediation config | JSONArray in String |
| coppa           | Flag indicating if COPPA regulations can be applied. The Children's Online Privacy Protection Act (COPPA) was established by the U.S. Federal Trade Commission. | String |
| logging_enabled | Enable logs if required | String |
| test_mode       | Enable test mode | String |
| subject_to_gdpr | Flag indicating if GDPR regulations can be applied. The General Data Protection Regulation (GDPR) is a regulation of the European Union. | String |
| has_consent     | User has given consent to the processing of personal data relating to him or her. https://www.eugdpr.org/ | String |
| consent_string  | GDPR consent string (if applicable), indicating the compliance to the IAB standard Consent String Format of the Transparency and Consent Framework technical specifications. | String |
| ad_content_type | Content type for interstitial ad, one of following: "All", "Static", "Video"   | String              |
| userId          | Vendor-specific ID for the user                                                | String              |
| gender          | Gender, one of following: "F", "M", "O"                                        | String              |
| yob             | Year of birth as a 4-digit integer (e.g - 1990)                                | String              |
| keywords        | List of keywords, interests, or intents (separated by comma)                   | String              |
| country         | Country of the user's home base (i.e., not necessarily their current location) | String              |
| city            | City of the user's home base (i.e., not necessarily their current location)    | String              |
| zip             | Zip of the user's home base (i.e., not necessarily their current location)     | String              |
| sturl           | App store URL for an installed app; for IQG 2.1 compliance                     | String              |
| paid            | Determines, if it is a free or paid version of the app                         | String              |
| bcat            | Block list of content categories using IDs (separated by comma)                | String              |
| badv            | Block list of advertisers by their domains (separated by comma)                | String              |
| bapps           | Block list of apps where ads are disallowed (separated by comma)               | String              |
| priceFloors     | List of price floor                                                            | JSONArray in String |

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
        .setMediationConfig(YOURJSONArray.toString)
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
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