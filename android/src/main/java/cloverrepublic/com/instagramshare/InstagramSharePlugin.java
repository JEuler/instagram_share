package cloverrepublic.com.instagramshare;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * InstagramsharePlugin
 */
public class InstagramSharePlugin implements MethodCallHandler, PluginRegistry.RequestPermissionsResultListener {

    /// the authorities for FileProvider
    private static final int CODE_ASK_PERMISSION = 100;
    private static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";

    private String mPath;
    private String mType;
    private PluginRegistry.Registrar mRegistrar;

    public InstagramSharePlugin(PluginRegistry.Registrar registrar) {
        mRegistrar = registrar;
    }

    public static void registerWith(PluginRegistry.Registrar registrar) {
        MethodChannel channel = new MethodChannel(registrar.messenger(), "instagramshare");
        final InstagramSharePlugin instance = new InstagramSharePlugin(registrar);
        registrar.addRequestPermissionsResultListener(instance);
        channel.setMethodCallHandler(instance);
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
        return ContextCompat.checkSelfPermission(mRegistrar.activity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(mRegistrar.activity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_ASK_PERMISSION);
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
        mRegistrar.context().startActivity(intent);
    }

    private boolean instagramInstalled() {
        try {
            if (mRegistrar.activity() != null) {
                mRegistrar.activity()
                        .getPackageManager()
                        .getApplicationInfo(INSTAGRAM_PACKAGE_NAME, 0);
                return true;
            } else {
                Toast.makeText(mRegistrar.context(), "Please install Instagram!", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
        Uri uri = ShareUtils.getUriForFile(mRegistrar.context(), f);

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
                mRegistrar.context().startActivity(shareIntent);
            } catch (ActivityNotFoundException ex) {
                openInstagramInPlayStore();
            }
        } else {
            openInstagramInPlayStore();
        }
    }
}
