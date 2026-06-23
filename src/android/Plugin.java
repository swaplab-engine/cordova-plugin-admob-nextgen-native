package com.emi.cordova.admob.nextgen.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.view.WindowInsets;
import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdValue;

import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd.NativeAdType;
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Plugin extends CordovaPlugin {
    private static final String TAG = "AdMobNextGenNative";

    private FrameLayout adContainer;
    private NativeAd currentNativeAd;

    private ViewTreeObserver.OnScrollChangedListener scrollListener;
    private long lastShowTime = 0;
    private boolean isCordova15 = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("showWith".equals(action)) {
            JSONObject options = args.optJSONObject(0);
            if (options != null) {
                showNativeAd(options, callbackContext);
                return true;
            }
        } else if ("hide".equals(action)) {
            hideNativeAd(callbackContext);
            return true;
        }
        return false;
    }

    private void showNativeAd(JSONObject options, CallbackContext callbackContext) {
        final Activity activity = cordova.getActivity();

        String adUnitId = options.optString("adUnitId", "");
        if (adUnitId == null || adUnitId.trim().isEmpty()) {
            callbackContext.error("adUnitId is required");
            return;
        }

        if (options.has("isCordova15")) this.isCordova15 = options.optBoolean("isCordova15");
        int retryInterval = options.optInt("retryInterval", 5000);

        long currentTime = System.currentTimeMillis();
        if (lastShowTime > 0 && (currentTime - lastShowTime) < retryInterval) {
            String errorMsg = "Request too fast. Please wait " + retryInterval + " ms to prevent invalid traffic.";
            callbackContext.error(errorMsg);
            return;
        }

        lastShowTime = currentTime;

        String templateName = options.optString("template", "small");
        int x = options.optInt("x", 0);
        int y = options.optInt("y", 0);
        int width = options.optInt("width", ViewGroup.LayoutParams.MATCH_PARENT);
        int heightHtml = options.optInt("height", ViewGroup.LayoutParams.WRAP_CONTENT);

        activity.runOnUiThread(() -> {
            NativeAdRequest adRequest = new NativeAdRequest.Builder(adUnitId, List.of(NativeAdType.NATIVE)).build();

            NativeAdLoaderCallback callback = new NativeAdLoaderCallback() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    activity.runOnUiThread(() -> {
                        nativeAd.setAdEventCallback(new NativeAdEventCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() { fireEvent("on.nextgen.native.shown", null); }

                            @Override
                            public void onAdDismissedFullScreenContent() { fireEvent("on.nextgen.native.dismissed", null); }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError error) {
                                try {
                                    JSONObject data = new JSONObject();
                                    data.put("message", error.getMessage());
                                    fireEvent("on.nextgen.native.failed.shown", data);
                                } catch (JSONException ignored) {}
                            }

                            @Override
                            public void onAdImpression() { fireEvent("on.nextgen.native.impression", null); }

                            @Override
                            public void onAdClicked() { fireEvent("on.nextgen.native.clicked", null); }

                            @Override
                            public void onAdPaid(@NonNull AdValue adValue) {
                                try {
                                    JSONObject data = new JSONObject();
                                    data.put("value", adValue.getValueMicros());
                                    data.put("currencyCode", adValue.getCurrencyCode());
                                    data.put("precisionType", adValue.getPrecisionType().name());
                                    fireEvent("on.nextgen.native.revenue", data);
                                } catch (JSONException ignored) {}
                            }
                        });

                        FrameLayout pendingContainer = buildAdContainer(activity, nativeAd, templateName, width);

                        if (adContainer != null && adContainer.getParent() != null) {
                            ((ViewGroup) adContainer.getParent()).removeView(adContainer);
                        }

                        if (currentNativeAd != null) {
                            currentNativeAd.destroy();
                        }

                        currentNativeAd = nativeAd;
                        adContainer = pendingContainer;

                        ViewGroup webViewContainer = (ViewGroup) webView.getView().getParent();

                        boolean isMedium = "medium".equalsIgnoreCase(templateName);
                        int adHeight = isMedium ? dpToPx(350) : ViewGroup.LayoutParams.WRAP_CONTENT;

                        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                                width > 0 ? dpToPx(width) : ViewGroup.LayoutParams.MATCH_PARENT,
                                adHeight
                        );

                        if (isCordova15) {

                            int[] webViewLocation = new int[2];
                            webView.getView().getLocationOnScreen(webViewLocation);
                            int[] parentLocation = new int[2];
                            webViewContainer.getLocationOnScreen(parentLocation);

                            int offsetY = (webViewLocation[1] - parentLocation[1]) + webView.getView().getPaddingTop();
                            int offsetX = (webViewLocation[0] - parentLocation[0]) + webView.getView().getPaddingLeft();

                            containerParams.leftMargin = dpToPx(x) + offsetX;
                            containerParams.topMargin = dpToPx(y) + offsetY;

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                                adContainer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                                    @NonNull
                                    @Override
                                    public WindowInsets onApplyWindowInsets(@NonNull View v, @NonNull WindowInsets insets) {
                                        return insets; 
                                    }
                                });
                            }
                        } else {

                            containerParams.leftMargin = dpToPx(x);
                            containerParams.topMargin = dpToPx(y);
                        }

                        webViewContainer.addView(adContainer, containerParams);

                        setupGlobalLayoutListener(callbackContext);

                        syncScrollObserver();
                    });
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    activity.runOnUiThread(() -> {
                        try {
                            JSONObject data = new JSONObject();
                            data.put("message", adError.getMessage());
                            fireEvent("on.nextgen.native.failed.load", data);
                        } catch (JSONException ignored) {}
                        callbackContext.error(adError.getMessage());
                    });
                }
            };
            NativeAdLoader.load(adRequest, callback);
        });
    }

    private FrameLayout buildAdContainer(Activity activity, NativeAd nativeAd, String templateName, int width) {
        FrameLayout newContainer = new FrameLayout(activity);

        int layoutId = "medium".equalsIgnoreCase(templateName) ?
                getResourceId("gnt_medium_template_view", "layout") :
                getResourceId("gnt_small_template_view", "layout");

        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(layoutId, null);

        adView.setHeadlineView(adView.findViewById(id("primary")));
        adView.setBodyView(adView.findViewById(id("secondary")));
        adView.setCallToActionView(adView.findViewById(id("cta")));
        adView.setIconView(adView.findViewById(id("icon")));
        adView.setStarRatingView(adView.findViewById(id("rating_bar")));
        MediaView mediaView = adView.findViewById(id("media_view"));

        if (adView.getHeadlineView() != null) ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        if (adView.getBodyView() != null) {
            adView.getBodyView().setVisibility(nativeAd.getBody() == null ? View.INVISIBLE : View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (adView.getCallToActionView() != null) {
            adView.getCallToActionView().setVisibility(nativeAd.getCallToAction() == null ? View.INVISIBLE : View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null || nativeAd.getIcon().getDrawable() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }
        if (adView.getStarRatingView() != null) {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        adView.registerNativeAd(nativeAd, mediaView);

        boolean isMedium = "medium".equalsIgnoreCase(templateName);
        int adHeight = isMedium ? dpToPx(350) : ViewGroup.LayoutParams.WRAP_CONTENT;

        FrameLayout.LayoutParams adViewParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                adHeight
        );
        newContainer.addView(adView, adViewParams);

        return newContainer;
    }

    private void setupGlobalLayoutListener(CallbackContext callbackContext) {
        if (adContainer == null) return;

        adContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int exactHeightPx = adContainer.getHeight();
                int exactWidthPx = adContainer.getWidth();

                try {
                    JSONObject result = new JSONObject();
                    result.put("width", pxToDp(exactWidthPx));
                    result.put("height", pxToDp(exactHeightPx));

                    fireEvent("on.nextgen.native.loaded", result);
                    callbackContext.success(result);
                } catch (JSONException e) {
                    callbackContext.success();
                }
            }
        });
    }

    private void syncScrollObserver() {
        if (scrollListener == null) {
            scrollListener = () -> {
                if (adContainer != null) {
                    adContainer.setTranslationY(-webView.getView().getScrollY());
                    adContainer.setTranslationX(-webView.getView().getScrollX());
                }
            };
            webView.getView().getViewTreeObserver().addOnScrollChangedListener(scrollListener);
        }

        if (adContainer != null) {
            adContainer.setTranslationY(-webView.getView().getScrollY());
            adContainer.setTranslationX(-webView.getView().getScrollX());
        }
    }

    private void hideNativeAd(CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(() -> {
            if (scrollListener != null) {
                webView.getView().getViewTreeObserver().removeOnScrollChangedListener(scrollListener);
                scrollListener = null;
            }
            if (adContainer != null) {
                adContainer.setVisibility(View.GONE);
                if (adContainer.getParent() != null) {
                    ((ViewGroup) adContainer.getParent()).removeView(adContainer);
                }
                adContainer = null;
            }

            if (currentNativeAd != null) {
                currentNativeAd.destroy();
                currentNativeAd = null;
            }

            callbackContext.success();
        });
    }

    @SuppressLint("DiscouragedApi")
    private int getResourceId(String name, String defType) {
        return cordova.getActivity().getResources().getIdentifier(name, defType, cordova.getActivity().getPackageName());
    }

    private int id(String name) {
        return getResourceId(name, "id");
    }

    private int dpToPx(int dp) {
        float density = cordova.getActivity().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private int pxToDp(int px) {
        float density = cordova.getActivity().getResources().getDisplayMetrics().density;
        return Math.round((float) px / density);
    }

    @Override
    public void onDestroy() {
        if (cordova != null && cordova.getActivity() != null) {
            cordova.getActivity().runOnUiThread(() -> {

                if (scrollListener != null && webView != null && webView.getView() != null) {
                    webView.getView().getViewTreeObserver().removeOnScrollChangedListener(scrollListener);
                    scrollListener = null;
                }

                if (adContainer != null) {
                    if (adContainer.getParent() != null) {
                        ((ViewGroup) adContainer.getParent()).removeView(adContainer);
                    }
                    adContainer.removeAllViews();
                    adContainer = null;
                }

                if (currentNativeAd != null) {
                    currentNativeAd.destroy();
                    currentNativeAd = null;
                }
            });
        }

        super.onDestroy(); 
    }

    private void fireEvent(String eventName, JSONObject data) {
        if (cordova == null) return;
        cordova.getActivity().runOnUiThread(() -> {
            StringBuilder js = new StringBuilder();
            js.append("javascript:cordova.fireDocumentEvent('");
            js.append(eventName);
            js.append("'");
            if (data != null) {
                js.append(", ");
                js.append(data.toString());
            }
            js.append(");");
            if (webView != null && webView.getView() != null) {
                webView.loadUrl(js.toString());
            }
        });
    }
}
