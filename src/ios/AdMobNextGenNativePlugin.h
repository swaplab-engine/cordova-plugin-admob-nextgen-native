#import <Cordova/CDV.h>
#import <GoogleMobileAds/GoogleMobileAds.h>

@interface AdMobNextGenNativePlugin : CDVPlugin <GADNativeAdLoaderDelegate, GADNativeAdDelegate>

@property (nonatomic, strong) GADAdLoader *adLoader;
@property (nonatomic, strong) UIView *adContainer;
@property (nonatomic, strong) NSString *currentCallbackId;

- (void)showWith:(CDVInvokedUrlCommand *)command;
- (void)hide:(CDVInvokedUrlCommand *)command;

@end
