#import "GADTSmallTemplateView.h"

@implementation GADTSmallTemplateView

- (nonnull instancetype)initWithFrame:(CGRect)frame {
  if (self = [super initWithFrame:frame]) {
    self.translatesAutoresizingMaskIntoConstraints = NO;
  }
  return self;
}

- (nonnull NSString *)getTemplateTypeName {
  return @"small_template";
}

@end
