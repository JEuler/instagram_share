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

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * InstagramsharePlugin
 */
public class InstagramSharePlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener {

    /// the authorities for FileProvider
    private static final int CODE_ASK_PERMISSION = 100;
    private static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";

    private String mPath;
    private String mType;
    private Context mContext;
    private Activity mActivity;
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        setupChannel(flutterPluginBinding.getBinaryMessenger(), flutterPluginBinding.getApplicationContext());
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        teardownChannel();
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
        binding.addRequestPermissionsResultListener(this);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        mActivity = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
        binding.addRequestPermissionsResultListener(this);
    }

    @Override
    public void onDetachedFromActivity() {
        mActivity = null;
    }

    // This static function is optional and equivalent to onAttachedToEngine.
    // It supports the old pre-Flutter-1.12 Android projects.
    // The function is kept for backward compatibility
    @SuppressWarnings("deprecation")
    public static void registerWith(io.flutter.plugin.common.PluginRegistry.Registrar registrar) {
        InstagramSharePlugin instance = new InstagramSharePlugin();
        instance.setupChannel(registrar.messenger(), registrar.context());
        instance.mActivity = registrar.activity();
        registrar.addRequestPermissionsResultListener(instance);
    }

    private void setupChannel(BinaryMessenger messenger, Context context) {
        mContext = context;
        channel = new MethodChannel(messenger, "instagramshare");
        channel.setMethodCallHandler(this);
    }

    private void teardownChannel() {
        channel.setMethodCallHandler(null);
        channel = null;
        mContext = null;
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
        return ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_ASK_PERMISSION);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] perms, int[] grantResults) {
        if (requestCode == CODE_ASK_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareToInstagram(mPath, mType);
        }
        return false;
    }

    private void openInstagramInPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("market://details?id=" + INSTAGRAM_PACKAGE_NAME));
        mContext.startActivity(intent);
    }

    private boolean instagramInstalled() {
        try {
            if (mActivity != null) {
                mActivity
                        .getPackageManager()
                        .getApplicationInfo(INSTAGRAM_PACKAGE_NAME, 0);
                return true;
            } else {
                Toast.makeText(mContext, "Please install Instagram!", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return false;
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
}
