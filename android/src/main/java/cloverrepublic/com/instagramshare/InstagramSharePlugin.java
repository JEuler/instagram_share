package cloverrepublic.com.instagramshare;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * InstagramsharePlugin
 */
public class InstagramSharePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// the authorities for FileProvider
  private static final int CODE_ASK_PERMISSION = 100;
  private static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";

  private String mPath;
  private String mType;
  private MethodChannel mChannel;
  private Context mContext;

  private Activity mActivity;

  public static void registerWith(Registrar registrar) {
    MethodChannel channel = new MethodChannel(registrar.messenger(), "instagramshare");
    final InstagramSharePlugin instance = new InstagramSharePlugin();
    channel.setMethodCallHandler(instance);
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    mContext = flutterPluginBinding.getApplicationContext();
    mChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "instagramshare");
    mChannel.setMethodCallHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    mChannel.setMethodCallHandler(null);
    mChannel = null;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("share")) {
      mPath = call.argument("path");
      mType = call.argument("type");
      shareToInstagram(mPath, mType);
      result.success(null);
    } else {
      result.notImplemented();
    }
  }

  private boolean checkPermission() {
    return ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED;
  }

  private void requestPermission() {
    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_ASK_PERMISSION);
  }

  private boolean instagramInstalled() {
    try {
      PackageManager pm = mContext.getPackageManager();
      pm.getPackageInfo(INSTAGRAM_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  private void openInstagramInPlayStore() {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.setData(Uri.parse("market://details?id=" + INSTAGRAM_PACKAGE_NAME));
    mContext.startActivity(intent);
  }

  private void shareToInstagram(String path, String type) {
    String mediaType = "";
    if ("image".equals(type)) {
      mediaType = "image/jpeg";
    } else {
      mediaType = "video/*";
    }

    if (ShareUtils.shouldRequestPermission(path)) {
      if (!checkPermission()) {
        requestPermission();
        return;
      }
    }

    File f = new File(path);
    Uri uri = ShareUtils.getUriForFile(mContext, f);

    if (instagramInstalled()) {
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      StrictMode.setVmPolicy(builder.build());
      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.setPackage(INSTAGRAM_PACKAGE_NAME);
      shareIntent.setType(mediaType);
      shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
      shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      try {
        mContext.startActivity(shareIntent);
      } catch (ActivityNotFoundException ex) {
        openInstagramInPlayStore();
      }
    } else {
      openInstagramInPlayStore();
    }
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    mActivity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }
}
