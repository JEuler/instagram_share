import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:instagramshare/instagramshare.dart';

void main() {
  const MethodChannel channel = MethodChannel('instagramshare');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await InstagramShare.platformVersion, '42');
  });
}
