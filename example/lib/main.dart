import 'package:flutter/material.dart';

import 'package:instagram_share/instagram_share.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: MaterialButton(child: Text('Share'), onPressed: () {
          InstagramShare.share('/', 'image');
        }),
      ),
    );
  }
}
