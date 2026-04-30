#import <GoogleMobileAds/GoogleMobileAds.h>

typedef NSString* GADTNativeTemplateStyleKey NS_STRING_ENUM;

#pragma mark - Call To Action

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCallToActionFont;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCallToActionFontColor;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCallToActionBackgroundColor;

#pragma mark - Primary Text

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyPrimaryFont;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyPrimaryFontColor;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyPrimaryBackgroundColor;

#pragma mark - Secondary Text

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeySecondaryFont;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeySecondaryFontColor;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeySecondaryBackgroundColor;

#pragma mark - Tertiary Text

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyTertiaryFont;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyTertiaryFontColor;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyTertiaryBackgroundColor;

#pragma mark - Additional Style Options

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyMainBackgroundColor;

extern GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCornerRadius;

@interface GADTTemplateView : GADNativeAdView
@property(nonatomic, copy) NSDictionary<GADTNativeTemplateStyleKey, NSObject *> *styles;
@property(weak) IBOutlet UILabel *adBadge;
@property(weak) UIView *rootView;

- (void)addHorizontalConstraintsToSuperviewWidth;

- (void)addVerticalCenterConstraintToSuperview;

+ (UIColor *)colorFromHexString:(NSString *)hexString;

- (NSString *)getTemplateTypeName;
@end
