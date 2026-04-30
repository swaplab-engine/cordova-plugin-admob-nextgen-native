#import "GADTMediumTemplateView.h"

@implementation GADTMediumTemplateView

- (nonnull instancetype)initWithFrame:(CGRect)frame {
  if (self = [super initWithFrame:frame]) {
    self.translatesAutoresizingMaskIntoConstraints = NO;
  }
  return self;
}

- (nonnull NSString *)getTemplateTypeName {
  return @"medium_template";
}

@end
