// --- UI Logger Utility ---
    const logConsole = document.getElementById('log-console');
    window.logToScreen = function(message, type = 'info') {
        const time = new Date().toLocaleTimeString();
        const logLine = document.createElement('div');
        logLine.className = `log-${type}`;
        logLine.textContent = `[${time}] ${message}`;
        logConsole.appendChild(logLine);
        logConsole.scrollTop = logConsole.scrollHeight;
    };

    // --- UI Elements ---
    const btnShow = document.getElementById('btn-show');
    const btnHide = document.getElementById('btn-hide');
    const adContainer = document.getElementById('native-ad-container');

    // --- Ad IDs & State ---
    let isDeviceready = false;
    let isPlatformIOS = false;
    let NativeAd_ID;

    // Initialize Native Ad Action Listeners
    function setupAdButtons() {
        btnShow.disabled = false;

        btnShow.addEventListener('click', function() {
            window.logToScreen("Requesting to show Native Ad...", "info");
            btnShow.disabled = true;

            if (window.admobNextGenNative) {
                admobNextGenNative.showWith(
                    adContainer,
                    {
                        adUnitId: NativeAd_ID,
                        template: 'medium', // Use the medium or small option
                        retryInterval: 5000 // Anti-spam interval
                    },
                    function(result) {
                        window.logToScreen("Native Ad Rendered Success!", "success");

                        // === Responsive HTML Container ===
                        if (result && result.height) {

                            // Empty the placeholder text to avoid clashing with CSS padding.
                            adContainer.innerHTML = ''; 
                            // Apply box-sizing to avoid padding effect
                            adContainer.style.boxSizing = 'border-box';
                            // Adjust the container height with precision
                            // Force HTML container to fit native height of Android/IOS Ads
                            adContainer.style.height = result.height + 'px';
                            window.logToScreen("Ad Dimension: " + result.width + "x" + result.height, "info");
                        }

                        btnShow.disabled = false;
                        btnHide.disabled = false;
                    },
                    function(error) {
                        window.logToScreen("Native Ad Show Failed: " + error, "error");
                        btnShow.disabled = false;
                    }
                );
            }
        });

        btnHide.addEventListener('click', function() {
            window.logToScreen("Hiding Native Ad...", "info");

            if (window.admobNextGenNative) {
                admobNextGenNative.hide(
                    function() {
                        window.logToScreen("Native Ad Hidden.", "success");
                        btnHide.disabled = true;

                        // Return the container height to its original size.
                        adContainer.style.height = '250px';
                    },
                    function(error) {
                        window.logToScreen("Native Ad Hide Failed: " + error, "error");
                    }
                );
            }
        });

    }

    // --- Cordova Initialization Sequence ---
    document.addEventListener("deviceready", function () {
        isDeviceready = true;
        window.logToScreen("Device Ready! Starting AdMob sequence...", "info");

        if (window.cordova && window.cordova.platformId === 'ios') {
            NativeAd_ID = 'ca-app-pub-3940256099942544/3986624511';
            isPlatformIOS = true;
        } else {
            NativeAd_ID = 'ca-app-pub-3940256099942544/2247696110';
        }

        // STEP 1: UMP (GDPR)
        function startPrivacyFlow() {
            window.logToScreen("Requesting UMP Consent...", "info");

            admobNextGen.requestConsentInfo({
                debug: true,
                reset: false,
                tagForUnderAgeOfConsent: false
            }, function () {
                window.logToScreen("Consent Info Ready.", "success");
                checkATTFlow();
            }, function (err) {
                window.logToScreen("UMP Error: " + err, "warn");
                checkATTFlow();
            });
        }

        // STEP 2: ATT (iOS IDFA)
        function checkATTFlow() {
            if (isPlatformIOS && admobNextGen.requestTrackingAuthorization) {
                window.logToScreen("iOS detected: Requesting ATT...", "info");
                admobNextGen.requestTrackingAuthorization(
                    function (status) {
                        window.logToScreen("ATT Status: " + status, "success");
                        startSdk();
                    },
                    function (err) {
                        window.logToScreen("ATT Request Failed", "warn");
                        startSdk();
                    }
                );
            } else {
                startSdk();
            }
        }

        // STEP 3: INITIALIZE SDK Core 
        function startSdk() {
            window.logToScreen("Initializing AdMob SDK...", "info");
            
            admobNextGen.initialize({
                maxAdContentRating: 'G',  // 'G' | 'PG' | 'T' | 'MA' | ""
                tagForChildDirectedTreatment: false, // true | false | null
                tagForUnderAgeOfConsent: false, // true | false | null
                isNativeValidatorDisabled: false // Default: true
            }, function () {
                window.logToScreen("✅ SDK READY TO SERVE ADS", "success");

                // Activate Buttons now that SDK is ready
                setupAdButtons();
                checkTCData();
            }, function (err) {
                window.logToScreen("SDK Init Failed: " + err, "error");
            });
        }

        function checkTCData() {
            admobNextGen.getTCData(function (data) {
                window.logToScreen("Ads Personalized: " + data.isPersonalizedAllowed, "info");
            });
        }

        // Start Stream
        startPrivacyFlow();


/*
on.nextgen.native.loaded
on.nextgen.native.failed.load
on.nextgen.native.impression
on.nextgen.native.clicked
on.nextgen.native.shown
on.nextgen.native.dismissed
on.nextgen.native.revenue
on.nextgen.native.failed.shown // only android
*/

document.addEventListener('on.nextgen.native.loaded', (e) => {
    var data = e.data || e;
    console.log("Native Loaded: " + data.width + "x" + data.height);
});


document.addEventListener('on.nextgen.native.revenue', function(e) {
    if (e && e.value) {
        // Note: On Android, this is valueMicros (divided by 1 million).
        // On iOS, NSNumber is often already a decimal or double value.
        // You may need to adjust the calculation depending on the exact output.
        console.log("💰 EVENT: Ad Paid! Value: " + e.value + " " + e.currencyCode, "success");
    }
});

document.addEventListener('on.nextgen.native.impression', function(e) {
    window.logToScreen("🚀 EVENT: Ad Impression Recorded!", "success");
});

document.addEventListener('on.nextgen.native.clicked', function(e) {
    window.logToScreen("🖱️ EVENT: Ad Clicked!", "info");
});

document.addEventListener('on.nextgen.native.shown', function(e) {
    window.logToScreen("📱 EVENT: Full Screen Content Showed", "info");
});

document.addEventListener('on.nextgen.native.dismissed', function(e) {
    window.logToScreen("🔙 EVENT: Full Screen Content Dismissed", "info");
});


}, false);