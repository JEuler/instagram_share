import 'dart:async';

import 'package:flutter/services.dart';

class InstagramShare {
  static const MethodChannel _channel =
      const MethodChannel('instagramshare');

  /// method to share to Instagram
  /// It uses the ACTION_SEND Intent on Android
  /// [path] is file path
  /// [type]  "image" ,"video"
  static Future<void> share(String path, String type) {
    assert(path != null && path.isNotEmpty);
    final Map<String, dynamic> params = <String, dynamic>{
      'path': path,
      'type': type,
    };
    return _channel.invokeMethod('share', params);
  }
}
