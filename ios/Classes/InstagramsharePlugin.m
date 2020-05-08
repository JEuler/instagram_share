#import "InstagramsharePlugin.h"
#import <instagram_share/instagram_share-Swift.h>

@implementation InstagramsharePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftInstagramsharePlugin registerWithRegistrar:registrar];
}
@end
