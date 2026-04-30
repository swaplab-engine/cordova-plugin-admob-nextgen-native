#import "AdMobNextGenNativePlugin.h"
#import "GADTMediumTemplateView.h"
#import "GADTSmallTemplateView.h"

@implementation AdMobNextGenNativePlugin {
    CGFloat _adX;
    CGFloat _adY;
    CGFloat _adWidth;
    NSString *_templateName;

    NSInteger _retryInterval;
    NSTimeInterval _lastShowTime;
}

- (void)showWith:(CDVInvokedUrlCommand *)command {
    NSDictionary *options = [command.arguments objectAtIndex:0];
    if (!options) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Options are null"] callbackId:command.callbackId];
        return;
    }

    self.currentCallbackId = command.callbackId;

    NSString *adUnitId = options[@"adUnitId"];
    if (!adUnitId || [adUnitId isKindOfClass:[NSNull class]] || adUnitId.length == 0) {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"adUnitId is required"] callbackId:command.callbackId];
        return;
    }

    _retryInterval = options[@"retryInterval"] ? [options[@"retryInterval"] integerValue] : 5000;

    NSTimeInterval currentTime = [[NSDate date] timeIntervalSince1970] * 1000.0;
    if (_lastShowTime > 0 && (currentTime - _lastShowTime) < _retryInterval) {
        NSString *errorMsg = [NSString stringWithFormat:@"Anti-spam protection: Request ignored. Please wait %ld ms.", (long)_retryInterval];
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMsg] callbackId:command.callbackId];
        return;
    }

    _lastShowTime = currentTime;

    _templateName = options[@"template"] ?: @"small";
    _adX = [options[@"x"] floatValue];
    _adY = [options[@"y"] floatValue];
    _adWidth = [options[@"width"] floatValue];

    if (self.adContainer) {
        [self.adContainer removeFromSuperview];
        self.adContainer = nil;
    }

    self.adLoader = [[GADAdLoader alloc] initWithAdUnitID:adUnitId
                                       rootViewController:self.viewController
                                                  adTypes:@[ GADAdLoaderAdTypeNative ]
                                                  options:nil];
    self.adLoader.delegate = self;

    GADRequest *request = [GADRequest request];
    [self.adLoader loadRequest:request];
}

- (void)hide:(CDVInvokedUrlCommand *)command {
    if (self.adContainer) {
        [self.adContainer removeFromSuperview];
        self.adContainer = nil;
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_OK] callbackId:command.callbackId];
    } else {
        [self.commandDelegate sendPluginResult:[CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No ad to hide"] callbackId:command.callbackId];
    }
}

#pragma mark - GADNativeAdLoaderDelegate

- (void)adLoader:(GADAdLoader *)adLoader didReceiveNativeAd:(GADNativeAd *)nativeAd {
    nativeAd.delegate = self;

    nativeAd.paidEventHandler = ^(GADAdValue *_Nonnull adValue) {
            NSDecimalNumber *value = adValue.value;
            NSString *currencyCode = adValue.currencyCode;
            GADAdValuePrecision precision = adValue.precision;

            NSDictionary *data = @{
                @"value": value,
                @"currencyCode": currencyCode ?: @"USD",
                @"precisionType": @(precision)
            };
            [self fireEvent:@"on.nextgen.native.revenue" withData:data];
        };

    GADTTemplateView *templateView;
    BOOL isMedium = [_templateName.lowercaseString isEqualToString:@"medium"];

    if (isMedium) {
        templateView = [[NSBundle mainBundle] loadNibNamed:@"GADTMediumTemplateView" owner:nil options:nil].firstObject;
    } else {
        templateView = [[NSBundle mainBundle] loadNibNamed:@"GADTSmallTemplateView" owner:nil options:nil].firstObject;
    }

    templateView.nativeAd = nativeAd;

    CGFloat exactHeight = isMedium ? 350.0 : 120.0;

    self.adContainer = [[UIView alloc] initWithFrame:CGRectMake(_adX, _adY, _adWidth, exactHeight)];

    templateView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.adContainer addSubview:templateView];

    [NSLayoutConstraint activateConstraints:@[
        [templateView.leadingAnchor constraintEqualToAnchor:self.adContainer.leadingAnchor],
        [templateView.trailingAnchor constraintEqualToAnchor:self.adContainer.trailingAnchor],
        [templateView.topAnchor constraintEqualToAnchor:self.adContainer.topAnchor],
        [templateView.bottomAnchor constraintEqualToAnchor:self.adContainer.bottomAnchor]
    ]];

    UIScrollView *scrollView = nil;
    if ([self.webView respondsToSelector:@selector(scrollView)]) {
        scrollView = [self.webView valueForKey:@"scrollView"];
    }

    if (scrollView) {
        [scrollView addSubview:self.adContainer];
    } else {
        [self.webView addSubview:self.adContainer];
    }

    NSDictionary *result = @{
        @"width": @(_adWidth),
        @"height": @(exactHeight)
    };

    [self fireEvent:@"on.nextgen.native.loaded" withData:result];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:result];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
}

- (void)adLoader:(GADAdLoader *)adLoader didFailToReceiveAdWithError:(NSError *)error {
    NSDictionary *errData = @{@"message": error.localizedDescription};
    [self fireEvent:@"on.nextgen.native.failed.load" withData:errData];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.currentCallbackId];
}

#pragma mark - GADNativeAdDelegate (Event Tracking)

- (void)nativeAdDidRecordImpression:(GADNativeAd *)nativeAd {
    [self fireEvent:@"on.nextgen.native.impression" withData:nil];
}

- (void)nativeAdDidRecordClick:(GADNativeAd *)nativeAd {
    [self fireEvent:@"on.nextgen.native.clicked" withData:nil];
}

- (void)nativeAdWillPresentScreen:(GADNativeAd *)nativeAd {
    [self fireEvent:@"on.nextgen.native.shown" withData:nil];
}

- (void)nativeAdWillDismissScreen:(GADNativeAd *)nativeAd {
    [self fireEvent:@"on.nextgen.native.dismissed" withData:nil];
}

#pragma mark - JS Event Emitter

- (void)fireEvent:(NSString *)eventName withData:(NSDictionary *)data {
    NSString *dataString = @"null";
    if (data) {
        NSError *error;
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:data options:0 error:&error];
        if (!error) {
            dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        }
    }

    NSString *js = [NSString stringWithFormat:@"cordova.fireDocumentEvent('%@', %@);", eventName, dataString];
    [self.commandDelegate evalJs:js];
}

@end
