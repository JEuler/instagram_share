import 'package:flutter/material.dart';

import 'package:instagram_share/instagram_share.dart';

void main() => runApp(ShareApp());

class ShareApp extends StatefulWidget {
  @override
  _ShareAppState createState() => _ShareAppState();
}

class _ShareAppState extends State<ShareApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Sharing App'),
        ),
        body: Center(
          child: MaterialButton(child: Text('Share'), onPressed: () {
            InstagramShare.share('/', 'image');
          }),
        ),
      ),
    );
  }
}
