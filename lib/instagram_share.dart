import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';
import 'package:image_downloader/image_downloader.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:url_launcher/url_launcher.dart';

class InstagramShare {
  static const MethodChannel _channel = const MethodChannel('instagramshare');

  /// method to share to Instagram
  /// It uses the ACTION_SEND Intent on Android
  /// [path] is file path
  /// [type]  "image" ,"video"
  static Future<void> share(String path, String type) {
    assert(path != null && path.isNotEmpty);
    if (Platform.isAndroid) {
      final Map<String, dynamic> params = <String, dynamic>{
        'path': path,
        'type': type,
      };
      return _channel.invokeMethod('share', params);
    } else {}
  }

  Future<void> _shareMediaIOS(String filePath) async {
    if (await Permission.photos.request().isGranted) {
      final mediaId = await ImageDownloader.downloadImage(filePath);
      if (mediaId == null) {}
      final instagramUrl = 'instagram://library?LocalIdentifier=$mediaId';
      if (await canLaunch(instagramUrl)) {
        final instagramUrl = 'instagram://library?LocalIdentifier=$mediaId';
        await launch(instagramUrl);
      } else {}
    } else {
      Future.delayed(const Duration(milliseconds: 1000), () {
        /// Let's open app settings on
        openAppSettings();
      });
    }
  }
}
