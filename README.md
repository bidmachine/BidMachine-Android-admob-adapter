# BidMachine Android AdMobAdapter

[<img src="https://img.shields.io/badge/SDK%20Version-1.6.3-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.6.3.10-brightgreen">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.admob/1.6.3.10/)
[<img src="https://img.shields.io/badge/AdMob%20Version-19.7.0-blue">](https://developers.google.com/admob/android/quick-start)

* [Useful links](#useful-links)
* [Integration](#integration)
* [Types of integration](#types-of-integration)
* [Work with price](#work-with-price)
* [What's new in last version](#whats-new-in-last-version)

## Useful links
* [BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)

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
    implementation 'io.bidmachine:ads.adapters.admob:1.6.3.10'
    //Add AdMob SDK dependency
    implementation 'com.google.android.gms:play-services-ads:19.7.0'
}
```

## Types of integration
* [Classic AdMob implementation](example_admob)
* [HeaderBidding AdMob implementation](example_admob_fetch)
* [HeaderBidding AdManager implementation](example_ad_manager)

## Work with price
When **BidMachineFetcher.fetch(...)** is called, price rounding occurs. By default, RoundingMode is **RoundingMode.CEILING**, but if you want specific RoundingMode, you can change it with help **BidMachineFetcher.setPriceRounding(...)**. You can try your rounding configuration via call **BidMachineFetcher.roundPrice(...)**. More info about RoundingMode [here](https://developer.android.com/reference/java/math/RoundingMode).

**Attention**:  RoundingMode.UNNECESSARY is not supported.

Price rounding examples:

| Round mode | Result |
| ---------- | ------ |
| BidMachineFetcher.setPriceRounding(0.01) | 0.01 -> 0.01 <br> 0.99 -> 0.99 <br> 1.212323 -> 1.22 <br> 1.34538483 -> 1.35 <br> 1.4 -> 1.40 <br> 1.58538483 -> 1.59 |
| BidMachineFetcher.setPriceRounding(0.1) | 0.01 -> 0.1 <br> 0.99 -> 1.0 <br> 1.212323 -> 1.3 <br> 1.34538483 -> 1.4 <br> 1.4 -> 1.4 <br> 1.58538483 -> 1.6 |
| BidMachineFetcher.setPriceRounding(0.01, RoundingMode.FLOOR) | 0.01 -> 0.01 <br> 0.99 -> 0.99 <br> 1.212323 -> 1.21 <br> 1.34538483 -> 1.34 <br> 1.4 -> 1.40 <br> 1.58538483 -> 1.58 |

## What's new in last version
Please view the [changelog](CHANGELOG.md) for details.