#import "GADTTemplateView.h"
#import <QuartzCore/QuartzCore.h>

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCallToActionFont =
    @"call_to_action_font";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCallToActionFontColor =
    @"call_to_action_font_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCallToActionBackgroundColor =
    @"call_to_action_background_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeySecondaryFont = @"secondary_font";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeySecondaryFontColor =
    @"secondary_font_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeySecondaryBackgroundColor =
    @"secondary_background_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyPrimaryFont = @"primary_font";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyPrimaryFontColor = @"primary_font_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyPrimaryBackgroundColor =
    @"primary_background_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyTertiaryFont = @"tertiary_font";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyTertiaryFontColor =
    @"tertiary_font_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyTertiaryBackgroundColor =
    @"tertiary_background_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyMainBackgroundColor =
    @"main_background_color";

GADTNativeTemplateStyleKey const GADTNativeTemplateStyleKeyCornerRadius = @"corner_radius";

static NSString* const GADTBlue = @"#5C84F0";

@implementation GADTTemplateView {
  NSDictionary<GADTNativeTemplateStyleKey, NSObject*>* _defaultStyles;
}

- (instancetype)initWithFrame:(CGRect)frame {
  if (self = [super initWithFrame:frame]) {
    _rootView = [NSBundle.mainBundle loadNibNamed:NSStringFromClass([self class])
                                            owner:self
                                          options:nil]
                    .firstObject;

    [self addSubview:_rootView];

    [self
        addConstraints:[NSLayoutConstraint
                           constraintsWithVisualFormat:@"H:|[_rootView]|"
                                               options:0
                                               metrics:nil
                                                 views:NSDictionaryOfVariableBindings(_rootView)]];
    [self
        addConstraints:[NSLayoutConstraint
                           constraintsWithVisualFormat:@"V:|[_rootView]|"
                                               options:0
                                               metrics:nil
                                                 views:NSDictionaryOfVariableBindings(_rootView)]];
    [self applyStyles];
  }
  return self;
}

- (NSString *)getTemplateTypeName {
  return @"root";
}

- (id)styleForKey:(GADTNativeTemplateStyleKey)key {
  return _styles[key] ?: nil;
}

- (void)applyStyles {
  self.layer.borderColor = [GADTTemplateView colorFromHexString:@"E0E0E0"].CGColor;
  self.layer.borderWidth = 1.0f;
  [self.mediaView sizeToFit];
  if ([self styleForKey:GADTNativeTemplateStyleKeyCornerRadius]) {
    float roundedCornerRadius =
        ((NSNumber *)[self styleForKey:GADTNativeTemplateStyleKeyCornerRadius]).floatValue;

    self.iconView.layer.cornerRadius = roundedCornerRadius;
    self.iconView.clipsToBounds = YES;
    ((UIButton*)self.callToActionView).layer.cornerRadius = roundedCornerRadius;
    ((UIButton*)self.callToActionView).clipsToBounds = YES;
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyPrimaryFont]) {
    ((UILabel *)self.headlineView).font =
        (UIFont *)[self styleForKey:GADTNativeTemplateStyleKeyPrimaryFont];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeySecondaryFont]) {
    ((UILabel *)self.bodyView).font =
        (UIFont *)[self styleForKey:GADTNativeTemplateStyleKeySecondaryFont];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyTertiaryFont]) {
    ((UILabel *)self.advertiserView).font =
        (UIFont *)[self styleForKey:GADTNativeTemplateStyleKeyTertiaryFont];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyCallToActionFont]) {
    ((UIButton *)self.callToActionView).titleLabel.font =
        (UIFont *)[self styleForKey:GADTNativeTemplateStyleKeyCallToActionFont];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyPrimaryFontColor])
    ((UILabel *)self.headlineView).textColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyPrimaryFontColor];

  if ([self styleForKey:GADTNativeTemplateStyleKeySecondaryFontColor]) {
    ((UILabel *)self.bodyView).textColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeySecondaryFontColor];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyTertiaryFontColor]) {
    ((UILabel *)self.advertiserView).textColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyTertiaryFontColor];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyCallToActionFontColor]) {
    [((UIButton *)self.callToActionView)
        setTitleColor:(UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyCallToActionFontColor]
             forState:UIControlStateNormal];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyPrimaryBackgroundColor]) {
    ((UILabel *)self.headlineView).backgroundColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyPrimaryBackgroundColor];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeySecondaryBackgroundColor]) {
    ((UILabel *)self.bodyView).backgroundColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeySecondaryBackgroundColor];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyTertiaryBackgroundColor]) {
    ((UILabel *)self.advertiserView).backgroundColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyTertiaryBackgroundColor];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyCallToActionBackgroundColor]) {
    ((UIButton *)self.callToActionView).backgroundColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyCallToActionBackgroundColor];
  }

  if ([self styleForKey:GADTNativeTemplateStyleKeyMainBackgroundColor]) {
    self.backgroundColor =
        (UIColor *)[self styleForKey:GADTNativeTemplateStyleKeyMainBackgroundColor];
  }

  [self styleAdBadge];
}

- (void)styleAdBadge {
  UILabel *adBadge = self.adBadge;
  adBadge.layer.borderColor = adBadge.textColor.CGColor;
  adBadge.layer.borderWidth = 1.0;
  adBadge.layer.cornerRadius = 3.0;
  adBadge.layer.masksToBounds = YES;
}

- (void)setStyles:(NSDictionary<GADTNativeTemplateStyleKey, NSObject *> *)styles {
  _styles = [styles copy];
  [self applyStyles];
}

- (void)setNativeAd:(GADNativeAd *)nativeAd {
  ((UILabel *)self.headlineView).text = nativeAd.headline;

  ((UIImageView *)self.iconView).image = nativeAd.icon.image;
  self.iconView.hidden = nativeAd.icon ? NO : YES;

  [((UIButton *)self.callToActionView) setTitle:nativeAd.callToAction
                                       forState:UIControlStateNormal];

  if (nativeAd.advertiser && !nativeAd.store) {

    self.storeView.hidden = YES;
    ((UILabel *)self.advertiserView).text = nativeAd.advertiser;
    self.advertiserView.hidden = NO;
  } else if (nativeAd.store && !nativeAd.advertiser) {

    self.advertiserView.hidden = YES;
    ((UILabel *)self.storeView).text = nativeAd.store;
    self.storeView.hidden = NO;
  } else if (nativeAd.advertiser && nativeAd.store) {

    self.storeView.hidden = YES;
    ((UILabel *)self.advertiserView).text = nativeAd.advertiser;
    self.advertiserView.hidden = NO;
  }

  if (nativeAd.starRating.floatValue > 0) {
    NSMutableString* stars = [[NSMutableString alloc] initWithString:@""];
    int count = 0;
    for (; count < nativeAd.starRating.intValue; count++) {
      NSString* filledStar = [NSString stringWithUTF8String:"\u2605"];
      [stars appendString:filledStar];
    }
    for (; count < 5; count++) {
      NSString* emptyStar = [NSString stringWithUTF8String:"\u2606"];
      [stars appendString:emptyStar];
    }
    ((UILabel *)self.starRatingView).text = stars;
    self.bodyView.hidden = YES;
    self.starRatingView.hidden = NO;
  } else {
    self.starRatingView.hidden = YES;
    ((UILabel *)self.bodyView).text = nativeAd.body;
    self.bodyView.hidden = NO;
  }

  [self.mediaView setMediaContent:nativeAd.mediaContent];
  [super setNativeAd:nativeAd];
}

- (void)addHorizontalConstraintsToSuperviewWidth {

  if (self.superview) {
    UIView* child = self;
    [self.superview
        addConstraints:[NSLayoutConstraint
                           constraintsWithVisualFormat:@"H:|[child]|"
                                               options:0
                                               metrics:nil
                                                 views:NSDictionaryOfVariableBindings(child)]];
  }
}

- (void)addVerticalCenterConstraintToSuperview {
  if (self.superview) {
    UIView* child = self;
    [self.superview addConstraint:[NSLayoutConstraint constraintWithItem:self.superview
                                                               attribute:NSLayoutAttributeCenterY
                                                               relatedBy:NSLayoutRelationEqual
                                                                  toItem:child
                                                               attribute:NSLayoutAttributeCenterY
                                                              multiplier:1
                                                                constant:0]];
  }
}

+ (UIColor*)colorFromHexString:(NSString*)hexString {
  if (hexString == nil) {
    return nil;
  }
  NSRange range = [hexString rangeOfString:@"^#[0-9a-fA-F]{6}$" options:NSRegularExpressionSearch];
  if (range.location == NSNotFound) {
    return nil;
  }
  unsigned rgbValue = 0;
  NSScanner* scanner = [NSScanner scannerWithString:hexString];
  [scanner setScanLocation:1];  
  [scanner scanHexInt:&rgbValue];

  return [UIColor colorWithRed:((rgbValue & 0xff0000) >> 16) / 255.0f
                         green:((rgbValue & 0xff00) >> 8) / 255.0f
                          blue:(rgbValue & 0xff) / 255.0f
                         alpha:1];
}
@end
