# Cordova Plugin AdMob Next-Gen Native

A professional, high-performance Cordova plugin for rendering seamless, scrollable AdMob Native Ads using the latest Google Mobile Ads (GMA) Next-Gen SDK.

## See It In Action

<table align="center">
  <tr>
    <td align="center"><strong>iOS Seamless Scroll</strong></td>
    <td align="center"><strong>Android Seamless Scroll</strong></td>
  </tr>
  <tr>
    <td width="50%" align="center">
      <video src="https://github.com/user-attachments/assets/317a0418-37bb-4c3a-969c-75db341e7b1d" width="100%" controls autoplay loop muted playsinline></video>
    </td>
    <td width="50%" align="center">
      <video src="https://github.com/user-attachments/assets/8d23576e-b055-4462-a622-516c7371ce2b" width="100%" controls autoplay loop muted playsinline></video>
    </td>
  </tr>
</table>

## Why Native Ads?
Native ads are widely known to generate **up to 2x higher revenue** compared to standard banner ads. Because they blend naturally into your app's content, they provide a non-intrusive user experience, leading to significantly higher engagement and click-through rates.

## Features & Advantages
*   **True Seamless Scrolling:** Unlike older plugins that struggle with absolute positioning, this plugin renders native ads that scroll smoothly and flawlessly alongside your web/HTML content.
*   **Official Google Templates:** We utilize the official AdMob Native Templates provided by Google, ensuring your ads meet the highest professional and production standards.
    *   [Android Template Source](https://github.com/googleads/googleads-mobile-android-native-templates)
    *   [iOS Template Source](https://github.com/googleads/googleads-mobile-ios-native-templates)
*   **Built-in Anti-Spam:** Includes configurable throttle protection (`retryInterval`) to prevent accidental layout loops from triggering ad-spam, protecting your AdMob account from policy violations.
*   **Impression-Level Ad Revenue (ILRD):** Fully supports capturing ad revenue data in real-time.

---

## ⚠️ Important Dependencies

This plugin **cannot be used standalone**. It is an extension designed to work exclusively alongside the core engine: `cordova-plugin-admob-nextgen`.

You **must** install both plugins for the native ads to function correctly.

### 🚫 Incompatibility Warning
Do **not** use this plugin alongside older or legacy AdMob plugins such as:
*   `community-cordova-plugin-admob`
*   `admob-plus-cordova`
*   `@capacitor-community/admob`

**Why?** Legacy plugins rely on the older `com.google.android.gms:play-services-ads` architecture. This plugin strictly requires the new [GMA Next-Gen SDK](https://developers.google.com/admob/android/next-gen/quick-start) provided by our core dependency. Mixing them will result in fatal build conflicts.

---

## Installation

### Via Cordova CLI
Install the core plugin first (requires your AdMob App IDs), then install this native extension:

```bash
# 1. Install the Core Engine
cordova plugin add cordova-plugin-admob-nextgen --save \
  --variable APP_ID_ANDROID="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy" \
  --variable APP_ID_IOS="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"

# 2. Install the Native Extension
cordova plugin add cordova-plugin-admob-nextgen-native --save
```
*(Optional iOS Variable)*: To enable the iOS Native Ad Validator during development, you can pass `--variable IOS_NATIVE_VALIDATOR_ENABLED=true` during the installation of the native extension.

### Via config.xml
Add the following lines to your `config.xml` to ensure the plugins are restored automatically upon build:

```xml
<plugin name="cordova-plugin-admob-nextgen-native" />

<plugin name="cordova-plugin-admob-nextgen">
    <!-- Replace with your actual App IDs -->
    <variable name="APP_ID_ANDROID" value="ca-app-pub-3940256099942544~3347511713" />
    <variable name="APP_ID_IOS" value="ca-app-pub-3940256099942544~1458002511" />
</plugin>
```

### For Capacitor Users
Install via npm:

```bash
npm install cordova-plugin-admob-nextgen
npm install cordova-plugin-admob-nextgen-native
npx cap sync
```


## Simple overview

👉 **[View Native Full API & Examples Here](https://github.com/swaplab-engine/cordova-plugin-admob-nextgen-native/tree/main/cordova-example/www)**

<details>
👉 <summary>Click View</summary>
  
```
HTML 

<div id="native-ad-container">
    Native Ad Will Appear Here<br>
    (Placeholder)
</div>


// app.js
const adContainer = document.getElementById('native-ad-container');

 // --- Cordova Initialization Sequence ---
    document.addEventListener("deviceready", function () {

        // Core: cordova-plugin-admob-nextgen
        // INITIALIZE SDK
        function startSdk() {
            console.log("Initializing AdMob SDK...", "info");
            admobNextGen.initialize({
                maxAdContentRating: 'G',  // 'G' | 'PG' | 'T' | 'MA' | ""
                tagForChildDirectedTreatment: false, // true | false | null
                tagForUnderAgeOfConsent: false, // true | false | null
                isNativeValidatorDisabled: false // Default: true
            }, function () {

                showNativeAd();
                console.log("✅ SDK READY TO SERVE ADS", "success");
            }, function (err) {
                console.error("SDK Init Failed: " + err, "error");
            });
        }

        // Native Extension: cordova-plugin-admob-nextgen-native 
        function showNativeAd() {
         if (window.admobNextGenNative) {
                admobNextGenNative.showWith(
                    adContainer,
                    {
                        adUnitId: 'ca-app-pub-3940256099942544/2247696110', // IOS test 'ca-app-pub-3940256099942544/3986624511'
                        template: 'medium', // Use the medium or small option
                        retryInterval: 5000 // Anti-spam interval
                    },
                    function(result) {
                        // === Responsive HTML Container ===
                        if (result && result.height) {
                            // Force HTML container to fit native height of Android/IOS Ads
                            adContainer.style.height = result.height + 'px';
                        }
                    },
                    function(error) {
                        console.error("Native Ad Show Failed: " + error, "error");
                    }
                );
            }
        }

        // method hide: admobNextGenNative.hide()
        
       startSdk(); // Start Stream

/* All Event
on.nextgen.native.loaded
on.nextgen.native.failed.load
on.nextgen.native.impression
on.nextgen.native.clicked
on.nextgen.native.shown
on.nextgen.native.dismissed
on.nextgen.native.revenue
on.nextgen.native.failed.shown // only android
*/

}, false);

```

</details>


Looking for a quick start? Check out our ready-to-use **Capacitor Boilerplate Template** integrating the Next-Gen AdMob engine:
👉 [cordova-plugin-admob-nextgen-template](https://github.com/swaplab-engine/cordova-plugin-admob-nextgen-template)

---

## Usage Examples & API Reference

For complete examples of how to implement methods, parameters, and event listeners in your JavaScript/HTML code, please refer directly to our example project:

👉 **[View Native Full API & Examples Here](https://github.com/swaplab-engine/cordova-plugin-admob-nextgen-native/tree/main/cordova-example/www)**

---

## ⚖️ Revenue Policy

**No Ad-Sharing. No Hidden Fees.**

* **0% Revenue Share:** We do not inject our own Ad Unit IDs into your traffic.
* **100% Control:** Every impression and click goes directly to your AdMob account.
* **Transparent Source:** The code is open source. You can verify that no third-party SDKs or hidden backdoors exist.

---

## ❤️ Support the Project

This plugin is developed and maintained in my free time. If it saved you hours of work, consider supporting the development!

<a href="https://www.buymeacoffee.com/emi.indo" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" >
</a>

Your support helps me keep the dependencies updated and the cleaner script running smoothly.


<p align="center">
  Built with ❤️ by <b>Swaplab Engine</b> (EMI-INDO).
</p>