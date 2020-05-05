#import "InstagramsharePlugin.h"
#if __has_include(<instagramshare/instagramshare-Swift.h>)
#import <instagramshare/instagramshare-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "instagramshare-Swift.h"
#endif

@implementation InstagramsharePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftInstagramsharePlugin registerWithRegistrar:registrar];
}
@end
